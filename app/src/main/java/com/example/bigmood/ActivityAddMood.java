package com.example.bigmood;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.spec.ECField;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import static com.example.bigmood.DashboardActivity.adapter;
import static com.example.bigmood.DashboardActivity.index;
import static com.example.bigmood.DashboardActivity.moodObjects;

/**
 * todo: Activity add mood does both edit and add
 */
public class ActivityAddMood extends AppCompatActivity {
    public static final int CAMERA_ACCESS = 1001;
    public static final int GALLERY_ACCESS = 9999;
    private Context context;
    TextView dateText , description, moodUserName;
    Button saveButton;
    Button addLoc;
    LinearLayout profileBackground;
    ImageView profilePic, deleteMood, emojiPic;
    Spinner moodTitle, moodColor, moodSituation; // moodTitle and moodType is the same here for now
    String image;
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd, HH:MM");
    Date date = Calendar.getInstance().getTime();
    String dayString = dateFormat.format(date);
    private FusedLocationProviderClient fusedLocationClient;
    private String userId;


    /**
     * firebase stuff here
     * todo: putting mood objects in firebase and generating them
     */

    private FirebaseFirestore db;
    private CollectionReference moodCollectionReference;

    public ActivityAddMood() {
        this.db = FirebaseFirestore.getInstance();
        // collection reference for moods
        this.moodCollectionReference = db.collection("Moods");
    }


    private DatePickerDialog.OnDateSetListener mDateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        // all the stuff id's
        setContentView(R.layout.activity_add_mood);
        profilePic = findViewById(R.id.Profile_image);
        saveButton = findViewById(R.id.save_button);
        addLoc = findViewById(R.id.add_loc);
        dateText = findViewById(R.id.currentDate);
        description = findViewById(R.id.moodDescription);
        moodTitle = findViewById(R.id.currentMoodSpinner);
        moodUserName = findViewById(R.id.moodUserName);
        //todo: mood username
        moodUserName.setText("MoodUserName");
        moodColor = findViewById(R.id.currentMoodColorSpinner);
        moodSituation = findViewById(R.id.moodSituationSpinner);
        profileBackground = findViewById(R.id.background_pic);
        deleteMood = findViewById(R.id.deleteMood);
        emojiPic = findViewById(R.id.currentMoodImage);
        final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        this.userId = getIntent().getExtras().getString("USER_ID");
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        // object added to moods array adapter
        final ArrayAdapter<CharSequence> colorAdapter = ArrayAdapter.createFromResource(this, R.array.editmood_moodcolor_spinner, android.R.layout.simple_list_item_1);
        final ArrayAdapter<CharSequence> titleAdapter = ArrayAdapter.createFromResource(this, R.array.editmood_moodspinner, android.R.layout.simple_list_item_1);
        final ArrayAdapter<CharSequence> situations = ArrayAdapter.createFromResource(this, R.array.editmood_moodsituation_spinner, android.R.layout.simple_list_item_1);

        /**
         * Set up the spinner adapters accordingly
         */
        moodTitle.setAdapter(titleAdapter);
        moodColor.setAdapter(colorAdapter);
        moodSituation.setAdapter(situations);


        /**
         * HashMap for each mood Colors
         * todo: change the color here according to the necessity
         */
        final HashMap<String,String> colorHash = new HashMap<String, String>(){{
            put("Set Color", "#FFFFFF");
            put("Happy", "#FFFF00");
            put("Fear", "#1AFF00");
            put("Surprise", "#00B7FF");
            put("Disgust", "#CC00FF");
            put("Anger", "#AEFF00");
            put("Bored", "#FE6301");
            put("Sad", "#0054FF");
            put("Love","#EDC0E1");
        }};




        final Mood mood = (Mood)getIntent().getSerializableExtra("Mood");
        Log.d("oKAY","Mood Id from Add Mood" + mood.getMoodID());

        final CollectionReference collectionReference = db.collection("Moods");
        final CollectionReference userCollectionReference = db.collection("Users");
        if (mood.getMoodDate() == null) {
            mood.setMoodDate(Timestamp.now());
        }
        /**
         * set the mood user name according to the username on database
         */
        final String TAG = "Sample";

        /**
         * If the index is not -1 or it's in edit mood situation
         */
        if (DashboardActivity.index != -1 ){

            moodTitle.setSelection(titleAdapter.getPosition(mood.getMoodTitle()));
            moodUserName.setText(mood.getMoodUsername());
            moodColor.setSelection(titleAdapter.getPosition(colorHash.get(mood.getMoodTitle())));
            moodSituation.setSelection(situations.getPosition(mood.getMoodSituation()));
            setMoodEmoji(mood.getMoodTitle());
            description.setText(mood.getMoodDescription());
            String stringHEX = mood.getMoodColor();

            try {

                profileBackground.setBackgroundColor(Color.parseColor(stringHEX));
            }catch (Throwable e){
                e.printStackTrace();
            }

            //todo: String to bitmap
            try{
                byte [] encodeByte=Base64.decode(mood.getMoodPhoto(),Base64.DEFAULT);
                Bitmap bitmap=BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
                profilePic.setImageBitmap(bitmap);

            }catch (Exception e){
                e.getMessage();
            }
        }


        /**
         * Save button to save mood object with it's requirements
         */
        //todo:  each user will have the moodiD's in their user profile as a reference
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // todo: set Mood user name here (hardcoded here)
                mood.setMoodUsername("Nasif Hossain");
                mood.setMoodTitle(titleAdapter.getItem(moodTitle.getSelectedItemPosition()).toString());
                setMoodEmoji(mood.getMoodTitle());
                mood.setMoodColor(colorHash.get(titleAdapter.getItem(moodTitle.getSelectedItemPosition())));
                mood.setMoodDescription(description.getText().toString());
                mood.setMoodSituation(situations.getItem(moodSituation.getSelectedItemPosition()).toString());
                mood.setMoodPhoto(image);
                mood.setMoodEmoji(getMoodEmoji());

                // date input given
                try{
                    mood.setMoodDate(new Timestamp((dateFormat.parse(dateText.getText().toString()))));

                }catch (ParseException e){
                    e.getStackTrace();
                }
                HashMap<String, Object> data = new HashMap<>();
                data.put("moodTitle", mood.getMoodTitle());
                data.put("moodDescription", mood.getMoodDescription());
                data.put("moodColor", mood.getMoodColor());
                data.put("moodPhoto", mood.getMoodPhoto());
                data.put("moodDate", mood.getMoodDate());
                data.put("dateCreated", Timestamp.now());
                data.put("dateUpdated", Timestamp.now());

                data.put("moodCreator", userId);
                //TODO: replace with enum for social situation
                data.put("moodSituation", mood.getMoodSituation());
                data.put("longitude", mood.getLongitude());
                data.put("latitude", mood.getLatitude());
                data.put("moodEmoji", mood.getMoodEmoji());
                Log.d("Index: ",String.valueOf(index));
                try{
                    if (index != -1){
                        data.put("moodId", mood.getMoodID());
                        adapter.notifyDataSetChanged();
                        collectionReference
                                .document(mood.getMoodID())
                                .update(data)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG,"Data addition successful");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "Data addition failed" + e.toString());
                                    }
                                });
                        index = -1;
                        finish();

                    }
                    else{
                        mood.setMoodID(String.valueOf(Timestamp.now().hashCode()));
                        data.put("moodId", mood.getMoodID());
                        collectionReference
                                .document(mood.getMoodID())
                                .set(data)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG,"Data addition successful");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "Data addition failed" + e.toString());
                                    }
                                });
                        index = -1;
                        finish();
                    }

                } catch (Exception e){
                    Toast.makeText(context, "You haven't put a title for your mood",Toast.LENGTH_LONG).show();
                }

            }
        });

        // todo: implementing delete mood
        deleteMood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (moodObjects.size() > 0){
                    collectionReference.document(mood.getMoodID())
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "Mood successfully deleted!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error deleting mood", e);
                                }
                            });
                    moodObjects.remove(mood);
                    adapter.notifyDataSetChanged();
                }else{
                    Toast.makeText(ActivityAddMood.this, "No mood posted",Toast.LENGTH_LONG).show();

                }

                finish();
            }
        });

        addLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null){
                            mood.setLongitude(location.getLongitude());
                            mood.setLatitude(location.getLatitude());
                        } else {
                            Toast.makeText(context, "Location error",Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenCamera(view);
            }
        });
        /**
         * date picker
         */
        dateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(
                        ActivityAddMood.this,android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        // date picker format
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                dateText.setText(String.format("%04d-%02d-%02d",year,month,day));

            }
        };
    }

    /**
     * Set emoji according to mood type
     * @param emotion
     */
    // todo: set emoji according to hashmap of mood type
    public void setMoodEmoji(String emotion){
        switch (emotion){
            case "Happy":
                emojiPic.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.mipmap.emoji_happy));
                break;
            case "Sad":
                emojiPic.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.mipmap.emoji_sad));
                break;
            case "Fear":
                emojiPic.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.mipmap.emoji_fear));
                break;
            case "Surprise":
                emojiPic.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.mipmap.emoji_surprised));
                break;
            case "Anger":
                emojiPic.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.mipmap.emoji_angry));
                break;
            case "Bored":
                emojiPic.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.mipmap.emoji_bored));
                break;
            case "Disgust":
                emojiPic.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.mipmap.emoji_disgust));
                break;
            case "Love":
                emojiPic.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.mipmap.emoji_love));
                break;
        }
    }

    public String getMoodEmoji(){
        Drawable drawable= emojiPic.getDrawable();
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        BitmapDrawable bitmapDrawable = ((BitmapDrawable) drawable);
        Bitmap bitmap = bitmapDrawable.getBitmap();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b =baos.toByteArray();
        String temp=Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }
    /**
     * working on the open camera and open album functionality
     */
    public void OpenCamera(View view){
        Intent intent =  new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,CAMERA_ACCESS);
    }
    public void OpenAlbum(View view){
        Intent intent =  new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,GALLERY_ACCESS);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        context = getApplicationContext();
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==CAMERA_ACCESS && resultCode == RESULT_OK)    {
            Bitmap bitmap= (Bitmap) data.getExtras().get("data");
            // todo: working on image compression
            ByteArrayOutputStream baos=new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
            byte [] b =baos.toByteArray();
            String temp=Base64.encodeToString(b, Base64.DEFAULT);
            //moods.get(index).setMoodPhoto(temp);
            image = temp;
            profilePic.setImageBitmap(bitmap);
        }

        else if(requestCode==GALLERY_ACCESS){
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                profilePic.setImageBitmap(selectedImage);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(context, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(context, "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
    }
}