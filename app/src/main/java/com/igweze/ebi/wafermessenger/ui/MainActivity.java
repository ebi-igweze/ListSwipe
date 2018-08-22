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
import android.view.View;

import com.igweze.ebi.wafermessenger.Functions.Function;
import com.igweze.ebi.wafermessenger.R;
import com.igweze.ebi.wafermessenger.models.Country;
import com.igweze.ebi.wafermessenger.services.CountryService;
import com.igweze.ebi.wafermessenger.ui.CountryAdapter.CountryViewHolder;

import java.util.List;

public class MainActivity extends AppCompatActivity implements SwipeTouchHelper.SwipeTouchHelperListener {

    private List<Country> countries;
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
        // set layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        // add divider decoration
        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, layoutManager.getOrientation());
        recyclerView.addItemDecoration(itemDecoration);


        // get countries and handle response
        service.getCountries(countries -> {
            // set countries field
            this.countries = countries;
            // create and set recycler adapter
            countryAdapter = new CountryAdapter(countries);
            recyclerView.setAdapter(countryAdapter);
        });

        int noDrag = 0;
        int touchDirections = ItemTouchHelper.LEFT;
        Function<ViewHolder, View> getForegroundView = viewHolder -> ((CountryViewHolder) viewHolder).foreground;
        // create swipe touch helper with left swipe
        SwipeTouchHelper touchHelper = new SwipeTouchHelper(noDrag, touchDirections, this, getForegroundView);
        // attach touch helper to recycler view
        new ItemTouchHelper(touchHelper).attachToRecyclerView(recyclerView);
    }

    @Override
    public void onSwiped(ViewHolder viewHolder, int countryPosition) {
        if (viewHolder instanceof CountryAdapter.CountryViewHolder) {
            // retain removed country, for undo functionality
            final Country deletedCountry = countries.get(countryPosition);
            // get country name for display in snack bar
            String name = deletedCountry.getName();
            // remove the item from recycler view
            countryAdapter.removeCountry(countryPosition);

            // showing snack bar with Undo option
            Snackbar.make(coordinatorLayout, "'" + name + "' country was removed!", Snackbar.LENGTH_LONG)
                    .setActionTextColor(Color.YELLOW)
                    .setAction("UNDO", view -> {
                        // undo is selected, restore the deleted item
                        countryAdapter.restoreCountry(deletedCountry, countryPosition);
                    })
                    .show();
        }
    }
}
