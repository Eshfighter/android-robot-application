package launcher.hotelrobot.com.testsdk.model;

public class faceDetect {
    String[] faces_detected;
    public void setData(String[] faceResults){
        faces_detected=faceResults;
    }

    public String[] getFaces(){
        return faces_detected;
    }
}
