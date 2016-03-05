package com.gmail.nelsonr462.crossover;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.nhaarman.listviewanimations.ArrayAdapter;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.undo.UndoAdapter;

import java.util.ArrayList;

public class DragNDropAdapter extends ArrayAdapter<String> implements UndoAdapter {

    private final Context mContext;
    private boolean showAllCheckTextView = false;

    public DragNDropAdapter(final Context context, final ArrayList<String> mTabs) {
        mContext = context;
        for (int i = 0; i < mTabs.size(); i++) {
            add(mTabs.get(i));
        }
    }

    public DragNDropAdapter(final Context context, final int textViewResourceId, final ArrayList<String> items) {
        mContext = context;
    }

    public DragNDropAdapter(Context context, ArrayList<String> mTabs, int mode) {
        mContext = context;
        for (int i = 0; i < mTabs.size(); i++) {
            add(mTabs.get(i));
        }
        if ( mode == 0 ) {
            showAllCheckTextView = true;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.fragment_list_url_item_handler, parent, false);
        }
        TextView mTextView = (TextView) view.findViewById(R.id.list_row_draganddrop_textview);
        mTextView.setText(getItem(position));
        mTextView.setTag(getItem(position).hashCode());
        CheckedTextView mCheckedTextView = (CheckedTextView) view.findViewById(R.id.list_row_draganddrop_checkbox);
        mCheckedTextView.setChecked(false);
        if (showAllCheckTextView) {
            mCheckedTextView.setVisibility(View.VISIBLE);
        }
        return view;
    }

    @NonNull
    @Override
    public String getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public long getItemId(final int position) {
        return getItem(position).hashCode();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @NonNull
    @Override
    public View getUndoView(final int position, final View convertView, @NonNull final ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.undo_button, parent, false);
        }
        return view;
    }

    @NonNull
    @Override
    public View getUndoClickView(@NonNull final View view) {
        return view.findViewById(R.id.undo_row_undobutton);
    }


}