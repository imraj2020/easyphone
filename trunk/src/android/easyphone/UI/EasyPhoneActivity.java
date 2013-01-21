package android.easyphone.UI;

import java.util.Timer;
import java.util.TimerTask;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.easyphone.MenuManager;
import android.easyphone.Utils;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.LinearLayout;

public class EasyPhoneActivity extends Activity
{
	static public String EASYPHONE_TAG = "EasyPhone";
	
	// menu variables
	protected MenuManager mMenu = null;
	protected String mName = "";
	protected boolean mReadMenu = true;
	
	// exit variables
	private int mMenuHits = 0;
	private boolean mHasStarted = false;
	private int TIME_THRESHOLD = 2000; // 1s
	private Timer mExitTimer = null;
	
	// window variables
	LinearLayout mContainer = null;
	WindowManager mWindowManager = null;
	
	/** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState, String activityName, int layoutResource) 
    {
    	mName = activityName;
    	Log.v(EASYPHONE_TAG, mName + ".onCreate()");
    	
    	super.onCreate(savedInstanceState);
   
    	// Initialize Scanning Menu
    	mMenu = new MenuManager();
    	
    	// Initialize exiting vars
    	mMenuHits = 0;
    	mHasStarted = false;
    	mExitTimer = new Timer();
    	
    	// Always on top window
        WindowManager.LayoutParams params = new WindowManager.LayoutParams( 
        		WindowManager.LayoutParams.FLAG_FULLSCREEN, 
        		WindowManager.LayoutParams.FLAG_FULLSCREEN,                 
        		WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,               
        		WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_TOUCHABLE_WHEN_WAKING,
        		PixelFormat.OPAQUE);
        mWindowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);    
       
        // Create window container to be always on top
        mContainer = new Container(this.getApplicationContext());
        mContainer.setLayoutParams(params);
        mContainer.setClickable(true);
        mContainer.setFocusable(true);
        mContainer.setFocusableInTouchMode(true);
        
        LayoutInflater inflater = (LayoutInflater) this.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(layoutResource, mContainer);
        mWindowManager.addView(mContainer, params);
    }
    
    public void onCreate(Bundle savedInstanceState, String activityName, int layoutResource, boolean readMenuOnStart) 
    {
    	mReadMenu = readMenuOnStart;
    	onCreate(savedInstanceState, activityName, layoutResource);
    }
    
    @Override
    public void onStart()
    {
    	Log.v(EASYPHONE_TAG, mName + ".onStart()");
    	super.onStart();
    	
    	if(mReadMenu)	startScanningMenu(true);
    }
    
    @Override
    public void onStop()
    {
    	Log.v(EASYPHONE_TAG, mName + ".onStop()");
    	
    	// stop menu reading
    	//mMenu.stopScanning();
    	
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
    public void onDestroy()
    {
    	Log.v(EASYPHONE_TAG, mName + ".onDestroy()");
    	
    	Log.v(easyphone.EASYPHONE_TAG, mName + ".onDestroy()");
    	try
    	{
	    	mWindowManager.removeView(mContainer);
    	}
    	catch(Exception e)
    	{
    		Log.v(easyphone.EASYPHONE_TAG, mName + "onDestroy() - EXCEPTION");
    	}
    	
    	super.onDestroy();
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
            		  startScanningMenu(true);
            	  }
            	  
                  break;
              }
            }
    	return true;
    }
    
    protected void startScanningMenu(boolean readTitle)
    {
    	mMenu.startScanning(readTitle);
    }
    
    @Override
    public void onBackPressed() 
    {
    	// Comment if you don't want to deal with back button 
    	/*Log.v(EASYPHONE_TAG, mName + ".onBackPressed()");

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
	  	}*/
    }
    
    @Override 
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
    	Log.v(EASYPHONE_TAG, mName + ".onKeyDown(): " + keyCode);
    	if(keyCode == KeyEvent.KEYCODE_BACK)
    	{
    		return super.onKeyDown(keyCode, event);
    	}
    	else if(keyCode == KeyEvent.KEYCODE_MENU)
    	{
    		processMenuKey();
    	}
    	if(keyCode == KeyEvent.KEYCODE_VOLUME_UP)
    	{ 
    		//increase volume
    		Utils.increaseVolume(getApplicationContext());
    	}
    	else if(keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)
    	{
    		// decrease volume
    		Utils.decreaseVolume(getApplicationContext());
    	}
    	
    	return true;
    }
    
    @Override
    public void onAttachedToWindow() 
    {
    	Log.v(EASYPHONE_TAG, mName + ".onAttachedToWindow()");
    	
    	// Disable Power Button
    	KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Activity.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock lock = keyguardManager.newKeyguardLock(KEYGUARD_SERVICE);
        lock.disableKeyguard();
        
        super.onAttachedToWindow();
    }
    
    // default selectOption behavior
    protected void selectOption(int option)
    {
    	Log.v(EASYPHONE_TAG, mName + ".selectOption() - " + option);
    	easyphone.mTTS.playEarcon("click", TextToSpeech.QUEUE_FLUSH, null);
    	mMenu.stopScanning();
    }
    
    /* CONTAINER CLASS AND DISPATCHING METHODS */
    
    /* This will only be called when the container has focus. */
    private boolean onContainerKey(KeyEvent event) {
        int keyCode = event.getKeyCode();
        
        if(event.getAction() == KeyEvent.ACTION_DOWN)
        {
        	return onKeyDown(keyCode, event);
        }
        else if(event.getAction() == KeyEvent.ACTION_UP)
        {
        	if(keyCode == KeyEvent.KEYCODE_BACK)
        	{
        		onBackPressed();
        	}
        }
        return true;
    }
    
    public boolean onContainerTouchEvent(MotionEvent event)
    { 
    	return onTouchEvent(event);
    }
    
    // Container Class
    private class Container extends LinearLayout {
        public Container(Context context) {
            super(context);
        }

        /*
         * Need to override this to intercept the key events. Otherwise, we
         * would attach a key listener to the container but its superclass
         * ViewGroup gives it to the focused View instead of calling the key
         * listener, and so we wouldn't get the events.
         */
        @Override
        public boolean dispatchKeyEvent(KeyEvent event) {
            return onContainerKey(event) ? true : super.dispatchKeyEvent(event);
        }
        
        @Override
        public boolean onTouchEvent(MotionEvent event)
        { 
        	return onContainerTouchEvent(event) ? true : super.onTouchEvent(event);
        }
    }
    
    // check if key combinations allow exit
    private void processMenuKey()
    {
    	mMenuHits++;
    	if(!mHasStarted)
    	{ // start new timer
    		mHasStarted = true;
    		mExitTimer.schedule(new TimerTask() {
				
				@Override
				public void run() 
				{
					mHasStarted = false;
					mMenuHits = 1;
				}
			}, TIME_THRESHOLD);
    	}
    	if(mMenuHits >= 4)
    	{ // 4 consecutive hits, exit app
    		if(easyphone.mTTS != null) easyphone.mTTS.playEarcon("exit", TextToSpeech.QUEUE_FLUSH, null);
    		while(easyphone.mTTS != null && easyphone.mTTS.isSpeaking());
    		finish();
    		System.exit(RESULT_OK);
    	}
    }
}
