package com.spellingo.client_app

import android.media.MediaPlayer
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import java.lang.NullPointerException

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class GameSessionFragment : Fragment() {

    private val viewModel: GameSessionViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Links Widgets to Variables
        val root = inflater.inflate(R.layout.fragment_game_session, container, false)
        val mainWordField = root.findViewById<EditText>(R.id.mainWordField)
        val submitButton = root.findViewById<Button>(R.id.buttonSubmit)
        val pronunciationButton = root.findViewById<ImageView>(R.id.button_pronunciation)
        val infoBox = root.findViewById<TextView>(R.id.info_box)

        // Mutable fields and observers
        var getCorrectWord = ""
        var mediaPlayer: MediaPlayer? = null

        // Word information
        viewModel.wordLiveData.observe(viewLifecycleOwner, Observer(fun(word) {
            getCorrectWord = word.id
            // TODO(andrew): better formatting
            val infoBoxString = word.definition + "\n" +
                    word.usage + "\n" +
                    word.origin + "\n" +
                    word.part + "\n"
            infoBox.text = infoBoxString
        }))

        // Pronunciation audio
        viewModel.pronunciationLiveData.observe(viewLifecycleOwner, Observer(fun(mp) {
            if(mediaPlayer == null) {
                mediaPlayer = mp
            }
        }))

        // Listeners

        // Pronunciation button to replay audio
        pronunciationButton.setOnClickListener {
            try {
                mediaPlayer!!.start()
            }
            catch (e: NullPointerException) {} // mediaPlayer not observed
            catch (e: IllegalStateException) {} // mediaPlayer not ready
        }

        // Submit Button to check if entered word is correct. Look in text field
        submitButton.setOnClickListener {
            if (mainWordField.text.isNotEmpty()) { // only process if text box is not empty
                if (mainWordField.text.toString() == getCorrectWord) {
                    println ("Correct!")
                    val remainingWords = viewModel.nextWord()
                    //TODO if remainingWords == 0, change submitButton into transition to stats page
                } else {
                    println ("Incorrect!")
                    // put your widget stuff here
                }
                // Reset word field
                mainWordField.text.clear()
            }
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