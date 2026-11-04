package com.thu.currencyconverter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import okhttp3.OkHttpClient;

public class CurrencyUpdateWorker extends Worker {


    public CurrencyUpdateWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            ExchangeRateDatabase db = new ExchangeRateDatabase();
            OkHttpClient client = new OkHttpClient();

            CurrencyUpdateRunnable runnable = new CurrencyUpdateRunnable(
                    getApplicationContext(), db, client, null);
            runnable.updateCurrencies();
            return Result.success();
        } catch (Exception e) {
            return Result.failure();
        }
    }
}
