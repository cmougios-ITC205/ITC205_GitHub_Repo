import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Calendar {

    private static Calendar thisInstance;
    private static java.util.Calendar javaCalendar;


    private Calendar() {
        javaCalendar = java.util.Calendar.getInstance();
    }

    public static Calendar getInstance() {
        if (thisInstance == null) {
            thisInstance = new Calendar();
        }
        return thisInstance;
    }

    public void incrementDate(int days) {
        javaCalendar.add(java.util.Calendar.DATE, days);
    }

    public synchronized void setDate(Date date) {
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
    public synchronized Date getDate() {
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

    public synchronized Date getDueDate(int loanPeriod) {
        Date currentDate = getDate();
        javaCalendar.add(java.util.Calendar.DATE, loanPeriod);
        Date dueDate = javaCalendar.getTime();
        javaCalendar.setTime(currentDate);
        return dueDate;
    }

    public synchronized long getDaysDifference(Date targetDate) {

        long differenceMilliseconds = getDate().getTime() - targetDate.getTime();
        long Diff_Days = TimeUnit.DAYS.convert(differenceMilliseconds, TimeUnit.MILLISECONDS);
        return Diff_Days;
    }

}
