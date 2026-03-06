package com.jungma.currencyconverter;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Arrays;

public class CurrencyListActivity extends AppCompatActivity {
    private final ExchangeRateDatabase exchangeRateDatabase = new ExchangeRateDatabase();
    private ActivityResultLauncher<Intent> editCurrencyLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_currency_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.currency_list), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        CurrencyListAdapter currencyListAdapter = new CurrencyListAdapter(exchangeRateDatabase);

        ListView listView = findViewById(R.id.currency_list);
        listView.setAdapter(currencyListAdapter);

        editCurrencyLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        //handle returned data here
                        String currency = data.getStringExtra("currencyName");
                        double newRateForOneEuro = Double.parseDouble(data.getStringExtra("newRateForOneEuro"));
                        exchangeRateDatabase.setExchangeRate(currency, newRateForOneEuro);
                        currencyListAdapter.notifyDataSetChanged();
                    }
                }
        );

        listView.setOnItemClickListener((parent, view, position, id) -> {
            String currency = (String) currencyListAdapter.getItem(position);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0`?q=" + exchangeRateDatabase.getCapital(currency)));
            Intent editCurrencyIntent = new Intent(CurrencyListActivity.this, EditCurrencyActivity.class);
            editCurrencyIntent.putExtra("currencyName", currency);
            editCurrencyIntent.putExtra("rateForOneEuro", exchangeRateDatabase.getExchangeRate(currency));
            editCurrencyLauncher.launch(editCurrencyIntent);
        });
    }
}