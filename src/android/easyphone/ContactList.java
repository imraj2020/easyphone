package android.easyphone;
import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ContactList extends EasyPhoneActivity {
	
	private ArrayList<Pair<String, String>>contactsList = null;
	private String contactListType=null;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, "ContactList", R.layout.contactlist);
        contactListType = (String) getIntent().getExtras().get("contactListType");
        setContentView(R.layout.contactlist);
                
        if (contactListType.equalsIgnoreCase("priority")){
        	contactsList = Utils.getPriorityContactsList();
        }
        else if (contactListType.equalsIgnoreCase("smallList"))
        {
        	contactsList = Utils.getSmallContactsList();
        }
        if (contactListType.equalsIgnoreCase("a_d")) contactsList=Utils.geta_dContactsList();
		else if (contactListType.equalsIgnoreCase("e_h")) contactsList=Utils.gete_hContactsList();
		else if (contactListType.equalsIgnoreCase("i_n")) contactsList=Utils.geti_nContactsList();
		else if (contactListType.equalsIgnoreCase("o_t")) contactsList=Utils.geto_tContactsList();
		else if (contactListType.equalsIgnoreCase("u_z")) contactsList=Utils.getu_zContactsList();

        
        //Set Menu Options
        mMenu.setTitle((String) ((TextView)this.findViewById(R.id.TextView01)).getText());
        
        for(int i=0; i<contactsList.size(); i++)
        { 
	        TextView tv = new TextView(getApplicationContext());
			tv.setId(i);
			tv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			Log.v(EASYPHONE_TAG, (i+1) + ", " + contactsList.get(i).first);
			tv.setText((i+1) + ", " + contactsList.get(i).first);
			LinearLayout layout = (LinearLayout) this.findViewById(R.id.LinearLayoutId01);
			layout.addView(tv);
			mMenu.addOption((String) ((TextView)this.findViewById(i)).getText());
        }
    }
    
    protected void selectOption(int option)
    {
    	super.selectOption(option);
    	
    	Log.v(EASYPHONE_TAG, "Contact List Type: " + contactListType);
    	Log.v(EASYPHONE_TAG, "Is groups: " + Utils.isGroups);
    	
    	/*
    	 easyphone.callControl.makeCall(Utils.getContactsList()[option-1], getApplicationContext());
    	  
    	 */
    	
    	if(option==contactsList.size()-1)
    	{
    		this.finish();
    	}
    	else if(option==contactsList.size()-2 && Utils.getNumberOfContacts() > Utils.lowContactThreshold && contactListType.equalsIgnoreCase("priority"))
    	{
    		Intent contactList =  new Intent(getApplicationContext(), ContactList.class);
    		contactList.putExtra("contactListType", "smallList");
    		startActivity(contactList);
    		
    	}
    	else if(contactListType.equalsIgnoreCase("smallList") && Utils.isGroups)
    	{
    		Log.v(EASYPHONE_TAG, "Right placee");
    		Log.v(EASYPHONE_TAG, "Option: " + option);
    		String listType = null;
    		if (option==0) listType= "a_d";
    		else if(option==1) listType="e_h";
    		else if(option==2) listType="i_n";
    		else if(option==3) listType="o_t";
    		else if(option==4) listType="u_z";
    		Intent contactList =  new Intent(getApplicationContext(), ContactList.class);
    		contactList.putExtra("contactListType", listType);
    		startActivity(contactList);
    	}
    	else
    	{
    		easyphone.callControl.makeCall(contactsList.get(option).second, getApplicationContext());
    	}

    }

}
