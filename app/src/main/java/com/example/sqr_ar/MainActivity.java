package com.example.sqr_ar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.ar.core.AugmentedImage;
import com.google.ar.core.AugmentedImageDatabase;
import com.google.ar.core.Frame;
import com.google.ar.core.Session;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Scene;

import com.google.ar.sceneform.rendering.ExternalTexture;
import com.google.ar.sceneform.rendering.ModelRenderable;


import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.google.ar.sceneform.Scene;

public class MainActivity extends AppCompatActivity {

    private CustomArFragment arFragment;
    private Scene scene;


    private ImageButton but_3d,clearbut;
    private TextView status_txt;
    private String plan_name="screen19.sfb";// ตัว Model Plan ที่เรียกมาใช้งาน

    private Set<String> cardset;
    //float inc_an= 5f;

    private String mode ="vdo";
    private  boolean clear = false; //-- ดูว่ากดปุ่มยางลบเพื่อclear Scene หรือป่าว
    private int[] count ;//---เก็บจำนวนในการเรียก active Model โดนจะเก็บตาม index ของชุดบัตรคำใน DB เลย
    private  boolean Set_txt= true;


    ArrayList<String> cardname = new ArrayList<>();//-- เก็บบัตรคำทั้งหมดที่มีอยู่ใน DB

    String Ftxt= "occupationDB.imgdb-imglist.txt";//-เก็บชื่อรูปทั้งหมดที่อยู่ใน database
    String Fdb ="occupationDB.imgdb";//----เก็บ database รูป โดย index จะเรีกกตามชื่อที่อยู่ใน txtไฟล์ด้านบน


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        but_3d = (ImageButton)findViewById(R.id.but_3d);
        clearbut = (ImageButton) findViewById(R.id.eraser_but);
        status_txt= (TextView)findViewById(R.id.txt_status);



        but_3d.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mode.contains("vdo"))mode="3d";
                else mode="vdo";
            }
        });
        clearbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clear = true;
            }
        });


        arFragment = (CustomArFragment)
                getSupportFragmentManager().findFragmentById(R.id.arFragment);

        scene = arFragment.getArSceneView().getScene();
        //----- Set ค่าเบื้องต้นที่เอาไว้ใช้ในการแสดงผล Model AR
        DisplayAR da1 = new DisplayAR();
        da1.setScene(scene);
        da1.setArFragment(arFragment);

        /*
                ทำการอ่านไฟล์ txtที่มาควบคู่กับ DB เพื่อดูว่าในDB  มันเก็บรูปบัตรคำอะไรบ้างindex ที่เท่าไร  เพราะในตัว DB มันไม่อนุญาติให้เข้าไป query ใน DB แบบทั่วไปที่ใช้
                มันเลยมี txt แยกมาควบคู่เพื่อในสามารถอ่านข้อมูลเบื้องต้นแทน query ใน DB
         */


        FileManage fi = new FileManage();
        String path1 = getFilesDir().getAbsolutePath();


        /*----  set CardName(บัตรคำ) เพื่อเอาไปใช้ค้นหาว่ารูปที่ส่องเจอยู่ในชุดบัตรคำหรือไม่

         */

        cardname = fi.readFile(path1,Ftxt);


        cardset = new HashSet<String>(cardname);

        count = new int[cardname.size()];

        resetCount();

        scene.addOnUpdateListener(this::onUpdate); //-- ต้องอยู่หลัง resetCount เพราะresetCount เหมือนเป็นการกำหนดค่าเริ่มต้นให้  count


    }

    public  void resetCount(){

        for(int i=0; i<cardname.size();i++){
            count[i]=0;
        }
        Set_txt =true;

    }

    public AugmentedImageDatabase SetAugmentImgDB(Session session){

        String path= getFilesDir().getAbsolutePath()+"/"+Fdb;//ตอนนี้ไปเก็บ db ที่ /data/user/0/com.example.ar_sqr/files/occupationDB.imgdb

        Log.i("LOG","path="+path);

        File fi = new File(path);




        AugmentedImageDatabase aid= new AugmentedImageDatabase(session);

        try{
            InputStream is =  new FileInputStream(fi);

            aid = AugmentedImageDatabase.deserialize(session,is);
            Log.i("LOG","DBnum="+aid.getNumImages());


        }catch (Exception ex){

        }



        return  aid;



    }


    private  void onUpdate(FrameTime frameTime) {


        Frame frame = arFragment.getArSceneView().getArFrame();

        Collection<AugmentedImage> augmentedImages =
                frame.getUpdatedTrackables(AugmentedImage.class);


        for (AugmentedImage image : augmentedImages) {



            //  Log.d("LOG","before track="+image.getName()+"/num="+image.getIndex());


            if(image.getTrackingState()==TrackingState.TRACKING){//--- เมื่อเจอตำแหน่งของบัตรคำที่ส่องมันจะขึ้นสถานะ Tracking ซึ่งใช้เวลา ประมาณ 3-4วิ ในการที่จะ tracking เจอตำแหน่ง
                if(cardset.contains(image.getName())){//ตรงนี้มันจะเช็คว่ารูปที่เจอมันอยู่ในชุดบัตรคำที่อยู่ใน db ที่เราเรียกใช้หรือเปล่า

                    Log.e("LOG","+++++Tracking Found SET cardset ="+image.getName());
                    if(count[image.getIndex()]<3){

                        DisplayAR Pmodel =new DisplayAR(); //เป็น Class ที่ใช้ในการแสดงผลทั้งในส่วน VDO และ 3D
                        Pmodel.setCardname(cardname);//---ส่งชุดบัตรคำไปที่ class เพื่อนำไปใช้งานต่อไผ
                        Pmodel.SetVideo(image.getName(),this,plan_name);//-- ส่งชื่อรูปที่มันdetectเจอเพื่อใช้สำหรับเรียกไฟล์  model หรือ video

                        Pmodel.playVideo(image.createAnchor(image.getCenterPose()),image.getExtentX(),image.getExtentZ(),image.getName());

                        status_txt.setText("พบตำแหน่งของรูป");
                    }else{
                        status_txt.setText("ตรวจหาบัตรคำ");
                    }

                    count[image.getIndex()]++;

                }
                Set_txt= true;

            }

/*
            if(image.getTrackingState()== TrackingState.STOPPED){
                Log.e("LOG","State STOP");
            }

 */

            if(image.getTrackingState()==TrackingState.PAUSED){

                if(Set_txt){//ไม่ให้มันทำการ set ค่า Textview หลายรอบ
                    status_txt.setText("กำลังตรวจหาตำแหน่งภาพ");
                    Set_txt =false;
                }

                //---ตอนนี้อยู่ในสถานะ PAUSE ทำงานถูกแต่พออยู่ใน Tracking ทำงานเหมือนมันติด cach ซึ่งจะใช้เวลาซักพักประมาณ 1-2วิ ถึงจะdetect เจอรูปใหม่ แปลว่าเราเก็บค่า current ใน State PAUSEลองหาวิธีในการทำให้Tracking อยู่ในสถานะ PAUSE สิว่าทำได้หรือป่าว
                //https://developers.google.com/ar/reference/java/arcore/reference/com/google/ar/core/TrackingState  ตรงนี้บอกว่า session current PAUSE

            }










            if(clear){

                DisplayAR Pmodel1= new DisplayAR();
                Pmodel1.Clear_Scene();//---- ลบทุก Model ออกจาก scence
                //---reset count ที่set การเรียกModel ทั้งหมด
                resetCount();

                Log.e("LOG","Clear Screen");
                clear= false;

             /*
                Intent i  =new Intent(getBaseContext(),MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);

              */


            }

        }



    }





}
