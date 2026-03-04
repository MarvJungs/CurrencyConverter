package com.jungma.currencyconverter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


public class CurrencyListAdapter extends BaseAdapter {
    private final ExchangeRateDatabase exchangeRateDatabase;

    public CurrencyListAdapter(ExchangeRateDatabase exchangeRateDatabase) {
        this.exchangeRateDatabase = exchangeRateDatabase;
    }

    @Override
    public int getCount() {
        return exchangeRateDatabase.getCurrencies().length;
    }

    @Override
    public Object getItem(int position) {
        return exchangeRateDatabase.getCurrencies()[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Context context = parent.getContext();
        String currency = (String) getItem(position);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_view_currency_item, null, false);
        }

        TextView textView_currencyName = convertView.findViewById(R.id.textView_currencyName);
        TextView textView_currencyRate = convertView.findViewById(R.id.textView_currencyRate);

        textView_currencyName.setText(currency);
        textView_currencyRate.setText(Double.toString(exchangeRateDatabase.getExchangeRate(currency)));

        return convertView;
    }
}
