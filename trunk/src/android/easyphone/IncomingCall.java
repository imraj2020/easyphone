package android.easyphone;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;

public class IncomingCall extends Activity{
	private final String TAG = "IncomingCall";

	private String mTitle;
    private String[] mOptions = new String[2];
    private Timer mTimer = null;
    private int mCurrentOption = -1;
    private int mCurrentCycle = 1;
    private final int NCYCLES = 2;
    
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.v(easyphone.EASYPHONE_TAG, "IncomingCall.onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.incomingcall);
        
        getApplicationContext().registerReceiver(receiver, new IntentFilter("android.easyphone.CLOSE_INCOMINGCALL_ACTIVITY"));
        
      //Get UI Elements
        ((TextView)this.findViewById(R.id.TextView01)).setText("Chamada de, " + easyphone.callControl.getIncomingNumber());
        mTitle = (String) ((TextView)this.findViewById(R.id.TextView01)).getText();
        mOptions[0] = (String) ((TextView)this.findViewById(R.id.TextView02)).getText();
        mOptions[1] = (String) ((TextView)this.findViewById(R.id.TextView03)).getText();
        
        mTimer = new Timer();
    }
    
    @Override
    public void onStart()
    {
    	Log.v(easyphone.EASYPHONE_TAG, "IncomingCall.onStart()");
    	super.onStart();
    	
    	if(easyphone.mTTS == null) return;
    	
    	//READ TITLE
    	//readTitle();
    	
    	//READ OPTIONS
    	//scanOptions();
    }
    
    private void readTitle()
    {
    	Log.v(easyphone.EASYPHONE_TAG, "IncomingCall.readTitle() " + mTitle);
    	
    	easyphone.mTTS.speak(mTitle, TextToSpeech.QUEUE_FLUSH, null);
    }
    
    private void scanOptions()
    {
    	Log.v(TAG, "IncomingCall.scanOptions()");
    	if(mOptions.length == 0) return;
		mTimer.scheduleAtFixedRate(new TimerTask() {
					@Override
					public void run() {
						//next option
						mCurrentOption++;
						if(mCurrentOption > 0 && mCurrentOption % mOptions.length == 0)
						{
							mCurrentOption = 0;
							mCurrentCycle++;
						}
						
						//read current options
						easyphone.mTTS.speak(mOptions[mCurrentOption], TextToSpeech.QUEUE_FLUSH, null);
						
						//if last option and cycle stop timer
						if(mCurrentCycle == NCYCLES && mCurrentOption == mOptions.length - 1)
						{
							stopScanning();
						}
					}
				}, 2500, 5000);
    }
    
    private void stopScanning()
    {
    	Log.v(easyphone.EASYPHONE_TAG, "IncomingCall.stopScanning()");
    	mTimer.cancel();
		mCurrentOption = -1;
		mCurrentCycle = 1;
		mTimer = new Timer();
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event)
    { 
    	Log.v(easyphone.EASYPHONE_TAG, "IncomingCall.onTouchEvent()");
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
              {  // finger up event, Select current option
            	  int option = mCurrentOption;
            	  
            	  if(option >= 0)
            	  {
            		  //is scanning, thus select option
            		  stopScanning();
            		  selectOption(option);
            	  }
            	  else
            	  {
            		//is not scanning, thus start scanning
            			
            	    //READ OPTIONS
            		easyphone.callControl.silenceRinger();
            		readTitle();
            	    scanOptions();
            	  }
            	  
                  break;
              }
            }
    	return true;
    }
    
    private void selectOption(int option)
    {
    	Log.v(easyphone.EASYPHONE_TAG, "IncomingCall.selectOption()");
    	switch(option)
    	{
	    	case 0:  
	    	{
	    		stopScanning();
	    		//Accept call
	    		easyphone.callControl.answerCall();
	    		this.finish();
	    		break;
	    	}
	    	
	    	case 1:
	    	{
	    		stopScanning();
	    		//Reject call
	    		easyphone.callControl.cancelCall();
	    		break;
	    	}
    	}
    }
    
    /* USED BY CALLCONTROL */
    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
        	Log.v(easyphone.EASYPHONE_TAG, "IncomingCall.onReceive()");
        	Log.v(TAG, "Action received: " + intent.getAction());
        	if(intent.getAction().equals("android.easyphone.CLOSE_INCOMINGCALL_ACTIVITY"))
        	{
        		getApplicationContext().unregisterReceiver(receiver);
        		IncomingCall.this.finish();
        	}
        }
    };
    
    
}
