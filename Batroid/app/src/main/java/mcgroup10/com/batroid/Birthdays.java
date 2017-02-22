package mcgroup10.com.batroid;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by murali on 13/11/16.
 */

public class Birthdays extends AppCompatActivity {

    private static final String TAG = "Birthdays";

    Button sendBtn;
    TextView textDetail;

    ArrayList<String> contactName = new ArrayList<String>();
    ArrayList<String> contactNumber = new ArrayList<String>();
    StringBuffer sb = new StringBuffer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_birthdays);
        textDetail = (TextView) findViewById(R.id.textView1);
        sendBtn = (Button) findViewById(R.id.btnSendSMS);
        startYourWork();
        sendBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                sendSMSMessage();
            }
        });
    }

    private Cursor getContactsBirthdays() {
        Uri uri = ContactsContract.Data.CONTENT_URI;
        String[] projection = new String[]{
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Event.CONTACT_ID,
                ContactsContract.CommonDataKinds.Event.START_DATE
        };
        String where =
                ContactsContract.Data.MIMETYPE + "= ? AND " +
                        ContactsContract.CommonDataKinds.Event.TYPE + "=" +
                        ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY;
        String[] selectionArgs = new String[]{
                ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE
        };
        String sortOrder = null;
        ContentResolver cr = getContentResolver();
        return cr.query(uri, projection, where, selectionArgs, sortOrder);
    }

    protected void startYourWork() {
        Cursor cursor = getContactsBirthdays();
        int bDayColumn = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Event.START_DATE);
        int nameColumn = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
        String number;
        while (cursor.moveToNext()) {
            String bDay = cursor.getString(bDayColumn);
            String name = cursor.getString(nameColumn);

            Date date = new Date();
            String modifiedDate = new SimpleDateFormat("MM-dd").format(date);

            if (bDay.contains(modifiedDate)) {
                number = getPhoneNumber(this, name);
                Log.d(TAG, "Name: " + name);
                Log.d(TAG, "Birthday: " + bDay);
                Log.d(TAG, "Number: " + number);
                Log.i(TAG, "........................");
                contactName.add(name);
                contactNumber.add(number);
            }
        }
        Log.i(TAG, "*************************");
        Log.d(TAG, "contactName: " + contactName);
        Log.d(TAG, "contactNumber: " + contactNumber);
        Log.i(TAG, "*************************");
        sb.append("~~~Today's Birthday~~~ \n");
        for (int i = 0; i < contactName.size(); i++) {
            sb.append("\n" + contactName.get(i) + "\n" + contactNumber.get(i) + "\n");
        }
        textDetail.setText(sb);
    }

    public String getPhoneNumber(Context context, String name) {
        String ret = null;
        String selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " like'%" + name + "%'";
        String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER};
        Cursor c = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection, selection, null, null);
        if (c.moveToFirst()) {
            ret = c.getString(0);
        }
        c.close();
        if (ret == null)
            ret = "Unsaved";
        return ret;
    }

    protected void sendSMSMessage() {
        String smsMessage = "Wish you a very Happy Birthday!";
        try {
            SmsManager smsManager = SmsManager.getDefault();
            for (int i = 0; i < contactNumber.size(); i++) {
                smsManager.sendTextMessage(contactNumber.get(i), null, smsMessage, null, null);
                Toast.makeText(getApplicationContext(), "Birthday SMS sent.", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),
                    "Sending SMS failed.",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }


    }
}
