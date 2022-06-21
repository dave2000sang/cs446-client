package com.spellingo.client_app
import androidx.lifecycle.*

class GameSessionViewModel() : ViewModel() {
    val model = Model()


    // startGame() -> Starts the game and loads the list of words
    fun startGame () {
        model.startGame()
    }

    // getWord(): String -> Returns a randomly generated word from the model
    fun getWord () : String{
        return model.getRandomWord()
    }
}