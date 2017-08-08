package com.punkhazard.kuzan.voiceclassifier;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
/**
 * Created by Kuzan on 08/08/2017.
 */

public class AudioRecorder extends Thread{
    private boolean isRecording= false;
    AudioRecord audioInput;
    int sampleRate=44100;
    private Context context;

    public AudioRecorder(Context context){
        this.context=context;
    }

    @Override
    public void run(){
        int minSize = AudioRecord.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        audioInput = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT,minSize);

        HashMap<String, String> params = new HashMap<String,String>();
        short[] lin = new short[1024];
        audioInput.startRecording();
        while(this.isRecording){
            audioInput.read(lin,0,lin.length);
            processAudio(lin,params);
        }
        audioInput.stop();// finished recording

        postData(params);
    }

    public void setRecording(Boolean isRecording){
        this.isRecording=isRecording;
    }

    private void processAudio(short[] lin, HashMap<String, String> params) {
        Complex [] complex = new Complex[lin.length];
        for (int i=0; i<lin.length;i++){
            //double r =(double)lin[i]/32768.0;
            double r =(double)lin[i];
            complex[i] = new Complex(r,0);
        }

        Complex[] fft = FFT.fft(complex);
        double peak =0;
        for (int i=0; i<lin.length;i++){
            if(fft[i].abs() > peak){
                peak=fft[i].abs();
            }
            params.put(""+params.size(),""+fft[i].abs());
        }

        //double frequency =(sampleRate*peak)/lin.length;


        //Log.d("AVERAGE_FFT",""+frequency);

    }

    public void postData(HashMap<String, String> params){
        String url = "http://192.168.2.100:5000/backend/api/v1.0/classifier";

        JsonObjectRequest request_json = new JsonObjectRequest(url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //try {
                        //Process os success response
                        Log.d("RESPONSE",response.toString());
                        //} catch (JSONException e) {
                        // e.printStackTrace();
                        //}
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
            }
        });

        //request_json.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));

        Volley.newRequestQueue(context).add(request_json);

    }

}
