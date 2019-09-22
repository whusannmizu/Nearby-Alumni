package com.sannmizu.nearby_alumni.chat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeObject extends BaseObject {
    public static final int TYPE = RecordAdapter.TimeMsg;
    private Date time;

    public TimeObject(Date time) {
        super(TYPE);
        this.time = time;
    }

    public String getFormatTime() {
        SimpleDateFormat sdf;
        Calendar now = Calendar.getInstance();
        Calendar date = Calendar.getInstance();
        date.setTime(time);
        if (now.get(Calendar.YEAR) != date.get(Calendar.YEAR)) {
            sdf = new SimpleDateFormat("yyyy年MM月dd日 a h:mm");
        } else if (now.get(Calendar.WEEK_OF_YEAR) != date.get(Calendar.WEEK_OF_YEAR)) {
            sdf = new SimpleDateFormat("MM月dd日 a hh:mm");
        } else if (now.get(Calendar.DAY_OF_WEEK) == date.get(Calendar.DAY_OF_WEEK)) {
            sdf = new SimpleDateFormat("a hh:mm");
        } else if (now.get(Calendar.DAY_OF_WEEK) - 1 == date.get(Calendar.DAY_OF_WEEK)) {
            sdf = new SimpleDateFormat("昨天 a hh:mm");
        } else {
            sdf = new SimpleDateFormat(getWeekDayName(date.get(Calendar.DAY_OF_WEEK)) + " a hh:mm");
        }
        return sdf.format(time);
    }

    private String getWeekDayName(int day) {
        switch(day) {
            case Calendar.MONDAY:
                return "星期一";
            case Calendar.TUESDAY:
                return "星期二";
            case Calendar.WEDNESDAY:
                return "星期三";
            case Calendar.THURSDAY:
                return "星期四";
            case Calendar.FRIDAY:
                return "星期五";
            case Calendar.SATURDAY:
                return "星期六";
            case Calendar.SUNDAY:
                return "星期日";
            default:
                return "";
        }
    }
    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
