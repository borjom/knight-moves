package com.finchmil.chess.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.finchmil.chess.Bonus;
import com.finchmil.chess.R;

import org.w3c.dom.Text;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Vgrigoryev on 22.09.2016.
 */

public class BottomBar extends FrameLayout {

    @BindView(R.id.ver_count)
    Button verticalCount;

    @BindView(R.id.horiz_count)
    Button horizontalCount;

    private int verticalBonuses;
    private int horizontalBonuses;

    public BottomBarInterface bottomBarInterface;

    public BottomBar(Context context) {
        super(context);
        init();
    }

    public BottomBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BottomBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public BottomBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void init() {
        inflate(getContext(), R.layout.bottom_bar, this);
        ButterKnife.bind(this);

        verticalCount.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verticalBonuses <= 0) {
                    return;
                }

                verticalBonuses--;
                verticalCount.setText("Вертикальные бонусы \n " +verticalBonuses);

                bottomBarInterface.onBonusUses(Bonus.VERTICAL_BONUS);
            }
        });

        horizontalCount.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (horizontalBonuses <= 0) {
                    return;
                }

                horizontalBonuses--;
                horizontalCount.setText("Горизонтальные бонусы \n " +horizontalBonuses);

                bottomBarInterface.onBonusUses(Bonus.HORIZONTAL_BONUS);
            }
        });
    }

    public void addVerticalBonus() {
        verticalBonuses++;
        verticalCount.setText("Вертикальные бонусы \n " +verticalBonuses);
    }

    public void addHorizontalBonus() {
        horizontalBonuses++;
        horizontalCount.setText("Горизонтальные бонусы \n " + horizontalBonuses);
    }

    public int getVerticalBonuses() {
        return verticalBonuses;
    }

    public int getHorizontalBonuses() {
        return horizontalBonuses;
    }

    public void reloadBar() {
        horizontalBonuses = 0;
        horizontalCount.setText("Горизонтальные бонусы \n 0");

        verticalBonuses = 0;
        verticalCount.setText("Вертикальные бонусы \n 0");
    }

    public interface BottomBarInterface {
        void onBonusUses(Bonus bonus);
    }
}
