package com.hackathon.collisionavoidancewarning;


import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

public class PedWarning extends Activity implements TextToSpeech.OnInitListener {

	private TextToSpeech tts;
	private String toSpeak = "Pedestrian ahead";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		/* Prepare Activity UI */
        setContentView(new SampleView(this));
        tts = new TextToSpeech(this, this);
           
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {	
            	 Intent openMainActivity= new Intent(PedWarning.this, MainActivity.class);
                 openMainActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                 startActivity(openMainActivity);
            }
        }, 4000);     
	}
	
    private static class SampleView extends View {
        private AnimateDrawable mDrawable;

        public SampleView(Context context) {
            super(context);
            setFocusable(true);
            setFocusableInTouchMode(true);
            
            Drawable dr = context.getResources().getDrawable(R.drawable.ped_large);
            dr.setBounds(0, 0, dr.getIntrinsicWidth(), dr.getIntrinsicHeight());
            
            Animation an = new TranslateAnimation(500, 0, 150, 150);
            an.setDuration(2000);
            an.setRepeatCount(-1);
            an.initialize(1, 1, 1, 1);

            mDrawable = new AnimateDrawable(dr, an);
            an.startNow();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawColor(Color.BLACK);
            mDrawable.draw(canvas);
            invalidate();
        }
    }

	@Override
	public void onInit(int status) {
		if (status != TextToSpeech.SUCCESS) {
			Log.d("tts", "TTS engine failed");
		} else {
			Log.d("tts", "PASSED");
			 tts.setLanguage(Locale.US);
			 tts.setPitch((float) 1.1);
			 tts.setSpeechRate((float) .75);
			 tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		 if (tts != null) {
			 tts.stop();
	         tts.shutdown();
	     }
	}
}
