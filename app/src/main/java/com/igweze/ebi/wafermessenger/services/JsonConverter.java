package com.igweze.ebi.wafermessenger.services;

import com.igweze.ebi.wafermessenger.models.Country;
import com.igweze.ebi.wafermessenger.models.Currency;
import com.igweze.ebi.wafermessenger.models.Language;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JsonConverter {
    private static String KEY_CURRENCIES = "currencies";
    private static String KEY_LANGUAGES = "languages";
    private static String KEY_NAME = "name";

    public static List<Country> toCountries(String countriesAsString) throws JSONException {
        List<Country> countries = new ArrayList<>();

        if (countriesAsString == null || countriesAsString.isEmpty()) {

        } else {

            JSONArray jsonArray = new JSONArray(countriesAsString);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                countries.add(parseCountry(jsonObject));
            }
        }

        return countries;
    }

    private static Country parseCountry(JSONObject jsonCountry) throws JSONException {
        String name = jsonCountry.getString(KEY_NAME);
        JSONArray languageArray = jsonCountry.getJSONArray(KEY_LANGUAGES);
        JSONArray currencyArray = jsonCountry.getJSONArray(KEY_CURRENCIES);

        Currency[] currencies = new Currency[currencyArray.length()];
        Language[] languages = new Language[languageArray.length()];

        for (int i = 0; i < currencyArray.length();  i++) {
            JSONObject jsonCurrency = currencyArray.getJSONObject(i);
            currencies[i]= parseCurrency(jsonCurrency);
        }

        for (int i = 0; i < languageArray.length(); i++) {
            JSONObject jsonLanguage = languageArray.getJSONObject(i);
            languages[i] = parseLanguage(jsonLanguage);
        }

        return new Country(name, languages, currencies);
    }

    private static Currency parseCurrency(JSONObject jsonCurrency) throws JSONException {
        String name = jsonCurrency.getString(KEY_NAME);
        return new Currency(name);
    }

    private static Language parseLanguage(JSONObject jsonLanguage) throws JSONException {
        String name = jsonLanguage.getString(KEY_NAME);
        return new Language(name);
    }
}
