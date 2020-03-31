package launcher.hotelrobot.com.testsdk;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

import com.hotelrobot.common.logcat.HeartbeatService;
import com.hotelrobot.common.logcat.entity.HeartbeatBean;
import com.robot.performlib.action.SDKProp;
import com.robot.performlib.action.SpeakAction;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by xushiyun on 17/5/27.
 */

public class MyApp extends Application {
    private String TAG = "MyApp";

    private static MainActivity mActivity;

    public static boolean isWakeUp = false;

    @Override
    public void onCreate() {
        super.onCreate();

        SDKProp.aiuiInit(this, new HeartbeatService.HeartDataListener() {
            @Override
            public void heartData(final HeartbeatBean heartbeatBean) {
                //将时间戳替换成日期
                String s = heartbeatBean.toString();
                int startIdx = s.indexOf(" ts=") + 4;
                int endIdx = s.indexOf(",", startIdx);
                String substring = s.substring(startIdx, endIdx);
                final String replace;
                if (!TextUtils.equals("0", substring)) {
                    String dateToString = "\'" + getDateToString(Long.valueOf(substring)) + "\'";
                    replace = s.replace(substring, dateToString);
                } else {
                    replace = s;
                }

                //每秒显示一次机器人当前状态
                if (mActivity != null) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mActivity.tv_heart.setText(replace);
                        }
                    });
                }
            }

            @Override
            public void softEStopStateChange(boolean b) {
                Log.d(TAG, "软急停状态切换为:" + b);
                SpeakAction.getInstance().speak(mActivity, b ? "软急停开启" : "软急停关闭");
            }

            @Override
            public void hardStopStateChange(boolean b) {
                Log.d(TAG, "硬急停状态为:" + b);
                SpeakAction.getInstance().speak(mActivity, b ? "急停已开启" : "急停已关闭");
            }
        });

    }

    public static void setActivity(MainActivity activity) {
        mActivity = activity;
    }

    public static MainActivity getMainActivity() {
        return mActivity;
    }


    private SimpleDateFormat sf = new SimpleDateFormat("yyyy年MM月dd日HH:mm:ss");

    public String getDateToString(long time) {
        Date d = new Date(time);
        return sf.format(d);
    }

}