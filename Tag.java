package com.aac.wsg.alyssa;

/**
 * Created by ASUS on 5/6/2016.
 */
public class Tag {
    private int _id;
    private String string;

    public Tag(String t_string) {
        this.string = t_string;
    }

    public int get_id() {
        return _id;
    }

    public String getString() {

        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public void set_id(int _id) {
        this._id = _id;
    }
}
