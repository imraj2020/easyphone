package android.easyphone;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class InCall extends Activity {
	private final String TAG = this.getClass().getSimpleName();
	private MenuManager mMenu = null; 
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.v(easyphone.EASYPHONE_TAG, "InCall.onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.incall);
        
        getApplicationContext().registerReceiver(receiver, new IntentFilter("android.easyphone.CLOSE_INCALL_ACTIVITY"));
        
        //Get UI Elements
        mMenu = new MenuManager(100, 5000);
        mMenu.setTitle((String) ((TextView)this.findViewById(R.id.TextView01)).getText());
        mMenu.addOption((String) ((TextView)this.findViewById(R.id.TextView02)).getText());
        
        AudioManager am = (AudioManager)getApplicationContext().getSystemService(getApplicationContext().AUDIO_SERVICE);
        am.setSpeakerphoneOn(true);
    }
    
    @Override
    public void onStart()
    {
    	Log.v(easyphone.EASYPHONE_TAG, "ContactList.onStart()");
    	super.onStart();
    }
    
    @Override
    public void onStop()
    {
    	Log.v(easyphone.EASYPHONE_TAG, "ContactList.onStop()");
    	mMenu.stopScanning();
    	super.onStop();
    }
    
    @Override
    public void onResume()
    {
    	super.onResume();
    	
    	easyphone.mTTS.playEarcon("click", TextToSpeech.QUEUE_ADD, null);
		//Screen Brightness
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = (float) 0;
        getWindow().setAttributes(lp);
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event)
    { 
    	Log.v(easyphone.EASYPHONE_TAG, "InCall.onTouchEvent()");
    	int eventaction = event.getAction(); 
        switch (eventaction ) { 
              case MotionEvent.ACTION_DOWN:
              { // touch on the screen event
            	  break;
              }
              case MotionEvent.ACTION_MOVE:
              { // move event
                 break;
              }
              case MotionEvent.ACTION_UP:
              {  // finger up event, END CALL
            	  selectOption(0);

                  break;
              }
            }
    	return true;
    }
    
    private void selectOption(int option)
    {
    	Log.v(easyphone.EASYPHONE_TAG, "InCall.selectOption()");
    	switch(option)
    	{
	    	case 0:  
	    	{
	    		//End Call
	    		easyphone.mTTS.playEarcon("back", TextToSpeech.QUEUE_FLUSH, null);
	    		easyphone.callControl.cancelCall();
	    		this.finish();
	    		break;
	    	}
    	}
    }
    
    @Override 
    public void finish()
    {
    	AudioManager am = (AudioManager)getApplicationContext().getSystemService(getApplicationContext().AUDIO_SERVICE);
        am.setSpeakerphoneOn(false);
    	super.finish();
    }
    
    /* USED BY CALLCONTROL */
    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
        	Log.v(easyphone.EASYPHONE_TAG, "InCall.onReceive()");
        	Log.v(TAG, "Action received: " + intent.getAction());
        	if(intent.getAction().equals("android.easyphone.CLOSE_INCALL_ACTIVITY"))
        	{
        		getApplicationContext().unregisterReceiver(receiver);
        		InCall.this.finish();
        	}
        }
    };

}