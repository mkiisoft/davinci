package com.davinci.android.ui;

import androidx.appcompat.app.AppCompatActivity;
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

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.davinci.android.data.ApiWorker;
import com.davinci.android.data.AppDatabase;
import com.davinci.android.data.ClassesDao;
import com.davinci.android.model.Element;
import com.davinci.android.util.Constants;
import com.davinci.android.data.Generator;
import com.davinci.android.R;
import com.davinci.android.items.ClassItem;
import com.davinci.android.items.DateItem;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        subscribers = new CompositeDisposable();

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

        populateClasses(adapter);
        setupMode();

        findViewById(R.id.davinci_logo).setOnClickListener(v -> {
            Preference<Boolean> mode = Generator.initPreferences(this)
                    .getBoolean(MODE, false);
            mode.set(!mode.get());
        });

        scheduleWork();
    }

    @Override
    protected void onDestroy() {
        subscribers.dispose();
        super.onDestroy();
    }

    private void setupMode() {
        subscribers.add(Generator.initPreferences(this)
                .getBoolean(MODE, false)
                .asObservable()
                .subscribe(dark -> {
                    findViewById(R.id.root_view).setBackgroundColor(dark
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
        loading.setVisibility(View.VISIBLE);
        subscribers.add(Generator.initClient().getClasses()
                .flatMapIterable(classes -> {
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
                        .subscribe(this::fillElements))));
    }

    private void fillElements(List<Element> elements) {
        adapter.clear();
        loading.setVisibility(View.GONE);
        Collections.sort(elements, (first, second) ->
                Long.compare(first.getItem().getDate(), second.getItem().getDate()));
        for (Element item : elements) {
            adapter.add(new DateItem(item.getItem().getDate()));
            adapter.add(new ClassItem(item.getItem(), this));
            adapter.notifyDataSetChanged();
        }
        scrollToClass(elements);
        recyclerClasses.scheduleLayoutAnimation();
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
