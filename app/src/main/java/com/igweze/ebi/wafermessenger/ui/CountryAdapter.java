package com.igweze.ebi.wafermessenger.ui;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.igweze.ebi.wafermessenger.R;
import com.igweze.ebi.wafermessenger.models.Country;
import com.igweze.ebi.wafermessenger.models.Currency;
import com.igweze.ebi.wafermessenger.models.Language;

import java.util.List;

public class CountryAdapter extends RecyclerView.Adapter<CountryAdapter.CountryViewHolder> {

    private List<Country> countries;

    public CountryAdapter(List<Country> countryList) {
        this.countries = countryList;
    }

    @NonNull
    @Override
    public CountryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_country, parent, false);
        return new CountryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CountryViewHolder holder, int position) {
        Country country = countries.get(position);
        holder.bindView(country);
    }

    @Override
    public int getItemCount() {
        return countries.size();
    }

    public static class CountryViewHolder extends RecyclerView.ViewHolder {
        private TextView countryName;
        private TextView languageName;
        private TextView currencyName;

        public CountryViewHolder(View view) {
            super(view);
            countryName = view.findViewById(R.id.countryName);
            languageName = view.findViewById(R.id.languageName);
            currencyName = view.findViewById(R.id.currencyName);
        }

        public void bindView(Country country) {
            countryName.setText(country.getName());
            Language language = country.getLanguages()[0];
            languageName.setText(language.getName());
            Currency currency = country.getCurrencies()[0];
            currencyName.setText(currency.getName());
        }
    }
}
