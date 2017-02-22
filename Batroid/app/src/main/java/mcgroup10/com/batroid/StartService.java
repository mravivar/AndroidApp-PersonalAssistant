package mcgroup10.com.batroid;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class StartService extends Service {

    public StartService() {
    }

    Context mContext;
    public StartService(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Intent i = new Intent();
        i.setClass(this, DoNotDisturb.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}