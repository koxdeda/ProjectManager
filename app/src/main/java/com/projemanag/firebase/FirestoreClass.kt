package com.projemanag.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.projemanag.activities.*
import com.projemanag.model.Board
import com.projemanag.model.User
import com.projemanag.utils.Constants

class FirestoreClass {

    private val mFireStore = FirebaseFirestore.getInstance()


    fun registerUser(activity: SignUpActivity, userInfo: User) {

        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {

                activity.userRegisteredSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error writing document",
                    e
                )
            }
    }


    fun getCurrentUserId(): String {
        val currentUser = FirebaseAuth.getInstance().currentUser

        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }

        return currentUserID
    }

    fun loadUserData(activity: Activity, readBoardsList: Boolean = false) {

        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->
                Log.e(activity.javaClass.simpleName, document.toString())

                val loggedInUser = document.toObject(User::class.java)!!

                when (activity) {
                    is SignInActivity -> {
                        activity.signInSuccess()
                    }
                    is MainActivity -> {
                        activity.updateNavigationUserDetails(loggedInUser, readBoardsList)
                    }
                    is MyProfileActivity -> {
                        activity.setUserDataInUI(loggedInUser)
                    }
                }
            }
            .addOnFailureListener { e ->
                // Here call a function of base activity for transferring the result to it.
                when (activity) {
                    is SignInActivity -> {
                        activity.hideProgressDialog()
                    }
                    is MainActivity -> {
                        activity.hideProgressDialog()
                    }
                    is MyProfileActivity -> {
                        activity.hideProgressDialog()
                    }
                }
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while getting loggedIn user details",
                    e
                )
            }
    }

    fun updateUserProfileData(activity: MyProfileActivity, userHashMap: HashMap<String, Any>) {
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .update(userHashMap)
            .addOnSuccessListener {
                Log.e(activity.javaClass.simpleName, "Profile Data updated successfully!")

                Toast.makeText(activity, "Profile updated successfully!", Toast.LENGTH_SHORT).show()

                activity.profileUpdateSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while creating a board.",
                    e
                )
            }
    }

    fun createBoard(activity: CreateBoardActivity, board: Board){
        mFireStore.collection(Constants.BOARDS)
            .document()
            .set(board, SetOptions.merge())
            .addOnSuccessListener {
                Log.e(
                    activity.javaClass.simpleName,
                    "Board created successfully",
                )
                activity.boardCreatedSuccessfully()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error writing document",
                    e
                )
            }
    }

    fun getBoardsList(activity: MainActivity){
        mFireStore.collection(Constants.BOARDS)
            .whereArrayContains(Constants.ASSIGNED_TO, getCurrentUserId())
            .get()
            .addOnSuccessListener {
                document ->
                Log.i(activity.javaClass.simpleName, document.documents.toString())
                val boardsList: ArrayList<Board> = ArrayList()
                for (i in document.documents){
                    val board = i.toObject(Board::class.java)!!
                    board.documentId = i.id
                    boardsList.add(board)
                }

                activity.populateBoardsListToUI(boardsList)
            }
            .addOnFailureListener {
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error getting boards")
            }
    }

    fun getBoardDetails(activity: TaskListActivity, documentId: String){
        mFireStore.collection(Constants.BOARDS)
            .document(documentId)
            .get()
            .addOnSuccessListener {
                    document ->
                Log.i(activity.javaClass.simpleName, document.toString())
                val board = document.toObject(Board::class.java)!!
                board.documentId = document.id

                activity.boardDetails(board)
            }
            .addOnFailureListener {
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error getting boards")
            }
    }


    fun addUpdateTaskList(activity: Activity, board: Board){
        val taskListHashMap = HashMap<String, Any>()
        taskListHashMap[Constants.TASK_LIST] = board.taskList

        mFireStore.collection(Constants.BOARDS)
            .document(board.documentId)
            .update(taskListHashMap)
            .addOnSuccessListener {
                Log.e(activity.javaClass.simpleName, "Profile Data updated successfully!")
                if(activity is TaskListActivity) {
                    activity.addUpdateTaskListSuccess()
                }
                else if(activity is CardDetailsActivity) {
                    activity.addUpdateTaskListSuccess()
                }
            }
            .addOnFailureListener { e->
                if(activity is TaskListActivity) {
                    activity.hideProgressDialog()
                }
                if(activity is CardDetailsActivity) {
                    activity.hideProgressDialog()
                }
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while updating task list.",
                    e
                )
            }
    }

    fun getAssignedMembersListDetails(
        activity: MembersActivity, assignedTo: ArrayList<String>){
        mFireStore.collection(Constants.USERS)
            .whereIn(Constants.USER_ID, assignedTo)
            .get()
            .addOnSuccessListener {
                document ->
                Log.e(activity.javaClass.simpleName, document.documents.toString())

                val usersList: ArrayList<User> = ArrayList()

                for (i in document.documents){
                    Log.i("test", i.toString())
                    val user = i.toObject(User::class.java)!!
                    usersList.add(user)
                }
                activity.setupMembersList(usersList)
            }
            .addOnFailureListener {e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while getting assigned members list",
                    e)
            }
    }

    fun getMemberDetails(activity: MembersActivity, email: String){
        mFireStore.collection(Constants.USERS)
            .whereEqualTo(Constants.EMAIL, email)
            .get()
            .addOnSuccessListener {
                document ->
                if(document.documents.size > 0){
                    val user = document.documents[0].toObject(User::class.java)!!
                    activity.memberDetails(user)
                }else{
                    activity.hideProgressDialog()
                    activity.showErrorSnackBar("No such member found")
                }
            }
            .addOnFailureListener {e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error find user details",
                    e)
            }
    }

    fun assignMemberToBoard(
        activity: MembersActivity, board: Board, user: User){

        val assignedToHashmap = HashMap<String, Any>()
        assignedToHashmap[Constants.ASSIGNED_TO] = board.assignedTo

        mFireStore.collection(Constants.BOARDS)
            .document(board.documentId)
            .update(assignedToHashmap)
            .addOnSuccessListener {
                activity.memberAssignSuccess(user)
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while creating a board",
                    e)
            }
    }


}