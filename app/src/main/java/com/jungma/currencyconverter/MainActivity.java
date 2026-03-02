package com.jungma.currencyconverter;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

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
        ExchangeRateDatabase exchangeRateDatabase = new ExchangeRateDatabase();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.spinner_view_item, R.id.textview_spinner, exchangeRateDatabase.getCurrencies());

        spinner_currencyFrom.setAdapter(arrayAdapter);
        spinner_currencyTo.setAdapter(arrayAdapter);
    }



}