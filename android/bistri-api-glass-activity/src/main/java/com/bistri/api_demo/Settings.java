package com.bistri.api_demo;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Settings
        extends Activity
        implements View.OnClickListener{

    EditText room;
    Button ok;

    SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings);
        room = (EditText)findViewById( R.id.room_name );
        ok = (Button)findViewById( R.id.ok );

        settings = PreferenceManager.getDefaultSharedPreferences(this);
        room.setText( settings.getString( getString( R.string.api_room_key ), getString( R.string.api_room_value ) ) );
    }

    void validate() {
        settings.edit()
                .putString(getString(R.string.api_room_key), room.getText().toString())
                .commit();
        finish();
    }

    private final Handler mHandler = new Handler();
    protected void post(Runnable runnable) {
        mHandler.post(runnable);
    }

    @Override
    public void onClick(View view) {
        switch( view.getId() ) {
            case R.id.ok:
                validate();
                break;
        }
    }
}
