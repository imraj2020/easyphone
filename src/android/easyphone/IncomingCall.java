package android.easyphone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

public class IncomingCall extends EasyPhoneActivity{

	ViewGroup mTopView = null;
    
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, "IncomingCall", false);
        
        setContentView(R.layout.incomingcall);
        
        /* ALLWAYS ON TOP WINDOW */
        WindowManager.LayoutParams params = new WindowManager.LayoutParams( 
        		WindowManager.LayoutParams.FLAG_FULLSCREEN, 
        		WindowManager.LayoutParams.FLAG_FULLSCREEN,                 
        		WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,               
        		WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
        		PixelFormat.OPAQUE);
        WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);    
        mTopView = (ViewGroup) this.getLayoutInflater().inflate(R.layout.incomingcall, null);
        getWindow().setAttributes(params);
        
        mTopView.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
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
		    	return false;
			}
		});
        
        mTopView.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				Log.v(easyphone.EASYPHONE_TAG, "IncomingCall.onKey()");
				
				if(event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK)
				{
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
	            		  easyphone.callControl.silenceRinger();
	            		  mMenu.startScanning(true);
	            	  }
				}
				return false;
			}
		});
        
        wm.addView(mTopView, params);
        
        getApplicationContext().registerReceiver(receiver, new IntentFilter("android.easyphone.CLOSE_INCOMINGCALL_ACTIVITY"));
        
        //Set Menu Options
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
    protected void selectOption(int option)
    {
    	super.selectOption(option);
    	switch(option)
    	{
	    	case 0:  
	    	{
	    		//Accept call
	    		closeWindow();
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
    
    private void closeWindow()
    {
    	Log.v(easyphone.EASYPHONE_TAG, "IncomingCall.closeWindow()");
    	try
    	{
	    	WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
	    	wm.removeView(mTopView);
    	}
    	catch(Exception e)
    	{
    		Log.v(easyphone.EASYPHONE_TAG, "IncomingCall.closeWindow() - EXCEPTION");
    		return;
    	}
    	
    }
    
    /* USED BY CALLCONTROL */
    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
        	Log.v(easyphone.EASYPHONE_TAG, "IncomingCall.onReceive()");
        	if(intent.getAction().equals("android.easyphone.CLOSE_INCOMINGCALL_ACTIVITY"))
        	{
        		getApplicationContext().unregisterReceiver(receiver);
        		closeWindow();
        		IncomingCall.this.finish();
        	}
        }
    };
    
    
}
