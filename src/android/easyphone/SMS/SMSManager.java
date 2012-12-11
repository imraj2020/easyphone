package android.easyphone.SMS;

import java.util.ArrayList;
import java.util.Date;

import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.easyphone.Utils;
import android.net.Uri;
import android.telephony.gsm.SmsManager;;

public class SMSManager {

    private Context mContext;

    public SMSManager(Context context) {
        mContext = context;
    }
    // intents for sms sending
    /*private PendingIntent sentPI = PendingIntent.getBroadcast(mContext, 0,
            new Intent("SMS_SENT"), 0);
    private PendingIntent deliveredPI = PendingIntent.getBroadcast(mContext, 0,
            new Intent("SMS_DELIVERED"), 0);*/

    /** Sends a sms to the specified phone number */
    /*public void sendSMSByPhoneNumber(String message, String phoneNumber) {
        SmsManager sms = SmsManager.getDefault();
        ArrayList<String> messages = sms.divideMessage(message);
        for (int i=0; i < messages.size(); i++) {
            sms.sendTextMessage(phoneNumber, null, messages.get(i), sentPI, deliveredPI);
            addSMSToSentBox(message, phoneNumber);
        }
    }*/

    /**
     * Returns a ArrayList of <Sms> with count sms where the contactId match the argument
     */
    public ArrayList<SMS> getSMS(Long contactId, String contactName) {
        ArrayList<SMS> res = new ArrayList<SMS>();

        if(null != contactId) {
            Uri mSmsQueryUri = Uri.parse("content://sms/inbox");
            String columns[] = new String[] { "_id", "thread_id", "person", "address", "body", "date", "status"};
            Cursor c = mContext.getContentResolver().query(mSmsQueryUri, columns, "person = " + contactId, null, null);

            if (c.getCount() > 0) {
                for (boolean hasData = c.moveToFirst() ; hasData ; hasData = c.moveToNext()) {
                    Date date = new Date();
                    date.setTime(Long.parseLong(Utils.getString(c ,"date")));
                    SMS sms = new SMS();
                    sms.date = date;
                    sms.number = Utils.getString(c ,"address");
                    sms.message = Utils.getString(c ,"body");
                    sms.sender = contactName;
                    res.add( sms );
                }
            }
            c.close();
        }
        return res;
    }
    
    /**
     * Returns a ArrayList of <Sms> with all received SMS
     */
    public ArrayList<SMS> getAllReceivedSMS() {
        ArrayList<SMS> res = new ArrayList<SMS>();

        
            Uri mSmsQueryUri = Uri.parse("content://sms/inbox");
            String columns[] = new String[] { "_id", "thread_id", "person", "address", "body", "date", "status"};
            Cursor c = mContext.getContentResolver().query(mSmsQueryUri, columns, null, null, null);

            if (c.getCount() > 0) {
                for (boolean hasData = c.moveToFirst() ; hasData ; hasData = c.moveToNext()) {
                    Date date = new Date();
                    date.setTime(Long.parseLong(Utils.getString(c ,"date")));
                    SMS sms = new SMS();
                    sms.date = date;
                    sms.number = Utils.getString(c ,"address");
                    sms.message = Utils.getString(c ,"body");
                    sms.sender = Utils.getString(c, "person");
                    sms.id = Utils.getString(c, "_id");
                    sms.threadid = Utils.getString(c, "thread_id");
                    res.add(sms);
                }
            }
            c.close();
        return res;
    }

    /**
     * Deletes the sms with id and threadid 
     */
    public boolean deleteReceivedSMS(String id, String threadid)
    {
    	Uri thread = Uri.parse( "content://sms");
    	int deleted = mContext.getContentResolver().delete( thread, "thread_id=? and _id=?", new String[]{threadid, id} );
    	return deleted == 0 ? false : true;
    }
    
    /**
     * Returns a ArrayList of <Sms> with all sent sms
     */
    public ArrayList<SMS> getAllSentSMS() {
        ArrayList<SMS> res = new ArrayList<SMS>();

        Uri mSmsQueryUri = Uri.parse("content://sms/sent");
        String columns[] = new String[] { "address", "body", "date", "status"};
        Cursor c = mContext.getContentResolver().query(mSmsQueryUri, columns, null, null, null);

        if (c.getCount() > 0) {
            for (boolean hasData = c.moveToFirst() ; hasData ; hasData = c.moveToNext()) {
                Date date = new Date();
                date.setTime(Long.parseLong(Utils.getString(c ,"date")));
                SMS sms = new SMS();
                sms.date = date;
                sms.number = Utils.getString(c ,"address");
                sms.message = Utils.getString(c ,"body");
                sms.sender = "Me";
                res.add( sms );

            }
        }
        c.close();

        return res;
    }

    /**
     * Returns a ArrayList of <Sms> with count sms where the contactId match the argument
     */
    /*public ArrayList<SMS> getSentSms(ArrayList<Phone> phones, ArrayList<SMS> sms) {
        ArrayList<SMS> res = new ArrayList<SMS>();

        for (Sms aSms : sms) {
            Boolean phoneMatch = false;

            for (Phone phone : phones) {
                if (phone.phoneMatch(aSms.number)) {
                    phoneMatch = true;
                    break;
                }
            }

            if (phoneMatch) {
                res.add( aSms );
            }
        }

        return res;
    }*/

    /** Adds the text of the message to the sent box */
  /*  public void addSMSToSentBox(String message, String phoneNumber) {
        ContentValues values = new ContentValues();
        values.put("address", phoneNumber);
        values.put("date", System.currentTimeMillis());
        values.put("body", message);
        mContext.getContentResolver().insert(Uri.parse("content://sms/sent"), values);
    }*/
}
