package android.easyphone;
import java.util.ArrayList;
import android.os.Bundle;
import android.util.Pair;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ContactList extends EasyPhoneActivity {

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, "ContactList", R.layout.contactlist);
        setContentView(R.layout.contactlist);
        
        //Set Menu Options
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
    
    protected void selectOption(int option)
    {
    	super.selectOption(option);
    	
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
