package csc.game.word;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.GridLayout;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class LevelBonusWord {

    public enum checkWord{
        NOT_EXIST, ALREADY_FOUND, NEW_FOUND
    }

    private ArrayList<String> words;
    private ArrayList<String> foundWords;

    private final Context context;
    private GridLayout parentLayout;

    private static final String PREFS_BNS_WORDS = "bonusFoundCnt";
    private static final String WORDS = "words";

    LevelBonusWord(Context context){
        this.context = context;
        foundWords = new ArrayList<>();
    }

    LevelBonusWord(Context context, ArrayList<String> levelWords){
        this.context = context;
        this.words = levelWords;
        foundWords = new ArrayList<>();
    }

    public void setWords(ArrayList<String> words){
        this.words = words;
    }

    public ArrayList<String> getWords() {
        return words;
    }

    public ArrayList<String> getFoundWords() {
        return foundWords;
    }

    public checkWord checkIfBonusWord(String word){
        if(words.contains(word)){
            if (!foundWords.contains(word)){
                foundWords.add(word);
                return checkWord.NEW_FOUND;
            }
            return checkWord.ALREADY_FOUND;
        }
        return checkWord.NOT_EXIST;
    }

    public void readCache(){
        SharedPreferences prefs = context.getSharedPreferences(PREFS_BNS_WORDS, Context.MODE_PRIVATE);
        Set<String> set = prefs.getStringSet(WORDS, null);
        if (set!=null)
            foundWords.addAll(set);
    }

    public void clearCache(){
        SharedPreferences words = context.getSharedPreferences(PREFS_BNS_WORDS, Context.MODE_PRIVATE);
        words.edit().clear().apply();
    }

    public void saveToCache(){
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_BNS_WORDS, Context.MODE_PRIVATE).edit();
        Set<String> set = new HashSet<>(foundWords);
        editor.putStringSet(WORDS, set);
        editor.apply();
    }

}
