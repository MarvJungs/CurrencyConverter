package com.jungma.currencyconverter;

import android.content.ContentValues;
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
    private final ExchangeRateDatabase exchangeRateDatabase;
    private final ExchangeRateUpdateNotifier exchangeRateUpdateNotifier;
    private final MainActivity mainActivity;
    private ExchangeRateDbHelper exchangeRateDbHelper;

    public ExchangeRateUpdateRunnable(MainActivity mainActivity, ExchangeRateDatabase exchangeRateDatabase) {
        this.mainActivity = mainActivity;
        this.exchangeRateDatabase = exchangeRateDatabase;
        this.exchangeRateUpdateNotifier = new ExchangeRateUpdateNotifier(mainActivity);
        this.exchangeRateDbHelper = new ExchangeRateDbHelper(mainActivity);
    }

    @Override
    public void run() {
        updateCurrencies();
    }

    private void updateCurrencies() {
        SQLiteDatabase sqLiteDatabase = exchangeRateDbHelper.getWritableDatabase();
        try {
            exchangeRateUpdateNotifier.showAndUpdateNotification("Updating currencies...");
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


                            if (Arrays.asList(exchangeRateDatabase.getCurrencies()).contains(currency)) {
                                exchangeRateDatabase.setExchangeRate(currency, Double.parseDouble(rateForOneEuro));
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

        mainActivity.runOnUiThread(() -> {
            CharSequence text = "Currencies Update finished!";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(mainActivity, text, duration);
            toast.show();
        });
    }
}
