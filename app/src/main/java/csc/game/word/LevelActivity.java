package csc.game.word;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

public class LevelActivity extends AppCompatActivity implements View.OnClickListener {

    private static boolean IS_CURRENT = false;
    private String LEVEL_WORD;
    private int LEVEL_NUMBER;

    Fragment circleLettersFragment;
    GridLayout wordsSpaceContainer;
    TextView currentText, points, bonusFoundCnt, title;
    RelativeLayout bonusCont;

    ImageButton back, helpButton;

    GamePoints gamePoints;
    LevelBonusWord bonusWords;
    LevelWordsContainer levelWords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level);

        LEVEL_NUMBER = getIntent().getIntExtra(MainActivity.KEY_LVL_NUM, 1);
        IS_CURRENT = getIntent().getBooleanExtra(MainActivity.KEY_IS_CURR, false);

        gamePoints = new GamePoints(this, IS_CURRENT, new Runnable() {
            @Override
            public void run() {
                updatePoints();
            }
        });
        levelWords = new LevelWordsContainer(this);
        bonusWords = new LevelBonusWord(this);

        initiateViews();
        getLevelWords();

        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getFragments().size()==0) {
            circleLettersFragment = CircleLettersFragment.newInstance(LEVEL_WORD);
            fragmentManager
                    .beginTransaction()
                    .add(R.id.letters_menu_fragment, circleLettersFragment)
                    .commit();
        }

    }

    private void getLevelWords() {

        DB db = new DB(this);
        Log.d("mylog", "read");
        db.readDB();
        Cursor cursor = db.getLevelData(LEVEL_NUMBER);
        ArrayList<String> mainLevelWords = new ArrayList<>();
        ArrayList<String> allLevelWords = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                String word = cursor.getString(cursor.getColumnIndex(DB.WORD));
                int category = cursor.getInt(cursor.getColumnIndex(DB.CATEGORY));

                switch (category){
                    case DB.CATEGORY_LVL:
                        LEVEL_WORD = word;
                        Log.d("mylog", "LEVEL: "+word);
                        break;
                    case DB.CATEGORY_MAIN:
                        mainLevelWords.add(word);
                        Log.d("mylog", "Words: "+word);
                        break;
                    case DB.CATEGORY_EXTRA:
                        allLevelWords.add(word);
                        Log.d("mylog", "Extra:"+word);
                        break;
                }

            } while(cursor.moveToNext());
        }
        cursor.close();
        db.closeDB();

        sortWordsForLevel(mainLevelWords);

        levelWords.setWords(mainLevelWords);
        bonusWords.setWords(allLevelWords);
        if (IS_CURRENT){
            levelWords.readCache();
            bonusWords.readCache();
            bonusFoundCnt.setText(String.valueOf(bonusWords.getFoundWords().size()));
        }
    }

    private void sortWordsForLevel(ArrayList<String> words) {
        for (int i=0; i<words.size(); i++){
            for (int k=i+1; k<words.size(); k++){
                if (words.get(i).length()>words.get(k).length())
                    Collections.swap(words, i, k);
                else if ( words.get(i).length()==words.get(k).length() &&
                            words.get(i).compareTo(words.get(k))>0 ){
                    Collections.swap(words, i, k);
                }
            }
        }
    }

    private void initiateViews(){
        currentText = findViewById(R.id.current_text);
        wordsSpaceContainer = findViewById(R.id.words_container);
        points = findViewById(R.id.points);
        bonusFoundCnt = findViewById(R.id.bonusWord);
        bonusCont = findViewById(R.id.bonusCont);
        title = findViewById(R.id.levelTitle);

        back = findViewById(R.id.back);
        helpButton = findViewById(R.id.help);

        //Set onclick listeners
        back.setOnClickListener(this);
        bonusCont.setOnClickListener(this);
        helpButton.setOnClickListener(this);

        //Set values
        levelWords.setParentLayout(wordsSpaceContainer);

        title.setText("Level "+LEVEL_NUMBER);
        points.setText(String.valueOf(gamePoints.getPoint()));
    }


    public void onNewLetter(String currentWord) {
        currentText.setText(currentWord);
        currentText.setBackground(getDrawable(R.drawable.bg_current_text));
    }

    public void checkFoundWord(String foundWord) {
        foundWord = foundWord.toLowerCase();
        switch (levelWords.checkIfLevelWord(foundWord)){
            case LEVEL_COMPLETE:
                onRightWord();
                gamePoints.addPoint(foundWord.length());
                completeLevel();
                if (IS_CURRENT){
                    levelWords.clearCache();
                    bonusWords.clearCache();
                }
                return;
            case NEW_FOUND:
                onRightWord();
                gamePoints.addPoint(foundWord.length());
                if (IS_CURRENT)
                    levelWords.saveToCache();
                return;
            case ALREADY_FOUND:
                onRightWord();
                return;
            case NOT_EXIST:
                onWrongWord();
                break;
        }

        switch (bonusWords.checkIfBonusWord(foundWord)){
            case NEW_FOUND:
                onRightWord();
                foundBonus();
                gamePoints.addPoint(foundWord.length());
                if (IS_CURRENT)
                    bonusWords.saveToCache();
                return;
            case ALREADY_FOUND:
                onRightWord();
                foundBonus();
                Toast.makeText(this, "Bonus word is already found!", Toast.LENGTH_SHORT).show();
                return;
            case NOT_EXIST:
                onWrongWord();
                break;
        }
    }

    private void completeLevel() {

        SharedPreferences.Editor editor = getSharedPreferences(MainActivity.PREF_CURR_LVL, MODE_PRIVATE).edit();
        editor.putInt(MainActivity.PREF_KEY_CURR, LEVEL_NUMBER+1);
        editor.apply();

        new AlertDialog.Builder(this)
                .setTitle("Good job!")
                .setMessage("Good luck in the next level!")
                .setCancelable(false)
                .setPositiveButton("Next", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intent = new Intent(LevelActivity.this, LevelActivity.class);
                        intent.putExtra(MainActivity.KEY_LVL_NUM, LEVEL_NUMBER+1);
                        intent.putExtra(MainActivity.KEY_IS_CURR, true);
                        startActivity(intent);

                        finish();
                    }
                })
                .show();
    }

    private void foundBonus() {
        bonusFoundCnt.setText(String.valueOf(bonusWords.getFoundWords().size()));
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.anim_letter_found);
        bonusFoundCnt.startAnimation(anim);
    }

    private void updatePoints() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.anim_letter_found);
        points.startAnimation(anim);
        points.setText(String.valueOf(gamePoints.getPoint()));
    }

    private void onRightWord(){
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.anim_letter_found);
        anim.setAnimationListener(new TranslateAnimation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) { }

            @Override
            public void onAnimationRepeat(Animation animation) { }

            @Override
            public void onAnimationEnd(Animation animation)
            {
                currentText.setText("");
                currentText.setBackground(null);
            }
        });

        currentText.startAnimation(anim);

    }

    private void onWrongWord(){
        Animation animShake = AnimationUtils.loadAnimation(this, R.anim.anim_shake_wrong);
        currentText.startAnimation(animShake);
        currentText.animate()
                .setDuration(400)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        currentText.setText("");
                        currentText.setBackground(null);
                    }
                })
                .start();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                exit();
                break;
            case R.id.bonusCont:
                showExtraFoundWords();
                break;
            case R.id.help:
                onClickHelp();
                break;
        }
    }

    private void onClickHelp() {
        if (gamePoints.getPoint() >9)
            if (levelWords.openOneLetter()) {
                gamePoints.addPoint(-10);
                if (IS_CURRENT)
                    levelWords.cacheHints();
            }else
                Toast.makeText(this, "There is no any UNKNOWN LETTER!", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "You have to have at least 10 points!", Toast.LENGTH_SHORT).show();


    }

    public void showExtraFoundWords() {
        if (bonusWords.getFoundWords().size()==0)
            return;

        String[] wrds = new String[bonusWords.getFoundWords().size()];
        int i=0;
        for (String str: bonusWords.getFoundWords())
            wrds[i++] = str.toUpperCase();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setItems(wrds, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        exit();
    }

    private void exit() {
        new AlertDialog.Builder(this)
                .setTitle("Exit")
                .setMessage("Are you sure you want to exit from this level?")

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
