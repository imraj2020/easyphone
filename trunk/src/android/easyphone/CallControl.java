/*
 * Copyright (C) 2010 Prasanta Paul, http://prasanta-paul.blogspot.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.easyphone;

import java.lang.reflect.Method;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.android.internal.telephony.ITelephony;

/**
 * This class will interrupt incoming call
 * @author Prasanta Paul
 *
 */
public class CallControl extends BroadcastReceiver {

	private final String TAG = "CallControl";
	private Context context;
	private String incomingNumber;
	private ITelephony telephonyService = null;
	
	private boolean wasRinging = false;
	private boolean wasInCall = false;
	public boolean inCall = false;
	
	@Override
	public void onReceive(final Context context, Intent intent) {
		Log.v(easyphone.EASYPHONE_TAG, "CallControl.onReceive()");
		Log.v(TAG, "Call BroadCast received...");
		
		this.context = context;
		Bundle b = intent.getExtras();
		
		Log.v(TAG, "Phone State: "+ b.get(TelephonyManager.EXTRA_STATE));
		String state = b.getString(TelephonyManager.EXTRA_STATE);
		
		if(state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_RINGING))
		{ //INCOMING CALL
			// do this only when there is an incoming call
			Log.v(easyphone.EASYPHONE_TAG, "Incoming Call ...");
			incomingNumber = b.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
			
			new Handler().postDelayed(new Runnable() 
			{
				public void run() 
			    {
					//launch new dialer UI
					Log.v(easyphone.EASYPHONE_TAG, "Launching new dialer activity ...");
			        Intent intentIncomingCall = new Intent("android.intent.action.ANSWER");
			        intentIncomingCall.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			        context.startActivity(intentIncomingCall);
			        wasRinging = true;
			    }
			 }, 1000);
		}
		else if(state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_IDLE))
		{ //HANG UP CALL or REJECTED CALL
			if(wasRinging)
			{
				Log.v(easyphone.EASYPHONE_TAG, "Call ended, BROADCASTING CLOSE_INCOMINGCALL_ACTIVITY");
				Intent i = new Intent("android.easyphone.CLOSE_INCOMINGCALL_ACTIVITY");
				context.sendBroadcast(i);
				wasRinging = false;
			}
			else if(wasInCall)
			{
				Log.v(easyphone.EASYPHONE_TAG, "Call ended, BROADCASTING CLOSE_INCALL_ACTIVITY");
				Intent i = new Intent("android.easyphone.CLOSE_INCALL_ACTIVITY");
				context.sendBroadcast(i);
				wasInCall = false;
			}
			inCall = false;
		}
		else if(state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_OFFHOOK))
		{ //CALL ACCEPTED OR INCALL
			Log.v(easyphone.EASYPHONE_TAG, "Call accepted...");
	        wasRinging = false;
	        
			new Handler().postDelayed(new Runnable() 
			{
				public void run() 
			    {        
					//launch new dialer UI
					Log.v(TAG, "Launching new dialer activity ...");
			        Intent intentInCall = new Intent(CallControl.this.context, InCall.class);
			        intentInCall.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			        context.startActivity(intentInCall);
			        wasInCall = true;
			    }
			 }, 1000);
		}
		else{
			Log.v(TAG, "[No Call Handling]");
			wasRinging = false;
		}
	}

	public boolean isRinging(){
		Log.v(easyphone.EASYPHONE_TAG, "CallControl.isRinging()");
		try {
			if(telephonyService == null) getTelephonyService();
			return telephonyService.isRinging();
			
		} catch (Exception e) {
			e.printStackTrace();
			Log.d(TAG,
					"Error in accessing Telephony Manager: "+ e.toString());
			telephonyService = null;
		}
		
		return false;
	}
	
	public void cancelCall(){
		Log.v(easyphone.EASYPHONE_TAG, "CallControl.cancelCall()");
		Log.d(TAG, "Ending call... "+ incomingNumber);
		try {
			if(telephonyService == null) getTelephonyService();
			telephonyService.endCall();
			
		} catch (Exception e) {
			e.printStackTrace();
			Log.d(TAG,
					"Error in accessing Telephony Manager: "+ e.toString());
		}
	}
	
	public void answerCall()
	{
		Log.v(easyphone.EASYPHONE_TAG, "CallControl.answerCall()");
		Log.d(TAG, "Answering call... "+ incomingNumber);
		try {
			if(telephonyService == null) getTelephonyService();
			inCall = true;
			telephonyService.answerRingingCall();
			
		} catch (Exception e) {
			e.printStackTrace();
			Log.d(TAG,
					"Error in accessing Telephony Manager: "+ e.toString());
		}
	}
	
	public void makeCall(String number, Context context)
	{
		Log.v(easyphone.EASYPHONE_TAG, "CallControl.makeCall()");
		Log.d(TAG, "Dialing number... " + number);
		this.context = context;
		try {
			if(telephonyService == null) getTelephonyService();
			telephonyService.call(number);
		} catch (Exception e) {
			e.printStackTrace();
			Log.d(TAG,
					"Error in accessing Telephony Manager: "+ e.toString());
		}
	}
	
	public void silenceRinger()
	{
		Log.v(easyphone.EASYPHONE_TAG, "CallControl.silenceRinger()");
		Log.d(TAG, "Silencing ringer ... "+ incomingNumber);
		try {
			if(telephonyService == null) getTelephonyService();
			telephonyService.silenceRinger();
		} catch (Exception e) {
			e.printStackTrace();
			Log.d(TAG,
					"Error in accessing Telephony Manager: "+ e.toString());
		}
	}
	
	private void getTelephonyService() throws Exception
	{
		Log.v(easyphone.EASYPHONE_TAG, "CallControl.getTelephonyService()");
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		try
		{
			Log.v(TAG, "Get getTeleService...");
			Class c = Class.forName(tm.getClass().getName());
			Method m = c.getDeclaredMethod("getITelephony");
			m.setAccessible(true);
			telephonyService = (ITelephony) m.invoke(tm);
		}
		catch (Exception e) {
			throw(e);
		}
	}
	
	public String getIncomingNumber()
	{
		Log.v(easyphone.EASYPHONE_TAG, "CallControl.getIncomingNumber()");
		return incomingNumber;
	}
	
	public void sendSMS(){
		/*Log.i(TAG, "Number of SMS sent: "+ Status.count_of_sms_sent);
		// TODO: Send SMS on a separate Thread using Handler
		if(Status.count_of_sms_sent >= Status.max_sms_allowed){
			Log.d(TAG, "Already sent Max number of SMS...");
			return;
		}
		Log.d(TAG, "Sending SMS...");
		Status.count_of_sms_sent++;
		// TODO: send SMS to the Incoming number
		SmsManager sms = SmsManager.getDefault(); 
		sms.sendTextMessage(incomingNumber, null, Status.msg, null, null); 
		storeSMSCount();*/
	}
	
	public void storeSMSCount(){
		/*SharedPreferences spref = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = spref.edit();
		
		editor.putInt(Status.SPREF_SMS_SENT_COUNT, Status.count_of_sms_sent);
		editor.commit();*/
	}
}
