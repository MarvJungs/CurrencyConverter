package com.jungma.currencyconverter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class CurrencyListAdapter extends BaseAdapter {
    private List<ExchangeRate> data;

    public CurrencyListAdapter(List<ExchangeRate> data) {
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Context context = parent.getContext();
        ExchangeRate exchangeRate = (ExchangeRate) getItem(position);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_view_currency_item, null, false);
        }

        TextView textView_currencyName = convertView.findViewById(R.id.textView_currencyName);
        TextView textView_currencyRate = convertView.findViewById(R.id.textView_currencyRate);

        textView_currencyName.setText(exchangeRate.getCurrencyName());
        textView_currencyRate.setText(Double.toString(exchangeRate.getRateForOneEuro()));

        return convertView;
    }
}
