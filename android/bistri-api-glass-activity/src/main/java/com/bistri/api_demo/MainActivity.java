package com.bistri.api_demo;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bistri.api.Conference;
import com.bistri.api.Conference.*;
import com.bistri.api.DataStream;
import com.bistri.api.MediaStream;
import com.bistri.api.PeerStream;
import com.bistri.api_demo.utils.NetworkConnectivityReceiver;

import java.lang.Override;
import java.util.ArrayList;

public class MainActivity
        extends Activity
        implements NetworkConnectivityReceiver.ConnectivityChangeListener, Conference.Listener, PeerStream.Handler, MediaStream.Handler {

    private static final String TAG = "MainActivity";
    private static final int RESULT_SETTINGS = 1;

    private Conference conference;
    private String room_name;
    private NetworkConnectivityReceiver networkConnectivityReceiver;
    private TextView status;
    private SharedPreferences settings;
    private RelativeLayout call_layout;
    private boolean in_call;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        status = (TextView)findViewById( R.id.status );
        call_layout = (RelativeLayout)findViewById( R.id.call_layout );
	in_call = false;

        init();
    }

    @Override
    public void onDestroy() {
        networkConnectivityReceiver.setListener(null);
        conference.removeListener(this);

        if ( in_call ) {
            conference.leave( room_name );
        }
        if ( conference.getStatus() == Conference.Status.CONNECTED ) {
            conference.disconnect();
        }

        super.onDestroy();
    }

    void init() {
        Log.d(TAG, "init");
        conference = Conference.getInstance( getApplicationContext() );
        conference.setInfo("38077edb", "4f304359baa6d0fd1f9106aaeb116f33");

        conference.setVideoOption( VideoOption.MAX_WIDTH, 320 );
        conference.setVideoOption( VideoOption.MAX_HEIGHT, 240 );
        conference.setVideoOption( VideoOption.MAX_FRAME_RATE, 5 );

        conference.setAudioOption( AudioOption.PREFERRED_CODEC, AudioCodec.ISAC );
        conference.setAudioOption( AudioOption.PREFERRED_CODEC_CLOCKRATE, 16000 );

        networkConnectivityReceiver = new NetworkConnectivityReceiver( this );

        loadSettings();
        conference.addListener( this );
        networkConnectivityReceiver.setListener( this );

        // Force
        statusUpdate(conference.getStatus());
    }

    private void statusUpdate(Conference.Status conf_status) {

        Log.d(TAG, "statusUpdate");
        boolean network = networkConnectivityReceiver.hasNetwork();

        String str = "stats=" + (
                conf_status== Conference.Status.CONNECTED?"CONNECTED":
                        conf_status== Conference.Status.CONNECTING?"CONNECTING":
                                conf_status== Conference.Status.CONNECTING_SENDREQUEST?"CONNECTING_SENDREQUEST":
                                        conf_status== Conference.Status.DISCONNECTED?"DISCONNECTED":
                                                "<unknown>"
        ) + " network==" + network;
        Log.d(TAG, str);


        String status_str = getString(
                ( conf_status == Conference.Status.CONNECTED && network ) ? R.string.connected :
                        ( (conf_status == Conference.Status.CONNECTING || conf_status == Conference.Status.CONNECTING_SENDREQUEST) && network ) ? R.string.connecting :
                                ( !network ) ? R.string.no_network : R.string.disconnected
        );
        Log.d(TAG, "new status : " + status_str);

        status.setText(status_str);

        if ( network ) {

            switch (conf_status) {
                case DISCONNECTED:
                    // Auto reconnect
                    conference.connect();
                    break;
                case CONNECTED:
                    if ( in_call ) {
                        conference.join(room_name);
                    }
                    break;
            }

        }
    }

    private void loadSettings() {

        Log.d(TAG, "loadSettings");

        if ( settings == null ) {
            settings = PreferenceManager.getDefaultSharedPreferences(this);
        }
        room_name = settings.getString( getString(R.string.api_room_key), getString(R.string.api_room_value) );
    }

    @Override
    public void onConnectivityChange( boolean hasNetwork ) {
        Log.d(TAG, "onConnectivityChange");
        if ( hasNetwork ) {
            statusUpdate( conference.getStatus() );
        }
    }

    @Override
    public void onConnectionEvent(Conference.Status state) {
        Log.d(TAG, "onConnectionEvent");
        statusUpdate(state);
    }

    @Override
    public void onError(Conference.ErrorEvent error) {
        if ( error == Conference.ErrorEvent.CONNECTION_ERROR ) {
            Toast.makeText(getApplicationContext(), "Connection error", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRoomJoined(String room_name) {
	in_call = true;
    }

    @Override
    public void onRoomQuitted(String room_name) {
	in_call =false;
    }

    @Override
    public void onNewPeer(PeerStream peerStream) {
//        peerStream.setHandler( this );
    }

    @Override
    public void onRemovedPeer(PeerStream peerStream) {
    }

    @Override
    public void onMediaStream(String peerId, MediaStream mediaStream) {
    }

    @Override
    public void onDataStream(String peer_id, DataStream dataStream) {
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
            openOptionsMenu();
            return true;
        }
        return super.onKeyDown( keyCode, event );

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle item selection.
        switch (item.getItemId()) {
            case R.id.stop:
                finish();
                return true;
            case R.id.settings:
                if ( in_call ) {
                    conference.leave( room_name );
                }

                Intent i = new Intent( this, Settings.class );
                startActivityForResult( i, RESULT_SETTINGS );
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RESULT_SETTINGS:
                loadSettings();
                statusUpdate( conference.getStatus() );
                break;
        }
    }

    @Override
    public void onVideoRatioChange(String peer_id, MediaStream mediaStream, float ratio) {

    }

    @Override
    public void onPresence( String peerId, Presence presence ) {

    }

    @Override
    public void onIncomingRequest(String peerId, String peerName, String room, String event) {

    }

    @Override
    public void onRoomMembers(String roomName, ArrayList<Member> members) {
        
    }
}
