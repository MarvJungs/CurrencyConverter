package com.jungma.currencyconverter;

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
    private final MainActivity mainActivity;

    public ExchangeRateUpdateRunnable(MainActivity mainActivity, ExchangeRateDatabase exchangeRateDatabase) {
        this.mainActivity = mainActivity;
        this.exchangeRateDatabase = exchangeRateDatabase;
    }

    @Override
    public void run() {
        updateCurrencies();
    }

    private void updateCurrencies() {
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

                        if (Arrays.asList(exchangeRateDatabase.getCurrencies()).contains(currency)) {
                            exchangeRateDatabase.setExchangeRate(currency, Double.parseDouble(rateForOneEuro));
                        }
                    }
                }
                eventType = parser.next();
            }
        } catch (Exception ignored) {
        }

        mainActivity.runOnUiThread(() -> {
            CharSequence text = "Currencies Update finished!";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(mainActivity, text, duration);
            toast.show();
        });
    }
}
