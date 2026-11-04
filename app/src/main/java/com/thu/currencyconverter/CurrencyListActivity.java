package com.thu.currencyconverter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import okhttp3.OkHttpClient;

public class CurrencyListActivity extends AppCompatActivity {

    private final OkHttpClient client = new OkHttpClient();
    private CurrencyListAdapter adapter;
    private ExchangeRateDatabase db;
    ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        String currency = result.getData().getStringExtra("currency");
                        double newRate = result.getData().getDoubleExtra("newRate", -1);
                        if (newRate > 0) {
                            db.setExchangeRate(currency, newRate);
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            }
    );
    private CurrencyListHelper dbHelper;
    private boolean editMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_currency_list);

        // StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        // StrictMode.setThreadPolicy(policy);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toolbar toolbar = findViewById(R.id.currencyListToolbar);
        setSupportActionBar(toolbar);

        db = new ExchangeRateDatabase();
        adapter = new CurrencyListAdapter(this, db);
        dbHelper = new CurrencyListHelper(this);
        loadDataFromDatabase();

        ListView listView = findViewById(R.id.currencyListView);
        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String currency = (String) adapter.getItem(position);
                if (editMode) {

                    Intent intent = new Intent(CurrencyListActivity.this, EditCurrencyActivity.class);
                    intent.putExtra("currency", currency);
                    intent.putExtra("listPosition", position);
                    launcher.launch(intent);
                } else {

                    String capital = db.getCapital(currency);
                    Uri uri = Uri.parse("geo:0,0?q=" + Uri.encode(capital));
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    intent.setPackage("com.google.android.apps.maps");
                    startActivity(intent);
                }
            }
        });

        Button refreshButton = findViewById(R.id.refreshButton);

        refreshButton.setOnClickListener(v -> {
            CurrencyUpdateRunnable runnable = new CurrencyUpdateRunnable(this, db, client, adapter);
            new Thread(runnable).start();

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cc_clist_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_edit_mode) {
            editMode = !editMode;
            item.setChecked(editMode);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadDataFromDatabase() {
        SQLiteDatabase sqlDb = dbHelper.getReadableDatabase();
        Cursor cursor = sqlDb.query(
                CurrencyListHelper.CURRENCY_TABLE,
                new String[]{CurrencyListHelper.CURRENCY_COL_NAME, CurrencyListHelper.CURRENCY_COL_RATE}, null, null, null, null, null);
        while (cursor.moveToNext()) {
            @SuppressLint("Range") String currency = cursor.getString(cursor.getColumnIndex(CurrencyListHelper.CURRENCY_COL_NAME));
            @SuppressLint("Range") double rate = cursor.getDouble(cursor.getColumnIndex(CurrencyListHelper.CURRENCY_COL_RATE));
            db.setExchangeRate(currency, rate);
        }
        cursor.close();
    }

}