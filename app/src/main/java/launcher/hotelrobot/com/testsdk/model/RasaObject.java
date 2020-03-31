package launcher.hotelrobot.com.testsdk.model;


public class RasaObject{
    String recipient_id;
    String text;
    public void setData(String recipientResults,String textResult){
        recipient_id=recipientResults;
        text=textResult;
    }

    public String getText(){
        return text;
    }
}
