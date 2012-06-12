package android.easyphone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.BatteryManager;
import android.provider.ContactsContract;
import android.util.Log;

public class Utils {
	private static int mBatteryLevel = -1;
	
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
	
	public static int getBatteryLevel()
	{
		return mBatteryLevel;
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
