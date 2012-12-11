package android.easyphone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;

public class InCall extends EasyPhoneActivity {

	private AudioManager mAudioManager = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, "InCall", R.layout.incall, false);
        setContentView(R.layout.incall);
        
        getApplicationContext().registerReceiver(receiver, new IntentFilter("android.easyphone.CLOSE_INCALL_ACTIVITY"));
        
        //Get UI Elements
        mMenu = new MenuManager();
        mMenu.setTitle((String) ((TextView)this.findViewById(R.id.TextView01)).getText());
        mMenu.addOption((String) ((TextView)this.findViewById(R.id.TextView02)).getText());
        
        //Turn Speaker ON
		mAudioManager = (AudioManager)InCall.this.getSystemService(Context.AUDIO_SERVICE);
		
		//Log.v(EASYPHONE_TAG, "isHeadsetOn: " + mAudioManager.isWiredHeadsetOn());
		int mode = mAudioManager.getMode();
    	mAudioManager.setMode(AudioManager.MODE_IN_CALL);
    	if(!mAudioManager.isWiredHeadsetOn()) mAudioManager.setSpeakerphoneOn(true);
    	mAudioManager.setMode(mode);
		//else mAudioManager.setSpeakerphoneOn(false);*/
    }
    
    @Override
    public void onStart()
    {
    	super.onStart();
    	
    	//Turn Speaker ON
    	Log.v(EASYPHONE_TAG, "isHeadsetOn: " + mAudioManager.isWiredHeadsetOn());
    	int mode = mAudioManager.getMode();
    	mAudioManager.setMode(AudioManager.MODE_IN_CALL);
    	if(!mAudioManager.isWiredHeadsetOn()) mAudioManager.setSpeakerphoneOn(true);
    	mAudioManager.setMode(mode);
    	//else mAudioManager.setSpeakerphoneOn(false);*/
    }
    
    @Override
    public void onResume()
    {
    	super.onResume();
    	
    	//Turn Speaker ON
    	Log.v(EASYPHONE_TAG, "isHeadsetOn: " + mAudioManager.isWiredHeadsetOn());
    	int mode = mAudioManager.getMode();
    	mAudioManager.setMode(AudioManager.MODE_IN_CALL);
    	if(!mAudioManager.isWiredHeadsetOn()) mAudioManager.setSpeakerphoneOn(true);
    	mAudioManager.setMode(mode);
    	/*else mAudioManager.setSpeakerphoneOn(false);*/
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
              {  
            	// check if it is a bounce
            	  if(!Utils.isEventValid(event)) break;
            	  
            	  // finger up event, Select current option
            	  int option = mMenu.getCurrentOption();
            	  
            	  if(option >= 0)
            	  {
            		  //is scanning, thus select option
            		  mMenu.stopScanning();
            		  selectOption(option);
            	  }
            	  else if(option == -1 && mMenu.isScanning())
            	  { // still reading title, thus selection option 1
            		  mMenu.stopScanning();
            		  selectOption(0);
            	  }
            	  else
            	  {
            		//is not scanning, thus start scanning
            		  mMenu.startScanning(false);
            	  }
            	  
                  break;
              }
            }
    	return true;
    }
    
   /* @Override
    public void onBackPressed() 
    {
    	Log.v(easyphone.EASYPHONE_TAG, "InCall.onBackPressed()");
    	// finger up event, END CALL
    	selectOption(0);
    }*/
    
    @Override
    protected void selectOption(int option)
    {
    	super.selectOption(option);
    	switch(option)
    	{
	    	case 0:  
	    	{
	    		//End Call
	    		easyphone.callControl.cancelCall();
	    		this.finish();
	    		break;
	    	}
    	}
    }
    
    @Override 
    public void finish()
    {
    	//Turn Speaker OFF
    	mAudioManager.setMode(AudioManager.MODE_IN_CALL);
    	mAudioManager.setSpeakerphoneOn(false);
    	super.finish();
    }
    
    /* USED BY CALLCONTROL */
    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
        	Log.v(easyphone.EASYPHONE_TAG, "InCall.onReceive()");
        	if(intent.getAction().equals("android.easyphone.CLOSE_INCALL_ACTIVITY"))
        	{
        		getApplicationContext().unregisterReceiver(receiver);
        		InCall.this.finish();
        	}
        }
    };

}