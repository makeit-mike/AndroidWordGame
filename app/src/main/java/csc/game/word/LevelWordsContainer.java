package csc.game.word;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.GridLayout;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class LevelWordsContainer {

    public enum checkWord{
        NOT_EXIST, ALREADY_FOUND, NEW_FOUND, LEVEL_COMPLETE
    }

    private ArrayList<String> words;
    private ArrayList<String> foundWords;
    private ArrayList<Integer[]> hints;

    private GridLayout parentLayout;
    private Context context;

    private static final String PREFS_LVL_WORDS = "lvlWords";
    private static final String WORDS = "words";
    private static final String HINTS = "hints";

    LevelWordsContainer(Context context){
        this.context = context;
        foundWords = new ArrayList<>();
        hints = new ArrayList<>();
    }

    LevelWordsContainer(Context context, ArrayList<String> words){
        this(context);
        this.words = words;
    }

    public void setWords(ArrayList<String> words) {
        this.words = words;
        if (parentLayout!=null)
            initiateWordHolders();
    }

    public void setParentLayout(GridLayout gridLayout){
        parentLayout = gridLayout;
        if (words!=null)
            initiateWordHolders();
    }

    public ArrayList<String> getWords() {
        return words;
    }

    public ArrayList<String> getFoundWords() {
        return foundWords;
    }

    public void openOneLetter(int wordNumber, int letterNumber){
        hints.add(new Integer[]{wordNumber, letterNumber});
        LevelWordLayout layout = (LevelWordLayout) parentLayout.getChildAt(wordNumber);
        layout.setHintLetter(letterNumber, words.get(wordNumber).charAt(letterNumber));
    }

    public boolean openOneLetter(){
        for (int i=0; i<words.size(); i++) {
            if (foundWords.contains(words.get(i)))
                continue;
            for (int j = 0; j < words.get(i).length(); j++){
                if (!isAlreadyOpened(i ,j)){
                    openOneLetter(i, j);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isAlreadyOpened(int i, int j) {
        for (Integer[] hint : hints){
            if (hint[0]==i && hint[1]==j)
                return true;
        }
        return false;
    }

    private void initiateWordHolders() {
        int size = words.size();
        if (size>5){
            parentLayout.setRowCount(size/2+size%2);
        }

        for (String word : words){
            LevelWordLayout layout = new LevelWordLayout(context, word.length());
            parentLayout.addView(layout);
        }
    }

    public checkWord checkIfLevelWord(String word){
        int index = words.indexOf(word);
        boolean isContain = index > -1;
        if(isContain){
            foundWord(word, index);
            if (!foundWords.contains(word)){
                foundWords.add(word);
                if (foundWords.size()==words.size())
                    return checkWord.LEVEL_COMPLETE;

                return checkWord.NEW_FOUND;
            }
            return checkWord.ALREADY_FOUND;
        }
        return checkWord.NOT_EXIST;
    }

    private void foundWord(String foundWord, int index) {
        LevelWordLayout spaceLayout = (LevelWordLayout) parentLayout.getChildAt(index);
        spaceLayout.setFoundWord(foundWord);

    }

    public void readCache(){
        SharedPreferences prefs = context.getSharedPreferences(PREFS_LVL_WORDS, Context.MODE_PRIVATE);
        Set<String> set = prefs.getStringSet(WORDS, null);
        if (set!=null)
            foundWords.addAll(set);

        for (String word : foundWords){
            int i = words.indexOf(word);
            if (i>-1)
                foundWord(word, i);
        }

        String str = prefs.getString(HINTS, null);
        if (str!=null)
            for (String s: str.split(";")) {
                String[] pair = s.split(",");
                if (pair.length > 1) {
                    int wordNum = Integer.valueOf(pair[0]);
                    int letterNum = Integer.valueOf(pair[1]);
                    openOneLetter(wordNum, letterNum);
                }
            }

    }

    public void clearCache(){
        SharedPreferences words = context.getSharedPreferences(PREFS_LVL_WORDS, Context.MODE_PRIVATE);
        words.edit().clear().apply();
    }

    public void saveToCache(){
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_LVL_WORDS, Context.MODE_PRIVATE).edit();
        Set<String> set = new HashSet<>(foundWords);
        editor.putStringSet(WORDS, set);
        editor.apply();
    }

    public void cacheHints(){
        StringBuilder str = new StringBuilder();
        for (Integer[] arr: hints){
            if (!foundWords.contains(words.get(arr[0])))
                str.append(arr[0]).append(",").append(arr[1]).append(";");
        }

        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_LVL_WORDS, Context.MODE_PRIVATE).edit();
        editor.putString(HINTS, str.toString());
        editor.apply();
    }
}
