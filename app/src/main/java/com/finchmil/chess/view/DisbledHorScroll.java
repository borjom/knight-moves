package com.finchmil.chess.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

/**
 * Created by Vgrigoryev on 21.09.2016.
 */

public class DisbledHorScroll extends HorizontalScrollView {
    public DisbledHorScroll(Context context) {
        super(context);
    }

    public DisbledHorScroll(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DisbledHorScroll(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public DisbledHorScroll(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }
}
