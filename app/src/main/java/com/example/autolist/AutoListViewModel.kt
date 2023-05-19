package com.example.autolist

import androidx.lifecycle.ViewModel

class AutoListViewModel : ViewModel() {

    private val autoRepository = AutoRepository.get()
    val autoListLiveData = autoRepository.getAutos()

    fun addAuto(auto: Auto) {
        autoRepository.addAuto(auto)
    }

}