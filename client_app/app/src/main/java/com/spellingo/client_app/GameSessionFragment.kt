package com.spellingo.client_app

import android.content.Context
import android.graphics.Typeface
import android.media.MediaPlayer
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class GameSessionFragment : Fragment() {

    private val viewModel: GameSessionViewModel by activityViewModels()
    private lateinit var navController: NavController
    private val navListener = NavController.OnDestinationChangedListener { _, destination, _ ->
        if(viewModel.previousDestination != destination.id
            && destination.id == R.id.fragment_game_session) {
            viewModel.startSession()
        }
        viewModel.previousDestination = destination.id
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Links Widgets to Variables
        val root = inflater.inflate(R.layout.fragment_game_session, container, false)
        val mainWordField = root.findViewById<EditText>(R.id.mainWordField)
        val submitButton = root.findViewById<MaterialButton>(R.id.buttonSubmit)
        val hintButton = root.findViewById<ImageView>(R.id.button_hint)
        val pronunciationButton = root.findViewById<ImageView>(R.id.button_pronunciation)
        val infoBox = root.findViewById<TextView>(R.id.info_box)

        // NavController
        navController = findNavController()
        navController.addOnDestinationChangedListener(navListener)

        // Mutable fields and observers
        var getCorrectWord = ""
        var mediaPlayer: MediaPlayer? = null

        // Word information
        viewModel.wordLiveData.observe(viewLifecycleOwner, Observer(fun(word) {
            getCorrectWord = word.id
            val infoList = mutableListOf<InfoBox>()
            // Decorators for infobox, start with part of speech and definition only
            infoList.add(GameSessionInfoBox(requireContext()))
            // Add usage for medium difficulty
            infoList.add(InfoBoxUsage(infoList.last(), requireContext()))
            // Add origin for hard difficulty
            infoList.add(InfoBoxOrigin(infoList.last(), requireContext()))
            infoBox.text = when(viewModel.difficulty) {
                "hard" -> infoList[0].getSpannable(word)
                "medium" -> infoList[1].getSpannable(word)
                "easy" -> infoList[2].getSpannable(word)
                else -> ""
            }
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
            //TODO add toasts for error messages
            catch (e: NullPointerException) {} // mediaPlayer not observed
            catch (e: IllegalStateException) {} // mediaPlayer not ready
        }

        // Correct / incorrect message
        val snack = Snackbar.make(requireActivity().findViewById(android.R.id.content), "This is a snack.", Snackbar.LENGTH_INDEFINITE)
        val snackTextView = snack.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        snackTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,18F)
        // Submit Button to check if entered word is correct. Look in text field
        submitButton.setOnClickListener {
            // Submit button and input in non-empty
            if (submitButton.text == getString(R.string.submit) && mainWordField.text.isNotEmpty()) {
                val result = mainWordField.text.toString().equals(getCorrectWord, true)
                // Hide keyboard
                val imm = mainWordField.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(mainWordField.windowToken, 0)
                // Change to continue button
                submitButton.text = getString(R.string.cont)
                // Correct / incorrect message
                if (result) {
                    snack.setText("Correct")
                    snack.view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.monokai_green))
                    submitButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.monokai_green))
                } else {
                    val snackText = "The correct spelling is: $getCorrectWord"
                    val snackSpannable = SpannableString(snackText)
                    snackSpannable.setSpan(
                        StyleSpan(Typeface.BOLD),
                        snackText.length - getCorrectWord.length, snackText.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    snack.setText(snackSpannable)
                    snack.view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.monokai_red))
                    submitButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.monokai_red))
                }
                snack.show()
                viewModel.updateResults(getCorrectWord, result)
            }
            // Continue button
            else if (submitButton.text == getString(R.string.cont)) {
                // Dismiss correct / incorrect message
                snack.dismiss()
                // Reset word field
                mainWordField.text.clear()
                val remainingWords = viewModel.nextWord()
                // Change to submit button
                submitButton.text = getString(R.string.submit)
                submitButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.submit_button_color))
                //TODO if remainingWords == 0, change submitButton into transition to stats page
            }
        }

        // Get hint button
        hintButton.setOnClickListener {
            if(submitButton.text == getString(R.string.submit)) { // only show hint before submission
                snack.setText("Hint: " + viewModel.getHint())
                snack.view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.monokai_yellow))
                submitButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.monokai_yellow))
                snack.show()
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

    override fun onDestroyView() {
        navController.removeOnDestinationChangedListener(navListener)
        super.onDestroyView()
    }
}