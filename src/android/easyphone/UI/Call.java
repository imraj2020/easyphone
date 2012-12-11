package android.easyphone.UI;

import android.app.Activity;
import android.content.Intent;
import android.easyphone.R;
import android.os.Bundle;
import android.widget.TextView;

public class Call extends EasyPhoneActivity{
	
	private String mActivityName = "Empty"; //TODO: change activity name, needed for debug purposes
	private int mLayoutResource = R.layout.main; //TODO: change layout resource
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, mActivityName, mLayoutResource);
        setContentView(mLayoutResource);
        
        // TODO: set menu title
        mMenu.setTitle((String) ((TextView)this.findViewById(R.id.tvTitle)).getText());
        
        // TODO: set menu Options
        mMenu.addOption((String) ((TextView)this.findViewById(R.id.tvBack)).getText());
    }

    protected void selectOption(int option)
    {
    	super.selectOption(option);
    
    	//TODO: add activity options
    	switch(option)
    	{
    	case 0:
    		// back
    		Intent resultIntent = new Intent();
			setResult(Activity.RESULT_CANCELED, resultIntent);
    		this.finish();
    		break;
    	}
    }
}
