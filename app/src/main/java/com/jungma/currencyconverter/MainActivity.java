package com.jungma.currencyconverter;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
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

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private final ExchangeRateDatabase exchangeRateDatabase = new ExchangeRateDatabase();
    private final int PRECISION = 3;
    private ShareActionProvider shareActionProvider;
    private CurrencyListAdapter currencyListAdapter;
    private ExchangeRateUpdateRunnable exchangeRateUpdateRunnable;

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
        Spinner spinner_currencyFrom = findViewById(R.id.spinner_currencyFrom);
        Spinner spinner_currencyTo = findViewById(R.id.spinner_currencyTo);
        currencyListAdapter = new CurrencyListAdapter(exchangeRateDatabase);

        spinner_currencyFrom.setAdapter(currencyListAdapter);
        spinner_currencyTo.setAdapter(currencyListAdapter);

        Toolbar toolbar = findViewById(R.id.app_toolbar_main);
        setSupportActionBar(toolbar);
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
        Spinner spinner_currencyFrom = findViewById(R.id.spinner_currencyFrom);
        Spinner spinner_currencyTo = findViewById(R.id.spinner_currencyTo);
        EditText input_amount = findViewById(R.id.input_amount);
        TextView textView_result = findViewById(R.id.textView_result);

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
}