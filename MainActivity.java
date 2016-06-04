package com.aac.wsg.alyssa;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private DBAdapter dbHelper;
    private Message message;

    private ArrayList<Word> wordArrayList;

    private GridView buttonSelection;
    private CustomGridAdapter gridAdapter;

    private Button addWord;
    private Button addCategory;
    private Button clear;
    private Button back;
    private Button play;

    private HorizontalScrollView messageView;
    private LinearLayout messageLayout;

    private boolean levelOne = true;

    private final int PICK_IMAGE = 100;

    private String selectedImagePath;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        selectedImagePath = new String();

        setContentView(R.layout.activity_main);

        dbHelper = new DBAdapter(this);

        // populate wordArrayList and categoryArrayList
        wordArrayList = dbHelper.getCategoryList();

        buttonSelection = (GridView) findViewById(R.id.buttonSelection);
        messageView = (HorizontalScrollView) findViewById(R.id.message);
        messageLayout = (LinearLayout) findViewById(R.id.messageLayout);
        back = (Button) findViewById(R.id.back);
        addWord = (Button) findViewById(R.id.newWord);
        clear = (Button) findViewById(R.id.clear);
        // play = (Button) findViewById(R.id.play);
        addCategory = (Button) findViewById(R.id.newCategory);

        gridAdapter = new CustomGridAdapter(wordArrayList, this);

        buttonSelection.setAdapter(gridAdapter);
        buttonSelection.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,int position, long id) { // tap and addWord to message
                 if(!levelOne){  // if it's not level one, add word to message
                    addWordToMessage(wordArrayList.get(position));
                }else{ // else go to next level (categories --> words)
                    retrieveWords(position);
                }

            }
        });



        buttonSelection.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if(levelOne){
                    editCategory(wordArrayList.get(position));
                }else{
                    editWord(wordArrayList.get(position));
                }
                return false;
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // do something when the button is clicked
                goBackToCategories();
            }
        });

        /*play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // playMessage();
            }
        });*/

        addWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addWord();
            }
        });

        addCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCategory();
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearWordInMessage();
            }
        });

        back.setVisibility(View.INVISIBLE);
    }

    private void addCategory(){
        // pop up window to input things
        LayoutInflater inflater = LayoutInflater.from(this);
        View promptView = inflater.inflate(R.layout.edit_word, null);

        final Word category = new Word();

        final ImageView promptImage = (ImageView) promptView.findViewById(R.id.imagePromptView);
        final EditText editWord = (EditText) promptView.findViewById(R.id.editWord);
        final EditText editTags = (EditText) promptView.findViewById(R.id.editTags);
        final Button newImage = (Button) promptView.findViewById(R.id.btnGallery);
        final Button delete = (Button) promptView.findViewById(R.id.delete);
        final TextView textString = (TextView) promptView.findViewById(R.id.textEnterString);

        textString.setText("Enter category");

        delete.setVisibility(View.INVISIBLE);

        Picasso.with(this).load(R.drawable.path).into(promptImage);

        newImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
                // Picasso.with(context).load(intent.getData()).placeholder(R.drawable.path).into(promptImage);
            }
        });

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptView)
                .setTitle("New Category")
                .setMessage("Add new category.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        category.setString(editWord.getText().toString());
                        if(dbHelper.exists(category, 1) || editWord.getText().toString().isEmpty() || editTags.getText().toString().isEmpty()){
                            // toast that it exists
                            Toast.makeText(context, "Category could not be added\n" +
                                    "It either exists or you have left some fields blank.", Toast.LENGTH_LONG).show();
                        }else{
                            category.setImgpath(selectedImagePath);

                            String[] tags = editTags.getText().toString().replaceAll("\\s+","").split(",");

                            long id = dbHelper.insertCategory(category);

                            if(id > 0){
                                category.set_id(dbHelper.getWordID(category.getString(), 1));

                                // insert tags
                                category.setTags(dbHelper.insertTags2Category(tags, category.get_id()));
                            }

                            goBackToCategories();
                        }
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        })
                .show();
    }

    private void addWord(){
        // pop up window to input things
        LayoutInflater inflater = LayoutInflater.from(this);
        View promptView = inflater.inflate(R.layout.edit_word, null);

        final Word word = new Word();

        final ImageView promptImage = (ImageView) promptView.findViewById(R.id.imagePromptView);
        final EditText editWord = (EditText) promptView.findViewById(R.id.editWord);
        final EditText editTags = (EditText) promptView.findViewById(R.id.editTags);
        final Button newImage = (Button) promptView.findViewById(R.id.btnGallery);
        final Button delete = (Button) promptView.findViewById(R.id.delete);

        delete.setVisibility(View.INVISIBLE);

        Picasso.with(this).load(R.drawable.path).into(promptImage);

        newImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
                // Picasso.with(context).load(intent.getData()).placeholder(R.drawable.path).into(promptImage);
            }
        });

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptView)
                .setTitle("New Word")
                .setMessage("Add new word in the vocabulary.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        word.setString(editWord.getText().toString());
                        if(dbHelper.exists(word, 0) || editWord.getText().toString().isEmpty() || editTags.getText().toString().isEmpty()){
                            // toast that it exists
                            Toast.makeText(context, "Word could not be added\n" +
                                    "It either exists or you have left some fields blank.", Toast.LENGTH_LONG).show();
                        }else{
                            word.setImgpath(selectedImagePath);

                            String[] tags = editTags.getText().toString().replaceAll("\\s+","").split(",");

                            long id = dbHelper.insertWord(word);

                            if(id > 0){
                                word.set_id(dbHelper.getWordID(word.getString(), 0));

                                // insert tags
                                word.setTags(dbHelper.insertTags2Word(tags, word.get_id()));
                            }

                            goBackToCategories();
                        }
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        })
                .show();

    }

    private void editCategory(final Word word){
        // pop up window to input things
        LayoutInflater inflater = LayoutInflater.from(this);
        View promptView = inflater.inflate(R.layout.edit_word, null);

        selectedImagePath = word.getImgpath();

        final ImageView promptImage = (ImageView) promptView.findViewById(R.id.imagePromptView);
        final EditText editWord = (EditText) promptView.findViewById(R.id.editWord);
        final EditText editTags = (EditText) promptView.findViewById(R.id.editTags);
        final Button newImage = (Button) promptView.findViewById(R.id.btnGallery);
        final Button delete = (Button) promptView.findViewById(R.id.delete);

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        final Word oldWord = word;

        String tags = "";
        final String[] oldTags = new String[word.getTags().size()];
        int count = 0;
        while(count < word.getTags().size() - 1){
            oldTags[count] = word.getTags().get(count).getString();
            tags += oldTags[count] + ", ";
            ++count;
        }
        oldTags[count] = word.getTags().get(count).getString();
        tags += oldTags[count];
        Arrays.sort(oldTags);

        editTags.setText(tags);

        Picasso.with(this).load(word.getImgpath()).placeholder(R.drawable.path).into(promptImage);
        editWord.setText(word.getString());

        newImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);

                if(selectedImagePath.isEmpty()){
                    Picasso.with(context).load(R.drawable.path).into(promptImage);
                }else{
                    Picasso.with(context).load(selectedImagePath).placeholder(R.drawable.path).into(promptImage);
                }

            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHelper.deleteCategory(word);
            }
        });

        alertDialogBuilder.setView(promptView)
                .setTitle("Edit Word")
                .setMessage("Edit the fields.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        word.setImgpath(selectedImagePath);
                        word.setString(editWord.getText().toString());

                        String[] tags = editTags.getText().toString().replaceAll("\\s+","").split(",");
                        Arrays.sort(tags);

                        long id = dbHelper.editCategory(word);

                        if(id <= 0){
                            // insert word
                            id = dbHelper.insertCategory(word);
                            word.set_id(dbHelper.getWordID(word.getString(), 1));
                        }

                        if(id > 0){
                            // edit tags
                            dbHelper.insertTag2Category(oldTags, tags, word.get_id());
                        }

                        goBackToCategories();
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        })
                .show();
    }

    private void editWord(final Word word){
        // pop up window to input things
        LayoutInflater inflater = LayoutInflater.from(this);
        View promptView = inflater.inflate(R.layout.edit_word, null);

        selectedImagePath = word.getImgpath();

        final ImageView promptImage = (ImageView) promptView.findViewById(R.id.imagePromptView);
        final EditText editWord = (EditText) promptView.findViewById(R.id.editWord);
        final EditText editTags = (EditText) promptView.findViewById(R.id.editTags);
        final Button newImage = (Button) promptView.findViewById(R.id.btnGallery);
        final Button delete = (Button) promptView.findViewById(R.id.delete);

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        String tags = "";
        final String[] oldTags = new String[word.getTags().size()];
        int count = 0;
        while(count < word.getTags().size() - 1){
            oldTags[count] = word.getTags().get(count).getString();
            tags += oldTags[count] + ", ";
            ++count;
        }
        oldTags[count] = word.getTags().get(count).getString();
        tags += oldTags[count];
        Arrays.sort(oldTags);

        editTags.setText(tags);

        Picasso.with(this).load(word.getImgpath()).placeholder(R.drawable.path).into(promptImage);
        editWord.setText(word.getString());

        newImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);

                if(selectedImagePath.isEmpty()){
                    Picasso.with(context).load(R.drawable.path).into(promptImage);
                }else{
                    Picasso.with(context).load(selectedImagePath).placeholder(R.drawable.path).into(promptImage);
                }

            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHelper.deleteWord(word);
            }
        });

        alertDialogBuilder.setView(promptView)
                .setTitle("Edit Word")
                .setMessage("Edit the fields.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        word.setImgpath(selectedImagePath);
                        word.setString(editWord.getText().toString());

                        String[] tags = editTags.getText().toString().replaceAll("\\s+","").split(",");
                        Arrays.sort(tags);

                        long id = dbHelper.editWord(word);

                        if(id <= 0){
                            // insert word
                            id = dbHelper.insertWord(word);
                            word.set_id(dbHelper.getWordID(word.getString(), 0));
                        }

                        if(id > 0){
                            // edit tags
                            dbHelper.insertTag2Word(oldTags, tags, word.get_id());
                        }

                        goBackToCategories();
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        })
                .show();
    }

    private void addWordToMessage(Word word){
        LayoutInflater layoutInflater =
                (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View addView = layoutInflater.inflate(R.layout.wic_layout, null);
        TextView text = (TextView) addView.findViewById(R.id.itemString);
        ImageView pic = (ImageView) addView.findViewById(R.id.itemView);

        text.setText(word.getString());
        Picasso.with(this).load(word.getImgpath()).placeholder(R.drawable.path).into(pic);

        addView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((LinearLayout)addView.getParent()).removeView(addView);
            }
        });

        messageLayout.addView(addView, messageLayout.getChildCount() - 1);

    }

    private void retrieveWords(int position){
        back.setVisibility(View.VISIBLE);
        Word category = wordArrayList.get(position);

        wordArrayList.clear();

        wordArrayList = dbHelper.getWordData(category);

        gridAdapter.setWordList(wordArrayList);

        levelOne = false;
    }

    private void goBackToCategories(){
        back.setVisibility(View.INVISIBLE);

        wordArrayList.clear();

        wordArrayList = dbHelper.getCategoryList();
        gridAdapter.setWordList(wordArrayList);

        levelOne = true;
    }

    private void clearWordInMessage(){
        messageLayout.removeAllViews();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE) {
                Uri selectedImageUri = data.getData();
                selectedImagePath = getRealPathFromURI(selectedImageUri);
            }
        }
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }
}
