package com.thu.currencyconverter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CurrencyListAdapter extends BaseAdapter {

    private final String[] currencies;
    private final ExchangeRateDatabase db;
    private final Context context;

    public CurrencyListAdapter(Context context, ExchangeRateDatabase db) {
        this.context = context;
        this.db = db;
        this.currencies = db.getCurrencies();
    }

    @Override
    public int getCount() {
        return currencies.length;
    }

    @Override
    public Object getItem(int position) {
        return currencies[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.list_item_currency, parent, false);
        }

        String currency = currencies[position];

        TextView textName = convertView.findViewById(R.id.textCurrencyName);
        TextView textRate = convertView.findViewById(R.id.textExchangeRate);

        ImageView flagImage = convertView.findViewById(R.id.imageFlag);
        String flagName = "flag_" + currency.toLowerCase();
        int resId = context.getResources().getIdentifier(flagName, "drawable", context.getPackageName());
        if (resId != 0) {
            flagImage.setImageResource(resId);
        }
        textName.setText(currency);
        textRate.setText(String.format("%.4f", db.getExchangeRate(currency)));
        return convertView;
    }
}