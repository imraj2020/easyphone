package android.easyphone.Calls;

import java.util.ArrayList;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.easyphone.Utils;
import android.net.Uri;
import android.provider.CallLog;

public class CallsManager {

    private Context mContext;

    public CallsManager(Context context) {
        mContext = context;
    }
    
    /*
     * Returns all new missed calls
     */
    public ArrayList<Call> getMissedCalls()
    {
    	ArrayList<Call> result = new ArrayList<Call>();
    	
    	// columns
    	String[] projection = { CallLog.Calls.CACHED_NAME, CallLog.Calls.NUMBER, CallLog.Calls.TYPE, CallLog.Calls.DATE,
    			CallLog.Calls.NEW};
    	// where
        String where = CallLog.Calls.TYPE+"="+CallLog.Calls.MISSED_TYPE+" AND "+CallLog.Calls.NEW+"!=0";
        //sort
        String sort = CallLog.Calls.DATE + " DESC";
        //query
        Cursor c = mContext.getContentResolver().query(CallLog.Calls.CONTENT_URI, projection, where, null, sort);
        c.moveToFirst();    
        
        if (c.getCount() > 0) {
            for (boolean hasData = c.moveToFirst() ; hasData ; hasData = c.moveToNext()) 
            {
            	Date date = new Date();
                date.setTime(Long.parseLong(Utils.getString(c ,"date")));
                String number = Utils.getString(c , CallLog.Calls.NUMBER);
                String person = Utils.getString(c, CallLog.Calls.CACHED_NAME);
                
                Call call = new Call();
                call.date = date;
                call.name = person;
                call.number = number;
                result.add(call);
            }
        }
    	
    	return result;
    }
    
    /**
     * Clear new missed calls 
     */
    public boolean clearMissedCalls()
    {
    	//uri
    	Uri calls = CallLog.Calls.CONTENT_URI;
    	// where
        String where = CallLog.Calls.TYPE+"="+CallLog.Calls.MISSED_TYPE+" AND "+CallLog.Calls.NEW+"!=0";
        //values
        ContentValues values = new ContentValues();
        values.put(CallLog.Calls.NEW, "0");
        //query
    	int updated = mContext.getContentResolver().update(calls, values, where, null);
    	
    	return updated == 0 ? false : true;
    }
}
