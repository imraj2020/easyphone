package android.easyphone.UI;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.easyphone.R;
import android.easyphone.Utils;
import android.easyphone.R.id;
import android.easyphone.R.layout;
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
        
        mMenu.addOption("1, Voltar atrás");
        
        for(int i=0; i<contactsList.size(); i++)
        { 
	        /*TextView tv = new TextView(getApplicationContext());
			tv.setId(i);
			tv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			Log.v(EASYPHONE_TAG, (i+2) + ", " + contactsList.get(i).first);
			tv.setText((i+2) + ", " + contactsList.get(i).first);
			LinearLayout layout = (LinearLayout) this.findViewById(R.id.LinearLayoutId01);
			layout.addView(tv);*/
			mMenu.addOption((String) ((i+2) + ", " + contactsList.get(i).first));//((TextView)this.findViewById(i)).getText());
        }
    }
    
    protected void selectOption(int option)
    {
    	super.selectOption(option);
    	
    	Log.v(EASYPHONE_TAG, "Contact List Type: " + contactListType);
    	Log.v(EASYPHONE_TAG, "Is groups: " + Utils.isGroups);
    	
    	if(option==0)
    	{
    		Intent resultIntent = new Intent();
			setResult(Activity.RESULT_CANCELED, resultIntent);
    		this.finish();
    	}
    	else if(option==contactsList.size() && Utils.getNumberOfContacts() > Utils.lowContactThreshold && contactListType.equalsIgnoreCase("priority"))
    	{
    		// other contacts - last element of priority contactsList
    		Intent contactList =  new Intent(getApplicationContext(), ContactList.class);
    		contactList.putExtra("contactListType", "smallList");
    		startActivity(contactList);
    		
    	}
    	else if(contactListType.equalsIgnoreCase("smallList") && Utils.isGroups)
    	{
    		// other contacts > groups
    		Log.v(EASYPHONE_TAG, "Right placee");
    		Log.v(EASYPHONE_TAG, "Option: " + option);
    		String listType = null;
    		if (option==1) listType= "a_d";
    		else if(option==2) listType="e_h";
    		else if(option==3) listType="i_n";
    		else if(option==4) listType="o_t";
    		else if(option==5) listType="u_z";
    		Intent contactList =  new Intent(getApplicationContext(), ContactList.class);
    		contactList.putExtra("contactListType", listType);
    		startActivity(contactList);
    	}
    	else
    	{
    		// call contact
    		easyphone.callControl.makeCall(contactsList.get(option-1).second, getApplicationContext());
    	}

    }

}
