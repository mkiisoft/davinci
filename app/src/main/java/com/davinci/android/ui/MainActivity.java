package com.davinci.android.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.davinci.android.data.ApiWorker;
import com.davinci.android.data.AppDatabase;
import com.davinci.android.data.ClassesDao;
import com.davinci.android.model.Element;
import com.davinci.android.util.Constants;
import com.davinci.android.data.Generator;
import com.davinci.android.R;
import com.davinci.android.items.ClassItem;
import com.davinci.android.items.DateItem;
import com.davinci.android.util.Welcome;
import com.f2prateek.rx.preferences2.Preference;
import com.google.gson.Gson;
import com.xwray.groupie.GroupAdapter;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements Constants {

    private CompositeDisposable subscribers;

    private ProgressBar loading;
    private RecyclerView recyclerClasses;
    private SwipeRefreshLayout refresh;

    private GroupAdapter adapter;
    private ClassesDao dao;

    private ConstraintLayout root;
    private Welcome welcome;

    private LinearLayoutCompat dataHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        root = findViewById(R.id.root_view);

        welcome = findViewById(R.id.welcome_screen);

        subscribers = new CompositeDisposable();

        dataHolder = findViewById(R.id.data_holder);

        loading = findViewById(R.id.loading);

        recyclerClasses = findViewById(R.id.recycler_class);

        adapter = new GroupAdapter();
        recyclerClasses.setAdapter(adapter);

        refresh = findViewById(R.id.refresh);
        refresh.setColorSchemeColors(Color.parseColor(PURPLE));
        refresh.setProgressViewOffset(false, Generator.dpToPx(100), Generator.dpToPx(160));
        refresh.setOnRefreshListener(() -> populateClasses(adapter));

        AppDatabase database = AppDatabase.getDatabase(this);
        dao = database.appDao();

        setupMode();

        findViewById(R.id.davinci_logo).setOnClickListener(v -> {
            Preference<Boolean> mode = Generator.initPreferences(this)
                    .getBoolean(MODE, false);
            mode.set(!mode.get());
        });

        if (Generator.getTurn(this) == 0) {
            welcome.clickMode(this);
        } else {
            root.removeView(welcome);
            populateClasses(adapter);
        }

        scheduleWork();
    }

    @Override
    protected void onDestroy() {
        subscribers.dispose();
        super.onDestroy();
    }

    @Override
    public void click(int turn) {
        animateWelcome(turn);
    }

    private void animateWelcome(int turn) {
        CardView mainCard = findViewById(R.id.cardView);
        CardView welcomeCard = welcome.findViewById(R.id.root_welcome);

        ValueAnimator radius = ObjectAnimator.ofFloat(welcomeCard.getRadius(), mainCard.getRadius());
        ValueAnimator dimensWidth = ObjectAnimator.ofFloat(welcomeCard.getMeasuredWidth(), mainCard.getMeasuredWidth());
        ValueAnimator dimensHeight = ObjectAnimator.ofFloat(welcomeCard.getMeasuredHeight(), mainCard.getMeasuredHeight());
        ValueAnimator toX = ObjectAnimator.ofFloat(welcomeCard.getX(), mainCard.getX());
        ValueAnimator toY = ObjectAnimator.ofFloat(welcomeCard.getY(), mainCard.getY());
        ValueAnimator alpha = ObjectAnimator.ofFloat(1f, 0f);

        radius.addUpdateListener(valueAnimator -> {
            float value = (float) valueAnimator.getAnimatedValue();
            welcomeCard.setRadius(value);
        });

        dimensWidth.addUpdateListener(valueAnimator -> {
            float value = (float) valueAnimator.getAnimatedValue();
            welcomeCard.getLayoutParams().width = (int) value;
            welcomeCard.requestLayout();
        });

        dimensHeight.addUpdateListener(valueAnimator -> {
            float value = (float) valueAnimator.getAnimatedValue();
            welcomeCard.getLayoutParams().height = (int) value;
            welcomeCard.requestLayout();
        });

        toX.addUpdateListener(valueAnimator -> {
            float value = (float) valueAnimator.getAnimatedValue();
            welcomeCard.setX(value);
        });

        toY.addUpdateListener(valueAnimator -> {
            float value = (float) valueAnimator.getAnimatedValue();
            welcomeCard.setY(value);
        });

        alpha.addUpdateListener(valueAnimator -> {
            float value = (float) valueAnimator.getAnimatedValue();
            findViewById(R.id.holder_welcome).setAlpha(value);
        });

        AnimatorSet animator = new AnimatorSet();
        animator.playTogether(radius, dimensWidth, dimensHeight, toY, toX, alpha);
        animator.setDuration(800);
        animator.setStartDelay(400);
        animator.start();

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                welcome.animate().setDuration(300)
                        .alpha(0)
                        .withEndAction(() -> {
                            root.removeView(welcome);
                            Generator.saveTurn(MainActivity.this, turn);
                            populateClasses(adapter);
                        });
            }
        });
    }

    private void setupMode() {
        subscribers.add(Generator.initPreferences(this)
                .getBoolean(MODE, false)
                .asObservable()
                .subscribe(dark -> {
                    ((ImageView) findViewById(R.id.data_image)).setColorFilter(dark
                            ? Color.WHITE
                            : Color.BLACK);
                    ((TextView) findViewById(R.id.data_text)).setTextColor(dark
                            ? Color.WHITE
                            : Color.BLACK);
                    root.setBackgroundColor(dark
                            ? Color.parseColor(DARK_BG)
                            : Color.WHITE);
                    refresh.setProgressBackgroundColorSchemeColor(dark
                            ? Color.parseColor(DARK)
                            : Color.WHITE);
                    Objects.requireNonNull(recyclerClasses.getAdapter()).notifyDataSetChanged();
                }));
    }

    private void scheduleWork() {
        PeriodicWorkRequest work = new PeriodicWorkRequest.Builder(ApiWorker.class,
                3, TimeUnit.HOURS, 5, TimeUnit.MINUTES)
                .setConstraints(new Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build())
                .build();

        WorkManager.getInstance().enqueueUniquePeriodicWork(API_WORK, ExistingPeriodicWorkPolicy.KEEP, work);
    }

    private void populateClasses(GroupAdapter adapter) {
        adapter.clear();
        refresh.setRefreshing(false);
        dataHolder.setVisibility(View.GONE);
        loading.setVisibility(View.VISIBLE);
        subscribers.add(Generator.initClient().getClasses((Generator.getTurn(this) == NIGHT)
                ? NIGHT_PATH
                : MORNING_PATH)
                .flatMapIterable(classes -> {
                    dao.deleteLinks();
                    dao.deleteClasses();
                    Generator.saveClasses(this, new Gson().toJson(classes));
                    return classes;
                })
                .flatMap(item -> dao.insert(new Element(item, item.getLinks())))
                .toList()
                .toObservable()
                .flatMap(element -> dao.getAll())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .take(1)
                .subscribe(this::fillElements, error -> subscribers.add(dao.getAll()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .take(1)
                        .subscribe(this::fillElements))));
    }

    private void fillElements(List<Element> elements) {
        adapter.clear();
        loading.setVisibility(View.GONE);
        if (!elements.isEmpty()) {
            Collections.sort(elements, (first, second) ->
                    Long.compare(first.getItem().getDate(), second.getItem().getDate()));
            for (Element item : elements) {
                adapter.add(new DateItem(item.getItem().getDate()));
                adapter.add(new ClassItem(item.getItem(), this));
                adapter.notifyDataSetChanged();
            }
            scrollToClass(elements);
            recyclerClasses.scheduleLayoutAnimation();
        } else {
            dataHolder.setVisibility(View.VISIBLE);
        }
    }

    private void scrollToClass(List<Element> elements) {
        int scrollToPosition = 0;
        for (int i = 0; i < elements.size() - 1; i++) {
            if (System.currentTimeMillis() > (elements.get(i + 1).getItem().getDate() - (1000 * 60 * 60 * 2))) {
                scrollToPosition = i + 1;
            }
        }
        Objects.requireNonNull(recyclerClasses.getLayoutManager())
                .scrollToPosition(scrollToPosition * 2);
    }
}