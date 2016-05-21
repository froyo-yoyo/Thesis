package com.aac.wsg.alyssa;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by ASUS on 5/14/2016.
 */
public class CustomGridAdapter extends BaseAdapter {
    private ArrayList<Word> wordList;
    private Context context;
    private static LayoutInflater inflater;

    public CustomGridAdapter(ArrayList<Word> mainList, Context context) {
        this.wordList = mainList;
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {

        return wordList.size();
    }

    @Override
    public Object getItem(int position) {

        return wordList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null){
            convertView = inflater.inflate(R.layout.wic_layout, null);

            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        Word word = wordList.get(position);

        if(word.getImgpath().startsWith("file:///android_asset/")){
            Picasso.with(context).load(word.getImgpath()).placeholder(R.drawable.path).into(holder.pic);
        }else{
            Picasso.with(context).load(new File(word.getImgpath())).placeholder(R.drawable.path).into(holder.pic);
        }

        holder.word.setText(word.getString());

        return convertView;
    }

    public void addItem(Word word){
        wordList.add(word);
        notifyDataSetChanged();
    }

    public void removeItem(int position){
        wordList.remove(position);
        notifyDataSetChanged();
    }

    public void setWordList(ArrayList<Word> newList){
        wordList.clear();
        wordList = newList;
        notifyDataSetChanged();
    }

    private class ViewHolder{
        ImageView pic;
        TextView word;

        ViewHolder(View v){
            pic = (ImageView) v.findViewById(R.id.itemView);
            word = (TextView) v.findViewById(R.id.itemString);
        }
    }
}
