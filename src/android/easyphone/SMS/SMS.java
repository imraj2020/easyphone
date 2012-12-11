package android.easyphone.SMS;

import java.util.Date;

public class SMS implements Comparable<SMS> {
    public String message;
    public String number;
    public String sender;
    public Date date;
    public String id;
    public String threadid;

    public int compareTo(SMS another) {
        return date.compareTo(another.date);
    }
}
