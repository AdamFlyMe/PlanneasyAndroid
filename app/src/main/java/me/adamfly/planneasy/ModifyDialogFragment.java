package me.adamfly.planneasy;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static android.app.Activity.RESULT_OK;
import static me.adamfly.planneasy.EntryDialogFragment.toMap;

/**
 * Created by adamf on 5/16/2017.
 */

public class ModifyDialogFragment extends DialogFragment{
    Bitmap mBitmap = null;
    Uri uri = null;
    Entry entry;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        final View view = inflater.inflate(R.layout.entry_layout, null);
        final EditText newEntryTitle = (EditText) view.findViewById(R.id.newEntryTitle);
        if (entry.getTitle() != null)
            newEntryTitle.setText(entry.getTitle());
        final EditText newEntryCategory = (EditText) view.findViewById(R.id.newEntryCategory);
        if (entry.getCategory() != null)
            newEntryCategory.setText(entry.getCategory());
        final EditText newEntryDescription = (EditText) view.findViewById(R.id.newEntryDescription);
        if (entry.getDescription() != null)
            newEntryDescription.setText(entry.getDescription());
        final Spinner newEntryColor = (Spinner) view.findViewById(R.id.newEntryColor);
        ArrayAdapter<CharSequence> colorsAdapter = ArrayAdapter.createFromResource(getActivity().getBaseContext(),
                R.array.colors, android.R.layout.simple_spinner_item);
        colorsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        newEntryColor.setAdapter(colorsAdapter);
        if (entry.getColor() != null)
            newEntryColor.setSelection(colorsAdapter.getPosition(entry.getColor()));
        builder.setTitle("Modify your entry:");
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
                                                oldJson.put(entry.getEntryId(), newEntry);
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
                        ModifyDialogFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }
    public void setEntry(Entry e){
        entry = e;
    }
}
