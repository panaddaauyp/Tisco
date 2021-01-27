/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package th.co.d1.digitallending.util;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtils{

//    private static final long serialVersionUID = 1L;

    public static boolean dateEmpty(Date input) {
        return input == null;
    }

    public static String getDisplayEnDate(Date date, String format) {
        if (dateEmpty(date)) {
            return null;
        }
        // format : dd/MM/yyyy
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    public static String getDisplayEnDate(Date date, String format, String noData) {
        if (dateEmpty(date)) {
            return noData;
        }
        // format : dd/MM/yyyy
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    public static String getDisplayThDate(Date date, String format) {
        if (dateEmpty(date)) {
            return null;
        }
        // format : dd/MM/yyyy
        Locale local = new Locale("th", "TH");
        SimpleDateFormat sdf = new SimpleDateFormat(format, local);
        return sdf.format(date);
    }

    public static String getDisplayThDate(Date date, String format, String noData) {
        if (dateEmpty(date)) {
            return noData;
        }
        // format : dd/MM/yyyy
        Locale local = new Locale("th", "TH");
        SimpleDateFormat sdf = new SimpleDateFormat(format, local);
        return sdf.format(date);
    }

//   public static String getDisplayTime(Date date) {
//      // format : hh:mm:ss
//      SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
//      return sdf.format(date);
//   }
//
//   public static String getDisplaySQLDate(Date date) {
//      // format : yyyy-MM-dd
//      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//      return sdf.format(date);
//   }
//    public static String getDisplaySQLPreviousDate(String date, int paramDate) {
//        List<String> spiltDate = StringUtils.splitString(date, Constant.SEPARATOR_HYPHEN);
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//        Calendar cal = Calendar.getInstance();
//        cal.set(Integer.valueOf(spiltDate.get(0)), Integer.valueOf(spiltDate.get(1)) - 1, Integer.valueOf(spiltDate.get(2)));
//        cal.add(Calendar.DATE, paramDate);
//        return sdf.format(cal.getTime());
//    }
    public static String getLongDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return sdf.format(new Date());
    }

    public static java.sql.Date utilDateToSqlDate(Date d) {
        return d == null ? null : new java.sql.Date(d.getTime());
    }

    public static Object utilDateToSqlDate(LocalDate endDate) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public static Date addDate(Date d, int year, int month, int day, int hourOfDay, int minute, int second) {
        if (null == d) {
            d = new Date();
        }
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        c.add(Calendar.YEAR, year); // Add Year
        c.add(Calendar.MONTH, month); // Add Month
        c.add(Calendar.DATE, day); // Add Day
        c.add(Calendar.HOUR_OF_DAY, hourOfDay); // Add Hour
        c.add(Calendar.MINUTE, minute); // Add Minute
        c.add(Calendar.SECOND, second); // Add Second
        return c.getTime();
    }

    public static String convertFormatDate2Str(Date date) {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return f.format(date);
    }

    public static String convertUnixTimeToDate(long time) {
        Date date = new Date(time);
        Format format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }

    public static LocalDateTime convertUnixTimeToLocalDateTime(long time) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(time), TimeZone.getDefault().toZoneId());
    }

}
