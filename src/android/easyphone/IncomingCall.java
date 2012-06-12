package android.easyphone;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.TextView;

public class IncomingCall extends Activity{
	private final String TAG = "IncomingCall";
	private MenuManager mMenu = null;
    
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.v(easyphone.EASYPHONE_TAG, "IncomingCall.onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.incomingcall);
        
        getApplicationContext().registerReceiver(receiver, new IntentFilter("android.easyphone.CLOSE_INCOMINGCALL_ACTIVITY"));
        
        //Get UI Elements
        String incomingNumber = easyphone.callControl.getIncomingNumber();
        String name = null;
        if(incomingNumber == null)
        {
        	incomingNumber = "Número privado";
        	((TextView)this.findViewById(R.id.TextView01)).setText("Chamada de, " + incomingNumber);
        }
        else if((name = Utils.getContactName(getApplicationContext(), incomingNumber)) != null)
        {
        	((TextView)this.findViewById(R.id.TextView01)).setText("Chamada de, " + name);
        }
        else
        {
        	String aux = "";
        	for (char c : incomingNumber.toCharArray())
            {
                aux += c;
                aux += " ";
            }
        	((TextView)this.findViewById(R.id.TextView01)).setText("Chamada de, " + aux);
        }
        
        mMenu = new MenuManager(3500, 5000);
        mMenu.setTitle((String) ((TextView)this.findViewById(R.id.TextView01)).getText());
        mMenu.addOption((String) ((TextView)this.findViewById(R.id.TextView02)).getText());
        mMenu.addOption((String) ((TextView)this.findViewById(R.id.TextView03)).getText());
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
    	
    	easyphone.mTTS.playEarcon("click", TextToSpeech.QUEUE_FLUSH, null);
    	
		//Screen Brightness
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = (float) 10;
        getWindow().setAttributes(lp);
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
            		  easyphone.callControl.silenceRinger();
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
    	Log.v(easyphone.EASYPHONE_TAG, "easyphone.onBackPressed()");
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
    
    private void selectOption(int option)
    {
    	Log.v(easyphone.EASYPHONE_TAG, "IncomingCall.selectOption()");
    	switch(option)
    	{
	    	case 0:  
	    	{
	    		//Accept call
	    		easyphone.callControl.answerCall();
	    		this.finish();
	    		break;
	    	}
	    	
	    	case 1:
	    	{
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
