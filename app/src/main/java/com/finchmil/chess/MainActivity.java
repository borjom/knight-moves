package com.finchmil.chess;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.GridLayout;
import android.widget.ImageView;

import com.finchmil.chess.utils.ViewUtils;
import com.finchmil.chess.view.BoardView;
import com.finchmil.chess.view.BottomBar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.bottom_bar)
    BottomBar bottomBar;
    @BindView(R.id.board_view)
    BoardView boardView;

    private int turn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        init();
    }

    private void init() {
        turn = 0;
        boardView.setBoardViewInterface(new BoardView.BoardViewInterface() {
            @Override
            public void incrementTurn() {
                turn++;
            }

            @Override
            public int getTurn() {
                return turn;
            }

            @Override
            public void showGameOver() {
                ViewUtils.showYesAlert(MainActivity.this, "Game over", "Game over", "начать заново", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boardView.reloadGame();
                    }
                });
            }
        });

        // 0 - default cell
        // 1 - restorable cell
        // 2 - all time empty cell

        // 3 - horizotal bonus
        // 4 - vertical bonus



        boardView.setBoard(new int[][]{
                new int[]{2, 0, 2, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                new int[]{2, 0, 2, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                new int[]{2, 0, 2, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                new int[]{2, 0, 2, 0, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                new int[]{2, 0, 2, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0},
                new int[]{2, 0, 2, 0, 0, 0, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0},
                new int[]{2, 0, 2, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0},
                new int[]{2, 0, 2, 0, 0, 0, 0, 0, 0, 0, 4, 0, 0, 0, 0, 0},
                new int[]{2, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0},
                new int[]{2, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 0, 0, 0},
                new int[]{2, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0},
                new int[]{2, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 0},
                new int[]{2, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3},
                new int[]{2, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 0},
                new int[]{2, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0},
                new int[]{2, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 0, 0, 0},
                new int[]{2, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0},
                new int[]{2, 0, 2, 0, 0, 0, 0, 0, 0, 0, 4, 0, 0, 0, 0, 0},
        });
        boardView.setHorsePosition(new int[]{1, 1});
        boardView.setPickBonuses(true);
        boardView.setBottomBar(bottomBar);

        boardView.reloadGame();
    }
}
