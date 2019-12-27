package com.example.sqr_ar;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.ArrayMap;
import android.util.Log;
import android.widget.Button;

import com.google.ar.core.Anchor;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.SkeletonNode;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.ExternalTexture;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import java.util.ArrayList;
import java.util.List;

public class DisplayAR {
    static ArrayList<String> cardname = new ArrayList<>();
    private ExternalTexture texture;
    private MediaPlayer mediaPlayer;
    private static CustomArFragment arFragment;
    private static Scene scene;
    private static ArrayMap<String,MediaPlayer> media_all = new ArrayMap<>();

    private ModelRenderable renderable;
    private  static TransformableNode pre_plan_node;

    private  static ArrayMap<String,Integer> vdo_map = new ArrayMap<String, Integer>();

    public void setArFragment(CustomArFragment arF){
        arFragment= arF;

        //----ส่วนนี้ทำแค่ set ค่าเบื้องต้น เป็น mock เฉยๆ แต่สามารถใช้ arrayMap ในการmap ชื่อ vdo กับชื่อ card ได้พอดีในตัวต้นแบบพี่เก็บไฟล์ไว้ที่ res>raw เลยต้องเรียกผ่าน R.raw.[video]
        vdo_map.put("musician.mp4",R.raw.musicial);
        vdo_map.put("singer.mp4",R.raw.singer);

         Log.d("LOG","musicID="+R.raw.musicial);
         Log.d("LOG","singerID="+R.raw.singer);

    }
    public void setScene(Scene sc){
        this.scene= sc;

    }

    public void setCardname(ArrayList<String> cardname) {
        this.cardname = cardname;
    }


    public void SetVideo(String cardname, Context contxt, String pname){


        String realname = cardname.split("\\.")[0];
        String MP4 = realname+".mp4";



        Log.d("LOG","vdo_map Size="+vdo_map.size());

        Log.i("LOG","+++++in class Found="+cardname+"/mp4="+MP4);
         Log.e("LOG","---- file mp4="+vdo_map.get(MP4));

        texture = new ExternalTexture();

        //https://stackoverflow.com/questions/7976141/get-uri-of-mp3-file-stored-in-res-raw-folder-in-androidการ Uri เรียกไฟล์ ใน android

        mediaPlayer = MediaPlayer.create(contxt,vdo_map.get(MP4));
        mediaPlayer.setSurface(texture.getSurface());
        mediaPlayer.setLooping(true);

        Log.i("LOG","Plan_name="+pname);

        ModelRenderable
                .builder()
                .setSource(contxt, Uri.parse(pname))
                .build()
                .thenAccept(modelRenderable -> {
                    modelRenderable.getMaterial().setExternalTexture("videoTexture",
                            texture);
                    //---- มันคือ choma key ซึ่งเป็นการrender video บน plan 3D
                    modelRenderable.getMaterial().setFloat4("keyColor",
                            new Color(0.01843f, 1f, 0.098f));


                    renderable = modelRenderable;
                });



    }

    public  void playVideo(Anchor anchor, float extentX, float extentZ, String fname){

        mediaPlayer.start();

        media_all.put(fname,mediaPlayer);
        if( pre_plan_node!=null){
            scene.onRemoveChild(pre_plan_node); // remove render before from scene
            pre_plan_node.setParent(null);
            pre_plan_node= null;

        }


        AnchorNode anchorNode = new AnchorNode(anchor);



        Log.i("LOG","======================"+fname+"=====================");





        texture.getSurfaceTexture().setOnFrameAvailableListener(surfaceTexture -> {
            //--render video on texture of plan
            anchorNode.setRenderable(renderable);
            //--remove texture from plan
            texture.getSurfaceTexture().setOnFrameAvailableListener(null);
        });



        anchorNode.setWorldScale(new Vector3(extentX,extentZ,1));






        float posX= anchorNode.getWorldPosition().x;
        float posY= anchorNode.getWorldPosition().y;
        float posZ= anchorNode.getWorldPosition().z-0.1f;// ให้มันขยับไปหน่อยไม่ทับบัตรคำ






        try {

            TransformableNode plan_node = new TransformableNode(arFragment.getTransformationSystem());


            plan_node.setWorldPosition(new Vector3(posX,posY,posZ));


            Log.i("LOG", " node Localscale=" + plan_node.getLocalScale().toString());



            plan_node.setParent(anchorNode);
            plan_node.setRenderable(renderable);
            plan_node.select();
            pre_plan_node = plan_node;




            scene.addChild(plan_node);


        }catch (Exception ex){
            Log.e("LOG","Error gesture control::"+ex.getMessage());
        }

    }

    private  void checkPlayVideo(String fname){
        MediaPlayer mediaPlayer1;

        mediaPlayer1= media_all.get(fname);

        if(!mediaPlayer1.isPlaying()&&mediaPlayer1!=null) mediaPlayer1.start();

    }

    public void display3D(Anchor anchor, String cardname, ArFragment arFragment,Context contxt){

        String realname = cardname.split("\\.")[0]; //--- อันนี้คือชื่อcardname ชื่อเดียวกับ Model
        String model_3d = realname+".sfb";

        ModelRenderable
                .builder()
                .setSource(contxt, Uri.parse(model_3d))///---- URI--- ให้ใส่ชื่อไฟล์ 3d model .sfb
                .build()
                .thenAccept(modelRenderable -> {
                    AnchorNode anchorNode = new AnchorNode(anchor);
                    SkeletonNode skeletonNode = new SkeletonNode();
                    skeletonNode.setParent(anchorNode);
                    skeletonNode.setRenderable(modelRenderable);
                    arFragment.getArSceneView().getScene().addChild(anchorNode);


                });

    }


    public void Clear_Scene(){

        List<Node> nodeList = new ArrayList<>(arFragment.getArSceneView().getScene().getChildren());


        for(Node chnode: nodeList){


            if(chnode instanceof TransformableNode){


                scene.onRemoveChild(chnode);

                chnode.setParent(null);



            }

        }

    }
}
