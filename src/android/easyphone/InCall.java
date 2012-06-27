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

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, "InCall", R.layout.incall, false);
        setContentView(R.layout.incall);
        
        getApplicationContext().registerReceiver(receiver, new IntentFilter("android.easyphone.CLOSE_INCALL_ACTIVITY"));
        
        //Get UI Elements
        mMenu = new MenuManager(100, 5000);
        mMenu.setTitle((String) ((TextView)this.findViewById(R.id.TextView01)).getText());
        mMenu.addOption((String) ((TextView)this.findViewById(R.id.TextView02)).getText());
        
        //Turn Speaker ON
		AudioManager am = (AudioManager)getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        am.setSpeakerphoneOn(true);
    }
    
    @Override
    public void onStart()
    {
    	super.onStart();
    	
    	//Turn Speaker ON
    	AudioManager am = (AudioManager)getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        am.setSpeakerphoneOn(true);
    }
    
    @Override
    public void onResume()
    {
    	super.onResume();
    	
    	//Turn Speaker ON
    	AudioManager am = (AudioManager)getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        am.setSpeakerphoneOn(true);
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
    
    @Override
    public void onBackPressed() 
    {
    	Log.v(easyphone.EASYPHONE_TAG, "InCall.onBackPressed()");
    	// finger up event, END CALL
    	selectOption(0);
    }
    
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
		AudioManager am = (AudioManager)getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        am.setSpeakerphoneOn(false);
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