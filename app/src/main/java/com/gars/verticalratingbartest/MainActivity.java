package com.gars.verticalratingbartest;

import android.app.Activity;
import android.os.Bundle;

import com.gars.verticalratingbar.VerticalRatingBar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        VerticalRatingBar cRatingBar = (VerticalRatingBar) findViewById(R.id.cRatingBar);
        cRatingBar.setSelectColor(getResources().getColor(R.color.selectedColor));

        List<Integer> rates = new ArrayList<>();
        rates.add(0);
        rates.add(3);
        rates.add(45);
        rates.add(300);
        rates.add(140);

        cRatingBar.setAutoRating(rates);
    }
}
