package com.punkhazard.kuzan.voiceclassifier.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.punkhazard.kuzan.voiceclassifier.helpers.AudioRecorder;
import com.punkhazard.kuzan.voiceclassifier.R;

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
    }
}
