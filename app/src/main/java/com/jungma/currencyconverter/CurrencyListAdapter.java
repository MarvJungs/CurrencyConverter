package com.jungma.currencyconverter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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

        ImageView imageView_flag = convertView.findViewById(R.id.imageView_flag);
        TextView textView_currencyName = convertView.findViewById(R.id.textView_currencyName);
        TextView textView_currencyRate = convertView.findViewById(R.id.textView_currencyRate);

        imageView_flag.setImageResource(getFlagResourceId(currency));
        textView_currencyName.setText(currency);
        textView_currencyRate.setText(Double.toString(exchangeRateDatabase.getExchangeRate(currency)));

        return convertView;
    }

    private int getFlagResourceId(String currency) {
        switch (currency) {
            case "AUD": return R.drawable.flag_aud;
            case "BGN": return R.drawable.flag_bgn;
            case "BRL": return R.drawable.flag_brl;
            case "CAD": return R.drawable.flag_cad;
            case "CHF": return R.drawable.flag_chf;
            case "CNY": return R.drawable.flag_cny;
            case "CZK": return R.drawable.flag_czk;
            case "DKK": return R.drawable.flag_dkk;
            case "EUR": return R.drawable.flag_eur;
            case "GBP": return R.drawable.flag_gbp;
            case "HKD": return R.drawable.flag_hkd;
            case "HRK": return R.drawable.flag_hrk;
            case "HUF": return R.drawable.flag_huf;
            case "IDR": return R.drawable.flag_idr;
            case "ILS": return R.drawable.flag_ils;
            case "INR": return R.drawable.flag_inr;
            case "JPY": return R.drawable.flag_jpy;
            case "KRW": return R.drawable.flag_krw;
            case "MXN": return R.drawable.flag_mxn;
            case "MYR": return R.drawable.flag_myr;
            case "NOK": return R.drawable.flag_nok;
            case "NZD": return R.drawable.flag_nzd;
            case "PHP": return R.drawable.flag_php;
            case "PLN": return R.drawable.flag_pln;
            case "RON": return R.drawable.flag_ron;
            case "RUB": return R.drawable.flag_rub;
            case "SEK": return R.drawable.flag_sek;
            case "SGD": return R.drawable.flag_sgd;
            case "THB": return R.drawable.flag_thb;
            case "TRY": return R.drawable.flag_try;
            case "USD": return R.drawable.flag_usd;
            case "ZAR": return R.drawable.flag_zar;
            default: return 0;
        }
    }
}
