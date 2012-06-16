package android.easyphone;

import android.app.Activity;
import android.app.KeyguardManager;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

public class EasyPhoneActivity extends Activity
{
	static public String EASYPHONE_TAG = "EasyPhone";
	protected MenuManager mMenu = null;
	protected String mName = "";
	protected boolean mReadMenu = true;
	
	/** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState, String activityName) 
    {
    	mName = activityName;
    	Log.v(EASYPHONE_TAG, mName + ".onCreate()");
   
    	super.onCreate(savedInstanceState);
   
    	// Initialize Scanning Menu
    	mMenu = new MenuManager();
    }
    
    public void onCreate(Bundle savedInstanceState, String activityName, boolean readMenuOnStart) 
    {
    	mReadMenu = readMenuOnStart;
    	onCreate(savedInstanceState, activityName);
    }
    
    @Override
    public void onStart()
    {
    	Log.v(EASYPHONE_TAG, mName + ".onStart()");
    	super.onStart();
    	
    	if(mReadMenu)	mMenu.startScanning(true);
    }
    
    @Override
    public void onStop()
    {
    	Log.v(EASYPHONE_TAG, mName + ".onStop()");
    	mMenu.stopScanning();
    	super.onStop();
    }
    
    @Override
    public void onResume()
    {
    	Log.v(EASYPHONE_TAG, mName + ".onResume()");
    	super.onResume();
            	
		//Screen Brightness
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = (float) 10;
        getWindow().setAttributes(lp);
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event)
    { 
    	Log.v(EASYPHONE_TAG, mName + ".onTouchEvent()");
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
            	  { // still reading title, thus selection option 1
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
    
    @Override
    public void onBackPressed() 
    {
    	Log.v(EASYPHONE_TAG, mName + ".onBackPressed()");
    	// finger up event, Select current option
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
    }
    
    @Override 
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
    	Log.v(EASYPHONE_TAG, mName + ".onKeyDown()");
    	if(keyCode == KeyEvent.KEYCODE_BACK)
    	{
    		return super.onKeyDown(keyCode, event);
    	}
    	else
    	{
    		return true;
    	}
    }
    
    @Override
    public void onAttachedToWindow() 
    {
    	Log.v(EASYPHONE_TAG, mName + ".onAttachedToWindow()");
    	
    	// Disable Power Button
    	KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Activity.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock lock = keyguardManager.newKeyguardLock(KEYGUARD_SERVICE);
        lock.disableKeyguard();
        
    	// Disable Home Button        
        this.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        
        super.onAttachedToWindow();
    }
    
    protected void selectOption(int option)
    {
    	Log.v(EASYPHONE_TAG, mName + ".selectOption()");
    	easyphone.mTTS.playEarcon("click", TextToSpeech.QUEUE_FLUSH, null);
    }
}
