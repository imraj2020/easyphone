package android.easyphone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import android.easyphone.UI.easyphone;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;
import android.util.Log;

public class MenuManager implements OnUtteranceCompletedListener {
	//Used to manage menu options and scanning
	private String mTitle;
    private ArrayList<String> mOptions = null;
    private int mCurrentOption = -1;
    private int mCurrentCycle = 1;
    private boolean mIsScanning = false;
    private final int NCYCLES = 2;
    private int READ_TITLE_TIME = 0; //ms
    private int READ_OPTION_TIME = 2000; //ms
    private HashMap<String, String> mTitleParams = null;
    private HashMap<String, String> mOptionParams = null;
    private Timer t = null;
	
	public MenuManager() 
	{
		mOptions = new ArrayList<String>();
		
		// set utterance callback
		if(easyphone.mTTS != null)
		{
			setUtteranceCallback();
		}
		
		mTitleParams = new HashMap<String, String>();
		mTitleParams.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "title");
		
		mOptionParams = new HashMap<String, String>();
		mOptionParams.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "option");
	}
	
	public MenuManager(int titleInterval, int optionInterval) 
	{
		READ_TITLE_TIME = titleInterval;
		READ_OPTION_TIME = optionInterval;
		
		mOptions = new ArrayList<String>();
		
		// set utterance callback
		if(easyphone.mTTS != null)
		{
			setUtteranceCallback();
		}
		
		mTitleParams = new HashMap<String, String>();
		mTitleParams.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "title");
		
		mOptionParams = new HashMap<String, String>();
		mOptionParams.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "option");
	}
	
	/* MANAGE OPTIONS */
	
	// add option
	public void addOption(String option)
	{
		Log.v(easyphone.EASYPHONE_TAG, "MenuManager.addOption() - " + option);
		mOptions.add(option);
	}
	
	// get number of option in menu
	public int getNumberOptions()
	{
		return mOptions.size();
	}
	
	// remove all options from menu
	public void clearOptions()
	{
		mOptions.clear();
	}
	
	// set title of menu
	public void setTitle(String title)
	{
		Log.v(easyphone.EASYPHONE_TAG, "MenuManager.setTitle() - " + title);
		mTitle = title;
	}
	
	public int getCurrentOption()
	{
		Log.v(easyphone.EASYPHONE_TAG, "MenuManager.getCurrentOption()");
		return mCurrentOption;
	}
	
	
	/*
	 * MANAGE MENU READING BEHAVIOR 
	 */
	
	// start reading menu
	public void startScanning(boolean readTitle)
	{
		Log.v(easyphone.EASYPHONE_TAG, "MenuManager.startScanning()");
		
		if(easyphone.mTTS == null) return;
		
		mIsScanning = true;
		setUtteranceCallback();
		
		// read title
	    if(readTitle) readTitle();
	    else readNextOption();
	}
	
	private void readTitle()
	{
		Log.v(easyphone.EASYPHONE_TAG, "MenuManager.readTitle()");
		if(easyphone.callControl.inCall) return; //no options are read when incall mode
		
    	easyphone.mTTS.speak(mTitle, TextToSpeech.QUEUE_FLUSH, mTitleParams);
	}
	
	private void readNextOption()
	{
		Log.v(easyphone.EASYPHONE_TAG, "readNextOption()");
		if(mOptions.size() == 0 || mIsScanning == false) 
		{ 
			Log.v(easyphone.EASYPHONE_TAG, "scanning ended optionSize: " + mOptions.size() + " flag: " + mIsScanning); 
			return;
		}
		
		//if last option and last cycle, then stop timer
		if(mCurrentCycle == NCYCLES && mCurrentOption == mOptions.size() - 1)
		{
			Log.v(easyphone.EASYPHONE_TAG, "readNextOption() - last option & last cycle");
			stopScanning();
			return;
		}
		
		if(easyphone.callControl.inCall) return; //no options are read when incall mode
		
		//next option
		mCurrentOption++;
		if(mCurrentOption > 0 && mCurrentOption % mOptions.size() == 0)
		{
			mCurrentOption = 0;
			mCurrentCycle++;
		}
		
		//read current options
		easyphone.mTTS.speak(mOptions.get(mCurrentOption), TextToSpeech.QUEUE_FLUSH, mOptionParams); //add to queue, after earcon
	}
	
	/* TTS Callback */
	public void setUtteranceCallback()
	{
		easyphone.mTTS.setOnUtteranceCompletedListener(this);
	}
	
    public void onUtteranceCompleted(String uttID) 
    {
    	// title
    	if(uttID.equalsIgnoreCase("title"))
    	{
    		//finish reading title
    		Log.v(easyphone.EASYPHONE_TAG, "finish reading title, time waiting: " + READ_TITLE_TIME);
    		t = new Timer();
    		t.schedule(new TimerTask() {
				
				@Override
				public void run() 
				{
					readNextOption();
				}
			}, READ_TITLE_TIME);
    	}
    	// menu option
    	else if(uttID.equalsIgnoreCase("option"))
    	{
    		// finish reading option
    		Log.v(easyphone.EASYPHONE_TAG, "finish reading option, time waiting: " + READ_OPTION_TIME);
    		t = new Timer();
    		t.schedule(new TimerTask() {
				
				@Override
				public void run() 
				{
					readNextOption();
				}
			}, READ_OPTION_TIME);
    	}
    	
    }
    
	public void stopScanning()
	{
		Log.v(easyphone.EASYPHONE_TAG, "MenuManager.stopScanning()");
		easyphone.mTTS.stop();
		if( t != null) t.cancel();
		mIsScanning = false;
		mCurrentOption = -1;
		mCurrentCycle = 1;
	}
	
	public  boolean isScanning()
	{
		Log.v(easyphone.EASYPHONE_TAG, "MenuManager.isScanning()");
		return mIsScanning;
	}
	
	/* MANAGE MENU READING OPTIONS */
	
	// config option time after reading
	public void setReadOptionTime(int time)
	{
		READ_OPTION_TIME = time;
	}
	
	// config title time after reading
	public void setReadTitleTime(int time)
	{
		READ_TITLE_TIME = time;
	}
	
	// get option time after reading
	public int getReadOptionTime()
	{
		return READ_OPTION_TIME;
	}
	
	// get title time after reading
	public int getReadTitleTime()
	{
		return READ_TITLE_TIME;
	}

}
