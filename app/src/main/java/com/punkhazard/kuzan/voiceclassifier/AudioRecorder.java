package com.punkhazard.kuzan.voiceclassifier;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;
import android.view.View;

/**
 * Created by Kuzan on 08/08/2017.
 */

public class AudioRecorder extends Thread{
    private boolean isRecording= false;
    AudioRecord audioInput;
    int sampleRate=44100;

    @Override
    public void run(){
        int minSize = AudioRecord.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        audioInput = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT,minSize);


        short[] lin = new short[1024];
        audioInput.startRecording();
        int v=0;
        while(this.isRecording){
            audioInput.read(lin,0,lin.length);
            v++;
            processAudio(lin);
        }
        Log.d("Number of Samples",""+v);
        audioInput.stop();
    }

    public void setRecording(Boolean isRecording){
        this.isRecording=isRecording;
    }

    private void processAudio(short[] lin) {
        Complex [] complex = new Complex[lin.length];
        for (int i=0; i<lin.length;i++){
            double r =(double)lin[i]/32768.0;
            complex[i] = new Complex(r,0);
        }

        Complex[] fft = FFT.fft(complex);
        double peak =0;
        for (int i=0; i<lin.length;i++){
            if(fft[i].abs() > peak){
                peak=fft[i].abs();
            }
        }

        double frequency =(sampleRate*peak)/lin.length;

        Log.d("AVERAGE_FFT",""+frequency);

    }

}
