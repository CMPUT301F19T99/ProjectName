package com.example.bigmood;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import static java.sql.Types.NULL;

public class FriendsActivity extends BaseDrawerActivity {
    //TODO: clean up imports and variables
    private static final String TAG = "FRIENDSACTIVITY";
    private FirebaseFirestore db;
    private CollectionReference userCollectionReference;
    private CollectionReference friendsCollectionReference;
    private RecyclerView recyclerView;
    public static RecyclerViewAdapter adapter;
    public static ArrayList<String> friendObjects = new ArrayList<>();
    private String userId;
    private List userFriends;
    private int startingIndex = 0;
    final private int queryLimit = 25;
    ImageView deleteMood;
    public static int index;

    public FriendsActivity() {
        this.db = FirebaseFirestore.getInstance();
        this.userCollectionReference = db.collection("Users");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_friends, frameLayout);
        toolbar.setTitle("Friends");

        this.userId = getIntent().getExtras().getString("USER_ID");

        this.recyclerView = findViewById(R.id.friends_recyclerview);
        this.userCollectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                friendObjects.clear();
                pullFriends();
                adapter.notifyDataSetChanged();
            }
        });
        initRecyclerView();
    }

    @Override
    protected void onResume() {
        index = -1;
        super.onResume();
        navigationView.getMenu().getItem(0).setChecked(true);
    }

    private void initRecyclerView() {
        //TODO: Load in Friends from Online.

        Log.d(TAG, "initRecyclerView: init recyclerview");
        recyclerView = findViewById(R.id.friends_recyclerview);
        adapter = new RecyclerViewAdapter(friendObjects, this.userId, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    // step 1: get all friends and shunt them into a list
    // step 2: realize there's only one step
    private void pullFriends() {
        final Query query = userCollectionReference.whereEqualTo("userId", this.userId);
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                friendObjects.clear();
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    if (doc.contains("userFriends")) {
                        // This line might explode
                        // slam in a try/catch
                        Toast.makeText(FriendsActivity.this, "Cool!", Toast.LENGTH_SHORT).show();
                        userFriends = (List) doc.get("userFriends");
                        friendObjects = new ArrayList<String>(userFriends);
                    } else {
                        // no friends, you fucking loser
                        recyclerView.setVisibility(View.GONE);
                    }
                }
            }
        });
    }
}