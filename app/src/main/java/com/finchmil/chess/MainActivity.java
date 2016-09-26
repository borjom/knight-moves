package com.finchmil.chess;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;

import com.finchmil.chess.models.LevelModel;
import com.finchmil.chess.models.LevelsConfig;
import com.finchmil.chess.utils.ApiWorker;
import com.finchmil.chess.utils.ViewUtils;
import com.finchmil.chess.view.BoardView;
import com.finchmil.chess.view.BottomBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscriber;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.view_pager)
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        ApiWorker.getInstance().getLevelConfig().subscribe(new Subscriber<LevelsConfig>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(LevelsConfig levelsConfig) {
                viewPager.setAdapter(new MainAdapter(getSupportFragmentManager(), levelsConfig));
                tabLayout.setupWithViewPager(viewPager);
            }
        });
    }

    protected class MainAdapter extends FragmentStatePagerAdapter {

        private LevelsConfig config;

        public MainAdapter(FragmentManager fm, LevelsConfig config) {
            super(fm);
            this.config = config;
        }

        @Override
        public int getCount() {
            return config.getLevels().length;
        }

        @Override
        public Fragment getItem(int position) {
            return GameFragment.getFragment(config.getLevels()[position]);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return (position + 1) + "";
        }
    }
}
