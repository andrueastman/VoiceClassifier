package com.punkhazard.kuzan.voiceclassifier.helpers;

import android.content.Context;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.punkhazard.kuzan.voiceclassifier.activities.ResultActivity;
import com.punkhazard.kuzan.voiceclassifier.utils.Complex;
import com.punkhazard.kuzan.voiceclassifier.utils.FFT;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
/**
 * Created by Kuzan on 08/08/2017.
 */

public class AudioRecorder extends Thread{
    private boolean isRecording= false;
    AudioRecord audioInput;
    int sampleRate=8000;
    private Context context;
    public static HashMap<String, String> params;
    public static HashMap<String, String> actualParams;

    public AudioRecorder(Context context){
        this.context=context;
    }

    @Override
    public void run(){
        int minSize = AudioRecord.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        audioInput = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT,minSize);

        params = new HashMap<String,String>();
        actualParams = new HashMap<String,String>();
        short[] lin = new short[4096];
        audioInput.startRecording();
        while(this.isRecording){
            audioInput.read(lin,0,lin.length);
            processAudio(lin,params,actualParams);
        }
        audioInput.stop();// finished recording

        postData(params);
    }

    public void setRecording(Boolean isRecording){
        this.isRecording=isRecording;
    }

    private void processAudio(short[] lin, HashMap<String, String> params, HashMap<String, String> actualParams) {
        Complex[] complex = new Complex[lin.length];
        for (int i=0; i<lin.length;i++){
            double r =(double)lin[i]/32768.0;
            //double r =(double)lin[i];
            complex[i] = new Complex(r,0);
        }

        Complex[] fft = FFT.fft(complex);
        int index=0;
        double peak =fft[0].abs();
        for (int i=0; i<(lin.length/2);i++){
            if(fft[i].abs() > peak){
                peak=fft[i].abs();
                index=i;
            }

        }

        double frequency =(double) (sampleRate*index)/lin.length;
        if(frequency < 280){
            params.put(""+params.size(),""+frequency);          //filter data to be sent for processing to server
        }
        actualParams.put(""+actualParams.size(),""+frequency);// data to display on graph. No need to filter


        //Log.d("AVERAGE_FFT",""+frequency);

    }

    public void postData(HashMap<String, String> params){
        String url = "http://188.226.203.244/backend/api/v1.0/classifier";

        JsonObjectRequest request_json = new JsonObjectRequest(url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                        //Process os success response
                            String gender = response.getJSONObject("results").getString("gender");
                            Intent goToNextActivity = new Intent(context, ResultActivity.class);
                            goToNextActivity.putExtra("GENDER", gender);
                            context.startActivity(goToNextActivity);
                            Log.d("RESPONSE",response.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
                InfoUtils.showServerDownDialog(context);
            }
        });

        //request_json.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));

        Volley.newRequestQueue(context).add(request_json);

    }

}
