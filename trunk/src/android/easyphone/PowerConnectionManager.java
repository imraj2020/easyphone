package android.easyphone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.speech.tts.TextToSpeech;
import android.util.Log;

public class PowerConnectionManager extends BroadcastReceiver  {

	private static int mBatteryLevel = -1;
	private static boolean mIsCharging = false;
	
	public PowerConnectionManager()	{}
	
	public int getBatteryLevel()
	{
		return mBatteryLevel;
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.v(easyphone.EASYPHONE_TAG, "PowerConnectionManager.batteryLevelReceiver.onReceive()");
		
		// Are we charging / charged?
		int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
		if(status != -1)
		{ //only treats valid values
			boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;
			
			//If there are changes to battery status, read aloud battery change
		    if(isCharging != mIsCharging)
		    {
		    	// Update battery changes
		    	mIsCharging = isCharging;
		    	
		    	if(mIsCharging)
		    	{
		    		if(easyphone.mTTS != null) easyphone.mTTS.speak("Cabo de bateria ligado.", TextToSpeech.QUEUE_ADD, null);
		    	}
		    	else if(!mIsCharging)
		    	{
		    		if(easyphone.mTTS != null) easyphone.mTTS.speak("Cabo de bateria desligado.", TextToSpeech.QUEUE_ADD, null);
		    	}
		    	
		    }    		
		}
	    int lastBatteryLevel = mBatteryLevel;
	    
		// Calculate battery level
        int rawlevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        int level = -1;
        if (rawlevel >= 0 && scale > 0) 
        {
        	level = (rawlevel * 100) / scale;
            mBatteryLevel = level;
        }
        
        //Battery is fully charged
        if(mIsCharging && lastBatteryLevel != mBatteryLevel && mBatteryLevel == 100)
        {
        	if(easyphone.mTTS != null) easyphone.mTTS.speak("Bateria carregada.", TextToSpeech.QUEUE_ADD, null);
        }
		
	}

}
