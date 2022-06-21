package com.spellingo.client_app

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class GameSessionFragment : Fragment() {

    private val viewModel: GameSessionViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Starts the game and loads data. Do NOT change the position of this, it needs to go
        // before variable declarations below.
        viewModel.startGame()

        // Links Widgets to Variables
        val root = inflater.inflate(R.layout.fragment_game_session, container, false)
        val returnToMainMenuButton = root.findViewById<Button>(R.id.goBackToMenuButton)
        val mainWordField = root.findViewById<EditText>(R.id.mainWordField)
        val submitButton = root.findViewById<Button>(R.id.buttonSubmit)
        val getCorrectWord = viewModel.getWord() // gets a word to be tested on.
        println (getCorrectWord) // prints to word for testing (look in the run tab)

        // Listeners

        // Submit Button to check if entered word is correct. Look in text field
        submitButton.setOnClickListener {
            if (mainWordField.text.toString() == getCorrectWord) {
                println ("Correct!")
                // put your widget stuff here
            } else {
                println ("Incorrect!")
                // put your widget stuff here
            }
        }

        // Button to go back to main menu.
        returnToMainMenuButton.setOnClickListener {
            findNavController().navigate(R.id.mainMenuFragment_to_gameSessionFragment)
        }

        // The commented out code is for Textchange listeners for a Textfield may be useful later
//        mainWordField.addTextChangedListener(object : TextWatcher {
//
//            override fun afterTextChanged(s: Editable) {}
//
//            override fun beforeTextChanged(s: CharSequence, start: Int,
//                                           count: Int, after: Int) {
//            }
//
//            override fun onTextChanged(s: CharSequence, start: Int,
//                                       before: Int, count: Int) {
//
//            }
//        })
        return root
    }
}