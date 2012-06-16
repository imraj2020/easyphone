package android.easyphone;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

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
        ArrayList<Pair<String, String>>contactsList = Utils.getContactsList();
        
        for(int i=0; i<contactsList.size(); i++)
        { 
	        TextView tv = new TextView(getApplicationContext());
			tv.setId(i);
			tv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			tv.setText((i+1) + ", " + contactsList.get(i).first);
			LinearLayout layout = (LinearLayout) this.findViewById(R.id.LinearLayoutId01);
			layout.addView(tv);
			mMenu.addOption((String) ((TextView)this.findViewById(i)).getText());
        }
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
    	Log.v(easyphone.EASYPHONE_TAG, "ContactList.selectOption()");
    	Log.v(TAG, "Contact selected: " + option);
    	/*
    	 easyphone.callControl.makeCall(Utils.getContactsList()[option-1], getApplicationContext());
    	  
    	 */
    	
    	if(option==Utils.getContactsList().size()-1)
    	{
    		this.finish();
    	}
    	else
    	{
    		easyphone.callControl.makeCall(Utils.getContactsList().get(option).second, getApplicationContext());
    	}

    }

}
