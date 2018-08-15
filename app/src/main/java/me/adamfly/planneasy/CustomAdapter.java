package me.adamfly.planneasy;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.Base64;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.view.GestureDetector.OnGestureListener;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class CustomAdapter extends BaseAdapter {
    Entry[] result;
    Context context;
    FragmentManager manager;
    int mediaLength;
    Bitmap decodedByte;
    private static LayoutInflater inflater = null;

    public CustomAdapter(MainActivity mainActivity, ArrayList<Entry> entryList) {
        // TODO Auto-generated constructor stub
        result = entryList.toArray(new Entry[entryList.size()]);
        context = mainActivity;
        manager = mainActivity.getFragmentManager();
        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return result.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public class Holder {
        Entry entry;
        CardView entryCard;
        TextView cardTitle;
        TextView cardCategory;
        TextView cardDescription;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        // TODO Auto-generated method stub
        final Holder holder = new Holder();
        final View rowView;

        rowView = inflater.inflate(R.layout.grid_layout, null);
        holder.entry = result[position];
        holder.entryCard = (CardView) rowView.findViewById(R.id.entryCard);
        holder.cardTitle = (TextView) rowView.findViewById(R.id.cardTitle);
        holder.cardCategory = (TextView) rowView.findViewById(R.id.cardCategory);
        holder.cardDescription = (TextView) rowView.findViewById(R.id.cardDescription);

        if (holder.entry.getTitle() != null){
            holder.cardTitle.setText(holder.entry.getTitle());
        }
        else{
            holder.cardTitle.setVisibility(View.INVISIBLE);
        }

        if (holder.entry.getCategory() != null){
            holder.cardCategory.setText(holder.entry.getCategory());
        }
        else{
            holder.cardCategory.setVisibility(View.INVISIBLE);
        }

        if (holder.entry.getDescription() != null){
            holder.cardDescription.setText(holder.entry.getDescription());
        }
        else{
            holder.cardDescription.setVisibility(View.INVISIBLE);
        }

        if (holder.entry.getColor() != null){
            switch(holder.entry.getColor()){
                case "white":
                    break;
                case "black":
                    holder.entryCard.setCardBackgroundColor(Color.parseColor("#424242"));
                    holder.cardTitle.setTextColor(Color.parseColor("#FFFFFF"));
                    holder.cardCategory.setTextColor(Color.parseColor("#FFFFFF"));
                    holder.cardDescription.setTextColor(Color.parseColor("#FFFFFF"));
                    break;
                case "indigo":
                    holder.entryCard.setCardBackgroundColor(Color.parseColor("#009688"));
                    holder.cardTitle.setTextColor(Color.parseColor("#FFFFFF"));
                    holder.cardCategory.setTextColor(Color.parseColor("#FFFFFF"));
                    holder.cardDescription.setTextColor(Color.parseColor("#FFFFFF"));
                    break;
                case "gray":
                    holder.entryCard.setCardBackgroundColor(Color.parseColor("#6c757d"));
                    holder.cardTitle.setTextColor(Color.parseColor("#FFFFFF"));
                    holder.cardCategory.setTextColor(Color.parseColor("#FFFFFF"));
                    holder.cardDescription.setTextColor(Color.parseColor("#FFFFFF"));
                    break;
                case "green":
                    holder.entryCard.setCardBackgroundColor(Color.parseColor("#4caf50"));
                    holder.cardTitle.setTextColor(Color.parseColor("#FFFFFF"));
                    holder.cardCategory.setTextColor(Color.parseColor("#FFFFFF"));
                    holder.cardDescription.setTextColor(Color.parseColor("#FFFFFF"));
                    break;
                case "red":
                    holder.entryCard.setCardBackgroundColor(Color.parseColor("#f44336"));
                    holder.cardTitle.setTextColor(Color.parseColor("#FFFFFF"));
                    holder.cardCategory.setTextColor(Color.parseColor("#FFFFFF"));
                    holder.cardDescription.setTextColor(Color.parseColor("#FFFFFF"));
                    break;
                case "orange":
                    holder.entryCard.setCardBackgroundColor(Color.parseColor("#ff5722"));
                    holder.cardTitle.setTextColor(Color.parseColor("#FFFFFF"));
                    holder.cardCategory.setTextColor(Color.parseColor("#FFFFFF"));
                    holder.cardDescription.setTextColor(Color.parseColor("#FFFFFF"));
                    break;
                case "blue":
                    holder.entryCard.setCardBackgroundColor(Color.parseColor("#03a9f4"));
                    holder.cardTitle.setTextColor(Color.parseColor("#FFFFFF"));
                    holder.cardCategory.setTextColor(Color.parseColor("#FFFFFF"));
                    holder.cardDescription.setTextColor(Color.parseColor("#FFFFFF"));
                    break;
            }
        }

        rowView.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                DeleteDialogFragment dialog = new DeleteDialogFragment();
                dialog.passManager(manager);
                dialog.setDeleteEntry(holder.entry);
                dialog.show(manager, "Modify entry?");
                return true;
            }

        });

        return rowView;
    }
}