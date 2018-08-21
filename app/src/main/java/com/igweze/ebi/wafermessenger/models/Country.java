package com.igweze.ebi.wafermessenger.models;

public class Country {

    private String name;

    private Language[] languages;

    private Currency[] currencies;


    public Country(String name, Language[] languages, Currency[] currencies) {
        this.name = name;
        this.languages = languages;
        this.currencies = currencies;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Language[] getLanguages() {
        return languages;
    }

    public void setLanguages(Language[] languages) {
        this.languages = languages;
    }

    public Currency[] getCurrencies() {
        return currencies;
    }

    public void setCurrencies(Currency[] currencies) {
        this.currencies = currencies;
    }

}
