package com.jungma.currencyconverter;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;

public class ExchangeRateUpdateRunnable implements Runnable {
    private final String ECB_DAILY_URL = "https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml";
    private final ExchangeRateUpdateNotifier exchangeRateUpdateNotifier;
    private final Context context;
    private final ExchangeRateDbHelper exchangeRateDbHelper;

    public ExchangeRateUpdateRunnable(Context context) {
        this.context = context.getApplicationContext();
        this.exchangeRateUpdateNotifier = new ExchangeRateUpdateNotifier(this.context);
        this.exchangeRateDbHelper = new ExchangeRateDbHelper(this.context);
    }

    @Override
    public void run() {
        updateCurrencies();
    }

    private void updateCurrencies() {
        SQLiteDatabase sqLiteDatabase = exchangeRateDbHelper.getWritableDatabase();
        try {
            URL url = new URL(ECB_DAILY_URL);
            URLConnection connection = url.openConnection();

            XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
            parser.setInput(connection.getInputStream(), connection.getContentEncoding());

            int eventType = parser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if ("Cube".equals(parser.getName())) {
                        String currency = parser.getAttributeValue(null, "currency");
                        String rateForOneEuro = parser.getAttributeValue(null, "rate");

                        if (currency != null && rateForOneEuro != null) {
                            ContentValues contentValues = new ContentValues();
                            contentValues.put(exchangeRateDbHelper.EXCHANGERATE_COL_CURRENCYNAME, currency);
                            contentValues.put(exchangeRateDbHelper.EXCHANGERATE_COL_RATEFORONEEURO, rateForOneEuro);
                            if (!exchangeRateDbHelper.hasCurrency(currency)) {
                                sqLiteDatabase.insert(exchangeRateDbHelper.EXCHANGERATE_TABLE, null, contentValues);
                            } else {
                                sqLiteDatabase.update(exchangeRateDbHelper.EXCHANGERATE_TABLE, contentValues, exchangeRateDbHelper.EXCHANGERATE_COL_CURRENCYNAME + " = ?", new String[]{currency});
                            }
                        }
                    }
                }
                eventType = parser.next();
            }
            exchangeRateUpdateNotifier.showAndUpdateNotification("Currencies are up-to-date now :)");
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
    }
}
