package mcgroup10.com.batroid;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.TextView;

/**
 * Created by murali on 13/11/16.
 */

public class AutoReply extends AppCompatActivity {

    private static final String TAG = "AutoReply";

    StringBuffer sb = new StringBuffer();
    TextView textDetail;

    public static void methodtocall(String contactNumber) {

        Log.i(TAG, "****************************");
        Log.d(TAG, "number: " + contactNumber);
        Log.i(TAG, "****************************");

        String smsMessage = "AutoReply: Thanks for your message. I am in a meeting. I will text you later!";

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(contactNumber, null, smsMessage, null, null);
            Log.i(TAG, "****************************");
            Log.d(TAG, "Autoreply sent to:" + contactNumber);
            Log.i(TAG, "****************************");
            //}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autoreply);
        textDetail = (TextView) findViewById(R.id.textView1);
        startYourWork();
    }

    protected void startYourWork() {
        sb.append("Auto-Reply mode is on!");
        textDetail.setText(sb);
    }
}
