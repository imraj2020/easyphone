package android.easyphone;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.speech.tts.TextToSpeech;
import android.util.Log;

public class MenuManager {
	//Used to manage menu options and scanning
	private String mTitle;
    private ArrayList<String> mOptions = null;
    private Timer mTimer = null;
    private int mCurrentOption = -1;
    private int mCurrentCycle = 1;
    private boolean mIsScanning = false;
    private final int NCYCLES = 2;
    private int READ_TITLE_TIME = 2000; //ms
    private int READ_OPTION_TIME = 5000; //ms
	
	public MenuManager() 
	{
		mOptions = new ArrayList<String>();
		mTimer = new Timer();
	}
	
	public MenuManager(int titleInterval, int optionInterval) 
	{
		mOptions = new ArrayList<String>();
		mTimer = new Timer();
		
		READ_TITLE_TIME = titleInterval;
		READ_OPTION_TIME = optionInterval;
	}
	
	public void addOption(String option)
	{
		mOptions.add(option);
	}
	
	public void setTitle(String title)
	{
		mTitle = title;
	}
	
	public void startScanning(boolean readMenu)
	{
		Log.v(easyphone.EASYPHONE_TAG, "ContactList.startScanning()");
		
		if(easyphone.mTTS == null) return;
		
		mIsScanning = true;
		
		//READ TITLE
	    if(readMenu) readTitle();
	    	
	    //READ OPTIONS
	    scanOptions();
	}
	
	public void stopScanning()
	{
		Log.v(easyphone.EASYPHONE_TAG, "MenuManager.stopScanning()");
		mIsScanning = false;
    	mTimer.cancel();
		mCurrentOption = -1;
		mCurrentCycle = 1;
		mTimer = new Timer();
	}
	
	private void scanOptions()
	{
		Log.v(easyphone.EASYPHONE_TAG, "MenuManager.scanOptions()");
    	if(mOptions.size() == 0) return;
		mTimer.scheduleAtFixedRate(new TimerTask() {
					@Override
					public void run() {
						//if last option and last cycle stop timer
						if(mCurrentCycle == NCYCLES && mCurrentOption == mOptions.size() - 1)
						{
							stopScanning();
						}
						
						//next option
						mCurrentOption++;
						if(mCurrentOption > 0 && mCurrentOption % mOptions.size() == 0)
						{
							mCurrentOption = 0;
							mCurrentCycle++;
						}
						
						//read current options
						easyphone.mTTS.speak(mOptions.get(mCurrentOption), TextToSpeech.QUEUE_FLUSH, null);
					}
				}, READ_TITLE_TIME, READ_OPTION_TIME);	
	}
	
	private void readTitle()
	{
		Log.v(easyphone.EASYPHONE_TAG, "MenuManager.readTitle()");
    	easyphone.mTTS.speak(mTitle, TextToSpeech.QUEUE_FLUSH, null);
	}
	
	public int getCurrentOption()
	{
		return mCurrentOption;
	}
	
	public  boolean isScanning()
	{
		return mIsScanning;
	}

}
