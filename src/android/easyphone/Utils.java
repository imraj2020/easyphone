package android.easyphone;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Timer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.BatteryManager;
import android.provider.ContactsContract;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;

public class Utils {	
	
	// Battery receiver
	private static PowerConnectionManager mBatteryReceiver = null;

	// Screen state receiver
	private static ScreenReceiver mScreenStateReceiver = null;
	
	// Contact list vars
	private static ArrayList<Pair<String, String>> contactsList = new ArrayList<Pair<String, String>>();
	
	// Bounce vars
	private static int mBounceThreshold = 1000; //1 second
	private static long mLastTimeStamp = Long.MIN_VALUE;
	
	/***/
	
	//check if event is valid
	public static boolean isEventValid(MotionEvent event)
	{
		boolean result = true;
		long timestamp = event.getEventTime();
		
		Log.v(easyphone.EASYPHONE_TAG, "Utils.isEventValid() time: " + timestamp);
		
		if(mLastTimeStamp != Long.MIN_VALUE)
		{
			long diff = timestamp - mLastTimeStamp;
			if(diff < mBounceThreshold)
			{
				// invalid
				result = false;
			}
			else
			{
				// valid
				mLastTimeStamp = timestamp;
			}
		}
		else
		{
			// first touch
			mLastTimeStamp = timestamp;
		}
		
		return result;
	}
	
	// get contact name from phonenumber
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
		Log.v(easyphone.EASYPHONE_TAG, "Utils.getAllContacts()");
		contactsList = new ArrayList<Pair<String, String>>();
		Cursor people = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
		
		//Iterate all contacts
		while(people.moveToNext()) {
			
		   //Get the contactId and name
		   String contactId = people.getString(people.getColumnIndex( ContactsContract.Contacts._ID)); 
		   int nameFieldColumnIndex = people.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
		   String contact = people.getString(nameFieldColumnIndex);
		  
		      // Exception to avoid strange cases. Example: contact without any phone number
		   
		   try
		   {
			   
			  // Get all phone numbers 
			   Cursor phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ contactId, null, null); 
		      
			  //Iterate the phone numbers
			  while (phones.moveToNext()) {
		    	 
				 //Get Number
		         String phoneNumber = phones.getString(phones.getColumnIndex( ContactsContract.CommonDataKinds.Phone.NUMBER));
		         
		         //If the contact has more than one phone number, detail the type of number (mobile, work or home);
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
			   Log.v(easyphone.EASYPHONE_TAG, "Utils.getAllContacts() - EXCEPTION");
			   continue;
		   }
		}
		//Sort the contacts list
		Collections.sort(contactsList, new pairComparator());
		
		contactsList.add(new Pair<String, String>("Voltar atrás", ""));
		people.close();
	}
	
	//Function to return a string with the type of phone number
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
	
	public static ArrayList<Pair<String, String>> getContactsList()
	{
		return contactsList;
	}

    public static class pairComparator implements Comparator<Pair<String, String>> {
	    public int compare(Pair<String, String> name1, Pair <String, String> name2) {
	        return name1.first.compareTo(name2.first);
	    }
	}
    
    /* Battery Event Listener */
	public static void registerBatteryListener(Context context)
	{
		Log.v(easyphone.EASYPHONE_TAG, "Utils.registerBatteryListener()");
		IntentFilter batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		mBatteryReceiver = new PowerConnectionManager();
		context.registerReceiver(mBatteryReceiver, batteryLevelFilter);
	}
	
	public static void unregisterBatteryListener(Context context)
	{
		context.unregisterReceiver(mBatteryReceiver);
	}
    
	public static int getBatteryLevel()
	{
		return mBatteryReceiver.getBatteryLevel();
	}
	
	/* Screen State Listener*/
	public static void registerScreenStateListener(Context context)
	{
		IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        mScreenStateReceiver = new ScreenReceiver();
        context.registerReceiver(mScreenStateReceiver, filter);
	}
	
	public static void unregisterScreenStateListener(Context context)
	{
		context.unregisterReceiver(mScreenStateReceiver);
	}	
}
