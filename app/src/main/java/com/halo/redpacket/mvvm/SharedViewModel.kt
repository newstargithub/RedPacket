package com.halo.redpacket.mvvm

import androidx.lifecycle.MutableLiveData

class SharedViewModel: BaseViewModel() {
    private val text: MutableLiveData<String> = MutableLiveData()

    fun setText(input: String) {
        text.value = input
    }

    fun getText() = text
}