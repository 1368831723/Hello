package com.pwj.adapter;

import android.support.annotation.NonNull;

import com.github.wrdlbrnft.sortedlistadapter.SortedListAdapter;
import com.pwj.bean.WordModel;
import com.pwj.helloya.databinding.ItemSearchBinding;


public class SearchViewHolder extends SortedListAdapter.ViewHolder<WordModel> {

    private final ItemSearchBinding mBinding;

    public SearchViewHolder(ItemSearchBinding binding, SearchAdapter.Listener listener) {
        super(binding.getRoot());
        binding.setListener(listener);

        mBinding = binding;
    }

    @Override
    protected void performBind(@NonNull WordModel item) {
        mBinding.setModel(item);
    }
}
