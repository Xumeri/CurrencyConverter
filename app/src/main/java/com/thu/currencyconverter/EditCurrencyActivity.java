package com.thu.currencyconverter;

import android.content.Intent;
import android.os.Bundle;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class EditCurrencyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_currency);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toolbar toolbar = findViewById(R.id.editToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String currency = getIntent().getStringExtra("currency");
        int position = getIntent().getIntExtra("listPosition", -1);
        ExchangeRateDatabase db = new ExchangeRateDatabase();

        TextView nameView = findViewById(R.id.editCurrencyName);
        TextView currentRateView = findViewById(R.id.editCurrentRate);
        EditText newRateInput = findViewById(R.id.editNewRate);
        Button saveButton = findViewById(R.id.saveButton);

        nameView.setText(currency);
        currentRateView.setText("Aktueller Kurs: " + String.format("%.4f", db.getExchangeRate(currency)));


        newRateInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                returnResult(newRateInput.getText().toString(), currency, position);
                return true;
            }
            return false;
        });


        saveButton.setOnClickListener(v ->
                returnResult(newRateInput.getText().toString(), currency, position)
        );
    }

    private void returnResult(String input, String currency, int position) {
        if (input.isEmpty()) return;
        Intent returnIntent = new Intent();
        returnIntent.putExtra("newRate", Double.parseDouble(input));
        returnIntent.putExtra("currency", currency);
        returnIntent.putExtra("listPosition", position);
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        setResult(RESULT_CANCELED);
        finish();
        return true;
    }
}