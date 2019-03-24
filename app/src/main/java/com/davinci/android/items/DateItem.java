package com.davinci.android.items;

import android.graphics.Color;
import android.widget.TextView;

import com.davinci.android.util.Constants;
import com.davinci.android.data.Generator;
import com.davinci.android.R;
import com.xwray.groupie.Item;
import com.xwray.groupie.ViewHolder;

import java.text.SimpleDateFormat;
import java.util.Locale;

import androidx.annotation.NonNull;

public class DateItem extends Item<ViewHolder> {

    private long date;

    public DateItem(long date) {
        this.date = date;
    }

    @Override
    public void bind(@NonNull ViewHolder viewHolder, int position) {
        TextView textDate = viewHolder.itemView.findViewById(R.id.text_date);
        textDate.setText(String.format("%1$shs", wordFirstCap(getDate(date))));

        long time = System.currentTimeMillis() + (1000 * 60 * 60 * 2);
        if (time > date) {
            viewHolder.itemView.setAlpha(0.3f);
        } else {
            viewHolder.itemView.setAlpha(1f);
        }

        if (Generator.darkMode(viewHolder.itemView.getContext())) {
            textDate.setTextColor(Color.WHITE);
        } else {
            textDate.setTextColor(Color.parseColor(Constants.DARK));
        }
    }

    @Override
    public int getLayout() {
        return R.layout.item_date;
    }

    private static String getDate(long milliSeconds) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy - HH:mm",
                new Locale("es", "ES"));
        return formatter.format(milliSeconds);
    }

    private static String wordFirstCap(String str) {
        String[] words = str.trim().split(" ");
        StringBuilder ret = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            if (words[i].trim().length() > 0) {
                ret.append(Character.toUpperCase(words[i].trim().charAt(0)));
                ret.append(words[i].trim().substring(1));
                if (i < words.length - 1) {
                    ret.append(' ');
                }
            }
        }

        return ret.toString();
    }
}
