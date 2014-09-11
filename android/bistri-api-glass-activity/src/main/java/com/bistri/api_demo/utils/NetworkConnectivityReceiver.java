package com.bistri.api_demo.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkConnectivityReceiver  extends BroadcastReceiver{

    public static interface ConnectivityChangeListener {
        void onConnectivityChange( boolean hasNetwork );
    }

    private ConnectivityChangeListener listener;
    private ConnectivityManager connectivityManager;

    public NetworkConnectivityReceiver(Context context){
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public void setListener( ConnectivityChangeListener listener ) {
        this.listener = listener;
    }

    public boolean hasNetwork() {
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (this.listener == null) {
            return;
        }
        String action = intent.getAction();
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)){
            this.listener.onConnectivityChange( hasNetwork() );
        }
    }
}