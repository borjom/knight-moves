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
    private int starCount;

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
                ViewUtils.showYesAlert(getContext(), "Game over", generateEndMessage(false), "начать заново", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boardView.reloadGame();
                    }
                });
            }

            @Override
            public void showEndLevel() {
                ViewUtils.showYesAlert(getContext(), "Уровень пройден", generateEndMessage(true), "начать заново", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boardView.reloadGame();
                    }
                });
            }

            @Override
            public void addStar() {
                starCount++;
            }

            @Override
            public void reloadGame() {
                starCount = 0;
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

    private String generateEndMessage(boolean end) {
        StringBuilder result = new StringBuilder(end ? "Уровень пройден." : "Вы проиграли");
        result.append("\nСобрано звезд: ");
        result.append(starCount);
        result.append("\nСобрано бонусов всего: ");
        result.append(bottomBar.getHorizontalBonusesTotal() + bottomBar.getVerticalBonusesTotal());
        result.append("\nИспользовано бонусов: ");
        result.append((bottomBar.getHorizontalBonusesTotal() + bottomBar.getVerticalBonusesTotal()) - (bottomBar.getVerticalBonuses() + bottomBar.getHorizontalBonuses()));
        result.append("\nОсталось бонусов: ");
        result.append(bottomBar.getHorizontalBonuses() + bottomBar.getVerticalBonuses());
        result.append("\nСделано шагов: ");
        result.append(turn);

        return result.toString();
    }
}
