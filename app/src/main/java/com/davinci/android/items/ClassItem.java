package com.davinci.android.items;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.davinci.android.model.Classes;
import com.davinci.android.util.Constants;
import com.davinci.android.ui.DetailActivity;
import com.davinci.android.data.Generator;
import com.davinci.android.R;
import com.xwray.groupie.Item;
import com.xwray.groupie.ViewHolder;

import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

public class ClassItem extends Item<ViewHolder> {

    private final Classes item;
    private final Activity activity;

    public ClassItem(Classes item, Activity activity) {
        this.item = item;
        this.activity = activity;
    }

    @Override
    public void bind(@NonNull ViewHolder viewHolder, int position) {
        CardView holder = viewHolder.itemView.findViewById(R.id.card_holder);

        TextView title = viewHolder.itemView.findViewById(R.id.unity_title);
        title.setText(item.isExam()
                ? String.format(new Locale("es", "ES"),
                "%1$s", item.getTitle())
                : String.format(new Locale("es", "ES"),
                "Unidad: %1$d - %2$s", item.getUnity(), item.getTitle()));

        TextView classes = viewHolder.itemView.findViewById(R.id.class_classes);
        classes.setText(String.format("Clases: %1$s", item.getRounds()));

        viewHolder.itemView.findViewById(R.id.exam_label).setVisibility(item.isExam()
                ? View.VISIBLE
                : View.GONE);

        ImageView image = viewHolder.itemView.findViewById(R.id.unity_image);

        int resource = viewHolder.itemView.getContext().getResources().getIdentifier(item.getDrawable(),
                "drawable", viewHolder.itemView.getContext().getPackageName());
        Glide.with(viewHolder.itemView.getContext()).load(resource).into(image);

        long date = System.currentTimeMillis() - (1000 * 60 * 60 * 2);
        if (date > item.getDate()) {
            viewHolder.itemView.setAlpha(0.3f);
        } else {
            viewHolder.itemView.setAlpha(1f);
        }

        if (Generator.darkMode(viewHolder.itemView.getContext())) {
            title.setTextColor(Color.WHITE);
            classes.setTextColor(Color.WHITE);
            holder.setCardBackgroundColor(Color.parseColor(Constants.DARK));
        } else {
            title.setTextColor(Color.BLACK);
            classes.setTextColor(Color.BLACK);
            holder.setCardBackgroundColor(Color.WHITE);
        }

        viewHolder.itemView.setOnClickListener(v ->
                activity.startActivity(new Intent(activity, DetailActivity.class)
                        .putExtra(Constants.BUNDLE, item.getId())));
    }

    @Override
    public int getLayout() {
        return R.layout.item_class;
    }
}
