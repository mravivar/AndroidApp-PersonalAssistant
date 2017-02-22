package mcgroup10.com.batroid;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by mayankkhullar on 11/12/16.
 */

public class WeatherUpdates extends AsyncTask<String, Void, String> {

    private static String BASE_URL = "http://api.openweathermap.org/data/2.5/weather?appid=dc9817ae766f0fca56bdc5c24b4ad927&zip=";
    private static String IMG_URL = "http://openweathermap.org/img/w/";
    String temp = null;
    String desc = null;
    float sunset;
    float sunrise;
    TextView tv;
    TextView cond;
    ImageView iv;
    private Context mContext;

    public WeatherUpdates() {

    }

    public WeatherUpdates(Context context, TextView temp, ImageView condIcon, TextView cond) {
        this.mContext = context;
        this.tv = temp;
        this.iv = condIcon;
        this.cond = cond;
    }

    //Weather Updates
    private static JSONObject getObject(String tagName, JSONObject jObj) throws JSONException {
        JSONObject subObj = jObj.getJSONObject(tagName);
        return subObj;
    }

    public String getWeatherData(String location) {
        HttpURLConnection con = null;
        InputStream is = null;

        try {
            con = (HttpURLConnection) (new URL(BASE_URL + location)).openConnection();
            con.setRequestMethod("POST");
            con.setReadTimeout(10000); // millis
            con.setConnectTimeout(15000); // millis
            con.setDoInput(true);
            con.setDoOutput(true);
            con.connect();

            // Let's read the response
            StringBuffer buffer = new StringBuffer();
            is = con.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line = null;
            while ((line = br.readLine()) != null)
                buffer.append(line + "\r\n");
            is.close();
            con.disconnect();
            return buffer.toString();
        } catch (Exception e) {
            Log.d("Error", e.toString());
        } finally {
            try {
                is.close();
            } catch (Throwable t) {
            }
            try {
                con.disconnect();
            } catch (Throwable t) {
            }
        }

        return null;
    }

    public byte[] getImage(String code) {
        HttpURLConnection con = null;
        InputStream is = null;
        try {
            con = (HttpURLConnection) (new URL(IMG_URL + code)).openConnection();
            con.setRequestMethod("POST");
            con.setReadTimeout(10000); // millis
            con.setConnectTimeout(15000); // millis
            con.setDoInput(true);
            con.setDoOutput(true);
            con.connect();

            // Let's read the response
            is = con.getInputStream();
            byte[] buffer = new byte[1024];
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            while (is.read(buffer) != -1)
                baos.write(buffer);

            return baos.toByteArray();
        } catch (Exception e) {
            Log.d("Error", e.toString());
        } finally {
            try {
                is.close();
            } catch (Throwable t) {
            }
            try {
                con.disconnect();
            } catch (Throwable t) {
            }
        }

        return null;

    }

    public void updateWeather(String data) {

        JSONObject jObj = null;
        try {
            jObj = new JSONObject(data);
            JSONObject mainObj = getObject("main", jObj);
            temp = mainObj.getString("temp");
            JSONArray weatherArr = jObj.getJSONArray("weather");
            JSONObject JSONWeather = weatherArr.getJSONObject(0);
            desc = JSONWeather.getString("description");
            JSONObject sunObj = getObject("sys", jObj);
            sunrise = sunObj.getLong("sunrise");
            sunset = sunObj.getLong("sunset");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected String doInBackground(String... zip) {
        updateWeather(getWeatherData(zip[0]));
        return null;
    }

    protected void onPostExecute(String data) {
        // TODO: check this.exception
        int cel = (int) (Double.parseDouble(temp) - 272.15);
        temp = "" + cel + " " + (char) 0x00B0 + "C";
        tv.setText(temp);
        Long tsLong = System.currentTimeMillis() / 1000;
        String ts = tsLong.toString();
        float fTs = Float.parseFloat(ts);
        int ID = R.drawable.clear_day;
        if(fTs<sunrise || fTs>sunset){
            ID = R.drawable.clear_night;
       
        }

        if (desc.contains("rain"))
            ID=R.drawable.rain;
        if (desc.contains("thunderstorm"))
            ID = R.drawable.thunderstorm;
        if (desc.contains("snow"))
            ID = R.drawable.snow;

        Drawable myDrawable = mContext.getDrawable(ID);
        iv.setImageDrawable(myDrawable);
        cond.setText(desc);
        if(desc.contains("rain") || desc.contains("thunderstorm") || desc.contains("snow"))
            sendNotification(desc);
    }

    public void sendNotification(String desc){
        int ID = R.drawable.rain;

        if (desc.contains("thunderstorm"))
            ID = R.drawable.thunderstorm;
        if (desc.contains("snow"))
            ID = R.drawable.snow;

        NotificationManager notificationManager =
                (NotificationManager) mContext.getSystemService(Service.NOTIFICATION_SERVICE);
        Notification notify=new Notification.Builder
                (mContext).setContentTitle(desc).setContentText("Get ready for some fun").
                setContentTitle(desc).setSmallIcon(ID).build();

        notify.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(0, notify);
    }

}