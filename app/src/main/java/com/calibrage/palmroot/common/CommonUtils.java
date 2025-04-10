 package com.calibrage.palmroot.common;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.calibrage.palmroot.cloudhelper.ApplicationThread;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class   CommonUtils {

    public static final int REQUEST_CAM_PERMISSIONS = 1;
    public static final int FROM_CAMERA = 1;
    public static final int FROM_GALLERY = 2;
    public static ArrayList<String> tableNames = new ArrayList<>();
    /**
     * validating email pattern
     */
    public static final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
    );

    public static final Pattern VEHICLE_NUMBER_PATTERN = Pattern.compile("^[A-Z]{2}[ -][0-9]{1,2}(?: [A-Z])?(?: [A-Z]*)? [0-9]{4}$");

    public static final int PERMISSION_CODE = 100;
    private static final String PASSWORD_PATTERN = "((?=.*\\d)(?=.*[a-zA-Z]).{6,20})";
    public static FragmentActivity currentActivity;
    public static DecimalFormat twoDForm = new DecimalFormat("#.##");
    public static boolean isFarmerDetailsAdded = false;
    public static boolean isPlotDetailsAdded = false;
    public static String[] stateCode = {"AN", "AP", "AR", "AS", "BR", "CG", "CH", "DD", "DL", "DN", "GA", "GJ", "HR", "HP", "JH", "JK", "KA", "KL", "LD", "MH", "ML", "MN", "MP", "MZ", "NL", "OD", "PB", "PY", "RJ", "SK", "TN", "TR", "TS", "UK", "UP", "WB"};
    public static String numeric = "1234567890";
    public static String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    static Pattern pattern = null;
    static Matcher matcher;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static String LOG_TAG = CommonUtils.class.getName();

    public static boolean isValidVehicleNumber(String vehicleNumber) {
        return VEHICLE_NUMBER_PATTERN.matcher(vehicleNumber).matches();
    }

    /**
     * Checking the given emailId is valid or not
     *
     * @param email --> user emailId
     * @return ---> true if valid else false
     */
    public static boolean isValidEmail(String email) {
        return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
    }

    /**
     * Validate password with regular expression
     *
     * @param password password for validation
     * @return true valid password, false invalid password
     */

    public static boolean passwordValidate(final String password, Context context) {
        /*
         * Pattern pattern = null; Matcher matcher;
         */
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);
        if (matcher.matches() == false)
            Toast.makeText(context, "Password Must contain: Minimum 6 characters and atleast one number.", Toast.LENGTH_SHORT).show();
        return matcher.matches();

    }

    /**
     * Checking the Internet connection is available or not
     *
     * @param context
     * @return true if available else false
     */
    public static boolean isNetworkAvailable(final Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null != connectivityManager) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
        }

        return false;
    }

    public static void showToast(final String message, final Context context) {
        ApplicationThread.uiPost(LOG_TAG, "toast", new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void showMyToast(final String message, final Context context) {
        ApplicationThread.uiPost(LOG_TAG, "toast", new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static String[] fMap(LinkedHashMap<String, ArrayList<String>> inputMap, String type) {
        Collection c = inputMap.values();
        Iterator itr = c.iterator();
        int size = inputMap.size() + 1;
        String[] toMap = new String[size];
        toMap[0] = "-- Select " + type + " --";
        int iCount = 1;
        while (iCount < size && itr.hasNext()) {
            toMap[iCount] = itr.next().toString();
            iCount++;
        }
        while (iCount < size && itr.hasNext()) {
            toMap[iCount] = itr.next().toString();
            iCount++;
        }
        return toMap;
    }


    public static boolean isBlueToothEnabled() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return bluetoothAdapter.isEnabled();
    }

    /**
     * Deleting a particular directory from sdcard
     *
     * @param path ---> file path
     * @return ---> true is successfully deleted else false
     */
    public static boolean deleteDirectory(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) deleteDirectory(files[i]);
                else files[i].delete();
            }
        }
        return true;
    }

    public static void hideKeyPad(Context context, EditText editField) {
        final InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editField.getWindowToken(), 0);
    }


    /**
     * Checking SD card is available or not in mobile
     *
     * @param context
     * @return --> true if available else false
     */
    public static boolean isSDcardAvailable(Context context) {
        boolean isAvailable = false;
        boolean available = Environment.getExternalStorageState().toString().equalsIgnoreCase(Environment.MEDIA_MOUNTED) || Environment.getExternalStorageState().toString().equalsIgnoreCase(Environment.MEDIA_CHECKING) ||
                Environment.getExternalStorageState().toString().equalsIgnoreCase(Environment.MEDIA_MOUNTED_READ_ONLY);
        isAvailable = available;
        return isAvailable;
    }

    public static boolean isValidMobile(String phone2, EditText tv) {
        boolean check = false;
        if (!Pattern.matches("[a-zA-Z]+", "  ")) {
            if (phone2.length() < 10 || phone2.length() > 10) {
                check = false;
                tv.setError(Html.fromHtml("<font color='red'>" + "Please specify a valid contact number" + "</font>"));
            } else {
                check = true;
            }
        } else {
            check = false;
        }
        return check;
    }

    /**
     * Checking a string contains an integer or not
     *
     * @param s --> Input string
     * @return true is string contains number else false.
     */
    public static boolean isInteger(final String s) {
        return Pattern.matches("^\\d*$", s);
    }


    private static byte[] loadFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);

        long length = file.length();
        if (length > Integer.MAX_VALUE) {
            // File is too large
        }
        byte[] bytes = new byte[(int) length];

        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
                && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += numRead;
        }

        if (offset < bytes.length) {
            throw new IOException("Could not completely read file " + file.getName());
        }

        is.close();
        return bytes;
    }

    //Converts image in filepath to base64
    public static String encodeFileToBase64Binary(File file)
            throws IOException {

        byte[] bytes = loadFile(file);

        return Base64.encodeToString(bytes, Base64.NO_WRAP);
    }

    /**
     * Getting the device information(android version, Device model,Device manufacturer)
     *
     * @return ---> Device information in a string formate
     */
    public static String getDeviceExtraInfo() {
        return "" + Build.VERSION.RELEASE + "/n" + Build.MANUFACTURER + " - " + Build.MODEL + "/n";
    }

    /**
     * Getting the current date and time with comma separated.
     *
     * @return if both are required returns date and time with comma separated else if only time required returns time else only date else default date and time.
     */

    public static String getcurrentDateTime(final String format) {
        Calendar c = Calendar.getInstance();
        String date;
        @SuppressLint("SimpleDateFormat") SimpleDateFormat Objdateformat = new SimpleDateFormat(format);
        date = Objdateformat.format(c.getTime());
        return date;
    }

    /**
     * Replacing empty spaces with %20
     *
     * @param url ---> Input url
     * @return ----> encoded url
     */
    public static String encodeURL(String url) {
        try {
            return URLEncoder.encode(url, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }

    public static String getRealPathFromURI(Uri uri, Context context) {
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    public static void getAddressFromLocation(final Context context, final double latitude, final double longitude, ApplicationThread.OnComplete onComplete) {
        try {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            String result = null;
            List<Address> addressList = geocoder.getFromLocation(
                    latitude, longitude, 1);
            if (addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0);
                StringBuilder sb = new StringBuilder();
                sb.append(address.getLocality()).append("\n");
                result = sb.toString();
                onComplete.execute(true, result, null);
            }
        } catch (IOException e) {
            Log.e("", "Unable connect to Geocoder", e);
            onComplete.execute(false, null, null);
        }
    }

    public static boolean spinnerSelect(Spinner spinner, String name, Context context) {
        if (spinner.getSelectedItemPosition() == 0) {
            Toast.makeText(context, "Please select " + name, Toast.LENGTH_SHORT).show();
            return false;
        } else
            return true;
    }

    public static boolean edittextEampty(EditText edittext, String name, Context context) {
        if (edittext.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(context, "Please enter " + name, Toast.LENGTH_SHORT).show();
            return false;
        } else
            return true;
    }

    public static boolean spinnerPositinCondition(String spinner, int minimum, int maximum, Context context) {
        if (minimum >= maximum) {
            Toast.makeText(context, "Please select " + spinner, Toast.LENGTH_SHORT).show();
            return false;
        } else
            return true;
    }

    public static String[] getGenericArrayValues(String type, int size) {
        String[] items = new String[size + 1];
        items[0] = "Select";
        for (int i = 1; i <= size; i++) {

            if (type.length() == 0) {
                if (i == size)
                    items[i] = "" + (i - 1) + "+";
                else {
                    items[i] = "" + (i - 1);
                }

            } else {
                items[i] = "" + ((i - 1) + 1980);
            }
        }
        return items;
    }

    public static String[] getGenericAfterArrayValues(int startSize, int size) {
        String[] items = new String[size - startSize];
        Log.i("", "Item size: " + items.length);
        for (int i = 0; i <= size - 1; i++) {

            if (i >= startSize) {

                if (i == size - 1)
                    items[i - startSize] = "" + (i) + "+";
                else {
                    items[i - startSize] = "" + (i);
                }

            } else {
//                items[i]="Extra";
            }
        }
        return items;
    }

    public static int getImageOrientation(String imagePath) {
        int rotate = 0;
        try {

            File imageFile = new File(imagePath);
            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rotate;
    }

    public static String[] fromMap(LinkedHashMap<String, String> inputMap, String type) {
        Collection c = inputMap.values();
        Iterator itr = c.iterator();
        int size = inputMap.size() + 1;
        String[] toMap = new String[size];
        toMap[0] = "-- Select " + type + " --";
        int iCount = 1;
        while (iCount < size && itr.hasNext()) {
            toMap[iCount] = itr.next().toString();
            iCount++;
        }
        while (iCount < size && itr.hasNext()) {
            toMap[iCount] = itr.next().toString();
            iCount++;
        }
        return toMap;
    }

    public static String[] arrayFromPair(LinkedHashMap<String, Pair> inputMap, String type) {
        Collection c = inputMap.values();
        Iterator itr = c.iterator();
        int size = inputMap.size() + 1;
        String[] toMap = new String[size];
        toMap[0] = "-- Select " + type + " --";
        int iCount = 1;
        while (iCount < size && itr.hasNext()) {
            Pair valuePair = (Pair) itr.next();
            toMap[iCount] = valuePair.second.toString();
            iCount++;
        }
        while (iCount < size && itr.hasNext()) {
            Pair valuePair = (Pair) itr.next();
            toMap[iCount] = valuePair.second.toString();
            iCount++;
        }
        return toMap;
    }

    public static List<String> listFromPair(LinkedHashMap<String, Pair> inputMap, String type) {
        String[] array = arrayFromPair(inputMap, type);
        return Arrays.asList(array);
    }

    public static double getPercentage(double n, double total) {
        double proportion;
        proportion = ((n - total) / (n + total)) * 100;
        return Math.abs(Math.round(proportion));
    }

    public static String[] removeDuplicates(LinkedHashMap<String, String> inputMap, String type) {
        Collection c = inputMap.values();
        Iterator itr = c.iterator();
        int size = inputMap.size() + 1;
        String[] toMap = new String[size];
        toMap[0] = "-- Select " + type + " --";
        int iCount = 1;
        while (iCount < size && itr.hasNext()) {
            toMap[iCount] = itr.next().toString();
            iCount++;
        }
        while (iCount < size && itr.hasNext()) {
            toMap[iCount] = itr.next().toString();
            iCount++;
        }

        List<String> stringList = new ArrayList<String>();
        for (int i = 0; i < toMap.length; i++) {
            if (!stringList.contains(toMap[i].toString()))
                stringList.add(toMap[i].toString());
        }
        toMap = stringList.toArray(new String[stringList.size()]);

        return toMap;
    }

    public static String[] fromMapAll(LinkedHashMap<String, String> inputMap, String type) {
        Collection c = inputMap.values();
        Collection cc = inputMap.keySet();
        Iterator itr = c.iterator();
        Iterator itrcc = cc.iterator();
        int size = inputMap.size() + 1;
        String[] toMap = new String[size];
        toMap[0] = "-- Select " + type + " --";
        int iCount = 1;
        while (iCount < size && itr.hasNext()) {
            toMap[iCount] = itrcc.next().toString() + "-" + itr.next().toString();
            iCount++;
        }
        while (iCount < size && itr.hasNext()) {
            toMap[iCount] = itrcc.next().toString() + "-" + itr.next().toString();
            iCount++;
        }

        return toMap;
    }

    public static String formattedTime() {
        DateFormat stringTime = new SimpleDateFormat("hh:mm a");
        Date currentDate = new Date(System.currentTimeMillis());
        return stringTime.format(currentDate);
    }

//    public  static  String[] sortStringArray(String[] sort){
//
//        String[] jobType_array = sort;
//        Arrays.sort(jobType_array);
//        jobType_array[0] = "Select";
//
//        return  jobType_array;
//
//    }

    public static int parseIntValue(String inputValue) {
        if (!TextUtils.isEmpty(inputValue) && TextUtils.isDigitsOnly(inputValue)) {
            try {
                return Integer.parseInt(inputValue);
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
        return 0;
    }

    public static String convertToDays(String inputStr) {
        int resultInt = 0;
        String resultStr = inputStr.replaceAll("\\D+", "");
        if (inputStr.contains("Week") || inputStr.contains("Weeks")) {
            resultInt = parseIntValue(resultStr) * 7;
            resultStr = String.valueOf(resultInt);
        } else if (inputStr.contains("Month") || inputStr.contains("Months")) {
            resultInt = parseIntValue(resultStr) * 30;
            resultStr = String.valueOf(resultInt);
        }
        return resultStr;
    }

    public static String convertToWeeks(String inputStr) {

        if (inputStr.equals("7")) {
            inputStr = "1 Week";
        } else if (inputStr.contains("15")) {
            inputStr = "15 Days";
        } else if (inputStr.contains("21")) {
            inputStr = "3 Weeks";
        } else if (inputStr.contains("30")) {
            inputStr = "1 Month";
        } else if (inputStr.contains("60")) {
            inputStr = "2 Months";
        } else if (inputStr.contains("90")) {
            inputStr = "3 Months";
        } else {
            inputStr = "0 Day";
        }
        return inputStr;
    }

    public static String getCleanDate(String dateToFormate) {
        if (!dateToFormate.equalsIgnoreCase("")) {
            long dateValue = Long.parseLong(dateToFormate.replaceAll("[^0-9]", ""));
            Date date = new Date(dateValue);
            SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy");
            String dateText = df2.format(date);
            return dateText;
        }
        return dateToFormate;
    }

    public static int getIndex(Set<? extends String> set, String value) {
        int result = 0;
        for (String entry : set) {
            Log.d("", "entry value " + entry + "   " + value);
            if (entry.equalsIgnoreCase(value)) {
                return result;
            }
            result++;
        }
        return -1;
    }

    public static int findIndex(LinkedHashMap<String, Pair> map, String value) {
        if (map == null) {
            return -1;
        }
        String[] array = map.keySet().toArray(new String[map.size()]);
        int result = 0;
        for (String entry : map.keySet()) {
            if (entry.equalsIgnoreCase(value)) {
                return result;
            }
            result++;
        }
        return -1;
    }

    public static int getIndexFromValue(Collection<String> set, String value) {
        int result = 0;
        for (String entry : set) {
            Log.d("", "entry value" + entry + "   " + value);
            if (entry.equals(value)) {
                return result;
            }
            result++;
        }
        return -1;
    }

    public static int getIndexFromArray(String[] arrayValues, String value) {
        int result = 0;
        for (String entry : arrayValues) {
            Log.d("", "entry value" + entry + "   " + value);
            if (entry.equalsIgnoreCase(value)) {
                return result;
            }
            result++;
        }
        return -1;
    }

    public static String getKeyFromValue(LinkedHashMap<String, String> map, String value) {
        String toReturnvalue = "";
        for (Map.Entry entry : map.entrySet()) {
            if (value.equalsIgnoreCase(entry.getValue().toString())) {
                toReturnvalue = (String) entry.getKey();
                break; //breaking because its one to one map
            }
        }
        return toReturnvalue;
    }

    public static String removeLastComma(String str) {
        if (str.length() > 0 && str.charAt(str.length() - 1) == ',') {
            str = str.substring(0, str.length() - 1);
        }
        return str;
    }

    public static String replaceNewLines(String inputStr) {

        if (null != inputStr && inputStr.length() > 0) {
            return inputStr.replaceAll("\\\\n", "");
        }
        return "";
    }

    /**
     * Converting given string into date format
     *
     * @param dateStr          ---> Given date
     * @param _dateFormatteStr ----> Given date format
     * @return ----> converted date   (if any exception occurred returns null)
     */
    public static Date convertStringToDate(String dateStr, String _dateFormatteStr) {
        Date _convertedDate = null;
        SimpleDateFormat _dateFormate = new SimpleDateFormat("" + _dateFormatteStr);
        try {
            _convertedDate = _dateFormate.parse(dateStr);
        } catch (ParseException e) {
            // TODO Auto-generated catch blockdata
            e.printStackTrace();
        }
        return _convertedDate;
    }

//    public static boolean checkOnlyPalmDataExistance(final Context context) {
//        DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
//
//        if (CommonConstants.screenFrom.equalsIgnoreCase("addReg")) {
//            if (dataAccessHandler.checkValueExistedInDatabase(Queries.getInstance().marketSurveyDataCheck(CommonConstants.FARMER_CODE))
//                    || dataAccessHandler.checkValueExistedInDatabase(Queries.getInstance().getPlotExistanceInAnyPalmDetailsQuery(CommonConstants.PLOT_CODE))) {
//                return true;
//            }
//        } else if (dataAccessHandler.checkValueExistedInDatabase(Queries.getInstance().getPlotExistanceInAnyPalmDetailsQuery(CommonConstants.PLOT_CODE))) {
//            return true;
//        }
//
//        return false;
//    }

    public static String serialNumber(int number, int stringLength) {
        int numberOfDigits = String.valueOf(number).length();
        int numberOfLeadingZeroes = stringLength - numberOfDigits;
        StringBuilder sb = new StringBuilder();
        if (numberOfLeadingZeroes > 0) {
            for (int i = 0; i < numberOfLeadingZeroes; i++) {
                sb.append("0");
            }
        }
        sb.append(number);
        return sb.toString();
    }

    public static String capitalizeFirstLetter(String original) {
        if (original == null || original.length() == 0) {
            return original;
        }
        return original.substring(0, 1).toUpperCase() + original.substring(1);
    }

    //We are calling this method to check the permission status
    public static boolean isPermissionAllowed(final Context context, final String permission) {
        //Getting the permission status
        int result = ContextCompat.checkSelfPermission(context, permission);

        //If permission is granted returning true
        if (result == PackageManager.PERMISSION_GRANTED)
            return true;

        //If permission is not granted returning false
        return false;
    }

    public static boolean areAllPermissionsAllowed(final Context context, final String[] permissions) {
        boolean isAllPermissionsGranted = false;
        for (String permission : permissions) {
            int result = ContextCompat.checkSelfPermission(context, permission);
            if (result == PackageManager.PERMISSION_GRANTED) {
                isAllPermissionsGranted = true;
            }
        }
        return isAllPermissionsGranted;
    }

    public static boolean areAllPermissionsAllowedNew(final Context context, final String[] permissions) {
        boolean isAllPermissionsGranted = true;
        for (String permission : permissions) {
            int result = ContextCompat.checkSelfPermission(context, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                return isAllPermissionsGranted = false;
            }
        }
        return isAllPermissionsGranted;
    }

    public static String getIMEInumber(final Context context) { // get IMENumber
        String deviceId;
        if (android.os.Build.VERSION.SDK_INT >= 29) {
            deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        } else {
            TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            deviceId = mTelephony.getDeviceId();
        }

//return deviceId;

//return "351558072999099"; // roja
//
return "ddfe270e3940733e";// roja
// return "d6afb709f9725b96";

        // return telephonyManager.getDeviceId();
    }

    public static boolean isEmptySpinner(final Spinner inputSpinner) {
        if (null == inputSpinner) return true;
        return inputSpinner.getSelectedItemPosition() == -1 || inputSpinner.getSelectedItemPosition() == 0;
    }

    public static double distance(double lat1, double lon1, double lat2, double lon2, char unit) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        if (unit == 'K' || unit == 'k') {
            dist = dist * 1.609344;
        } else if (unit == 'N' || unit == 'n') {
            dist = dist * 0.8684;
        } else if (unit == 'M' || unit == 'm') {
            dist = dist * 1609.344;
        }
        return Double.parseDouble(twoDForm.format(dist));
    }

    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private static double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    public static void hideSoftKeyboard(final FragmentActivity appCompatActivity) {
        if (appCompatActivity.getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) appCompatActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(appCompatActivity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    public static String getAppVersion(final Context context) {
        PackageInfo pInfo = null;
        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "version error " + e.getMessage());
        }
        return pInfo.versionName;
      }

    public static double getOnlyTwoDecimals(final Double inputValue) {
        return Math.round(100 * (inputValue)) / (double) 100;
    }

    public static Map<String, Object> jsonToMap(JSONObject json) throws JSONException {
        Map<String, Object> retMap = new HashMap<String, Object>();

        if (json != JSONObject.NULL) {
            retMap = toMap(json);
        }
        return retMap;
    }

    public static LinkedHashMap<String, Object> toMap(JSONObject object) throws JSONException {
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();

        Iterator<String> keysItr = object.keys();
        while (keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);

            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }

    public static List<Object> toList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<Object>();
        for (int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }

    public static byte[] base64ToByte(String imageString) throws Exception {
        return Base64.decode(imageString, Base64.DEFAULT);
    }

    public static List<File> deleteImages(File root, List<File> fileList) {
        File listFile[] = root.listFiles();
        if (listFile != null && listFile.length > 0) {
            for (int i = 0; i < listFile.length; i++) {
                if (listFile[i].isDirectory() && (listFile[i].getName().equalsIgnoreCase("DCIM") || listFile[i].getName().equalsIgnoreCase("Camera"))) {
                    fileList.add(listFile[i]);
                    deleteImages(listFile[i], fileList);
                } else {
                    if (listFile[i].getName().endsWith(".png")
                            || listFile[i].getName().endsWith(".jpg")
                            || listFile[i].getName().endsWith(".jpeg")
                            || listFile[i].getName().endsWith(".gif")) {
                        fileList.add(listFile[i]);
                        listFile[i].delete();
                    }
                }


            }
        }
        return fileList;
    }

    public static void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }

    public static void gzipFile(File sourceFile, String destinaton_zip_filepath, final ApplicationThread.OnComplete<String> onComplete) {

        byte[] buffer = new byte[1024];

        try {

            FileOutputStream fileOutputStream = new FileOutputStream(destinaton_zip_filepath);

            GZIPOutputStream gzipOuputStream = new GZIPOutputStream(fileOutputStream);

            FileInputStream fileInput = new FileInputStream(sourceFile);

            int bytes_read;

            while ((bytes_read = fileInput.read(buffer)) > 0) {
                gzipOuputStream.write(buffer, 0, bytes_read);
            }

            fileInput.close();

            gzipOuputStream.finish();
            gzipOuputStream.close();

            System.out.println("The file was compressed successfully!");
            onComplete.execute(true, "success", "success");
        } catch (IOException ex) {
            ex.printStackTrace();
            onComplete.execute(false, "failed", "failed");
        }
    }

    public static void createZipFile(final String path, final File file, final ApplicationThread.OnComplete<String> onComplete) {
        try {
            int BUFFER = 2048;
            BufferedInputStream origin = null;
            FileOutputStream dest = new FileOutputStream(path);

            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));

            byte data[] = new byte[BUFFER];

            Log.v("Compress", "Adding: " + file);
            FileInputStream fi = new FileInputStream(file);
            origin = new BufferedInputStream(fi, BUFFER);
            ZipEntry entry = new ZipEntry(file.getName());
            out.putNextEntry(entry);
            int count;
            while ((count = origin.read(data, 0, BUFFER)) != -1) {
                out.write(data, 0, count);
            }
            origin.close();
            out.close();
            onComplete.execute(true, "success", "success");
        } catch (Exception e) {
            e.printStackTrace();
            onComplete.execute(false, "failed", "failed");
        }
    }

    public static void profilePic(final FragmentActivity activity) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        activity.startActivityForResult(intent, FROM_CAMERA);
    }

//    public static void deleteInCompleteData(final Context context) {
//        DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
//        LinkedHashMap<String, List<String>> inCompleteRecords = CommonUtils.getZombiRecords(dataAccessHandler);
//        Log.v(LOG_TAG, "@@@@ incomplete data count " + CommonUtils.getZombiRecords(dataAccessHandler).size());
//        if (null != inCompleteRecords && !inCompleteRecords.isEmpty()) {
//            Set<String> tableNames = inCompleteRecords.keySet();
//            for (String tableName : tableNames) {
//                List<String> dataList = inCompleteRecords.get(tableName);
//                String plotIds = TextUtils.join(",", dataList);
//                if (tableName.equalsIgnoreCase(DatabaseKeys.TABLE_MARKETSURVEYDETAILS)) {
//                    String deleteQuery = String.format(Queries.getInstance().deleteInCompleteMarketSurveyData(), tableName, plotIds);
//                    Log.v(LOG_TAG, "@@@ farmercode ids to delete " + plotIds);
//                    Log.v(LOG_TAG, "@@@ farmercode ids to delete query " + deleteQuery);
//                    dataAccessHandler.executeRawQuery(deleteQuery);
//                } else {
//                    String deleteQuery = String.format(Queries.getInstance().deleteInCompleteData(), tableName, plotIds);
//                    Log.v(LOG_TAG, "@@@ plot ids to delete " + plotIds);
//                    Log.v(LOG_TAG, "@@@ plot ids to delete query " + deleteQuery);
//                    dataAccessHandler.executeRawQuery(deleteQuery);
//                }
//            }
//        }
//
//    }

//    public static LinkedHashMap<String, List<String>> getZombiRecords(final DataAccessHandler dataAccessHandler) {
//        LinkedHashMap<String, List<String>> zombiRecord = new LinkedHashMap<>();
//        List<String> transactionsData = DatabaseKeys.transactionTables;
//        transactionsData.add(DatabaseKeys.TABLE_PICTURE_REPORTING);
//        Log.v(LOG_TAG, "@@@ tables list to delete " + transactionsData);
//        for (String tableName : transactionsData) {
//            if (tableName.equalsIgnoreCase(DatabaseKeys.TABLE_MARKETSURVEYDETAILS)) {
//                List<String> zombiData = dataAccessHandler.getZombiData(String.format(Queries.getInstance().queryToGetIncompleteMarketSurveyData()));
//                if (null != zombiData && !zombiData.isEmpty()) {
//                    zombiRecord.put(tableName, zombiData);
//                }
//            } else {
//                List<String> zombiData = dataAccessHandler.getZombiData(String.format(Queries.getInstance().queryToFindJunkData(), tableName));
//                if (null != zombiData && !zombiData.isEmpty()) {
//                    zombiRecord.put(tableName, zombiData);
//                }
//            }
//        }
//        return zombiRecord;
//    }
//
//    public static void resetData() {
//        CommonConstants.totalFarmerData = new UserData();
//        CommonConstants.FARMER_CODE = "";
//        CommonConstants.PLOT_CODE = "";
//        FieldCalculatorActivity.recordedBoundries.clear();
//        FieldCalculatorActivity.firstFourCoordinates.clear();
//        CommonConstants.stateCode = "";
//        CommonConstants.districtCode = "";
//        CommonConstants.mandalCode = "";
//        CommonConstants.stateName = "";
//        CommonConstants.villageCode = "";
//        CommonConstants.districtName = "";
//        CommonConstants.mandalName = "";
//        CommonConstants.bankTypeId = "";
//        CommonConstants.castCode = "";
//        CommonConstants.villageName = "";
//        COUNT_OF_TREES = "";
//
//
//        CommonConstants.districtCodePlot = "";
//        CommonConstants.mandalCodePlot = "";
//        CommonConstants.villageCodePlot = "";
//
//        CommonConstants.prevMandalPos = 0;
//        CommonConstants.prevVillagePos = 0;
//        CommonConstants.preDistrictPos = 0;
//
//
//        CommonConstants.farmerFirstName = "";
//        CommonConstants.farmerMiddleName = "";
//        CommonConstants.farmerLastName = "";
//        CommonConstants.landMark = "";
//        CommonConstants.palmArea = "";
//        CommonConstants.plotVillage = "";
//        CommonConstants.gpsArea = "";
//
//    }

//    public static void copyFile(final Context context) {
//        try {
//            String dataDir = context.getApplicationInfo().dataDir;
/////sdcard/3FNursery_Files/3F_Database/3foilpalm.sqlite
//            final String dbfile = "/sdcard/3f_" + CommonConstants.TAB_ID + "_" + System.nanoTime();
//          Log.e(LOG_TAG,"============>dbfile"+ dbfile);
//
////            File dir = new File(dataDir + "/databases");
//            File dir = new File(dataDir + "/database");
//            for (File file : dir.listFiles()) {
//                if (file.isFile() && file.getName().equals("3foilpalm.sqlite")) {
//                    try {
//                        copy(file, new File(dbfile));
//                    } catch (Exception e) {
//                        android.util.Log.e(LOG_TAG, "", e);
//                    }
//                    break;
//                }
//            }
//        } catch (Exception e) {
//            android.util.Log.w("Settings Backup", e);
//        }
//
//    }
    public static void copyFile(final Context context) {
        try {
            String dataDir = context.getApplicationInfo().dataDir;

            final String dbfile = "/sdcard/3f_" + CommonConstants.TAB_ID + "_" + System.nanoTime();

            File dir = new File(dataDir + "/databases");
            for (File file : dir.listFiles()) {
                if (file.isFile() && file.getName().equals("3foilpalm.sqlite")) {
                    try {
                        copy(file, new File(dbfile));
                    } catch (Exception e) {
                        android.util.Log.e(LOG_TAG, "", e);
                    }
                    break;
                }
            }
        } catch (Exception e) {
            android.util.Log.w("Settings Backup", e);
        }

    }
    public static boolean isLocationPermissionGranted(final Context context) {
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION);

        return (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Build.VERSION.SDK_INT >= 28
                || (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED
                && hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED));
    }


    public static boolean isLocationPermissionGranted1(final Context context) {
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION);

        return ((hasFineLocationPermission == PackageManager.PERMISSION_GRANTED
                && hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED));
    }

//    public static void startFalogLocationService(Context context, BroadcastReceiver mLbsMessageReceiver) {
//        if (mLbsMessageReceiver != null) {
//            LocalBroadcastManager.getInstance(context).registerReceiver(mLbsMessageReceiver, new IntentFilter(FalogService.ACTION_LOCATION_UPDATED_TRACKING));
//        }
//        Intent intent = new Intent(context, FalogService.class);
//        intent.setAction(FalogService.ACTION_START);
//        context.startService(intent);
//    }

//    public static void startLocationService(Context context, BroadcastReceiver mLbsMessageReceiver) {
//        if (mLbsMessageReceiver != null) {
//            LocalBroadcastManager.getInstance(context).registerReceiver(mLbsMessageReceiver, new IntentFilter(LocationService.ACTION_LOCATION_UPDATED));
//        }
//        Intent intent = new Intent(context, LocationService.class);
//        intent.setAction(LocationService.ACTION_START);
//        context.startService(intent);
//    }

    public static void getAddressByLocation(final Context context, final double latitude, final double longitude, final boolean onlyVillage, final ApplicationThread.OnComplete onComplete) {
        Log.d(LOG_TAG, "### in getAddressByLocation");
        ApplicationThread.nuiPost(LOG_TAG, "get zipcode", new Runnable() {
            @Override
            public void run() {
                try {
                    Geocoder coder = new Geocoder(context, Locale.getDefault());
                    List<Address> addrList = coder.getFromLocation(latitude, longitude, 1);
                    if (!addrList.isEmpty()) {
                        Address addr = addrList.get(0);
                        String countryCode = addr.getCountryCode();
                        String postalCode = addr.getPostalCode();
                        String locality = addr.getLocality();
                        String area = addr.getAdminArea();

                        String add1 = addr.getAddressLine(0);
                        String add2 = addr.getAddressLine(1);

                        StringBuilder addressBuilder = new StringBuilder();
                        if (!TextUtils.isEmpty(add1)) {
                            addressBuilder.append("House/Door No: " + add1);
                        }

                        if (!TextUtils.isEmpty(add2)) {
                            addressBuilder.append(", \n");
                            addressBuilder.append(add2);
                        }

                        addressBuilder.append(", \n");
                        addressBuilder.append(area);
                        addressBuilder.append(", \n");
                        addressBuilder.append(locality);
                        addressBuilder.append(", \n");
                        addressBuilder.append("Pincode: " + postalCode);


                        Log.d(LOG_TAG, "### def zipcode:" + postalCode + "/" + countryCode + "/" + area + "/" + locality);
                        if (onlyVillage) {
//                            CollectionCenterHomeScreen.receivedAddr = true;
                            onComplete.execute(true, addr.getLocality() + "-" + addr.getSubLocality(), addressBuilder.toString());
                        } else {
                            onComplete.execute(true, postalCode, addressBuilder.toString());
                        }

                    } else {
                        onComplete.execute(false, null, null);
                    }
                } catch (Exception e) {
                    Log.e(LOG_TAG, "Fail to lookup zipcode");
                    onComplete.execute(false, null, null);
                }
            }
        });
    }

//    public static void stopLocationService(Context context, BroadcastReceiver mLbsMessageReceiver) {
//        if (mLbsMessageReceiver != null) {
//            LocalBroadcastManager.getInstance(context).unregisterReceiver(mLbsMessageReceiver);
//        }
//
//        if (isServiceRunning(context, LocationService.class)) {
//            Intent intent = new Intent(context, LocationService.class);
//            context.stopService(intent);
//        }
//    }

    public static boolean isServiceRunning(Context context, Class<?> clazz) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (clazz.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isValidDouble(String doubleValue) {
        return !TextUtils.isEmpty(doubleValue) && doubleValue.length() > 0 && !doubleValue.equalsIgnoreCase(".");
    }

    public static void takeScreenShot(final FragmentActivity activity) {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            // image naming and path  to include sd card  appending name you choose for file
            String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg";

            // create bitmap screen capture
            View v1 = activity.getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();
            openScreenshot(imageFile, activity);
        } catch (Throwable e) {
            // Several error may come out with file handling or OOM
            e.printStackTrace();
        }
    }

    public static void openScreenshot(File imageFile, final FragmentActivity activity) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(imageFile);
        intent.setDataAndType(uri, "image/*");
        activity.startActivity(intent);
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    /**
     * Converting given date into required format
     *
     * @param _date          --> Date to convert
     * @param requiredFormat ----> Format required
     * @return final date string
     */
    public String convertDateToString(Date _date, String requiredFormat) {
        String _finalDateStr = "";
        SimpleDateFormat _dateFormatter = new SimpleDateFormat(requiredFormat);
        try {
            _finalDateStr = _dateFormatter.format(_date);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return _finalDateStr;
    }

    public void getActivityName(final ApplicationThread.OnComplete oncomplete, final Context context) {
        ApplicationThread.bgndPost("", "Registering Device...", new Runnable() {
            @Override
            public void run() {
                String[] activePackages;
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
                    activePackages = getActivePackages(context);
                } else {
                    activePackages = getActivePackagesCompat(context);
                }
                if (activePackages != null) {
                    for (String activePackage : activePackages) {
                        if (activePackage.equals("com.google.android.calendar")) {
                            //Calendar app is launched, do something
                        }
                    }
                }
            }
        });
    }

    public String[] getActivePackagesCompat(Context context) {
        ActivityManager mActivityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningTaskInfo> taskInfo = mActivityManager.getRunningTasks(1);
        final ComponentName componentName = taskInfo.get(0).topActivity;
        final String[] activePackages = new String[1];
        activePackages[0] = componentName.getPackageName();
        return activePackages;
    }

    public String[] getActivePackages(Context context) {
        ActivityManager mActivityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        final Set<String> activePackages = new HashSet<>();
        final List<ActivityManager.RunningAppProcessInfo> processInfos = mActivityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo processInfo : processInfos) {
            if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                activePackages.addAll(Arrays.asList(processInfo.pkgList));
            }
        }
        return activePackages.toArray(new String[activePackages.size()]);
    }

    private String formatDouble(double d) {
        if (d % 1.0 == 0) {
            return new DecimalFormat("#").format(d);
        } else {
            DecimalFormat df = new DecimalFormat("#.####");
            df.setRoundingMode(RoundingMode.FLOOR);
            return df.format(d);
        }
    }

    public static String getTargetDate(String expecteddate, int targetday) {
        final SimpleDateFormat sdfq = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        df.setTimeZone(UTC_TIME_ZONE);
        sdfq.setLenient(true);
        sdfq.setTimeZone(TimeZone.getDefault());
        Date date = null;
        try {
            date = df.parse(expecteddate);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        Calendar cal = Calendar.getInstance();
        cal.set(date.getYear(), date.getMonth(), date.getDate());
        cal.add(Calendar.DAY_OF_YEAR, targetday);
        return sdfq.format(new Date(cal.getTimeInMillis()));

    }

    public static String getTargetDate2(String expecteddate, int targetday) {
        expecteddate ="2021-08-05T19:22:21.597";
        final SimpleDateFormat sdfq = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
//        df.setTimeZone(UTC_TIME_ZONE);
        sdfq.setLenient(true);
//        sdfq.setTimeZone(TimeZone.getDefault());
        Date date = null;
        try {
            date = df.parse(expecteddate);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        Calendar cal = Calendar.getInstance();
        cal.set(date.getYear(), date.getMonth(), date.getDate());
        cal.add(Calendar.DATE, 10);
//        return getProperComplaintsDate(sdfq.format(new Date(cal.getTimeInMillis())));
        return df.format(new Date(cal.getTimeInMillis()));

    }
    public static Date addDays(Date date, int days)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return cal.getTime();
    }
    public static String getTargetDateNew(String datestr, int days) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        Date date = null;
        try {
            date = dateFormat.parse(datestr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar c = Calendar.getInstance();

        c.add(Calendar.DATE, 5);
        return dateFormat.format(c.getTime());
    }

//    public static String getImageUrl(final BasicFarmerDetails basicFarmerDetails) {
//        if (basicFarmerDetails == null) {
//            return "";
//        }
//        String url = Config.image_url + basicFarmerDetails.getPhotoLocation() + "/" + basicFarmerDetails.getPhotoName() + basicFarmerDetails.getFileExtension();
//        return url.replace('\\', '/');
//    }
//
//    public static String getImageUrl(final FileRepository fileRepository) {
//        if (fileRepository == null) {
//            return "";
//        }
//        String url = Config.image_url + fileRepository.getPicturelocation() + "/" + fileRepository.getFilename() + fileRepository.getFileextension();
//        return url.replace('\\', '/');
//    }
//
//
//    public static String getImageUrl(final ComplaintRepository fileRepository) {
//        if (fileRepository == null) {
//            return "";
//        }
//        String url = Config.image_url + fileRepository.getFileLocation() + "/" + fileRepository.getFileName() + fileRepository.getFileExtension();
//        return url.replace('\\', '/');
//    }

    public static List<String> ignoreDuplicatedInArrayList(List<String> inputList) {
        if (null != inputList && !inputList.isEmpty()) {
            Set<String> hs = new HashSet<>();
            hs.addAll(inputList);
            inputList.clear();
            inputList.addAll(hs);
        }
        if (null == inputList) {
            inputList = new ArrayList<>();
        }
        return inputList;
    }

//    public static String getImageUrl(final FarmerBank farmerBank) {
//        if (farmerBank == null) {
//            return "";
//        }
//        String url = Config.image_url + farmerBank.getFilelocation() + "/" + farmerBank.getFilename() + farmerBank.getFileextension();
//        return url.replace('\\', '/');
//    }

    public static boolean isNewRegistration() {
        return (CommonConstants.REGISTRATION_SCREEN_FROM.equalsIgnoreCase(CommonConstants.REGISTRATION_SCREEN_FROM_NEW_FARMER));
    }

    public static boolean isNewPlotRegistration() {
        return (CommonConstants.REGISTRATION_SCREEN_FROM.equalsIgnoreCase(CommonConstants.REGISTRATION_SCREEN_FROM_NEW_PLOT));
    }

    public static boolean isFromFollowUp() {
        return (CommonConstants.REGISTRATION_SCREEN_FROM.equalsIgnoreCase(CommonConstants.REGISTRATION_SCREEN_FROM_FOLLOWUP));
    }

    public static boolean isFromConversion() {
        return (CommonConstants.REGISTRATION_SCREEN_FROM.equalsIgnoreCase(CommonConstants.REGISTRATION_SCREEN_FROM_CONVERSION));
    }

    public static boolean isComplaint() {
        return (CommonConstants.REGISTRATION_SCREEN_FROM.equalsIgnoreCase(CommonConstants.REGISTRATION_SCREEN_FROM_COMPLAINT));
    }

    public static boolean isFromCropMaintenance() {
        return (CommonConstants.REGISTRATION_SCREEN_FROM.equalsIgnoreCase(CommonConstants.REGISTRATION_SCREEN_FROM_CP_MAINTENANCE));
    }

    public static boolean isViewProspectiveFarmers() {
        return (CommonConstants.REGISTRATION_SCREEN_FROM.equalsIgnoreCase(CommonConstants.REGISTRATION_SCREEN_FROM_VPF));
    }

    public static boolean isPlotSplitFarmerPlots() {
        return (CommonConstants.REGISTRATION_SCREEN_FROM.equalsIgnoreCase(CommonConstants.REGISTRATION_SCREEN_FROM_PLOT_SPLIT));
    }

    public static boolean isVisitRequests() {
        return (CommonConstants.REGISTRATION_SCREEN_FROM.equalsIgnoreCase(CommonConstants.REGISTRATION_SCREEN_FROM_VISIT_REQUESTS));
    }


    public static String get3FFileRootPath() {
        String root = Environment.getExternalStorageDirectory().toString();
        File rootDirectory = new File(root + "/PalmRoot_Files");
        if (!rootDirectory.exists()) {
            rootDirectory.mkdirs();
        }
        return rootDirectory.getAbsolutePath() + File.separator;
    }

    public static Integer convertToBigNumber(String inputNumber) {
        return new BigInteger(inputNumber).intValue();
    }

    public static int distanceToCompare(double plotSize) {
        int distanceToCompare = 200;
        if (plotSize <= 2 && plotSize >= 1) {
            distanceToCompare = 200;
        } else if (plotSize <= 3 && plotSize >= 2) {
            distanceToCompare = 200;
        } else if (plotSize <= 4 && plotSize >= 3) {
            distanceToCompare = 700;
        } else if (plotSize <= 5 && plotSize >= 4) {
            distanceToCompare = 1000;
        } else if (plotSize <= 6 && plotSize >= 5) {
            distanceToCompare = 1200;
        } else if (plotSize <= 7 && plotSize >= 6) {
            distanceToCompare = 1500;
        } else if (plotSize <= 8 && plotSize >= 7) {
            distanceToCompare = 1800;
        } else if (plotSize <= 9 && plotSize >= 8) {
            distanceToCompare = 2000;
        } else if (plotSize <= 10 && plotSize >= 9) {
            distanceToCompare = 2500;
        }

        if (plotSize > 10) {
            distanceToCompare = 3000;
        }
        return distanceToCompare;
    }

    public String cleanDate(String inputString) {
        StringBuilder dateBuilder = new StringBuilder();
        String[] dataToConvert = TextUtils.split(inputString, " ");
        if (dataToConvert.length == 3) {
            dateBuilder.append(dataToConvert[0]).append(" ").append(dataToConvert[1]);
        }
        return dateBuilder.toString();
    }

    public static String getFilename(String inputStr) {
        if (TextUtils.isEmpty(inputStr)) {
            return null;
        }
        return inputStr.substring(inputStr.lastIndexOf("/") + 1, inputStr.length());
    }

    public static String getProperDate(final String dateTime) {

        final SimpleDateFormat sdfq = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
        sdfq.setLenient(true);
        sdfq.setTimeZone(TimeZone.getDefault());
        Date date = null;
        try {
            date = sdfq.parse(parseAsIso8601((dateTime)).toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat dt1 = new SimpleDateFormat("dd-MM-yyyy");
        System.out.println(dt1.format(date));
        String s = "" + dt1.format(date);
        try {
            return "" + dt1.format(date) + " " + dateTime.split("T")[1].split(".")[0];
        } catch (Exception ex) {
            return "" + dt1.format(date) + " " + dateTime.split("T")[1];
        }
    }

    public static String getProperDate2(final String dateTime) {

        final SimpleDateFormat sdfq = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        sdfq.setLenient(true);
        sdfq.setTimeZone(TimeZone.getDefault());
        Date date = null;
        try {
            date = sdfq.parse(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat dt1 = new SimpleDateFormat("dd-MM-yyyy");
        System.out.println(dt1.format(date));
        String s = "" + dt1.format(date);
        try {
            return "" + dt1.format(date) + " " + dateTime.split("T")[1].split(".")[0];
        } catch (Exception ex) {
            return "" + dt1.format(date) + " " + dateTime.split("T")[1];
        }
    }
    public static String getProperDate3(final String dateTime) {

        final SimpleDateFormat sdfq = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.ENGLISH);
        sdfq.setLenient(true);
        sdfq.setTimeZone(TimeZone.getDefault());
        Date date = null;
        try {
            date = sdfq.parse(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat dt1 = new SimpleDateFormat("dd-MM-yyyy");
        System.out.println(dt1.format(date));
        String s = "" + dt1.format(date);
        try {
            return "" + dt1.format(date) + " " + dateTime.split("T")[1].split(".")[0];
        } catch (Exception ex) {
            return "" + dt1.format(date) + " " + dateTime.split("T")[1];
        }
    }
    public static String formateDateFromstring(String inputFormat, String outputFormat, String inputDate){

        Date parsed = null;
        String outputDate = "";

        SimpleDateFormat df_input = new SimpleDateFormat(inputFormat, java.util.Locale.getDefault());
        SimpleDateFormat df_output = new SimpleDateFormat(outputFormat, java.util.Locale.getDefault());

        try {
            parsed = df_input.parse(inputDate);
            outputDate = df_output.format(parsed);

        } catch (ParseException e) {
//            LOGE(TAG, "ParseException - dateFormat");
        }

        return outputDate;

    }
    public static String getProperComplaintsDate(final String dateTime) {

        final SimpleDateFormat sdfq = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
        sdfq.setLenient(true);
        sdfq.setTimeZone(TimeZone.getDefault());
        Date date = null;
        try {
            date = sdfq.parse(parseAsIso8601_2((dateTime)).toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat dt1 = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
        System.out.println(dt1.format(date));
        return "" + dt1.format(date);
    }

    public static String getPropeCreateDate(final String utcTime) {


        String time = "";
            if (utcTime != null) {
                SimpleDateFormat utcFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
               utcFormatter.setTimeZone(TimeZone.getDefault());
                Date gpsUTCDate = null;//from  ww  w.j  a va 2 s  . c  o  m
                try {
                    gpsUTCDate = utcFormatter.parse(utcTime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                SimpleDateFormat localFormatter = new SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.ENGLISH);
               localFormatter.setTimeZone(TimeZone.getDefault());
                assert gpsUTCDate != null;
                time = localFormatter.format(gpsUTCDate.getTime());
            }
            return time;

    }
    public static String getProperComplaintsDate2(final String dateTime) {

        final SimpleDateFormat sdfq = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
        sdfq.setLenient(true);
        sdfq.setTimeZone(TimeZone.getDefault());
        Date date = null;
        try {
            date = sdfq.parse(parseAsIso8601_2((dateTime)).toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat dt1 = new SimpleDateFormat("dd-MM-yyyy");
        System.out.println(dt1.format(date));
        return "" + dt1.format(date);
    }

    public static final TimeZone UTC_TIME_ZONE = TimeZone.getTimeZone("UTC");

    private static final ThreadLocal<DateFormat> ISO_8601_FORMAT = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            df.setTimeZone(UTC_TIME_ZONE);
            return df;
        }
    };

    private static final ThreadLocal<DateFormat> ISO_8601_1_FORMAT = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            df.setTimeZone(UTC_TIME_ZONE);
           // sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            return df;
        }
    };

    private static final ThreadLocal<DateFormat> ISO_8601_FORMAT_2 = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            return df;
        }
    };

    public static Date parseAsIso8601(String date) {
        Date dateToCheck = null;
        try {
            dateToCheck = ISO_8601_FORMAT.get().parse(date);
        } catch (ParseException e) {
//            throw new RuntimeException(e);
            dateToCheck = thisIsStupid(date);
        }

        return dateToCheck;
    }

    public static Date parseAsIso8601_2(String date) {
        Date dateToCheck = null;
        try {
            dateToCheck = ISO_8601_FORMAT_2.get().parse(date);
        } catch (ParseException e) {
//            throw new RuntimeException(e);
            dateToCheck = thisIsStupid(date);
        }

        return dateToCheck;
    }

    public static Date parseAsIso8601_(String date) {
        Date dateToCheck = null;
        try {
            dateToCheck = ISO_8601_1_FORMAT.get().parse(date);
        } catch (ParseException e) {
//            throw new RuntimeException(e);
            dateToCheck = thisIsStupid(date);
        }

        return dateToCheck;
    }
    public static Date thisIsStupid(final String date) {
        try {
            return ISO_8601_1_FORMAT.get().parse(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);

        }
    }

   public  static  int getIntFromEditText(EditText editText)
    {
        int value= 0;

        try
        {
            int nIntFromET = Integer.parseInt(editText.getText().toString());
            value = nIntFromET;
        }
        catch (NumberFormatException e)
        {
            // handle the exception
            value = 0;
        }
        return value;
    }


}
