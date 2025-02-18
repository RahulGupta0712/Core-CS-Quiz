# **Core-CS-Quiz**  

## **Features:**  

1. **Authentication:** Supports Google Sign-In, Email Sign-Up, and Email Sign-In.  
2. **Quiz Subjects:** Offers four core CS subjects—Object-Oriented Programming (OOPS), Operating Systems (OS), Computer Networks (CN), and Database Management Systems (DBMS).  
3. **Question Bank:** Each subject currently contains 20 questions.  
4. **User Information:** Users are required to provide their Name, Age, and Country during registration.  
5. **Initial Coins:** Users start with **0 coins**.  
6. **Quiz Format:** Each quiz consists of **10 questions** randomly selected from the respective subject’s question bank.  
7. **Question Structure:** Each question has **four answer options**, with exactly **one correct answer**.  
8. **Timer Mechanism:** A **30-second countdown timer**, represented by a progress bar, automatically advances to the next question if no option is selected.  
9. **Progress Tracking:** A **dynamic progress bar** indicates the number of completed questions, helping users track their progress.  
10. **Answer Feedback Animation:** Upon selecting an answer, a visual animation indicates whether the choice was correct or incorrect.  
11. **Score & Points Calculation:** At the end of a quiz, users can view their score and points earned:  
    - If the score is **≥ 5**, the user earns **(score × 10) points**.  
    - If the score is **< 5**, no points are awarded.  
12. **Leaderboard:** Displays the top users, ranked by their **average score across all attempted quizzes**.  
13. **User Profile:** Users can view their stored information, including **name, email, password, age, and country**.  
14. **Quiz History:** Users can review their past quiz attempts, which include:  
    - **Quiz subject**  
    - **Date & time** of the attempt  
    - **Score achieved**  
    - **Points granted**  
15. **Profile Editing:** Users can edit their profile details. However, **password changes are restricted for Google Sign-In users**, as passwords are not stored for them.  
16. **Logout Option:** Users can securely log out from their accounts.  
17. **Country Selection:** A **custom, searchable spinner** allows users to quickly find their country by typing initials.  
18. **Navigation:**  
    - Utilizes **ViewPager** for seamless navigation between:  
      - **Home Fragment** (select subjects and start quizzes)  
      - **Leaderboard Fragment**  
      - **Profile Fragment** (user details and quiz history)  
    - A **Bottom Navigation View** is implemented for switching between these fragments.  
19. **Tech Stack:** The app is built using **Kotlin and XML**.  
20. **Database & Backend:**  
    - **Firebase Realtime Database** is used for storing **leaderboard data, user details, and quiz history**.  
    - **Firebase Firestore** is used to store **quiz questions** efficiently.  
