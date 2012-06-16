package android.easyphone;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.BatteryManager;
import android.provider.ContactsContract;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Utils {
	private static int mBatteryLevel = -1;
	private static ArrayList<Pair<String, String>> contactsList = new ArrayList<Pair<String, String>>();
	
	
	public static String getContactName(Context context, String number)
	{
		Log.v(easyphone.EASYPHONE_TAG, "Utils.getContactName()");
		String name = null;
		
		// define the columns I want the query to return
		String[] projection = new String[] {ContactsContract.PhoneLookup.DISPLAY_NAME};

		// encode the phone number and build the filter URI
		Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));

		// query time
		Cursor cursor = context.getContentResolver().query(contactUri, projection, null, null, null);

		if (cursor.moveToFirst()) {
		    // Get values from contacts database:
		    name = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
		}

		return name;
	}
	
	public static void getAllContacts(Context context)
	{		
		Cursor people = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
		while(people.moveToNext()) {
			String contactId = people.getString(people.getColumnIndex( ContactsContract.Contacts._ID)); 
		   int nameFieldColumnIndex = people.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
		   String contact = people.getString(nameFieldColumnIndex);
		  
		      // You know it has a number so now query it like this
		   try
		   {
			  Cursor phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ contactId, null, null); 
		      while (phones.moveToNext()) {
		    	  
		         String phoneNumber = phones.getString(phones.getColumnIndex( ContactsContract.CommonDataKinds.Phone.NUMBER));
		         String phoneType="";
		         if (phones.getCount()>1)
		         {
		        	 int numberType = phones.getInt(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
			         phoneType = getPhoneNumberType(numberType);
		         }
		         contactsList.add(new Pair<String, String>(contact+phoneType, phoneNumber));
		      } 
		      phones.close();
		   }
		   catch(Exception e){
			   continue;
		   }

		}
		contactsList.add(new Pair<String, String>("Voltar atrás", ""));
		people.close();
	}
	
	private static String getPhoneNumberType(int type)
	{
		String s;
        switch(type)
        {
            case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                s = ", casa";
                break;
            case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                s = ", telemóvel";
                break;
            case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                s = ", trabalho";
                break;
            default:
                s = "";
        }
        return s;
	}
	
	public static int getBatteryLevel()
	{
		return mBatteryLevel;
	}
	
	public static ArrayList<Pair<String, String>> getContactsList()
	{
		return contactsList;
	}
	
	/* Battery Event Listener */
	public static void registerBatteryListener(Context context)
	{
		Log.v(easyphone.EASYPHONE_TAG, "Utils.registerBatteryListener()");
		IntentFilter batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		context.registerReceiver(batteryLevelReceiver, batteryLevelFilter);
	}
	
	public static void unregisterBatteryListener(Context context)
	{
		context.unregisterReceiver(batteryLevelReceiver);
	}
	
	private static BroadcastReceiver batteryLevelReceiver = new BroadcastReceiver() {
    	public void onReceive(Context context, Intent intent) {
    		Log.v(easyphone.EASYPHONE_TAG, "Utils.batteryLevelReceiver.onReceive()");
    		//context.unregisterReceiver(this);
            int rawlevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            int level = -1;
            if (rawlevel >= 0 && scale > 0) {
            	level = (rawlevel * 100) / scale;
                mBatteryLevel = level;
            }
        }
    };

}
