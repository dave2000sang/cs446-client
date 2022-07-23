package com.spellingo.client_app

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Typeface
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
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
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar


/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class GameSessionFragment : Fragment() {

    private val viewModel: GameSessionViewModel by activityViewModels()
    private var snack: Snackbar? = null
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var navController: NavController
    private val navListener = NavController.OnDestinationChangedListener { _, destination, _ ->
        // Have we just navigated to game session fragment?
        if(viewModel.previousDestination != destination.id
            && destination.id == R.id.fragment_game_session) {
            // Reset livedata
            viewModel.resetLiveData()

            // Are we doing word of the day?
            if(arguments != null && arguments!!.getBoolean("wotd")) {
                viewModel.updateStrategy(GameStrategy.WOTD)
            }
            else {
                viewModel.updateStrategy(GameStrategy.STANDARD)
            }
            // Get selected category
            val category = if(arguments != null && arguments!!.getString("category") != null) {
                arguments!!.getString("category")!!
            }
            else {
                "standard"
            }
            // Get selected difficulty
            val difficulty = if(arguments != null && arguments!!.getString("difficulty") != null) {
                Difficulty.getByName(arguments!!.getString("difficulty")!!)
            }
            else {
                Difficulty.OTHER
            }
            // Get sudden death mode
            val suddenDeath = if(arguments != null && arguments!!.getString("suddenDeath") != null) {
                SuddenDeathMode.getByName(arguments!!.getString("suddenDeath")!!)
            }
            else {
                SuddenDeathMode.STANDARD
            }
            // Start the session by fetching words
            viewModel.startSession(category, difficulty, suddenDeath)
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

        // Shared preferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())

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
            if(word == null) return
            getCorrectWord = word.id
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
            if(mainWordField.requestFocus()) {
                val imm = mainWordField.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(mainWordField, InputMethodManager.SHOW_IMPLICIT)
            }
        }))

        // Pronunciation audio
        viewModel.pronunciationLiveData.observe(viewLifecycleOwner, Observer(fun(mp) {
            if(mediaPlayer == null) {
                mediaPlayer = mp
            }
        }))

        // Submit button state
        viewModel.submitLiveData.observe(viewLifecycleOwner, Observer(fun(it) {
            if(it == null) return
            submitButton.text = it
        }))
        viewModel.colorLiveData.observe(viewLifecycleOwner, Observer(fun(it) {
            if(it == null) return
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
        }))

        // Listeners

        // Pronunciation button to replay audio
        pronunciationButton.setOnClickListener {
            try {
                val millis: Long = 200
                object : CountDownTimer(millis, millis) {
                    override fun onTick(p0: Long) {}
                    override fun onFinish() {
                        mediaPlayer!!.start()
                    }
                }.start()
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
                val attempt = mainWordField.text.toString().lowercase()
                // Hide keyboard
                val imm = mainWordField.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(mainWordField.windowToken, 0)
                // Change to continue button
                viewModel.submitLiveData.value = getString(R.string.cont)
                // Correct / incorrect message
                if (attempt == getCorrectWord) {
                    viewModel.colorLiveData.value = "green"
                } else {
                    viewModel.colorLiveData.value = "red"
                }
                viewModel.updateResults(getCorrectWord, attempt)
            }
            // Continue button
            else if (submitButton.text == getString(R.string.cont)) {
                val death = viewModel.colorLiveData.value == "red" && viewModel.suddenDeath != SuddenDeathMode.STANDARD
                // Reset word field
                mainWordField.text.clear()
                // Change to submit button
                viewModel.submitLiveData.value = getString(R.string.submit)
                viewModel.colorLiveData.value = "yellow"
                // Death in sudden death
                if (death) {
                    endSession()
                }
                // Continue logic
                else {
                    // Get next word
                    val remainingWords = viewModel.nextWord()
                    // change submitButton into transition to stats page
                    if (remainingWords == 0) {
                        when(viewModel.suddenDeath) {
                            // End session and go to next page
                            SuddenDeathMode.STANDARD -> endSession()
                            // Get more words
                            else -> viewModel.startSession(
                                viewModel.category,
                                viewModel.difficulty,
                                SuddenDeathMode.SD_CONTINUE
                            )
                        }
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

    private fun endSession() {
        // start post session update logic
        viewModel.postSessionUpdate()
        // Navigate to next fragment
        if(sharedPreferences.getBoolean("show_statistics", true) && viewModel.strategyChoice == GameStrategy.STANDARD) {
            findNavController().navigate(R.id.action_fragment_game_session_to_postGameStatisticsFragment)
        }
        else {
            findNavController().navigate(R.id.gameSessionFragment_to_mainMenuFragment)
        }
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