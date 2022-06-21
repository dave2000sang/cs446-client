package com.spellingo.client_app

import android.app.Application
import android.content.Context
import android.content.Context.*
import android.content.res.AssetManager
import java.io.File.*
import java.net.URL
import java.nio.file.Paths
import java.util.stream.Collectors
import android.os.Bundle.*
import android.support.v4.app.*
import android.content.res.Resources.*
import java.io.File
import kotlin.random.Random


class Model {
//    init {
//        try {
//            val fileName = "WordList.txt"
//            val lines: List<String> = File(fileName).readLines()
//            lines.forEach {
//                line -> println(line)
//            }
//        } catch(e:Exception) {
//            e.printStackTrace()
//        } finally {
//            println ("File read successfully")
//        }
//    }

    private var listOfWords = mutableListOf<String>()



    fun startGame () {
        addWords()
    }

    fun getRandomWord () :String {
        return listOfWords.random()
    }

    private fun addWords () {
        listOfWords.add("Elephant")
        listOfWords.add("Recession")
        listOfWords.add("Commercial")
        listOfWords.add("Together")
    }
}