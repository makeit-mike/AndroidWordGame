package csc.game.word;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int TOTAL_LEVEL = 50;
    public static final String PREF_CURR_LVL = "currentLevel";
    public static final String KEY_IS_CURR = "isCurrent";
    public static final String KEY_LVL_NUM = "levelNumber";
    public static final String PREF_KEY_CURR = "level";
    GridLayout levelCont;
    private int currentLvl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initiateViews();
        generateLevelButtons();
    }

    private void initiateViews() {
        levelCont = findViewById(R.id.levelNums);
    }

    private void generateLevelButtons() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels-200;

        SharedPreferences prefs = getSharedPreferences(PREF_CURR_LVL, MODE_PRIVATE);
        currentLvl = prefs.getInt(PREF_KEY_CURR, 1);

        for (int i=1; i<TOTAL_LEVEL+1; i++){
            Button button = new Button(this);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(width/5, width/5);
            lp.setMargins(width/50, width/50, width/50, width/50);
            button.setPadding(10, 10, 10, 10);
            button.setText(i+"");
            button.setLayoutParams(lp);

            if (i==currentLvl){
                button.setBackground(getDrawable(R.drawable.bg_lvl_current));
                button.setOnClickListener(this);
            }
            else if (i<currentLvl) {
                button.setBackground(getDrawable(R.drawable.bg_lvl_complete));
                button.setOnClickListener(this);
            }
            else if (i>currentLvl){
                button.setBackground(getDrawable(R.drawable.bg_btn_holder));
            }

            levelCont.addView(button);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = getSharedPreferences(PREF_CURR_LVL, MODE_PRIVATE);
        int lvl = prefs.getInt(PREF_KEY_CURR, 1);
        if (lvl!=currentLvl){
            currentLvl = lvl;
            levelCont.removeAllViews();
            generateLevelButtons();
        }
    }

    @Override
    public void onClick(View view) {

        int lvl = Integer.valueOf(((Button)view).getText().toString());

        Intent intent = new Intent(this, LevelActivity.class);
        intent.putExtra(KEY_LVL_NUM, lvl);
        intent.putExtra(KEY_IS_CURR, lvl==currentLvl);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        exit();
    }

    private void exit() {
        new AlertDialog.Builder(this)
                .setTitle("Exit")
                .setMessage("Are you sure you want to exit?")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }
}
