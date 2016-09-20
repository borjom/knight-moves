package com.finchmil.chess;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.GridLayout;
import android.widget.ImageView;

import com.finchmil.chess.utils.ViewUtils;
import com.finchmil.chess.view.BoardView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.greed_layout)
    GridLayout gridLayout;
    @BindView(R.id.horse_image_view)
    ImageView horseImageView;

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
        boardView.setBoardSize(4, 8);
    }
}
