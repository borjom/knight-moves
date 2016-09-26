package com.finchmil.chess.view;

import android.content.Context;
import android.graphics.Color;
import android.media.Image;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.finchmil.chess.Bonus;
import com.finchmil.chess.R;
import com.finchmil.chess.utils.ViewUtils;

/**
 * Created by Vgrigoryev on 13.07.2016.
 */

public class CellView extends FrameLayout {

    private boolean isActive = true;
    private int row;
    private int column;

    private Bonus bonus = Bonus.NO_BONUS;
    private ImageView bonusView;

    private int portalId = 0;
    private ImageView portalView;
    private ImageView exitView;
    private ImageView starView;

    public CellView(Context context) {
        super(context);
        init();
    }

    public CellView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CellView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public CellView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setLayoutParams(new FrameLayout.LayoutParams(ViewUtils.getCellSize(getContext()), ViewUtils.getCellSize(getContext())));
    }

    public void setCellIndex(int row, int column) {
        this.row = row;
        this.column = column;

        int color = (column + row) % 2 == 0 ? R.color.colorAccent : R.color.colorPrimary;

        setBackgroundResource(color);
    }

    public int[] getCellIndex() {
        return new int[]{row, column};
    }

    public void deactivateCell(boolean restorable) {
        isActive = false;
        setClickable(false);
        if (!restorable) {
            setBackgroundColor(Color.BLACK);
        } else {
            ViewUtils.animateBackgroundColor(this, getResources().getColor((column + row) % 2 == 0 ? R.color.colorAccent_darker : R.color.colorPrimary_darker), 300);
        }
    }

    public void activateCell() {
        isActive = true;
        setClickable(true);

        ViewUtils.animateBackgroundColor(this, getResources().getColor((column + row) % 2 == 0 ? R.color.colorAccent : R.color.colorPrimary), 300);
    }

    public void setDeactive(boolean restorable) {
        isActive = false;
        setClickable(false);
        if (!restorable) {
            setBackgroundColor(Color.BLACK);
        } else {
            setBackgroundColor(getResources().getColor((column + row) % 2 == 0 ? R.color.colorAccent_darker : R.color.colorPrimary_darker));
        }
    }

    public boolean isCellActive() {
        return isActive;
    }

    public Bonus getBonus() {
        return bonus;
    }

    public void setBonus(Bonus bonus) {

        this.bonus = bonus;

        if (bonus == Bonus.NO_BONUS) {
            if (bonusView.getParent() != null) {
                removeView(bonusView);
            }

            return;
        }

        if (bonusView == null) {
            bonusView = new ImageView(getContext());
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            bonusView.setLayoutParams(lp);
        }

        if (bonus == Bonus.HORIZONTAL_BONUS) {
            bonusView.setImageResource(R.drawable.horizontal);
        } else {
            bonusView.setImageResource(R.drawable.vertical);
        }

        if (bonusView.getParent() == null) {
            addView(bonusView);
        }

        bonusView.setAlpha(0f);

        bonusView
                .animate()
                .withLayer()
                .alpha(1f)
                .setDuration(300)
                .setInterpolator(new DecelerateInterpolator(2));
    }

    public int getPortalId() {
        return portalId;
    }

    public void setPortal(int id) {
        this.portalId = id;
        if (id < BoardView.PORTAL_MIN_VALUE) {
            if (portalView != null && portalView.getParent() != null) {
                removeView(portalView);
            }
            return;
        }

        if (portalView == null) {
            portalView = new ImageView(getContext());
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            portalView.setLayoutParams(lp);
            portalView.setImageResource(R.drawable.portal);
            portalView.setColorFilter(Color.parseColor("#" + Integer.toHexString(id)));
        }

        if (portalView.getParent() == null) {
            addView(portalView);
        }

        portalView.setAlpha(0f);

        portalView
                .animate()
                .withLayer()
                .alpha(1f)
                .setDuration(300)
                .setInterpolator(new DecelerateInterpolator(2));
    }

    public void setExit() {
        if (exitView == null) {
            exitView = new ImageView(getContext());
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            exitView.setLayoutParams(lp);
            exitView.setImageResource(R.drawable.exit);
        }

        if (exitView.getParent() == null) {
            addView(exitView);
        }

        exitView.setAlpha(0f);

        exitView
                .animate()
                .withLayer()
                .alpha(1f)
                .setDuration(300)
                .setInterpolator(new DecelerateInterpolator(2));
    }

    public void setStar() {
        if (starView == null) {
            starView = new ImageView(getContext());
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            starView.setLayoutParams(lp);
            starView.setImageResource(R.drawable.star);
        }

        if (starView.getParent() == null) {
            addView(starView);
        }

        starView.setAlpha(0f);

        starView
                .animate()
                .withLayer()
                .alpha(1f)
                .setDuration(300)
                .setInterpolator(new DecelerateInterpolator(2));
    }

    public void removeStar() {
        try {
            removeView(starView);
        } catch (Exception e) {

        }
    }
}
