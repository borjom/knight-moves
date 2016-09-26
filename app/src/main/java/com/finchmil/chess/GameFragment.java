package com.finchmil.chess;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.finchmil.chess.models.LevelModel;
import com.finchmil.chess.utils.ApiWorker;
import com.finchmil.chess.utils.ViewUtils;
import com.finchmil.chess.view.BoardView;
import com.finchmil.chess.view.BottomBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscriber;

/**
 * Created by Vgrigoryev on 23.09.2016.
 */

public class GameFragment extends Fragment {

    @BindView(R.id.bottom_bar)
    BottomBar bottomBar;
    @BindView(R.id.board_view)
    BoardView boardView;

    private int turn;
    private String jsonUrl;

    public static GameFragment getFragment(String jsonUrl) {
        GameFragment gameFragment = new GameFragment();
        Bundle bundle = new Bundle();
        bundle.putString("bla", jsonUrl);
        gameFragment.setArguments(bundle);
        return gameFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        jsonUrl = getArguments().getString("bla");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.game_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

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
                ViewUtils.showYesAlert(getContext(), "Game over", "Game over", "начать заново", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boardView.reloadGame();
                    }
                });
            }

            @Override
            public void showEndLevel() {
                ViewUtils.showYesAlert(getContext(), "Exit reached", "Exit reached", "начать заново", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boardView.reloadGame();
                    }
                });
            }
        });

        ApiWorker.getInstance().getLevelModel(jsonUrl).subscribe(new Subscriber<LevelModel>() {
            @Override
            public void onCompleted() {
                boardView.reloadGame();
            }

            @Override
            public void onError(Throwable e) {
                Log.e("bla", e.toString());
            }

            @Override
            public void onNext(LevelModel levelModel) {
                boardView.setPickBonuses(levelModel.getCollectBonuses());
                boardView.setBoard(levelModel.getBoardArray());
            }
        });

        boardView.setBottomBar(bottomBar);
    }
}
