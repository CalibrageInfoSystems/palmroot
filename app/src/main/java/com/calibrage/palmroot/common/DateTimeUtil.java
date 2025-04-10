package com.calibrage.palmroot.common;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;



public class   DateTimeUtil {
    private static final String TAG = "DateUtils";
    public static final String DATE_FORMAT_1 = "hh:mm a";
    public static final String DATE_FORMAT_2 = "h:mm a";
    public static final String DATE_FORMAT_3 = "yyyy-MM-dd";
    public static final String DATE_FORMAT_4 = "dd-MMMM-yyyy";
    public static final String DATE_FORMAT_5 = "dd MMMM yyyy";
    public static final String DATE_FORMAT_6 = "dd MMMM yyyy zzzz";
    public static final String DATE_FORMAT_7 = "EEE, MMM d, ''yy";
    public static final String DATE_FORMAT_8 = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT_9 = "h:mm a dd MMMM yyyy";
    public static final String DATE_FORMAT_10 = "K:mm a, z";
    public static final String DATE_FORMAT_11 = "hh 'o''clock' a, zzzz";
    public static final String DATE_FORMAT_12 = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String DATE_FORMAT_13 = "E, dd MMM yyyy HH:mm:ss z";
    public static final String DATE_FORMAT_14 = "yyyy.MM.dd G 'at' HH:mm:ss z";
    public static final String DATE_FORMAT_15 = "yyyyy.MMMMM.dd GGG hh:mm aaa";
    public static final String DATE_FORMAT_16 = "EEE, d MMM yyyy HH:mm:ss Z";
    public static final String DATE_FORMAT_17 = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    public static final String DATE_FORMAT_18 = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
    public static final String DATE_FORMAT_19 = "dd/MM/yyyyhh:mm:ss";
    public static final String DATE_FORMAT_20 = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String DATE_FORMAT_21 = "dd-MM-yyyy";
    public static final String DATE_FORMAT_22 = "dd/MM/yyyy";
    public static final String DATE_FORMAT_23 = "yyyy";

    public static Date getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_19);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        Date today = new Date(System.currentTimeMillis());



//        return dateFormat.format(today);
       return today ;
    }

    public static Date getDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_4);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date today = Calendar.getInstance().getTime();
//        return dateFormat.format(today);
        return today;
    }

    public static String getCurrentTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_1);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date today = Calendar.getInstance().getTime();
        return dateFormat.format(today);
    }

    /**
     * @param time        in milliseconds (Timestamp)
     * @param mDateFormat SimpleDateFormat
     * @return
     */
    public static String getDateTimeFromTimeStamp(Long time, String mDateFormat) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(mDateFormat);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date dateTime = new Date(time);
        return dateFormat.format(dateTime);
    }

    /**
     * Get Timestamp from date and time
     *
     * @param mDateTime   datetime String
     * @param mDateFormat Date Format
     * @return
     * @throws ParseException
     */
    public static long getTimeStampFromDateTime(String mDateTime, String mDateFormat) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat(mDateFormat);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = dateFormat.parse(mDateTime);
        return date.getTime();
    }

    /**
     * Return  datetime String from date object
     *
     * @param mDateFormat format of date
     * @param date        date object that you want to parse
     * @return
     */
    public static String formatDateTimeFromDate(String mDateFormat, Date date) {
        if (date == null) {
            return null;
        }
        return DateFormat.format(mDateFormat, date).toString();
    }

    /**
     * Convert one date format string  to another date format string in android
     *
     * @param inputDateFormat  Input SimpleDateFormat
     * @param outputDateFormat Output SimpleDateFormat
     * @param inputDate        input Date String
     * @return
     * @throws ParseException
     */

    public static String formatDateFromDateString(String inputDateFormat, String outputDateFormat, String inputDate) throws ParseException {
        Date mParsedDate;
        String mOutputDateString;
        SimpleDateFormat mInputDateFormat = new SimpleDateFormat(inputDateFormat, java.util.Locale.getDefault());
        SimpleDateFormat mOutputDateFormat = new SimpleDateFormat(outputDateFormat, java.util.Locale.getDefault());
        mParsedDate = mInputDateFormat.parse(inputDate);
        mOutputDateString = mOutputDateFormat.format(mParsedDate);
        return mOutputDateString;

    }




    public static String printDifference(Date startDate, Date endDate) {
        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

        System.out.println("startDate : " + startDate);
        System.out.println("endDate : " + endDate);
        System.out.println("different : " + different);

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        System.out.printf(
                "%d days, %d hours, %d minutes, %d seconds%n",
                elapsedDays, elapsedHours, elapsedMinutes, elapsedSeconds);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(": ");
        if (elapsedDays > 0) {
            stringBuilder.append(elapsedDays + " Days, ");
        }
        if (elapsedHours >= 0) {
            stringBuilder.append(elapsedHours + " Hours, ");
        }
        if (elapsedMinutes > 0) {
            stringBuilder.append(elapsedMinutes + " Minutes ");
        }
        stringBuilder.append("Ago ");

//        if(elapsedSeconds > 0)
//        {
//            stringBuilder.append(elapsedSeconds+" Seconds");
//        }
        Log.d(TAG, "---  analysis ----- Date :" + stringBuilder.toString());
        return stringBuilder.toString();

    }

    public static String onGetCurrentDate(Context context) {
    // Date date=   new Date(System.currentTimeMillis() - (1* 40000));
       Date date = Calendar.getInstance().getTime();

       // Log.d(TAG, "onGetCurrentDate :" + date2  +"========"+ date);
        java.text.DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_20);
        String currentDate = dateFormat.format(date);
        Log.d(TAG, "onGetCurrentDate :" + currentDate);
        return currentDate;
    }
    public static String onGetCurrentDateForDB(Context context) {
    Date date= Calendar.getInstance().getTime();
   //Date date=   new Date(System.currentTimeMillis() - (1* 40000));

        java.text.DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_20);
        String currentDate = dateFormat.format(date);
        Log.d(TAG, "onGetCurrentDateDB :" + currentDate);
        return currentDate;
    }
    public static String onGetCurrentDateForDB2(Context context) {

 Date date = Calendar.getInstance().getTime();
        java.text.DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_3);
        String currentDate = dateFormat.format(date);
        Log.d(TAG, "onGetCurrentDate :" + currentDate);
        return currentDate;
    }

    public static Date stringTodate2(String datestring) {
        Date date = null;
        String dtStart = datestring;
        Log.d(TAG, "mahesh2:");
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT_22);
        try {
            date = format.parse(dtStart);
            System.out.println(date);
            Log.d(TAG, "mahesh2:" + date.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "mahesh2:");
        return date;
    }
    public static Date stringTodate(String datestring) {
        Date date = null;
        String dtStart = datestring;
        Log.d(TAG, "mahesh:");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            date = format.parse(dtStart);
            System.out.println(date);
            Log.d(TAG, "mahesh:" + date.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "mahesh:");
        return date;
    }
    public static Date stringTodate_new(String datestring) {
        Date finaldate=null;
        Date date = null;
        String dtStart = datestring;
        Log.d(TAG, "mahesh:");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
       String datenew= formatDateTimeUi(datestring);
        try {

            date = format.parse(datenew);

            System.out.println(date);
            Log.d(TAG, "mahesh:" + date.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "mahesh:");
        return date;
    }
    public static String formatDateTimeUi(String fromStr) {
        String date = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            Date newDate = format.parse(fromStr);
            format = new SimpleDateFormat("yyyy-MM-dd");
            date = format.format(newDate);
            return date;
        } catch (Exception e) {
            return date;
        }
    }
    public static String DateToString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }
    public static String DateToStringDB(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_20);
        return sdf.format(date);
    }
}
