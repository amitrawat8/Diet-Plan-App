package com.example.diet.view.ui.fragmentLoader;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.UiThread;
import com.example.diet.R;
import com.example.diet.utils.CommonUtil;
import com.example.diet.utils.CustomAsyncInflater;
import com.github.ybq.android.spinkit.SpinKitView;
import com.github.ybq.android.spinkit.style.CubeGrid;
import com.github.ybq.android.spinkit.style.Wave;

public class LoadingViewFragment extends CommonFragment implements CustomAsyncInflater.OnInflateFinishedListener {

    public static final int STYLE_WAVE = 2;
    public static final int STYLE_CUBE_GRID = 4;
    public static final int PROGRESS_TYPE_TEXT_AND_CIRCLE = 4;
    private static boolean customDesignFlag = false;
    public TextView error_msg_tv, progress_text_tv, retry_tv;
    private LinearLayout loader_parent_ll, child_parent_ll, error_ll;
    private ProgressBar progress_circle_pb;
    private SpinKitView prog_skv;
    private Handler handler;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @UiThread
    public View getLoaderView(Context context, View childView) {
        View view = View.inflate(context, R.layout.frag_loader, null);
        performFindViewById(view);
        addChildToParent(view, childView);
        return view;
    }
    private void performFindViewById(View view) {
        child_parent_ll = view.findViewById(R.id.child_parent_ll);
        loader_parent_ll = view.findViewById(R.id.loader_parent_ll);
        error_ll = view.findViewById(R.id.error_ll);
        error_msg_tv = view.findViewById(R.id.error_msg_tv);
        progress_circle_pb = view.findViewById(R.id.progress_circle_pb);
        progress_text_tv = view.findViewById(R.id.progress_text_tv);
        prog_skv = view.findViewById(R.id.loader_prog_skv);
        retry_tv = view.findViewById(R.id.retry_tv);

    }

    @UiThread
    public void addChildToParent(View rootView, View childView) {
        if (childView != null) {
            ViewGroup viewGroup = (ViewGroup) childView.getParent();
            if (viewGroup != null) {
                viewGroup.removeView(childView);
            }
            if (childView != null) {
                child_parent_ll.addView(childView);
            }
        }
    }

    /**
     * Show Progress
     */
    @UiThread
    public void showProgress() {
        if (customDesignFlag) {
            return;
        }
        error_ll.setVisibility(View.GONE);
        child_parent_ll.setVisibility(View.GONE);
        loader_parent_ll.setVisibility(View.VISIBLE);
        progress_text_tv.setVisibility(View.GONE);
        progress_circle_pb.setVisibility(View.GONE);
    }


    /**
     * Show Progress
     */
    @UiThread
    public void showProgress(int type, String message, int design) {
        switch (type) {
            case PROGRESS_TYPE_TEXT_AND_CIRCLE:
                customDesignFlag = true;
                progress_text_tv.setVisibility(View.VISIBLE);
                progress_circle_pb.setVisibility(View.VISIBLE);
                progress_text_tv.setCompoundDrawables(null, null, null, null);
                break;
            default:
                progress_text_tv.setVisibility(View.GONE);
                progress_circle_pb.setVisibility(View.VISIBLE);
                customDesignFlag = false;
                break;
        }
        switch (design) {
            case STYLE_WAVE:
                Wave wave = new Wave();
                prog_skv.setIndeterminateDrawable(wave);
                prog_skv.setVisibility(View.VISIBLE);
                progress_circle_pb.setVisibility(View.GONE);
                break;

            case STYLE_CUBE_GRID:
                CubeGrid cubeGrid = new CubeGrid();
                prog_skv.setIndeterminateDrawable(cubeGrid);
                prog_skv.setVisibility(View.VISIBLE);
                progress_circle_pb.setVisibility(View.GONE);
                break;

            default:
                progress_circle_pb.setVisibility(View.VISIBLE);
                prog_skv.setVisibility(View.GONE);
                break;
        }
        progress_text_tv.setText(message);
        error_ll.setVisibility(View.GONE);
        child_parent_ll.setVisibility(View.GONE);
        loader_parent_ll.setVisibility(View.VISIBLE);
    }

    /**
     * Show Error
     */
    @UiThread
    public boolean showError() {
        if (error_ll.getVisibility() == View.VISIBLE) {
            return false;
        }
        loader_parent_ll.setVisibility(View.GONE);
        child_parent_ll.setVisibility(View.GONE);
        error_ll.setVisibility(View.VISIBLE);
        return true;
    }

    /**
     * Show Error with message
     */
    @UiThread
    public void showError(String message) {
        if (showError()) {
            if (!CommonUtil.isNullOrEmpty(message)) {
                error_msg_tv.setText(message);
            }
        }
    }

    /**
     * Show Content
     */
    @UiThread
    public void showContent() {
        loader_parent_ll.setVisibility(View.GONE);
        child_parent_ll.setVisibility(View.VISIBLE);
        error_ll.setVisibility(View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void onInflateFinished(View view) {
        if (CommonUtil.isUIThreadRunning(this, getActivity())) {
            if (view != null) {
                showProgress();//Let Child Handle it
            } else {
                showError();
            }
        }
    }
}
