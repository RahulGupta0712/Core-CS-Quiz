package com.example.core_quiz.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.core_quiz.DataModel.QuestionData
import com.example.core_quiz.R
import com.example.core_quiz.databinding.FragmentAddQuestionBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import java.util.UUID


class FragmentAddQuestion : BottomSheetDialogFragment() {

    private val binding by lazy{
        FragmentAddQuestionBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // set up spinner
        val datalist = arrayOf("Operating Systems", "Object Oriented Programming", "Computer Networks", "Database Management Systems")
        val spinAdapter = ArrayAdapter(requireActivity(), android.R.layout.preference_category, datalist)
        binding.quizSubjectAdd.setAdapter(spinAdapter)
        binding.quizSubjectAdd.onClearClick {
            binding.quizSubjectAdd.clearSelectedText()
        }

        val db = FirebaseFirestore.getInstance()

        binding.saveAdd.setOnClickListener {
            db.collection(binding.quizSubjectAdd.text.toString())
                .add(
                    QuestionData(
                        binding.questionAdd.text.toString(),
                        binding.option1Add.text.toString(),
                        binding.option2Add.text.toString(),
                        binding.option3Add.text.toString(),
                        binding.option4Add.text.toString(),
                        binding.correctAnswerAdd.text.toString(), Math.random()
                    )
                )
                .addOnSuccessListener {
                    Toast.makeText(requireActivity(), "Question Added successfully", Toast.LENGTH_SHORT).show()
                    dismiss()
                }
                .addOnFailureListener {
                    Toast.makeText(requireActivity(), "Failed to add question", Toast.LENGTH_SHORT).show()
                    dismiss()
                }
        }

    }
}