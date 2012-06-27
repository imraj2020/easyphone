package android.easyphone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ScreenReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) 
	{
		if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) 
		{
            // play screen off sound
			if(easyphone.mTTS != null) easyphone.mTTS.playEarcon("screenoff", easyphone.mTTS.QUEUE_FLUSH, null);
        
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) 
        {
            // play screen on sound
        	if(easyphone.mTTS != null) easyphone.mTTS.playEarcon("screenon", easyphone.mTTS.QUEUE_FLUSH, null);
        }
	}

}
