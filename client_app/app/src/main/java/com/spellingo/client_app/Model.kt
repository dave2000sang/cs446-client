package com.spellingo.client_app

class Model {
    init {
    }

    private var listOfWords = mutableListOf<String>()

    fun addWords () {
        listOfWords.add("one")
        listOfWords.add("two")
        println(listOfWords)
    }
}