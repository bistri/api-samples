package com.bistri.api_demo;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bistri.api.MediaStream;

import java.util.ArrayList;

public class MediaStreamLayout extends RelativeLayout implements MediaStream.Handler{
    private static final String TAG = "MediaStreamLayout";

    private ArrayList<VideoView> views;

    private int lastWidth = 0;
    private int lastHeight = 0;

    private class VideoView extends FrameLayout implements View.OnClickListener {
        public TextView videoInfo;
        public SurfaceView renderView;
        public MediaStream mediaStream;

        private int muteState;
        private boolean local;

        public VideoView(Context context, MediaStream mediaStream) {
            super(context);
            muteState = 0;
            this.mediaStream = mediaStream;
            local = mediaStream.getPeerId().equals("local");
            renderView = mediaStream.getRender();
            videoInfo = new TextView( context );
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams( FrameLayout.LayoutParams.MATCH_PARENT,  FrameLayout.LayoutParams.MATCH_PARENT );
            renderView.setLayoutParams( params );

            params = new FrameLayout.LayoutParams( FrameLayout.LayoutParams.MATCH_PARENT,  FrameLayout.LayoutParams.MATCH_PARENT);
            videoInfo.setLayoutParams( params );
            videoInfo.setTextColor(Color.RED );
            videoInfo.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14.f);

            this.addView( renderView );
            this.addView( videoInfo );

            setOnClickListener( this );
        }

        public void onRemove() {
            this.removeAllViews();
        }

        @Override
        public void onClick(View view) {
            Log.d(TAG, ( !mediaStream.isAudioMute() ? "Mute" : "Unmute" ) + " peer " + mediaStream.getPeerId() );
            muteState = (muteState+1)>3 ? 0 : muteState+1;

            String info = "";
            switch( muteState ) {
                case 0:
                    mediaStream.muteAudio( false );
                    mediaStream.muteVideo( false );
                    break;
                case 1:
                    info = ( local ? "Microphone" : "Audio" ) + " muted";
                    mediaStream.muteAudio( true );
                    mediaStream.muteVideo(false);
                    break;
                case 2:
                    info = ( local ? "Microphone" : "Audio" ) + " muted";
                    info += "\n" +( local ? "Camera" : "Video" ) + " muted";
                    mediaStream.muteAudio( true );
                    mediaStream.muteVideo( true );
                    break;
                case 3:
                    info = ( local ? "Camera" : "Video" ) + " muted";
                    mediaStream.muteAudio( false );
                    mediaStream.muteVideo( true );
                    break;
            }

            videoInfo.setText( info );
        }
    }

    public MediaStreamLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    private void init() {
        views = new ArrayList<VideoView>();
    }

    public void removeMediaStream(MediaStream mediaStream) {
        mediaStream.setHandler( null );

        for (VideoView vv : views) {
            if ( vv.mediaStream == mediaStream ) {
                this.removeView(vv);
                vv.onRemove();
                views.remove(vv);
                break;
            }
        }

        resizeAllVideo();
    }

    public void removeAllMediaStream() {
        for ( VideoView videoView : views ) {
            videoView.mediaStream.setHandler( null );
            videoView.onRemove();
        }
        views.clear();
        this.removeAllViews();
    }

    public void addMediaStream(MediaStream mediaStream) {
        if ( !mediaStream.hasVideo() ) {
            Log.w( TAG, "No video for mediaStream of " + mediaStream.getPeerId() );
            return;
        }
        VideoView vv = new VideoView( getContext(), mediaStream );
        views.add( vv );

        this.addView( vv );
        mediaStream.setHandler( this );

        resizeAllVideo();
    }

    @Override
    public void onVideoRatioChange(String peer_id, MediaStream mediaStream, float ratio) {
        resizeAllVideo();
    }

    private synchronized void resizeAllVideo() {
        int width = this.getWidth();
        int height = this.getHeight();
        int cpt = 0, nbView = views.size();

        if (nbView == 0)
            return;

        int nbLine = (int) Math.ceil((double) nbView / 2);
        int nbCol = nbView > 2 ? 2 : nbView;

        int maxViewWidth = width / nbCol;
        int maxViewHeight = height / nbLine;

        for (VideoView vv : views) {

            float ratio = vv.mediaStream.getVideoRatio();

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            int viewWidth = maxViewWidth;
            int viewHeight = (int) (maxViewWidth / ratio);
            if (viewHeight > maxViewHeight) {
                viewHeight = maxViewHeight;
                viewWidth = (int) (maxViewHeight * ratio);
            }
            int vertMargin = (viewHeight < maxViewHeight) ? (maxViewHeight - viewHeight) / 2 : 0;
            int horiMargin = (viewWidth < maxViewWidth) ? (maxViewWidth - viewWidth) / 2 : 0;
            int left, right, top, bottom;
            left = right = top = bottom = 0;

            params.width = viewWidth;
            params.height = viewHeight;

            if (cpt < 2) {
                // First line
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                top = vertMargin;
            } else {
                // Second line
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                right = horiMargin;
            }
            if ((cpt % 2) == 0) {
                // First column
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                left = horiMargin;
            } else {
                // Second column
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                right = horiMargin;
            }

            if (cpt == 2 && nbView == 3) {
                left += maxViewWidth / 2;
            }

            params.setMargins(left, top, right, bottom);
            vv.setLayoutParams(params);
            cpt++;
        }

        lastWidth = getWidth();
        lastHeight = getHeight();
    }

    public void onOrientationChange() {
        ViewTreeObserver observer = getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (lastWidth != getWidth() || lastHeight != getHeight()) {
                    if (Build.VERSION.SDK_INT < 16) {
                        getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    } else {
                        getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                    resizeAllVideo();
                }
            }
        });
    }
}
