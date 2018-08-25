package com.igweze.ebi.wafermessenger.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.igweze.ebi.wafermessenger.Functions.Function;
import com.igweze.ebi.wafermessenger.R;
import com.igweze.ebi.wafermessenger.models.Country;
import com.igweze.ebi.wafermessenger.services.CountryService;
import com.igweze.ebi.wafermessenger.ui.CountryAdapter.CountryViewHolder;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SwipeTouchHelper.SwipeTouchHelperListener {

    private CountryAdapter countryAdapter;
    private CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CountryService service = new CountryService();

        // setup ui component
        coordinatorLayout = findViewById(R.id.rootLayout);
        RecyclerView recyclerView = findViewById(R.id.countryListView);
        // create and set recycler adapter
        countryAdapter = new CountryAdapter(new ArrayList<>());
        recyclerView.setAdapter(countryAdapter);
        // set layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        // add divider decoration
        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, layoutManager.getOrientation());
        recyclerView.addItemDecoration(itemDecoration);


        // get countries and handle response
        service.getCountries().onsuccess(countries -> {
            // set countries field
            countryAdapter.setCountries(countries);
        }).onfailure(msg -> {
            Log.e("MainActivity", msg);
        });

        // button visible width in dp
        int buttonDimensionAndPadding = 100;
        // convert button dp dimensions to pixels
        float buttonWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, buttonDimensionAndPadding, getResources().getDisplayMetrics());
        // create swipe touch helper with left swipe
        SwipeTouchHelper touchHelper = new SwipeTouchHelper( buttonWidth, this);
        // attach touch helper to recycler view
        new ItemTouchHelper(touchHelper).attachToRecyclerView(recyclerView);
    }

    @Override
    public void onSwiped(ViewHolder viewHolder, int countryPosition) {
        if (viewHolder instanceof CountryAdapter.CountryViewHolder) {
            // remove the item from recycler view
            countryAdapter.removeCountry(countryPosition);
        }
    }
}
