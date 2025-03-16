package com.example.focusflow;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import java.util.HashSet;

public class StreakDecorator implements DayViewDecorator {
    private final Drawable icon;
    private final HashSet<CalendarDay> dates;

    public StreakDecorator(Context context, HashSet<CalendarDay> dates, int drawableRes) {
        this.dates = dates;
        this.icon = ContextCompat.getDrawable(context, drawableRes);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        if (icon != null) {
            view.setSelectionDrawable(icon);
        }
    }
}
