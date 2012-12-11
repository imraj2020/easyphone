package android.easyphone.Calls;

import java.util.Date;

public class Call implements Comparable<Call> {
    public String number;
    public String name;
    public Date date;

    public int compareTo(Call another) {
        return date.compareTo(another.date);
    }
}
