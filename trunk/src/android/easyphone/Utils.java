package android.easyphone;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.easyphone.Calls.CallsManager;
import android.easyphone.SMS.SMSManager;
import android.easyphone.UI.easyphone;
import android.media.AudioManager;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;

public class Utils {	
	
	// Battery receiver
	private static PowerConnectionManager mBatteryReceiver = null;

	// Screen state receiver
	private static ScreenReceiver mScreenStateReceiver = null;
	
	// Contact list vars
	private static ArrayList<Pair<String, String>> priorityContactsList = new ArrayList<Pair<String, String>>();
	private static ArrayList<Pair<String, String>> smallContactsList = new ArrayList<Pair<String, String>>();
	private static ArrayList<Pair<String, String>> a_dContactsList = new ArrayList<Pair<String, String>>();
	private static ArrayList<Pair<String, String>> e_hContactsList = new ArrayList<Pair<String, String>>();
	private static ArrayList<Pair<String, String>> i_nContactsList = new ArrayList<Pair<String, String>>();
	private static ArrayList<Pair<String, String>> o_tContactsList = new ArrayList<Pair<String, String>>();
	private static ArrayList<Pair<String, String>> u_zContactsList = new ArrayList<Pair<String, String>>();
	public static int lowContactThreshold = 5;
	public static int mediumContactThreshold = 15;
	public static int numberOfContacts = 15;
	public static boolean isGroups=false;
	
	// SMS Manager
	public static SMSManager mSMSManager = null;
	
	// Calls Manager
	public static CallsManager mCallsManager = null;
	
	// Bounce vars
	private static int mBounceThreshold = 1000; //1 second
	private static long mLastTimeStamp = Long.MIN_VALUE;
	
	// Volume vars
	private static int mMaxMusicVolume = Integer.MIN_VALUE;
	private static int mMusicVolumeStep = Integer.MAX_VALUE;
	private static AudioManager mAudioManager = null;
	
	/***/
	
	// increase volume
	public static void increaseVolume(Context context)
	{
		if(mAudioManager == null) initializeAudio(context);
		
		// set music volume
		int musicvolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		int newMusicVolume = musicvolume + mMusicVolumeStep;
		if(newMusicVolume > mMaxMusicVolume) newMusicVolume = mMaxMusicVolume;
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newMusicVolume, 0);
	}
	
	// decrease volume
	public static void decreaseVolume(Context context)
	{
		if(mAudioManager == null) initializeAudio(context);
		
		// set music volume
		int musicvolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		int newMusicVolume = musicvolume - mMusicVolumeStep;
		if(newMusicVolume < 0) newMusicVolume = 0;
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newMusicVolume, 0);
		
		
	}
	
	// initialize audio variables
	private static void initializeAudio(Context context)
	{
		mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		//initialize music stream vars
		mMaxMusicVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		mMusicVolumeStep = mMaxMusicVolume / 10;
	}
	
	//check if click event is valid
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
	
	// return contact for TTS to read
	public static String getFormatedPhoneNumber(Context context, String number)
	{
		String name = "";
		
		if((name = Utils.getContactName(context, number)) != null)
        {
			// return contact name
    		return name;
        }
        else
        {
        	// unknown contact name
        	if(Utils.isPhoneNumber(number))
        	{
        		// return phone number separated by blank spaces
            	String aux = "";
            	for (char c : number.toCharArray())
                {
                    aux += c;
                    aux += " ";
                }
            	return aux;
        	}
        	else
        	{
        		// return sender name
        		return number;
        	}
        }
	}
	
	// return true if string is a phone number
	public static boolean isPhoneNumber(String number)
	{
		if(number==null) return false;
		
		return number.matches("\\+?\\d+");
	}
	
	public static void getAllContacts(Context context)
	{		
		Log.v(easyphone.EASYPHONE_TAG, "Utils.getAllContacts()");
		priorityContactsList = new ArrayList<Pair<String, String>>();
		
		boolean lessThanLowThreshold = false;
		boolean lessThanMiddleThreshold = false;
		
		Cursor people = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
		numberOfContacts = people.getCount();
		
		if (numberOfContacts <= lowContactThreshold)
		{
			lessThanLowThreshold = true;
		}
		else if(numberOfContacts <= mediumContactThreshold)
		{
			lessThanMiddleThreshold = true;
		}
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
		         if(contact.charAt(contact.length()-1) == '.' || lessThanLowThreshold)
		         {
		        	 priorityContactsList.add(new Pair<String, String>(contact+phoneType, phoneNumber));
		         }
		         else if(lessThanMiddleThreshold)
		         {
		        	 smallContactsList.add(new Pair<String, String>(contact+phoneType, phoneNumber));
		         }
		         else
		         {
		        	 insertInAbcdGroups(contact+phoneType, phoneNumber);
		         }
		      }
			  phones.close();
			  
		   }
		   catch(Exception e){
			   Log.v(easyphone.EASYPHONE_TAG, "Utils.getAllContacts() - EXCEPTION");
			   continue;
		   }
		}
		//Sort the contacts list
		Collections.sort(priorityContactsList, new pairComparator());
		
		if(lessThanLowThreshold) priorityContactsList.add(new Pair<String, String>("Voltar atrás", ""));
		else
		{
			priorityContactsList.add(new Pair<String, String>("Outros contactos", ""));
			//priorityContactsList.add(new Pair<String, String>("Voltar atrás", ""));
			if(lessThanMiddleThreshold)
			{
				if (!smallContactsList.isEmpty())
				{
					Collections.sort(smallContactsList, new pairComparator());
					//smallContactsList.add(new Pair<String, String>("Voltar atrás", ""));
				}
			}
			else
			{
				isGroups = true;
				smallContactsList.add(new Pair<String, String>("à, a, d", ""));
				smallContactsList.add(new Pair<String, String>("é, a, h", ""));
				smallContactsList.add(new Pair<String, String>("iii, a, n", ""));
				smallContactsList.add(new Pair<String, String>("ó, a, t", ""));
				smallContactsList.add(new Pair<String, String>("u, a, z", ""));
				//smallContactsList.add(new Pair<String, String>("Voltar atrás", ""));
				
				Collections.sort(a_dContactsList, new pairComparator());
				Collections.sort(e_hContactsList, new pairComparator());
				Collections.sort(i_nContactsList, new pairComparator());
				Collections.sort(o_tContactsList, new pairComparator());
				Collections.sort(u_zContactsList, new pairComparator());
				/*a_dContactsList.add(new Pair<String, String>("Voltar atrás", ""));
				e_hContactsList.add(new Pair<String, String>("Voltar atrás", ""));
				i_nContactsList.add(new Pair<String, String>("Voltar atrás", ""));
				o_tContactsList.add(new Pair<String, String>("Voltar atrás", ""));
				u_zContactsList.add(new Pair<String, String>("Voltar atrás", ""));*/
				
			}
		}
		people.close();
	}
	
	
	private static void insertInAbcdGroups(String contact, String phoneNumber)
	{
		String a_d= "abcd";
		String e_h= "efgh";
		String i_n= "ijklmn";
		String o_t= "opqrst";
		String u_z= "uvwxyz";
		char contactInitial = contact.charAt(0);
		String initial = Character.toString(contactInitial).toLowerCase();
		if (a_d.contains(initial)) a_dContactsList.add(new Pair<String, String>(contact, phoneNumber));
		else if (e_h.contains(initial)) e_hContactsList.add(new Pair<String, String>(contact, phoneNumber));
		else if (i_n.contains(initial)) i_nContactsList.add(new Pair<String, String>(contact, phoneNumber));
		else if (o_t.contains(initial)) o_tContactsList.add(new Pair<String, String>(contact, phoneNumber));
		else if (u_z.contains(initial)) u_zContactsList.add(new Pair<String, String>(contact, phoneNumber));
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
	
	public static ArrayList<Pair<String, String>> getSmallContactsList()
	{
		return smallContactsList;
	}
	
	public static ArrayList<Pair<String, String>> getPriorityContactsList()
	{
		return priorityContactsList;
	}
	
	public static ArrayList<Pair<String, String>> geta_dContactsList()
	{
		return a_dContactsList;
	}
	
	public static ArrayList<Pair<String, String>> gete_hContactsList()
	{
		return e_hContactsList;
	}
	
	public static ArrayList<Pair<String, String>> geti_nContactsList()
	{
		return i_nContactsList;
	}
	
	public static ArrayList<Pair<String, String>> geto_tContactsList()
	{
		return o_tContactsList;
	}
	
	public static ArrayList<Pair<String, String>> getu_zContactsList()
	{
		return u_zContactsList;
	}
	
	public static int getNumberOfContacts()
	{
		return numberOfContacts;
	}

    public static class pairComparator implements Comparator<Pair<String, String>> {
	    public int compare(Pair<String, String> name1, Pair <String, String> name2) {
	        return name1.first.compareTo(name2.first);
	    }
	}
    
    public static String getFeminine(int count)
    {
    	switch(count)
    	{
    	case 1:
    		return "uma";
    	case 2:
    		return "duas";
    	}
    	
    	return String.valueOf(count);
    }
    
    /* SMS Manager*/
    public static void configSMSManager(Context context)
    {
    	mSMSManager = new SMSManager(context);
    }
    
    /* Calls Manager*/
    public static void configCallsManager(Context context)
    {
    	mCallsManager = new CallsManager(context);
    }
    
    /* Cursor Utilities */
    public static Long getLong(Cursor c, String col) {
        return c.getLong(c.getColumnIndex(col));
    }

    public static String getString(Cursor c, String col) {
        return c.getString(c.getColumnIndex(col));
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
