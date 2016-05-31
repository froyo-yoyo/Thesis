package com.aac.wsg.alyssa;

import android.content.Context;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.EmptyStackException;

/**
 * Created by ASUS on 5/7/2016.
 */
public class Message {
    private ArrayList<Word> wordArrayList;
    private Context context;

    public Message(Context context) {
        this.wordArrayList = new ArrayList<Word>();
        this.context = context;
    }

    public void push(Word word) {
        wordArrayList.add(word);
    }

    public Word pop(){
        if(!wordArrayList.isEmpty()){
            return wordArrayList.get(wordArrayList.size() - 1);
        }else throw new EmptyStackException();
    }

    public void update(){
        // if there are changes in words (images, string), update the list
    }
}
