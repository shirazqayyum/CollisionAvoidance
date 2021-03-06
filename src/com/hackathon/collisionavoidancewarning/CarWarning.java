package com.hackathon.collisionavoidancewarning;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

public class CarWarning extends Activity{
	
	Ringtone r;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/* Play sound */
		Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
		r = RingtoneManager.getRingtone(getApplicationContext(), notification);
		r.play();
		
		/* Prepare Activity UI */ 
        setContentView(new SampleView(this));
        
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {	
            	 Intent openMainActivity= new Intent(CarWarning.this, MainActivity.class);
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

            Drawable dr = context.getResources().getDrawable(R.drawable.stop_480);   
            dr.setBounds(0, 0, dr.getIntrinsicWidth(), dr.getIntrinsicHeight());

            Animation an = new TranslateAnimation(50, 50, 50, 200);
            an.setDuration(500);
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
	protected void onStop() {
		super.onStop();
		r.stop();
	}
}
