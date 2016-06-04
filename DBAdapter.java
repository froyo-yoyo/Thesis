package com.aac.wsg.alyssa;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.widget.Toast;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by ASUS on 4/19/2016.
 */
public class DBAdapter {
    DBHelper dbHelper;
    final String TAG = "DBAdapter";

    public DBAdapter(Context context){
        dbHelper = new DBHelper(context);
    }

    static class DBHelper extends SQLiteAssetHelper {
        private static final String DATABASE_NAME = "wordDatabase.db";
        private static final int DATABASE_VERSION = 1;

        private static final String TAG_TABLE = "tag";
        private static final String WORD_TABLE = "word";
        private static final String MAP_TABLE = "w_map";
        private static final String CATEGORY_TABLE = "category";
        private static final String C_MAP_TABLE = "c_map";

        private static final String W_STRING = "w_string";
        private static final String T_STRING = "t_string";
        private static final String C_STRING = "c_string";

        private static final String W_IMGPATH = "w_imgpath";
        private static final String C_IMGPATH = "c_imgpath";

        private static final String W_ID = "_id";
        private static final String T_ID = "_id";
        private static final String C_ID = "_id";

        private static final String M_TAG = "tag";
        private static final String M_WORD = "word";
        private static final String CM_CATEGORY = "category";
        private static final String CM_TAG = "tag";

        private Context context;

        public DBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            this.context = context;
        }
    }

    public long insertWord(Word word){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        /*
        insert into word (w_string, w_imgpath)
        select 'bibig', 'path'
        where not exists(select 1 from word where w_string = 'bibig');
        */

        contentValues.put(dbHelper.W_STRING, word.getString());
        contentValues.put(dbHelper.W_IMGPATH, word.getImgpath());

        long checkWord = db.insert(dbHelper.WORD_TABLE, null, contentValues);

        if(checkWord < 0){
            // add a toast
            Toast.makeText(dbHelper.context, "Adding word " + word.getString() + " unsuccessful.", Toast.LENGTH_LONG).show();
            return -1;
        }else{
            Toast.makeText(dbHelper.context, "Adding word " + word.getString() + " successful!", Toast.LENGTH_LONG).show();
        }

        // db.close();

        return checkWord;
    }

    public long insertCategory(Word word){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        /*
        insert into word (w_string, w_imgpath)
        select 'bibig', 'path'
        where not exists(select 1 from word where w_string = 'bibig');
        */

        contentValues.put(dbHelper.C_STRING, word.getString());
        contentValues.put(dbHelper.C_IMGPATH, word.getImgpath());

        long checkWord = db.insert(dbHelper.CATEGORY_TABLE, null, contentValues);

        if(checkWord < 0){
            // add a toast
            Toast.makeText(dbHelper.context, "Adding category " + word.getString() + " unsuccessful.", Toast.LENGTH_LONG).show();
            return -1;
        }else{
            Toast.makeText(dbHelper.context, "Adding category " + word.getString() + " successful!", Toast.LENGTH_LONG).show();
        }

        // db.close();

        return checkWord;
    }

    public boolean exists(Word word, int which){ // true if the word exists (it's in the database), false if not
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "";
        if(which == 0){
            query = "SELECT count(*) from " + dbHelper.WORD_TABLE +
                    " where " + dbHelper.W_STRING + "=? LIMIT 1";
        }else{
            query = "SELECT count(*) from " + dbHelper.CATEGORY_TABLE +
                    " where " + dbHelper.C_STRING + "=? LIMIT 1";
        }
        boolean check = DatabaseUtils.longForQuery(db, query, new String[] {new Integer(word.get_id()).toString()}) > 0;
        // db.close();
        return check;
    }

    public boolean exists(String tag){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT count(*) from " + dbHelper.TAG_TABLE +
                " where " + dbHelper.T_STRING + "=? LIMIT 1";
        boolean check = DatabaseUtils.longForQuery(db, query, new String[] {tag}) > 0;
        // db.close();
        return check;
    }

    public boolean exists(String tag, SQLiteDatabase db){
        String query = "SELECT count(*) from " + dbHelper.TAG_TABLE +
                " where " + dbHelper.T_STRING + "=? LIMIT 1";
        boolean check = DatabaseUtils.longForQuery(db, query, new String[] {tag}) > 0;
        // db.close();
        return check;
    }

    public int getWordID(String w_string, int which){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "";

        if(which == 0){
            query = "SELECT " + dbHelper.W_ID + " from " + dbHelper.WORD_TABLE +
                    " WHERE " + dbHelper.W_STRING + "='" + w_string + "' LIMIT 1";
        }else{
            query = "SELECT " + dbHelper.C_ID + " from " + dbHelper.CATEGORY_TABLE +
                    " WHERE " + dbHelper.C_STRING + "='" + w_string + "' LIMIT 1";
        }
        Cursor cursor = db.rawQuery(query, null);

        if(cursor != null){
            cursor.moveToFirst();
        }

        int id = cursor.getInt(0);
        cursor.close();
        // db.close();
        return id;
    }

    public int getTagID(String t_string){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT " + dbHelper.T_ID + " from " + dbHelper.TAG_TABLE +
                " WHERE " + dbHelper.T_STRING + "='" + t_string + "' LIMIT 1";
        Cursor cursor = db.rawQuery(query, null);

        if(cursor != null){
            cursor.moveToFirst();
        }

        int id = cursor.getInt(0);
        cursor.close();
        // db.close();
        return id;
    }

    public int getTagID(String t_string, SQLiteDatabase db){
        String query = "SELECT " + dbHelper.T_ID + " from " + dbHelper.TAG_TABLE +
                " WHERE " + dbHelper.T_STRING + "='" + t_string + "' LIMIT 1";
        Cursor cursor = db.rawQuery(query, null);

        if(cursor != null){
            cursor.moveToFirst();
        }

        int id = cursor.getInt(0);
        cursor.close();
        // db.close();
        return id;
    }

    public ArrayList<Tag> insertTags2Word(String[] tags, int w_id){
        ArrayList<Tag> tag = new ArrayList<Tag>();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // db.beginTransactionNonExclusive();
        ContentValues contentValues = new ContentValues();


            for(int i = 0; i < tags.length; i++){
                Tag t = new Tag(tags[i]);
                if(exists(tags[i])){ // if tag is not in tags table, insert into table
                    contentValues.put(dbHelper.T_STRING, tags[i]);
                    db.insert(dbHelper.TAG_TABLE, null, contentValues);
                    contentValues.clear();
                }

                t.set_id(getTagID(tags[i]));

                // map tag and word
                contentValues.put(dbHelper.M_WORD, w_id);
                contentValues.put(dbHelper.M_TAG, t.get_id());
                db.insert(dbHelper.MAP_TABLE, null, contentValues);
                contentValues.clear();
                tag.add(t);
            }


        //db.close();

        return tag;
    }

    public ArrayList<Tag> insertTags2Category(String[] tags, int c_id){
        ArrayList<Tag> tag = new ArrayList<Tag>();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // db.beginTransactionNonExclusive();
        ContentValues contentValues = new ContentValues();


        for(int i = 0; i < tags.length; i++){
            Tag t = new Tag(tags[i]);
            if(exists(tags[i])){ // if tag is not in tags table, insert into table
                contentValues.put(dbHelper.T_STRING, tags[i]);
                db.insert(dbHelper.TAG_TABLE, null, contentValues);
                contentValues.clear();
            }

            t.set_id(getTagID(tags[i]));

            // map tag and word
            contentValues.put(dbHelper.CM_CATEGORY, c_id);
            contentValues.put(dbHelper.CM_TAG, t.get_id());
            db.insert(dbHelper.C_MAP_TABLE, null, contentValues);
            contentValues.clear();
            tag.add(t);
        }


        //db.close();

        return tag;
    }

    public ArrayList<Word> getCategoryList(){
        ArrayList<Word> output = new ArrayList<Word>();
        String query = "SELECT " + dbHelper.C_ID + ", " + dbHelper.C_STRING + ", " + dbHelper.C_IMGPATH  +
                " from " + dbHelper.CATEGORY_TABLE;
        String query_t = "SELECT t." + dbHelper.T_ID + ", t." + dbHelper.T_STRING + " FROM " + dbHelper.TAG_TABLE + " t INNER JOIN "
                + dbHelper.C_MAP_TABLE + " m ON t." + dbHelper.T_ID + "=m." + dbHelper.CM_TAG +
                " WHERE m." + dbHelper.CM_CATEGORY + "=?";

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if(cursor != null){
            while(cursor.moveToNext()){
                int id = cursor.getInt(0);
                String string = cursor.getString(1);
                String path = cursor.getString(2);
                ArrayList<Tag> tags = new ArrayList<Tag>();

                Cursor cursor_t = db.rawQuery(query_t, new String[]{new Integer(id).toString()});

                while(cursor_t.moveToNext()){
                    Tag tag = new Tag(cursor_t.getString(1));
                    tag.set_id(cursor_t.getInt(0));

                    tags.add(tag);
                }

                cursor_t.close();

                Word category = new Word(id, string, path, tags);
                output.add(category);
            }
        }

        //db.close();
        cursor.close();
        return output;
    }

    public ArrayList<Word> getWordData(Word category){
        ArrayList<Word> output = new ArrayList<Word>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String join = "SELECT DISTINCT w." + dbHelper.W_ID + ", w." + dbHelper.W_STRING + ", w." + dbHelper.W_IMGPATH +
                " FROM " + dbHelper.WORD_TABLE + " w INNER JOIN " + dbHelper.MAP_TABLE + " m ON w." + dbHelper.W_ID +
                "=m." + dbHelper.M_WORD + " INNER JOIN " + dbHelper.TAG_TABLE + " t ON m." + dbHelper.M_TAG + "=t." + dbHelper.T_ID;
        String where = "";
        String tags = " OR t." + dbHelper.T_STRING + "='";
        String query = "";
        String query_t = "SELECT t." + dbHelper.T_ID + ", t." + dbHelper.T_STRING + " FROM " + dbHelper.TAG_TABLE + " t INNER JOIN "
                + dbHelper.MAP_TABLE + " m ON t." + dbHelper.T_ID + "=m." + dbHelper.M_TAG +
                " WHERE m." + dbHelper.M_WORD + "=?";

        if(category.getTags().size() >= 1){
            where = " WHERE t." + dbHelper.T_STRING + "='" + category.getTags().get(0).getString() + "'";
            for(int i = 1; i < category.getTags().size(); i++){
                where += tags + category.getTags().get(i).getString() + "'";
            }
        }

        query = join + " " + where;

        Cursor cursor = db.rawQuery(query, null);

        if(cursor != null){
            while(cursor.moveToNext()){
                int id = cursor.getInt(0);
                String string = cursor.getString(1);
                String path = cursor.getString(2);
                ArrayList<Tag> tagList = new ArrayList<Tag>();

                // set tags!
                Cursor cursor_t = db.rawQuery(query_t, new String[]{new Integer(id).toString()});

                while(cursor_t.moveToNext()){
                    Tag tag = new Tag(cursor_t.getString(1));
                    tag.set_id(cursor_t.getInt(0));

                    tagList.add(tag);
                }

                cursor_t.close();

                Word word = new Word(id, string, path, tagList);

                output.add(word);
            }
        }


        //db.close();
        cursor.close();

        return output;
    }


    public long deleteWord(Word word){
        long check = -1;
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        for(int i = 0; i < word.getTags().size(); i++){
            db.delete(dbHelper.MAP_TABLE, dbHelper.M_TAG + "=" + word.getTags().get(i).get_id()
                    + " AND " + dbHelper.M_WORD + "=" + word.get_id(), null);

            String query = "SELECT count(*) from " + dbHelper.MAP_TABLE +
                    " where " + dbHelper.M_TAG + "=? LIMIT 1";
            boolean check_t = DatabaseUtils.longForQuery(db, query, new String[]{new Integer(word.getTags().get(i).get_id()).toString()}) > 0;
            if(!check_t){
                db.delete(dbHelper.TAG_TABLE, dbHelper.T_ID + "=" + word.getTags().get(i).get_id(), null);
            }
        }

        check = db.delete(dbHelper.WORD_TABLE, dbHelper.W_ID + "=" + word.get_id(), null);

        return check;
    }

    public long deleteCategory(Word word){
        long check = -1;
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        for(int i = 0; i < word.getTags().size(); i++){
            db.delete(dbHelper.C_MAP_TABLE, dbHelper.CM_TAG + "=" + word.getTags().get(i).get_id()
                    + " AND " + dbHelper.CM_CATEGORY + "=" + word.get_id(), null);

            if(!exists(word.getTags().get(i).getString(), db)){
                db.delete(dbHelper.TAG_TABLE, dbHelper.T_ID + "=" + word.getTags().get(i).get_id(), null);
            }
        }

        check = db.delete(dbHelper.CATEGORY_TABLE, dbHelper.C_ID + "=" + word.get_id(), null);

        return check;
    }

    public long editWord(Word word){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(dbHelper.W_STRING, word.getString());
        cv.put(dbHelper.W_IMGPATH, word.getImgpath());

        return db.update(dbHelper.WORD_TABLE, cv, dbHelper.W_ID + "=" + word.get_id(), null);
    }

    public long editCategory(Word word){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(dbHelper.C_STRING, word.getString());
        cv.put(dbHelper.C_IMGPATH, word.getImgpath());

        return db.update(dbHelper.CATEGORY_TABLE, cv, dbHelper.C_ID + "=" + word.get_id(), null);
    }

    public void insertTag2Word(String[] oldTags, String[] newTags, int w_id){
        if(!Arrays.equals(oldTags, newTags)) {
            // update tags
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues cv = new ContentValues();

            Set<String> newSet =  new HashSet<String>(Arrays.asList(newTags));
            Set<String> oldSet = new HashSet<String>(Arrays.asList(oldTags));

            for(int i = 0; i < newTags.length; i++){
                if(!oldSet.contains(newTags[i])){
                    // insert newTags[counter] in word
                    if(!exists(newTags[i], db)){
                        cv.put(dbHelper.T_STRING, newTags[i]);
                        db.insert(dbHelper.TAG_TABLE, null, cv);
                        cv.clear();
                    }

                    int id = getTagID(newTags[i], db);

                    cv.put(dbHelper.M_TAG, id);
                    cv.put(dbHelper.M_WORD, w_id);

                    db.insert(dbHelper.MAP_TABLE, null, cv);

                    cv.clear();
                }
            }

            for(int i = 0; i < oldTags.length; i++){
                if(!newSet.contains(oldTags[i])){
                    // delete oldTags[i]
                    int id = getTagID(oldTags[i], db);
                    db.delete(dbHelper.MAP_TABLE, dbHelper.M_TAG + "=" + id + " AND " + dbHelper.M_WORD + "=" + w_id, null);

                    if(!exists(oldTags[i], db)){
                        db.delete(dbHelper.TAG_TABLE, dbHelper.T_ID + "=" + id, null);
                    }
                }
            }

        }
    }

    public void insertTag2Category(String[] oldTags, String[] newTags, int w_id){
        if(!Arrays.equals(oldTags, newTags)) {
            // update tags
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues cv = new ContentValues();

            Set<String> newSet =  new HashSet<String>(Arrays.asList(newTags));
            Set<String> oldSet = new HashSet<String>(Arrays.asList(oldTags));

            for(int i = 0; i < newTags.length; i++){
                if(!oldSet.contains(newTags[i])){
                    // insert newTags[counter] in word
                    if(!exists(newTags[i], db)){
                        cv.put(dbHelper.T_STRING, newTags[i]);
                        db.insert(dbHelper.TAG_TABLE, null, cv);
                        cv.clear();
                    }

                    int id = getTagID(newTags[i], db);

                    cv.put(dbHelper.CM_TAG, id);
                    cv.put(dbHelper.CM_CATEGORY, w_id);

                    db.insert(dbHelper.C_MAP_TABLE, null, cv);

                    cv.clear();
                }
            }

            for(int i = 0; i < oldTags.length; i++){
                if(!newSet.contains(oldTags[i])){
                    // delete oldTags[i]
                    int id = getTagID(oldTags[i], db);
                    db.delete(dbHelper.C_MAP_TABLE, dbHelper.CM_TAG + "=" + id + " AND " + dbHelper.CM_CATEGORY + "=" + w_id, null);

                    if(!exists(oldTags[i], db)){
                        db.delete(dbHelper.TAG_TABLE, dbHelper.T_ID + "=" + id, null);
                    }
                }
            }

        }
    }

    public void close(){
        dbHelper.close();
    }
}
