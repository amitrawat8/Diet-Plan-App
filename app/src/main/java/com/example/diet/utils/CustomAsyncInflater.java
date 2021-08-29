package com.example.diet.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.core.util.Pools;
import java.util.concurrent.ArrayBlockingQueue;

public class CustomAsyncInflater {

    LayoutInflater mInflater;
    Handler mHandler;
    InflateThread mInflateThread;
    LinearLayout parent;
    private Handler.Callback mHandlerCallback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            InflateRequest request = (InflateRequest) msg.obj;
            if (request.view == null) {
                request.view = mInflater.inflate(
                        request.resid, request.parent, false);
            }
            //add inflated view to child safely.
            View childView = request.view;
            if (childView != null) {
                ViewGroup viewGroup = (ViewGroup) childView.getParent();
                if (viewGroup != null) {
                    viewGroup.removeView(childView);
                }
                parent.addView(childView);
            }
            request.callback.onInflateFinished(
                    parent);
            mInflateThread.releaseRequest(request);
            return true;
        }
    };

    public CustomAsyncInflater(@NonNull Context context, @NonNull LinearLayout parent) {
        mInflater = new BasicInflater(context);
        mHandler = new Handler(mHandlerCallback);
        mInflateThread = InflateThread.getInstance();
        this.parent = parent;
    }

    @UiThread
    public void inflate(@LayoutRes int resid, @Nullable ViewGroup parent,
                        @NonNull OnInflateFinishedListener callback) {
        if (callback == null) {
            throw new NullPointerException("callback argument may not be null!");
        }
        InflateRequest request = mInflateThread.obtainRequest();
        request.inflater = this;
        request.resid = resid;
        request.parent = parent;
        request.callback = callback;
        mInflateThread.enqueue(request);
    }

    public interface OnInflateFinishedListener {
        void onInflateFinished(View view);
    }

    private static class InflateRequest {
        com.example.diet.utils.CustomAsyncInflater inflater;
        ViewGroup parent;
        int resid;
        View view;
        OnInflateFinishedListener callback;

        InflateRequest() {
        }
    }

    private static class BasicInflater extends LayoutInflater {
        private static final String[] sClassPrefixList = {
                "android.widget.",
                "android.webkit.",
                "android.app."
        };

        BasicInflater(Context context) {
            super(context);
        }

        @Override
        public LayoutInflater cloneInContext(Context newContext) {
            return new BasicInflater(newContext);
        }

        @Override
        protected View onCreateView(String name, AttributeSet attrs) throws ClassNotFoundException {
            for (String prefix : sClassPrefixList) {
                try {
                    View view = createView(name, prefix, attrs);
                    if (view != null) {
                        return view;
                    }
                } catch (ClassNotFoundException e) {
                    // In this case we want to let the base class take a crack
                    // at it.
                }
            }

            return super.onCreateView(name, attrs);
        }
    }

    private static class InflateThread extends Thread {
        private static final InflateThread sInstance;
        private static final String TAG = InflateThread.class.getSimpleName();

        static {
            sInstance = new InflateThread();
            sInstance.start();
        }

        private ArrayBlockingQueue<InflateRequest> mQueue = new ArrayBlockingQueue<>(10);
        private Pools.SynchronizedPool<InflateRequest> mRequestPool = new Pools.SynchronizedPool<>(10);

        public static InflateThread getInstance() {
            return sInstance;
        }

        @Override
        public void run() {
            while (true) {
                InflateRequest request;
                try {
                    request = mQueue.take();
                } catch (InterruptedException ex) {
                    // Odd, just continue
                    Log.w(TAG, ex);
                    continue;
                }

                try {
                    //--
                    if (Looper.myLooper() == null) {
                        Looper.prepare();
                    }
                    //--
                    request.view = request.inflater.mInflater.inflate(
                            request.resid, request.parent, false);
                } catch (RuntimeException ex) {
                    // Probably a Looper failure, retry on the UI thread
                    Log.w(TAG, "Failed to inflate resource in the background! Retrying on the UI"
                            + " thread", ex);
                }
                Message.obtain(request.inflater.mHandler, 0, request)
                        .sendToTarget();
            }
        }

        public InflateRequest obtainRequest() {
            InflateRequest obj = mRequestPool.acquire();
            if (obj == null) {
                obj = new InflateRequest();
            }
            return obj;
        }

        public void releaseRequest(InflateRequest obj) {
            obj.callback = null;
            obj.inflater = null;
            obj.parent = null;
            obj.resid = 0;
            obj.view = null;
            mRequestPool.release(obj);
        }

        public void enqueue(InflateRequest request) {
            try {
                mQueue.put(request);
            } catch (InterruptedException e) {
                throw new RuntimeException(
                        "Failed to enqueue async inflate request", e);
            }
        }
    }

}
