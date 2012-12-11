package android.easyphone;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.easyphone.SMS.SMS;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MessageList extends EasyPhoneActivity{
	
	private final int NMESSAGES = 4; //MAX NUMBER -1 OF DISPLAYED MESSAGES ON PRIORITY MODE 
	private ArrayList<SMS> mSMS = null; 
	private boolean mOtherMessages = false;
	private String mListType = "priority";
	private boolean mUpdate = false;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, "MessageList", R.layout.messagelist);
        setContentView(R.layout.messagelist);
        
        // adjust menu reading time in order to read phone numbers
        if(mMenu.getReadOptionTime() < 7000)
        {
        	mMenu.setReadOptionTime(7000);
        }
        
        // get list type 
        mListType = (String) getIntent().getExtras().get("messageListType");
        
        // set menu title
        mMenu.setTitle((String) ((TextView)this.findViewById(R.id.tvTitle)).getText());
        
        updateMenuOptions();
    }

    private void updateMenuOptions()
    {
    	mMenu.clearOptions();
    	
    	// set menu Options
        int optionCount = 0;
        
        // add back option
        optionCount++;
        TextView backView = new TextView(getApplicationContext());
        backView.setId(optionCount);
        backView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        backView.setText(optionCount + ", Voltar atr�s");
		LinearLayout layout = (LinearLayout) this.findViewById(R.id.LinearLayoutId01);
		layout.addView(backView);
		mMenu.addOption((String) ((TextView)this.findViewById(optionCount)).getText());
        
		// get all received sms ordered by date
        mSMS = Utils.mSMSManager.getAllReceivedSMS();
        
        int maxOptions = mSMS.size() + 1;
        if(!mListType.equalsIgnoreCase("all")) maxOptions = NMESSAGES; // if priority only add NMESSAGES items
        else
        {
        	// only present the remaining messages
        	for(int i=0; i < NMESSAGES - 1; i++)
        	{
        		if(mSMS.size() > 0) mSMS.remove(0);
        	}
        	
        	maxOptions = mSMS.size() + 1;
        }
        
        for(int i = 0; i < mSMS.size() && optionCount < maxOptions; i++)
        {
        	optionCount++;
        	String name = Utils.getFormatedPhoneNumber(getApplicationContext(), mSMS.get(i).number);
        	mMenu.addOption(optionCount + ", " + name + ", enviada a " + mSMS.get(i).date.getDate() + " do " + mSMS.get(i).date.getMonth());
        }
        
        if(mSMS.size() >= NMESSAGES && !mListType.equalsIgnoreCase("all"))
        {
        	// add option: see all messages
        	mOtherMessages = true;
        	optionCount++;
        	mMenu.addOption(optionCount + ", Outras mensagens");
        }
    }
    
    protected void selectOption(int option)
    {
    	super.selectOption(option);
    	
    	if(option == 0)
    	{
    		// back
    		Intent resultIntent = new Intent();
    		if(mUpdate)
				setResult(Activity.RESULT_OK, resultIntent);
    		else
    			setResult(Activity.RESULT_CANCELED, resultIntent);
    		this.finish();
    	}
    	else if(mOtherMessages)
    	{
    		// check if selected option is see all message
    		if(option == mMenu.getNumberOptions() - 1)
    		{
    			// open other messages
    			Intent messageList =  new Intent(getApplicationContext(), MessageList.class);
    			messageList.putExtra("messageListType", "all");
    			mReadMenu = false;
	    		startActivityForResult(messageList, 1);	
    		}
    		else
    		{
    			// open message
    			SMS sms = mSMS.get(option - 1);
    			Intent message =  new Intent(getApplicationContext(), Message.class);
    			message.putExtra("message", sms.message);
    			message.putExtra("number", sms.number);
    			message.putExtra("day", sms.date.getDate());
    			message.putExtra("month", sms.date.getMonth());
    			message.putExtra("year", sms.date.getYear());
    			message.putExtra("hour", sms.date.getHours());
    			message.putExtra("minutes", sms.date.getMinutes());
    			message.putExtra("id", sms.id);
    			message.putExtra("threadid", sms.threadid);
    			mReadMenu = false;
    			startActivityForResult(message, 0);
    		}
    	}
    	else
    	{
    		// open message
    		SMS sms = mSMS.get(option - 1);
			Intent message =  new Intent(getApplicationContext(), Message.class);
			message.putExtra("message", sms.message);
			message.putExtra("number", sms.number);
			message.putExtra("day", sms.date.getDate());
			message.putExtra("month", sms.date.getMonth());
			message.putExtra("year", sms.date.getYear());
			message.putExtra("hour", sms.date.getHours());
			message.putExtra("minutes", sms.date.getMinutes());
			message.putExtra("id", sms.id);
			message.putExtra("threadid", sms.threadid);
			mReadMenu = false;
    		startActivityForResult(message, 0);
    	}
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	
    	if(requestCode == 0)
    	{
	    	if(resultCode == RESULT_OK)
	    	{
	    		//message deleted
	    		mUpdate = true;
	    		
	    		// update message list
	    		updateMenuOptions();
	    	}
    	}
    	else if(requestCode == 1)
    	{
    		if(resultCode == RESULT_OK)
	    	{
    			
    			// update message list
	    		updateMenuOptions();
	    	}
    	}
    	mMenu.startScanning(true);;
    }
    
}