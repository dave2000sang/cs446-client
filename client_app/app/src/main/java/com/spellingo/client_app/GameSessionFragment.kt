package com.spellingo.client_app

import android.graphics.Typeface
import android.media.MediaPlayer
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.content.Context
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.google.android.material.button.MaterialButton
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
        val submitButton = root.findViewById<MaterialButton>(R.id.buttonSubmit)
        val pronunciationButton = root.findViewById<ImageView>(R.id.button_pronunciation)
        val infoBox = root.findViewById<TextView>(R.id.info_box)

        // Mutable fields and observers
        var getCorrectWord = ""
        var mediaPlayer: MediaPlayer? = null

        // Word information
        viewModel.wordLiveData.observe(viewLifecycleOwner, Observer(fun(word) {
            getCorrectWord = word.id
            val part = word.part.replaceFirstChar { it.uppercase() }
            val definition = word.definition.replaceFirstChar { it.uppercase() }
            val usage = word.usage.replaceFirstChar { it.uppercase() }
            val origin = word.origin.replaceFirstChar { it.uppercase() }
            val infoBoxString = "$part\n\n$definition\n\nUsage\n\t$usage\n\nEtymology\n\t$origin"
            val infoBoxSpannable = SpannableString(infoBoxString)
            var textIdx = 0
            // Span for part
            infoBoxSpannable.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.infobox_highlight1)),
                textIdx, textIdx + part.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            // Span for "Usage"
            textIdx += part.length + 2 + definition.length + 2
            infoBoxSpannable.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.infobox_highlight2)),
                textIdx, textIdx + "Usage".length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            textIdx += "Usage".length + 2
            // Span for usage
            infoBoxSpannable.setSpan(
                StyleSpan(Typeface.ITALIC),
                textIdx, textIdx + usage.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            infoBoxSpannable.setSpan(
                RelativeSizeSpan(0.8f),
                textIdx, textIdx + usage.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            textIdx += usage.length + 2
            // Span for "Etymology"
            infoBoxSpannable.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.infobox_highlight2)),
                textIdx, textIdx + "Etymology".length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            textIdx += "Etymology".length + 2
            // Span for origin
            infoBoxSpannable.setSpan(
                RelativeSizeSpan(0.8f),
                textIdx, textIdx + origin.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            infoBox.text = infoBoxSpannable
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
            // Continue
            if (submitButton.text == "Continue") {
                // Reset word field
                mainWordField.text.clear()
                val remainingWords = viewModel.nextWord()
                // Change to submit button
                submitButton.text = getString(R.string.submit)
                submitButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.submit_button_color))
                //TODO if remainingWords == 0, change submitButton into transition to stats page
            }
            // Submit
            if (mainWordField.text.isNotEmpty()) { // only process if text box is not empty
                if (mainWordField.text.toString() == getCorrectWord) {
                    println ("Correct!")
                } else {
                    println ("Incorrect!")
                    // put your widget stuff here
                }
                // Hide keyboard
                val imm = mainWordField.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(mainWordField.windowToken, 0)
                // Change to continue button
                submitButton.text = getString(R.string.cont)
                submitButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.continue_button_color))
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