package launcher.hotelrobot.com.testsdk.activities;

import android.content.Intent;
import android.provider.MediaStore;
import android.hardware.Camera;
import android.util.Base64;

public class RecognizeFace {

    public void CaptureImage(){
        Camera mCamera = Camera.open();
        mCamera.startPreview();// I don't know why I added that,
        // but without it doesn't work... :D

        mCamera.takePicture(null, null, mPicture);
    }

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            System.out.println("***************");
            System.out.println(Base64.encodeToString(data, Base64.DEFAULT));
            System.out.println("***************");
        }
    };
}