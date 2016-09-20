package com.finchmil.chess;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.finchmil.chess.utils.Const;
import com.finchmil.chess.utils.Utils;
import com.finchmil.chess.utils.ViewUtils;
import com.finchmil.chess.view.CellView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.score_text_view)
    TextView scoreTextView;
    @BindView(R.id.bonuses_text_view)
    TextView bonusesTextView;

    @BindView(R.id.greed_layout)
    GridLayout gridLayout;
    @BindView(R.id.horse_image_view)
    ImageView horseImageView;

    private CellView[][] cellsArray;
    private int[] horsePosition;

    private int score;
    private int bonusesCount;
    private int turn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        init();
    }

    private void init() {
        cellsArray = new CellView[Const.CELL_COUNT][Const.CELL_COUNT];
        horsePosition = new int[]{0,0};

        score = 0;
        scoreTextView.setText("0");

        bonusesCount = 0;
        bonusesTextView.setText("0");

        turn = 0;

        generateCells();

        ViewGroup.LayoutParams lp = horseImageView.getLayoutParams();
        int size = ViewUtils.getCellSize(this);
        lp.height = size;
        lp.width = size;
        horseImageView.setLayoutParams(lp);
        horseImageView.setTranslationX(0);
        horseImageView.setTranslationY(0);
    }

    private void generateCells() {
        gridLayout.removeAllViews();

        CellView view;

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v instanceof CellView) {
                    onCellClick((CellView)v);
                }
            }
        };

        for (int r = 0; r < Const.CELL_COUNT; r++) {

            for (int c = 0; c < Const.CELL_COUNT; c++) {
                view = new CellView(this);

                view.setCellIndex(r, c);
                view.setOnClickListener(listener);

                gridLayout.addView(view);
                cellsArray[r][c] = view;
            }
        }
    }

    private void onCellClick(CellView cell) {
        int[] cellIndex = cell.getCellIndex();

        int cellRow = cellIndex[0];
        int cellColumn = cellIndex[1];

        int horseRow = horsePosition[0];
        int horseColumn = horsePosition[1];

        boolean bigMoveToLeftColumn = horseColumn - cellColumn == 2;
        boolean bigMoveToRightColumn = cellColumn - horseColumn == 2;

        boolean smallMoveToLeftColumn = horseColumn - cellColumn == 1;
        boolean smallMoveToRightColumn = cellColumn - horseColumn == 1;

        boolean bigMoveToTopRow = cellRow - horseRow == 2;
        boolean bigMoveToBottomRow = horseRow - cellRow == 2;

        boolean smallMoveToTopRow = cellRow - horseRow == 1;
        boolean smallMoveToBottomRow = horseRow - cellRow == 1;

        boolean shouldMoveHorse = false;

        if (bigMoveToLeftColumn || bigMoveToRightColumn) {

            if (smallMoveToTopRow || smallMoveToBottomRow) {
                shouldMoveHorse = true;
            }
        } else if (bigMoveToTopRow || bigMoveToBottomRow) {
            if (smallMoveToLeftColumn || smallMoveToRightColumn) {
                shouldMoveHorse = true;
            }
        }

        if (shouldMoveHorse) {
            horsePosition = cellIndex;

            int cellSize = ViewUtils.getCellSize(this);

            horseImageView
                    .animate()
                    .withLayer()
                    .translationX(cellColumn * cellSize)
                    .translationY(cellRow * cellSize)
                    .setDuration(300)
                    .setInterpolator(new DecelerateInterpolator(2));

            cellsArray[horseRow][horseColumn].deactivateCell();
            turn++;

            if (analyseMoves()) {
                updateScore();
                placeBonus();
                checkIfHasBonus();
            }
        }
    }

    private void placeBonus() {

        if (turn % 3 != 0) {
            return;
        }

        Bonus bonus = Utils.randInt(0, 1) == 0 ? Bonus.HORIZONTAL_BONUS : Bonus.VERTICAL_BONUS;
        CellView cellView = cellsArray[Utils.randInt(0, Const.CELL_COUNT - 1)][Utils.randInt(0, Const.CELL_COUNT - 1)];

        if (cellView.isCellActive() && cellView.getBonus() == Bonus.NO_BONUS) {
            cellView.setBonus(bonus);
        } else {
            placeBonus();
        }
    }

    private void checkIfHasBonus() {
        int horseRow = horsePosition[0];
        int horseColumn = horsePosition[1];

        CellView currentCell = cellsArray[horseRow][horseColumn];
        Bonus currentBonus = currentCell.getBonus();

        if (currentBonus != Bonus.NO_BONUS) {
            currentCell.setBonus(Bonus.NO_BONUS);
            bonusesCount++;
            bonusesTextView.setText(String.valueOf(bonusesCount));
        }

        switch (currentBonus) {
            case HORIZONTAL_BONUS:
                horizontalBonusPick(horseRow);
                break;
            case VERTICAL_BONUS:
                verticalBonusPick(horseColumn);
                break;
        }
    }

    private void horizontalBonusPick(int index) {
        CellView[] cellArray = cellsArray[index];

        for (CellView cell : cellArray) {
            cell.activateCell();
        }
    }

    private void verticalBonusPick(int index) {
        for (CellView[] cellArray : cellsArray) {
            cellArray[index].activateCell();
        }
    }

    private void updateScore() {
        score++;
        scoreTextView.setText(String.valueOf(score));
    }

    private boolean analyseMoves() {

        int horseRow = horsePosition[0];
        int horseColumn = horsePosition[1];

        int bigLeftMove = horseColumn - 2;
        int bigRightMove = horseColumn + 2;
        int bigTopMove = horseRow - 2;
        int bigBottomMove = horseRow + 2;

        int smallLeftMove = horseColumn - 1;
        int smallRightMove = horseColumn + 1;
        int smallTopMove = horseRow - 1;
        int smallBottomMove = horseRow + 1;

        ArrayList<CellView> cellsToMove = new ArrayList<>();

        addCellViewFromPositionToArrayList(smallTopMove, bigLeftMove, cellsToMove);
        addCellViewFromPositionToArrayList(smallBottomMove, bigLeftMove, cellsToMove);

        addCellViewFromPositionToArrayList(smallTopMove, bigRightMove, cellsToMove);
        addCellViewFromPositionToArrayList(smallBottomMove, bigRightMove, cellsToMove);

        addCellViewFromPositionToArrayList(bigTopMove, smallLeftMove, cellsToMove);
        addCellViewFromPositionToArrayList(bigTopMove, smallRightMove, cellsToMove);

        addCellViewFromPositionToArrayList(bigBottomMove, smallLeftMove, cellsToMove);
        addCellViewFromPositionToArrayList(bigBottomMove, smallRightMove, cellsToMove);

        if (cellsToMove.isEmpty()) {
            showGameOver();
            return false;
        }

        return true;
    }

    private void addCellViewFromPositionToArrayList(int row, int column, ArrayList<CellView> list) {
        CellView result = null;

        try {
            result = cellsArray[row][column];
        } catch (Exception e) {
            // bad, but i'm lazy
        }

        if (result != null && result.isCellActive()) {
            list.add(result);
        }
    }

    private void showGameOver() {
        ViewUtils.showYesAlert(this, "Game over", "Game over", "начать заново", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                init();
            }
        });
    }
}
