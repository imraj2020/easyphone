package android.easyphone;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.easyphone.UI.easyphone;
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
    private int READ_OPTION_TIME = 3000; //ms
	
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
		Log.v(easyphone.EASYPHONE_TAG, "MenuManager.addOption() - " + option);
		mOptions.add(option);
	}
	
	public int getNumberOptions()
	{
		return mOptions.size();
	}
	
	public void clearOptions()
	{
		mOptions.clear();
	}
	
	public void setTitle(String title)
	{
		Log.v(easyphone.EASYPHONE_TAG, "MenuManager.setTitle() - " + title);
		mTitle = title;
	}
	
	public void startScanning(boolean readTitle)
	{
		Log.v(easyphone.EASYPHONE_TAG, "MenuManager.startScanning()");
		
		if(easyphone.mTTS == null) return;
		
		mIsScanning = true;
		
		//READ TITLE
	    if(readTitle) readTitle();
	    	
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
						easyphone.mTTS.speak(mOptions.get(mCurrentOption), TextToSpeech.QUEUE_FLUSH, null); //add to queue, after earcon
					}
				}, READ_TITLE_TIME, READ_OPTION_TIME);	
	}
	
	private void readTitle()
	{
		Log.v(easyphone.EASYPHONE_TAG, "MenuManager.readTitle()");
		if(easyphone.callControl.inCall) return; //no options are read when incall mode
    	easyphone.mTTS.speak(mTitle, TextToSpeech.QUEUE_FLUSH, null);
	}
	
	public int getCurrentOption()
	{
		Log.v(easyphone.EASYPHONE_TAG, "MenuManager.getCurrentOption()");
		return mCurrentOption;
	}
	
	public  boolean isScanning()
	{
		Log.v(easyphone.EASYPHONE_TAG, "MenuManager.isScanning()");
		return mIsScanning;
	}
	
	public void setReadOptionTime(int time)
	{
		READ_OPTION_TIME = time;
	}
	
	public void setReadTitleTime(int time)
	{
		READ_TITLE_TIME = time;
	}
	
	public int getReadOptionTime()
	{
		return READ_OPTION_TIME;
	}
	
	public int getReadTitleTime()
	{
		return READ_TITLE_TIME;
	}

}
