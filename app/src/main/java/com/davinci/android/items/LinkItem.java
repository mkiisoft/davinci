package com.davinci.android.items;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.TextView;

import com.davinci.android.R;
import com.davinci.android.data.Generator;
import com.davinci.android.model.Links;
import com.xwray.groupie.Item;
import com.xwray.groupie.ViewHolder;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

public class LinkItem extends Item<ViewHolder> {

    private final Links link;
    private final Activity activity;

    public LinkItem(Links link, Activity activity) {
        this.link = link;
        this.activity = activity;
    }

    @Override
    public void bind(@NonNull ViewHolder viewHolder, int position) {
        TextView textLink = viewHolder.itemView.findViewById(R.id.link_text);
        textLink.setText(String.format("- %1$s", link.getName()));

        ImageView web = viewHolder.itemView.findViewById(R.id.ic_web);

        viewHolder.itemView.setOnClickListener(v ->
                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link.getLink()))));

        viewHolder.itemView.setOnLongClickListener(v -> {
                    activity.startActivity(new Intent(Intent.ACTION_SEND)
                            .setType("text/plain")
                            .putExtra(Intent.EXTRA_SUBJECT, R.string.share_link)
                            .putExtra(Intent.EXTRA_TEXT, link.getLink()));
                    return true;
                });

        if (Generator.darkMode(viewHolder.itemView.getContext())) {
            web.setColorFilter(Color.WHITE);
            textLink.setTextColor(Color.WHITE);
            viewHolder.itemView.findViewById(R.id.root_link)
                    .setBackground(ContextCompat.getDrawable(activity, R.drawable.select_item));
        } else {
            web.setColorFilter(Color.BLACK);
            textLink.setTextColor(Color.BLACK);
            viewHolder.itemView.findViewById(R.id.root_link)
                    .setBackground(ContextCompat.getDrawable(activity, R.drawable.select_item_dark));
        }
    }

    @Override
    public int getLayout() {
        return R.layout.item_link;
    }
}
