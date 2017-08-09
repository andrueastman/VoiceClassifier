package com.punkhazard.kuzan.voiceclassifier.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.punkhazard.kuzan.voiceclassifier.helpers.AudioRecorder;
import com.punkhazard.kuzan.voiceclassifier.R;
import com.punkhazard.kuzan.voiceclassifier.helpers.InfoUtils;
import com.punkhazard.kuzan.voiceclassifier.helpers.NetworkUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String value = extras.getString("GENDER");
            TextView textView=(TextView)findViewById(R.id.resultText);
            textView.setText("Seems like that voice belongs to a "+value);
            //The key argument here must match that used in the other activity
        }

        GraphView graph = (GraphView) findViewById(R.id.graph);
        DataPoint dataPoints[] = new DataPoint[AudioRecorder.actualParams.size()];
        for(int i=0;i<AudioRecorder.actualParams.size();i++){
            double x= Double.parseDouble(AudioRecorder.actualParams.get(""+i));
            dataPoints[i]=new DataPoint(i,x);
        }
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dataPoints);
        graph.setTitle("Voice Pitch over Time");
        graph.addSeries(series);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        final TextView textView = (TextView) findViewById(R.id.questionText);
        fab.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(final View view) {
                if(NetworkUtils.isNetworkAvailable(ResultActivity.this)){
                    postWrongResult();
                    fab.setVisibility(View.GONE);
                    textView.setVisibility(View.GONE);
                }
                else{
                    NetworkUtils.showNoNetworkDialog(ResultActivity.this);
                }


            }
        });

    }

    private void postWrongResult() {
        String id = getIntent().getExtras().getString("RECORD");
        String url = "http://188.226.203.244/backend/api/v1.0/feedback/"+id;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Do something with the response
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                    }
                });

        //request_json.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));

        Volley.newRequestQueue(ResultActivity.this).add(stringRequest);

    }
}
