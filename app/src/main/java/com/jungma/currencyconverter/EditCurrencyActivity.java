package com.jungma.currencyconverter;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class EditCurrencyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_currency);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.editCurrency), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView headerText = findViewById(R.id.textView_editCurrency);
        headerText.setText("Edit Currency: " + getIntent().getStringExtra("currencyName"));
        EditText editText = findViewById(R.id.editText_currencyRate);
        editText.setText(Double.toString(getIntent().getDoubleExtra("rateForOneEuro", 0.0)));
        editText.setOnEditorActionListener((v, actionId, event) -> {
            Log.i("EditorActionListenerTest", "Callback triggered!");
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                Intent returnIntent = new Intent();
                String currencyName = getIntent().getStringExtra("currencyName");
                returnIntent.putExtra("currencyName", currencyName);
                returnIntent.putExtra("newRateForOneEuro", v.getText().toString());
                setResult(RESULT_OK, returnIntent);
                finish();
                return true;
            }
            return false;
        });
    }
}