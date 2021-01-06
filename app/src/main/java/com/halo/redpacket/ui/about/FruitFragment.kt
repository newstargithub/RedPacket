package com.halo.redpacket.ui.about

import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import com.halo.redpacket.R
import com.halo.redpacket.base.BaseFragment
import com.halo.redpacket.mvvm.viewModelDelegate
import kotlinx.android.synthetic.main.fragment_fruit.*
import java.util.ArrayList

class FruitFragment : BaseFragment() {

    val viewModel: FruitViewModel by viewModelDelegate(FruitViewModel::class)

    override fun layoutId(): Int {
        return R.layout.fragment_fruit
    }

    override fun initView() {
        viewModel.getFruitList().observe(this, Observer {list->
            var adapter = context?.let {
                ArrayAdapter(it, android.R.layout.simple_list_item_1, list)
            }
            this.listView.adapter = adapter
        })
    }




}