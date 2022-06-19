package com.spellingo.client_app
import androidx.lifecycle.*

class GameSessionViewModel() : ViewModel() {
    val model = Model()
    fun Hello () {
        println("Hello")
        model.addWords()
    }
}