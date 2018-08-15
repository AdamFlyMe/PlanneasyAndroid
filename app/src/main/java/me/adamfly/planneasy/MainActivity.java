package me.adamfly.planneasy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private static final int RC_SIGN_IN = 123;
    private DatabaseReference mDatabase;
    private RelativeLayout relLayout;
    private TextView loadingText;

    static final ArrayList<Entry> entries = new ArrayList<Entry>();


    public void getData(String uid){
        relLayout = (RelativeLayout) findViewById(R.id.relLayout);
        loadingText.setText("Loading...\n\nTaking too long? Please check your internet connection and pull down to refresh.");

        mDatabase = FirebaseDatabase.getInstance().getReference("users").child(uid);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    String values = new Gson().toJson(dataSnapshot.getValue());
                    GridView gridView = (GridView) findViewById(R.id.gridView1);
                    entries.clear();

                    try {
                        Log.d("Planneasy", values);
                        JSONObject json = new JSONObject(values);
                        Iterator<String> iter = json.keys();
                        while (iter.hasNext()) {
                            String title = null;
                            String category = null;
                            String description = null;
                            String color = null;
                            String key = iter.next();
                            JSONObject subObject = json.getJSONObject(key);
                            if(subObject.has("title")){
                                title = subObject.getString("title");
                            }
                            if(subObject.has("category")){
                                category = subObject.getString("category");
                            }
                            if(subObject.has("description")){
                                description = subObject.getString("description");
                            }
                            if(subObject.has("color")){
                                color = subObject.getString("color");
                            }
                            entries.add(new Entry(key, title, category, description, color));
                        }
                    } catch (JSONException e) {
                        loadingText.setText("Loading failed. Please check your internet connection and try again.");
                        Log.d("Planneasy", e.toString());
                        e.printStackTrace();
                    }

                    gridView.setAdapter(new CustomAdapter(MainActivity.this, entries));

                    Log.d("Planneasy", entries.toString());

                    if (entries.isEmpty()) {
                        loadingText.setText("Click the plus button to add your first event!");
                    }
                    else{
                        relLayout.removeView(loadingText);
                    }
                }
                else{
                    loadingText.setText("Click the plus button to add your first event!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        relLayout = (RelativeLayout) findViewById(R.id.relLayout);
        RelativeLayout.LayoutParams textParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        textParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, R.id.RelConstLayout);

        loadingText = new TextView(getApplicationContext());
        loadingText.setGravity(Gravity.CENTER);
        loadingText.setTextAppearance(getApplicationContext(), android.R.style.TextAppearance_Large);
        loadingText.setLayoutParams(textParams);
        relLayout.addView(loadingText);
        loadingText.setText("Please sign in to Google for the app to store entries.\n\nCancelled sign in/didn't popup? Pull down to refresh.");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EntryDialogFragment dialog = new EntryDialogFragment();
                dialog.show(getFragmentManager(), "Enter your event");
            }
        });
        final SwipeRefreshLayout refresh = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh.setRefreshing(false);
                entries.clear();
                onStart();
            }
        });
    }

    @Override
    public void onStart() {
        mAuth = FirebaseAuth.getInstance();
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            List<AuthUI.IdpConfig> providers = Arrays.asList(new AuthUI.IdpConfig.GoogleBuilder().build());

            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .build(),
                    RC_SIGN_IN);
        }
        else{
            firstTimeRunCheck();
            getData(user.getUid());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user != null) {
                    firstTimeRunCheck();
                    getData(user.getUid());
                }
            }
        }
    }

    public void firstTimeRunCheck(){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences getPrefs = PreferenceManager
                        .getDefaultSharedPreferences(getBaseContext());

                boolean isFirstStart = getPrefs.getBoolean("firstStart", true);

                if (isFirstStart) {

                    Intent i = new Intent(MainActivity.this, IntroActivity.class);
                    startActivity(i);

                    SharedPreferences.Editor e = getPrefs.edit();
                    e.putBoolean("firstStart", false);
                    e.apply();
                }
            }
        });

        t.start();
    }
}
