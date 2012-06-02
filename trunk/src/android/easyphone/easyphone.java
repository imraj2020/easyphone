package android.easyphone;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;

public class easyphone extends Activity implements OnInitListener{
	static public String EASYPHONE_TAG = "EasyPhone";
	static public CallControl callControl = null;
	private final String TAG = this.getClass().getSimpleName();
	static public TextToSpeech mTTS = null;
    private int MY_DATA_CHECK_CODE;
    private boolean mttsloaded = false;
    private MenuManager mMenu = null;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.v(EASYPHONE_TAG, "easyphone.onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        //Register CallControl
        registerCallReceiver();
        
        //TTS - check if it is installed
        Intent checkIntent = new Intent();
        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkIntent, MY_DATA_CHECK_CODE); //wait for activity result
        
        //Get UI Elements
        mMenu = new MenuManager();
        mMenu.setTitle((String) ((TextView)this.findViewById(R.id.TextView01)).getText());
        mMenu.addOption((String) ((TextView)this.findViewById(R.id.TextView02)).getText());
    }
    
    @Override
    public void onStart()
    {
    	Log.v(EASYPHONE_TAG, "easyphone.onStart()");
    	super.onStart();
    	
    	mMenu.startScanning(true);
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event)
    { 
    	Log.v(EASYPHONE_TAG, "easyphone.onTouchEvent()");
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
    	Log.v(EASYPHONE_TAG, "easyphone.selectOption()");
    	switch(option)
    	{
	    	case 0:  
	    	{
	    		//Call
	    		Intent contactList =  new Intent(getApplicationContext(), ContactList.class);
	    		startActivity(contactList);
	    		break;
	    	}
    	}
    }
    
    /** Called when TTS reply arrive. */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	Log.v(EASYPHONE_TAG, "easyphone.onActivityResult()");
        if (requestCode == MY_DATA_CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                // success, create the TTS instance
                //tts = new TextToSpeech(this, this); //wait for TTS init
            	mTTS = new TextToSpeech(this, this); //wait for TTS init
            } else {
                // missing data, install it
                Intent installIntent = new Intent();
                installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installIntent);
            }
        }
    }
    
    /** Called when TTS is initialized. */
    public void onInit(int status) {
    	Log.v(EASYPHONE_TAG, "easyphone.onInit()");
    	if(mttsloaded) return;
    	mttsloaded = true;
    	
    	if(status == TextToSpeech.SUCCESS)
    	{
    		Locale locale = Locale.getDefault();
    		mTTS.setLanguage(locale);
    		//tts.addEarcon("click", "/sdcard/click.wav");
    		//tts.addEarcon("error", "/sdcard/error.wav");
    		//tts.addEarcon("back", "/sdcard/back.wav");
    		
    		//first time read title and options
    		//readTitle();
    		//scanOptions();
    	}
    	else //ERROR
    	{
    		//tts.playEarcon("error", TextToSpeech.QUEUE_FLUSH, null);
    		Toast.makeText(this, "Error: TTS not avaliable. Check your device settings.", Toast.LENGTH_LONG).show();
    	}
	}
    
    private void registerCallReceiver(){
    	Log.v(EASYPHONE_TAG, "easyphone.registerCallReceiver()");
		if(callControl != null){
			// Receiver is already registered
			return;
		}
		Log.i(TAG, "Register Receiver.........");
		IntentFilter inf = new IntentFilter("android.intent.action.PHONE_STATE");
		
		Log.i(TAG, "Call Receiver instance_New_________");
		callControl = new CallControl();
		getApplicationContext().registerReceiver(callControl, inf, "android.permission.READ_PHONE_STATE", null);
	}
}