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

import java.util.ArrayList;

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
        play = (Button) findViewById(R.id.play);
        addCategory = (Button) findViewById(R.id.newCategory);

        gridAdapter = new CustomGridAdapter(wordArrayList, this);

        buttonSelection.setAdapter(gridAdapter);
        buttonSelection.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,int position, long id) { // tap and addWord to message
                 if(!levelOne){  // if it's not level one, add word to message
                    // addWordToMessage(wordArrayList.get(position));
                }else{ // else go to next level (categories --> words)
                    retrieveWords(position);
                }

            }
        });



        buttonSelection.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if(levelOne){
                    // editCategory(position);
                }else{
                    // editWord(position);
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

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // playMessage();
            }
        });

        addWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addWord();
            }
        });

        addCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // addCategory();
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // clearWordInMessage();
            }
        });

        back.setVisibility(View.INVISIBLE);
    }

    /*private void addCategory(){
        LayoutInflater inflater = LayoutInflater.from(this);
        View promptView = inflater.inflate(R.layout.edit_word, null);

        final Word word = new Word();

        final ImageView promptImage = (ImageView) promptView.findViewById(R.id.imagePromptView);
        final EditText editWord = (EditText) promptView.findViewById(R.id.editWord);
        final EditText editTags = (EditText) promptView.findViewById(R.id.editTags);
        Button newImage = (Button) promptView.findViewById(R.id.btnGallery);

        Picasso.with(this).load(R.drawable.path).into(promptImage);

        newImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image*//*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
                Picasso.with(context).load(intent.getData()).placeholder(R.drawable.path).into(promptImage);
            }
        });

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptView)
                .setTitle("New Category")
                .setMessage("Add new category.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(dbHelper.exists(word) && editWord.getText().toString().isEmpty()){
                            // toast that it exists
                            Toast.makeText(context, "Category could not be added\n" +
                                    "It either exists or you did not input any text for the name.", Toast.LENGTH_LONG).show();
                        }else{
                            word.setImgpath(selectedImagePath);
                            word.setString(editWord.getText().toString());

                            String[] tags = editTags.getText().toString().split(",");

                            int id = dbHelper.insertCategory(word);

                            if(id < 0){
                                // error message
                                dbHelper.deleteCategory(word);
                                Toast.makeText(context, "Category could not be added.", Toast.LENGTH_LONG).show();
                            }else{
                                word.set_id(id);

                                // insert tags
                                for(int i = 0; i < tags.length; i++){
                                    Tag tag = new Tag(tags[i]);
                                    if(!dbHelper.exists(tag)){
                                        int id_t = dbHelper.insertNewTag(tag);
                                        tag.set_id(id_t);
                                    }

                                    dbHelper.addTag2Category(tag, word);
                                }

                                wordArrayList.add(word);
                                gridAdapter.addItem(word);
                            }
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
    }*/

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
                Picasso.with(context).load(intent.getData()).placeholder(R.drawable.path).into(promptImage);
            }
        });

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptView)
                .setTitle("New Word")
                .setMessage("Add new word in the vocabulary.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(dbHelper.exists(word) || editWord.getText().toString().isEmpty() || editTags.getText().toString().isEmpty()){
                            // toast that it exists
                            Toast.makeText(context, "Word could not be added\n" +
                                    "It either exists or you have left some fields blank.", Toast.LENGTH_LONG).show();
                        }else{
                            word.setImgpath(selectedImagePath);
                            word.setString(editWord.getText().toString());

                            String[] tags = editTags.getText().toString().replaceAll("\\s+","").split(",");

                            long id = dbHelper.insertWord(word);

                            if(id > 0){
                                word.set_id(dbHelper.getWordID(word.getString()));

                                // insert tags
                                word.setTags(dbHelper.insertTags2Word(tags, word.get_id()));
                            }
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

    /*private void playMessage(){

    }

    private void addWordToMessage(Word word){
        TextView text = (TextView) findViewById(R.id.itemString);
        ImageView pic = (ImageView) findViewById(R.id.itemView);

    }

    private void clearWordInMessage(){

    }

    private void editWord(final int position){
        // choice of deleting or editing
        LayoutInflater inflater = LayoutInflater.from(this);
        View promptView = inflater.inflate(R.layout.edit_word, null);

        final Word word = wordArrayList.get(position);

        final ImageView promptImage = (ImageView) promptView.findViewById(R.id.imagePromptView);
        final EditText editWord = (EditText) promptView.findViewById(R.id.editWord);
        final EditText editTags = (EditText) promptView.findViewById(R.id.editTags);
        final Button newImage = (Button) promptView.findViewById(R.id.btnGallery);
        final Button delete = (Button) promptView.findViewById(R.id.delete);

        Picasso.with(this).load(R.drawable.path).into(promptImage);

        newImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image*//*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
                Picasso.with(context).load(intent.getData()).placeholder(R.drawable.path).into(promptImage);
            }
        });

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptView)
                .setTitle("New Word")
                .setMessage("Add new word in the vocabulary.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        delete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                deleteWord(position);
                                dbHelper.deleteTagsFromWord(word);

                                Toast.makeText(context, "Word has been deleted.", Toast.LENGTH_LONG).show();
                                dialog.cancel();
                            }
                        });
                        if(dbHelper.exists(word) && editWord.getText().toString().isEmpty()){
                            // toast that it exists
                            Toast.makeText(context, "Word could not be added\n" +
                                    "It either exists or you did not input any text for the name.", Toast.LENGTH_LONG).show();
                        }else{
                            wordArrayList.get(position).setImgpath(selectedImagePath);
                            wordArrayList.get(position).setString(editWord.getText().toString());
                            wordArrayList.get(position).getTags().clear();
                            dbHelper.deleteTagsFromWord(word);

                            String[] tags = editTags.getText().toString().split(",");

                            dbHelper.editWord(word);

                                // insert tags
                                for(int i = 0; i < tags.length; i++){
                                    Tag tag = new Tag(tags[i]);
                                    if(!dbHelper.exists(tag)){
                                        int id_t = dbHelper.insertNewTag(tag);
                                        tag.set_id(id_t);
                                    }else{
                                        tag.set_id(dbHelper.getTagID(tag));
                                    }

                                    wordArrayList.get(position).getTags().add(tag);

                                    dbHelper.addTag2Word(tag, word);
                                }

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

    private void editCategory(int n){
        // choice of deleting or editing
        LayoutInflater inflater = LayoutInflater.from(this);
        View promptView = inflater.inflate(R.layout.edit_word, null);
        final int position = n;
        final Word word = new Word();

        final ImageView promptImage = (ImageView) promptView.findViewById(R.id.imagePromptView);
        final EditText editWord = (EditText) promptView.findViewById(R.id.editWord);
        final EditText editTags = (EditText) promptView.findViewById(R.id.editTags);
        final Button newImage = (Button) promptView.findViewById(R.id.btnGallery);
        final Button delete = (Button) promptView.findViewById(R.id.delete);

        Picasso.with(this).load(R.drawable.path).into(promptImage);

        newImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image*//*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });


        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptView)
                .setTitle("New Word")
                .setMessage("Add new word in the vocabulary.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        delete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                deleteCategory(position);

                                Toast.makeText(context, "Category has been deleted.", Toast.LENGTH_LONG).show();
                                goBackToCategories();
                                dialog.cancel();
                            }
                        });
                        if(dbHelper.exists(word) && editWord.getText().toString().isEmpty()){
                            // toast that it exists
                            Toast.makeText(context, "Category could not be added\n" +
                                    "It either exists or you did not input any text for the name.", Toast.LENGTH_LONG).show();
                        }else{
                            wordArrayList.get(position).setImgpath(selectedImagePath);
                            wordArrayList.get(position).setString(editWord.getText().toString());
                            wordArrayList.get(position).getTags().clear();
                            dbHelper.deleteTagsFromCategory(word);

                            String[] tags = editTags.getText().toString().split(",");

                            dbHelper.editCategory(word);

                            // insert tags
                            for(int i = 0; i < tags.length; i++){
                                Tag tag = new Tag(tags[i]);
                                if(!dbHelper.exists(tag)){
                                    int id_t = dbHelper.insertNewTag(tag);
                                    tag.set_id(id_t);
                                }else{
                                    tag.set_id(dbHelper.getTagID(tag));
                                }

                                wordArrayList.get(position).getTags().add(tag);

                                dbHelper.addTag2Category(tag, word);
                            }

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

    private void deleteWord(int position){
        dbHelper.deleteTagsFromWord(wordArrayList.get(position));
        dbHelper.deleteWord(wordArrayList.get(position));
        wordArrayList.remove(position);
        gridAdapter.removeItem(position);
    }

    private void deleteCategory(int position){
        dbHelper.deleteTagsFromCategory(wordArrayList.get(position));
        dbHelper.deleteCategory(wordArrayList.get(position));
        wordArrayList.remove(position);
        gridAdapter.removeItem(position);
    }*/

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
