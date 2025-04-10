package com.calibrage.palmroot.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import com.calibrage.palmroot.cloudhelper.ApplicationThread;
import com.calibrage.palmroot.common.CommonConstants;
import com.calibrage.palmroot.common.CommonUtils;
import com.calibrage.palmroot.dbmodels.ActivityTasks;
import com.calibrage.palmroot.dbmodels.Alerts;
import com.calibrage.palmroot.dbmodels.CheckNurseryAcitivity;
import com.calibrage.palmroot.dbmodels.ConsignmentData;
import com.calibrage.palmroot.dbmodels.ConsignmentDetails;
import com.calibrage.palmroot.dbmodels.ConsignmentReports;
import com.calibrage.palmroot.dbmodels.ConsignmentStatuData;
import com.calibrage.palmroot.dbmodels.CullinglossFileRepository;
import com.calibrage.palmroot.dbmodels.DisplayData;
import com.calibrage.palmroot.dbmodels.FertilizerDetails;
import com.calibrage.palmroot.dbmodels.Irrigationhistorymodel;
import com.calibrage.palmroot.dbmodels.LandlevellingFields;
import com.calibrage.palmroot.dbmodels.LocationTracker;
import com.calibrage.palmroot.dbmodels.MutipleData;
import com.calibrage.palmroot.dbmodels.NurseryAcitivity;
import com.calibrage.palmroot.dbmodels.NurseryData;
import com.calibrage.palmroot.dbmodels.NurseryDetails;
import com.calibrage.palmroot.dbmodels.NurseryIrrigationLog;
import com.calibrage.palmroot.dbmodels.NurseryIrrigationLogForDb;
import com.calibrage.palmroot.dbmodels.NurseryIrrigationLogXref;
import com.calibrage.palmroot.dbmodels.NurseryLabourLog;
import com.calibrage.palmroot.dbmodels.NurseryRMActivity;
import com.calibrage.palmroot.dbmodels.NurseryRMTransctions;
import com.calibrage.palmroot.dbmodels.NurseryVisitLog;
import com.calibrage.palmroot.dbmodels.RMTransactions;
import com.calibrage.palmroot.dbmodels.RMTransactionsStatusHistory;
import com.calibrage.palmroot.dbmodels.SaplingActivity;
import com.calibrage.palmroot.dbmodels.SaplingActivityHistoryModel;
import com.calibrage.palmroot.dbmodels.SaplingActivityStatusModel;
import com.calibrage.palmroot.dbmodels.SaplingActivityXrefModel;
import com.calibrage.palmroot.dbmodels.Saplings;
import com.calibrage.palmroot.dbmodels.UserDetails;
import com.calibrage.palmroot.dbmodels.UserSync;
import com.calibrage.palmroot.dbmodels.ViewVisitLog;
import com.calibrage.palmroot.dbmodels.Visitdata;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DataAccessHandler<T> {

    private static final String LOG_TAG = DataAccessHandler.class.getName();

    private Context context;
    private SQLiteDatabase mDatabase;
    private String var = "";
    String queryForLookupTable = "select Name from LookUp where id=" + var;
    private int value;

    public DataAccessHandler() {

    }

    SimpleDateFormat simpledatefrmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    String currentTime = simpledatefrmt.format(new Date());


    public DataAccessHandler(final Context context) {
        this.context = context;
        try {
            mDatabase = Palm3FoilDatabase.openDataBaseNew();
            DataBaseUpgrade.upgradeDataBase(context, mDatabase);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public DataAccessHandler(final Context context, boolean firstTime) {
        this.context = context;
        try {
            mDatabase = Palm3FoilDatabase.openDataBaseNew();
        } catch (SQLException e) {
            e.printStackTrace();
            Log.d("WhatistheException", e.toString());
        }
    }

    public String getSingleValue(String query) {
        Log.v(LOG_TAG, "@@@ query " + query);
        Cursor mOprQuery = null;
        try {
            mOprQuery = mDatabase.rawQuery(query, null);
            if (mOprQuery != null && mOprQuery.moveToFirst()) {
                return mOprQuery.getString(0);
            }

            return null;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != mOprQuery)
                mOprQuery.close();

            closeDataBase();
        }
        return "";
    }

    public Integer getSingleIntValue(String query) {
        Log.v(LOG_TAG, "@@@ query=======int " + query);
        Cursor mOprQuery = null;
        try {
            mOprQuery = mDatabase.rawQuery(query, null);
            if (mOprQuery != null && mOprQuery.moveToFirst()) {
                return mOprQuery.getInt(0);
            }

            return null;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != mOprQuery)
                mOprQuery.close();

            closeDataBase();
        }
        return null;
    }
    public String getSingleValueInt(String query) {
        Log.v(LOG_TAG, "@@@ query " + query);
        Cursor mOprQuery = null;
        try {
            mOprQuery = mDatabase.rawQuery(query, null);
            if (mOprQuery != null && mOprQuery.moveToFirst()) {
                String isSelect = String.valueOf(mOprQuery.getColumnIndex("IsOptional"));
                return isSelect;
            }

            return null;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != mOprQuery)
                mOprQuery.close();

            closeDataBase();
        }
        return null;
    }

    public LinkedHashMap<String, String> getGenericData(final String query) {
        Log.v(LOG_TAG, "@@@ Generic Query " + query);
        LinkedHashMap<String, String> mGenericData = new LinkedHashMap<>();
        Cursor genericDataQuery = null;
        try {
            genericDataQuery = mDatabase.rawQuery(query, null);
            if (genericDataQuery.moveToFirst()) {
                do {
                    mGenericData.put(genericDataQuery.getString(0), genericDataQuery.getString(1));
                } while (genericDataQuery.moveToNext());
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        } finally {
            if (null != genericDataQuery)
                genericDataQuery.close();

            closeDataBase();
        }
        return mGenericData;
    }

    public LinkedHashMap<String, String> getMoreGenericData(final String query) {
        Log.v(LOG_TAG, "@@@ Generic Query " + query);
        LinkedHashMap<String, String> mGenericData = new LinkedHashMap<>();
        Cursor genericDataQuery = mDatabase.rawQuery(query, null);
        try {
            if (genericDataQuery.moveToFirst()) {
                do {
                    mGenericData.put(genericDataQuery.getString(0), genericDataQuery.getString(1) + "-" + genericDataQuery.getString(2) + "-" + genericDataQuery.getString(3));
                } while (genericDataQuery.moveToNext());
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        } finally {
            if (null != genericDataQuery)
                genericDataQuery.close();

            closeDataBase();
        }
        return mGenericData;
    }


    public LinkedHashMap<String, String> getFarmerDetailsData(String query) {
        LinkedHashMap linkedHashMap = new LinkedHashMap<String, String>();
        Cursor cursor = mDatabase.rawQuery(query, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    String key = cursor.getString(cursor.getColumnIndex("Code"));
                    String value = cursor.getString(cursor.getColumnIndex("FirstName"))
                            + "-" + cursor.getString(cursor.getColumnIndex("ContactNumber"));

                    linkedHashMap.put(key, value);


                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return linkedHashMap;

    }


    public long addUserSync(UserSync userSync) {


        ContentValues contentValues = new ContentValues();
        contentValues.put("UserId", userSync.getUserId());
        contentValues.put("App", userSync.getApp());
        contentValues.put("Date", userSync.getDate());
        contentValues.put("MasterSync", userSync.getMasterSync());
        contentValues.put("TransactionSync", userSync.getTransactionSync());
        contentValues.put("ResetData", userSync.getResetData());
        contentValues.put("IsActive", userSync.getIsActive());
        contentValues.put("CreatedByUserId", userSync.getCreatedByUserId());
        contentValues.put("CreatedDate", userSync.getCreatedDate());
        contentValues.put("UpdatedByUserId", userSync.getUpdatedByUserId());
        contentValues.put("UpdatedDate", userSync.getUpdatedDate());
        contentValues.put("ServerUpdatedStatus", userSync.getServerUpdatedStatus());
        return mDatabase.insert("UserSync", null, contentValues);

    }

    public void updateUserSync() {
        ContentValues contentValues = new ContentValues();
        contentValues.put("ServerUpdatedStatus", 1);
        mDatabase.update("UserSync", contentValues, "ServerUpdatedStatus='0'", null);
        Log.v("@@@MM", "Updating");
    }

    public void updateMasterSync() {
        ContentValues contentValues = new ContentValues();
        contentValues.put("MasterSync", 1);
        contentValues.put("ServerUpdatedStatus", 0);
        contentValues.put("Date", CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
        contentValues.put("CreatedDate", CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
        contentValues.put("UpdatedDate", CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));

        // mDatabase.update("UserSync",contentValues,"ServerUpdatedStatus='0'",null);
        mDatabase.update("UserSync", contentValues, null, null);
        Log.v("@@@MM", "Updating");
    }



    public LinkedHashMap<String, Pair> getPairData(final String query) {
        Log.v(LOG_TAG, "@@@ Generic Query " + query);
        LinkedHashMap<String, Pair> mGenericData = new LinkedHashMap<>();
        Cursor genericDataQuery = null;
        try {
            genericDataQuery = mDatabase.rawQuery(query, null);
            if (genericDataQuery.moveToFirst()) {
                do {
                    mGenericData.put(genericDataQuery.getString(0), Pair.create(genericDataQuery.getString(1), genericDataQuery.getString(2)));
                } while (genericDataQuery.moveToNext());
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        } finally {
            if (null != genericDataQuery)
                genericDataQuery.close();

            closeDataBase();
        }
        return mGenericData;
    }


    public boolean checkValueExistedInDatabase(final String query) {
        Cursor mOprQuery = mDatabase.rawQuery(query, null);
        Log.e("============>", "checkValueExistedInDatabase" + query+"");
        try {
            if (mOprQuery != null && mOprQuery.moveToFirst()) {
                return (mOprQuery.getInt(0) > 0);
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != mOprQuery)
                mOprQuery.close();

            closeDataBase();
        }
        return false;
    }


    public Integer getOnlyOneIntValueFromDb(String query) {
        Log.v(LOG_TAG, "@@@ query " + query);
        Cursor mOprQuery = null;
        try {
            mOprQuery = mDatabase.rawQuery(query, null);
            if (mOprQuery != null && mOprQuery.moveToFirst()) {
                return mOprQuery.getInt(0);
            }

            return null;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != mOprQuery)
                mOprQuery.close();

            closeDataBase();
        }
        return null;
    }



    public String getOnlyOneValueFromDb(String query) {
        Log.v(LOG_TAG, "@@@ query " + query);
        Cursor mOprQuery = null;
        try {
            mOprQuery = mDatabase.rawQuery(query, null);
            if (mOprQuery != null && mOprQuery.moveToFirst()) {
                return mOprQuery.getString(0);
            }

            return null;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != mOprQuery)
                mOprQuery.close();

            closeDataBase();
        }
        return "";
    }



    public synchronized void insertData(boolean fromMaster, String tableName, List<LinkedHashMap> mapList, final ApplicationThread.OnComplete<String> oncomplete) {
        int checkCount = 0;
        try {
            List<ContentValues> values1 = new ArrayList<>();
            for (int i = 0; i < mapList.size(); i++) {
                checkCount++;
                List<LinkedHashMap.Entry> entryList = new ArrayList<>((mapList.get(i)).entrySet());

                ContentValues contentValues = new ContentValues();
                for (LinkedHashMap.Entry temp : entryList) {
                    String keyToInsert = temp.getKey().toString();
                    if (!fromMaster) {
                        if (keyToInsert.equalsIgnoreCase("Id") && !tableName.equalsIgnoreCase(DatabaseKeys.TABLE_ALERTS) && !tableName.equalsIgnoreCase(DatabaseKeys.TABLE_SAPLING))
                            continue;
                    }
                    if (keyToInsert.equalsIgnoreCase("ServerUpdatedStatus")) {
                        contentValues.put(keyToInsert, "1");
                    } else {
                        contentValues.put(temp.getKey().toString(), temp.getValue().toString());
                    }
                }
                values1.add(contentValues);
            }
            Log.v(LOG_TAG, "@@@@ log check " + checkCount + " here " + values1.size());
            boolean hasError = bulkinserttoTable(values1, tableName);
            if (hasError) {
                Log.v(LOG_TAG, "@@@ Error while inserting data ");
                if (null != oncomplete) {
                    oncomplete.execute(false, "failed to insert data", "");
                }
            } else {
                Log.v(LOG_TAG, "@@@ data inserted successfully for table :" + tableName);
                if (null != oncomplete) {
                    oncomplete.execute(true, "data inserted successfully", "");
                }
            }
        } catch (Exception e) {
            checkCount++;
            e.printStackTrace();
            Log.v(LOG_TAG, "@@@@ exception log check " + checkCount + " here " + mapList.size());
            if (checkCount == mapList.size()) {
                if (null != oncomplete)
                    oncomplete.execute(false, "data insertion failed", "" + e.getMessage());
            }
        } finally {
            closeDataBase();
        }
    }


    public synchronized void insertMyData(boolean fromMaster, String tableName, List<LinkedHashMap> mapList, final ApplicationThread.OnComplete<String> oncomplete) {
        int checkCount = 0;
        try {
            List<ContentValues> values1 = new ArrayList<>();
            for (int i = 0; i < mapList.size(); i++) {
                checkCount++;
                List<LinkedHashMap.Entry> entryList = new ArrayList<>((mapList.get(i)).entrySet());

                ContentValues contentValues = new ContentValues();
                for (LinkedHashMap.Entry temp : entryList) {
                    String keyToInsert = temp.getKey().toString();
                    if (!fromMaster) {
                        if (keyToInsert.equalsIgnoreCase("Id") && !tableName.equalsIgnoreCase(DatabaseKeys.TABLE_ALERTS))
                            continue;
                    }
                    if (keyToInsert.equalsIgnoreCase("ServerUpdatedStatus")) {
                        contentValues.put(keyToInsert, "0");
                    } else {
                        contentValues.put(temp.getKey().toString(), temp.getValue().toString());
                    }
                }
                values1.add(contentValues);
            }
            Log.v(LOG_TAG, "@@@@ log check " + checkCount + " here " + values1.size());
            boolean hasError = bulkinserttoTable(values1, tableName);
            if (hasError) {
                Log.v(LOG_TAG, "@@@ Error while inserting data ");
                if (null != oncomplete) {
                    oncomplete.execute(false, "failed to insert data", "");
                }
            } else {
                Log.v(LOG_TAG, "@@@ data inserted successfully for table :" + tableName);
                if (null != oncomplete) {
                    oncomplete.execute(true, "data inserted successfully", "");
                }
            }
        } catch (Exception e) {
            checkCount++;
            e.printStackTrace();
            Log.v(LOG_TAG, "@@@@ exception log check " + checkCount + " here " + mapList.size());
            if (checkCount == mapList.size()) {
                if (null != oncomplete)
                    oncomplete.execute(false, "data insertion failed", "" + e.getMessage());
            }
        } finally {
            closeDataBase();
        }
    }

    public synchronized void insertData(String tableName, List<LinkedHashMap> mapList, final ApplicationThread.OnComplete<String> oncomplete) {
        insertData(false, tableName, mapList, oncomplete);
    }

    public synchronized void insertMyDataa(String tableName, List<LinkedHashMap> mapList, final ApplicationThread.OnComplete<String> oncomplete) {
        insertMyData(false, tableName, mapList, oncomplete);
    }

    /**
     * Updating database records
     *
     * @param tableName      ---> Table name to update
     * @param list           ---> List which contains data values
     * @param isClaues       ---> Checking where condition availability
     * @param whereCondition ---> condition
     */
    public synchronized void updateData(String tableName, List<LinkedHashMap> list, Boolean isClaues, String whereCondition, final ApplicationThread.OnComplete<String> oncomplete) {
        boolean isUpdateSuccess = false;
        int checkCount = 0;
        try {
            for (int i = 0; i < list.size(); i++) {
                checkCount++;
                List<Map.Entry> entryList = new ArrayList<Map.Entry>((list.get(i)).entrySet());
                String query = "update " + tableName + " set ";
                String namestring = "";
                Log.v(LOG_TAG, "@@@ query for namestring 1" + query);
                System.out.println("\n==> Size of Entry list: " + entryList.size());
                StringBuffer columns = new StringBuffer();
                for (Map.Entry temp : entryList) {
                    columns.append(temp.getKey());
                    columns.append("='");
                    columns.append(temp.getValue());
                    columns.append("',");
                }
                Log.v(LOG_TAG, "@@@ query for namestring " + namestring);
                namestring = columns.deleteCharAt(columns.length() - 1).toString();
                query = query + namestring + "" + whereCondition;
                mDatabase.execSQL(query);
                isUpdateSuccess = true;
                Log.v(LOG_TAG, "@@@ query for Plantation " + query);
            }
        } catch (Exception e) {
            checkCount++;
            e.printStackTrace();
            isUpdateSuccess = false;
        } finally {
            closeDataBase();
            if (checkCount == list.size()) {
                if (isUpdateSuccess) {
                    Log.v(LOG_TAG, "@@@ data updated successfully for " + tableName);
                    oncomplete.execute(true, null, "data updated successfully for " + tableName);
                } else {
                    oncomplete.execute(false, null, "data updation failed for " + tableName);
                }
            }
        }
    }

    /**
     * Deleting records from database table
     *
     * @param tableName  ---> Table name
     * @param columnName ---> Column name to deleting
     * @param value      ---> Value for where condition
     * @param isWhere    ---> Checking where condition is required or not
     */
    public synchronized void deleteRow(String tableName, String columnName, String value, boolean isWhere, final ApplicationThread.OnComplete<String> onComplete) {
        boolean isDataDeleted = true;
//        if (!ApplicationThread.dbThreadCheck())
//            Log.e(LOG_TAG, "called on non-db thread", new RuntimeException());

        try {
//            mDatabase = palm3FoilDatabase.getWritableDatabase();
            String query = "delete from " + tableName;
            if (isWhere) {
                query = query + " where " + columnName + " = '" + value + "'";
            }
            mDatabase.execSQL(query);
        } catch (Exception e) {
            isDataDeleted = false;
            Log.e(LOG_TAG, "@@@ master data deletion failed for " + tableName + " error is " + e.getMessage());
            onComplete.execute(false, null, "master data deletion failed for " + tableName + " error is " + e.getMessage());
        } finally {
            closeDataBase();

            if (isDataDeleted) {
                Log.v(LOG_TAG, "@@@ master data deleted successfully for " + tableName);
                onComplete.execute(true, null, "master data deleted successfully for " + tableName);
            }

        }
    }


    public String getCountValue(String query) {

        Log.v(LOG_TAG, "@@@ getCountValue for " + query);
//        mDatabase = palm3FoilDatabase.getWritableDatabase();
        Cursor mOprQuery = null;
        try {
            mOprQuery = mDatabase.rawQuery(query, null);
            if (mOprQuery != null && mOprQuery.moveToFirst()) {
                return mOprQuery.getString(0);
            }

            return null;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mOprQuery.close();
            closeDataBase();
        }
        return "";
    }


    public void closeDataBase() {
//        if (mDatabase != null)
//            mDatabase.close();
    }

    public void executeRawQuery(String query) {
        try {
            if (mDatabase != null) {
                mDatabase.execSQL(query);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }



    public List<String> getSingleListData(final String query) {
        Log.v(LOG_TAG, "@@@ Generic Query " + query);
        List<String> mGenericData = new ArrayList<>();
        Cursor genericDataQuery = null;
        try {
            genericDataQuery = mDatabase.rawQuery(query, null);
            if (genericDataQuery.moveToFirst()) {
                do {
                    String plotCode = genericDataQuery.getString(0);
                    mGenericData.add(plotCode);
                } while (genericDataQuery.moveToNext());
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        } finally {
            if (null != genericDataQuery)
                genericDataQuery.close();
            closeDataBase();
        }
        return mGenericData;
    }



    public T getUserDetails(final String query, int dataReturnType) {
        UserDetails userDetails = null;
        Cursor cursor = null;
        List userDataList = new ArrayList();
        Log.v(LOG_TAG, "@@@ user details query " + query);
        try {
            cursor = mDatabase.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    userDetails = new UserDetails();
                    userDetails.setUserId(cursor.getString(0));
                    userDetails.setUserName(cursor.getString(1));
                    userDetails.setPassword(cursor.getString(2));
                    userDetails.setRoleId(cursor.getInt(3));
                    userDetails.setManagerId(cursor.getInt(4));
                    userDetails.setId(cursor.getString(5));
                    userDetails.setFirstName(cursor.getString(6));
                    userDetails.setTabName(cursor.getString(7));
                    userDetails.setUserCode(cursor.getString(8));
//                    userDetails.setTabletId(cursor.getInt(5));
//                    userDetails.setUserVillageId(cursor.getString(6));
                    if (dataReturnType == 1) {
                        userDataList.add(userDetails);
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "@@@ getting user details " + e.getMessage());
        }
        return (T) ((dataReturnType == 0) ? userDetails : userDataList);
    }


    public T getAlertsDetails(final String query, int dataReturnType, boolean fromRefresh) {
        Cursor cursor = null;
        Alerts alertDetails = null;
        List alertsDataList = new ArrayList();
        Log.v(LOG_TAG, "@@@ alertDetails  query " + query);
        try {
            cursor = mDatabase.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    alertDetails = new Alerts();
                    alertDetails.setId(cursor.getInt(0));
                    alertDetails.setName(cursor.getString(1));
                    alertDetails.setDesc(cursor.getString(2));
                    alertDetails.setUserId(cursor.getInt(3));
                    alertDetails.setHTMLDesc(cursor.getString(4));
                    alertDetails.setIsRead(cursor.getInt(5));
                    alertDetails.setPlotCode(cursor.getString(6));
                    alertDetails.setComplaintCode(cursor.getString(7));
                    alertDetails.setAlertTypeId(cursor.getInt(8));
                    alertDetails.setCreatedByUserId(cursor.getInt(9));
                    alertDetails.setCreatedDate(cursor.getString(10));
                    alertDetails.setUpdatedByUserId(cursor.getInt(11));
                    alertDetails.setUpdatedDate(cursor.getString(12));
                    alertDetails.setServerUpdatedStatus(cursor.getInt(13));
                    if (!fromRefresh) {
                        alertDetails.setHTMLDesc(cursor.getString(14));
                    }
                    if (dataReturnType == 1) {
                        alertsDataList.add(alertDetails);
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "@@@ getting  alertDetails " + e.getMessage());
        }
        return (T) ((dataReturnType == 0) ? alertDetails : alertsDataList);
    }

    public boolean bulkinserttoTable(List<ContentValues> cv, final String tableName) {
        boolean isError = false;
        mDatabase.beginTransaction();
        try {
            for (int i = 0; i < cv.size(); i++) {
                ContentValues stockResponse = cv.get(i);

                // Added BY MAHESH - CIS   06082021 SAPLING TABLE ID NOT INCREMENT VALUE
                long id = mDatabase.insert(tableName, null, stockResponse);
                if (id < 0) {
                    isError = true;
                }


            }
            mDatabase.setTransactionSuccessful();
        } finally {
            mDatabase.endTransaction();
        }
        return isError;
    }


    public List<NurseryDetails> getNurseryDetails(final String query) {
        List<NurseryDetails> nurserySaplingDetails = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = mDatabase.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {

                    NurseryDetails nurseryDetails = new NurseryDetails();
                    nurseryDetails.setCode(cursor.getString(cursor.getColumnIndex("Code")));
                    nurseryDetails.setName(cursor.getString(cursor.getColumnIndex("Name")));
                    nurseryDetails.setPinCode(cursor.getInt(cursor.getColumnIndex("PinCode")));

                    nurserySaplingDetails.add(nurseryDetails);
                } while (cursor.moveToNext());

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }
        return nurserySaplingDetails;
    }


    public List<NurseryData> getNurseryData(final String query) {    // Get Nursery Details
        List<NurseryData> nurseryData = new ArrayList<>();
        Log.d(LOG_TAG, "=== > Analysis ==> getNurseryData:" + query);
        Cursor cursor = null;
        try {
            cursor = mDatabase.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {

                    NurseryData nurseryDataDetails = new NurseryData();
                    nurseryDataDetails.setCode(cursor.getString(cursor.getColumnIndex("Code")));
                    nurseryDataDetails.setName(cursor.getString(cursor.getColumnIndex("name")));
                    nurseryDataDetails.setStatename(cursor.getString(cursor.getColumnIndex("Statename")));
                    nurseryDataDetails.setDistrictname(cursor.getString(cursor.getColumnIndex("DistrictName")));
                    nurseryDataDetails.setMandalname(cursor.getString(cursor.getColumnIndex("MandalName")));
                    nurseryDataDetails.setVillagename(cursor.getString(cursor.getColumnIndex("Villagename")));
                    nurseryDataDetails.setPinCode(cursor.getInt(cursor.getColumnIndex("PinCode")));


                    nurseryData.add(nurseryDataDetails);
                } while (cursor.moveToNext());

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }
        return nurseryData;
    }

    public List<ConsignmentData> getConsignmentData(final String query) {
        Log.d(LOG_TAG, "==> analysis GetConsinmentData :" + query);
        List<ConsignmentData> consignmentData = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = mDatabase.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {

                    ConsignmentData consignmentdetails = new ConsignmentData();
                    consignmentdetails.setId(cursor.getInt(cursor.getColumnIndex("Id")));
                    consignmentdetails.setConsignmentCode(cursor.getString(cursor.getColumnIndex("ConsignmentCode")));
                    consignmentdetails.setOriginname(cursor.getString(cursor.getColumnIndex("Originname")));
                    consignmentdetails.setVendorname(cursor.getString(cursor.getColumnIndex("Vendorname")));
                    consignmentdetails.setVarietyname(cursor.getString(cursor.getColumnIndex("Varietyname")));
                    consignmentdetails.setEstimatedQuantity(cursor.getInt(cursor.getColumnIndex("EstimatedQuantity")));
                    consignmentdetails.setCreatedDate(cursor.getString(cursor.getColumnIndex("CreatedDate")));
                    consignmentdetails.setArrivedDate(cursor.getString(cursor.getColumnIndex("ArrivedDate")));
                    consignmentdetails.setArrivedQuantity(cursor.getInt(cursor.getColumnIndex("ArrivedQuantity")));
                    consignmentdetails.setStatus(cursor.getString(cursor.getColumnIndex("Status")));


                    consignmentData.add(consignmentdetails);
                } while (cursor.moveToNext());

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }
        return consignmentData;
    }

    public List<SaplingActivity> getSaplingActivityData(final String query) {
        List<SaplingActivity> sapactivitydata = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = mDatabase.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {

                    SaplingActivity saplingsactivityDetails = new SaplingActivity();
                 //   saplingsactivityDetails.setId(cursor.getInt(cursor.getColumnIndex("Id")));
                    saplingsactivityDetails.setTransactionId(cursor.getString(cursor.getColumnIndex("TransactionId")));
                    saplingsactivityDetails.setConsignmentCode(cursor.getString(cursor.getColumnIndex("ConsignmentCode")));
                    saplingsactivityDetails.setActivityId(cursor.getInt(cursor.getColumnIndex("ActivityId")));
                    saplingsactivityDetails.setStatusTypeId(cursor.getInt(cursor.getColumnIndex("StatusTypeId")));
                    saplingsactivityDetails.setComment(cursor.getString(cursor.getColumnIndex("Comment")));
                    saplingsactivityDetails.setIsActive(cursor.getInt(cursor.getColumnIndex("IsActive")));
                    saplingsactivityDetails.setCreatedByUserId(cursor.getInt(cursor.getColumnIndex("CreatedByUserId")));
                    saplingsactivityDetails.setCreatedDate(cursor.getString(cursor.getColumnIndex("CreatedDate")));
                    saplingsactivityDetails.setUpdatedByUserId(cursor.getInt(cursor.getColumnIndex("UpdatedByUserId")));
                    saplingsactivityDetails.setUpdatedDate(cursor.getString(cursor.getColumnIndex("UpdatedDate")));
                    saplingsactivityDetails.setServerUpdatedStatus(cursor.getInt(cursor.getColumnIndex("ServerUpdatedStatus")));


                    sapactivitydata.add(saplingsactivityDetails);
                } while (cursor.moveToNext());

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }
        return sapactivitydata;
    }


    public List<ConsignmentStatuData> getConsignmentStatus(final String query) {
        Log.d(DataAccessHandler.class.getSimpleName(),"@@Query :"+query);
        List<ConsignmentStatuData> consignmentStatusData = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = mDatabase.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {

                    ConsignmentStatuData consignmentstatusdetails = new ConsignmentStatuData();
                    consignmentstatusdetails.setConsignmentCode(cursor.getString(cursor.getColumnIndex("ConsignmentCode")));
                    consignmentstatusdetails.setOriginname(cursor.getString(cursor.getColumnIndex("Originname")));
                    consignmentstatusdetails.setSowingDate(cursor.getString(cursor.getColumnIndex("SowingDate")));
                    consignmentstatusdetails.setCreatedDate(cursor.getString(cursor.getColumnIndex("CreatedDate")));
                    consignmentstatusdetails.setStatusType(cursor.getString(cursor.getColumnIndex("StatusType")));
                    consignmentstatusdetails.setVarietyname(cursor.getString(cursor.getColumnIndex("Varietyname")));


                    consignmentStatusData.add(consignmentstatusdetails);
                } while (cursor.moveToNext());

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }
        return consignmentStatusData;
    }

    public List<ConsignmentDetails> getConsignmentDetails(final String query) {
        List<ConsignmentDetails> consignmentDetails = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = mDatabase.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {

                    ConsignmentDetails consDetails = new ConsignmentDetails();
                    consDetails.setId(cursor.getInt(cursor.getColumnIndex("Id")));
                    consDetails.setNurseryCode(cursor.getString(cursor.getColumnIndex("NurseryCode")));
                    consDetails.setConsignmentCode(cursor.getString(cursor.getColumnIndex("ConsignmentCode")));

                    consignmentDetails.add(consDetails);
                } while (cursor.moveToNext());

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }
        return consignmentDetails;
    }



    public List<NurseryAcitivity> getNurseryActivityDetails(final String query) {
        Log.d(DataAccessHandler.class.getSimpleName(), "====> Analysis ==> GET ACTIVITIES :" + query);
        List<NurseryAcitivity> nurseryActivityDetails = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = mDatabase.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {




                    NurseryAcitivity nurseryActivityyDetails = new NurseryAcitivity();
                    nurseryActivityyDetails.setActivityId(cursor.getInt(cursor.getColumnIndex("ActivityId")));
                    nurseryActivityyDetails.setActivityTypeId(cursor.getInt(cursor.getColumnIndex("ActivityId")));
                    nurseryActivityyDetails.setIsMultipleEntries(cursor.getString(cursor.getColumnIndex("IsMultipleEntries")));
                    nurseryActivityyDetails.setConsignmentCode(cursor.getString(cursor.getColumnIndex("ActivityCode")));
                    nurseryActivityyDetails.setActivityName(cursor.getString(cursor.getColumnIndex("ActivityName")));
                    nurseryActivityyDetails.setStatusTypeId(cursor.getInt(cursor.getColumnIndex("StatusTypeId")));
                    nurseryActivityyDetails.setActivityStatus(cursor.getString(cursor.getColumnIndex("ActivityStatus")));
                    nurseryActivityyDetails.setActivityDoneDate(cursor.getString(cursor.getColumnIndex("ActivityDoneDate")));
                    nurseryActivityyDetails.setTargetDate(cursor.getString(cursor.getColumnIndex("TargetDate")));
                    nurseryActivityyDetails.setDependentActivityCode(cursor.getString(cursor.getColumnIndex("DependentActivityCode")));
                    nurseryActivityyDetails.setColorIndicator(cursor.getInt(cursor.getColumnIndex("ColorIndicator")));

                    nurseryActivityDetails.add(nurseryActivityyDetails);
                } while (cursor.moveToNext());

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }
        return nurseryActivityDetails;
    }


    public List<MutipleData> getMultipleDataDetails(final String query) {
        Log.d(DataAccessHandler.class.getSimpleName(), "==> analysis Query :" + query);
        List<MutipleData> mutipleDataListDetails = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = mDatabase.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {

                    MutipleData multipleDataDetails = new MutipleData();
                    multipleDataDetails.setId(cursor.getInt(cursor.getColumnIndex("Id")));
                    multipleDataDetails.setTransactionId(cursor.getString(cursor.getColumnIndex("TransactionId")));
                    multipleDataDetails.setConsignmentCode(cursor.getString(cursor.getColumnIndex("ConsignmentCode")));
                    multipleDataDetails.setActivityId(cursor.getInt(cursor.getColumnIndex("ActivityId")));
                    multipleDataDetails.setStatusTypeId(cursor.getInt(cursor.getColumnIndex("StatusTypeId")));
                    multipleDataDetails.setComment(cursor.getString(cursor.getColumnIndex("Comment")));
                    multipleDataDetails.setIsActive(cursor.getInt(cursor.getColumnIndex("IsActive")));
                    multipleDataDetails.setCreatedDate(cursor.getString(cursor.getColumnIndex("CreatedDate")));
                    multipleDataDetails.setServerUpdatedStatus(cursor.getInt(cursor.getColumnIndex("ServerUpdatedStatus")));
                    multipleDataDetails.setDesc(cursor.getString(cursor.getColumnIndex("Desc")));

                    mutipleDataListDetails.add(multipleDataDetails);
                } while (cursor.moveToNext());

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }
        return mutipleDataListDetails;
    }

    public List<LandlevellingFields> getlandlevelligfeildDetails(final String query) {
        List<LandlevellingFields> landlevellingDetails = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = mDatabase.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {

                    LandlevellingFields fieldDetails = new LandlevellingFields();
                    fieldDetails.setValue(cursor.getInt(cursor.getColumnIndex("Value")));
                    fieldDetails.setField(cursor.getString(cursor.getColumnIndex("Field")));
                    landlevellingDetails.add(fieldDetails);
                } while (cursor.moveToNext());

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }
        return landlevellingDetails;
    }

    public List<DisplayData> getdisplayDetails(final String query) {
        List<DisplayData> displayData = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = mDatabase.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {

                    DisplayData displayDetails = new DisplayData();
                    displayDetails.setFieldId(cursor.getInt(cursor.getColumnIndex("FieldId")));
                    displayDetails.setInputType(cursor.getString(cursor.getColumnIndex("InputType")));
                    displayDetails.setValue(cursor.getString(cursor.getColumnIndex("Value")));
                    displayDetails.setServerUpdatedStatus(cursor.getInt(cursor.getColumnIndex("ServerUpdatedStatus")));
                    displayDetails.setTransactionId(cursor.getString(cursor.getColumnIndex("TransactionId")));
                    displayData.add(displayDetails);
                } while (cursor.moveToNext());

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }
        return displayData;
    }

    public List<Integer> getGroupids(final String query) {
        List<Integer> groupIds = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = mDatabase.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {

                    Integer groupId = cursor.getInt(cursor.getColumnIndex("GroupId"));

                    groupIds.add(groupId);
                } while (cursor.moveToNext());

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }
        return groupIds;
    }


    public List<Integer> getsortids(final String query) {
        List<Integer> sortIds = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = mDatabase.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {

                    Integer sortId = cursor.getInt(cursor.getColumnIndex("SortOrder"));

                    sortIds.add(sortId);
                } while (cursor.moveToNext());

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }
        return sortIds;
    }

    public List<ActivityTasks> getActivityTasksDetails(final String query) {
        List<ActivityTasks> activityTaskDetails = new ArrayList<>();
        Log.d(DataAccessHandler.class.getSimpleName(), "===> getActivityTasksDetails Query :" + query);
        Cursor cursor = null;
        try {
            cursor = mDatabase.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {

                    ActivityTasks taskDetails = new ActivityTasks();
                    taskDetails.setId(cursor.getInt(cursor.getColumnIndex("Id")));
                    taskDetails.setActivityTypeId(cursor.getInt(cursor.getColumnIndex("ActivityTypeId")));
                    taskDetails.setDependency(cursor.getString(cursor.getColumnIndex("Dependency")));
                    taskDetails.setIsOptional(cursor.getInt(cursor.getColumnIndex("IsOptional")));
                    taskDetails.setBucket(cursor.getString(cursor.getColumnIndex("Bucket")));
                    taskDetails.setField(cursor.getString(cursor.getColumnIndex("Field")));
                    taskDetails.setItemCode(cursor.getString(cursor.getColumnIndex("ItemCode")));
                    taskDetails.setItemCodeName(cursor.getString(cursor.getColumnIndex("ItemCodeName")));
                    taskDetails.setGLCode(cursor.getString(cursor.getColumnIndex("GLCode")));
                    taskDetails.setGLName(cursor.getString(cursor.getColumnIndex("GLName")));
                    taskDetails.setInputType(cursor.getString(cursor.getColumnIndex("InputType")));
                    taskDetails.setUom(cursor.getString(cursor.getColumnIndex("UOM")));
                    taskDetails.setIsActive(cursor.getInt(cursor.getColumnIndex("IsActive")));
                    taskDetails.setCreatedByUserId(cursor.getInt(cursor.getColumnIndex("CreatedByUserId")));
                    taskDetails.setCreatedDate(cursor.getString(cursor.getColumnIndex("CreatedDate")));
                    taskDetails.setUpdatedByUserId(cursor.getInt(cursor.getColumnIndex("UpdatedByUserId")));
                    taskDetails.setUpdatedDate(cursor.getString(cursor.getColumnIndex("UpdatedDate")));
                    taskDetails.setDataType(cursor.getString(cursor.getColumnIndex("DataType")));
                    taskDetails.setGroupId(cursor.getInt(cursor.getColumnIndex("GroupId")));
                    taskDetails.setSortOrder(cursor.getInt(cursor.getColumnIndex("SortOrder")));

                    activityTaskDetails.add(taskDetails);
                } while (cursor.moveToNext());

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }
        return activityTaskDetails;
    }

    public List<Saplings> getSaplingDetails(final String query, final int type) {
        List<Saplings> saplingDataDetails = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = mDatabase.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {

                    Saplings saplingsDetails = new Saplings();
                    saplingsDetails.setId(cursor.getInt(cursor.getColumnIndex("Id")));
                    saplingsDetails.setNurseryCode(cursor.getString(cursor.getColumnIndex("NurseryCode")));
                    saplingsDetails.setConsignmentCode(cursor.getString(cursor.getColumnIndex("ConsignmentCode")));
                    saplingsDetails.setOriginId(cursor.getInt(cursor.getColumnIndex("OriginId")));
                    saplingsDetails.setVendorId(cursor.getInt(cursor.getColumnIndex("VendorId")));
                    saplingsDetails.setVarietyId(cursor.getInt(cursor.getColumnIndex("VarietyId")));
                    saplingsDetails.setPurchaseDate(cursor.getString(cursor.getColumnIndex("PurchaseDate")));
                    saplingsDetails.setEstimatedDate(cursor.getString(cursor.getColumnIndex("EstimatedDate")));
                    saplingsDetails.setEstimatedQuantity(cursor.getInt(cursor.getColumnIndex("EstimatedQuantity")));
                    saplingsDetails.setIsActive(cursor.getInt(cursor.getColumnIndex("IsActive")));
                    saplingsDetails.setCreatedByUserId(cursor.getInt(cursor.getColumnIndex("CreatedByUserId")));
                    saplingsDetails.setCreatedDate(CommonUtils.getPropeCreateDate(cursor.getString(cursor.getColumnIndex("CreatedDate"))));
                    saplingsDetails.setUpdatedByUserId(cursor.getInt(cursor.getColumnIndex("UpdatedByUserId")));
                    saplingsDetails.setUpdatedDate(cursor.getString(cursor.getColumnIndex("UpdatedDate")));
                    saplingsDetails.setServerUpdatedStatus(cursor.getInt(cursor.getColumnIndex("ServerUpdatedStatus")));
                    saplingsDetails.setStatusTypeId(cursor.getInt(cursor.getColumnIndex("StatusTypeId")));

                    saplingsDetails.setArrivedDate(cursor.getString(cursor.getColumnIndex("ArrivedDate")));
                    saplingsDetails.setArrivedQuantity(cursor.getInt(cursor.getColumnIndex("ArrivedQuantity")));
                    saplingsDetails.setSowingDate(cursor.getString(cursor.getColumnIndex("SowingDate")));
                    saplingsDetails.setTransplantingDate(cursor.getString(cursor.getColumnIndex("TransplantingDate")));
                    saplingsDetails.setSAPCode(cursor.getString(cursor.getColumnIndex("SAPCode")));
                    saplingsDetails.setCurrentClosingStock(cursor.getInt(cursor.getColumnIndex("CurrentClosingStock")));

                    saplingDataDetails.add(saplingsDetails);
                } while (cursor.moveToNext());

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }
        return saplingDataDetails;
    }


    public List<SaplingActivity> getSaplingActivityDetails(final String query, final int type) {
        List<SaplingActivity> saplingActivityDataDetails = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = mDatabase.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {

                    SaplingActivity saplingsactivityDetails = new SaplingActivity();
                  //  saplingsactivityDetails.setId(cursor.getInt(cursor.getColumnIndex("Id")));
                    saplingsactivityDetails.setTransactionId(cursor.getString(cursor.getColumnIndex("TransactionId")));
                    saplingsactivityDetails.setConsignmentCode(cursor.getString(cursor.getColumnIndex("ConsignmentCode")));
                    saplingsactivityDetails.setActivityId(cursor.getInt(cursor.getColumnIndex("ActivityId")));
                    saplingsactivityDetails.setStatusTypeId(cursor.getInt(cursor.getColumnIndex("StatusTypeId")));
                    saplingsactivityDetails.setComment(cursor.getString(cursor.getColumnIndex("Comment")));
                    saplingsactivityDetails.setIsActive(cursor.getInt(cursor.getColumnIndex("IsActive")));
                    saplingsactivityDetails.setCreatedByUserId(cursor.getInt(cursor.getColumnIndex("CreatedByUserId")));
                    saplingsactivityDetails.setCreatedDate(cursor.getString(cursor.getColumnIndex("CreatedDate")));
                    saplingsactivityDetails.setUpdatedByUserId(cursor.getInt(cursor.getColumnIndex("UpdatedByUserId")));
                    saplingsactivityDetails.setUpdatedDate(cursor.getString(cursor.getColumnIndex("UpdatedDate")));
                    saplingsactivityDetails.setServerUpdatedStatus(cursor.getInt(cursor.getColumnIndex("ServerUpdatedStatus")));

                    saplingActivityDataDetails.add(saplingsactivityDetails);
                } while (cursor.moveToNext());

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }
        return saplingActivityDataDetails;
    }
    public List<SaplingActivityXrefModel> getSaplingActivityXrefDetails(final String query, final int type) {
        List<SaplingActivityXrefModel> saplingActivityXrefDataDetails = new ArrayList<>();



        Cursor cursor = null;
        Log.v(LOG_TAG, "@@@ GradingRepo details query " + query);
        try {
            cursor = mDatabase.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    SaplingActivityXrefModel saplingsactivityxrefDetails = new SaplingActivityXrefModel();

                    String filelocation = cursor.getString(cursor.getColumnIndex("FilePath"));
                    if (filelocation != null) {
                        try {
                            saplingsactivityxrefDetails.setImageString(CommonUtils.encodeFileToBase64Binary(new File(filelocation)));
                            saplingsactivityxrefDetails.setFileExtension(".jpg");
                        } catch (Exception exc) {

                        }
                    }

//
//                    String imagestr="";
//                    String filelocation = cursor.getString(cursor.getColumnIndex("FileLocation"));
//                    Log.d(DataAccessHandler.class.getSimpleName(), "===> Analsis getSaplingActivityXrefDetails() FilePath :"+filelocation);
//
//                    // Log.v(LOG_TAG, "@@@ image base 64 ==>" + filelocation+"");
//
////                    if (!filelocation.isEmpty()) {
////                        File imagefile = new File(filelocation);
////                        FileInputStream fis = null;
////                        try {
////                            fis = new FileInputStream(imagefile);
////                        } catch (FileNotFoundException e) {
////                            e.printStackTrace();
////                        }
////
////                        Bitmap bm = BitmapFactory.decodeStream(fis);
////                        bm = ImageUtility.rotatePicture(90, bm);
////                        String base64string = ImageUtility.convertBitmapToString(bm);
////                        saplingsactivityxrefDetails.setImageString(base64string);
////                        //saplingsactivityxrefDetails.setFilePath(base64string);
////                    }
//                    if (!filelocation.isEmpty()) {
//                        try {
//
////                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
////                            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.addimage);
////                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
////                            byte[] imageBytes = baos.toByteArray();
////                            String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
//
//                            imagestr = CommonUtils.encodeFileToBase64Binary(new File(filelocation));
//
//
//                            //saplingsactivityxrefDetails.setImageString(imageString);
//                            //Log.v(LOG_TAG, "@@@ image base 64 ==>" + CommonUtils.encodeFileToBase64Binary(new File(filelocation)));
//
//                        } catch (Exception exc) {
//                            Log.d(DataAccessHandler.class.getSimpleName(), "===> Analsis Error getSaplingActivityXrefDetails() "+exc.getLocalizedMessage());
//                        }
//                    }
                    // saplingsactivityxrefDetails.setId(cursor.getInt(cursor.getColumnIndex("Id")));
                 //   saplingsactivityxrefDetails.setImageString(imagestr);
                    saplingsactivityxrefDetails.setTransactionId(cursor.getString(cursor.getColumnIndex("TransactionId")));
                    saplingsactivityxrefDetails.setFieldId(cursor.getInt(cursor.getColumnIndex("FieldId")));
                    saplingsactivityxrefDetails.setValue(cursor.getString(cursor.getColumnIndex("Value")));
                    saplingsactivityxrefDetails.setFilePath(cursor.getString(cursor.getColumnIndex("FilePath")));
                    saplingsactivityxrefDetails.setIsActive(cursor.getInt(cursor.getColumnIndex("IsActive")));
                    saplingsactivityxrefDetails.setCreatedByUserId(cursor.getInt(cursor.getColumnIndex("CreatedByUserId")));
                    saplingsactivityxrefDetails.setCreatedDate(cursor.getString(cursor.getColumnIndex("CreatedDate")));
                    saplingsactivityxrefDetails.setUpdatedByUserId(cursor.getInt(cursor.getColumnIndex("UpdatedByUserId")));
                    saplingsactivityxrefDetails.setUpdatedDate(cursor.getString(cursor.getColumnIndex("UpdatedDate")));
                    saplingsactivityxrefDetails.setServerUpdatedStatus(cursor.getInt(cursor.getColumnIndex("ServerUpdatedStatus")));
                    saplingsactivityxrefDetails.setLabourRate(cursor.getDouble(cursor.getColumnIndex("LabourRate")));

                   // saplingsactivityxrefDetails.setFileName(cursor.getString(cursor.getColumnIndex("FileName")));
                 //   saplingsactivityxrefDetails.setFileLocation(cursor.getString(cursor.getColumnIndex("FileLocation")));

                    saplingActivityXrefDataDetails.add(saplingsactivityxrefDetails);
                } while (cursor.moveToNext());

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }
        return saplingActivityXrefDataDetails;
    }

    public List<SaplingActivityHistoryModel> getSaplingActivityHistoryDetails(final String query, final int type) {
        List<SaplingActivityHistoryModel> saplingActivityHistoryDataDetails = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = mDatabase.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {

                    SaplingActivityHistoryModel saplingsactivityhistoryDetails = new SaplingActivityHistoryModel();
                  //  saplingsactivityhistoryDetails.setId(cursor.getInt(cursor.getColumnIndex("Id")));
                    saplingsactivityhistoryDetails.setTransactionId(cursor.getString(cursor.getColumnIndex("TransactionId")));
                    saplingsactivityhistoryDetails.setStatusTypeId(cursor.getInt(cursor.getColumnIndex("StatusTypeId")));
                    saplingsactivityhistoryDetails.setComments(cursor.getString(cursor.getColumnIndex("Comments")));
                    saplingsactivityhistoryDetails.setCreatedByUserId(cursor.getInt(cursor.getColumnIndex("CreatedByUserId")));
                    saplingsactivityhistoryDetails.setCreatedDate(cursor.getString(cursor.getColumnIndex("CreatedDate")));
                    saplingsactivityhistoryDetails.setServerUpdatedStatus(cursor.getInt(cursor.getColumnIndex("ServerUpdatedStatus")));

                    saplingActivityHistoryDataDetails.add(saplingsactivityhistoryDetails);
                } while (cursor.moveToNext());

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }
        return saplingActivityHistoryDataDetails;
    }
    public List<NurseryIrrigationLogForDb> getIrrigationDetails(final String query, final int type) {
        List<NurseryIrrigationLogForDb> NurseryIrrigationLogList = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = mDatabase.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {

                    NurseryIrrigationLogForDb nurseryIrrigationLog = new NurseryIrrigationLogForDb();
                    nurseryIrrigationLog.setId(cursor.getInt(cursor.getColumnIndex("Id")));
                    nurseryIrrigationLog.setLogDate(cursor.getString(cursor.getColumnIndex("LogDate")));
                    nurseryIrrigationLog.setIrrigationCode(cursor.getString(cursor.getColumnIndex("IrrigationCode")));
                    nurseryIrrigationLog.setRegularMale(cursor.getDouble(cursor.getColumnIndex("RegularMale")));
                    nurseryIrrigationLog.setRegularFemale(cursor.getDouble(cursor.getColumnIndex("RegularFemale")));
                    nurseryIrrigationLog.setContractMale(cursor.getDouble(cursor.getColumnIndex("ContractMale")));
                    nurseryIrrigationLog.setContractFemale(cursor.getDouble(cursor.getColumnIndex("ContractFemale")));
                    nurseryIrrigationLog.setStatusTypeId(cursor.getInt(cursor.getColumnIndex("StatusTypeId")));
                    nurseryIrrigationLog.setComments(cursor.getString(cursor.getColumnIndex("Comments")));
                    nurseryIrrigationLog.setIsActive(cursor.getInt(cursor.getColumnIndex("IsActive") ));
                    nurseryIrrigationLog.setCreatedByUserId(cursor.getInt(cursor.getColumnIndex("CreatedByUserId")));
                    nurseryIrrigationLog.setCreatedDate(cursor.getString(cursor.getColumnIndex("CreatedDate")));
                    nurseryIrrigationLog.setUpdatedByUserId(cursor.getInt(cursor.getColumnIndex("UpdatedByUserId")));
                    nurseryIrrigationLog.setUpdatedDate(cursor.getString(cursor.getColumnIndex("UpdatedDate")));
                    nurseryIrrigationLog.setRegularMaleRate(cursor.getDouble(cursor.getColumnIndex("RegularMaleRate")));
                    nurseryIrrigationLog.setRegularFeMaleRate(cursor.getDouble(cursor.getColumnIndex("RegularFeMaleRate")));
                    nurseryIrrigationLog.setContractMaleRate(cursor.getDouble(cursor.getColumnIndex("ContractMaleRate")));
                    nurseryIrrigationLog.setContractFeMaleRate(cursor.getDouble(cursor.getColumnIndex("ContractFeMaleRate")));

                    NurseryIrrigationLogList.add(nurseryIrrigationLog);
                } while (cursor.moveToNext());

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }
        return NurseryIrrigationLogList;
    }
    public List<NurseryIrrigationLogXref> getIrrigationDetailsXref(final String query, final int type) {
        List<NurseryIrrigationLogXref> NurseryIrrigationLogList = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = mDatabase.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {

                    NurseryIrrigationLogXref nurseryIrrigationLogXref = new NurseryIrrigationLogXref();
                    nurseryIrrigationLogXref.setId(cursor.getInt(cursor.getColumnIndex("Id")));
                    nurseryIrrigationLogXref.setIrrigationCode(cursor.getString(cursor.getColumnIndex("IrrigationCode")));
                    nurseryIrrigationLogXref.setConsignmentCode(cursor.getString(cursor.getColumnIndex("ConsignmentCode")));
                    nurseryIrrigationLogXref.setActive(cursor.getInt(cursor.getColumnIndex("IsActive")));
                    nurseryIrrigationLogXref.setCreatedByUserId(cursor.getInt(cursor.getColumnIndex("CreatedByUserId")));
                    nurseryIrrigationLogXref.setCreatedDate(cursor.getString(cursor.getColumnIndex("CreatedDate")));
                    nurseryIrrigationLogXref.setUpdatedByUserId(cursor.getInt(cursor.getColumnIndex("UpdatedByUserId")));
                    nurseryIrrigationLogXref.setUpdatedDate(cursor.getString(cursor.getColumnIndex("UpdatedDate")));

                    NurseryIrrigationLogList.add(nurseryIrrigationLogXref);
                } while (cursor.moveToNext());

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }
        return NurseryIrrigationLogList;
    }

    public List<SaplingActivityStatusModel> getSaplingActivityStatusDetails(final String query, final int type) {
        List<SaplingActivityStatusModel> saplingActivitystatusDataDetails = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = mDatabase.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {

                    SaplingActivityStatusModel saplingsactivitystatusDetails = new SaplingActivityStatusModel();
                  //  saplingsactivitystatusDetails.setId(cursor.getInt(cursor.getColumnIndex("Id")));
                    saplingsactivitystatusDetails.setConsignmentCode(cursor.getString(cursor.getColumnIndex("ConsignmentCode")));
                    saplingsactivitystatusDetails.setActivityId(cursor.getInt(cursor.getColumnIndex("ActivityId")));
                    saplingsactivitystatusDetails.setStatusTypeId(cursor.getInt(cursor.getColumnIndex("StatusTypeId")));
                    saplingsactivitystatusDetails.setCreatedByUserId(cursor.getInt(cursor.getColumnIndex("CreatedByUserId")));
                    saplingsactivitystatusDetails.setCreatedDate(cursor.getString(cursor.getColumnIndex("CreatedDate")));
                    saplingsactivitystatusDetails.setUpdatedByUserId(cursor.getInt(cursor.getColumnIndex("UpdatedByUserId")));
                    saplingsactivitystatusDetails.setUpdatedDate(cursor.getString(cursor.getColumnIndex("UpdatedDate")));
                    saplingsactivitystatusDetails.setServerUpdatedStatus(cursor.getInt(cursor.getColumnIndex("ServerUpdatedStatus")));

                    saplingsactivitystatusDetails.setJobCompletedDate(cursor.getString(cursor.getColumnIndex("JobCompletedDate")));

                    saplingActivitystatusDataDetails.add(saplingsactivitystatusDetails);
                } while (cursor.moveToNext());

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }
        return saplingActivitystatusDataDetails;
    }




    //Falog_Tracking...
    public T getGpsTrackingData(final String query, final int type) {
        LocationTracker mGpsBoundaries = null;
        List<LocationTracker> mGpsBoundariesList = new ArrayList<>();
        Cursor cursor = null;
        Log.v(LOG_TAG, "@@@ GeoBoundaries query " + query);
        try {
            cursor = mDatabase.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    mGpsBoundaries = new LocationTracker();

                    mGpsBoundaries.setUserId(cursor.getInt(1));
                    mGpsBoundaries.setLatitude(cursor.getDouble(2));
                    mGpsBoundaries.setLongitude(cursor.getDouble(3));
                    mGpsBoundaries.setAddress(cursor.getString(4));
                    mGpsBoundaries.setLogDate(cursor.getString(5));
                    //mGpsBoundaries.setServerUpdatedStatus(cursor.getInt(6));
                    if (type == 1) {
                        mGpsBoundariesList.add(mGpsBoundaries);
                    }

                    Log.v(LOG_TAG, "Lat@Log" + String.valueOf(mGpsBoundariesList));


                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "@@@ getting user details " + e.getMessage());
        }
        return (T) ((type == 0) ? mGpsBoundaries : mGpsBoundariesList);

    }


    public void upNotificationStatus() {
        ContentValues update_values = new ContentValues();
        update_values.put("ServerUpdatedStatus", "0");
        update_values.put("isRead", 1);
        update_values.put("UpdatedDate", CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
        String where = " isRead ='" + 0 + "'";
        mDatabase.update("Alerts", update_values, where, null);
    }


    public String getFalogLatLongs(final String query) {
        Log.v(LOG_TAG, "@@@ Generic Query " + query);
        String latlongData = "";
        Cursor genericDataQuery = mDatabase.rawQuery(query, null);
        try {
            if (genericDataQuery.getCount() > 0 && genericDataQuery.moveToFirst()) {
                do {
                    latlongData = (genericDataQuery.getDouble(0) + "-" + genericDataQuery.getDouble(1));
                } while (genericDataQuery.moveToNext());
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        } finally {
            if (null != genericDataQuery)
                genericDataQuery.close();

            closeDataBase();
        }
        return latlongData;
    }




    public List<NurseryIrrigationLog> getirigationlogs(final String query) {
        Log.d(DataAccessHandler.class.getSimpleName(), "====> Analysis ==> GET Irrigation :" + query);
        List<NurseryIrrigationLog> irrigationlogDetails = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = mDatabase.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {

                    NurseryIrrigationLog nurseryIrrigationLog  = new NurseryIrrigationLog();
                    nurseryIrrigationLog.setIrrigationCode(cursor.getString(cursor.getColumnIndex("IrrigationCode")));
                    nurseryIrrigationLog.setLogDate(cursor.getString(cursor.getColumnIndex("LogDate")));
                    nurseryIrrigationLog.setRegularMale(cursor.getDouble(cursor.getColumnIndex("RegularMale")));
                    nurseryIrrigationLog.setRegularFemale(cursor.getDouble(cursor.getColumnIndex("RegularFemale")));
                    nurseryIrrigationLog.setContractMale(cursor.getDouble(cursor.getColumnIndex("ContractMale")));
                    nurseryIrrigationLog.setContractFemale(cursor.getDouble(cursor.getColumnIndex("ContractFemale")));
                    nurseryIrrigationLog.setStatusTypeId(cursor.getInt(cursor.getColumnIndex("StatusTypeId")));
                    nurseryIrrigationLog.setComments(cursor.getString(cursor.getColumnIndex("Comments")));
                    nurseryIrrigationLog.setDesc(cursor.getString(cursor.getColumnIndex("Desc")));
                    nurseryIrrigationLog.setRegularMaleRate(cursor.getDouble(cursor.getColumnIndex("RegularMaleRate")));
                    nurseryIrrigationLog.setContractFeMaleRate(cursor.getDouble(cursor.getColumnIndex("RegularFeMaleRate")));
                    nurseryIrrigationLog.setContractMaleRate(cursor.getDouble(cursor.getColumnIndex("ContractMaleRate")));
                    nurseryIrrigationLog.setContractFeMaleRate(cursor.getDouble(cursor.getColumnIndex("ContractFeMaleRate")));
//                    nurseryIrrigationLog.setCreatedByUserId(cursor.getInt(cursor.getColumnIndex("CreatedByUserId")));
//                    nurseryIrrigationLog.setCreatedDate(cursor.getString(cursor.getColumnIndex("CreatedDate")));
//                    nurseryIrrigationLog.setUpdatedByUserId(cursor.getInt(cursor.getColumnIndex("UpdatedByUserId")));
//                    nurseryIrrigationLog.setUpdatedDate(cursor.getString(cursor.getColumnIndex("UpdatedDate")));
//                    nurseryIrrigationLog.setServerUpdatedStatus(cursor.getInt(cursor.getColumnIndex("ServerUpdatedStatus")));

                    irrigationlogDetails.add(nurseryIrrigationLog);
                } while (cursor.moveToNext());

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }
        return irrigationlogDetails;
    }


    public List<NurseryIrrigationLogXref> getirigationlogxref(final String query) {
        Log.d(DataAccessHandler.class.getSimpleName(), "====> Analysis ==> GET Irrigation :" + query);
        List<NurseryIrrigationLogXref> irrigationlogxrefDetails = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = mDatabase.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {

                    NurseryIrrigationLogXref nurseryIrrigationLogxref  = new NurseryIrrigationLogXref();
                    nurseryIrrigationLogxref.setIrrigationCode(cursor.getString(cursor.getColumnIndex("IrrigationCode")));
                    nurseryIrrigationLogxref.setConsignmentCode(cursor.getString(cursor.getColumnIndex("ConsignmentCode")));
                    nurseryIrrigationLogxref.setDesc(cursor.getString(cursor.getColumnIndex("Desc")));
//                    nurseryIrrigationLog.setCreatedByUserId(cursor.getInt(cursor.getColumnIndex("CreatedByUserId")));
//

                    irrigationlogxrefDetails.add(nurseryIrrigationLogxref);
                } while (cursor.moveToNext());

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }
        return irrigationlogxrefDetails;
    }



    public List<CheckNurseryAcitivity> getNurseryCheckActivityDetails(final String query) {
        Log.d(DataAccessHandler.class.getSimpleName(), "====> Analysis ==> GET ACTIVITIES :" + query);
        List<CheckNurseryAcitivity> nurseryActivityDetails = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = mDatabase.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {



                    CheckNurseryAcitivity nurseryActivityyDetails = new CheckNurseryAcitivity();
                    nurseryActivityyDetails.setId(cursor.getInt(cursor.getColumnIndex("ActivityId")));
                    nurseryActivityyDetails.setActivityTypeId(cursor.getInt(cursor.getColumnIndex("ActivityTypeId")));
                    nurseryActivityyDetails.setIsMultipleEntries(cursor.getString(cursor.getColumnIndex("IsMultipleEntries")));
                    nurseryActivityyDetails.setActicityType(cursor.getString(cursor.getColumnIndex("ActicityType")));
                    nurseryActivityyDetails.setCode(cursor.getString(cursor.getColumnIndex("ActivityCode")));
                    nurseryActivityyDetails.setName(cursor.getString(cursor.getColumnIndex("ActivityName")));
                    nurseryActivityyDetails.setStatusTypeId(cursor.getInt(cursor.getColumnIndex("StatusTypeId")));
                    nurseryActivityyDetails.setDesc(cursor.getString(cursor.getColumnIndex("ActivityStatus")));
                    nurseryActivityyDetails.setActivityDoneDate(cursor.getString(cursor.getColumnIndex("ActivityDoneDate")));
                    nurseryActivityyDetails.setConsignmentCode(cursor.getString(cursor.getColumnIndex("ConsignmentCode")));
                    nurseryActivityyDetails.setTargetDate(cursor.getString(cursor.getColumnIndex("TargetDate")));
                    nurseryActivityDetails.add(nurseryActivityyDetails);
                } while (cursor.moveToNext());

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }
        return nurseryActivityDetails;
    }




    public List<CullinglossFileRepository>getCullinglossRepoDetails(final String query) {
        List<CullinglossFileRepository> Cullinglossrepolist = new ArrayList<>();
        CullinglossFileRepository cullinglossrepository = null;
        Cursor cursor = null;
        Log.v(LOG_TAG, "@@@ GradingRepo details query " + query);
        try {
            cursor = mDatabase.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    cullinglossrepository = new CullinglossFileRepository();

                    String filelocation = cursor.getString(cursor.getColumnIndex("FileLocation"));
                    if (filelocation != null) {
                        try {
                            cullinglossrepository.setImageString(CommonUtils.encodeFileToBase64Binary(new File(filelocation)));
                        } catch (Exception exc) {

                        }
                    }
                 cullinglossrepository.setId(cursor.getInt(cursor.getColumnIndex("Id")));
                    cullinglossrepository.setTransactionId(cursor.getString(cursor.getColumnIndex("TransactionId")));
                    cullinglossrepository.setFileName(cursor.getString(cursor.getColumnIndex("FileName")));
                    cullinglossrepository.setFileLocation(cursor.getString(cursor.getColumnIndex("FileLocation")));
                    cullinglossrepository.setFileExtension(cursor.getString(cursor.getColumnIndex("FileExtension")));
                    cullinglossrepository.setCreatedByUserId(cursor.getInt(cursor.getColumnIndex("CreatedByUserId")));
                    cullinglossrepository.setCreatedDate(cursor.getString(cursor.getColumnIndex("CreatedDate")));
                    cullinglossrepository.setServerUpdatedStatus(0);
                    Cullinglossrepolist.add(cullinglossrepository);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "@@@ getting GradingRepo details " + e.getMessage());
        }
return Cullinglossrepolist;
    }
    public List<Irrigationhistorymodel> getIrrigationHistoryDetails(final String query, final int type) {
        List<Irrigationhistorymodel> IrrigationHistoryDataDetails = new ArrayList<>();
        Cursor cursor = null;
        Log.v(LOG_TAG, "@@@ Irrigationhistory details query " + query);

        try {
            cursor = mDatabase.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {

                    Irrigationhistorymodel IrrigationhistoryDetails = new Irrigationhistorymodel();
                    IrrigationhistoryDetails.setId(cursor.getInt(cursor.getColumnIndex("Id")));
                    IrrigationhistoryDetails.setIrrigationCode(cursor.getString(cursor.getColumnIndex("IrrigationCode")));
                    IrrigationhistoryDetails.setStatusTypeId(cursor.getInt(cursor.getColumnIndex("StatusTypeId")));
                    IrrigationhistoryDetails.setComments(cursor.getString(cursor.getColumnIndex("Comments")));
                    IrrigationhistoryDetails.setIsActive(cursor.getInt(cursor.getColumnIndex("IsActive")));
                    IrrigationhistoryDetails.setCreatedByUserId(cursor.getInt(cursor.getColumnIndex("CreatedByUserId")));
                    IrrigationhistoryDetails.setCreatedDate(cursor.getString(cursor.getColumnIndex("CreatedDate")));
                    IrrigationhistoryDetails.setServerUpdatedStatus(cursor.getInt(cursor.getColumnIndex("ServerUpdatedStatus")));

                    IrrigationHistoryDataDetails.add(IrrigationhistoryDetails);
                } while (cursor.moveToNext());

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }
        return IrrigationHistoryDataDetails;
    }


    public String getGenerateActivityid(final String maxNum) {
       // String maxNum = getOnlyOneValueFromDb(query);
        String convertedNum = "";
        if (!TextUtils.isEmpty(maxNum)) {
            convertedNum = CommonUtils.serialNumber(Integer.parseInt(maxNum) , 3);
        } else {
            convertedNum = CommonUtils.serialNumber(1, 3);
        }
     //   StringBuilder farmerCoder = new StringBuilder();
        String finalNumber = StringUtils.leftPad(convertedNum,3,"0");

        Log.v(LOG_TAG, "@@@ finalNumber code " + finalNumber);
        return finalNumber;
    }



    public List<NurseryLabourLog> getnurserylabourlogs(final String query) {
        Log.d(DataAccessHandler.class.getSimpleName(), "====> Analysis ==> GET Nursery :" + query);
        List<NurseryLabourLog> Nurserylog = new ArrayList<>();
        Cursor cursor = null;

        try {
            cursor = mDatabase.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {

                    NurseryLabourLog nurserylabourLog  = new NurseryLabourLog();

                    nurserylabourLog.setLogDate(cursor.getString(cursor.getColumnIndex("LogDate")));
                    nurserylabourLog.setRegularMale(cursor.getDouble(cursor.getColumnIndex("RegularMale")));
                    nurserylabourLog.setRegularFemale(cursor.getDouble(cursor.getColumnIndex("RegularFemale")));
                    nurserylabourLog.setContractMale(cursor.getDouble(cursor.getColumnIndex("ContractMale")));
                    nurserylabourLog.setContractFemale(cursor.getDouble(cursor.getColumnIndex("ContractFemale")));
                    nurserylabourLog.setIsActive(cursor.getInt(cursor.getColumnIndex("IsActive")));
                    nurserylabourLog.setCreatedByUserId(cursor.getInt(cursor.getColumnIndex("CreatedByUserId")));
                    nurserylabourLog.setCreatedDate(cursor.getString(cursor.getColumnIndex("CreatedDate")));
                    nurserylabourLog.setUpdatedByUserId(cursor.getInt(cursor.getColumnIndex("UpdatedByUserId")));
                    nurserylabourLog.setUpdatedDate(cursor.getString(cursor.getColumnIndex("UpdatedDate")));
                    nurserylabourLog.setServerUpdatedStatus(cursor.getInt(cursor.getColumnIndex("ServerUpdatedStatus")));
                    nurserylabourLog.setNurseryCode(cursor.getString(cursor.getColumnIndex("NurseryCode")));

                    Nurserylog.add(nurserylabourLog);
                } while (cursor.moveToNext());

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }
        return Nurserylog;
    }
    public List<ConsignmentData> getConsignmentcode(final String query) {
        Log.d(LOG_TAG, "==> analysis GetConsinmentData :" + query);
        List<ConsignmentData> consignmentData = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = mDatabase.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {

                    ConsignmentData consignmentdetails = new ConsignmentData();
                    consignmentdetails.setId(cursor.getInt(cursor.getColumnIndex("Id")));
                    consignmentdetails.setConsignmentCode(cursor.getString(cursor.getColumnIndex("ConsignmentCode")));



                    consignmentData.add(consignmentdetails);
                } while (cursor.moveToNext());

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }
        return consignmentData;
    }


    public List<NurseryVisitLog> getNurseryVisitLog(final String query) {
        Log.v(LOG_TAG, "@@@ Nurseryvisit details query " + query);
        List<NurseryVisitLog> nurserySaplingDetails = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = mDatabase.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {

                    NurseryVisitLog nurseryVisitLogDetails = new NurseryVisitLog();
                    String filelocation = cursor.getString(cursor.getColumnIndex("FileLocation"));
                    if (filelocation != null) {
                        try {
                            nurseryVisitLogDetails.setImageString(CommonUtils.encodeFileToBase64Binary(new File(filelocation)));
                        } catch (Exception exc) {

                        }
                    }

                        nurseryVisitLogDetails.setId(cursor.getInt(cursor.getColumnIndex("Id")));
                        nurseryVisitLogDetails.setNurseryCode(cursor.getString(cursor.getColumnIndex("NurseryCode")));
                        nurseryVisitLogDetails.setLogTypeId(cursor.getInt(cursor.getColumnIndex("LogTypeId")));
                        nurseryVisitLogDetails.setCosignmentCode(cursor.getString(cursor.getColumnIndex("CosignmentCode")));
                        nurseryVisitLogDetails.setClientName(cursor.getString(cursor.getColumnIndex("ClientName")));
                        nurseryVisitLogDetails.setLogDate(cursor.getString(cursor.getColumnIndex("LogDate")));

                        nurseryVisitLogDetails.setComments(cursor.getString(cursor.getColumnIndex("Comments")));
                        nurseryVisitLogDetails.setFileName(cursor.getString(cursor.getColumnIndex("FileName")));
                       // nurseryVisitLogDetails.setFileLocation(cursor.getString(cursor.getColumnIndex("FileLocation")));
                        nurseryVisitLogDetails.setFileExtension(cursor.getString(cursor.getColumnIndex("FileExtension")));
                        nurseryVisitLogDetails.setCreatedByUserId(cursor.getInt(cursor.getColumnIndex("CreatedByUserId")));
                        nurseryVisitLogDetails.setCreatedDate(cursor.getString(cursor.getColumnIndex("CreatedDate")));
                        nurseryVisitLogDetails.setServerUpdatedStatus(cursor.getInt(cursor.getColumnIndex("ServerUpdatedStatus")));

                    nurserySaplingDetails.add(nurseryVisitLogDetails);
                    } while (cursor.moveToNext());

                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {

                if (cursor != null) {
                    cursor.close();
                }
            }
            return nurserySaplingDetails;
        }


    public List<ViewVisitLog> getviewNurseryVisitLog(final String query) {
        Log.v(LOG_TAG, "@@@ Nurseryvisit details query " + query);
        List<ViewVisitLog> nurserySaplingDetails = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = mDatabase.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {

                    ViewVisitLog nurseryVisitLogDetails = new ViewVisitLog();
                    nurseryVisitLogDetails.setNurseryname(cursor.getString(cursor.getColumnIndex("name")));
                    nurseryVisitLogDetails.setNurseryCode(cursor.getString(cursor.getColumnIndex("NurseryCode")));
                    nurseryVisitLogDetails.setLogTypeId(cursor.getInt(cursor.getColumnIndex("LogTypeId")));
                    nurseryVisitLogDetails.setCosignmentCode(cursor.getString(cursor.getColumnIndex("CosignmentCode")));
                    nurseryVisitLogDetails.setClientName(cursor.getString(cursor.getColumnIndex("ClientName")));
                    nurseryVisitLogDetails.setLogDate(cursor.getString(cursor.getColumnIndex("LogDate")));
                    nurseryVisitLogDetails.setComments(cursor.getString(cursor.getColumnIndex("Comments")));
                    nurseryVisitLogDetails.setFileLocation(cursor.getString(cursor.getColumnIndex("FileLocation")));
                    nurseryVisitLogDetails.setLogtype(cursor.getString(cursor.getColumnIndex("Desc")));

                    nurserySaplingDetails.add(nurseryVisitLogDetails);
                } while (cursor.moveToNext());

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }
        return nurserySaplingDetails;
    }


    public List<NurseryRMActivity> getNurseryRMActivities(final String query) {    // Get Nursery RM Activities
        List<NurseryRMActivity> nurseryrmData = new ArrayList<>();
        Log.d(LOG_TAG, "=== > Analysis ==> getNurseryRMActivity:" + query);
        Cursor cursor = null;
        try {
            cursor = mDatabase.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {

                    NurseryRMActivity nurseryDataDetails = new NurseryRMActivity();
                    nurseryDataDetails.setTypeCdId(cursor.getInt(cursor.getColumnIndex("TypeCdId")));
                    nurseryDataDetails.setClassTypeId(cursor.getInt(cursor.getColumnIndex("ClassTypeId")));
                    nurseryDataDetails.setDesc(cursor.getString(cursor.getColumnIndex("Desc")));
                    nurseryDataDetails.setTableName(cursor.getString(cursor.getColumnIndex("TableName")));

                    nurseryrmData.add(nurseryDataDetails);
                } while (cursor.moveToNext());

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }
        return nurseryrmData;
    }

    public List<NurseryRMTransctions> getNurseryrmTransactionsg(final String query) {
        Log.v(LOG_TAG, "@@@ Nurseryvisit details query " + query);
        List<NurseryRMTransctions> nurseryrmtransactions = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = mDatabase.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {

                    NurseryRMTransctions rmtransactions = new NurseryRMTransctions();
                    rmtransactions.setTransactionId(cursor.getString(cursor.getColumnIndex("TransactionId")));
                    rmtransactions.setActivityTypeId(cursor.getInt(cursor.getColumnIndex("ActivityTypeId")));
                    rmtransactions.setStatusTypeId(cursor.getInt(cursor.getColumnIndex("StatusTypeId")));
                    rmtransactions.setCreatedDate(cursor.getString(cursor.getColumnIndex("CreatedDate")));
                    rmtransactions.setDesc(cursor.getString(cursor.getColumnIndex("Desc")));
                    rmtransactions.setActivityId(cursor.getInt(cursor.getColumnIndex("ActivityId")));
                    rmtransactions.setNurseryCode(cursor.getString(cursor.getColumnIndex("NurseryCode")));
                    nurseryrmtransactions.add(rmtransactions);
                } while (cursor.moveToNext());

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }
        return nurseryrmtransactions;
    }

    public List<RMTransactions> getRMTransactionsData(final String query) {
        Log.d(DataAccessHandler.class.getSimpleName(), "====> Analysis ==> GET Nursery :" + query);
        List<RMTransactions> RMTransactionsLog = new ArrayList<>();
        Cursor cursor = null;

        try {
            cursor = mDatabase.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {

                    RMTransactions rmTransactions  = new RMTransactions();

                    String filelocation = cursor.getString(cursor.getColumnIndex("FileLocation"));
                    if (filelocation != null) {
                        try {
                            rmTransactions.setByteImage(CommonUtils.encodeFileToBase64Binary(new File(filelocation)));
                        } catch (Exception exc) {

                        }
                    }

                   // rmTransactions.setId(cursor.getInt(cursor.getColumnIndex("Id")));
                    rmTransactions.setTransactionId(cursor.getString(cursor.getColumnIndex("TransactionId")));
                    rmTransactions.setNurseryCode(cursor.getString(cursor.getColumnIndex("NurseryCode")));
                    rmTransactions.setActivityId(cursor.getInt(cursor.getColumnIndex("ActivityId")));
                    rmTransactions.setActivityName(cursor.getString(cursor.getColumnIndex("ActivityName")));
                    rmTransactions.setActivityTypeId(cursor.getInt(cursor.getColumnIndex("ActivityTypeId")));
                    rmTransactions.setStatusTypeId(cursor.getInt(cursor.getColumnIndex("StatusTypeId")));
                    rmTransactions.setTransactionDate(cursor.getString(cursor.getColumnIndex("TransactionDate")));
                    rmTransactions.setMaleRegular(cursor.getDouble(cursor.getColumnIndex("MaleRegular")));
                    rmTransactions.setFemaleRegular(cursor.getDouble(cursor.getColumnIndex("FemaleRegular")));

                    rmTransactions.setMaleOutside(cursor.getDouble(cursor.getColumnIndex("MaleOutside")));
                    rmTransactions.setFemaleOutside(cursor.getDouble(cursor.getColumnIndex("FemaleOutside")));
                    rmTransactions.setMaleRegularCost(cursor.getDouble(cursor.getColumnIndex("MaleRegularCost")));
                    rmTransactions.setFemaleRegularCost(cursor.getDouble(cursor.getColumnIndex("FemaleRegularCost")));
                    rmTransactions.setMaleOutsideCost(cursor.getDouble(cursor.getColumnIndex("MaleOutsideCost")));
                    rmTransactions.setFemaleoutsideCost(cursor.getDouble(cursor.getColumnIndex("FemaleoutsideCost")));
                    rmTransactions.setExpenseType(cursor.getString(cursor.getColumnIndex("ExpenseType")));
                    rmTransactions.setUOMId(cursor.getInt(cursor.getColumnIndex("UOMId")));
                    rmTransactions.setQuantity(cursor.getDouble(cursor.getColumnIndex("Quantity")));
                    rmTransactions.setTotalCost(cursor.getDouble(cursor.getColumnIndex("TotalCost")));
                    rmTransactions.setComments(cursor.getString(cursor.getColumnIndex("Comments")));

                    rmTransactions.setFileName(cursor.getString(cursor.getColumnIndex("FileName")));
                    //rmTransactions.setFileLocation(cursor.getString(cursor.getColumnIndex("FileLocation")));
                    rmTransactions.setFileExtension(cursor.getString(cursor.getColumnIndex("FileExtension")));
                    rmTransactions.setRemarks(cursor.getString(cursor.getColumnIndex("Remarks")));
                    rmTransactions.setCreatedByUserId(cursor.getInt(cursor.getColumnIndex("CreatedByUserId")));
                    rmTransactions.setCreatedDate(cursor.getString(cursor.getColumnIndex("CreatedDate")));
                    rmTransactions.setUpdatedByUserId(cursor.getString(cursor.getColumnIndex("UpdatedByUserId")));
                    rmTransactions.setUpdatedDate(cursor.getString(cursor.getColumnIndex("UpdatedDate")));
                    rmTransactions.setServerUpdatedStatus(cursor.getInt(cursor.getColumnIndex("ServerUpdatedStatus")));

                    RMTransactionsLog.add(rmTransactions);
                } while (cursor.moveToNext());

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }
        return RMTransactionsLog;
    }


    public List<RMTransactionsStatusHistory> getRMTransactionsStatusHistoryData(final String query) {
        Log.d(DataAccessHandler.class.getSimpleName(), "====> Analysis ==> GET Nursery :" + query);
        List<RMTransactionsStatusHistory> RMTransactionsStatus = new ArrayList<>();
        Cursor cursor = null;

        try {
            cursor = mDatabase.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {

                    RMTransactionsStatusHistory rmTransactionsStatusHistory  = new RMTransactionsStatusHistory();

                   // rmTransactionsStatusHistory.setId(cursor.getInt(cursor.getColumnIndex("Id")));
                    rmTransactionsStatusHistory.setTransactionId(cursor.getString(cursor.getColumnIndex("TransactionId")));
                    rmTransactionsStatusHistory.setStatusTypeId(cursor.getInt(cursor.getColumnIndex("StatusTypeId")));
                    rmTransactionsStatusHistory.setCreatedByUserId(cursor.getInt(cursor.getColumnIndex("CreatedByUserId")));
                    rmTransactionsStatusHistory.setCreatedDate(cursor.getString(cursor.getColumnIndex("CreatedDate")));
                    rmTransactionsStatusHistory.setRemarks(cursor.getString(cursor.getColumnIndex("Remarks")));
                    rmTransactionsStatusHistory.setServerUpdatedStatus(cursor.getInt(cursor.getColumnIndex("ServerUpdatedStatus")));

                    RMTransactionsStatus.add(rmTransactionsStatusHistory);
                } while (cursor.moveToNext());

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }
        return RMTransactionsStatus;
    }

    public List<RMTransactions> getRMTransactions(final String query) {
        Log.d(DataAccessHandler.class.getSimpleName(), "====> Analysis ==> GET Nursery :" + query);
        List<RMTransactions> RMTransactionsLog = new ArrayList<>();
        Cursor cursor = null;

        try {
            cursor = mDatabase.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {

                    RMTransactions rmTransactions  = new RMTransactions();
            rmTransactions.setTransactionId(cursor.getString(cursor.getColumnIndex("TransactionId")));

                    rmTransactions.setActivityName(cursor.getString(cursor.getColumnIndex("ActivityName")));
                    rmTransactions.setActivityTypeId(cursor.getInt(cursor.getColumnIndex("ActivityTypeId")));

                    rmTransactions.setMaleRegular(cursor.getDouble(cursor.getColumnIndex("MaleRegular")));
                    rmTransactions.setFemaleRegular(cursor.getDouble(cursor.getColumnIndex("FemaleRegular")));

                    rmTransactions.setMaleOutside(cursor.getDouble(cursor.getColumnIndex("MaleOutside")));
                    rmTransactions.setFemaleOutside(cursor.getDouble(cursor.getColumnIndex("FemaleOutside")));

                    rmTransactions.setExpenseType(cursor.getString(cursor.getColumnIndex("ExpenseType")));
                    rmTransactions.setUOMId(cursor.getInt(cursor.getColumnIndex("UOMId")));
                    rmTransactions.setQuantity(cursor.getDouble(cursor.getColumnIndex("Quantity")));
                    rmTransactions.setTotalCost(cursor.getDouble(cursor.getColumnIndex("TotalCost")));
                    rmTransactions.setComments(cursor.getString(cursor.getColumnIndex("Comments")));
                    rmTransactions.setFileName(cursor.getString(cursor.getColumnIndex("FileName")));
                    rmTransactions.setFileLocation(cursor.getString(cursor.getColumnIndex("FileLocation")));



                    RMTransactionsLog.add(rmTransactions);
                } while (cursor.moveToNext());

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }
        return RMTransactionsLog;
    }
    public List<ConsignmentReports> getConsignmentdetails(final String query) {
        Log.d(LOG_TAG, "==> analysis GetConsinmentData :" + query);
        List<ConsignmentReports> consignmentData = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = mDatabase.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    ConsignmentReports consignmentdetails = new ConsignmentReports();
                    consignmentdetails.setId(cursor.getInt(cursor.getColumnIndex("Id")));
                    consignmentdetails.setNurseryCode(cursor.getString(cursor.getColumnIndex("NurseryCode")));
                    consignmentdetails.setNurseryName(cursor.getString(cursor.getColumnIndex("NurseryName")));
                    consignmentdetails.setConsignmentCode(cursor.getString(cursor.getColumnIndex("ConsignmentCode")));
                    consignmentdetails.setSAPId(cursor.getString(cursor.getColumnIndex("SAPCode")));
                    consignmentdetails.setOriginname(cursor.getString(cursor.getColumnIndex("Originname")));
                    consignmentdetails.setVendorname(cursor.getString(cursor.getColumnIndex("Vendorname")));
                    consignmentdetails.setVarietyname(cursor.getString(cursor.getColumnIndex("Varietyname")));
                    consignmentdetails.setSowingDate(cursor.getString(cursor.getColumnIndex("SowingDate")));
  consignmentdetails.setArrivalquantity(cursor.getInt(cursor.getColumnIndex("ArrivedQuantity")));
                    consignmentdetails.setTransplantDate(cursor.getString(cursor.getColumnIndex("TransplantingDate")));
                    consignmentdetails.setClosingStock(cursor.getInt(cursor.getColumnIndex("CurrentClosingStock")));
                    consignmentdetails.setStatus(cursor.getString(cursor.getColumnIndex("Status")));


                    consignmentData.add(consignmentdetails);
                } while (cursor.moveToNext());

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }
        return consignmentData;
    }



    public List<Visitdata> getvisiteddata(final String query) {
        Log.d(LOG_TAG, "==> analysis getvisiteddata :" + query);
        List<Visitdata> visitedData = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = mDatabase.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Visitdata visitdetails = new Visitdata();
                    visitdetails.setVisitedBy(cursor.getString(cursor.getColumnIndex("VisitedBy")));
                    visitdetails.setConsignmentCode(cursor.getString(cursor.getColumnIndex("ConsignmentCode")));
                    visitdetails.setRemarkes(cursor.getString(cursor.getColumnIndex("Remarkes")));
                    visitdetails.setUpdatedBy(cursor.getString(cursor.getColumnIndex("UpdatedBy")));

                    visitedData.add(visitdetails);
                } while (cursor.moveToNext());

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }
        return visitedData;
    }


    public List<FertilizerDetails> getfertilizerdata(final String query) {
        Log.d(LOG_TAG, "==> analysis getfertilizerdata :" + query);
        List<FertilizerDetails> FerilizerdataData = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = mDatabase.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    FertilizerDetails fertilizerdetails = new FertilizerDetails();
                    fertilizerdetails.setActivityId(cursor.getInt(cursor.getColumnIndex("ActivityId")));
                    fertilizerdetails.setActivityName(cursor.getString(cursor.getColumnIndex("ActivityName")));
                    fertilizerdetails.setProductname(cursor.getString(cursor.getColumnIndex("Productname")));
                    fertilizerdetails.setUOM(cursor.getString(cursor.getColumnIndex("UOM")));
                    fertilizerdetails.setQuantity(cursor.getString(cursor.getColumnIndex("Quantity")));
                    fertilizerdetails.setDoneBy(cursor.getString(cursor.getColumnIndex("DoneBy")));
                    fertilizerdetails.setDoneOn(cursor.getString(cursor.getColumnIndex("DoneOn")));


                    FerilizerdataData.add(fertilizerdetails);
                } while (cursor.moveToNext());

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }
        return FerilizerdataData;
    }

}




