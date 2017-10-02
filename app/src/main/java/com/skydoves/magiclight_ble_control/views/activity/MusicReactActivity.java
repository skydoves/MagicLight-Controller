package com.skydoves.magiclight_ble_control.views.activity;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.skydoves.magiclight_ble_control.R;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;

public class MusicReactActivity extends AppCompatActivity {

    private Thread listeningThread;
    private Handler uiThread = new Handler();

    private AudioDispatcher dispatcher;
    private AudioProcessor processor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_react);

        startDispatch();
    }

    private void startDispatch() {
        dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050, 1024, 0);
        PitchDetectionHandler pdh = (PitchDetectionResult result, AudioEvent audioEven) -> {
            uiThread.post(() -> {
                final float pitchInHz = result.getPitch();
                int pitch =  pitchInHz > 0 ? (int) pitchInHz : 1;
                Log.e("Test", pitch + "");
            });
        };

        processor = new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, 22050, 1024, pdh);
        dispatcher.addAudioProcessor(processor);
        listeningThread = new Thread(dispatcher);
        listeningThread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dispatcher.removeAudioProcessor(processor);
        listeningThread.interrupt();
    }
}
