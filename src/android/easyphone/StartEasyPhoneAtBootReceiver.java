package android.easyphone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class StartEasyPhoneAtBootReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) 
	{
		Log.v(EasyPhoneActivity.EASYPHONE_TAG, "StartEasyPhoneAtBootReceiver.onReceive()");
		
		if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
			Intent i = new Intent(context, easyphone.class);  
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
	}
	
	
}
