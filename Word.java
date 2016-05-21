package com.aac.wsg.alyssa;

import java.util.ArrayList;

/**
 * Created by ASUS on 5/2/2016.
 */
public class Word{
    private int _id;
    private String string;
    private String imgpath;
    private String query;
    private ArrayList<Tag> tags;

    public Word(int _id, String string, String imgpath) {
        this._id = _id;
        this.string = string;
        this.imgpath = imgpath;
    }

    public Word(String query, String string, String imgpath) {
        this.query = query;
        this.tags = tags;
        this.string = string;
        this.imgpath = imgpath;
    }

    public Word() {
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public void setString(String string) {
        this.string = string;
    }

    public String getImgpath() {
        return imgpath;
    }

    public void setImgpath(String imgpath) {
        this.imgpath = imgpath;
    }

    public String getString() {
        return string;
    }

    public ArrayList<Tag> getTags() {
        return tags;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public void setTags(ArrayList<Tag> tags) {
        this.tags = tags;
    }

    public void removeTag(String tag){
        for(int i = 0; i < tags.size(); i++){
            if(tag.equals(tags.get(i).getString())){
                tags.remove(i);
                break;
            }
        }
    }

    public void addTag(String tag){
        tags.add(new Tag(tag));
    }
}
