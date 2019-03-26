package com.davinci.android.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.davinci.android.data.AppDatabase;
import com.davinci.android.data.Generator;
import com.davinci.android.items.EmptyItem;
import com.davinci.android.items.LinkItem;
import com.davinci.android.model.Element;
import com.davinci.android.model.Links;
import com.davinci.android.R;
import com.xwray.groupie.GroupAdapter;

import java.util.Locale;
import java.util.Objects;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.davinci.android.util.Constants.BUNDLE;
import static com.davinci.android.util.Constants.DARK_BG;
import static com.davinci.android.util.Constants.IMG_DETAIL;

public class DetailActivity extends AppCompatActivity {

    private Disposable subscriber;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        initToolbar();

        TextView title = findViewById(R.id.title_detail);
        TextView description = findViewById(R.id.description_detail);

        initMode(Generator.darkMode(this), R.id.description_title, R.id.description_detail, R.id.material_title);

        getElement(AppDatabase.getDatabase(this), item -> {

            title.setText(item.getItem().isExam()
                    ? String.format(new Locale("es", "ES"),
                    "%1$s", item.getItem().getTitle())
                    : String.format(new Locale("es", "ES"),
                    "Unidad: %1$d - %2$s", item.getItem().getUnity(), item.getItem().getTitle()));
            description.setText(item.getItem().getDescription());

            int resource = getResources().getIdentifier(item.getItem().getDrawable() + IMG_DETAIL,
                    "drawable", getPackageName());

            Glide.with(this).load(resource)
                    .into((ImageView) findViewById(R.id.image_detail));

            RecyclerView material = findViewById(R.id.recycler_material);
            GroupAdapter adapter = new GroupAdapter();
            material.setAdapter(adapter);

            if (item.getLinks().size() < 1) {
                adapter.add(new EmptyItem());
            } else {
                for (Links link : item.getLinks()) {
                    adapter.add(new LinkItem(link, this));
                }
            }
            adapter.notifyDataSetChanged();
        });
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void initMode(boolean dark, int... texts) {
        findViewById(R.id.root_detail).setBackgroundColor(dark ? Color.parseColor(DARK_BG) : Color.WHITE);
        for (int text : texts) ((TextView) findViewById(text)).setTextColor(dark ? Color.WHITE : Color.BLACK);
    }

    private void getElement(AppDatabase database, Task<Element> task) {
        subscriber = database.appDao().getElement(Objects.requireNonNull(
                getIntent().getExtras()).getInt(BUNDLE))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(task::done, error -> onBackPressed());
    }

    @Override
    protected void onPause() {
        super.onPause();
        subscriber.dispose();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private interface Task<T> {
        void done(T item);
    }
}
