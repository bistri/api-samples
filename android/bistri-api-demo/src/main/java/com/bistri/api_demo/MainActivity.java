package com.bistri.api_demo;


import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bistri.api.Conference;
import com.bistri.api.Conference.*;
import com.bistri.api.MediaStream;
import com.bistri.api.PeerStream;

import java.util.ArrayList;


public class MainActivity extends Activity
    implements View.OnClickListener, Conference.Listener, PeerStream.Handler {
    // Static Fields
    private static final String TAG = "MainActivity";
    private static final String DEFAULT_ROOM_NAME = "androidroom";

    // Members
    private EditText room_name;
    private Button join_button;
    private TextView status;
    private ImageView loader_spinner;
    private MediaStreamLayout call_layout;
    private LinearLayout room_layout;
    private boolean orientation_landscape;
    private Spinner select_camera;

    private Conference conference;

    /*
    *       Activity Management
    */

    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        super.onCreate( savedInstanceState );

        setContentView(R.layout.demo);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        room_name = ( EditText )findViewById( R.id.room_name );
        join_button = ( Button )findViewById( R.id.join_button );
        status = ( TextView )findViewById( R.id.status );
        loader_spinner = ( ImageView )findViewById( R.id.loader_spinner );
        call_layout = ( MediaStreamLayout )findViewById( R.id.call_layout );
        room_name.setText( DEFAULT_ROOM_NAME );
        room_layout = ( LinearLayout )findViewById( R.id.room_layout );
        orientation_landscape = (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);
        if ( orientation_landscape ) {
            room_layout.setOrientation(LinearLayout.HORIZONTAL);
        } else {
            room_layout.setOrientation(LinearLayout.VERTICAL);
        }
        select_camera = ( Spinner )findViewById( R.id.select_camera );

        // Conference
        conference = Conference.getInstance( getApplicationContext() );
        conference.setInfo( "38077edb", "4f304359baa6d0fd1f9106aaeb116f33" );

        conference.setVideoOption( VideoOption.MAX_WIDTH, 320 );
        conference.setVideoOption( VideoOption.MAX_HEIGHT, 240 );
        conference.setVideoOption( VideoOption.MAX_FRAME_RATE, 15 );

        conference.setAudioOption( AudioOption.PREFERRED_CODEC, AudioCodec.ISAC );
        conference.setAudioOption( AudioOption.PREFERRED_CODEC_CLOCKRATE, 16000 );
        init();
    }

    protected void init() {

        // Force to use only Alphanumeric characters.
        InputFilter filter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                if ( !source.toString().matches("[a-zA-Z0-9_-]*") ) {
                    return "";
                }
                return null;
            }
        };
        room_name.setFilters(new InputFilter[]{filter});
        // Set keyboard action
        room_name.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    join_button.performClick();
                    return true;
                }
                return false;
            }
        });

        // Set button listener
        join_button.setOnClickListener(this);


        if ( conference.getCameraNb() == 0 ) {
            select_camera.setVisibility( View.GONE );
        } else {

            class CameraAdapter extends ArrayAdapter<Camera.CameraInfo> {
                public CameraAdapter(Context context, ArrayList<Camera.CameraInfo> infos) {
                    super(context, android.R.layout.simple_spinner_dropdown_item, infos);
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    if(convertView == null)
                        convertView = View.inflate(getContext(),
                                android.R.layout.simple_spinner_dropdown_item,
                                null);

                    TextView tvText1 = (TextView)convertView.findViewById(android.R.id.text1);
                    Camera.CameraInfo info = getItem( position );
                    tvText1.setText( "Camera "  + position + " " + ( info.facing == Camera.CameraInfo.CAMERA_FACING_BACK ? "(back)" : "(front)" ) );

                    return convertView;
                }

                @Override
                public View getDropDownView(int position, View convertView, ViewGroup parent) {
                    return getView(position, convertView, parent);
                }
            }


            select_camera.setAdapter( new CameraAdapter ( this, conference.getCameraInfos() ) );
            select_camera.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    conference.setCameraId( position );
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {}
            });
            select_camera.setSelection( conference.getCameraId() );
        }
    }

    void showLoaderSpinner( boolean visible )
    {
        int visibility = visible ? View.VISIBLE : View.GONE;

        if ( loader_spinner.getVisibility() == visibility ) return; // Nothing to do

        if ( visible ) {
            // Initialize loading spinner animation
            Animation rotation = AnimationUtils.loadAnimation(this, R.anim.rotate);
            if (rotation != null) loader_spinner.startAnimation(rotation);
        } else {
            loader_spinner.clearAnimation();
        }
        loader_spinner.setVisibility( visibility );
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        conference = Conference.getInstance( getApplicationContext() );
        
        conference.addListener( this );

        onConnectionEvent(conference.getStatus());
    }

    @Override
    protected void onPause()
    {
        Log.d(TAG, "onPause");

        conference.removeListener( this );

        super.onPause();
    }

    @Override
    protected void onDestroy()
    {
        Log.d(TAG, "onDestroy");

        conference.disconnect();
        super.onDestroy();
    }

    private void updateViewAccordingState(Status state) {

        if ( state == Status.DISCONNECTED ) {
            status.setText( R.string.disconnected );
        } else if ( ( state == Status.CONNECTING ) || ( state == Status.CONNECTING_SENDREQUEST ) ) {
            status.setText( R.string.connecting );
        } else if ( state == Status.CONNECTED ) {
            status.setText( R.string.connected );
        }

        boolean showLoader = ( state == Status.CONNECTING ) || ( state == Status.CONNECTING_SENDREQUEST );
        showLoaderSpinner( showLoader );

        room_name.setEnabled( state == Status.CONNECTED );
        join_button.setEnabled(state == Status.CONNECTED);
    }

    private void setInCall( boolean inCall ){
        room_layout.setVisibility( inCall ? View.GONE : View.VISIBLE );
        call_layout.setVisibility( inCall ? View.VISIBLE : View.GONE );
        if ( inCall ) {
            hideKeyboard();
        } else {
            call_layout.removeAllMediaStream();
        }
    }

    @Override
    public void onBackPressed() {
        Log.w(TAG, "onBackPressed");

        if ( conference.isInRoom() ) {
            conference.leave();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onClick( View view )
    {
        Log.d(TAG, "onClick");

        switch( view.getId() ) {
            case R.id.join_button:

                String roomName = room_name.getText().toString();

                if ( roomName == null || roomName.length() == 0 ) {
                    Toast.makeText(this, R.string.create_input_error, Toast.LENGTH_SHORT).show();
                    return;
                }

                if ( conference.getStatus() == Status.CONNECTED ) {
                    conference.join( roomName );
                    setInCall( true );
                    return;
                } else {
                    Log.w( TAG, "Cannot join room : not connected");
                }
                break;
        }
    }

    /*
    *       Listener implementation
    */

    @Override
    public void onConnectionEvent(Status state) {
        updateViewAccordingState(state);
        // Auto reconnect
        if ( state == Status.DISCONNECTED ) {
            conference.connect();
        }
    }

    @Override
    public void onError(ErrorEvent error) {
        if ( error == ErrorEvent.CONNECTION_ERROR ) {
            Toast.makeText(getApplicationContext(), "Connection error", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRoomJoined(String room_name) {
        setInCall( true );
    }

    @Override
    public void onRoomQuitted() {
        setInCall( false );
    }

    @Override
    public void onNewPeer( PeerStream peerStream ) {
        peerStream.setHandler( this );
    }

    @Override
    public void onRemovedPeer(PeerStream peerStream) {
        if ( !peerStream.hasMedia() )
            return;

        MediaStream mediaStream = peerStream.getMedia();
        call_layout.removeMediaStream(mediaStream);
    }

    @Override
    public void onMediaStream(String peerId, MediaStream mediaStream) {
        call_layout.addMediaStream(mediaStream);
    }

    @Override
    public void onPresence(String peerId, Presence presence) {}

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);

        orientation_landscape = (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE);
        room_layout.setOrientation(orientation_landscape ? LinearLayout.HORIZONTAL : LinearLayout.VERTICAL);
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager)getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(room_name.getWindowToken(), 0);
    }

}
