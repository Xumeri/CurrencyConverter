package com.thu.currencyconverter;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class CurrencyListHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "currency_list.db";

    public static final String CURRENCY_TABLE = "currency_list";
    public static final String CURRENCY_COL_NAME = "currencyName";
    public static final String CURRENCY_COL_RATE = "rateForOneEuro";

    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + CURRENCY_TABLE + " (" +
            BaseColumns._ID + " INTEGER PRIMARY KEY," +
            CURRENCY_COL_NAME + " TEXT," +
            CURRENCY_COL_RATE + " REAL)";

    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + CURRENCY_TABLE;

    public CurrencyListHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}
