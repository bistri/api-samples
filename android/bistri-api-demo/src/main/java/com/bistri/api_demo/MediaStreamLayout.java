package com.bistri.api_demo;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.RelativeLayout;

import com.bistri.api.MediaStream;

import java.util.ArrayList;

public class MediaStreamLayout extends RelativeLayout implements View.OnClickListener,MediaStream.Handler{
    private static final String TAG = "MediaStreamLayout";
    private ArrayList<MediaStream> mediaStreams;
    private boolean process_layout;
    private int last_video_nb = 0;
    int last_orientation = Configuration.ORIENTATION_UNDEFINED;

    public MediaStreamLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    private void init() {
        mediaStreams = new ArrayList<MediaStream>();
    }

    public void removeMediaStream(MediaStream mediaStream) {
        mediaStream.setHandler( null );
        this.removeView( mediaStream.getRender() );
        mediaStreams.remove( mediaStream );

        resizeAllVideo();
    }

    public void removeAllMediaStream() {
        for ( MediaStream mediaStream : mediaStreams ) {
            mediaStream.setHandler( null );
        }
        mediaStreams.clear();
        this.removeAllViews();
    }

    public void addMediaStream(MediaStream mediaStream) {
        if ( !mediaStream.hasVideo() ) {
            Log.w( TAG, "No video for mediaStream of " + mediaStream.getPeerId() );
            return;
        }
        mediaStreams.add( mediaStream );
        View render_view = mediaStream.getRender();
        this.addView( render_view );
        mediaStream.setHandler( this );
        render_view.setOnClickListener(this);

        resizeAllVideo();
    }

    @Override
    public void onVideoRatioChange(String peer_id, MediaStream mediaStream, float ratio) {
        resizeAllVideo();
    }

    @Override
    public void requestLayout() {
        if ( process_layout ) return;
        process_layout = true;

        super.requestLayout();

        process_layout = false;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout( changed, left, top, right, bottom );

        resizeAllVideo();
    }

    public void resize() {
        requestLayout();
    }

    // Check if we need to resize video
    private boolean needResize() {
        int current_orientation = this.getResources().getConfiguration().orientation;
        if ( last_orientation != current_orientation ) {
            return true;
        }
        if ( last_video_nb != mediaStreams.size() ) {
            return true;
        }
        for (MediaStream media : mediaStreams) {
            float ratio = media.getVideoRatio();
            SurfaceView view = media.getRender();
            float current_ratio = (float)view.getWidth() / view.getHeight();
            if ( (int)(ratio *100) != (int)(current_ratio *100) ) {
                return true;
            }
        }
        return false;
    }
    private synchronized void resizeAllVideo() {
        if ( !needResize() ) {
            return;
        }

        int width = this.getWidth();
        int height = this.getHeight();
        int cpt = 0, nbView = mediaStreams.size();

        if (nbView == 0)
            return;

        int nbLine = (int) Math.ceil((double) nbView / 2);
        int nbCol = nbView > 2 ? 2 : nbView;

        int maxViewWidth = width / nbCol;
        int maxViewHeight = height / nbLine;

        for (MediaStream media : mediaStreams) {

            float ratio = media.getVideoRatio();
            SurfaceView view = media.getRender();

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
            view.setLayoutParams(params);
            cpt++;
        }
        last_video_nb = mediaStreams.size();
    }

    @Override
    public void onClick(View view) {

        // Mute audio on render click
        for (MediaStream media : mediaStreams) {
            View render_view = media.getRender();
            if (view == render_view) {
                Log.d(TAG, ( !media.isAudioMute() ? "Mute" : "Unmute" ) + " peer " +media.getPeerId() );
                media.muteAudio(!media.isAudioMute());
                break;
            }
        }
    }
}
