package top.longsh1z.www.mycat.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.Gravity;
import android.widget.Toast;

import top.longsh1z.www.mycat.ui.MainActivity;

public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()){
//            Toast toast = Toast.makeText(context, "网络恢复咯！", Toast.LENGTH_SHORT);
//            toast.setGravity(Gravity.CENTER, 0, 0);
//            toast.show();
        }else {
            Toast toast = Toast.makeText(context, "网络开了点小差！", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }
}
