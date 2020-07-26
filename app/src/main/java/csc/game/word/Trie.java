package csc.game.word;

import java.util.ArrayList;

public class Trie{
    TrieNode root;

    Trie(){
        root = new TrieNode();
    }

    Trie(ArrayList<String> arr){
        root = new TrieNode();
        for (String str : arr)
            addWord(str);
    }

    public void addWord(String str){
        addWordRecursion(root, str);
    }

    private void addWordRecursion(TrieNode parent, String subString){
        if (subString.length()==0){
            parent.leaf = true;
            return;
        }

        char c = subString.charAt(0);
        if (parent.children[c-97]==null)
            parent.children[c-97] = new TrieNode(c);

        addWordRecursion(parent.children[c-97], subString.substring(1));
    }

    public void findWords(String str, ArrayList<String> foundWords){
        findWordsRecursion(root, str, "", foundWords);
    }

    public ArrayList<String> findWords(String str){
        ArrayList<String> foundWords = new ArrayList<>();
        findWordsRecursion(root, str, "", foundWords);
        return foundWords;
    }

    private void findWordsRecursion(TrieNode parent, String subWord, String checkingWord, ArrayList<String> foundWords){
        if (parent.leaf && !foundWords.contains(checkingWord))
            foundWords.add(checkingWord);

        if (subWord.length()==0)
            return;

        for (int i = 0; i < subWord.length(); i++) {
            char c = subWord.charAt(i);
            if (parent.children[c-97]!=null){
                findWordsRecursion(
                        parent.children[c-97],
                        subWord.substring(0, i) + subWord.substring(i+1),
                        checkingWord + c,
                        foundWords
                );
            }
        }
    }
}


class TrieNode{
    char value = 0;
    TrieNode[] children = new TrieNode[26];
    boolean leaf = false;   //End of word

    TrieNode(){
        value = 0;
    }

    TrieNode(char c){
        value = c;
    }

}