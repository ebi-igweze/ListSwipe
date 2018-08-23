package com.igweze.ebi.wafermessenger.services;

import android.util.Log;

import com.igweze.ebi.wafermessenger.models.Country;
import com.igweze.ebi.wafermessenger.models.Currency;
import com.igweze.ebi.wafermessenger.models.Language;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class CountryService {
    private static String COUNTRIES_URL = "https://restcountries.eu/rest/v2/all";

    public Promise<List<Country>> getCountries() {
        return new Promise<>(() -> {
            // create default empty list
            List<Country> countries = new ArrayList<>();
            String countriesAsString = getRestCountries();
            try {
                countries = JsonConverter.toCountries(countriesAsString);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("CountryService", e.getMessage());
            }

            return countries;
        });
    }

    private String getRestCountries() {
        URL url;
        HttpsURLConnection urlConnection = null;

        try {
            url = new URL(COUNTRIES_URL);

            // initialize https connection
            urlConnection = (HttpsURLConnection) url.openConnection();
            // set accept header
            urlConnection.setRequestProperty("Accept", "application/json");

            // check if response code is 200 OK.
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // get response input stream
                InputStream responseBody = urlConnection.getInputStream();
                // read stream into stream reader with specified charset
                InputStreamReader responseBodyReader = new InputStreamReader(responseBody, "UTF-8");
                BufferedReader streamReader = new BufferedReader(responseBodyReader);
                // string builder to build JSON string
                StringBuilder responseStrBuilder = new StringBuilder();

                // get JSON String
                String inputStr;
                while ((inputStr = streamReader.readLine()) != null) {
                    responseStrBuilder.append(inputStr);
                }
                return responseStrBuilder.toString();
            } else {
                throw new Exception("Connection error: " + urlConnection.getResponseCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) urlConnection.disconnect();
        }

        return "";
    }


    private List<Country>  getMockCountries() {

        Currency currency1 = new Currency("Afghan afghani");
        Language language1 = new Language("Pashto");

        Currency currency2 = new Currency("Albanian lek");
        Language language2 = new Language("Albanian");

        Currency currency3 = new Currency("Euro");
        Language language3 = new Language("Swedish");

        Country country1 = new Country("Afghanistan", new Language[] {language1}, new Currency[] {currency1});
        Country country2 = new Country("Albania", new Language[] {language2}, new Currency[] {currency2});
        Country country3 = new Country("Åland Islands", new Language[]{language3}, new Currency[]{currency3});

        ArrayList<Country> array = new ArrayList<>();
        array.add(country1);
        array.add(country2);
        array.add(country3);

        return array;
    }
}
