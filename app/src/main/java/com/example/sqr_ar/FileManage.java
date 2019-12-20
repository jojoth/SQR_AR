package com.example.sqr_ar;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class FileManage {

    public static  final  int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 123;

    public boolean checkPermissions(Context contxt, final Activity activity) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(contxt, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(contxt);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("Write Storage permission is necessary to Download Images and Videos!!!");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                } else {
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }
    public String  checkFolder(String floder_keep) {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath()+floder_keep;
        File dir =  new File(path);
        Log.i("LOG","path="+path);
        boolean isDirCreated  = dir.exists();
        if(!isDirCreated){
            isDirCreated = dir.mkdir();
            if(isDirCreated) Log.d("LOG","create done !!");

        }
        if(isDirCreated){
            Log.d("LOG","Already Created");
        }
        return path;


    }

    public ArrayList<String> readFile(String path, String fileN){

        //Log.d("LOG","path in func ="+path+fileN);


        ArrayList<String>  fname1 = new ArrayList<>();

        File  file = new File(path,fileN);


        //read text from file
        StringBuilder txt = new StringBuilder();


        try{
            BufferedReader br = new BufferedReader(new FileReader(file));


            String line ;
            String[] spilt1 = new String[2];

            int i=0;
            while((line= br.readLine())!=null){
                txt.append(line);

                spilt1 = line.split("[|]");


                fname1.add(spilt1[0]);
                txt.append('\n');
                i++;
            }
            br.close();


           // Log.d("LOG","all txt="+txt.toString());





        }catch (Exception ex){
            Log.e("LOG","Error::"+ex.toString());

        }

        return  fname1;

    }
}
