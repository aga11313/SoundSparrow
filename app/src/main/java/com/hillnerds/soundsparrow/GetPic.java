//package com.hillnerds.soundsparrow;
//
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.graphics.Bitmap;
//import android.os.Build;
//import android.os.Bundle;
//import android.provider.MediaStore;
//import android.util.Log;
//import android.view.SurfaceHolder;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.content.res.Configuration;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Matrix;
//import android.hardware.Camera;
//import android.os.AsyncTask;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Environment;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.SurfaceHolder;
//import android.view.SurfaceView;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.Toast;
//
//import java.io.File;
//
//
///**
// * Created by mayan on 28/01/2017.
// */
//
//public class GetPic {
//
//    private Bitmap bmp;
//    private SurfaceView sv;
//    private SurfaceHolder sHolder;
//    private Camera mCamera;
//    private Camera.Parameters parameters;
//
//
//    int windowWidth, windowHeight, screenCenter;
//
//    LinearLayout parentView;
//    Context m_context;
//    View m_view;
//
//    //Temp image array
//    int[] myImageList = new int[]{R.drawable.cats, R.drawable.puppy, R.drawable.bb8,
//            R.drawable.ewan, R.drawable.daisy};
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.mainlayout);
//
//        m_context = TakePicture.this;
//
//        parentView = (LinearLayout) findViewById(R.id.linLayout);
//        windowWidth = getWindowManager().getDefaultDisplay().getWidth();
//        windowHeight = getWindowManager().getDefaultDisplay().getHeight();
//        screenCenter = windowWidth / 2;
//
//        LayoutInflater inflate = (LayoutInflater) m_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        m_view = inflate.inflate(R.layout.fragment_main, null);
//
//        Bundle extras = getIntent().getExtras();
//        i = extras.getInt("ImageListIterator");
//
//        displayImage();
//
//        // check if this device has a camera
//        if (checkCameraHardware(getApplicationContext())) {
//
//            sv = (SurfaceView) findViewById(R.id.surfaceView);
//            sHolder = sv.getHolder();
//            sHolder.addCallback(this);
//
//            // tells Android that this surface will have its data constantly replaced
//            if (Build.VERSION.SDK_INT < 11)
//                sHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
//
//        } else {
//            // display in long period of time
//            Toast.makeText(getApplicationContext(),
//                    "Your device doesn't have a Camera!", Toast.LENGTH_LONG)
//                    .show();
//        }
//
//    }
//
//    @Override
//    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
//        Log.e("TAG", "Surface View Changed");
//
//        parameters = mCamera.getParameters();
//        mCamera.setParameters(parameters);
//        mCamera.startPreview();
//
//
//        //sets what code should be executed after the picture is taken
//        Camera.PictureCallback mCall = new Camera.PictureCallback() {
//            @Override
//            public void onPictureTaken(byte[] data, Camera camera) {
//                //  Toast.makeText(getApplicationContext(), "Took Picture", Toast.LENGTH_LONG).show();
//                Log.i("TAG", "Image Captured");
//
//                i++;
//
//                bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
//
//                //Code to fix orientation of image captured (not sure if Samsung only)
//                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
//                    // Notice that width and height are reversed
//                    Bitmap scaled = Bitmap.createScaledBitmap(bmp, windowHeight, windowWidth, true);
//                    int w = scaled.getWidth();
//                    int h = scaled.getHeight();
//                    // Setting post rotate to 90
//                    Matrix mtx = new Matrix();
//                    mtx.postRotate(270);
//                    // Rotating Bitmap
//                    bmp = Bitmap.createBitmap(scaled, 0, 0, w, h, mtx, true);
//                }
//
//                saveImage(bmp);
//
//                if (mCamera != null) {
//                    mCamera.stopPreview();
//                    mCamera.release();
//                }
//
//
//                if (bmp != null) {
//                    bmp.recycle();
//                    bmp = null;
//                    System.gc();
//                }
//
//                TakePicture.this.finish();
//            }
//        };
//
//        mCamera.takePicture(null, null, mCall);
//    }
//
//    @Override
//    public void surfaceCreated(SurfaceHolder holder) {
//        Log.e("TAG", "Surface View Created");
//
//        mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
//        //mCamera = Camera.open();
//
//        try {
//            mCamera.setPreviewDisplay(holder);
//
//        } catch (IOException exception) {
//            mCamera.release();
//            mCamera = null;
//        }
//    }
//
//    @Override
//    public void surfaceDestroyed(SurfaceHolder holder) {
//        Log.e("TAG", "Surface View Destroyed");
//
//        mCamera = null;
//    }
//
//    @Override
//    protected void onDestroy() {
////        Intent intent = new Intent("custom-event-name");
////        // You can also include some extra data.
////        intent.putExtra("message", "This is my message!");
////        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
//
//        super.onDestroy();
//    }
//
//    private boolean checkCameraHardware(Context context) {
//        if (context.getPackageManager().hasSystemFeature(
//                PackageManager.FEATURE_CAMERA)) {
//            // this device has a camera
//            return true;
//        } else {
//            // no camera on this device
//            return false;
//        }
//    }
//
//    private void saveImage(Bitmap finalBitmap) {
//        String root = Environment.getExternalStorageDirectory().getPath();
//
//        File myDir = new File(root + "/" + storageContainer);
//        myDir.mkdirs();
//        File file = new File(myDir, imageName);
//        if (file.exists()) file.delete();
//        try {
//            FileOutputStream out = new FileOutputStream(file);
//            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
//            out.flush();
//            out.close();
//
//            String imagePath = root + "/" + storageContainer + "/" + imageName;
//
//            GetEmotion getEmotion = new GetEmotion();
//            getEmotion.execute(imagePath);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}