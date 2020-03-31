package launcher.hotelrobot.com.testsdk;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.Camera;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.tts.Voice;
import android.support.annotation.MainThread;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.speech.SpeechRecognizer;
import android.os.Handler;
import android.media.AudioManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.widget.ImageView;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.media.MicrophoneInfo;
import android.media.AudioDeviceInfo;
import android.media.AudioRecord;
import android.media.AudioFormat;
import android.media.MediaRecorder;
import android.util.Base64;
import android.net.Uri;
import android.os.Environment;
import android.content.pm.PackageManager;
import android.graphics.*;

import com.google.gson.Gson;
import com.hotelrobot.common.Notification;
import com.hotelrobot.common.nethub.NetworkMonitorService;
import com.hotelrobot.common.nethub.entity.DiagnosisResultReponse;
import com.hotelrobot.common.nethub.entity.HotelNotification;
import com.hotelrobot.common.nethub.entity.HotelRobotResponse;
import com.hotelrobot.common.nethub.entity.Marker;
import com.hotelrobot.common.nethub.entity.RobotPowerStatusResponse;
import com.hotelrobot.common.nethub.entity.RobotStatueReponse;
import com.hotelrobot.common.nethub.entity.RobotVelocityReponse;
import com.hotelrobot.common.nethub.entity.RobotWifiDetailResponse;
import com.hotelrobot.common.utils.RkOperationUtil;
import com.interjoy.skface.FaceStruct;
import com.robot.performlib.action.AIUIAction;
import com.robot.performlib.action.ChargeAction;
import com.robot.performlib.action.RobotConnectAction;
import com.robot.performlib.action.RobotInfo;
import com.robot.performlib.action.SDKProp;
import com.robot.performlib.action.SpeakAction;
import com.robot.performlib.action.WakeupAction;
import com.robot.performlib.callback.FragmentMoveCallback;
import com.robot.performlib.callback.MoveCallback;
import com.robot.performlib.callback.PerformFaceDetectCallBack;
import com.robot.performlib.callback.RobotConnectCallBack;
import com.robot.performlib.constant.PerformLibConstant;
import com.robot.performlib.performs.CognizePerform;
import com.robot.performlib.performs.RecognitionPerform;
import com.robot.performlib.performs.StrollPerform;
import com.robot.performlib.position.PositionInfo;
import com.skfacedetect.demo.model.PersonInfo;
import com.skfacedetect.demo.utils.SingleThreadExecutor;
import com.yunjichina.facedetect.SKFaceDetectAction;
import com.yunjichina.facedetect.callback.DBSyncProListener;
import com.yunjichina.facedetect.callback.SKFaceDetectCallback;

import launcher.hotelrobot.com.testsdk.api.RasaApiCall;
import launcher.hotelrobot.com.testsdk.interfaces.AsyncResponse;
import launcher.hotelrobot.com.testsdk.activities.CecLabTour;
import launcher.hotelrobot.com.testsdk.activities.DetectFace;
import launcher.hotelrobot.com.testsdk.activities.MySpeechService;
import launcher.hotelrobot.com.testsdk.api.HttpClientRequest;
import launcher.hotelrobot.com.testsdk.api.WebSocket;
import launcher.hotelrobot.com.testsdk.activities.CameraStream;
import launcher.hotelrobot.com.testsdk.model.faceDetect;
import okhttp3.*;
import tech.gusavila92.websocketclient.WebSocketClient;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.io.*;
import java.util.Set;
//import java.util.Base64;
//import java.net.URI;
import java.nio.*;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,AsyncResponse {

    private static final String CODE = "G596CwPN7HECp3AYqkx5r/jkvl2HBxxhvitEkOL52+nI5r6ohgDHyNiW1clQTYY/";
    private static final String APIKEY = "1e16a4a8df72a35b328626b6fb0b034c";
    private static final String APISECRET = "9106d7787431c0102a252b2a8d9e682a";
    private final String TAG = "MainActivity";
    private Button bt_leadTo, bt_robotState, bt_sendSpeakTxt, bt_sendSpeakNum;
    private ImageButton bt_sendWakeUp, bt_sendSleep;
    private Activity context;
    private Button bt_setSleepTime, bt_getSleepTime, bt_setASRScene, bt_setMainScene;
    private Button bt_hideStatus, bt_showStatus, bt_productId, bt_searchFace, bt_stopSearchFace;
    public TextView tv_productId, tv_intonationTxt, tv_talkingSpeedTxt, tv_heart;
    private SurfaceView surface;
    private SurfaceHolder mHolder;
    private ImageView brain;
    public TextToSpeech tts;
    public CecLabTour cecLabTour;

    private Button bt_getMarkersList;

    private Handler uiHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            tv_intonationTxt.setText(String.valueOf(intonation.getProgress()));
            tv_talkingSpeedTxt.setText(String.valueOf(talkingSpeed.getProgress()));
        }
    };

    private SeekBar intonation, talkingSpeed;
    private Spinner positionSpinner;
    private Button bt_changeVoicer, bt_requestScene, bt_sendStopAudio, bt_showServiceToast, bt_hideServiceToast;
    private Button bt_setContinuousMode, bt_setOneShotMode, bt_requestMarkersList, bt_pauseTTS;
    private Button bt_resumeTTS, bt_textWrite, bt_startSPKProgress, bt_stopSPKProgress;
    private EditText et_text;

    private Map<String, Marker> markers;
    private RadioGroup rg_voicerGroup;
    private Button bt_turnLeft, bt_turnRight;
    private Button bt_startStroll, bt_stopStroll;
    private Button bt_backToCharge, bt_moveCancel, bt_openSoftEStop, bt_closeSoftEStop, bt_reboot;
    private Button bt_shutDown, bt_scrollMarkers, bt_getMarkersCount, bt_requestFloorMarkers, bt_queryBrief;

    private EditText et_insertPoseName, et_insertPoseX, et_insertPoseY, et_insertPoseTheta;
    private EditText et_adjustPoseX, et_adjustPoseY, et_adjustPoseTheta, et_lumValue;
    private Button bt_insertPose, bt_deleteMarker, bt_adjustPose, bt_diagnosis, bt_powerStatus, bt_wifiListDetail, bt_luminance, bt_Velocity;
    private Spinner deleteMarkerSpinner;

    public TextView tv_listen, tv_speak, tv_type, tv_intent, tv_testEnd, tv_testAIUIEnd;
    public TextView tv_testIntraEnd, tv_testAIUIIntraEnd, tv_OutNetState, tv_IntraState, tv_SPKProgress;

    public Button bt_rebootDelay;
    public EditText et_delayTime;

    private static SingleThreadExecutor mExecutor = null;

    private Camera mCamera;

    private static List<String> Faces;

    private List<Marker> markerList;

    private boolean coraActivated;

    private Intent intent;

    private SKFaceDetectCallback faceDetectCallback = new SKFaceDetectCallback() {
        /**
         * 识别到人员进入
         * @param personInfo 如果是陌生人Person_Name为空
         */
        @Override
        public void findPersonIn(PersonInfo personInfo) {
            super.findPersonIn(personInfo);
//            String gender = "unidentified gender";
//            Log.d(TAG, "人员进入-> name：" + personInfo.Person_Name + "\n "
//                    + "PersonID: " + personInfo.Person_id + "\n ");
//            if(personInfo.Gender == 2) {
//                gender = "guy";
//            } else if(personInfo.Gender == 1){
//                gender = "lady";
//            }
//            String speechText = "You are a " + gender + " of age " + personInfo.Age;
//            SpeakAction.getInstance().speak(context, speechText);
        }

        @Override
        public void findPersonOut(PersonInfo personInfo) {
            super.findPersonOut(personInfo);
//            Log.d(TAG, "人员离开-> name：" + personInfo.Person_Name + "\n "
//                    + "PersonID: " + personInfo.Person_id + "\n ");
//            toast("人员离开");
        }

        /**
         * 识别到人
         * @param currFaceStructs 图片中的人员数组
         * @param imageWidth 图片宽度
         * @param imageHeight 图片高度
         * @param data 相机拍摄到的进行人脸识别的图片
         */
        @Override
        public void findingPersonHandler(FaceStruct[] currFaceStructs, int imageWidth, int imageHeight, byte[] data) {
            if (currFaceStructs != null && currFaceStructs.length > 0) {
                byte[] baos = convertYuvToJpeg(data, imageWidth,imageHeight);
                String Base64String = Base64.encodeToString(baos, Base64.DEFAULT);
                WebSocketClient webSocketClient = WebSocket.createWebSocketClient();
                webSocketClient.send("{\"base64String\":" + " \"" + Base64String + "\""+ "}");
                Log.d("face detected","face detected");
            }
        }

        @Override
        public void noPerson() {
            super.noPerson();
        }
    };

    public byte[] convertYuvToJpeg(byte[] data,int imageWidth,int imageHeight) {
        try {
            YuvImage image = new YuvImage(data, ImageFormat.NV21,
                    imageWidth,
                    imageHeight,
                    null);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Rect rect = new Rect(0, 0,
                    imageWidth,
                    imageHeight);
            //set quality
            int quality = 50;
            image.compressToJpeg(rect, quality, baos);
            return baos.toByteArray();
        } catch (Exception e) {
        }
        return null;
    }

    private boolean[] markersCheck;

    private boolean isgetFloorMarkers;
    private SKFaceDetectAction faceDetectAction;

    private static SpeechRecognizer speechRecognizer;

//    private static AudioManager manager;

    protected volatile boolean mIsCountDownOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MyApp.setActivity(this);

        initView();
        initListener();
        initData();

        mExecutor = new SingleThreadExecutor(TAG);
        //初始化人脸识别
        faceDetectAction = SKFaceDetectAction.init(context, APIKEY, APISECRET, new DBSyncProListener() {
            @Override
            public void netDBSyncPro(int SyncPersonNum, int PersonTotal) {
                super.netDBSyncPro(SyncPersonNum, PersonTotal);
                //有人脸数据同步的时候会触发，同步比较快，待同步人数较少时有可能不会触发
            }

            @Override
            public void netRegisteredPersonNum(int Num) {
                super.netRegisteredPersonNum(Num);
                //同步过程中本地人脸数据库发生了变化，会触发回调。Num为本地人脸库总数，
            }
        });

        if (faceDetectAction != null) {
            mExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    bindDevices(CODE);
                }
            });
        }

        tts = new TextToSpeech(this,new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                // TODO Auto-generated method stub
                if(status == TextToSpeech.SUCCESS){
                    int result=tts.setLanguage(Locale.US);
                    if(result==TextToSpeech.LANG_MISSING_DATA ||
                            result==TextToSpeech.LANG_NOT_SUPPORTED){
                        Log.e("error", "This Language is not supported");
                    }
                    else{
                        Log.d("success","success");
                    }
                }
                else
                    Log.e("error", "Initilization Failed!");
            }
        });
        tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            final MainActivity mainActivity = MyApp.getMainActivity();

            @Override
            public void onDone(String utteranceId) {
                boolean isFound = utteranceId.indexOf("lab tour") !=-1 ? true: false;
                if(isFound){
                    String point = utteranceId.substring(utteranceId.length() - 1);
                    int pointToGo = Integer.parseInt(point);
                    cecLabTour.moveToPoint(pointToGo);
                }else{
                    switch(utteranceId){
                        case "":

                            break;
                        default:
                            mainActivity.runOnUiThread(new Runnable() {
                                public void run() {
//                                    googleSpeechToText();
                                    setMuteOn();
                                    speechRecognizer.startListening(intent);
                                }
                            });
                            SKFaceDetectAction.getInstance(context).setCallback(faceDetectCallback).setSurfaceHolder(mHolder).setTime(1000).startDetect();
                    }
                }
            }

            @Override
            public void onError(String utteranceId) {
                mainActivity.runOnUiThread(new Runnable() {
                    public void run() {
//                        googleSpeechToText();
                        setMuteOn();
                        speechRecognizer.startListening(intent);
                    }
                });
                SKFaceDetectAction.getInstance(context).setCallback(faceDetectCallback).setSurfaceHolder(mHolder).setTime(1000).startDetect();
            }

            @Override
            public void onStart(String utteranceId) {
            }
        });
        surface.getHolder().addCallback(surfaceViewcallback);
//        StrollPerform.create(movecallback,context).start(context);
        checkBatteryCountdown.start();
        DetectFace detectFace = new DetectFace();
        detectFace.startFaceRecognition(mHolder);
//        startService(new Intent(this, MySpeechService.class));
        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        googleSpeechToText();
//        CameraStream cameraStream = new CameraStream(context,null);
//        cameraStream.changeResolution(320, 240);
//        CaptureImage();
    }


    protected void setMuteOn(){
        AudioManager manager=(AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        manager.setStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_MUTE, 0);
        manager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, AudioManager.ADJUST_MUTE, 0);
        manager.setStreamVolume(AudioManager.STREAM_ALARM, AudioManager.ADJUST_MUTE, 0);
        manager.setStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_MUTE, 0);
        manager.setStreamVolume(AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_MUTE, 0);
    }


    protected void setMuteOff(){
        AudioManager manager=(AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        manager.setStreamVolume(AudioManager.STREAM_MUSIC, 30, 0);
        manager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 30, 0);
        manager.setStreamVolume(AudioManager.STREAM_ALARM, 30, 0);
        manager.setStreamVolume(AudioManager.STREAM_RING, 30, 0);
        manager.setStreamVolume(AudioManager.STREAM_SYSTEM,30, 0);
    }


    protected CountDownTimer mNoSpeechCountDown = new CountDownTimer(4000, 4000)
    {
        @Override
        public void onTick(long millisUntilFinished)
        {
            // TODO Auto-generated method stub

        }

        @Override
        public void onFinish()
        {
           // TODO handle on countdown finish
        }
    };

    public void googleSpeechToText(){

        if(speechRecognizer != null){
            speechRecognizer.destroy();
            speechRecognizer = null;
        }
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
//        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
//        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

//      mNoSpeechCountDown.start();

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {
            }

            @Override
            public void onBeginningOfSpeech() {
//
            }

            @Override
            public void onRmsChanged(float v) {
//                String s=String.valueOf(v);
//                Log.d("amplitude", s);
            }

            @Override
            public void onBufferReceived(byte[] bytes) {
            }

            @Override
            public void onEndOfSpeech() {
            }

            @Override
            public void onError(int i) {
                String s=String.valueOf(i);
                Log.d("error", s);
                googleSpeechToText();
//                speechRecognizer.startListening(intent);
            }

            @Override
            public void onResults(Bundle bundle) {
                ArrayList<String> matches = bundle.getStringArrayList(speechRecognizer.RESULTS_RECOGNITION);

                String text = matches.get(0);
                Log.d("matches",matches.toString());
//
                text = text.replaceAll("[^a-zA-Z0-9\\s]", "");

                if(matches!=null){
                    toast(matches.get(0));
                }

                setMuteOff();

                markerList = PositionInfo.init(MainActivity.this).getMarkerList();

                switch (text.toLowerCase()) {
                    case "go charge":
                        backToCharge();
                        break;

//                    case "get to work":
//                        if(speechRecognizer != null){
//                            speechRecognizer.destroy();
//                            speechRecognizer = null;
//                        }
//                        if (markerList == null || markerList.size() == 0)
//                            break;
//                        RobotConnectAction.init(context).setConnectCallback(robotConnectCallBack).sendMoveMarker(markerList.get(0));
//                        tts.speak("Sure! more than happy to", TextToSpeech.QUEUE_ADD, null,"UniqueId");
//                        break;
//                    case "do your job":
//                        if (markerList == null || markerList.size() == 0)
//                            break;
//
//                        cecLabTour = new CecLabTour();
//                        cecLabTour.startTour(markerList);
//                        break;
                    default:
                        if(coraActivated){
                            if( text.toLowerCase().indexOf("take a break") != -1){
                                coraActivated = false;
                                tts.speak("Okay good bye!", TextToSpeech.QUEUE_ADD, null,"UniqueId");
                            } else {
                                handleRasaCall(matches.get(0));
                            }
                        } else if( text.toLowerCase().indexOf("time to work") != -1){
                            coraActivated = true;
                            if(Faces!=null){
                                tts.speak("Hi! good to see you, " + Faces.get(0), TextToSpeech.QUEUE_ADD, null,"UniqueId");
                            }else{
                                tts.speak("Hi! good to see you!", TextToSpeech.QUEUE_ADD, null,"UniqueId");
                            }
                        } else {
//                            googleSpeechToText();
                            setMuteOn();
                            speechRecognizer.startListening(intent);
                        }
                        break;
                }


            }

            @Override
            public void onPartialResults(Bundle bundle) {
            }

            @Override
            public void onEvent(int i, Bundle bundle) {
            }
        });

        setMuteOn();
        speechRecognizer.startListening(intent);
    }


    private void handleRasaCall(String voiceText){
        RasaApiCall rasaApi = new RasaApiCall();
        rasaApi.callback = this;

        Random rand = new Random();
        // Generate random integers in range 0 to 999 for recipient
        int rand_int = rand.nextInt(1000);
//        rasaApi.execute("http://192.168.1.122:5005/webhooks/rest/webhook",voiceText,"bot_user_5");
        rasaApi.execute("http://205.252.40.121:7020/webhooks/rest/webhook",voiceText,"bot_user_4");
    }

    @Override
    public void processResult(String output) {
        boolean newPerson = output.toLowerCase().indexOf("how are you today") != -1 ? true : false;
        boolean RecognizeFace = output.toLowerCase().indexOf("my facial recognition system is under development") != -1 ? true : false;
        boolean showCoffeeSpot = output.toLowerCase().indexOf("toilet") != -1 ? true : false;
        if (newPerson && Faces.size() > 0) {
            String textToSpeak = output + " It's nice to see you " + Faces.get(0);
            tts.speak(textToSpeak, TextToSpeech.QUEUE_ADD, null, "UniqueId");
        } else if (RecognizeFace) {
            String textToSpeak = "";
            if (Faces.size() > 0) {
                textToSpeak = "You are a beautiful person " + Faces.get(0);
            } else {
                textToSpeak = "Unfortunately I do not know who you are";
            }

            tts.speak(textToSpeak, TextToSpeech.QUEUE_ADD, null, "UniqueId");
        } else if (showCoffeeSpot) {
            RobotConnectAction.init(context).setConnectCallback(robotConnectCallBack).sendMoveMarker(markerList.get(7));
            tts.speak(output, TextToSpeech.QUEUE_ADD, null, "UniqueId");
        } else{
            tts.speak(output, TextToSpeech.QUEUE_ADD, null, "UniqueId");
        }
    }

    public static void processFaces(List<String> output){
//        DetectFace detectFace = new DetectFace();
        Faces = output;
//        if (output.size() == 0){
//            tts.speak("hi there, how are you?", TextToSpeech.QUEUE_ADD, null,"UniqueId");
//        } else {
//            Faces = output;
//            for (int i = 0; i < output.size(); i++) {
//                String name = output.get(i);
//                Log.d("name",name);
////                tts.speak("hi there, how are you " + name, TextToSpeech.QUEUE_ADD, null,"UniqueId");
////                detectFace.startFaceRecognition(mHolder);
////            }
//        }
    }

    @Override
    public void processImage(String image){
        Log.d("processing","processing");
    }

    public void CaptureImage(){
        Camera c = null;
        try {
            c = Camera.open(0); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
            Log.d("test", "test");
        }
        try {
            c.setPreviewDisplay(mHolder);
            c.setPreviewCallbackWithBuffer(mPreviewCallback);
            c.startPreview();
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
        c.takePicture(null, null, mPicture);
    }

    private Camera.PreviewCallback mPreviewCallback = new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            Log.d("camera","camera started");
        }
    };

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            HttpClientRequest httpClientRequest = new HttpClientRequest();
            httpClientRequest.callback = MainActivity.this;
            System.out.println("***************");
            System.out.println(Base64.encodeToString(data, Base64.DEFAULT));
            System.out.println("***************");
            String Base64String = Base64.encodeToString(data, Base64.DEFAULT);
            camera.release();
            JSONObject faceObject = new JSONObject();
                try{
                    faceObject.put("base64String",Base64String);
                } catch (Exception e){
                    e.printStackTrace();
                }
                try {
                    httpClientRequest.execute("http://192.168.10.201:5000",Base64String);
//                    httpClientRequest.execute("http://192.168.1.12:5000",Base64String);
                } catch (Exception e) {
                    Log.d("error","error");
                }
        }
    };
    /**
     * 绑定设备串码
     *
     * @param code
     */
    private void bindDevices(final String code) {
        faceDetectAction.bindDevices(code, new SKFaceDetectAction.BindResultCallback() {
            @Override
            public void bindResult(int i, String s) {
                Log.d(TAG, "[绑定设备串码] code:" + i + "   msg:" + s);
                if (i != 0) {
                    bindDevices(code);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    //网络监控回调 数据每5秒回调1次
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            NetworkMonitorService.NetSpeedMonitorBinder bindNet
                    = (NetworkMonitorService.NetSpeedMonitorBinder) service;
            bindNet.startMonitor();
            bindNet.setNetListener(new NetworkMonitorService.NetWorkListener() {
                @Override
                public void testEnd(final double speed) {
                    super.testEnd(speed);
                    //获取外网ping值
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv_testEnd.setText("主板外网ping值：" + speed + " ms");
                        }
                    });
                }

                @Override
                public void testIntraEnd(final double speed) {
                    super.testIntraEnd(speed);
                    //获取内网ping值
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv_testIntraEnd.setText("主板内网ping值：" + speed + " ms");
                        }
                    });
                }

                @Override
                public void testAIUIIntraEnd(final double speed) {
                    super.testAIUIIntraEnd(speed);
                    //获取核心板内网ping值
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv_testAIUIIntraEnd.setText("核心板内网ping值：" + speed + " ms");
                        }
                    });
                }

                @Override
                public void testAIUIEnd(final double speed) {
                    super.testAIUIEnd(speed);
                    //获取核心板外网ping值
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv_testAIUIEnd.setText("核心板外网ping值：" + speed + " ms");
                        }
                    });
                }

                @Override
                public void IntraState(int state) {
                    super.IntraState(state);
                    //内网状态 -1:网络未连接  0:较差  1:一般  2:良好
                    final String sstate;
                    switch (state) {
                        case 0:
                            sstate = "较差";
                            break;
                        case 1:
                            sstate = "一般";
                            break;
                        case 2:
                            sstate = "良好";
                            break;
                        default:
                            sstate = "网络未连接";
                    }

                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv_IntraState.setText("内网状态：" + sstate);
                        }
                    });
                }

                @Override
                public void OutNetState(int state) {
                    super.OutNetState(state);
                    //外网状态 -1:网络未连接  0:较差  1:一般  2:良好
                    final String sstate;
                    switch (state) {
                        case 0:
                            sstate = "较差";
                            break;
                        case 1:
                            sstate = "一般";
                            break;
                        case 2:
                            sstate = "良好";
                            break;
                        default:
                            sstate = "网络未连接";
                    }

                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv_OutNetState.setText("外网状态：" + sstate);
                        }
                    });
                }
            });
        }
    };

    private void initData() {

        Intent bindIntent = new Intent(this, NetworkMonitorService.class);
        bindService(bindIntent, connection, BIND_AUTO_CREATE);

        context = this;

        //加载引领下拉框数据
        ArrayList<String> markerNameList;
        markers = PositionInfo.init(this).getMarkers();
        if (markers == null || markers.size() == 0) {
            markerNameList = new ArrayList<>();
            markerNameList.add("请点击\"获取地图上所有点位列表\"按钮");
        } else {
            markerNameList = new ArrayList<>(markers.keySet());
        }

        ArrayAdapter arr_adapter = new ArrayAdapter<String>(this, R.layout.spinneritem, markerNameList);
        arr_adapter.setDropDownViewResource(R.layout.spinneritem);
        positionSpinner.setAdapter(arr_adapter);
        deleteMarkerSpinner.setAdapter(arr_adapter);

        bt_sendSpeakTxt.setText("现在是" + getDateToString(System.currentTimeMillis()));
        bt_sendSpeakNum.setText("现在是" + getDateToString(System.currentTimeMillis()));

    }


    private SimpleDateFormat sf = new SimpleDateFormat("yyyy年");

    public String getDateToString(long time) {
        Date d = new Date(time);
        return sf.format(d);
    }

    private void initListener() {

        bt_searchFace.setOnClickListener(this);
        bt_stopSearchFace.setOnClickListener(this);

        bt_sendWakeUp.setOnClickListener(this);
        bt_sendSleep.setOnClickListener(this);
        bt_sendSpeakNum.setOnClickListener(this);
        bt_sendSpeakTxt.setOnClickListener(this);
        bt_getSleepTime.setOnClickListener(this);
        bt_setSleepTime.setOnClickListener(this);
        bt_setASRScene.setOnClickListener(this);
        bt_setMainScene.setOnClickListener(this);
        bt_requestScene.setOnClickListener(this);
        bt_sendStopAudio.setOnClickListener(this);
        bt_showServiceToast.setOnClickListener(this);
        bt_hideServiceToast.setOnClickListener(this);
        bt_setOneShotMode.setOnClickListener(this);
        bt_setContinuousMode.setOnClickListener(this);
        bt_pauseTTS.setOnClickListener(this);
        bt_resumeTTS.setOnClickListener(this);
        bt_textWrite.setOnClickListener(this);
        bt_startSPKProgress.setOnClickListener(this);
        bt_stopSPKProgress.setOnClickListener(this);

        bt_hideStatus.setOnClickListener(this);
        bt_showStatus.setOnClickListener(this);
        bt_productId.setOnClickListener(this);

        bt_leadTo.setOnClickListener(this);
        bt_turnLeft.setOnClickListener(this);
        bt_turnRight.setOnClickListener(this);
        bt_startStroll.setOnClickListener(this);
        bt_stopStroll.setOnClickListener(this);
        bt_robotState.setOnClickListener(this);
        bt_requestMarkersList.setOnClickListener(this);
        bt_backToCharge.setOnClickListener(this);
        bt_moveCancel.setOnClickListener(this);
        bt_openSoftEStop.setOnClickListener(this);
        bt_closeSoftEStop.setOnClickListener(this);
        bt_reboot.setOnClickListener(this);
        bt_shutDown.setOnClickListener(this);
        bt_rebootDelay.setOnClickListener(this);
        bt_scrollMarkers.setOnClickListener(new CheckBoxClickListener());
        bt_getMarkersCount.setOnClickListener(this);
        bt_requestFloorMarkers.setOnClickListener(this);
        bt_queryBrief.setOnClickListener(this);
        bt_insertPose.setOnClickListener(this);

        bt_deleteMarker.setOnClickListener(this);
        bt_adjustPose.setOnClickListener(this);
        bt_diagnosis.setOnClickListener(this);
        bt_powerStatus.setOnClickListener(this);
        bt_wifiListDetail.setOnClickListener(this);
        bt_luminance.setOnClickListener(this);
        bt_Velocity.setOnClickListener(this);

        bt_changeVoicer.setOnClickListener(this);
        intonation.setOnSeekBarChangeListener(seekBarListener);
        talkingSpeed.setOnSeekBarChangeListener(seekBarListener);

        bt_getMarkersList.setOnClickListener(this);
    }

    private void initView() {

        tv_heart = (TextView) findViewById(R.id.tv_heart);

        bt_searchFace = (Button) findViewById(R.id.searchFace);
        bt_stopSearchFace = (Button) findViewById(R.id.stopSearchFace);
        surface = (SurfaceView) findViewById(R.id.surface);
        brain = (ImageView) findViewById(R.id.brain);

        bt_sendWakeUp = (ImageButton) findViewById(R.id.sendWakeUp);
        bt_sendSleep = (ImageButton) findViewById(R.id.sendSleep);
        bt_sendSpeakTxt = (Button) findViewById(R.id.sendSpeakTxt);
        bt_sendSpeakNum = (Button) findViewById(R.id.sendSpeakNum);
        bt_setSleepTime = (Button) findViewById(R.id.setSleepTime);
        bt_getSleepTime = (Button) findViewById(R.id.getSleepTime);

        bt_setASRScene = (Button) findViewById(R.id.setASRScene);
        bt_setMainScene = (Button) findViewById(R.id.setMainScene);

        bt_hideStatus = (Button) findViewById(R.id.hideStatus);
        bt_showStatus = (Button) findViewById(R.id.showStatus);
        bt_productId = (Button) findViewById(R.id.getProductId);
        tv_productId = (TextView) findViewById(R.id.productId);

        positionSpinner = (Spinner) findViewById(R.id.PositionSpinner);
        bt_leadTo = (Button) findViewById(R.id.leadTo);
        bt_turnLeft = (Button) findViewById(R.id.turnLeft);
        bt_turnRight = (Button) findViewById(R.id.turnRight);
        bt_robotState = (Button) findViewById(R.id.robotStatus);
        bt_requestMarkersList = (Button) findViewById(R.id.requestMarkersList);
        bt_backToCharge = (Button) findViewById(R.id.backToCharge);
        bt_moveCancel = (Button) findViewById(R.id.moveCancel);
        bt_openSoftEStop = (Button) findViewById(R.id.openSoftEStop);
        bt_closeSoftEStop = (Button) findViewById(R.id.closeSoftEStop);
        bt_reboot = (Button) findViewById(R.id.reboot);
        bt_shutDown = (Button) findViewById(R.id.shutDown);
        bt_scrollMarkers = (Button) findViewById(R.id.scrollMarkers);
        bt_getMarkersCount = (Button) findViewById(R.id.getMarkersCount);
        bt_requestFloorMarkers = (Button) findViewById(R.id.requestFloorMarkers);
        bt_queryBrief = (Button) findViewById(R.id.queryBrief);
        bt_startStroll = (Button) findViewById(R.id.startStroll);
        bt_stopStroll = (Button) findViewById(R.id.stopStroll);

        //按坐标添加点位
        et_insertPoseName = (EditText) findViewById(R.id.insertPoseName);
        et_insertPoseX = (EditText) findViewById(R.id.insertPoseX);
        et_insertPoseY = (EditText) findViewById(R.id.insertPoseY);
        et_insertPoseTheta = (EditText) findViewById(R.id.insertPoseTheta);
        bt_insertPose = (Button) findViewById(R.id.insertByPose);

        //删除点位
        deleteMarkerSpinner = (Spinner) findViewById(R.id.deleteMarkerSpinner);
        bt_deleteMarker = (Button) findViewById(R.id.deleteMarker);

        //按坐标校正位置
        et_adjustPoseX = (EditText) findViewById(R.id.adjustPoseX);
        et_adjustPoseY = (EditText) findViewById(R.id.adjustPoseY);
        et_adjustPoseTheta = (EditText) findViewById(R.id.adjustPoseTheta);
        bt_adjustPose = (Button) findViewById(R.id.adjustByPose);

        bt_diagnosis = (Button) findViewById(R.id.diagnosis);
        bt_powerStatus = (Button) findViewById(R.id.powerStatus);
        bt_wifiListDetail = (Button) findViewById(R.id.wifiListDetail);
        bt_luminance = (Button) findViewById(R.id.luminance);
        et_lumValue = (EditText) findViewById(R.id.lumValue);
        bt_Velocity = (Button) findViewById(R.id.Velocity);

        intonation = (SeekBar) findViewById(R.id.intonation);
        talkingSpeed = (SeekBar) findViewById(R.id.talkingSpeed);
        tv_intonationTxt = (TextView) findViewById(R.id.intonationTxt);
        tv_talkingSpeedTxt = (TextView) findViewById(R.id.talkingSpeedTxt);
        rg_voicerGroup = (RadioGroup) findViewById(R.id.voicer);
        bt_changeVoicer = (Button) findViewById(R.id.changeVoicer);
        bt_requestScene = (Button) findViewById(R.id.requestScene);
        bt_sendStopAudio = (Button) findViewById(R.id.sendStopAudio);
        bt_showServiceToast = (Button) findViewById(R.id.showServiceToast);
        bt_hideServiceToast = (Button) findViewById(R.id.hideServiceToast);
        bt_setContinuousMode = (Button) findViewById(R.id.setContinuousMode);
        bt_setOneShotMode = (Button) findViewById(R.id.setOneShotMode);
        bt_pauseTTS = (Button) findViewById(R.id.pauseTTS);
        bt_resumeTTS = (Button) findViewById(R.id.resumeTTS);
        et_text = (EditText) findViewById(R.id.text);
        bt_textWrite = (Button) findViewById(R.id.textWrite);
        bt_startSPKProgress = (Button) findViewById(R.id.startSPKProgress);
        bt_stopSPKProgress = (Button) findViewById(R.id.stopSPKProgress);

        tv_listen = (TextView) findViewById(R.id.listen);
        tv_speak = (TextView) findViewById(R.id.speak);
        tv_type = (TextView) findViewById(R.id.type);
        tv_intent = (TextView) findViewById(R.id.intent);
        tv_testEnd = (TextView) findViewById(R.id.testEnd);
        tv_testAIUIEnd = (TextView) findViewById(R.id.testAIUIEnd);
        tv_testIntraEnd = (TextView) findViewById(R.id.testIntraEnd);
        tv_testAIUIIntraEnd = (TextView) findViewById(R.id.testAIUIIntraEnd);
        tv_OutNetState = (TextView) findViewById(R.id.OutNetState);
        tv_IntraState = (TextView) findViewById(R.id.IntraState);
        tv_SPKProgress = (TextView) findViewById(R.id.speakProgress);

        et_delayTime = (EditText) findViewById(R.id.rebootTime);
        bt_rebootDelay = (Button) findViewById(R.id.rebootDelay);

        bt_getMarkersList = (Button) findViewById(R.id.getMarkersList);
    }

    //
    private FragmentMoveCallback callback = new FragmentMoveCallback() {
        @Override
        public void MoveCompleteHandler() {
            super.MoveCompleteHandler();

            /**
             * do somethings
             *
             * Marker currentMarker = StrollPerform.create(callback, context).getCurrentMarker();
             * Log.d(TAG, "Stroll： current Marker->" + currentMarker.marker_name);
             */

            //每个巡游点移动成功（或失败）以后，再次调用start()移动到下一个点
            StrollPerform.create(callback, context).start(context);
        }
    };

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.searchFace:
                brain.setVisibility(View.GONE);
                if (surface.getVisibility() != View.VISIBLE && mHolder == null) {
                    surface.getHolder().addCallback(surfaceViewcallback);
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            surface.setVisibility(View.VISIBLE);
                        }
                    });
                } else {
//                    searchFace();
//                    或者注释掉上面一行，替换成下面的一行
//                    CognizePerform.create(cognizePerformCallBack).setSurfaceHolder(mHolder).start(context);
                }

                break;


            case R.id.stopSearchFace:
//                if (faceDetectAction != null)
//                    faceDetectAction.releaseCamera();

                //或者注释掉上面一行，替换成下面的一行
                CognizePerform.create(null).finish(this);
                break;

            case R.id.setSleepTime:
                AIUIAction.setSleepTime(this, 10000);
                break;

            case R.id.getSleepTime:
                AIUIAction.requestSleepTime(this);
                break;

            case R.id.hideStatus:
                RkOperationUtil.hideTab(context);
                break;

            case R.id.showStatus:
                RkOperationUtil.showTab(context);
                break;

            case R.id.getProductId:
                RobotInfo.requestProductId(context);
                break;

            case R.id.sendWakeUp:
                //指定唤醒波束为0的麦克风
//                WakeupAction.AIUIWakeUp(this, 0);
                googleSpeechToText();
                break;

            case R.id.sendSleep:
//                WakeupAction.AIUISleep(this);
                if(speechRecognizer != null){
                    speechRecognizer.destroy();
                    speechRecognizer = null;
                }
                break;

            case R.id.getMarkersList:
                final List<Marker> markerList = PositionInfo.init(MainActivity.this).getMarkerList();
                if (markerList == null || markerList.size() == 0)
                    break;

//                RobotConnectAction.init(context).sendMoveMarker(markerList.get(5));
                backToCharge();
                break;

            case R.id.sendSpeakTxt:
                SpeakAction.getInstance().speak(MainActivity.this
                        , bt_sendSpeakTxt.getText().toString(), "时间", SpeakAction.RDN.TXT);
                break;

            case R.id.sendSpeakNum:
                SpeakAction.getInstance().speak(MainActivity.this
                        , bt_sendSpeakNum.getText().toString(), "时间", SpeakAction.RDN.NUM);
                break;

            case R.id.setASRScene:
                AIUIAction.changeScene(context, AIUIAction.Scene.asr);
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showDialog("切换为静默场景", "该场景下只能接收唤醒和语音识别的事件，机器重启后失效");
                    }
                });
                break;

            case R.id.setMainScene:
                AIUIAction.changeScene(context, AIUIAction.Scene.main);
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showDialog("切换回主场景", "该场景下配置的语音指令和问答库都正常使用");
                    }
                });
                break;

            case R.id.requestScene:
                AIUIAction.requestScene(context);
                break;

            case R.id.sendStopAudio:
                AIUIAction.sendStopAudio(context);
                break;

            case R.id.showServiceToast:
                AIUIAction.setServiceToastShow(context, true);
                break;

            case R.id.hideServiceToast:
                AIUIAction.setServiceToastShow(context, false);

                break;

            case R.id.setContinuousMode:
                AIUIAction.setInteractMode(context, AIUIAction.InteractMode.continuous);
                break;

            case R.id.setOneShotMode:
                AIUIAction.setInteractMode(context, AIUIAction.InteractMode.oneshot);
                break;

            case R.id.pauseTTS:
                SpeakAction.getInstance().pauseSpeaking(context);
                break;

            case R.id.resumeTTS:
                SpeakAction.getInstance().resumeSpeaking(context);
                break;

            case R.id.textWrite:
                String trim = et_text.getText().toString().trim();

                if (!TextUtils.isEmpty(trim)) {
                    if (MyApp.isWakeUp) {
                        //在使用文本交互功能时需要机器处于唤醒状态
                        AIUIAction.textWrite(context, trim);
                    } else {
                        SpeakAction.getInstance().speak(context, "请先唤醒我，再和我对话哦");
                    }
                } else {
                    SpeakAction.getInstance().speak(context, "请输入正确的文本内容");
                }
                break;

            case R.id.leadTo:
                if (markers != null && markers.size() > 0) {
                    RobotConnectAction.init(context).setMaxContinuousRetries(5);
                    RobotConnectAction.init(context).sendMoveMarker(markers.get(positionSpinner.getSelectedItem()));
                } else {
                    SpeakAction.getInstance().speak(context, "请选择正确的点位");
                }
                break;

            case R.id.turnLeft:
                RecognitionPerform.create().turnAround(context, 60);
                break;

            case R.id.turnRight:
                RecognitionPerform.create().turnAround(context, -60);
                break;

            case R.id.startStroll:
                //开始巡游当前楼层除充电桩之外的所有点位
                StrollPerform.create(callback, context).start(context);
                break;

            case R.id.stopStroll:
                //停止巡游
                StrollPerform.create(callback, context).finish(context);
                break;

            case R.id.robotStatus:
                RobotConnectAction.init(this).setConnectCallback(robotConnectCallBack).robotStatus();
                break;

            case R.id.getMarkersCount:
                RobotConnectAction.init(this).setConnectCallback(robotConnectCallBack).getMarkersCount();
                break;

            case R.id.requestMarkersList:
                RobotConnectAction.init(this).setConnectCallback(robotConnectCallBack).requestMarkersList();
                break;

            case R.id.requestFloorMarkers:
                isgetFloorMarkers = true;
                RobotConnectAction.init(this).setConnectCallback(robotConnectCallBack).robotStatus();
                break;

            case R.id.backToCharge:
                backToCharge();
                break;

            case R.id.moveCancel:
                RobotConnectAction.init(context).setConnectCallback(robotConnectCallBack).sendCancel();
                break;

            case R.id.openSoftEStop:
                RobotConnectAction.init(context).eStop(true);
                break;

            case R.id.closeSoftEStop:
                RobotConnectAction.init(context).eStop(false);
                break;

            case R.id.reboot:
                RobotConnectAction.init(context).shutdown(true);
                break;

            case R.id.shutDown:
                RobotConnectAction.init(context).shutdown();
                break;

            case R.id.rebootDelay:
                String s = et_delayTime.getText().toString();
                RobotConnectAction.init(context).shutdown(Integer.valueOf(s));
                break;

            case R.id.changeVoicer:
                changeVoicer();
                break;

            case R.id.queryBrief:
                RobotConnectAction.init(context).setConnectCallback(robotConnectCallBack).getMarkersBrief();
                break;

            case R.id.startSPKProgress:
                SpeakAction.getInstance().startSPKProgress(context);
                break;

            case R.id.stopSPKProgress:
                SpeakAction.getInstance().stopSPKProgress(context);
                break;

            case R.id.insertByPose:
                String inserName = et_insertPoseName.getText().toString().trim();
                String insertX = et_insertPoseX.getText().toString().trim();
                String insertY = et_insertPoseY.getText().toString().trim();
                String insertTheta = et_insertPoseTheta.getText().toString().trim();

                if (!TextUtils.isEmpty(inserName) && !TextUtils.isEmpty(insertX)
                        && !TextUtils.isEmpty(insertY) && !TextUtils.isEmpty(insertTheta))
                    RobotConnectAction.init(context).insertByPose(inserName, Double.valueOf(insertX)
                            , Double.valueOf(insertY), Double.valueOf(insertTheta));
                break;

            case R.id.deleteMarker:
                RobotConnectAction.init(context).deleteMarker(markers.get(deleteMarkerSpinner.getSelectedItem()).marker_name);
                break;

            case R.id.adjustByPose:
                String adjustX = et_adjustPoseX.getText().toString().trim();
                String adjustY = et_adjustPoseY.getText().toString().trim();
                String adjustTheta = et_adjustPoseTheta.getText().toString().trim();

                if (!TextUtils.isEmpty(adjustX) && !TextUtils.isEmpty(adjustY) && !TextUtils.isEmpty(adjustTheta))
                    RobotConnectAction.init(context).robotPositionAdjustByPose(Double.valueOf(adjustX)
                            , Double.valueOf(adjustY), Double.valueOf(adjustTheta));
                break;

            case R.id.diagnosis:
                RobotConnectAction.init(context).setConnectCallback(robotConnectCallBack).getCheckResult();
                break;

            case R.id.powerStatus:
                RobotConnectAction.init(context).setConnectCallback(robotConnectCallBack).getPowerStatus();
                break;

            case R.id.wifiListDetail:
                RobotConnectAction.init(context).setConnectCallback(robotConnectCallBack).getWiFiListDetail();
                break;

            case R.id.luminance:
                String value = et_lumValue.getText().toString().trim();
                if (!TextUtils.isEmpty(value))
                    RobotConnectAction.init(context).setRobotLuminance(Integer.valueOf(value));
                break;

            case R.id.Velocity:
                RobotConnectAction.init(context).setConnectCallback(robotConnectCallBack).requestData_Velocity();
                break;

            default:
                break;
        }
    }

    public void backToCharge() {
        Marker markerChargingPile = PositionInfo.init(context).getMarkerChargingPile();
        if (markerChargingPile == null) {
            SpeakAction.getInstance().speak(context, "请标记充电桩点位");
        } else {
            ChargeAction.init(context, new MoveCallback() {
                @Override
                public void MoveCompleteHandler() {
                    super.MoveCompleteHandler();
                    tts.speak("I am back charging", TextToSpeech.QUEUE_ADD, null,"UniqueId");
//                    SpeakAction.getInstance().speak(context, "我充上电了");
                }

                @Override
                public void MoveFailedHandler() {
                    super.MoveFailedHandler();
                    googleSpeechToText();
//                    SpeakAction.getInstance().speak(context, "我没充上电，请帮我充电");
                }

                @Override
                public void estopPrompt(boolean estop) {
                    super.estopPrompt(estop);
                    googleSpeechToText();
//                    SpeakAction.getInstance().speak(context, "请松开急停按钮，我要去充电了");
                }

                @Override
                public void MoveResult(String code) {
                    super.MoveResult(code);
                }
            }).action(context);
        }
    };

    private void searchFace() {
        // 每10s识别一次
        if (faceDetectAction != null){
            SKFaceDetectAction.getInstance(context).setCallback(faceDetectCallback).setSurfaceHolder(mHolder).setTime(1000).startDetect();
        }
    };

    private void changeVoicer() {
        int checkId = rg_voicerGroup.getCheckedRadioButtonId();
        int Int_intonation = intonation.getProgress();
        int Int_speed = talkingSpeed.getProgress();
        switch (checkId) {
            case R.id.voicer_mengmeng:
                AIUIAction.changeVoicer(MainActivity.this, AIUIAction.Voicer.mengmeng, Int_speed, Int_intonation);
                break;
            case R.id.voicer_xiaofang:
                AIUIAction.changeVoicer(MainActivity.this, AIUIAction.Voicer.xiaofang, Int_speed, Int_intonation);
                break;
            case R.id.voicer_xiaofeng:
                AIUIAction.changeVoicer(MainActivity.this, AIUIAction.Voicer.xiaofeng, Int_speed, Int_intonation);
                break;
            case R.id.voicer_xiaohou:
                AIUIAction.changeVoicer(MainActivity.this, AIUIAction.Voicer.xiaohou, Int_speed, Int_intonation);
                break;
            case R.id.voicer_xiaotong:
                AIUIAction.changeVoicer(MainActivity.this, AIUIAction.Voicer.xiaotong, Int_speed, Int_intonation);
                break;
            case R.id.voicer_xiaoxue:
                AIUIAction.changeVoicer(MainActivity.this, AIUIAction.Voicer.xiaoxue, Int_speed, Int_intonation);
                break;
            case R.id.voicer_xiaoyan:
                AIUIAction.changeVoicer(MainActivity.this, AIUIAction.Voicer.xiaoyan, Int_speed, Int_intonation);
                break;
            case R.id.voicer_yueyu:
                AIUIAction.changeVoicer(MainActivity.this, AIUIAction.Voicer.xiaomei, Int_speed, Int_intonation);
                break;
        }
    };

    //引领任务回调
    private FragmentMoveCallback movecallback = new FragmentMoveCallback () {
        @Override
        public void MoveCompleteHandler() {
            super.MoveCompleteHandler(); // 移动完成
        }

        @Override
        public void MoveFailedHandler() {
            super.MoveFailedHandler(); // 移动失败
        }

        @Override
        public void MoveRetryHandler() {
            super.MoveRetryHandler();
            // 移动重试(1.0.6 新增)
        }

        @Override
        public void successLeaveDock() {
            super.successLeaveDock();
            // 离开充电桩成功(1.0.6 新增)
        }

        @Override
        public void estopPrompt(boolean estop) {
            super.estopPrompt(estop); //急停状态，只在开始执行任务的时候检查一次(1.0.6 新增)
        }

        @Override
        public void chargePrompt() {
            super.chargePrompt(); //电量低于限定值，开始自动回充(1.0.6 新增)
        }

        @Override
        public void chargeCompletePrompt() {
            super.chargeCompletePrompt(); //电量大于限定值，充电完成(1.0.6 新增)
        }

        @Override
        public void chargeFailed() {
            super.chargeFailed(); //充电失败(1.1.4.4 新增)
        }
    };

    //SurfaceView加载状态回调
    private SurfaceHolder.Callback surfaceViewcallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            mHolder = holder;
//            CaptureImage();
            searchFace();
            //或者注释掉上面一行，替换成下面的一行
//            DetectFace detectFace = new DetectFace();
//            detectFace.startFaceRecognition(mHolder);
//            CognizePerform.create(cognizePerformCallBack).setSurfaceHolder(mHolder).start(context);
//            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            mHolder = null;
        }
    };


    //seekBar监听
    private SeekBar.OnSeekBarChangeListener seekBarListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            uiHandler.sendEmptyMessage(0);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    //底盘数据回调
    private RobotConnectCallBack robotConnectCallBack = new RobotConnectCallBack() {

        //指令发送的结果回调
        @Override
        public void robotHotelRobotResponse(HotelRobotResponse info) {
            super.robotHotelRobotResponse(info);
            if (TextUtils.equals(info.type, PerformLibConstant.ResponseType.response)) {
                if (TextUtils.equals(info.command, PerformLibConstant.ConnectUrl.cancel)) {
//                    SpeakAction.getInstance().speak(context, "移动任务取消成功");
                }
            }
        }

        @Override
        public void robotCancelError() {
            super.robotCancelError();
            //取消移动指令发送失败，尝试再次发送
            RobotConnectAction.init(context).setConnectCallback(robotConnectCallBack).sendCancel();

        }

        //获取机器状态的回调
        @Override
        public void robotStatusResult(final RobotStatueReponse data) {
            super.robotStatusResult(data);

            if (isgetFloorMarkers) {
                isgetFloorMarkers = false;
                //获取当前楼层的点位
                RobotConnectAction.init(context).setConnectCallback(robotConnectCallBack)
                        .requestMarkersList(data.results.current_floor);
            } else {
                toast(data.toString());
            }
        }

        @Override
        public void robotStatusResultError() {
            super.robotStatusResultError();
            // 发送获取机器状态的指令失败，尝试再次发送
            RobotConnectAction.init(context).setConnectCallback(robotConnectCallBack).robotStatus();
        }

        //获取点位列表的回调
        @Override
        public void robotMarkersListResult(final HashMap<String, Marker> mapMarker) {
            super.robotMarkersListResult(mapMarker);
            ArrayList<String> markerNameList;
            if (mapMarker == null || mapMarker.size() == 0) {
                markerNameList = new ArrayList<>();
                markerNameList.add("请再次点击\"获取地图上所有点位列表\"按钮");
            } else {
                markers = mapMarker;
                markerNameList = new ArrayList<>(mapMarker.keySet());
            }

            final ArrayAdapter arr_adapter = new ArrayAdapter<String>(MainActivity.this, R.layout.spinneritem, markerNameList);
            arr_adapter.setDropDownViewResource(R.layout.spinneritem);
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    positionSpinner.setAdapter(arr_adapter);
                    deleteMarkerSpinner.setAdapter(arr_adapter);
                    Toast.makeText(context, "获取到点位" + mapMarker.size() + "个\n" + mapMarker.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void robotMarkersListResultError() {
            super.robotMarkersListResultError();
            // 发送获取点位列表指令失败，可尝试再次发送
            RobotConnectAction.init(context).setConnectCallback(robotConnectCallBack).requestMarkersList();
        }

        @Override
        public void robotNotificationResult(HotelNotification info) {
            super.robotNotificationResult(info);

            Log.d(TAG, "接收到通知" + info.code);

            switch (info.code) {
                case Notification.MOVE_START:
//                    Log.d(TAG, "开始向点位" + info.data.target + "移动");
                    break;

                case Notification.MOVE_FAILED:
//                    Log.d(TAG, "点位" + info.data.target + "移动失败");
                    break;

                case Notification.MOVE_FINISHED:
//                    Log.d(TAG, "点位" + info.data.target + "移动成功");
                    break;

                case Notification.CRUISE_STARTED:
                    Log.d("cruise started", "cruise started");
//                    SpeakAction.getInstance().speak(context, "我要开始巡游了");
//                    Log.d(TAG, "开始巡游 \n巡游点位为："
//                            + info.data.markers
//                            + "\n巡游圈数为: " + info.data.count
//                            + "\n点位到达距离容差：" + info.data.distance_tolerance);
                    break;

                case Notification.CRUISE_FINISHED:
                    Log.d("cruise finished", "cruise finished");
//                    SpeakAction.getInstance().speak(context, "巡游任务结束");
//                    Log.d(TAG, "巡游结束");
                    break;

                case Notification.CRUISE_FAILED:
//                    SpeakAction.getInstance().speak(context, "巡游任务失败，请将我推回充电桩");
//                    Log.d(TAG, "巡游失败");
                    break;

                case Notification.CRUISE_CANCELED:
//                    SpeakAction.getInstance().speak(context, "巡游任务已取消");
//                    Log.d(TAG, "巡游任务取消");
                    break;
            }
        }

        @Override
        public void robotMarkersCount(final int count) {
            super.robotMarkersCount(count);
            toast("获取到点位" + count + "个");
        }

        @Override
        public void robotMarkersBriefReponseResult(final HashMap<String, Marker> mapMarker) {
            super.robotMarkersBriefReponseResult(mapMarker);
            toast("获取到点位" + mapMarker.size() + "个" + "\n" + mapMarker.toString());
        }

        @Override
        public void robotCheckResult(final DiagnosisResultReponse reponse) {
            super.robotCheckResult(reponse);
            toast("自检结果：" + reponse.toString());
        }

        @Override
        public void robotGetWifiDetailList(final RobotWifiDetailResponse response) {
            super.robotGetWifiDetailList(response);
            toast("WIFI详情列表：" + response.toString());
        }

        @Override
        public void robotPowerSatus(final RobotPowerStatusResponse response) {
            super.robotPowerSatus(response);
            toast("当前电池状态：" + response.results.toString());
            if(response.results.battery_capacity<10 && !response.results.charger_connected_notice){
                backToCharge();
            }
        }

        @Override
        public void robotVelocityResult(final RobotVelocityReponse data) {
            super.robotVelocityResult(data);
            toast("实时移动速度：" + data.toString());
        }
    };

    private void toast(final String text) {
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDialog(String title, String str) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(title);
        builder.setMessage(str);
        builder.setNegativeButton("确定", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private ListView areaCheckListView;

    class CheckBoxClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            final List<Marker> markerList = PositionInfo.init(MainActivity.this).getMarkerList();
            if (markerList == null || markerList.size() == 0)
                return;

            markersCheck = new boolean[markerList.size()];
            String[] markersName = new String[markerList.size()];
            for (int i = 0; i < markerList.size(); i++) {
                markersName[i] = markerList.get(i).marker_name;
                markersCheck[i] = false;
            }

            android.app.AlertDialog ad = new android.app.AlertDialog.Builder(MainActivity.this)
                    .setTitle("选择需要巡游的点位")
                    .setMultiChoiceItems(markersName, markersCheck, new DialogInterface.OnMultiChoiceClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton, boolean isChecked) {
                        }
                    }).setPositiveButton("开始巡游", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            ArrayList<Marker> list = new ArrayList<>();
                            String s = "巡游的点位为：";
                            for (int i = 0; i < markersCheck.length; i++) {
                                if (areaCheckListView.getCheckedItemPositions().get(i)) {
                                    s += areaCheckListView.getAdapter().getItem(i) + " ";
                                    list.add(markerList.get(i));
                                } else {
                                    areaCheckListView.getCheckedItemPositions().get(i, false);
                                }
                            }
                            if (list.size() > 0) {
                                Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
                                RobotConnectAction.init(MainActivity.this).setConnectCallback(robotConnectCallBack)
                                        .sendMoveMarkers(list, 0.5f, 1);
                            }
                            dialog.dismiss();
                        }
                    }).setNegativeButton("取消", null).create();
            areaCheckListView = ad.getListView();
            ad.show();
        }
    }

    protected CountDownTimer checkBatteryCountdown = new CountDownTimer(50000, 50000)
    {

        @Override
        public void onTick(long millisUntilFinished)
        {
            // TODO Auto-generated method stub

        }

        @Override
        public void onFinish()
        {
            RobotConnectAction.init(context).setConnectCallback(robotConnectCallBack).getPowerStatus();
            checkBatteryCountdown.start();
        }
    };

    @Override
    protected void onDestroy() {
        unbindService(connection);
        if (faceDetectAction != null) {
            faceDetectAction.release();
        }

        if(speechRecognizer != null){
            speechRecognizer.destroy();
            speechRecognizer = null;
        }
        tts = null;
        SDKProp.destroy(getApplicationContext());

        super.onDestroy();
    }

}

