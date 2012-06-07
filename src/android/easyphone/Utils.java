package android.easyphone;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

public class Utils {
	
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

}
