package com.davinci.android.items;

import android.graphics.Color;
import android.widget.TextView;

import com.davinci.android.R;
import com.davinci.android.data.Generator;
import com.xwray.groupie.Item;
import com.xwray.groupie.ViewHolder;

import androidx.annotation.NonNull;

public class EmptyItem extends Item<ViewHolder> {

    @Override
    public void bind(@NonNull ViewHolder viewHolder, int position) {
        TextView noLinks = viewHolder.itemView.findViewById(R.id.text_empty);
        if (Generator.darkMode(viewHolder.itemView.getContext())) {
            noLinks.setTextColor(Color.WHITE);
        } else {
            noLinks.setTextColor(Color.BLACK);
        }
    }

    @Override
    public int getLayout() {
        return R.layout.item_empty;
    }
}
