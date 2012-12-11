package android.easyphone.UI;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Intent;
import android.easyphone.R;
import android.easyphone.Utils;
import android.easyphone.Calls.Call;
import android.os.Bundle;
import android.widget.TextView;

public class MissedCalls extends EasyPhoneActivity{
	
	private String mActivityName = "MissedCalls";
	private int mLayoutResource = R.layout.missedcalls;
	
	private ArrayList<Call> mMissedCalls = null; 
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, mActivityName, mLayoutResource);
        setContentView(mLayoutResource);
        
        // get missed calls
        mMissedCalls = Utils.mCallsManager.getMissedCalls();
        
        // set menu title
        mMenu.setTitle((String) ("Tem " + mMissedCalls.size() + " " + ((TextView)this.findViewById(R.id.tvTitle)).getText()));
        
        // set menu Options
        // back
        mMenu.addOption("1, Voltar atrás");
        
        // add missed calls to menu options
        for(int i=0; i < mMissedCalls.size(); i++)
        {
        	if(mMissedCalls.get(i).name != null)
        	{
        		mMenu.addOption((i+2) + ", " + mMissedCalls.get(i).name + ", no dia " + mMissedCalls.get(i).date.getDate() + 
        				" do " + mMissedCalls.get(i).date.getMonth() + " pelas " + mMissedCalls.get(i).date.getHours() + " horas e " + 
        				mMissedCalls.get(i).date.getMinutes() + " minutos");
        	}
        	else
        	{
        		mMenu.addOption((i+2) + ", " + Utils.getFormatedPhoneNumber(getApplicationContext(), mMissedCalls.get(i).number) + 
        				", no dia " + mMissedCalls.get(i).date.getDate() + " do " + mMissedCalls.get(i).date.getMonth() + 
        				" pelas " + mMissedCalls.get(i).date.getHours() + " horas e " +	mMissedCalls.get(i).date.getMinutes() + " minutos");
        	}
        }
    }

    protected void selectOption(int option)
    {
    	super.selectOption(option);
    
    	if(option == 0)
    	{
    		// back
    		// clear new missed calls
    		Utils.mCallsManager.clearMissedCalls();
    		
    		Intent resultIntent = new Intent();
			setResult(Activity.RESULT_CANCELED, resultIntent);
    		this.finish();
    	}
    	else if(option < mMissedCalls.size() + 1)
    	{
    		Call call = mMissedCalls.get(option - 1);
    		
    		// make call
    		easyphone.callControl.makeCall(call.number, getApplicationContext());
    	}
    }
}
