package launcher.hotelrobot.com.testsdk.activities;

import com.hotelrobot.common.Notification;
import com.hotelrobot.common.nethub.entity.HotelNotification;
import com.robot.performlib.action.RobotConnectAction;

import com.robot.performlib.callback.RobotConnectCallBack;

import com.hotelrobot.common.nethub.entity.Marker;

import android.app.Activity;
import android.speech.tts.TextToSpeech;

import java.util.List;

import launcher.hotelrobot.com.testsdk.MainActivity;
import launcher.hotelrobot.com.testsdk.MyApp;

public class CecLabTour {

    private Activity context;
    private Integer markersListSize;
    private Integer currentMarkerPoint;
    private List<Marker> pointsList;

    public void startTour(List<Marker> markerList){
        markersListSize = markerList.size();
        String[] markersName = new String[markerList.size()];
        currentMarkerPoint = 4;
        pointsList = markerList;
        RobotConnectAction.init(context).setConnectCallback(robotConnectCallBack).sendMoveMarker(pointsList.get(currentMarkerPoint));
    }

    public void moveToPoint(int point){
        RobotConnectAction.init(context).setConnectCallback(robotConnectCallBack).sendMoveMarker(pointsList.get(point));
    }

    //底盘数据回调
    private RobotConnectCallBack robotConnectCallBack = new RobotConnectCallBack() {
        final MainActivity mainActivity = MyApp.getMainActivity();
        @Override
        public void robotNotificationResult(HotelNotification info) {
            super.robotNotificationResult(info);

            switch (info.code) {
                case Notification.MOVE_START:
                    break;

                case Notification.MOVE_FAILED:
                    break;

                case Notification.MOVE_FINISHED:
                    if(currentMarkerPoint == 4){
                        currentMarkerPoint = 7;
                        mainActivity.tts.speak("This is our cec lab, we demonstrate our products here", TextToSpeech.QUEUE_ADD, null,"uniqueId");
                        moveToPoint(currentMarkerPoint);
                    } else {
                        currentMarkerPoint++;
                        mainActivity.tts.speak("I am at point " + (currentMarkerPoint -1), TextToSpeech.QUEUE_ADD, null,"lab tour point " + currentMarkerPoint);
                    }
                    break;

                case Notification.CRUISE_STARTED:
                    break;

                case Notification.CRUISE_FINISHED:
//
                    break;

                case Notification.CRUISE_FAILED:
                    break;

                case Notification.CRUISE_CANCELED:
                    break;
            }
        }
    };
}