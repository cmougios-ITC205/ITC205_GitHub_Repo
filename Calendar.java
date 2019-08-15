import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Calendar {

    private static Calendar thisInstance;
    private static java.util.Calendar javaCalendar;


    private Calendar() {
        javaCalendar = java.util.Calendar.getInstance();
    }

    public static Calendar INSTANCE() {
        if (thisInstance == null) {
            thisInstance = new Calendar();
        }
        return thisInstance;
    }

    public void incrementDate(int days) {
        javaCalendar.add(java.util.Calendar.DATE, days);
    }

    public synchronized void Set_dATE(Date date) {
        try {
            javaCalendar.setTime(date);
            javaCalendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
            javaCalendar.set(java.util.Calendar.MINUTE, 0);
            javaCalendar.set(java.util.Calendar.SECOND, 0);
            javaCalendar.set(java.util.Calendar.MILLISECOND, 0);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public synchronized Date Date() {
        try {
            javaCalendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
            javaCalendar.set(java.util.Calendar.MINUTE, 0);
            javaCalendar.set(java.util.Calendar.SECOND, 0);
            javaCalendar.set(java.util.Calendar.MILLISECOND, 0);
            return javaCalendar.getTime();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized Date Due_Date(int loanPeriod) {
        Date NoW = Date();
        javaCalendar.add(java.util.Calendar.DATE, loanPeriod);
        Date DuEdAtE = javaCalendar.getTime();
        javaCalendar.setTime(NoW);
        return DuEdAtE;
    }

    public synchronized long Get_Days_Difference(Date targetDate) {

        long Diff_Millis = Date().getTime() - targetDate.getTime();
        long Diff_Days = TimeUnit.DAYS.convert(Diff_Millis, TimeUnit.MILLISECONDS);
        return Diff_Days;
    }

}
