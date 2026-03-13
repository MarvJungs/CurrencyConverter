package com.jungma.currencyconverter;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import androidx.annotation.Nullable;

public class ExchangeRateDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "exchangerates.db";

    public static final String EXCHANGERATE_TABLE = "exchangerate";
    public static final String EXCHANGERATE_COL_CURRENCYNAME = "currencyName";
    public static final String EXCHANGERATE_COL_RATEFORONEEURO = "rateForOneEuro";

    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + EXCHANGERATE_TABLE + " ("
            + BaseColumns._ID + " INTEGER PRIMARY KEY,"
            + EXCHANGERATE_COL_CURRENCYNAME + " TEXT UNIQUE,"
            + EXCHANGERATE_COL_RATEFORONEEURO + " DECIMAL)";

    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + EXCHANGERATE_TABLE;

    public ExchangeRateDbHelper(@Nullable Context context) {
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

    public boolean hasCurrency(String currency) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String[] projection = {
                BaseColumns._ID,
                EXCHANGERATE_COL_CURRENCYNAME,
                EXCHANGERATE_COL_RATEFORONEEURO
        };
        String selection = EXCHANGERATE_COL_CURRENCYNAME + " = ?";
        String[] selectionArgs = {currency};

        Cursor cursor = sqLiteDatabase.query(EXCHANGERATE_TABLE, projection, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count > 0;
    }
}
