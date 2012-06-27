package android.easyphone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

public class IncomingCall extends EasyPhoneActivity{

	//ViewGroup mTopView = null;
	LinearLayout mContainer = null;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, "IncomingCall", false);
        
        setContentView(R.layout.incomingcall);
        
        /* ALWAYS ON TOP WINDOW */
        WindowManager.LayoutParams params = new WindowManager.LayoutParams( 
        		WindowManager.LayoutParams.FLAG_FULLSCREEN, 
        		WindowManager.LayoutParams.FLAG_FULLSCREEN,                 
        		WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,               
        		WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
        		PixelFormat.OPAQUE);
        WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);    
       
        // Create window container to be always on top
        mContainer = new Container(this.getApplicationContext());
        mContainer.setLayoutParams(params);
        mContainer.setClickable(true);
        mContainer.setFocusable(true);
        mContainer.setFocusableInTouchMode(true);
        
        LayoutInflater inflater = (LayoutInflater) this.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.incomingcall, mContainer);
        wm.addView(mContainer, params);
        
        // Register receiver to close this activity
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
    
    /* This will only be called when the container has focus. */
    private boolean onContainerKey(KeyEvent event) {
        int keyCode = event.getKeyCode();
        
        if(event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK)
		{
			super.onBackPressed();
		}
        return true;
    }
    
    public boolean onContainerTouchEvent(MotionEvent event)
    { 
    	if(!mMenu.isScanning())
    	{
    		easyphone.callControl.silenceRinger();
    	}
    	return super.onTouchEvent(event);
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
	    	//wm.removeView(mTopView);
	    	wm.removeView(mContainer);
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
}
