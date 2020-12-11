package com.halo.redpacket.mvvm

import android.os.Handler
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * LiveData 一般会跟 ViewModel 配合使用。ViewModel 跟 View 层解耦，通常将 LiveData 对象作为 ViewModel 的属性。
 *
 * val viewModel: FruitViewModel = ViewModelProviders.of(this).get(FruitViewModel::class.java)

    viewModel.getFruitList().observe(this, Observer {

    var adapter: ArrayAdapter<String> = ArrayAdapter(this,android.R.layout.simple_list_item_1,it)
    this.listView.adapter = adapter
    ......
    })
 */
class FruitViewModel : ViewModel(){
    private var fruitList: MutableLiveData<List<String>>? = null
    private var handler: Handler = Handler()

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "onCleared");
    }

    fun getFruitList(): MutableLiveData<List<String>> {
        if (fruitList == null) {
            fruitList = MutableLiveData()
            loadFruits()
        }
        return fruitList as MutableLiveData<List<String>>
    }

    /**
     * 模拟网络请求，延迟2.5s
     */
    private fun loadFruits() {
        handler.postDelayed({
            val list = ArrayList<String>()
            list.add("Apple")
            list.add("Banana")
            list.add("Orange")
            list.add("Pear")
            list.add("Watermelon")

            fruitList?.let {
                it.value = list
            }
        }, 2500)
    }

    companion object {
        private val TAG = FruitViewModel::class.java.simpleName
    }
}