package com.calibrage.palmroot.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.calibrage.palmroot.common.CommonConstants;

import static android.content.Context.MODE_PRIVATE;

public class DataBaseUpgrade {

    private static final String LOG_TAG = DataBaseUpgrade.class.getName();

    static void upgradeDataBase(final Context context, final SQLiteDatabase db) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("appprefs", MODE_PRIVATE);
        boolean result = true;
        try {
            boolean isFreshInstall = sharedPreferences.getBoolean(CommonConstants.IS_FRESH_INSTALL, true);
            if (isFreshInstall) {
                upgradeDb1(db);
                upgradeDB2(db);
                upgradeDB3(db);
                upgradeDB4(db);

            } else {
                boolean isDbUpgradeFinished = sharedPreferences.getBoolean(String.valueOf(Palm3FoilDatabase.DATA_VERSION), false);
                Log.v(LOG_TAG, "@@@@ database....." + isDbUpgradeFinished);
                if (!isDbUpgradeFinished) {
                    switch (Palm3FoilDatabase.DATA_VERSION) {
                        case 1:
//                            UiUtils.showCustomToastMessage("Updating database 6-->" + Palm3FoilDatabase.DATA_VERSION, context, 0);
                            upgradeDb1(db);
                            break;
                        case 2:
//                            UiUtils.showCustomToastMessage("Updating database 6-->" + Palm3FoilDatabase.DATA_VERSION, context, 0);
                            upgradeDB2(db);
                            break;
                        case 3:
//                            UiUtils.showCustomToastMessage("Updating database 6-->" + Palm3FoilDatabase.DATA_VERSION, context, 0);
                            upgradeDB3(db);
                            break;
                        case 4:
//                            UiUtils.showCustomToastMessage("Updating database 6-->" + Palm3FoilDatabase.DATA_VERSION, context, 0);
                            upgradeDB4(db);
                            break;

                    }
                } else {
                    Log.v(LOG_TAG, "@@@@ database is already upgraded " + Palm3FoilDatabase.DATA_VERSION);
                }
            }

        } catch (Exception e) {
            Log.e(LOG_TAG, String.valueOf(e));
            result = false;
        } finally {
            if (result) {
                Log.v(LOG_TAG, "@@@@ database is upgraded " + Palm3FoilDatabase.DATA_VERSION);
            } else {
                Log.e(LOG_TAG, "@@@@ database is upgrade failed or already upgraded");
            }
            sharedPreferences.edit().putBoolean(CommonConstants.IS_FRESH_INSTALL, false).apply();
            sharedPreferences.edit().putBoolean(String.valueOf(Palm3FoilDatabase.DATA_VERSION), true).apply();
        }
    }


    public static void upgradeDb1(final SQLiteDatabase db) {
        Log.d(LOG_TAG, "******* upgradeDataBase " + Palm3FoilDatabase.DATA_VERSION);

        // String alterGeoBoundariesTable1 = "ALTER TABLE GeoBoundaries ADD COLUMN CropMaintenanceCode VARCHAR (60)";

        String IrrigationLog1 = "Alter Table NurseryIrrigationLog Add RegularMaleRate FLOAT";
        String IrrigationLog2 = "Alter Table NurseryIrrigationLog Add RegularFeMaleRate FLOAT";
        String IrrigationLog3 = "Alter Table NurseryIrrigationLog Add ContractMaleRate FLOAT";
        String IrrigationLog4 = "Alter Table NurseryIrrigationLog Add ContractFeMaleRate FLOAT";


        String column1 = "Alter Table Sapling Add StatusTypeId int";
        String column2 = "Alter Table Sapling Add ArrivedDate datetime";
        String column3 = "Alter Table Sapling Add ArrivedQuantity int";
        String column4 = "Alter Table Sapling Add SowingDate datetime";
        String column5 = "Alter Table Sapling Add TransplantingDate datetime";
        String column6 = "Alter Table NurseryActivity Add DependentActivityCode VARCHAR(10)";


        String CREATE_LABOUR_RATE = "CREATE TABLE LabourRate(\n" +
                "Id INTEGER   PRIMARY KEY AUTOINCREMENT\n" +
                "                                      NOT NULL,\n" +
                "NurseryCode INT NOT NULL ,\n" +
                "Key VARCHAR NOT NULL ,\n" +
                "Value FLOAT NOT NULL ,\n" +
                "CreatedByUserId int NOT NULL ,\n" +
                "CreatedDate INT datetime NOT NULL\n" +
                ")";


        try {
            db.execSQL(column6);
            db.execSQL(IrrigationLog1);
            db.execSQL(IrrigationLog2);
            db.execSQL(IrrigationLog3);
            db.execSQL(IrrigationLog4);
            db.execSQL(column1);
            db.execSQL(column2);
            db.execSQL(column3);
            db.execSQL(column4);
            db.execSQL(column5);


            db.execSQL(CREATE_LABOUR_RATE);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void upgradeDB2(final SQLiteDatabase db) {
        Log.d(LOG_TAG, "******* upgradeDataBase " + Palm3FoilDatabase.DATA_VERSION);

        // String alterGeoBoundariesTable1 = "ALTER TABLE GeoBoundaries ADD COLUMN CropMaintenanceCode VARCHAR (60)";

        String column1 = "Alter table Saplingactivitystatus add JobCompletedDate DATETIME";
        try {

            db.execSQL(column1);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void upgradeDB3(final SQLiteDatabase db) {
        Log.d(LOG_TAG, "******* upgradeDataBase " + Palm3FoilDatabase.DATA_VERSION);

        String column2 = "Alter table Nursery add  CostCenter varchar(50)";
        String column3 = "alter table Nursery add SAPCode varchar(5) ";
        String column1 = "Alter table NurseryActivity add Bucket VARCHAR(50)";
        String column4 = "Alter table Sapling add SAPCode varchar(50)";

        String alertsTable = "CREATE TABLE Alerts( \n" +
                "Id INTEGER, \n" +
                "Name VARCHAR, \n" +
                "[Desc] VARCHAR, \n" +
                "UserId INT NOT NULL, \n" +
                "HTMLDesc Varchar(2000), \n" +
                "IsRead INT NOT NULL, \n" +
                "PlotCode VARCHAR, \n" +
                "ComplaintCode VARCHAR, \n" +
                "AlertTypeId INT, \n" +
                "CreatedByUserId INT,\n" +
                "CreatedDate VARCHAR,\n" +
                "UpdatedByUserId INT, \n" +
                "UpdatedDate VARCHAR,\n" +
                "ServerUpdatedStatus INT\n" +
                ")";


        try {

            db.execSQL(column1);
            db.execSQL(column2);
            db.execSQL(column3);
            db.execSQL(column4);
            db.execSQL(alertsTable);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void upgradeDB4(final SQLiteDatabase db) {
        Log.d(LOG_TAG, "******* upgradeDataBase " + Palm3FoilDatabase.DATA_VERSION);


        String cullingLossFileRepository = "CREATE TABLE CullingLossFileRepository( \n" +
                "Id INTEGER   PRIMARY KEY AUTOINCREMENT\n" +
                "                                      NOT NULL,\n" +
                "TransactionId VARCHAR, \n" +
                "FileName VARCHAR, \n" +
                "FileLocation VARCHAR, \n" +
                "FileExtension VARCHAR, \n" +
                "CreatedByUserId INT,\n" +
                "CreatedDate VARCHAR,\n" +
                "ServerUpdatedStatus INT\n" +
                ")";
        String CurrentClosingStock = "Alter table Sapling add CurrentClosingStock int";
        String column1 = "Alter Table Sapling Add TransitLoss int";
        String column2 = "Alter Table Sapling Add GerminationLoss int";
        String column3 = "Alter Table Sapling Add MortalityLoss int";
        String column4 = "Alter Table Sapling Add TransplantationLoss int";
        String column5 = "Alter Table Sapling Add CullingLoss int";


        String NurseryLabourLog = "CREATE TABLE NurseryLabourLog( \n" +
                "Id INTEGER, \n" +
                "LogDate VARCHAR, \n" +
                "RegularMale INT, \n" +
                "RegularFemale INT , \n" +
                "ContractMale INT, \n" +
                "ContractFemale INT , \n" +
                "IsActive VARCHAR, \n" +
                "CreatedByUserId INT,\n" +
                "CreatedDate VARCHAR,\n" +
                "UpdatedByUserId INT, \n" +
                "UpdatedDate VARCHAR,\n" +
                "ServerUpdatedStatus INT,\n" +
                "NurseryCode VARCHAR \n" +
                ")";

        String NurseryVisitLog = "CREATE TABLE NurseryVisitLog( \n" +
                "Id INTEGER, \n" +
                "NurseryCode VARCHAR, \n" +
                "LogTypeId INT, \n" +
                "LogDate  DateTime,\n" +
                "CosignmentCode VARCHAR , \n" +
                "ClientName VARCHAR, \n" +
                "Comments VARCHAR,\n" +
                "CreatedByUserId INT,\n" +
                "CreatedDate VARCHAR,\n" +
                "ServerUpdatedStatus INT,\n" +
                "FileName VARCHAR, \n" +
                "FileLocation VARCHAR, \n" +
                "FileExtension VARCHAR \n" +
                ")";
//
//        @Dasari  e 8 colomns neu add chesukona..?  ALTER TABLE Sapling
//        ADD SproutPurchaseDate DateTime,
//                SproutPurchasePrice Float,
//                ClearingDate DateTime,
//                ClearingCost Float,
//                CustomDutyDate DateTime,
//                CustomDutyCost Float,
//                TransportationDate DateTime,
//                TransportationCost Float

        String column7 = "Alter Table Sapling Add SproutPurchasePrice Float";
        String column8 = "Alter Table Sapling Add ClearingDate DATETIME";
        String column9 = "Alter Table Sapling Add ClearingCost Float";
        String column10 = "Alter Table Sapling Add CustomDutyDate DATETIME";
        String column11 = "Alter Table Sapling Add CustomDutyCost Float";
        String column12 = "Alter Table Sapling Add TransportationDate DATETIME";
        String column13 = "Alter Table Sapling Add TransportationCost Float";
        String column14 = "Alter Table Sapling Add PONumber VARCHAR(50)";

        String RMTransactions = "CREATE TABLE RMTransactions( \n" +
                "Id INTEGER   PRIMARY KEY AUTOINCREMENT\n" +
                "                                      NOT NULL,\n" +
                "TransactionId VARCHAR, \n" +
                "NurseryCode VARCHAR, \n" +
                "ActivityId INT , \n" +
                "ActivityName VARCHAR, \n" +
                "ActivityTypeId INT , \n" +
                "StatusTypeId INT , \n" +
                "TransactionDate DATETIME , \n" +
                "MaleRegular FLOAT , \n" +
                "FemaleRegular FLOAT , \n" +
                "MaleOutside FLOAT , \n" +
                "FemaleOutside FLOAT , \n" +
                "MaleRegularCost FLOAT , \n" +
                "FemaleRegularCost FLOAT , \n" +
                "MaleOutsideCost FLOAT , \n" +
                "FemaleoutsideCost FLOAT , \n" +
                "ExpenseType VARCHAR , \n" +
                "UOMId INT , \n" +
                "Quantity FLOAT , \n" +
                "TotalCost FLOAT , \n" +
                "Comments VARCHAR , \n" +
                "FileName VARCHAR, \n" +
                "FileLocation VARCHAR, \n" +
                "FileExtension VARCHAR, \n" +
                "CreatedByUserId INT,\n" +
                "CreatedDate VARCHAR,\n" +
                "UpdatedByUserId INT, \n" +
                "UpdatedDate VARCHAR,\n" +
                "ServerUpdatedStatus INT \n" +
                ")";

        String RMTransactionStatusHistory = "CREATE TABLE RMTransactionStatusHistory( \n" +
                "Id INTEGER   PRIMARY KEY AUTOINCREMENT\n" +
                "                                      NOT NULL,\n" +
                "TransactionId VARCHAR, \n" +
                "StatusTypeId INT , \n" +
                "CreatedByUserId INT,\n" +
                "CreatedDate VARCHAR, \n" +
                "ServerUpdatedStatus INT \n" +
                ")";
        String column15 = "Alter Table RMTransactions Add Remarks VARCHAR(500)";
        String column16 = "Alter Table RMTransactionStatusHistory Add Remarks VARCHAR(500)";

        String column17 = "Alter Table RMTransactions Add ByteImage";
        String column18 = "Alter Table Tablet Add IMEINumber2";
        String userconsignmentxref = "DROP TABLE UserConsignmentXref";
        String column19 = "Alter Table  NurseryActivityField add SortOrder INT";


        try {
            db.execSQL(cullingLossFileRepository);
            db.execSQL(CurrentClosingStock);
            db.execSQL(column1);
            db.execSQL(column2);
            db.execSQL(column3);
            db.execSQL(column4);
            db.execSQL(column5);
            db.execSQL(NurseryLabourLog);
            db.execSQL(NurseryVisitLog);
            db.execSQL(column7);
            db.execSQL(column8);
            db.execSQL(column9);
            db.execSQL(column10);
            db.execSQL(column11);
            db.execSQL(column12);
            db.execSQL(column13);
            db.execSQL(column14);
            db.execSQL(RMTransactions);
            db.execSQL(RMTransactionStatusHistory);
            db.execSQL(column15);
            db.execSQL(column16);
            db.execSQL(column17);
            db.execSQL(column18);
            db.execSQL(userconsignmentxref);
            db.execSQL(column19);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void checkTheColumnIsThere(String tableName, String columnName, String dataType, final SQLiteDatabase db) {

        boolean isThere = false;
        String query = "PRAGMA table_info(" + tableName + ");";
        try (Cursor cursor = db.rawQuery(query, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String name = cursor.getString(cursor.getColumnIndex("name"));

                    if (name.equals(columnName)) {
                        isThere = true;
                    }

                } while (cursor.moveToNext());


                if (!isThere) {
                    db.execSQL("ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " " + dataType);
                    Log.v(LOG_TAG, "@@@ added the column " + columnName);
                }
            }


        } catch (Exception e) {
            Log.v(LOG_TAG, "@@@ checking the column " + e.getMessage());
        }
    }

}
