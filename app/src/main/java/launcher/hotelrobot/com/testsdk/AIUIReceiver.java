package launcher.hotelrobot.com.testsdk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import android.media.AudioManager;

import com.hotelrobot.common.constant.AIUIConstant;
import com.hotelrobot.common.nethub.entity.Marker;
import com.hotelrobot.common.utils.CommandExe;
import com.robot.performlib.action.ChargeAction;
import com.robot.performlib.action.SDKProp;
import com.robot.performlib.action.SpeakAction;
import com.robot.performlib.callback.FragmentMoveCallback;
import com.robot.performlib.callback.MoveCallback;
import com.robot.performlib.constant.PerformLibConstant;
import com.robot.performlib.performs.LeadPerform;
import com.robot.performlib.position.PositionInfo;

import java.util.Map;
import java.util.TreeMap;

public class AIUIReceiver extends BroadcastReceiver {

    private Context context;
    private static final String TAG = "AIUIReceiver";
    private final String shutDown = "shutdown";

    @Override
    public void onReceive(final Context context, Intent intent) {
        if (intent == null)
            return;
        if (TextUtils.equals(intent.getAction(), "YUNFAN_SPEAKING")) {
            String type = "";
            if (intent.hasExtra(AIUIConstant.Key.type))
                type = intent.getStringExtra(AIUIConstant.Key.type);

            final MainActivity mainActivity = MyApp.getMainActivity();

            switch (type) {
                case AIUIConstant.main.speakProgress:
                    String speakTag = intent.getStringExtra(AIUIConstant.Key.speakTag);
                    final int progress = intent.getIntExtra(AIUIConstant.Key.progress, 0);
                    if (mainActivity != null)
                        mainActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                    mainActivity.tv_SPKProgress.setText(progress + "%");
                            }
                        });

                    Log.d("Speaking", "speakTag : " + speakTag + "\n progress:" + progress);
                    break;
                default:
                    break;
            }

        } else if (TextUtils.equals(intent.getAction(), "BROADCAST_TEST")) {
            this.context = context;
            String type = "";
            String content = "";
            String sintent = "";

            if (intent.hasExtra(AIUIConstant.Key.type))
                type = intent.getStringExtra(AIUIConstant.Key.type);

            if (intent.hasExtra(AIUIConstant.Key.content))
                content = intent.getStringExtra(AIUIConstant.Key.content);

            if (intent.hasExtra(AIUIConstant.Key.intent))
                sintent = intent.getStringExtra(AIUIConstant.Key.intent);

            if (TextUtils.isEmpty(type)) {
                return;
            }

            final String sType = type;
            final String sIntent = sintent;
            final String sContent = content;
            final MainActivity mainActivity = MyApp.getMainActivity();

            if (mainActivity != null)
                mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!TextUtils.isEmpty(sType))
                            mainActivity.tv_type.setText("触发技能：" + sType);

                        if (!TextUtils.isEmpty(sIntent))
                            mainActivity.tv_intent.setText("触发意图：" + sIntent);

                        if (!TextUtils.isEmpty(sContent))
                            mainActivity.tv_speak.setText("Content：" + sContent);
                    }
                });

            switch (type) {
                case AIUIConstant.main.sleepTime:
                    if (!TextUtils.isEmpty(content)) {
                        int sleepTime = Integer.valueOf(content) / 1000;
                        SpeakAction.getInstance().speak(context, "当前休眠时间为：" + sleepTime + "秒");
                        Toast.makeText(context, "当前休眠时间为：" + sleepTime + "秒", Toast.LENGTH_LONG).show();
                    }
                    break;
                case AIUIConstant.Type.wakeUp://唤醒
                    MyApp.isWakeUp = true;
//                    SpeakAction.getInstance().speak(context, "您好，很高兴为您服务", "wakeUp");
                    Log.d(TAG, "已唤醒，唤醒角度为:" + Integer.valueOf(content));
                    break;

                case AIUIConstant.Type.Sleep://休眠
                    MyApp.isWakeUp = false;
//                    SpeakAction.getInstance().speak(context, "我要休息了");
                    Log.d(TAG, "收到休眠指令");
                    break;

                case AIUIConstant.Type.Iat://语音识别结果
                    MyApp.isWakeUp = true;
                    Log.d(TAG, "识别结果为：" + content);
                    final String iat = content;
                    if (mainActivity != null && !TextUtils.isEmpty(iat))
                        mainActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mainActivity.tv_listen.setText("听到的文字：" + iat);
                            }
                        });
                    break;

                case AIUIConstant.Type.speakCompleted:
                    String speakTag = "";
                    //speakTag是调用speak方法是传递的标记值，说话完成回调时会把这个值再返回，用以区分
                    if (intent.hasExtra(AIUIConstant.Key.speakTag)) {
                        speakTag = intent.getStringExtra(AIUIConstant.Key.speakTag);

                        //例如：接收到tag为shutdown的语音播报完成，执行屏幕断电动作
                        if (TextUtils.equals(shutDown, speakTag)) {
                            shutdown();
                        }
                    }
                    Log.d(TAG, "speakCompleted, speakTag: " + speakTag);
//                    mainActivity.googleSpeechToText(speechRecognizerIntent);
                    break;
                case AIUIConstant.Type.speakBegin:
                    String speakTag_Begin = "";
                    //speakTag是调用speak方法是传递的标记值，说话开始前回调时会把这个值再返回，用以区分
                    if (intent.hasExtra(AIUIConstant.Key.speakTag)) {
                        speakTag_Begin = intent.getStringExtra(AIUIConstant.Key.speakTag);
                    }
                    Log.d(TAG, "speakBegin, speakTag: " + speakTag_Begin);
                    break;

                case AIUIConstant.Type.musicX://唱歌识别结果
                    if (TextUtils.isEmpty(sintent)) {
                        Log.d(TAG, "歌曲信息: " + content);
                        //content:听下朴树的平凡之路吧
                        //musicX、news、joke、story这四个场景中，sintent为空时，content为音频播放前说的文字
                    } else {
                        switch (sintent) {
                            case AIUIConstant.musicAndNews.err:
                                break;
                            case AIUIConstant.musicAndNews.next:
                                break;
                            case AIUIConstant.musicAndNews.past:
                                break;
                            case AIUIConstant.musicAndNews.pause:
                                break;
                            case AIUIConstant.musicAndNews.play:
                                break;
                            case AIUIConstant.musicAndNews.playCompleted:
                                break;
                            case AIUIConstant.musicAndNews.repeat:
                                break;
                            case AIUIConstant.musicAndNews.replay:
                                break;
                            case AIUIConstant.musicAndNews.volume_max:
                                break;
                            case AIUIConstant.musicAndNews.volume_mid:
                                break;
                            case AIUIConstant.musicAndNews.volume_min:
                                break;
                            case AIUIConstant.musicAndNews.volume_minus:
                                break;
                            case AIUIConstant.musicAndNews.volume_plus:
                                break;
                        }
                    }
                    break;

                case AIUIConstant.Type.news://说个新闻识别结果
                    if (!TextUtils.isEmpty(sintent)) {
                        switch (sintent) {
                            case AIUIConstant.musicAndNews.err:
                                break;
                            case AIUIConstant.musicAndNews.next:
                                break;
                            case AIUIConstant.musicAndNews.close:
                                break;
                            case AIUIConstant.musicAndNews.past:
                                break;
                            case AIUIConstant.musicAndNews.pause:
                                break;
                            case AIUIConstant.musicAndNews.play:
                                break;
                            case AIUIConstant.musicAndNews.playCompleted:
                                break;
                            case AIUIConstant.musicAndNews.repeat:
                                break;
                            case AIUIConstant.musicAndNews.replay:
                                break;
                            case AIUIConstant.musicAndNews.volume_max:
                                break;
                            case AIUIConstant.musicAndNews.volume_mid:
                                break;
                            case AIUIConstant.musicAndNews.volume_min:
                                break;
                            case AIUIConstant.musicAndNews.volume_minus:
                                break;
                            case AIUIConstant.musicAndNews.volume_plus:
                                break;
                        }
                    }
                    break;

                case AIUIConstant.Type.weather://天气识别结果
                    String weatherStr = "";
                    if (intent.hasExtra(AIUIConstant.Key.weatherStr))
                        weatherStr = intent.getStringExtra(AIUIConstant.Key.weatherStr);//weatherStr: 晴、雨、阴、晴转多云等
                    Log.d(TAG, "weather:" + " 天气状况为：" + weatherStr
                            + " \n 天气信息为：" + content);
                    break;

                case AIUIConstant.Type.Speak:
                    //在开始说话前回调说话的内容，只有在调用SDK中speak API才会返回内容
                    Log.d(TAG, "Speak: 通过SpeakAction发送的文字" + content);
                    break;

                case AIUIConstant.Type.joke:
                    //讲笑话aiui.cfg
                    if (!TextUtils.isEmpty(sintent)) {
                        switch (sintent) {
                            case AIUIConstant.joke.play:
                                //开始播放
                                break;
                            case AIUIConstant.joke.playCompleted:
                                //播放完成
                                break;
                        }
                    } else {
                        Log.d(TAG, "播放信息:" + content);
                        //例:content=请听笑话"让座"
                    }
                    break;

                case AIUIConstant.Type.story:
                    if (TextUtils.isEmpty(sintent)) {
                        return;
                    }
                    switch (sintent) {
                        case AIUIConstant.story.play:
                            break;
                        case AIUIConstant.story.playCompleted:
                            break;
                    }
                    break;

                case AIUIConstant.Type.powerOff:
                    //底盘断电关机之前发送的通知，如果没电导致的关机不会发送此通知
                    SDKProp.destroy(context.getApplicationContext());
                    SpeakAction.getInstance().speak(context, "我要关机了", shutDown);
                    break;

                case AIUIConstant.Type.CHARGE_STATUS_ON:
//                    SpeakAction.getInstance().speak(context, "我充上电啦");
                    Log.e(TAG, "onReceive：  未充电转充电");
                    break;

                case AIUIConstant.Type.CHARGE_STATUS_OFF:
//                    SpeakAction.getInstance().speak(context, "我要去工作啦");
                    Log.e(TAG, "onReceive：  充电转未充电");
                    break;

                case AIUIConstant.Type.getScene:
                    Toast.makeText(context, "当前语音场景为:" + content, Toast.LENGTH_SHORT).show();
                    break;

                case AIUIConstant.main.productId:
                    mainActivity.tv_productId.setText("该机器编号为：" + content);
                    break;

                case "IATFailed":
                    SpeakAction.getInstance().speak(context,"你好像没有说话哦");
                    break;

                default:
                    if (type.startsWith(AIUIConstant.Type.main)) {//主场景功能
                        if (TextUtils.isEmpty(sintent)) {
                            return;
                        }
                        switch (sintent) {
                            case AIUIConstant.main.charge:
                                //回充指令
                                if (mainActivity != null)
                                    mainActivity.backToCharge();
                                break;

                            case AIUIConstant.main.leadTo:
                                //引领
                                Log.d(TAG, "将要引领的点位名称是:" + content);
                                Map<String, Marker> markers = PositionInfo.init(context).getMarkers();
                                if (markers != null && markers.size() > 0) {
                                    Marker marker = markers.get(content);
                                    if (marker != null) {
                                        LeadPerform.create(new FragmentMoveCallback() {
                                        }).setMarker(marker).start(context);
                                    } else {
                                        SpeakAction.getInstance().speak(context, "没有找到该点位");
                                    }
                                }
                                break;
                            default:
                                //其他意图请参照"AIUIReceiver接收事件参数列表.xlsx"文件中Service以"YUNJI"开头的技能中的Intent字段添加
                                break;
                        }
                    } else {
                        //其他技能请参照"AIUIReceiver接收事件参数列表.xlsx"文件中Service字段添加
                    }

                    break;
            }

            //获取云帆说的文字
            if (TextUtils.isEmpty(sintent) && !TextUtils.equals(type, AIUIConstant.main.sleepTime)
                    && !TextUtils.equals(type, AIUIConstant.Type.wakeUp)
                    && !TextUtils.equals(type, AIUIConstant.Type.Iat)
                    && !TextUtils.equals(type, AIUIConstant.Type.speakBegin)
                    && !TextUtils.equals(type, AIUIConstant.Type.speakCompleted)
                    && !type.startsWith(AIUIConstant.Type.main)) {

                final String speak = content;

                if (mainActivity != null)
                    mainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                        mainActivity.tv_speak.setText("说的文字：" + speak);
                        }
                    });
            }
        }
    }

    private void shutdown() {

        CommandExe.execCommand("reboot -p", true);
    }
}
