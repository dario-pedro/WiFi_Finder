package com.vrem.wifianalyzer.Launcher;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;


import android.os.Handler;

import com.vrem.wifianalyzer.MainActivity;
import com.vrem.wifianalyzer.R;








/**
 * Created by Dario on 06/08/2015.
 */
public class LauncherActivity extends Activity  {



    private Intent i;
    MediaPlayer launcherSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launcher);



        launcherSound = MediaPlayer.create(this,R.raw.systemstart);
        launcherSound.start();
        final ImageView iv = (ImageView) findViewById(R.id.dario_imageView);
        final TextView tv = (TextView) findViewById(R.id.launcher_text);

        final Animation rotate = AnimationUtils.loadAnimation(getBaseContext(), R.anim.rotate);
        final Animation fade = AnimationUtils.loadAnimation(getBaseContext(), R.anim.abc_fade_out);
/*

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                callWebService();
                return null;
            }
        }.execute();
*/
        iv.startAnimation(rotate);
        i = new Intent(getBaseContext(), MainActivity.class);
        rotate.setAnimationListener(new Animation.AnimationListener(){
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                finish();
                startActivity(i);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}
