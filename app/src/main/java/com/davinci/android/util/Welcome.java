package com.davinci.android.util;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.davinci.android.R;

import java.util.ArrayList;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;

public class Welcome extends ConstraintLayout {

    Constants click;

    int counterChoose = 0;
    int counterWelcome = 0;

    LinearLayoutCompat choose;
    LinearLayoutCompat welcome;
    LinearLayoutCompat buttons;

    ArrayList<Character> charChoose;
    ArrayList<Character> charWelcome;

    public Welcome(Context context) {
        super(context);
        init(context);
    }

    public Welcome(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public Welcome(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void clickMode(Constants click) {
        this.click = click;
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.view_welcome, this, true);

        welcome = view.findViewById(R.id.welcome_layout);
        String textWelcome = getContext().getString(R.string.welcome_text);
        charWelcome = new ArrayList<>();

        initViews(context, textWelcome, charWelcome, welcome, true);

        choose = view.findViewById(R.id.choose_layout);
        String textChoose = getContext().getString(R.string.select_turn);
        charChoose = new ArrayList<>();

        initViews(context, textChoose, charChoose, choose, false);

        postDelayed(this::animateView, 500);

        buttons = view.findViewById(R.id.buttons);
        buttons.setAlpha(0f);
        buttons.setTranslationY(200);

        findViewById(R.id.click_night).setOnClickListener(v -> click.click(Constants.NIGHT));
        findViewById(R.id.click_morning).setOnClickListener(v -> click.click(Constants.MORNING));
    }

    private void initViews(Context context, String text, ArrayList<Character> characters, LinearLayoutCompat root,
                           boolean isTitle) {
        for (char letter : text.toCharArray()) {
            characters.add(letter);
            root.addView(createLetter(context, letter, isTitle));
        }

        for (int index : range(characters.size())) {
            View txview = root.getChildAt(index);
            txview.setTranslationY(isTitle ? 200 : 100);
            txview.setScaleX(1.2f);
            txview.setScaleY(1.2f);
            txview.setAlpha(0f);
        }
    }

    private void animateView() {
        if (counterWelcome < charWelcome.size()) {
            welcome.getChildAt(counterWelcome).animate().setDuration(250)
                    .setStartDelay(10)
                    .translationY(0)
                    .scaleX(1)
                    .scaleY(1)
                    .alpha(1f)
                    .withLayer()
                    .setInterpolator(new DecelerateInterpolator())
                    .withStartAction(() -> {
                        ++counterWelcome;
                        animateView();
                    });
        } else {
            if (counterChoose < charChoose.size()) {
                choose.getChildAt(counterChoose).animate().setDuration(50)
                        .setStartDelay(10)
                        .translationY(0)
                        .scaleX(1)
                        .scaleY(1)
                        .alpha(1f)
                        .withLayer()
                        .setInterpolator(new DecelerateInterpolator())
                        .withStartAction(() -> {
                            ++counterChoose;
                            animateView();
                        });
            } else {
                buttons.animate().setDuration(600)
                        .alpha(1f)
                        .withLayer()
                        .translationY(0)
                        .setStartDelay(200)
                        .setInterpolator(new DecelerateInterpolator())
                        .start();
            }
        }
    }

    private TextView createLetter(Context context, char letter, boolean isTitle) {
        LinearLayoutCompat.LayoutParams layoutParams = new LinearLayoutCompat.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        TextView letterText = new TextView(context);
        letterText.setLayoutParams(layoutParams);

        letterText.setText(String.valueOf(letter));
        letterText.setTextColor(Color.WHITE);
        letterText.setTextSize(TypedValue.COMPLEX_UNIT_SP, isTitle ? 35 : 25);
        letterText.setTypeface(ResourcesCompat.getFont(context, R.font.bariol), isTitle ? Typeface.BOLD : Typeface.NORMAL);
        return letterText;
    }

    public static int[] range(int length) {
        int[] r = new int[length];
        for (int i = 0; i < length; i++) {
            r[i] = i;
        }
        return r;
    }
}