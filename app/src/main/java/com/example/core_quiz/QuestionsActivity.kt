package com.example.core_quiz

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.core_quiz.CountdownTimer.Timer
import com.example.core_quiz.DataModel.QuestionData
import com.example.core_quiz.databinding.ActivityQuestionsBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject
import com.shashank.sony.fancytoastlib.FancyToast
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.sql.Time

class QuestionsActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityQuestionsBinding.inflate(layoutInflater)
    }

    private lateinit var questionsList: List<QuestionData>
    var currQuestionIndex = -1
    var selectedOption = ""
    var score = 0
    private var subject = ""
    private lateinit var timer:Timer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.group.visibility = View.GONE
        binding.progressBar3.visibility = View.VISIBLE

//        addQuestions()

        timer = Timer(30*1000, 1000, binding)

        val image = intent.getIntExtra("quiz subject image", R.drawable.background_transparent)
        subject = intent.getStringExtra("quiz subject") ?: ""

        binding.quizSubjectImage.setImageResource(image)
        binding.quizSubject.text = "$subject Quiz"

        lifecycleScope.launch {
            questionsList = fetchRandomDocuments(subject)

            if (questionsList.size >= 10) {
                updateDetails()
                binding.group.visibility = View.VISIBLE
                binding.correctAnimation.visibility=View.GONE
                binding.wrongAnimation.visibility=View.GONE
                binding.progressBar3.visibility = View.GONE
            }
        }

        binding.nextButton.setOnClickListener {
            if(timeUp){
                timer.cancel()
                FancyToast.makeText(this, "Time's up!", FancyToast.LENGTH_SHORT, FancyToast.DEFAULT, false).show()
                updateDetails()
                timeUp=false
            }
            else if (selectedOption == "") {
                // no option is selected, we will now allow user to skip a question without selecting an option
                FancyToast.makeText(this, "Choose 1 option!", FancyToast.LENGTH_SHORT, FancyToast.WARNING, false).show()
            } else {
                timer.cancel()
                binding.nextButton.isEnabled = false
                if (selectedOption == questionsList[currQuestionIndex].correctAns) {
                    binding.correctAnimation.visibility = View.VISIBLE
                    binding.correctAnimation.playAnimation()
                    score++
                } else {
                    binding.wrongAnimation.visibility = View.VISIBLE
                    binding.wrongAnimation.playAnimation()
                }

                Handler(Looper.getMainLooper()).postDelayed({
                    binding.correctAnimation.visibility = View.GONE
                    binding.wrongAnimation.visibility = View.GONE

                    updateDetails()
                }, 2000)
            }

        }

        binding.option1.setOnClickListener {
            selectedOption = binding.option1.text.toString()

            // change color
            binding.option1.setBackgroundColor(ContextCompat.getColor(this, R.color.yellow))
            binding.option2.setBackgroundColor(ContextCompat.getColor(this, R.color.floral_white))
            binding.option3.setBackgroundColor(ContextCompat.getColor(this, R.color.floral_white))
            binding.option4.setBackgroundColor(ContextCompat.getColor(this, R.color.floral_white))
        }
        binding.option2.setOnClickListener {
            selectedOption = binding.option2.text.toString()
            binding.option1.setBackgroundColor(ContextCompat.getColor(this, R.color.floral_white))
            binding.option2.setBackgroundColor(ContextCompat.getColor(this, R.color.yellow))
            binding.option3.setBackgroundColor(ContextCompat.getColor(this, R.color.floral_white))
            binding.option4.setBackgroundColor(ContextCompat.getColor(this, R.color.floral_white))
        }
        binding.option3.setOnClickListener {
            selectedOption = binding.option3.text.toString()
            binding.option1.setBackgroundColor(ContextCompat.getColor(this, R.color.floral_white))
            binding.option2.setBackgroundColor(ContextCompat.getColor(this, R.color.floral_white))
            binding.option3.setBackgroundColor(ContextCompat.getColor(this, R.color.yellow))
            binding.option4.setBackgroundColor(ContextCompat.getColor(this, R.color.floral_white))
        }
        binding.option4.setOnClickListener {
            selectedOption = binding.option4.text.toString()
            binding.option1.setBackgroundColor(ContextCompat.getColor(this, R.color.floral_white))
            binding.option2.setBackgroundColor(ContextCompat.getColor(this, R.color.floral_white))
            binding.option3.setBackgroundColor(ContextCompat.getColor(this, R.color.floral_white))
            binding.option4.setBackgroundColor(ContextCompat.getColor(this, R.color.yellow))
        }


    }

    private fun updateDetails() {
        if (currQuestionIndex == 9) {
            // this was the last question
            // move to score activity
            var intent = Intent(this, ScoreActivity::class.java)
            intent.putExtra("score", score)
            intent.putExtra("subject", subject)
            startActivity(intent)
            finish()
            return;
        } else if (currQuestionIndex == 8) {
            // next question is last one
            // change text of "next question"
            binding.nextButton.text = "Finish"
        }

        currQuestionIndex++

        binding.progressBar.progress = currQuestionIndex * 10

        selectedOption = ""
        binding.option1.setBackgroundColor(ContextCompat.getColor(this, R.color.floral_white))
        binding.option2.setBackgroundColor(ContextCompat.getColor(this, R.color.floral_white))
        binding.option3.setBackgroundColor(ContextCompat.getColor(this, R.color.floral_white))
        binding.option4.setBackgroundColor(ContextCompat.getColor(this, R.color.floral_white))

        binding.question.text = questionsList[currQuestionIndex].question
        binding.option1.text = questionsList[currQuestionIndex].option1
        binding.option2.text = questionsList[currQuestionIndex].option2
        binding.option3.text = questionsList[currQuestionIndex].option3
        binding.option4.text = questionsList[currQuestionIndex].option4

        binding.nextButton.isEnabled = true
        timer.start()
    }

    private suspend fun fetchRandomDocuments(subject: String): List<QuestionData> {
        // made this function suspend because, it will suspend further execution, till it get the list details
        val db = FirebaseFirestore.getInstance()
        val randomValue = Math.random() // used to fetch 10 random questions
        val results = ArrayList<QuestionData>()

        // currently, we have 20 questions for each subject, so we are fetching total 15 here, and then will take 10 out of them in the end

        var LIMIT = 15L

        var cnt = 0

        // First query: Get documents >= randomValue
        val firstQuery = db.collection(subject)
            .whereGreaterThanOrEqualTo("randomIndex", randomValue)
            .orderBy("randomIndex")
            .limit(LIMIT)
            .get()
            .await() // will noe move ahead till this task is completed

        results.addAll(firstQuery.toObjects(QuestionData::class.java))

        // Second query (if needed): Get remaining documents from the lower half
        if (results.size < LIMIT) {
            val remaining = LIMIT - results.size
            val secondQuery = db.collection(subject)
                .whereLessThan("randomIndex", randomValue)
                .orderBy("randomIndex", Query.Direction.DESCENDING)
                .limit(remaining)
                .get()
                .await()

            results.addAll(secondQuery.toObjects(QuestionData::class.java))

        }


        return results.shuffled().take(10)
    }

    companion object{
        var timeUp = false
    }


//    private fun addQuestions() {
//        val db = FirebaseFirestore.getInstance()
//        db.collection("Operating Systems").add(
//            QuestionData(
//                "Which type of operating system is designed for real-time applications with strict timing constraints?",
//                "Batch operating system",
//                "Time-sharing operating system",
//                "Real-time operating system",
//                "Distributed operating system",
//                "Real-time operating system",
//                Math.random()
//            )
//        )
//        db.collection("Operating Systems").add(
//            QuestionData(
//                "What is the role of device drivers in an operating system?",
//                "To manage user input/output",
//                "To provide an interface between the operating system and hardware devices",
//                "To control memory allocation",
//                "To schedule processes",
//                "To provide an interface between the operating system and hardware devices",
//                Math.random()
//            )
//        )
//        db.collection("Operating Systems").add(
//            QuestionData(
//                "What is the purpose of a system call?",
//                "To allow user programs to access hardware directly",
//                "To provide an interface between user programs and the operating system kernel",
//                "To manage memory allocation",
//                "To schedule processes",
//                "To provide an interface between user programs and the operating system kernel",
//                Math.random()
//            )
//        )
//        db.collection("Operating Systems").add(
//            QuestionData(
//                "Which scheduling algorithm prioritizes processes based on their CPU burst time?",
//                "First-Come, First-Served (FCFS)",
//                "Shortest Job First (SJF)",
//                "Priority Scheduling",
//                "Round Robin",
//                "Shortest Job First (SJF)",
//                Math.random()
//            )
//        )
//        db.collection("Operating Systems").add(
//            QuestionData(
//                "What is the purpose of a shell in an operating system?",
//                "To manage memory",
//                "To provide a command-line interface for users",
//                "To schedule processes",
//                "To control I/O devices",
//                "To provide a command-line interface for users",
//                Math.random()
//            )
//        )
//        db.collection("Operating Systems").add(
//            QuestionData(
//                "Which memory management technique divides memory into fixed-size blocks?",
//                "Paging",
//                "Segmentation",
//                "Swapping",
//                "Overlaying",
//                "Paging",
//                Math.random()
//            )
//        )
//        db.collection("Operating Systems").add(
//            QuestionData(
//                "What is the difference between a process and a thread?",
//                "A process is a single unit of work, while a thread is a smaller unit of work within a process.",
//                "A process is a collection of threads, while a thread is a single unit of work.",
//                "A process and a thread are synonymous terms.",
//                "A process is a hardware resource, while a thread is a software resource.",
//                "A process is a single unit of work, while a thread is a smaller unit of work within a process.",
//                Math.random()
//            )
//        )
//        db.collection("Operating Systems").add(
//            QuestionData(
//                "What is the role of the I/O subsystem in an operating system?",
//                "To manage user input/output",
//                "To provide an interface between the operating system and hardware devices",
//                "To control memory allocation",
//                "To schedule processes",
//                "To manage user input/output",
//                Math.random()
//            )
//        )
//        db.collection("Operating Systems").add(
//            QuestionData(
//                "What is virtualization?",
//                "Running multiple operating systems on a single physical machine",
//                "Running multiple applications within a single operating system instance",
//                "Emulating a physical machine using software",
//                "Isolating applications from each other",
//                "Running multiple operating systems on a single physical machine",
//                Math.random()
//            )
//        )
//        db.collection("Operating Systems").add(
//            QuestionData(
//                "What is the purpose of a cache in an operating system?",
//                "To store frequently accessed data in a faster memory location",
//                "To reduce the number of disk accesses",
//                "To improve data locality",
//                "All of the above",
//                "All of the above",
//                Math.random()
//            )
//        )
//        db.collection("Operating Systems").add(
//            QuestionData(
//                "Which of the following is NOT a type of kernel architecture?",
//                "Monolithic kernel",
//                "Microkernel",
//                "Hybrid kernel",
//                "Network kernel",
//                "Network kernel",
//                Math.random()
//            )
//        )
//        db.collection("Operating Systems").add(
//            QuestionData(
//                "What are interrupts?",
//                "Signals sent by the CPU to the operating system",
//                "Signals sent by devices to the CPU",
//                "Signals sent by the operating system to devices",
//                "Signals sent by processes to the operating system",
//                "Signals sent by devices to the CPU",
//                Math.random()
//            )
//        )
//        db.collection("Operating Systems").add(
//            QuestionData(
//                "What is the purpose of memory mapping?",
//                "To allow direct access to files in memory",
//                "To reduce the number of system calls",
//                "To improve cache utilization",
//                "All of the above",
//                "All of the above",
//                Math.random()
//            )
//        )
//        db.collection("Operating Systems").add(
//            QuestionData(
//                "Which storage device provides the fastest read/write speeds?",
//                "Hard disk drive (HDD)",
//                "Solid-state drive (SSD)",
//                "Random Access Memory (RAM)",
//                "Read-Only Memory (ROM)",
//                "Random Access Memory (RAM)",
//                Math.random()
//            )
//        )
//
//        db.collection("Operating Systems").add(QuestionData(
//            "What are the three primary states of a process in OS scheduling?",
//            "New, Running, Terminated",
//            "Ready, Running, Waiting",
//            "Active, Inactive, Zombie",
//            "User, Kernel, System",
//            "Ready, Running, Waiting",
//            Math.random()
//        ))
//
//        db.collection("Operating Systems").add(QuestionData(
//            "What is the main purpose of virtual memory?",
//            "To increase RAM speed",
//            "To allow processes to use more memory than physically available",
//            "To protect the kernel from user processes",
//            "To manage CPU cache efficiently",
//            "To allow processes to use more memory than physically available",
//            Math.random()
//        ))
//
//        db.collection("Operating Systems").add(QuestionData(
//            "Which scheduling algorithm uses time slices (quantums)?",
//            "First-Come, First-Served (FCFS)",
//            "Shortest Job Next (SJN)",
//            "Round Robin (RR)",
//            "Priority Scheduling",
//            "Round Robin (RR)",
//            Math.random()
//        ))
//
//        db.collection("Operating Systems").add(QuestionData(
//            "What is the primary purpose of a mutex lock?",
//            "Memory allocation",
//            "Process synchronization",
//            "File management",
//            "Network communication",
//            "Process synchronization",
//            Math.random()
//        ))
//
//        db.collection("Operating Systems").add(QuestionData(
//            "What does an inode store in Unix-like file systems?",
//            "File content",
//            "File metadata",
//            "Directory structure",
//            "User permissions",
//            "File metadata",
//            Math.random()
//        ))
//
//        db.collection("Operating Systems").add(QuestionData(
//            "What is the purpose of DMA (Direct Memory Access)?",
//            "To allow CPU to access peripherals directly",
//            "To enable devices to access memory without CPU intervention",
//            "To manage virtual memory allocation",
//            "To synchronize process execution",
//            "To enable devices to access memory without CPU intervention",
//            Math.random()
//        ))
//
//        db.collection("Object Oriented Programming").add(
//            QuestionData(
//                "Which OOP concept refers to the bundling of data (attributes) and methods that operate on that data within a single unit?",
//                "Inheritance",
//                "Polymorphism",
//                "Encapsulation",
//                "Abstraction",
//                "Encapsulation",
//                Math.random()
//            )
//        )
//        db.collection("Object Oriented Programming").add(
//            QuestionData(
//                "What is the relationship between a class and an object?",
//                "A class is an instance of an object.",
//                "An object is an instance of a class.",
//                "A class and an object are the same thing.",
//                "There is no relationship between a class and an object.",
//                "An object is an instance of a class.",
//                Math.random()
//            )
//        )
//        db.collection("Object Oriented Programming").add(
//            QuestionData(
//                "Which type of inheritance allows a class to inherit from multiple parent classes?",
//                "Single inheritance",
//                "Multiple inheritance",
//                "Multilevel inheritance",
//                "Hierarchical inheritance",
//                "Multiple inheritance",
//                Math.random()
//            )
//        )
//        db.collection("Object Oriented Programming").add(
//            QuestionData(
//                "How is polymorphism achieved through method overriding?",
//                "By defining methods with the same name but different parameters in the same class",
//                "By defining methods with the same name and parameters in different classes",
//                "By creating multiple instances of the same class",
//                "By defining methods with different names in different classes",
//                "By defining methods with the same name and parameters in different classes",
//                Math.random()
//            )
//        )
//        db.collection("Object Oriented Programming").add(
//            QuestionData(
//                "What are access specifiers?",
//                "Keywords that control the visibility of class members",
//                "Methods that are used to access class members",
//                "Variables that are used to store the state of an object",
//                "None of the above",
//                "Keywords that control the visibility of class members",
//                Math.random()
//            )
//        )
//        db.collection("Object Oriented Programming").add(
//            QuestionData(
//                "What is an abstract class?",
//                "A class that cannot be instantiated",
//                "A class that has no methods",
//                "A class that is inherited from all other classes",
//                "A class that is used to create interfaces",
//                "A class that cannot be instantiated",
//                Math.random()
//            )
//        )
//        db.collection("Object Oriented Programming").add(
//            QuestionData(
//                "What is the purpose of a constructor?",
//                "To destroy an object",
//                "To initialize an object",
//                "To modify the state of an object",
//                "To inherit from another class",
//                "To initialize an object",
//                Math.random()
//            )
//        )
//        db.collection("Object Oriented Programming").add(
//            QuestionData(
//                "What is the purpose of a destructor?",
//                "To create an object",
//                "To initialize an object",
//                "To destroy an object",
//                "To inherit from another class",
//                "To destroy an object",
//                Math.random()
//            )
//        )
//        db.collection("Object Oriented Programming").add(
//            QuestionData(
//                "What is the difference between method overloading and method overriding?",
//                "Method overloading occurs within the same class, while method overriding occurs in different classes.",
//                "Method overloading occurs in different classes, while method overriding occurs within the same class.",
//                "Method overloading changes the return type of a method, while method overriding changes the method name.",
//                "Method overloading changes the access modifier of a method, while method overriding changes the method parameters.",
//                "Method overloading occurs within the same class, while method overriding occurs in different classes.",
//                Math.random()
//            )
//        )
//        db.collection("Object Oriented Programming").add(
//            QuestionData(
//                "What is the concept of encapsulation?",
//                "The ability of a class to inherit from another class",
//                "The ability of a class to take on many forms",
//                "The bundling of data (attributes) and methods that operate on that data within a single unit",
//                "The ability to create multiple instances of a class",
//                "The bundling of data (attributes) and methods that operate on that data within a single unit",
//                Math.random()
//            )
//        )
//        db.collection("Object Oriented Programming").add(
//            QuestionData(
//                "What is the purpose of an interface?",
//                "To define a set of methods that a class must implement",
//                "To create an instance of a class",
//                "To inherit from another class",
//                "To implement a specific algorithm",
//                "To define a set of methods that a class must implement",
//                Math.random()
//            )
//        )
//        db.collection("Object Oriented Programming").add(
//            QuestionData(
//                "What is the concept of abstraction?",
//                "Hiding the internal implementation details of a class",
//                "Creating multiple instances of a class",
//                "Inheriting from other classes",
//                "Overriding methods",
//                "Hiding the internal implementation details of a class",
//                Math.random()
//            )
//        )
//        db.collection("Object Oriented Programming").add(
//            QuestionData(
//                "Which of the following is NOT a SOLID principle?",
//                "Single Responsibility Principle",
//                "Open/Closed Principle",
//                "Liskov Substitution Principle",
//                "Dependency Injection Principle",
//                "Dependency Injection Principle",
//                Math.random()
//            )
//        )
//        db.collection("Object Oriented Programming").add(
//            QuestionData(
//                "What is the purpose of the this keyword?",
//                "To refer to the current object",
//                "To refer to the parent class",
//                "To refer to a static member",
//                "To refer to a local variable",
//                "To refer to the current object",
//                Math.random()
//            )
//        )
//        db.collection("Object Oriented Programming").add(
//            QuestionData(
//                "What is the difference between shallow and deep copying?",
//                "Shallow copy creates a new object, but the new object shares the same references to the original object's internal data.",
//                "Deep copy creates a new object and also copies all the internal data, creating independent copies of all the objects.",
//                "Shallow copy is faster than deep copy.",
//                "Deep copy is faster than shallow copy.",
//                "Deep copy creates a new object and also copies all the internal data, creating independent copies of all the objects.",
//                Math.random()
//            )
//        )
//        db.collection("Object Oriented Programming").add(
//            QuestionData(
//                "What is the purpose of the final keyword in Java?",
//                "To prevent a class from being inherited",
//                "To prevent a method from being overridden",
//                "To prevent a variable from being modified after initialization",
//                "All of the above",
//                "All of the above",
//                Math.random()
//            )
//        )
//        db.collection("Object Oriented Programming").add(
//            QuestionData(
//                "Explain the concept of early and late binding.",
//                "Early binding refers to resolving method calls at compile time, while late binding refers to resolving method calls at runtime.",
//                "Early binding refers to resolving method calls at runtime, while late binding refers to resolving method calls at compile time.",
//                "Early binding is used for static methods, while late binding is used for instance methods.",
//                "Early binding is used for instance methods, while late binding is used for static methods.",
//                "Early binding refers to resolving method calls at compile time, while late binding refers to resolving method calls at runtime.",
//                Math.random()
//            )
//        )
//        db.collection("Object Oriented Programming").add(
//            QuestionData(
//                "What are the advantages of using object-oriented programming?",
//                "Code reusability, modularity, and maintainability",
//                "Increased complexity",
//                "Decreased flexibility",
//                "Slower development time",
//                "Code reusability, modularity, and maintainability",
//                Math.random()
//            )
//        )
//        db.collection("Object Oriented Programming").add(
//            QuestionData(
//                "What is the purpose of exception handling?",
//                "To prevent errors from occurring",
//                "To gracefully handle unexpected events",
//                "To improve code readability",
//                "To increase code execution speed",
//                "To gracefully handle unexpected events",
//                Math.random()
//            )
//        )
//        db.collection("Object Oriented Programming").add(
//            QuestionData(
//                "What are some common design patterns in OOP?",
//                "Singleton, Factory, Observer",
//                "While loop, For loop, If-else",
//                "Data types like int, float, string",
//                "Inheritance, Polymorphism, Encapsulation",
//                "Singleton, Factory, Observer",
//                Math.random()
//            )
//        )
//
//        db.collection("Database Management Systems").add(
//            QuestionData(
//                "What is a DBMS?",
//                "A collection of related data",
//                "A software system used to create, manage, and retrieve data",
//                "A programming language for interacting with databases",
//                "A hardware device for storing data",
//                "A software system used to create, manage, and retrieve data",
//                Math.random()
//            )
//        )
//        db.collection("Database Management Systems").add(
//            QuestionData(
//                "Which of the following is NOT a type of database?",
//                "Relational database",
//                "NoSQL database",
//                "Hierarchical database",
//                "Network database",
//                "Network database",
//                Math.random()
//            )
//        )
//        db.collection("Database Management Systems").add(
//            QuestionData(
//                "What is the basic unit of data in a relational database?",
//                "Field",
//                "Record",
//                "Table",
//                "Schema",
//                "Field",
//                Math.random()
//            )
//        )
//        db.collection("Database Management Systems").add(
//            QuestionData(
//                "What does SQL stand for?",
//                "Structured Query Language",
//                "Simple Query Language",
//                "Sequential Query Language",
//                "Standard Query Language",
//                "Structured Query Language",
//                Math.random()
//            )
//        )
//        db.collection("Database Management Systems").add(
//            QuestionData(
//                "Which SQL command is used to retrieve data from a table?",
//                "INSERT",
//                "UPDATE",
//                "DELETE",
//                "SELECT",
//                "SELECT",
//                Math.random()
//            )
//        )
//        db.collection("Database Management Systems").add(
//            QuestionData(
//                "What is a primary key?",
//                "A unique identifier for each row in a table",
//                "A field that references a primary key in another table",
//                "A field that cannot be null",
//                "A field that is automatically generated by the database",
//                "A unique identifier for each row in a table",
//                Math.random()
//            )
//        )
//        db.collection("Database Management Systems").add(
//            QuestionData(
//                "What is a foreign key?",
//                "A unique identifier for each row in a table",
//                "A field that references a primary key in another table",
//                "A field that cannot be null",
//                "A field that is automatically generated by the database",
//                "A field that references a primary key in another table",
//                Math.random()
//            )
//        )
//        db.collection("Database Management Systems").add(
//            QuestionData(
//                "What is normalization?",
//                "The process of organizing data to minimize redundancy and improve data integrity",
//                "The process of creating indexes on tables",
//                "The process of backing up a database",
//                "The process of optimizing database queries",
//                "The process of organizing data to minimize redundancy and improve data integrity",
//                Math.random()
//            )
//        )
//        db.collection("Database Management Systems").add(
//            QuestionData(
//                "What are the ACID properties of transactions?",
//                "Atomicity, Consistency, Isolation, Durability",
//                "Accuracy, Consistency, Integrity, Dependability",
//                "Availability, Consistency, Isolation, Durability",
//                "Atomicity, Correctness, Integrity, Dependability",
//                "Atomicity, Consistency, Isolation, Durability",
//                Math.random()
//            )
//        )
//        db.collection("Database Management Systems").add(
//            QuestionData(
//                "What is a database index?",
//                "A copy of a database table",
//                "A data structure that allows for faster data retrieval",
//                "A set of rules that govern data access",
//                "A collection of related tables",
//                "A data structure that allows for faster data retrieval",
//                Math.random()
//            )
//        )
//        db.collection("Database Management Systems").add(
//            QuestionData(
//                "What is a database view?",
//                "A temporary copy of a table",
//                "A virtual table created from one or more tables",
//                "A stored procedure that performs a specific task",
//                "A database trigger that executes automatically",
//                "A virtual table created from one or more tables",
//                Math.random()
//            )
//        )
//        db.collection("Database Management Systems").add(
//            QuestionData(
//                "What are NoSQL databases?",
//                "Databases that use SQL for data manipulation",
//                "Databases that do not use the traditional relational model",
//                "Databases that are optimized for high-volume data processing",
//                "Databases that are stored on a single server",
//                "Databases that do not use the traditional relational model",
//                Math.random()
//            )
//        )
//        db.collection("Database Management Systems").add(
//            QuestionData(
//                "What is data warehousing?",
//                "The process of storing and managing large amounts of data for analysis",
//                "The process of extracting, transforming, and loading data into a data warehouse",
//                "The process of analyzing data to discover patterns and trends",
//                "The process of creating reports from data",
//                "The process of storing and managing large amounts of data for analysis",
//                Math.random()
//            )
//        )
//        db.collection("Database Management Systems").add(
//            QuestionData(
//                "What is data mining?",
//                "The process of extracting, transforming, and loading data into a data warehouse",
//                "The process of analyzing data to discover patterns and trends",
//                "The process of creating reports from data",
//                "The process of storing and managing large amounts of data for analysis",
//                "The process of analyzing data to discover patterns and trends",
//                Math.random()
//            )
//        )
//        db.collection("Database Management Systems").add(
//            QuestionData(
//                "What are the challenges of managing large databases?",
//                "High storage costs",
//                "Performance issues",
//                "Data security and privacy concerns",
//                "All of the above",
//                "All of the above",
//                Math.random()
//            )
//        )
//        db.collection("Database Management Systems").add(
//            QuestionData(
//                "What is the purpose of database constraints?",
//                "To ensure data integrity and consistency",
//                "To improve database performance",
//                "To enhance database security",
//                "To simplify database design",
//                "To ensure data integrity and consistency",
//                Math.random()
//            )
//        )
//        db.collection("Database Management Systems").add(
//            QuestionData(
//                "What are the different types of database relationships?",
//                "One-to-one, one-to-many, many-to-many",
//                "Parent-child, sibling, ancestor-descendant",
//                "Primary, foreign, unique",
//                "Insert, update, delete",
//                "One-to-one, one-to-many, many-to-many",
//                Math.random()
//            )
//        )
//        db.collection("Database Management Systems").add(
//            QuestionData(
//                "What are database transactions?",
//                "A sequence of operations that are treated as a single unit of work",
//                "A set of rules for accessing data",
//                "A collection of related tables",
//                "A program that interacts with a database",
//                "A sequence of operations that are treated as a single unit of work",
//                Math.random()
//            )
//        )
//        db.collection("Database Management Systems").add(
//            QuestionData(
//                "What is the purpose of database backup and recovery?",
//                "To prevent data loss due to hardware or software failures",
//                "To improve database performance",
//                "To enhance database security",
//                "To simplify database design",
//                "To prevent data loss due to hardware or software failures",
//                Math.random()
//            )
//        )
//        db.collection("Database Management Systems").add(
//            QuestionData(
//                "Which SQL command is used to modify existing data in a table?",
//                "INSERT",
//                "UPDATE",
//                "DELETE",
//                "SELECT",
//                "UPDATE",
//                Math.random()
//            )
//        )
//
//        db.collection("Computer Networks").add(
//            QuestionData(
//                "What is the primary function of the OSI model?",
//                "To define a standard for network communication",
//                "To implement network protocols",
//                "To build network hardware",
//                "To manage network traffic",
//                "To define a standard for network communication",
//                Math.random()
//            )
//        )
//        db.collection("Computer Networks").add(
//            QuestionData(
//                "Which layer of the OSI model is responsible for the logical addressing of devices?",
//                "Physical Layer",
//                "Data Link Layer",
//                "Network Layer",
//                "Transport Layer",
//                "Network Layer",
//                Math.random()
//            )
//        )
//        db.collection("Computer Networks").add(
//            QuestionData(
//                "What is the difference between TCP and UDP?",
//                "TCP is connection-oriented, while UDP is connectionless.",
//                "TCP is connectionless, while UDP is connection-oriented.",
//                "TCP is used for streaming media, while UDP is used for file transfer.",
//                "TCP is more reliable than UDP, but less efficient.",
//                "TCP is connection-oriented, while UDP is connectionless.",
//                Math.random()
//            )
//        )
//        db.collection("Computer Networks").add(
//            QuestionData(
//                "What is the role of a router in a network?",
//                "To connect devices on the same network segment",
//                "To forward data packets between different networks",
//                "To convert digital signals to analog signals",
//                "To provide network security",
//                "To forward data packets between different networks",
//                Math.random()
//            )
//        )
//        db.collection("Computer Networks").add(
//            QuestionData(
//                "What is the purpose of DNS?",
//                "To translate domain names into IP addresses",
//                "To encrypt network traffic",
//                "To manage network traffic",
//                "To provide network security",
//                "To translate domain names into IP addresses",
//                Math.random()
//            )
//        )
//        db.collection("Computer Networks").add(
//            QuestionData(
//                "What is the role of the Transport Layer in the OSI model?",
//                "To provide end-to-end communication between applications",
//                "To handle the physical transmission of data",
//                "To provide logical addressing",
//                "To manage network security",
//                "To provide end-to-end communication between applications",
//                Math.random()
//            )
//        )
//        db.collection("Computer Networks").add(
//            QuestionData(
//                "What is the role of the Application Layer in the OSI model?",
//                "To handle the physical transmission of data",
//                "To provide logical addressing",
//                "To enable application-level communication",
//                "To manage network security",
//                "To enable application-level communication",
//                Math.random()
//            )
//        )
//        db.collection("Computer Networks").add(
//            QuestionData(
//                "What is the difference between circuit-switched networks and packet-switched networks?",
//                "Circuit-switched networks establish a dedicated path for communication, while packet-switched networks break data into packets.",
//                "Circuit-switched networks break data into packets, while packet-switched networks establish a dedicated path for communication.",
//                "Circuit-switched networks are more efficient than packet-switched networks.",
//                "Circuit-switched networks are less reliable than packet-switched networks.",
//                "Circuit-switched networks establish a dedicated path for communication, while packet-switched networks break data into packets.",
//                Math.random()
//            )
//        )
//        db.collection("Computer Networks").add(
//            QuestionData(
//                "What is network congestion?",
//                "When there is too much traffic on a network",
//                "When a network device fails",
//                "When a network is not connected to the internet",
//                "When a network is not secure",
//                "When there is too much traffic on a network",
//                Math.random()
//            )
//        )
//        db.collection("Computer Networks").add(
//            QuestionData(
//                "What are the different types of network topologies?",
//                "Bus, star, ring, mesh",
//                "Client-server, peer-to-peer",
//                "Wired, wireless",
//                "LAN, WAN",
//                "Bus, star, ring, mesh",
//                Math.random()
//            )
//        )
//        db.collection("Computer Networks").add(
//            QuestionData(
//                "What is the purpose of a firewall?",
//                "To encrypt network traffic",
//                "To control network access",
//                "To translate domain names into IP addresses",
//                "To manage network traffic",
//                "To control network access",
//                Math.random()
//            )
//        )
//        db.collection("Computer Networks").add(
//            QuestionData(
//                "What are the different types of network media?",
//                "Copper cables, fiber optic cables, wireless",
//                "LAN, WAN, MAN",
//                "TCP, UDP, IP",
//                "Router, switch, hub",
//                "Copper cables, fiber optic cables, wireless",
//                Math.random()
//            )
//        )
//        db.collection("Computer Networks").add(
//            QuestionData(
//                "What is the role of a switch in a network?",
//                "To connect devices on the same network segment",
//                "To forward data packets between different networks",
//                "To convert digital signals to analog signals",
//                "To provide network security",
//                "To connect devices on the same network segment",
//                Math.random()
//            )
//        )
//        db.collection("Computer Networks").add(
//            QuestionData(
//                "What is IP addressing?",
//                "A unique identifier assigned to each device on a network",
//                "The process of translating domain names into IP addresses",
//                "The process of encrypting network traffic",
//                "The process of managing network traffic",
//                "A unique identifier assigned to each device on a network",
//                Math.random()
//            )
//        )
//        db.collection("Computer Networks").add(
//            QuestionData(
//                "What is the difference between IPv4 and IPv6?",
//                "IPv4 uses 32-bit addresses, while IPv6 uses 128-bit addresses.",
//                "IPv4 is more secure than IPv6.",
//                "IPv4 is more efficient than IPv6.",
//                "IPv4 is no longer in use.",
//                "IPv4 uses 32-bit addresses, while IPv6 uses 128-bit addresses.",
//                Math.random()
//            )
//        )
//        db.collection("Computer Networks").add(
//            QuestionData(
//                "What is network virtualization?",
//                "The process of creating multiple virtual networks on a single physical network",
//                "The process of encrypting network traffic",
//                "The process of translating domain names into IP addresses",
//                "The process of managing network traffic",
//                "The process of creating multiple virtual networks on a single physical network",
//                Math.random()
//            )
//        )
//        db.collection("Computer Networks").add(
//            QuestionData(
//                "What are the challenges of large-scale networks?",
//                "Maintaining network security",
//                "Managing network traffic congestion",
//                "Ensuring network reliability",
//                "All of the above",
//                "All of the above",
//                Math.random()
//            )
//        )
//        db.collection("Computer Networks").add(
//            QuestionData(
//                "What are the future trends in networking?",
//                "Software-Defined Networking (SDN)",
//                "Network Function Virtualization (NFV)",
//                "Internet of Things (IoT)",
//                "All of the above",
//                "All of the above",
//                Math.random()
//            )
//        )
//        db.collection("Computer Networks").add(
//            QuestionData(
//                "What is the impact of cloud computing on networking?",
//                "Increased demand for network bandwidth",
//                "Increased reliance on network virtualization",
//                "Increased need for network security",
//                "All of the above",
//                "All of the above",
//                Math.random()
//            )
//        )
//        db.collection("Computer Networks").add(
//            QuestionData(
//                "What is the role of network protocols?",
//                "To define the rules for communication between devices on a network",
//                "To manage network traffic",
//                "To encrypt network traffic",
//                "To translate domain names into IP addresses",
//                "To define the rules for communication between devices on a network",
//                Math.random()
//            )
//        )
//    }
}