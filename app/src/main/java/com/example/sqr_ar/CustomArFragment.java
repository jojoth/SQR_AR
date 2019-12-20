package com.example.sqr_ar;


import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

import com.google.ar.core.AugmentedImageDatabase;
import com.google.ar.core.Config;
import com.google.ar.core.Session;
import com.google.ar.sceneform.ux.ArFragment;


public class CustomArFragment extends ArFragment {
    AugmentedImageDatabase aid;

    String Ftxt= "occupationDB.imgdb-imglist.txt";
    String Fdb ="occupationDB.imgdb";




    @Override
    protected Config getSessionConfiguration(Session session) {

        // Config config = new Config(session);
        Config config =super.getSessionConfiguration(session);
        config.setUpdateMode(Config.UpdateMode.LATEST_CAMERA_IMAGE);

        config.setFocusMode(Config.FocusMode.AUTO);


        FileManage fi = new FileManage();




        AugmentedImageDatabase aid = ((MainActivity)getActivity()).SetAugmentImgDB(session);




        config.setAugmentedImageDatabase(aid);

        this.getArSceneView().setupSession(session);

        return config;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        FrameLayout frameLayout = (FrameLayout) super.onCreateView(inflater, container, savedInstanceState);

        getPlaneDiscoveryController().hide();
        getPlaneDiscoveryController().setInstructionView(null);

        return frameLayout;
    }

}