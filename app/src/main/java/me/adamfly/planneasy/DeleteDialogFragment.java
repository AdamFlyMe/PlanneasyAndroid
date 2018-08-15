package me.adamfly.planneasy;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

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

import static me.adamfly.planneasy.EntryDialogFragment.toMap;
import static me.adamfly.planneasy.MainActivity.entries;

/**
 * Created by adamf on 5/16/2017.
 */

public class DeleteDialogFragment extends DialogFragment{
    Entry deleteEntry;
    FragmentManager manager;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        final View view = inflater.inflate(R.layout.delete_layout, null);
        builder.setTitle("Modify entry?");
        builder.setView(view)
                // Add action buttons
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
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
                                            oldJson.remove(deleteEntry.getEntryId());
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
                    }
                })
                .setNeutralButton("Edit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ModifyDialogFragment d = new ModifyDialogFragment();
                        d.setEntry(deleteEntry);
                        d.show(manager, "Modify entry");
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DeleteDialogFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }
    public void setDeleteEntry(Entry entry){
        deleteEntry = entry;
    }
    public void passManager(FragmentManager f){
        manager = f;
    }
}
