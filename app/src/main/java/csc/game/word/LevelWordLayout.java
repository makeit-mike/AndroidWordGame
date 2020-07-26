package csc.game.word;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

@SuppressLint("ViewConstructor")
public class LevelWordLayout extends LinearLayout {
    private int length;
    Context context;

    public LevelWordLayout(Context context, int wordLength) {
        super(context);
        this.context = context;
        this.length = wordLength;

        this.setOrientation(HORIZONTAL);

        LayoutParams lp = new LayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        lp.setMargins(20, 2, 20, 2);
        this.setLayoutParams(lp);

        addLetterHolders();
    }

    private void addLetterHolders() {
        for (int i=0; i<length; i++){
            this.addView(getLetterHolder());
        }
    }


    private View getLetterHolder() {
        TextView tv = new TextView(context);
        LayoutParams lp = new LayoutParams(80, 100);
        lp.setMargins(5, 7, 5, 7);
        tv.setLayoutParams(lp);
        tv.setGravity(Gravity.CENTER);
        tv.setTextColor(context.getResources().getColor(R.color.colorSecondary));
        tv.setBackground(context.getDrawable(R.drawable.bg_one_letter_holder));
        tv.setTextSize(18);

        return tv;
    }

    public void setHintLetter(int index, char c){
        final TextView tv = (TextView) this.getChildAt(index);
        final String letter = String.valueOf(c);
        final Animation anim = AnimationUtils.loadAnimation(context, R.anim.anim_letter_found);
//        tv.setBackground(context.getDrawable(R.drawable.bg_one_letter_holder_active));
        tv.startAnimation(anim);
        tv.setText(letter.toUpperCase());
        tv.setTextColor(Color.BLACK);
    }

    public void setFoundWord(String word){
        word = word.toUpperCase();
        for (int i=0; i<word.length(); i++){
            final TextView tv = (TextView) this.getChildAt(i);
            final String letter = String.valueOf(word.charAt(i));
            final Animation anim = AnimationUtils.loadAnimation(context, R.anim.anim_letter_found);

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    tv.setBackground(context.getDrawable(R.drawable.bg_one_letter_holder_active));
                    tv.startAnimation(anim);
                    tv.setText(letter);
                    tv.setTextColor(context.getResources().getColor(R.color.colorSecondary));
                }
            }, i*100);
        }
    }
}