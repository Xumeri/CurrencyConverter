package com.thu.currencyconverter;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CurrencyUpdateRunnable implements Runnable {
    private final ExchangeRateDatabase db;
    private final OkHttpClient client;
    private final CurrencyListAdapter adapter;
    private CurrencyListHelper dbHelper;
    private final Context context;

    private final CurrencyUpdateNotifier notifier;

    public CurrencyUpdateRunnable(Context context, ExchangeRateDatabase db, OkHttpClient client, CurrencyListAdapter adapter) {
        this.db = db;
        this.context = context;
        this.client = client;
        this.adapter = adapter;
        this.notifier = new CurrencyUpdateNotifier(context);
    }


    @Override
    public void run() {
        updateCurrencies();
    }

    public void updateCurrencies() {
        dbHelper = new CurrencyListHelper(context);
        try {
            Request request = new Request.Builder().url("https://www.floatrates.com/daily/eur.json").build();
            Response response = client.newCall(request).execute();
            if (response.body() == null) return;
            String responseBody = response.body().string();


            JSONObject root = new JSONObject(responseBody);
            JSONArray keys = root.names();
            if (keys == null) return;

            SQLiteDatabase sqlDb = dbHelper.getWritableDatabase();
            sqlDb.beginTransaction();
            int count = 0;
            try {
                for (int i = 0; i < keys.length(); i++) {
                    String key = keys.getString(i);
                    JSONObject rates = root.getJSONObject(key);
                    String currency = rates.getString("code");
                    double rate = rates.getDouble("rate");
                    db.setExchangeRate(currency, rate);
                    ContentValues values = new ContentValues();
                    values.put(CurrencyListHelper.CURRENCY_COL_NAME, currency);
                    values.put(CurrencyListHelper.CURRENCY_COL_RATE, rate);
                    sqlDb.replace(CurrencyListHelper.CURRENCY_TABLE, null, values);
                    count++;
                }

                sqlDb.setTransactionSuccessful();
                notifier.showOrUpdateNotification(count);
            } finally {
                sqlDb.endTransaction();
            }
            if (adapter != null) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    adapter.notifyDataSetChanged();
                    Toast.makeText(context, "Currencies updated", Toast.LENGTH_LONG).show();
                });

            }
            new CurrencyUpdateNotifier(context).showOrUpdateNotification(count);

        } catch (IOException exception) {
            Log.e("Floatrates", "Can't get data from Floatrates");
            exception.printStackTrace();
        } catch (JSONException exception) {
            Log.e("Floatrates", "Error parsing JSON.");
            exception.printStackTrace();
        }
    }

}
