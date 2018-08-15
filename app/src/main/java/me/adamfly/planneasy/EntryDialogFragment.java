package me.adamfly.planneasy;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Notification;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static me.adamfly.planneasy.MainActivity.entries;

/**
 * Created by adamf on 5/16/2017.
 */

public class EntryDialogFragment extends DialogFragment {

    //Thanks to https://stackoverflow.com/questions/21720759/convert-a-json-string-to-a-hashmap

    public static Map<String, Object> toMap(JSONObject object) throws JSONException {
        Map<String, Object> map = new HashMap<String, Object>();

        Iterator<String> keysItr = object.keys();
        while(keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);

            if(value instanceof JSONArray) {
                value = toList((JSONArray) value);
            }

            else if(value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }

    public static List<Object> toList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<Object>();
        for(int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            if(value instanceof JSONArray) {
                value = toList((JSONArray) value);
            }

            else if(value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        final View view = inflater.inflate(R.layout.entry_layout, null);
        final EditText newEntryTitle = (EditText) view.findViewById(R.id.newEntryTitle);
        final EditText newEntryCategory = (EditText) view.findViewById(R.id.newEntryCategory);
        final EditText newEntryDescription = (EditText) view.findViewById(R.id.newEntryDescription);
        final Spinner newEntryColor = (Spinner) view.findViewById(R.id.newEntryColor);
        ArrayAdapter<CharSequence> colorsAdapter = ArrayAdapter.createFromResource(getActivity().getBaseContext(),
                R.array.colors, android.R.layout.simple_spinner_item);
        colorsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        newEntryColor.setAdapter(colorsAdapter);

        builder.setTitle("Enter your entry:");
        builder.setView(view)
                // Add action buttons
                .setPositiveButton("Store", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if (!newEntryTitle.getText().toString().equals("") && !newEntryDescription.getText().toString().equals("")) {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if(user != null) {
                                final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
                                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {

                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.getValue() != null) {
                                            String values = new Gson().toJson(dataSnapshot.getValue());
                                            try{
                                                JSONObject oldJson = new JSONObject(values);
                                                JSONObject newEntry = new JSONObject();
                                                newEntry.put("title", newEntryTitle.getText().toString());
                                                if(!newEntryCategory.getText().toString().equals(""))
                                                    newEntry.put("category", newEntryCategory.getText().toString());
                                                newEntry.put("description", newEntryDescription.getText().toString());
                                                newEntry.put("color", newEntryColor.getSelectedItem().toString());
                                                int entryNum = 0;
                                                boolean entryIdFound = false;
                                                while(!entryIdFound){
                                                    if(!oldJson.has("entry" + entryNum)){
                                                        entryIdFound = true;
                                                    }
                                                    else{
                                                        entryNum++;
                                                    }
                                                }
                                                oldJson.put("entry" + entryNum, newEntry);
                                                Log.d("Planneasy", oldJson.toString());
                                                mDatabase.setValue(toMap(oldJson)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                    }
                                                });
                                            }
                                            catch(JSONException e){
                                            }
                                        }
                                        else{
                                            try{
                                                JSONObject oldJson = new JSONObject();
                                                JSONObject newEntry = new JSONObject();
                                                newEntry.put("title", newEntryTitle.getText().toString());
                                                if(!newEntryCategory.getText().toString().equals(""))
                                                    newEntry.put("category", newEntryCategory.getText().toString());
                                                newEntry.put("description", newEntryDescription.getText().toString());
                                                newEntry.put("color", newEntryColor.getSelectedItem().toString());
                                                int entryNum = 0;
                                                boolean entryIdFound = false;
                                                while(!entryIdFound){
                                                    if(!oldJson.has("entry" + entryNum)){
                                                        entryIdFound = true;
                                                    }
                                                    else{
                                                        entryNum++;
                                                    }
                                                }
                                                oldJson.put("entry" + entryNum, newEntry);
                                                Log.d("Planneasy", oldJson.toString());
                                                mDatabase.setValue(toMap(oldJson)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                    }
                                                });
                                            }
                                            catch(JSONException e){
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                    }
                                });
                            }
                        } else {
                            Toast.makeText(getActivity().getBaseContext(), "Please enter the title and description for the entry.", Toast.LENGTH_LONG)
                                    .show();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        EntryDialogFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }
}
