package com.pwj.adapter;

import android.support.annotation.NonNull;

import com.github.wrdlbrnft.sortedlistadapter.SortedListAdapter;
import com.pwj.bean.WordModel;
import com.pwj.helloya.databinding.ItemDetailBinding;


public class DetailViewHolder extends SortedListAdapter.ViewHolder<WordModel> {

    private final ItemDetailBinding mBinding;

    public DetailViewHolder(ItemDetailBinding binding, DetailAdapter.Listener listener) {
        super(binding.getRoot());
        binding.setListener(listener);

        mBinding = binding;
    }

    @Override
    protected void performBind(@NonNull WordModel item) {
        mBinding.setModel(item);
    }
}
