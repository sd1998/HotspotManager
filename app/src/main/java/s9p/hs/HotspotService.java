package s9p.hs;

import android.app.Service;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class HotspotService extends Service {

    private final String TAG = HotspotService.class.getSimpleName();

    private WifiManager wifiManager;
    private ConnectivityManager connectivityManager;


    @Override
    public void onCreate(){
        Log.d(TAG,"onCreate executing");
        wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);

    }

    @Override
    public int onStartCommand(Intent intent,int flags,int startId){
        return START_REDELIVER_INTENT;
    }

    private void showWritePermissionSettings(boolean force){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (force || !Settings.System.canWrite(getApplicationContext())){
                Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
    }

    private boolean setWifiApEnabled(WifiConfiguration wifiConfiguration,boolean enabled){
        try{
            if(enabled){
                wifiManager.setWifiEnabled(false);
            }
            Method method = wifiManager.getClass().getMethod("setWifiEnabled",WifiConfiguration.class,boolean.class);
            return (Boolean) method.invoke(wifiManager,wifiConfiguration,enabled);
        } catch(Exception exception){
            Log.d(TAG,"Exception: " + exception.getMessage());
            return false;
        }
    }

    private boolean setWifiConfiguration(WifiConfiguration wifiConfiguration){
        try{
            Method method = wifiManager.getClass().getMethod("setWifiApConfiguration",WifiConfiguration.class);
            return (Boolean) method.invoke(wifiManager,wifiConfiguration);
        } catch(Exception exception){
            Log.d(TAG,"Exception: " + exception.getMessage());
            return false;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy(){
        Log.d(TAG,"onDestroy() executing");
    }

}