package com.igweze.ebi.wafermessenger.services;

import com.igweze.ebi.wafermessenger.Functions.Consumer;
import com.igweze.ebi.wafermessenger.models.Country;
import com.igweze.ebi.wafermessenger.models.Currency;
import com.igweze.ebi.wafermessenger.models.Language;

import java.util.ArrayList;
import java.util.List;

public class CountryService {

    public void getCountries(Consumer<List<Country>> listConsumer) {
        listConsumer.accept(getMockCountries());
    }

    private List<Country>  getMockCountries() {

        Currency currency1 = new Currency("Afghan afghani");
        Language language1 = new Language("Pashto");

        Currency currency2 = new Currency("Albanian lek");
        Language language2 = new Language("Albanian");

        Country country1 = new Country("Afghanistan", new Language[] {language1}, new Currency[] {currency1});
        Country country2 = new Country("Albania", new Language[] {language2}, new Currency[] {currency2});
        ArrayList<Country> array = new ArrayList<>();
        array.add(country1);
        array.add(country2);
        return array;
    }
}
