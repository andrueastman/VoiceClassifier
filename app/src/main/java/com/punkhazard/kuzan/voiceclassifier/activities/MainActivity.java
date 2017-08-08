package com.punkhazard.kuzan.voiceclassifier.activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.punkhazard.kuzan.voiceclassifier.helpers.AudioRecorder;
import com.punkhazard.kuzan.voiceclassifier.R;
import com.punkhazard.kuzan.voiceclassifier.helpers.NetworkUtils;
import com.punkhazard.kuzan.voiceclassifier.helpers.InfoUtils;

public class MainActivity extends AppCompatActivity {

    AudioRecorder recorder;
    FloatingActionButton fab;
    TextView textView;
    int recordingTime = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        textView = (TextView) findViewById(R.id.mainText);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if(NetworkUtils.isNetworkAvailable(MainActivity.this)){
                    Snackbar.make(view, "Recording Started", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    //textView.setText("Recording ...");
                    fab.setVisibility(View.GONE);

                    recorder =new AudioRecorder(MainActivity.this);
                    recorder.setRecording(true);
                    recorder.start();

                    Handler handler = new Handler();
                    int t=0;
                    handler.postDelayed(new UpdateUI(handler,t),1000);
                    handler.postDelayed(new FinishRecording(),(recordingTime+1)*1000);
                }
                else{
                    NetworkUtils.showNoNetworkDialog(MainActivity.this);
                }


            }
        });

        InfoUtils.showWelcomeDialog(MainActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        textView.setText(R.string.fab_text);
        fab.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            InfoUtils.showInfoDialog(MainActivity.this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class FinishRecording implements Runnable{

        @Override
        public void run() {
            fab.setVisibility(View.INVISIBLE);
            recorder.setRecording(false);
            //Snackbar.make(fab, "Recording Finished", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            textView.setText("Recording Finished");
        }
    }

    private class UpdateUI implements Runnable{
        Handler handler;
        int t;

        public UpdateUI(Handler handler, int t) {
            this.handler=handler;
            this.t=t;
        }

        @Override
        public void run() {

            if(fab.getVisibility()==View.GONE) {
                t++;
                handler.postDelayed(this, 1000);
                textView.setText(t+"");
            }

        }
    }
}