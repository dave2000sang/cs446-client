package com.spellingo.client_app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.asksira.dropdownview.DropDownView

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class PostGameStatisticsFragment : Fragment() {

    private val viewModel: GameSessionViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Links Widgets to Variables
        val root = inflater.inflate(R.layout.fragment_post_session_statistics, container, false)
        val correctCounter = root.findViewById<TextView>(R.id.sessionStatCorrectCounter)
        val incorrectCounter = root.findViewById<TextView>(R.id.sessionStatIncorrectCounter)
        val returnToMainMenuButton = root.findViewById<Button>(R.id.goBackToMenu)
        var dropDownView: DropDownView = root.findViewById(R.id.dropdownview)
        var dropDownViewInCorrect: DropDownView = root.findViewById(R.id.dropdownview2)
        val listOfTestedWords = viewModel.listOfWords
        val listOfWords = mutableListOf<String>()

        for (i in listOfTestedWords) {
            listOfWords.add(i.id)
        }

        // Setting Correct/Incorrect Word Counter
        correctCounter.text = viewModel.getCorrectWordList().size.toString()
        incorrectCounter.text = viewModel.getInCorrectWordList().size.toString()



        // Correct DropDown
        val dropDownCorrectList: MutableList<String> = mutableListOf()
        val getCorrectWordList = viewModel.getCorrectWordList()
        for (i in getCorrectWordList) {
            dropDownCorrectList.add(i)
        }
        dropDownView.setDropDownListItem(dropDownCorrectList)
        dropDownView.setOnClickListener {
        }

        // Incorrect DropDown
        val dropDownInCorrectList: MutableList<String> = mutableListOf()
        val getInCorrectWordList = viewModel.getInCorrectWordList()
        for (i in getInCorrectWordList) {
            dropDownInCorrectList.add(i)
        }
        dropDownViewInCorrect.setDropDownListItem(dropDownInCorrectList)
        dropDownViewInCorrect.setOnClickListener {
        }

        // Remove All Data from Previous Session
        viewModel.emptyCorrectWordList()
        viewModel.emptyInCorrectWordList()

        returnToMainMenuButton.setOnClickListener {
            findNavController().navigate(R.id.action_postGameStatisticsFragment_to_fragment_main_menu)
        }
        return root
    }
}