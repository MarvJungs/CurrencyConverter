package com.jungma.currencyconverter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ShareActionProvider;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.MenuItemCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final ExchangeRateDatabase exchangeRateDatabase = new ExchangeRateDatabase();
    private final int PRECISION = 3;
    private final static String ID_SOURCE_CURRENCY = "sourceCurrency";
    private final static String ID_TARGET_CURRENCY = "targetCurrency";
    private final static String ID_AMOUNT_CURRENCY = "amountCurrency";
    private final static String ID_RESULT = "result";

    private ShareActionProvider shareActionProvider;
    private CurrencyListAdapter currencyListAdapter;
    private ExchangeRateUpdateRunnable exchangeRateUpdateRunnable;

    private Spinner spinner_currencyFrom;
    private Spinner spinner_currencyTo;
    private EditText input_amount;
    private TextView textView_result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        spinner_currencyFrom = findViewById(R.id.spinner_currencyFrom);
        spinner_currencyTo = findViewById(R.id.spinner_currencyTo);
        input_amount = findViewById(R.id.input_amount);
        textView_result = findViewById(R.id.textView_result);

        currencyListAdapter = new CurrencyListAdapter(exchangeRateDatabase);

        spinner_currencyFrom.setAdapter(currencyListAdapter);
        spinner_currencyTo.setAdapter(currencyListAdapter);

        Toolbar toolbar = findViewById(R.id.app_toolbar_main);
        setSupportActionBar(toolbar);

        setupCurrencies();
    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String sourceCurrency = spinner_currencyFrom.getSelectedItem().toString();
        String targetCurrency = spinner_currencyTo.getSelectedItem().toString();
        String amountCurrency = input_amount.getText().toString();
        String result = textView_result.getText().toString();

        editor.putString(ID_SOURCE_CURRENCY, sourceCurrency);
        editor.putString(ID_TARGET_CURRENCY, targetCurrency);
        editor.putString(ID_AMOUNT_CURRENCY, amountCurrency);
        editor.putString(ID_RESULT, result);

        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        List<String> currencies = Arrays.asList(exchangeRateDatabase.getCurrencies());

        String sourceCurrency = sharedPreferences.getString(ID_SOURCE_CURRENCY, (String) currencyListAdapter.getItem(0));
        String targetCurrency = sharedPreferences.getString(ID_TARGET_CURRENCY, (String) currencyListAdapter.getItem(0));
        String amountCurrency = sharedPreferences.getString(ID_AMOUNT_CURRENCY, "0");
        String result = sharedPreferences.getString(ID_RESULT, "0");

        spinner_currencyFrom.setSelection(currencies.indexOf(sourceCurrency));
        spinner_currencyTo.setSelection(currencies.indexOf(targetCurrency));
        input_amount.setText(amountCurrency);
        textView_result.setText(result);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.appbar_menu, menu);
        MenuItem shareItem = menu.findItem(R.id.appbar_menu_entry_share);
        shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.appbar_menu_entry_currencylist) {
            Intent currencylistIntent = new Intent(MainActivity.this, CurrencyListActivity.class);
            startActivity(currencylistIntent);
            return true;
        } else if (item.getItemId() == R.id.appbar_menu_entry_refreshrates) {
            exchangeRateUpdateRunnable = new ExchangeRateUpdateRunnable(this, exchangeRateDatabase);
            Thread t = new Thread(exchangeRateUpdateRunnable);
            t.start();
            currencyListAdapter.notifyDataSetChanged();
        }
        return true;
    }

    public void calculate(View view) {
        String currency_from = spinner_currencyFrom.getSelectedItem().toString();
        String currency_to = spinner_currencyTo.getSelectedItem().toString();
        double amount = Double.parseDouble(input_amount.getText().toString());

        double result = exchangeRateDatabase.convert(amount, currency_from, currency_to);
        double displayedResult = (int) (result * Math.pow(10, PRECISION)) / Math.pow(10, PRECISION);

        textView_result.setText(Double.toString(displayedResult));
        String sharedText = String.format("Converted %.2f %s to %.2f %s", amount, currency_from, displayedResult, currency_to);
        setShareText(sharedText);
    }

    private void setShareText(String text) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        if (text != null) {
            shareIntent.putExtra(Intent.EXTRA_TEXT, text);
        }
        shareActionProvider.setShareIntent(shareIntent);
    }

    private void setupCurrencies() {
        ExchangeRateDbHelper exchangeRateDbHelper = new ExchangeRateDbHelper(this);
        SQLiteDatabase sqLiteDatabase = exchangeRateDbHelper.getReadableDatabase();
        String[] projection = {
                BaseColumns._ID,
                exchangeRateDbHelper.EXCHANGERATE_COL_CURRENCYNAME,
                exchangeRateDbHelper.EXCHANGERATE_COL_RATEFORONEEURO
        };

        Cursor cursor = sqLiteDatabase.query(exchangeRateDbHelper.EXCHANGERATE_TABLE, projection, null, null, null,null, null);

        while (cursor.moveToNext()) {
            String currencyName = cursor.getString(cursor.getColumnIndexOrThrow(exchangeRateDbHelper.EXCHANGERATE_COL_CURRENCYNAME));
            double rateForOneEuro = cursor.getDouble(cursor.getColumnIndexOrThrow(exchangeRateDbHelper.EXCHANGERATE_COL_RATEFORONEEURO));

            if (Arrays.asList(exchangeRateDatabase.getCurrencies()).contains(currencyName)) {
                exchangeRateDatabase.setExchangeRate(currencyName, rateForOneEuro);
                Log.i("sqlite", currencyName + " = " + rateForOneEuro);
            }
        }
        cursor.close();
    }
}