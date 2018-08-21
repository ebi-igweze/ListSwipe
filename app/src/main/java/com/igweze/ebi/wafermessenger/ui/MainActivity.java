package com.igweze.ebi.wafermessenger.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.igweze.ebi.wafermessenger.R;
import com.igweze.ebi.wafermessenger.services.CountryService;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CountryService service = new CountryService();

        // setup ui component
        RecyclerView recyclerView = findViewById(R.id.countryListView);
        // set layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        // add divider decoration
        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, layoutManager.getOrientation());
        recyclerView.addItemDecoration(itemDecoration);

        // get countries and handle response
        service.getCountries(countries -> {
            // create and set recycler adapter
            CountryAdapter countryAdapter = new CountryAdapter(countries);
            recyclerView.setAdapter(countryAdapter);
        });
    }
}
