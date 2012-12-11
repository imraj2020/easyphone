package android.easyphone.UI;

import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.easyphone.R;
import android.easyphone.Utils;
import android.easyphone.R.id;
import android.easyphone.R.layout;
import android.easyphone.SMS.SMS;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.TextView;

public class Message extends EasyPhoneActivity{
	
	private SMS mSMS = null;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, "Message", R.layout.message);
        setContentView(R.layout.message);
        
        mSMS = new SMS();
        mSMS.message = (String)getIntent().getExtras().get("message");
        mSMS.number =  (String)getIntent().getExtras().get("number");
        mSMS.date = new Date((Integer)getIntent().getExtras().get("year"), (Integer)getIntent().getExtras().get("month"), (Integer)getIntent().getExtras().get("day"),
        		(Integer)getIntent().getExtras().get("hour"), (Integer)getIntent().getExtras().get("minutes"));        
        mSMS.id = (String)getIntent().getExtras().get("id");
        mSMS.threadid = (String)getIntent().getExtras().get("threadid");
        
        // set menu title
        mMenu.setTitle((String) ((TextView)this.findViewById(R.id.tvTitle)).getText());
        
        // set menu Options
        mMenu.addOption((String) ((TextView)this.findViewById(R.id.tvBack)).getText());
        mMenu.addOption((String) ((TextView)this.findViewById(R.id.tvRead)).getText());
        mMenu.addOption((String) ((TextView)this.findViewById(R.id.tvDelete)).getText());
        mMenu.addOption((String) ((TextView)this.findViewById(R.id.tvDetails)).getText());
    }

    protected void selectOption(int option)
    {
    	super.selectOption(option);
    
    	switch(option)
    	{
    	case 0:
    		// back
    		Intent resultIntent = new Intent();
			setResult(Activity.RESULT_CANCELED, resultIntent);
    		this.finish();
    		break;
    	case 1:
    		// read
    		easyphone.mTTS.speak(mSMS.message, TextToSpeech.QUEUE_ADD, null);
    		break;
    	case 2:
    		// delete
    		if(Utils.mSMSManager.deleteReceivedSMS(mSMS.id, mSMS.threadid))
    		{
    			// message deleted
	    		easyphone.mTTS.speak("Mensagem apagada com sucesso", TextToSpeech.QUEUE_ADD, null);
	    		
	    		try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
    			Intent resultIntent2 = new Intent();
    			setResult(Activity.RESULT_OK, resultIntent2);
    			this.finish();
    		}
    		else
    		{
    			easyphone.mTTS.speak("Não foi possível apagar a mensagem", TextToSpeech.QUEUE_ADD, null);
    		}
    		break;
    	case 3:
    		easyphone.mTTS.speak("Enviada por " + Utils.getFormatedPhoneNumber(getApplicationContext(), mSMS.number) + ", na data de " + 
    				mSMS.date.getDate() + " de " + mSMS.date.getMonth() + " de " + mSMS.date.getYear() + 
    				", pelas " + mSMS.date.getHours() + " horas e " + mSMS.date.getMinutes() + " minutos",
    				TextToSpeech.QUEUE_ADD, null);
    		break;
    	}
    }
}
