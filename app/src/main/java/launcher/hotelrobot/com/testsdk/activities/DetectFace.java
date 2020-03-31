package launcher.hotelrobot.com.testsdk.activities;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.util.Log;

import com.interjoy.skface.FaceStruct;
import com.robot.performlib.action.SpeakAction;
import com.robot.performlib.action.WakeupAction;
import com.robot.performlib.callback.PerformFaceDetectCallBack;
import com.robot.performlib.performs.CognizePerform;
import com.robot.performlib.performs.RecognitionPerform;

import launcher.hotelrobot.com.testsdk.MainActivity;
import launcher.hotelrobot.com.testsdk.MyApp;

public class DetectFace {

    private Context context;
    final MainActivity mainActivity = MyApp.getMainActivity();
    private SurfaceHolder mHolder;

    public void startFaceRecognition(SurfaceHolder holder){
        mHolder = holder;
        CognizePerform.create(cognizePerformCallBack).setSurfaceHolder(holder).start(context);
    }


    public PerformFaceDetectCallBack cognizePerformCallBack = new PerformFaceDetectCallBack() {

        //识别到人并且人在摄像头中间
        @Override
        public void FindFacesHandler(final FaceStruct faceRect) {
            super.FindFacesHandler(faceRect);
        }

        // 识别到人但是人不在中间
        @Override
        public void FindFacesNotMiddleHandler(final Context context, float rad) {
            super.FindFacesNotMiddleHandler(context, rad);
//            WakeupAction.AIUIWakeUp( context,0);
        }

        // 没有识别到人
        @Override
        public void NotFindFacesHandler() {
            super.NotFindFacesHandler();
//            CognizePerform.create(cognizePerformCallBack).finish(context);
        }
    };
}
