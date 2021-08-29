package com.example.diet.view.ui.fragmentLoader;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import androidx.fragment.app.Fragment;
import com.example.diet.utils.AlertHandler;


public class CommonFragment extends Fragment {

    public Resources mResources;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("SAMPLE_KEY", "SAMPLE_VALUE");
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mResources = getResources();
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        try {
            super.startActivityForResult(intent, requestCode);
        } catch (SecurityException e) {
            e.printStackTrace();
            AlertHandler.getInstance().showLongToast(getContext(), "Must giver permission: for Storage and Camera.");
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            AlertHandler.getInstance().showLongToast(getContext(), "Error: " + e.getMessage());
        }
    }
}
