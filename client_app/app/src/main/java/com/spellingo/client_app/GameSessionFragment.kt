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
    private var snack: Snackbar? = null
    private lateinit var navController: NavController
    private val navListener = NavController.OnDestinationChangedListener { _, destination, _ ->
        // Have we just navigated to game session fragment?
        if(viewModel.previousDestination != destination.id
            && destination.id == R.id.fragment_game_session) {
            // Are we doing word of the day?
            if(arguments != null && arguments!!.getBoolean("wotd")) {
                viewModel.updateStrategy(GameStrategy.WOTD)
            }
            else {
                viewModel.updateStrategy(GameStrategy.STANDARD)
            }
            val category = if(arguments != null && arguments!!.getString("category") != null) {
                arguments!!.getString("category")!!
            }
            else {
                "standard"
            }
            val difficulty = if(arguments != null && arguments!!.getString("difficulty") != null) {
                arguments!!.getString("difficulty")!!
            }
            else {
                "OTHER"
            }
            println("DEBUG ============================== $category $difficulty")
            // Start the session by fetching words
            viewModel.startSession(category, Difficulty.getByName(difficulty))
            // Reset submitButton
            viewModel.submitLiveData.value = getString(R.string.submit)
            viewModel.colorLiveData.value = "yellow"
        }
        // Update destination tracking
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

        // Init snackbar
        snack = Snackbar.make(requireActivity().findViewById(android.R.id.content), "This is a snack.", Snackbar.LENGTH_INDEFINITE)

        // NavController
        navController = findNavController()
        navController.addOnDestinationChangedListener(navListener)

        // Mutable fields and observers
        var getCorrectWord = ""
        var mediaPlayer: MediaPlayer? = null

        // Show/hide hint depending on difficulty
        hintButton.visibility = when(viewModel.difficulty) {
            Difficulty.EASY -> View.VISIBLE
            Difficulty.OTHER -> View.VISIBLE
            else -> View.GONE
        }

        // Word information
        viewModel.wordLiveData.observe(viewLifecycleOwner, Observer(fun(word) {
            viewModel.listOfWords.add(word)
            getCorrectWord = word.id
            val infoList = mutableListOf<InfoBox>()
            // Decorators for infobox, start with part of speech and definition only
            val infoBoxBase = GameSessionInfoBox(requireContext())
            // Add usage for medium difficulty
            val infoBoxWithUsage = InfoBoxUsage(infoBoxBase, requireContext())
            // Add origin for hard difficulty
            val infoBoxWithUsageEtymology = InfoBoxOrigin(infoBoxWithUsage, requireContext())
            infoBox.text = when(viewModel.difficulty) {
                Difficulty.HARD -> infoBoxBase.getSpannable(word)
                Difficulty.MEDIUM -> infoBoxWithUsage.getSpannable(word)
                Difficulty.EASY -> infoBoxWithUsageEtymology.getSpannable(word)
                Difficulty.OTHER -> infoBoxBase.getSpannable(word)
            }
        }))

        // Pronunciation audio
        viewModel.pronunciationLiveData.observe(viewLifecycleOwner, Observer(fun(mp) {
            if(mediaPlayer == null) {
                mediaPlayer = mp
            }
        }))

        // Submit button state
        viewModel.submitLiveData.observe(viewLifecycleOwner, Observer {
            submitButton.text = it
        })
        viewModel.colorLiveData.observe(viewLifecycleOwner, Observer {
            when(it) {
                "green" -> {
                    submitButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.monokai_green))
                    snack?.setText("Correct")
                    snack?.view?.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.monokai_green))
                    snack?.show()
                }
                "red" -> {
                    submitButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.monokai_red))
                    val snackText = "The correct spelling is: $getCorrectWord"
                    val snackSpannable = SpannableString(snackText)
                    snackSpannable.setSpan(
                        StyleSpan(Typeface.BOLD),
                        snackText.length - getCorrectWord.length, snackText.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    snack?.setText(snackSpannable)
                    snack?.view?.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.monokai_red))
                    snack?.show()
                }
                else -> {
                    submitButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.submit_button_color))
                    // Dismiss correct / incorrect message
                    snack?.dismiss()
                }
            }
        })

        //TODO move following sample code to Category Selection screen
//        viewModel.categoryLiveData.observe(viewLifecycleOwner, Observer(fun(categories) {
//            println(categories)
//        }))

        // Listeners

        // Pronunciation button to replay audio
        pronunciationButton.setOnClickListener {
            try {
                mediaPlayer!!.start()
            }
            catch (e: Exception) {
                Toast.makeText(requireContext(), "Internal audio player error", Toast.LENGTH_SHORT).show()
            }
        }

        // Correct / incorrect message
        val snackTextView = snack?.view?.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        snackTextView?.setTextSize(TypedValue.COMPLEX_UNIT_SP,18F)
        // Submit Button to check if entered word is correct. Look in text field
        submitButton.setOnClickListener {
            // Submit button and input in non-empty
            if (submitButton.text == getString(R.string.submit) && mainWordField.text.isNotEmpty()) {
                val result = mainWordField.text.toString().equals(getCorrectWord, true)
                // Hide keyboard
                val imm = mainWordField.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(mainWordField.windowToken, 0)
                // Change to continue button
                viewModel.submitLiveData.value = getString(R.string.cont)
                // Correct / incorrect message
                if (result) {
                    viewModel.addCorrectWord(getCorrectWord)
                    viewModel.colorLiveData.value = "green"
                } else {
                    viewModel.addInCorrectWord(getCorrectWord)
                    viewModel.colorLiveData.value = "red"
                }
                viewModel.updateResults(getCorrectWord, result)
            }
            // Continue button
            else if (submitButton.text == getString(R.string.cont)) {
                // Reset word field
                mainWordField.text.clear()
                val remainingWords = viewModel.nextWord()
                // Change to submit button
                viewModel.submitLiveData.value = getString(R.string.submit)
                viewModel.colorLiveData.value = "yellow"
                //TODO if remainingWords == 0, change submitButton into transition to stats page
                if (remainingWords == 0) {
                    // start post session update logic
                    viewModel.postSessionUpdate()
                    // Navigate to next fragment
                    if(viewModel.showStats && viewModel.strategyChoice == GameStrategy.STANDARD) {
                        findNavController().navigate(R.id.action_fragment_game_session_to_postGameStatisticsFragment)
                    }
                    else {
                        findNavController().navigate(R.id.gameSessionFragment_to_mainMenuFragment)
                    }
                }
            }
        }

        // Get hint button
        hintButton.setOnClickListener {
            if(submitButton.text == getString(R.string.submit)) { // only show hint before submission
                snack?.setText("Hint: " + viewModel.getHint())
                snack?.view?.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.monokai_yellow))
                submitButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.monokai_yellow))
                snack?.show()
            }
        }

        return root
    }

    override fun onDetach() {
        super.onDetach()
        snack?.takeIf{it.isShown}?.dismiss()
    }

    override fun onDestroyView() {
        navController.removeOnDestinationChangedListener(navListener)
        super.onDestroyView()
    }
}