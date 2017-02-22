package mcgroup10.com.batroid;

/**
 * Created by mayankkhullar on 11/22/16.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static mcgroup10.com.batroid.SmsReceiver.inGeoFence;


public class PhoneCallReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(final Context context, Intent intent) {
        TelephonyManager telephony = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        telephony.listen(new PhoneStateListener(){
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                super.onCallStateChanged(state, incomingNumber);
                if(inGeoFence){
                    try {
                        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                        Class<?> c = Class.forName(tm.getClass().getName());
                        Method m = c.getDeclaredMethod("getITelephony");
                        m.setAccessible(true);
                        Object telephonyService = m.invoke(tm);
                        Class<?> telephonyServiceClass = Class.forName(telephonyService.getClass().getName());
                        Method endCallMethod = telephonyServiceClass.getDeclaredMethod("endCall");
                        endCallMethod.invoke(telephonyService);
                        Thread.sleep(4000);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        Log.i("TAG",e.getCause().toString());
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    AutoReply.methodtocall(incomingNumber);

                }
            }
        },PhoneStateListener.LISTEN_CALL_STATE);
    }
}
