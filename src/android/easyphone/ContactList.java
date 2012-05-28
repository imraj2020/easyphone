package android.easyphone;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.IntentFilter;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;

public class ContactList extends Activity {
	
	private final String TAG = "ContactList";
	
	private String mTitle;
    private String[] mOptions = new String[2];
    private Timer mTimer = null;
    private int mCurrentOption = -1;
    private int mCurrentCycle = 1;
    private final int NCYCLES = 2; 
    
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.v(easyphone.EASYPHONE_TAG, "ContactList.onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contactlist);
        
        //Get UI Elements
        mTitle = (String) ((TextView)this.findViewById(R.id.TextView01)).getText();
        mOptions[0] = (String) ((TextView)this.findViewById(R.id.TextView02)).getText();
        mOptions[1] = (String) ((TextView)this.findViewById(R.id.TextView03)).getText();
        
        mTimer = new Timer();
    }
    
    @Override
    public void onStart()
    {
    	Log.v(easyphone.EASYPHONE_TAG, "ContactList.onStart()");
    	super.onStart();
    	
    	if(easyphone.mTTS == null) return;
    	
    	//READ TITLE
    	readTitle();
    	
    	//READ OPTIONS
    	scanOptions();
    }
    
    private void readTitle()
    {
    	Log.v(easyphone.EASYPHONE_TAG, "ContactList.readTitle()");
    	easyphone.mTTS.speak(mTitle, TextToSpeech.QUEUE_FLUSH, null);
    }
    
    private void scanOptions()
    {
    	Log.v(easyphone.EASYPHONE_TAG, "ContactList.scanOptions()");
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
						easyphone.mTTS.stop();
						
						//read current options
						easyphone.mTTS.speak(mOptions[mCurrentOption], TextToSpeech.QUEUE_FLUSH, null);
						
						//if last option and cycle stop timer
						if(mCurrentCycle == NCYCLES && mCurrentOption == mOptions.length - 1)
						{
							stopScanning();
						}
					}
				}, 2000, 5000);
    }
    
    private void stopScanning()
    {
    	Log.v(easyphone.EASYPHONE_TAG, "ContactList.stopScanning()");
    	mTimer.cancel();
		mCurrentOption = -1;
		mCurrentCycle = 1;
		mTimer = new Timer();
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event)
    { 
    	Log.v(easyphone.EASYPHONE_TAG, "ContactList.onTouchEvent()");
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
            		//READ TITLE
            	    readTitle();
            	    	
            	    //READ OPTIONS
            	    scanOptions();
            	  }
            	  
                  break;
              }
            }
    	return true;
    }
    
    private void selectOption(int option)
    {
    	Log.v(easyphone.EASYPHONE_TAG, "ContactList.selectOption()");
    	Log.v(TAG, "Contact selected: " + option);
    	switch(option)
    	{
	    	case 0:  
	    	{
	    		easyphone.callControl.makeCall("219205537", getApplicationContext());
	    		break;
	    	}
	    	case 1:
	    	{
	    		easyphone.callControl.makeCall("216014201", getApplicationContext());
	    		break;
	    	}
    	}
    }

}
