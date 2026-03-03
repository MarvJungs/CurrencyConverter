package com.jungma.currencyconverter;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    private final ExchangeRateDatabase exchangeRateDatabase = new ExchangeRateDatabase();

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
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.spinner_view_item, R.id.textview_spinner, exchangeRateDatabase.getCurrencies());

        spinner_currencyFrom.setAdapter(arrayAdapter);
        spinner_currencyTo.setAdapter(arrayAdapter);
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

        textView_result.setText(Double.toString(result));
    }

}