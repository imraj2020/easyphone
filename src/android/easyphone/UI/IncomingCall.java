package android.easyphone.UI;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.easyphone.MenuManager;
import android.easyphone.R;
import android.easyphone.Utils;
import android.easyphone.R.id;
import android.easyphone.R.layout;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;

public class IncomingCall extends EasyPhoneActivity{	
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, "IncomingCall", R.layout.incomingcall, false);
        setContentView(R.layout.incomingcall);
        
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
        
        mMenu = new MenuManager(3500, 7000);
        mMenu.setTitle((String) ((TextView)this.findViewById(R.id.TextView01)).getText());
        mMenu.addOption((String) ((TextView)this.findViewById(R.id.TextView02)).getText());
        mMenu.addOption((String) ((TextView)this.findViewById(R.id.TextView03)).getText());
    }    
    
    @Override
    public boolean onContainerTouchEvent(MotionEvent event)
    {
    	if(!mMenu.isScanning())
    	{
    		easyphone.callControl.silenceRinger();
    	}
    	return super.onContainerTouchEvent(event);
    }
    
    @Override
    public void onBackPressed() 
    {
    	/*if(!mMenu.isScanning())
    	{
    		easyphone.callControl.silenceRinger();
    	}*/
    	super.onBackPressed();
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
	    		//closeWindow();
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
        	if(intent.getAction().equals("android.easyphone.CLOSE_INCOMINGCALL_ACTIVITY"))
        	{
        		getApplicationContext().unregisterReceiver(receiver);
        		//closeWindow();
        		IncomingCall.this.finish();
        	}
        }
    };
}
