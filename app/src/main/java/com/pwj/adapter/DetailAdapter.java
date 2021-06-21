package com.pwj.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.github.wrdlbrnft.sortedlistadapter.SortedListAdapter;
import com.pwj.bean.WordModel;
import com.pwj.helloya.databinding.ItemDetailBinding;


import java.util.Comparator;


public class DetailAdapter extends SortedListAdapter<WordModel> {

    public interface Listener {
        void onExampleModelClicked(WordModel model);
    }

    private final Listener mListener;

    public DetailAdapter(Context context, Comparator<WordModel> comparator, Listener listener) {
        super(context, WordModel.class, comparator);
        mListener = listener;
    }

    @NonNull
    @Override
    protected ViewHolder<? extends WordModel> onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent, int viewType) {
        final ItemDetailBinding binding = ItemDetailBinding.inflate(inflater, parent, false);
        return new DetailViewHolder(binding, mListener);
    }
}
