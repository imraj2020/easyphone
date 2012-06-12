package android.easyphone;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.IntentFilter;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class ContactList extends Activity {
	
	private final String TAG = "ContactList";
    private MenuManager mMenu = null;
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.v(easyphone.EASYPHONE_TAG, "ContactList.onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contactlist);
        
        //Get UI Elements
        mMenu = new MenuManager();
        mMenu.setTitle((String) ((TextView)this.findViewById(R.id.TextView01)).getText());
        mMenu.addOption((String) ((TextView)this.findViewById(R.id.TextView02)).getText());
        mMenu.addOption((String) ((TextView)this.findViewById(R.id.TextView03)).getText());
        mMenu.addOption((String) ((TextView)this.findViewById(R.id.TextView04)).getText());
    }
    
    @Override
    public void onStart()
    {
    	Log.v(easyphone.EASYPHONE_TAG, "ContactList.onStart()");
    	super.onStart();
    	
    	mMenu.startScanning(true);
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
        lp.screenBrightness = (float) 10;
        getWindow().setAttributes(lp);
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
            	  int option = mMenu.getCurrentOption();
            	  
            	  if(option >= 0)
            	  {
            		  //is scanning, thus select option
            		  mMenu.stopScanning();
            		  selectOption(option);
            	  }
            	  else if(option == -1 && mMenu.isScanning())
            	  {
            		  mMenu.stopScanning();
            		  selectOption(0);
            	  }
            	  else
            	  {
            		//is not scanning, thus start scanning
            		  mMenu.startScanning(true);
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
	    		easyphone.callControl.makeCall("912939201", getApplicationContext());
	    		break;
	    	}
	    	case 1:
	    	{
	    		easyphone.callControl.makeCall("219205537", getApplicationContext());
	    		break;
	    	}
	    	case 2:
	    	{
	    		this.finish();
	    		break;
	    	}
    	}
    }

}
