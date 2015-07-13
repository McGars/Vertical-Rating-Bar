package com.gars.verticalratingbar;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Владимир on 09.07.2015.
 */
public class VerticalRatingBar extends FrameLayout {

    private LayoutInflater inflater;
    private ViewGroup llRatingBar;
    private TextView tvVoteAll, tvAvgRate;
    private List<Integer> ratings = new ArrayList<>(5);
    private int maxHeght;
    private int barsColor;
    private int selectColor;
    // подсвечиваем звезду и стролбик если самый большой
    private int selectMaxValue;
    private Drawable iconStar,iconStarActive;

    public VerticalRatingBar(Context context) {
        this(context, null);
    }

    public VerticalRatingBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public VerticalRatingBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflater = LayoutInflater.from(getContext());

        addView(inflater.inflate(R.layout.custom_rating_bar, this, false));
        llRatingBar = (ViewGroup) findViewById(R.id.llRatingBar);
        tvVoteAll = (TextView) findViewById(R.id.tvVoteAll);
        tvAvgRate = (TextView) findViewById(R.id.tvRate);

        maxHeght = pxToDp(100);

        iconStar = getContext().getResources().getDrawable(R.drawable.ic_star_gray);
        iconStarActive = getContext().getResources().getDrawable(R.drawable.ic_star_gray_active);
    }

    public void setBarsColor(int color){
        this.barsColor = color;
    }

    public void setSelectColor(int color){
        selectColor = color;
    }

    public void setAutoRating(List<Integer> ratings) {
        this.ratings = ratings;
        build();
    }

    public void setCustomRating(double avgProgress, int fullStateProgress, List<Integer> ratings) {
        this.ratings = ratings;
        int countStars = ratings.size();
        List<Rates> mRates = new ArrayList<>(countStars);
        Integer maxValue = Collections.max(ratings);

        if(fullStateProgress>0){
            // заполняем список
            for (int i = 0; i < countStars; i++) {

                Rates rate = new Rates(ratings.get(i));
                rate.procents = rate.value * 100 / fullStateProgress;
                if(rate.procents == 0)
                    rate.procents = 1;
                mRates.add(rate);
                if(rate.value == maxValue)
                    selectMaxValue = i;
            }

            // строим вьюхи
            buildViews(countStars, avgProgress, fullStateProgress, mRates);
        }
    }

    private void build() {
        int fullStateProgress = 0;
        // средняя оценка
        double avgProgress = 0;
        int size = ratings.size();
        List<Rates> mRates = new ArrayList<>(size);
        // высчитаваем весь прогресс
        Integer maxValue = Collections.max(ratings);

        // заполняем список
        for (int i = 0; i < size; i++) {
            int value = ratings.get(i);
            fullStateProgress += value;
            mRates.add(new Rates(value));
            if(value == maxValue)
                selectMaxValue = i;
        }
        // высчитываем процент каждого столбика относительно всего прогресса
        if (fullStateProgress > 0) {
            for (int i = 0; i < size; i++) {
                Rates rate = mRates.get(i);
                rate.procents = rate.value * 100 / fullStateProgress;
                avgProgress += rate.value * i;
                if (rate.procents < 1)
                    rate.procents = 1;
            }
        }

        // подсчет средней оценки
        avgProgress = avgProgress / fullStateProgress;

        // строим вьюхи
        buildViews(size, avgProgress, fullStateProgress, mRates);
    }

    // строим вьюхи
    private void buildViews(int size, double avgProgress, int fullStateProgress, List<Rates> mRates) {
        // очищаем предыдущие столбы
        llRatingBar.removeAllViews();

        if(selectColor !=0)
            iconStarActive.setColorFilter(selectColor, PorterDuff.Mode.SRC_ATOP);
        if(barsColor !=0)
            iconStar.setColorFilter(barsColor, PorterDuff.Mode.SRC_ATOP);

        for (int i = 0; i < size; i++) {
            Rates rate = mRates.get(i);
            // root view
            View v = inflater.inflate(R.layout.custom_rating_bar_item, llRatingBar, false);
            // цифра
            TextView tvRate = (TextView) v.findViewById(R.id.tvRate);
            // цифра
            ImageView ivStar = (ImageView) v.findViewById(R.id.ivStar);
            // столбик
            View vBar = v.findViewById(R.id.vBar);


            if(selectColor !=0 && i == selectMaxValue){
                vBar.setBackgroundColor(selectColor);
                ivStar.setImageDrawable(iconStarActive);
            } else if(barsColor != 0){
                vBar.setBackgroundColor(barsColor);
                ivStar.setImageDrawable(iconStar);
            }

            // строим высоту
            ViewGroup.LayoutParams params = vBar.getLayoutParams();
            params.height = maxHeght * rate.procents / 100;
            vBar.setLayoutParams(params);
            // set rate value
            tvRate.setText(String.valueOf(rate.value));
            llRatingBar.addView(v);
        }
        // общее число проголосовавших
        tvVoteAll.setText(String.valueOf(getContext().getString(R.string.rates, fullStateProgress)));
        // средняя оценка
        tvAvgRate.setText(String.format("%.1f", avgProgress));
    }

    class Rates {
        int value;
        int procents = 1;

        public Rates(int value) {
            this.value = value;
        }
    }


    public int pxToDp(int px) {
        return (int) TypedValue.applyDimension(1, (float) px, getContext().getResources().getDisplayMetrics());
    }
}
