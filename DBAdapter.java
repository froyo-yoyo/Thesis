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

/**
 * Created by ASUS on 4/19/2016.
 */
public class DBAdapter {
    DBHelper dbHelper;

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

        private static final String C_QUERY = "c_query";

        private static final String W_IMGPATH = "w_imgpath";
        private static final String C_IMGPATH = "c_imgpath";

        private static final String W_ID = "_id";
        private static final String T_ID = "_id";
        private static final String C_ID = "id";

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

    public int insertWord(Word word){
        int id = -1;

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
            Toast.makeText(dbHelper.context, "Adding word " + word.getString() + " unsuccessful", Toast.LENGTH_LONG).show();
            return -1;
        }

        // return word's id
        String query = "SELECT " + dbHelper.W_ID + " from " + dbHelper.WORD_TABLE +
                " where " + dbHelper.W_STRING + "='?' LIMIT 1";

        Cursor cursor = db.rawQuery(query, new String[]{word.getString()});

        id = cursor.getInt(0);

        return id;
    }

    public int insertCategory(Word category){
        int id = -1;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        /*
        insert into word (w_string, w_imgpath)
        select 'bibig', 'path'
        where not exists(select 1 from word where w_string = 'bibig');
        */

        contentValues.put(dbHelper.C_STRING, category.getString());
        contentValues.put(dbHelper.C_IMGPATH, category.getImgpath());

        long checkWord = db.insert(dbHelper.WORD_TABLE, null, contentValues);

        if(checkWord < 0){
            // add a toast
            Toast.makeText(dbHelper.context, "Adding word " + category.getString() + " unsuccessful", Toast.LENGTH_LONG).show();
            return -1;
        }

        // return word's id
        String query = "SELECT " + dbHelper.C_ID + " from " + dbHelper.CATEGORY_TABLE +
                " where " + dbHelper.C_STRING + "='?' LIMIT 1";

        Cursor cursor = db.rawQuery(query, new String[]{category.getString()});

        id = cursor.getInt(0);
        return id;
    }

    public int insertNewTag(Tag tag){ // return tag's primary key
        /*
        insert into tag (t_string)
        select 'katawan'
        where not exists(select 1 from tag where t_string = 'katawan');
        */
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(dbHelper.T_STRING, tag.getString());

        long checkTag = db.insert(dbHelper.TAG_TABLE, null, contentValues);

        if(checkTag < 0){
            Toast.makeText(dbHelper.context, "Adding tag " + tag.getString() + " unsuccessful", Toast.LENGTH_LONG).show();
            return -1;
        }

        // return tag's id
        String query = "SELECT " + dbHelper.T_ID + " from " + dbHelper.TAG_TABLE +
                " where " + dbHelper.T_STRING + "='?' LIMIT 1";

        Cursor cursor = db.rawQuery(query, new String[]{tag.getString()});

        int id = cursor.getInt(0);
        return id;
    }

    public int getTagID(Tag tag){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT " + dbHelper.T_ID + " from " + dbHelper.TAG_TABLE +
                " where " + dbHelper.T_STRING + "='?' LIMIT 1";

        Cursor cursor = db.rawQuery(query, new String[]{tag.getString()});

        int id = cursor.getInt(0);
        return id;
    }

    public void addTag2Word(Tag tag, Word word){ // return true if successful and false if not
        /*
        insert into map(word, tag) values
        ((select id from word where w_string = 'aklat'), (select id from tag where t_string = 'oras'));
        */

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String query = "SELECT count(*) from " + dbHelper.MAP_TABLE +
                " where " + dbHelper.M_TAG + "=? AND " + dbHelper.M_WORD + "=? LIMIT 1";

        if(DatabaseUtils.longForQuery(db, query,
                new String[] {new Integer(tag.get_id()).toString(), new Integer(word.get_id()).toString()}) <= 0){
            ContentValues contentValues = new ContentValues();

            contentValues.put(dbHelper.M_TAG, tag.get_id());
            contentValues.put(dbHelper.M_WORD, word.get_id());

            db.insert(dbHelper.MAP_TABLE, null, contentValues);

        }
    }

    public boolean addTag2Category(Tag tag, Word word){ // return true if successful and false if not
        /*
        insert into map(word, tag) values
        ((select id from word where w_string = 'aklat'), (select id from tag where t_string = 'oras'));
        */

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(dbHelper.CM_TAG, tag.get_id());
        contentValues.put(dbHelper.CM_CATEGORY, word.get_id());

        return db.insert(dbHelper.C_MAP_TABLE, null, contentValues) > 0;
    }

    public void deleteWord(Word word){
        /*
        delete from word where w_string = 'word';
        * */

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String where = dbHelper.W_STRING + "='?'";
        String where_m = dbHelper.M_WORD + "=? AND " + dbHelper.M_TAG + "=?";

        db.delete(dbHelper.WORD_TABLE,
                where,
                new String[]{word.getString()});

        // delete all maps
        if(!word.getTags().isEmpty()){
            for(int i = 0; i < word.getTags().size(); i++){
                String where_t = dbHelper.T_ID + "=?";
                int tmp = word.getTags().get(i).get_id();

                db.delete(dbHelper.MAP_TABLE, where_m,
                        new String[]{new Integer(word.get_id()).toString(),
                                new Integer(tmp).toString()});

                // check for orphan tags
                if(!exists(word.getTags().get(i))){
                    db.delete(dbHelper.TAG_TABLE, where_t,
                            new String[]{new Integer(tmp).toString()});
                }
            }
        }

    }

    public void deleteCategory(Word category){
        /*
        * delete from category where c_string = 'category';
        * */

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String where = dbHelper.C_STRING + "='?'";
        String where_m = dbHelper.CM_CATEGORY + "=? AND " + dbHelper.CM_TAG + "=?";

        db.delete(dbHelper.CATEGORY_TABLE,
                where,
                new String[]{category.getString()});

        // delete all maps
        for(int i = 0; i < category.getTags().size(); i++) {
            db.delete(dbHelper.C_MAP_TABLE, where_m,
                    new String[]{new Integer(category.get_id()).toString(),
                            new Integer(category.getTags().get(i).get_id()).toString()});
        }
    }

    public void deleteTagsFromWord(Word word){
        /*
        delete from map where tag = (select id from tag where t_string = 'tag') and
        word = (select id from word where w_string = 'word');
        delete from tag where t_string = 'tag'
        and not exists (select 1 from map m
        inner join tag t on m.tag = t.id
        where t.t_string = 'tag');
        */

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String where = dbHelper.M_TAG + "=? AND " + dbHelper.M_WORD + "=?";

        for(int i = 0; i < word.getTags().size(); i++){
            Tag tag = word.getTags().get(i);
            // delete map
            db.delete(dbHelper.MAP_TABLE,
                    where,
                    new String[]{new Integer(tag.get_id()).toString(), new Integer(word.get_id()).toString()});

            // check if tag is now an orphan
            if(!exists(tag)){
                // if so then delete tag

                String where_t = dbHelper.T_ID + "=?";
                db.delete(dbHelper.TAG_TABLE, where_t,
                        new String[]{new Integer(tag.get_id()).toString()});
            }
        }
    }

    public void deleteTagsFromCategory(Word word){
        /*
        delete from map where tag = (select id from tag where t_string = 'tag') and
        word = (select id from word where w_string = 'word');
        delete from tag where t_string = 'tag'
        and not exists (select 1 from map m
        inner join tag t on m.tag = t.id
        where t.t_string = 'tag');
        */

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String where = dbHelper.CM_TAG + "=? AND " + dbHelper.CM_CATEGORY + "=?";

        for(int i = 0; i < word.getTags().size(); i++){
            Tag tag = word.getTags().get(i);
            // delete map
            db.delete(dbHelper.C_MAP_TABLE,
                    where,
                    new String[]{new Integer(tag.get_id()).toString(), new Integer(word.get_id()).toString()});

            // check if tag is now an orphan
            if(!exists(tag)){
                // if so then delete tag

                String where_t = dbHelper.T_ID + "=?";
                db.delete(dbHelper.TAG_TABLE, where_t,
                        new String[]{new Integer(tag.get_id()).toString()});
            }
        }
    }

    public void editWord(Word newWord){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(dbHelper.W_IMGPATH, newWord.getImgpath());
        cv.put(dbHelper.W_STRING, newWord.getString());

        db.update(dbHelper.WORD_TABLE, cv, "_id=?", new String[]{new Integer(newWord.get_id()).toString()});
    }

    public void editCategory(Word newCategory){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(dbHelper.C_IMGPATH, newCategory.getImgpath());
        cv.put(dbHelper.C_STRING, newCategory.getString());

        db.update(dbHelper.CATEGORY_TABLE, cv, "_id=?", new String[]{new Integer(newCategory.get_id()).toString()});
    }

    public boolean exists(Tag tag){ // true if the tag exists (there are words still using it), false if not
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT count(*) from " + dbHelper.MAP_TABLE +
                " where " + dbHelper.M_TAG + "=? LIMIT 1";
        return DatabaseUtils.longForQuery(db, query, new String[] {new Integer(tag.get_id()).toString()}) > 0;
    }

    public boolean exists(Word word){ // true if the word exists (it's in the database), false if not
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT count(*) from " + dbHelper.WORD_TABLE +
                " where " + dbHelper.W_STRING + "=? LIMIT 1";
        return DatabaseUtils.longForQuery(db, query, new String[] {new Integer(word.get_id()).toString()}) > 0;
    }

    public ArrayList<Word> getWordData(Word category){
        ArrayList<Word> output = new ArrayList<Word>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(category.getQuery(), null);

        if(cursor != null){
            while(cursor.moveToNext()){
                int id = cursor.getInt(0);
                String string = cursor.getString(1);
                String path = cursor.getString(2);

                Word word = new Word(id, string, path);
                output.add(word);
            }
        }

        return output;
    }

    public ArrayList<Word> getCategoryList(){
        ArrayList<Word> output = new ArrayList<Word>();
        final String query = "SELECT " + dbHelper.C_STRING + ", " + dbHelper.C_IMGPATH + ", " + dbHelper.C_QUERY +
                " from " + dbHelper.CATEGORY_TABLE;

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if(cursor != null){
            while(cursor.moveToNext()){
                String string = cursor.getString(0);
                String path = cursor.getString(1);
                String q = cursor.getString(2);

                Word category = new Word(q, string, path);
                output.add(category);
            }
        }
        return output;
    }
}
