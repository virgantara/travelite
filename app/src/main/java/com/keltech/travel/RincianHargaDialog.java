package com.keltech.travel;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.tgwarrior.travelmockup.R;

/**
 * Created by ASUS on 8/28/2016.
 */
public class RincianHargaDialog extends DialogFragment {


    public RincianHargaDialog() {

        // Empty constructor is required for DialogFragment

        // Make sure not to add arguments to the constructor

        // Use `newInstance` instead as shown below

    }

    public static RincianHargaDialog newInstance(String title) {
        RincianHargaDialog frag = new RincianHargaDialog();
        Bundle args = new Bundle();

        args.putString("title", title);

        frag.setArguments(args);

        return frag;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        return inflater.inflate(R.layout.rincian_harga, container);

    }


    @Override

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

    }
}
