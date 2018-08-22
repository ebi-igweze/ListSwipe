package com.igweze.ebi.wafermessenger.ui;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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


    public void removeCountry(int position) {
        // remove item at position
        countries.remove(position);
        // notify the item removed at position
        // to perform delete animations
        notifyItemRemoved(position);
    }

    public void restoreCountry(Country country, int position) {
        countries.add(position, country);
        // notify item added by position
        notifyItemInserted(position);
    }

    public static class CountryViewHolder extends SwipeTouchHelper.SwipeTouchViewHolder {
        public final ConstraintLayout foreground;
        private final TextView countryName;
        private final TextView languageName;
        private final TextView currencyName;

        public CountryViewHolder(View view) {
            super(view);
            foreground = view.findViewById(R.id.foreground);
            countryName = view.findViewById(R.id.countryName);
            languageName = view.findViewById(R.id.languageName);
            currencyName = view.findViewById(R.id.currencyName);
        }

        @Override
        public View getForegroundView() {
            return foreground;
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
