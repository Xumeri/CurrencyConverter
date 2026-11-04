package com.thu.currencyconverter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ShareActionProvider;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.MenuItemCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "CurrencyConverterPrefs";
    private static final String KEY_FROM = "fromCurrency";
    private static final String KEY_TO = "toCurrency";
    private static final String KEY_AMOUNT = "amount";
    ShareActionProvider shareActionProvider;
    CurrencyUpdateRunnable dbUpdater;
    private Spinner fromSpinner;
    private Spinner toSpinner;
    private EditText amountInput;
    private ArrayAdapter<String> adapter;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button calculateButton = findViewById(R.id.button);
        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        ExchangeRateDatabase db = new ExchangeRateDatabase();
        String[] currencies = db.getCurrencies();

        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                currencies
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        fromSpinner = findViewById(R.id.fromValue_spinner);
        fromSpinner.setAdapter(adapter);

        toSpinner = findViewById(R.id.toValue_spinner);
        toSpinner.setAdapter(adapter);
        amountInput = findViewById(R.id.amountInput);

        loadPreferences();

        OkHttpClient client = new OkHttpClient();
        dbUpdater = new CurrencyUpdateRunnable(this, db, client, null);
        new Thread(dbUpdater).start();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        calculateButton.setOnClickListener(v -> {
            amountInput = findViewById(R.id.amountInput);
            double amount = Double.parseDouble(amountInput.getText().toString());
            double result = db.convert(amount, fromSpinner.getSelectedItem().toString(), toSpinner.getSelectedItem().toString());
            String formatResult = String.format("%.2f", result);
            TextView resultText = findViewById(R.id.resultText);
            resultText.setText(formatResult);
        });

        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(
                CurrencyUpdateWorker.class, 1, TimeUnit.DAYS).build();
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "currencyUpdate",
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
        );
    }

    @Override
    protected void onStop() {
        super.onStop();
        savePreferences();
    }

    private void savePreferences() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_FROM, fromSpinner.getSelectedItem().toString());
        editor.putString(KEY_TO, toSpinner.getSelectedItem().toString());
        editor.putString(KEY_AMOUNT, amountInput.getText().toString());
        editor.apply();
    }

    private void loadPreferences() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String fromCurrency = prefs.getString(KEY_FROM, "EUR");
        String toCurrency = prefs.getString(KEY_TO, "USD");
        String amount = prefs.getString(KEY_AMOUNT, "");

        fromSpinner.setSelection(adapter.getPosition(fromCurrency));
        toSpinner.setSelection(adapter.getPosition(toCurrency));
        amountInput.setText(amount);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cc_main_menu, menu);
        MenuItem shareItem = menu.findItem(R.id.action_share);
        shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
        setShareText(null);
        return true;
    }

    private void setShareText(String text) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        if (text != null) {
            shareIntent.putExtra(Intent.EXTRA_TEXT, text);
        }
        shareActionProvider.setShareIntent(shareIntent);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.currencyList) {
            Intent intent = new Intent(this, CurrencyListActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

