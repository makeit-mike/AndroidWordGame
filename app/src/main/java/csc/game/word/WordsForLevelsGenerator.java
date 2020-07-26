package csc.game.word;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

public class WordsForLevelsGenerator {

    static ArrayList[] findLevelRandomWords(int levelCount, Context context) {
        ArrayList<String> freqWords = readFrequentWords(context);
        ArrayList<String> allWords = readDictWords(context);

        Trie freqTrie = new Trie(freqWords);
        Trie allTrie = new Trie(allWords);

        ArrayList<String> lvlWords = new ArrayList<>();
        ArrayList<ArrayList<String>> createdWords = new ArrayList<>();
        ArrayList<ArrayList<String>> extraWords = new ArrayList<>();

        for (int i=0; i<levelCount; i++){
            int rand;
            boolean condtn = true;
            do{
                rand = (int)(Math.random() * freqWords.size());
                if (freqWords.get(rand).length()==4*i/levelCount+4){
                    ArrayList<String> makeWords =  freqTrie.findWords(freqWords.get(rand));
                    if (makeWords.size()>5){

                        modifyArr(makeWords);
                        lvlWords.add(freqWords.get(rand));
                        createdWords.add(makeWords);
                        extraWords.add(allTrie.findWords(freqWords.get(rand)));
                        condtn = false;
                    }
                }
            }while( condtn );
        }

        return new ArrayList[]{lvlWords, createdWords, extraWords};
    }

    public static void modifyArr(ArrayList<String> makeWords) {
        if (makeWords.size()<8)
            return;

        for (int i=0; i<makeWords.size(); i++){
            String str1 = makeWords.get(i);
            for (int k=i+1; k<makeWords.size(); k++){
                String str2 = makeWords.get(k);
                if (str1.length()<str2.length())
                    Collections.swap(makeWords, i, k);
            }
        }

        if (makeWords.size() > 8) {
            makeWords.subList(8, makeWords.size()).clear();
        }

    }

    private static ArrayList<String> getAllLevelWord(ArrayList<String> mainLvlWords, Context context) {

        ArrayList<String> words = readDictWords(context);

        Trie trie = new Trie(words);
        ArrayList<ArrayList<String>> levelWords = new ArrayList<>();

        for (String word: mainLvlWords){
            levelWords.add(trie.findWords(word));
        }

        for (int i=0; i<mainLvlWords.size(); i++){
            System.out.println("Level "+i+": "+mainLvlWords.get(i));
            for (String str : levelWords.get(i))
                System.out.println(str);
        }

        return mainLvlWords;
    }

    private static ArrayList<String> readDictWords(Context context) {
        ArrayList<String> words = new ArrayList<>();

        try {

            BufferedReader br = new BufferedReader(
                    new InputStreamReader(context.getAssets().open("words")));

            String st;
            while ((st = br.readLine()) != null) {
                st = st.trim();
                if (st.length()>2 && st.length()<9 && !st.contains("-") && !st.contains(".") && !st.contains("'")){
                    words.add(st);
                }
            }

            br.close();

        } catch (Exception e) {
            Log.e("mylog", e.toString());
        }


        return words;
    }

    private static ArrayList<String> readFrequentWords(Context context) {
        ArrayList<String> words = new ArrayList<>();

        try {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(context.getAssets().open("words_freq")));

            String st;
            while ((st = br.readLine()) != null) {
                st = st.trim().toLowerCase();
                if (st.length()>2 && st.length()<9 && !st.contains("-") && !st.contains(".") && !st.contains("'")){
                    words.add(st);
                }
            }
            br.close();
        } catch (Exception e) {
            Log.d("mylog", e.toString());
        }

        return words;
    }

}
