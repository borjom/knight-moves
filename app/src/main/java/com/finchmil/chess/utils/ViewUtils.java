package com.finchmil.chess.utils;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.util.Property;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;

import com.finchmil.chess.R;

/**
 * Created by Vgrigoryev on 13.07.2016.
 */

public class ViewUtils {

    public static int getScreenHeight(Context c) {
        WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.y;
    }

    public static int getScreenWidth(Context c) {
        WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    public static int getCellSize(Context c) {
        return ViewUtils.getScreenWidth(c) / Const.CELL_COUNT;
    }

    public static void showYesAlert(Context context, String title, String message, String positiveText, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(title);
        builder.setMessage(message);

        builder.setPositiveButton(positiveText, listener);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static void animateBackgroundColor(View view, int colorTo, int duration) {
        int colorFrom = 0;

        if (view.getBackground() instanceof ColorDrawable) {
            colorFrom = ((ColorDrawable)view.getBackground()).getColor();
        }

        animateBackgroundColor(view, colorFrom, colorTo, duration);
    }

    public static void animateBackgroundColor(View view, final int colorFrom, int colorTo, int duration) {
        final Property<View, Integer> property = new Property<View, Integer>(int.class, "backgroundColor") {
            @Override
            public Integer get(View object) {
                return colorFrom;
            }

            @Override
            public void set(View object, Integer value) {
                object.setBackgroundColor(value);
            }
        };

        ObjectAnimator animator = ObjectAnimator.ofInt(view, property, colorTo);
        animator.setDuration(duration);
        animator.setEvaluator(new ArgbEvaluator());
        animator.setInterpolator(new DecelerateInterpolator(2));
        animator.start();
    }
}
