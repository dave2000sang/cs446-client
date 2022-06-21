package com.spellingo.client_app
import androidx.lifecycle.*

class GameSessionViewModel() : ViewModel() {
    val model = Model()


    fun startGame () {
        model.startGame()
    }

    fun getWord () : String{
        return model.getRandomWord()
    }
}