package android.easyphone;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.text.format.Time;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class easyphone extends EasyPhoneActivity implements OnInitListener{
	static public CallControl callControl = null;
	static public TextToSpeech mTTS = null;
    private int MY_DATA_CHECK_CODE;
    private boolean mttsloaded = false;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, "EasyPhone");
        setContentView(R.layout.main);
        
        // Copy files on assets folder to ./sdcard
        CopyAssets();
        
        //Register CallControl
        registerCallReceiver();
        
        //TTS - check if it is installed
        Intent checkIntent = new Intent();
        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkIntent, MY_DATA_CHECK_CODE); //wait for activity result
       
        //Get Contacts from Phone Contact List
       // Utils.getAllContacts(getApplicationContext());

		//Set Menu Options
        mMenu.setTitle((String) ((TextView)this.findViewById(R.id.TextView01)).getText());
        mMenu.addOption((String) ((TextView)this.findViewById(R.id.TextView02)).getText());
        mMenu.addOption((String) ((TextView)this.findViewById(R.id.TextView03)).getText());
        mMenu.addOption((String) ((TextView)this.findViewById(R.id.TextView04)).getText());
        
        //Battery
        Utils.registerBatteryListener(getApplicationContext());
    }
    
    protected void selectOption(int option)
    {
    	super.selectOption(option);
    	
    	switch(option)
    	{
	    	case 0:  //Make call
	    	{
	    		Log.v(EASYPHONE_TAG, "make call");
	    		//Call
	    		Intent contactList =  new Intent(getApplicationContext(), ContactList.class);
	    		startActivity(contactList);
	    		break;
	    	}
	    	case 1: //Clock
	    	{
	    		Log.v(EASYPHONE_TAG, "clock");
	    		Time now = new Time();
	    		now.setToNow();
	    		int hour = now.hour;
	    		int minute = now.minute;
	    		easyphone.mTTS.speak("São " + hour + "horas e " + minute + "minutos", TextToSpeech.QUEUE_ADD, null);
	    		break;
	    	}
	    	case 2: //Battery
	    	{
	    		Log.v(EASYPHONE_TAG, "battery");
	    		easyphone.mTTS.speak(String.valueOf(Utils.getBatteryLevel()) + " porcento", TextToSpeech.QUEUE_ADD, null);
	    		break;
	    	}
    	}
    }
    
    @Override
    public void onDestroy()
    {
    	Utils.unregisterBatteryListener(getApplicationContext());
    	super.onDestroy();
    }
    
    /** Called when TTS reply arrive. */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	Log.v(EASYPHONE_TAG, "easyphone.onActivityResult()");
        if (requestCode == MY_DATA_CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                // success, create the TTS instance
                //tts = new TextToSpeech(this, this); //wait for TTS init
            	mTTS = new TextToSpeech(this, this); //wait for TTS init
            } else {
                // missing data, install it
                Intent installIntent = new Intent();
                installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installIntent);
            }
        }
    }
    
    /** Called when TTS is initialized. */
    public void onInit(int status) {
    	Log.v(EASYPHONE_TAG, "easyphone.onInit()");
    	if(mttsloaded) return;
    	mttsloaded = true;
    	
    	if(status == TextToSpeech.SUCCESS)
    	{
    		Locale locale = Locale.getDefault();
    		mTTS.setLanguage(locale);
    		mTTS.addEarcon("click", "/sdcard/EasyPhone/click.wav");
    		mTTS.addEarcon("back", "/sdcard/EasyPhone/invert.wav");
    		mTTS.addEarcon("startup", "/sdcard/EasyPhone/startup.wav");
    		
    		mTTS.playEarcon("startup", mTTS.QUEUE_FLUSH, null);
    	}
    	else //ERROR
    	{
    		//tts.playEarcon("error", TextToSpeech.QUEUE_FLUSH, null);
    		Toast.makeText(this, "Error: TTS not avaliable. Check your device settings.", Toast.LENGTH_LONG).show();
    	}
	}
    
    private void registerCallReceiver(){
    	Log.v(EASYPHONE_TAG, "easyphone.registerCallReceiver()");
		if(callControl != null){
			// Receiver is already registered
			return;
		}
		Log.i(EASYPHONE_TAG, "Register Receiver.........");
		IntentFilter inf = new IntentFilter("android.intent.action.PHONE_STATE");
		
		Log.i(EASYPHONE_TAG, "Call Receiver instance_New_________");
		callControl = new CallControl();
		getApplicationContext().registerReceiver(callControl, inf, "android.permission.READ_PHONE_STATE", null);
	}
    
    private void CopyAssets() {
        AssetManager assetManager = getAssets();
        String[] files = null;
        try {
            files = assetManager.list("");
        } catch (IOException e) {
            Log.e("tag", e.getMessage());
        }
        
        // Create new directory
        File dir = new File("/sdcard/EasyPhone");
        if (!dir.exists())
            dir.mkdir();
        
        for(String filename : files) {
            InputStream in = null;
            OutputStream out = null;
            try {
              in = assetManager.open(filename);
              out = new FileOutputStream("/sdcard/EasyPhone/" + filename);
              copyFile(in, out);
              in.close();
              in = null;
              out.flush();
              out.close();
              out = null;
            } catch(Exception e) {
                Log.v(EASYPHONE_TAG, e.getMessage());
            }       
        }
    }
    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
          out.write(buffer, 0, read);
        }
    }
}