package com.finchmil.chess.view;

import android.content.Context;
import android.os.UserManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.GridLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.finchmil.chess.Bonus;
import com.finchmil.chess.R;
import com.finchmil.chess.utils.Utils;
import com.finchmil.chess.utils.ViewUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Vgrigoryev on 20.09.2016.
 */

public class BoardView extends ScrollView {

    private static final int DEFAULT_FIELD = 0;
    private static final int RESTORABLE_FIELD = 1;
    private static final int EMPTY_FIELD = 2;
    private static final int HORIZONTAL_BONUS = 3;
    private static final int VERTICAL_BONUS = 4;
    private static final int HORSE_POSITION = 5;
    private static final int EXIT_POSITION = 6;
    private static final int STAR_BONUS = 7;

    public static final int PORTAL_MIN_VALUE = 100;

    @BindView(R.id.horizontal_scroll)
    HorizontalScrollView horizontalScrollView;
    @BindView(R.id.grid_layout)
    GridLayout gridLayout;
    @BindView(R.id.horse_image_view)
    ImageView horseImageView;

    private CellView[][] cellsArray;
    private int[] horsePosition;
    private int[] origHorsePosition;

    private BoardViewInterface boardViewInterface;

    private int[][] boardArray;
    private int rowCount;
    private int columnCount;
    private boolean pickBonuses;

    private BottomBar bottomBar;

    public BoardView(Context context) {
        super(context);
        init();
    }

    public BoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BoardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public BoardView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void setBoardViewInterface(BoardViewInterface boardViewInterface) {
        this.boardViewInterface = boardViewInterface;
    }

    private void init() {
        inflate(getContext(), R.layout.board_view, this);
        ButterKnife.bind(this);
        setVerticalScrollBarEnabled(false);
        setFillViewport(true);
    }

    public void setBoard(int[][] board) {
        this.boardArray = board;
        this.rowCount = board.length;
        this.columnCount = board[0].length;

        this.horsePosition = new int[]{0, 0};
        this.origHorsePosition = horsePosition;

        for (int r = 0; r < boardArray.length; r++) {
            for (int c = 0; c < boardArray[r].length; c++) {
                if (boardArray[r][c] == HORSE_POSITION) {
                    horsePosition[0] = r;
                    horsePosition[1] = c;
                    break;
                }
            }
        }
    }

    public void setPickBonuses(boolean pick) {
        pickBonuses = pick;
    }

    public void setBottomBar(BottomBar bottomBar) {
        this.bottomBar = bottomBar;
        this.bottomBar.bottomBarInterface = new BottomBar.BottomBarInterface() {
            @Override
            public void onBonusUses(Bonus bonus) {
                switch (bonus) {
                    case HORIZONTAL_BONUS:
                        horizontalBonusPick(horsePosition[0]);
                        break;
                    case VERTICAL_BONUS:
                        verticalBonusPick(horsePosition[1]);
                        break;
                }
            }

            @Override
            public void restartGame() {
                reloadGame();
            }
        };
    }

    public void reloadGame() {
        cellsArray = new CellView[rowCount][columnCount];
        horsePosition = origHorsePosition;

        generateCells();
        placeBonusNotRand();

        ViewGroup.LayoutParams lp = horseImageView.getLayoutParams();
        int size = ViewUtils.getCellSize(getContext());
        lp.height = size;
        lp.width = size;
        horseImageView.setLayoutParams(lp);
        horseImageView.setTranslationX(horsePosition[1] * size);
        horseImageView.setTranslationY(horsePosition[0] * size);

        int height = ViewUtils.getScreenHeight(getContext());
        int width = ViewUtils.getScreenWidth(getContext());

        int cellIHeight = height / size;
        int cellIWidth = width / size;

        smoothScrollTo(0, (horsePosition[0] - (cellIHeight / 2) ) * size);
        horizontalScrollView.smoothScrollTo((horsePosition[1] - (cellIWidth / 2) ) * size , 0);

        bottomBar.reloadBar();
        boardViewInterface.reloadGame();
    }

    private void generateCells() {
        gridLayout.removeAllViews();
        gridLayout.setColumnCount(columnCount);

        CellView view;

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v instanceof CellView) {
                    onCellClick((CellView)v);
                }
            }
        };

        for (int r = 0; r < rowCount; r++) {

            for (int c = 0; c < columnCount; c++) {
                view = new CellView(getContext());

                view.setCellIndex(r, c);
                view.setOnClickListener(listener);

                try {
                    int currentIndex = boardArray[r][c];
                    if (currentIndex == EMPTY_FIELD || currentIndex == RESTORABLE_FIELD) {
                        view.setDeactive(currentIndex == RESTORABLE_FIELD);
                    }
                } catch (Exception e) {

                }

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

            if (checkIfHasPortal(cellRow, cellColumn)) {
                analyseMoves();
                return;
            }

            if (boardArray[cellRow][cellColumn] == STAR_BONUS) {
                boardViewInterface.addStar();
                cellsArray[cellRow][cellColumn].removeStar();
            }

            horsePosition = cellIndex;

            int cellSize = ViewUtils.getCellSize(getContext());

            horseImageView
                    .animate()
                    .withLayer()
                    .translationX(cellColumn * cellSize)
                    .translationY(cellRow * cellSize)
                    .setDuration(300)
                    .setInterpolator(new DecelerateInterpolator(2));

            if (cellsArray[horseRow][horseColumn].getPortalId() < PORTAL_MIN_VALUE) {
                int index = boardArray[horseRow][horseColumn];
                cellsArray[horseRow][horseColumn].deactivateCell(index != EMPTY_FIELD);
            }
            boardViewInterface.incrementTurn();

            int height = ViewUtils.getScreenHeight(getContext());
            int width = ViewUtils.getScreenWidth(getContext());

            int cellIHeight = height / cellSize;
            int cellIWidth = width / cellSize;

            smoothScrollTo(0, (cellRow - (cellIHeight / 2) ) * cellSize);
            horizontalScrollView.smoothScrollTo((cellColumn - (cellIWidth / 2) ) * cellSize , 0);

            if (analyseMoves()) {
                checkIfHasBonus(pickBonuses);
            }
        }

    }

    private boolean analyseMoves() {

        int horseRow = horsePosition[0];
        int horseColumn = horsePosition[1];

        if (boardArray[horseRow][horseColumn] == EXIT_POSITION) {
            boardViewInterface.showEndLevel();
            return false;
        }

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

        if (cellsToMove.isEmpty() && bottomBar.getHorizontalBonuses() == 0 && bottomBar.getVerticalBonuses() == 0) {
            boardViewInterface.showGameOver();
            return false;
        }

        return true;
    }

    private void placeBonusNotRand() {
        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < columnCount; c++) {
                int index = boardArray[r][c];

                if (index == HORIZONTAL_BONUS) {
                    cellsArray[r][c].setBonus(Bonus.HORIZONTAL_BONUS);
                } else if (index == VERTICAL_BONUS) {
                    cellsArray[r][c].setBonus(Bonus.VERTICAL_BONUS);
                } else if (index >= PORTAL_MIN_VALUE) {
                    cellsArray[r][c].setPortal(index);
                } else if (index == EXIT_POSITION) {
                    cellsArray[r][c].setExit();
                } else if (index == STAR_BONUS) {
                    cellsArray[r][c].setStar();
                }
            }
        }
    }

    private void placeBonus() {

        if (boardViewInterface.getTurn() % 3 != 0) {
            return;
        }

        Bonus bonus = Utils.randInt(0, 1) == 0 ? Bonus.HORIZONTAL_BONUS : Bonus.VERTICAL_BONUS;
        CellView cellView = cellsArray[Utils.randInt(0, rowCount - 1)][Utils.randInt(0, columnCount - 1)];

        if (cellView.isCellActive() && cellView.getBonus() == Bonus.NO_BONUS) {
            cellView.setBonus(bonus);
        } else {
            placeBonus();
        }
    }

    private boolean checkIfHasPortal(int cellRow, int cellColumn) {
        int horseRow = horsePosition[0];
        int horseColumn = horsePosition[1];

        int portalId = boardArray[cellRow][cellColumn];

        if (portalId > PORTAL_MIN_VALUE) {
            for (int r = 0; r < boardArray.length; r++) {
                for (int c = 0; c < boardArray[r].length; c++) {
                    if (boardArray[r][c] == portalId && r != cellRow && c != cellColumn) {
                        horsePosition = new int[] {r, c};

                        int cellSize = ViewUtils.getCellSize(getContext());

                        horseImageView
                                .animate()
                                .withLayer()
                                .translationX(c * cellSize)
                                .translationY(r * cellSize)
                                .setDuration(300)
                                .setInterpolator(new DecelerateInterpolator(2));

                        if (cellsArray[horseRow][horseColumn].getPortalId() < PORTAL_MIN_VALUE) {
                            cellsArray[horseRow][horseColumn].deactivateCell(boardArray[horseRow][horseColumn] != EMPTY_FIELD);
                        }

                        boardViewInterface.incrementTurn();

                        int height = ViewUtils.getScreenHeight(getContext());
                        int width = ViewUtils.getScreenWidth(getContext());

                        int cellIHeight = height / cellSize;
                        int cellIWidth = width / cellSize;

                        smoothScrollTo(0, (r - (cellIHeight / 2) ) * cellSize);
                        horizontalScrollView.smoothScrollTo((c - (cellIWidth / 2) ) * cellSize , 0);

                        return true;
                    }
                }
            }
        }

        return false;
    }

    private void checkIfHasBonus(boolean pick) {
        int horseRow = horsePosition[0];
        int horseColumn = horsePosition[1];

        CellView currentCell = cellsArray[horseRow][horseColumn];
        Bonus currentBonus = currentCell.getBonus();

        if (currentBonus != Bonus.NO_BONUS) {
            currentCell.setBonus(Bonus.NO_BONUS);
        }

        if (!pick) {
            switch (currentBonus) {
                case HORIZONTAL_BONUS:
                    horizontalBonusPick(horseRow);
                    break;
                case VERTICAL_BONUS:
                    verticalBonusPick(horseColumn);
                    break;
            }
        } else {
            switch (currentBonus) {
                case HORIZONTAL_BONUS:
                    bottomBar.addHorizontalBonus();
                    break;
                case VERTICAL_BONUS:
                    bottomBar.addVerticalBonus();
                    break;
            }
        }
    }

    private void horizontalBonusPick(int index) {
        CellView[] cellArray = cellsArray[index];
        int[] cellIndexes = boardArray[index];

        for(int i = 0; i < cellArray.length; i++) {
            if (cellIndexes[i] != EMPTY_FIELD) {
                cellArray[i].activateCell();
            }
        }

        analyseMoves();
    }

    private void verticalBonusPick(int index) {
        for (int r = 0; r < boardArray.length; r++) {
            if (boardArray[r][index] != EMPTY_FIELD) {
                cellsArray[r][index].activateCell();
            }
        }

        analyseMoves();
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

    private float mx, my, curX, curY;
    private boolean started = false;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        curX = event.getX();
        curY = event.getY();
        int dx = (int) (mx - curX);
        int dy = (int) (my - curY);
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if (started) {
                    scrollBy(0, dy);
                    horizontalScrollView.scrollBy(dx, 0);
                } else {
                    started = true;
                }
                mx = curX;
                my = curY;
                break;
            case MotionEvent.ACTION_UP:
                scrollBy(0, dy);
                horizontalScrollView.scrollBy(dx, 0);
                started = false;
                break;
        }
        return true;
    }

    public interface BoardViewInterface {
        void incrementTurn();
        int getTurn();
        void showGameOver();
        void showEndLevel();
        void addStar();
        void reloadGame();
    }
}
