package com.projemanag.activities

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.projemanag.R
import com.projemanag.adapters.MemberListItemsAdapter
import com.projemanag.firebase.FirestoreClass
import com.projemanag.model.Board
import com.projemanag.model.User
import com.projemanag.utils.Constants
import kotlinx.android.synthetic.main.activity_members.*
import kotlinx.android.synthetic.main.dialog_search_member.*

class MembersActivity : BaseActivity() {

    private lateinit var mBoardDetails: Board
    private lateinit var mAssignedMembersList: ArrayList<User>
    private var anyChangesDone: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_members)

        if (intent.hasExtra(Constants.BOARD_DETAIL)) {
            mBoardDetails = intent.getParcelableExtra(Constants.BOARD_DETAIL)!!
        }

        setupActionBar()

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getAssignedMembersListDetails(
            this@MembersActivity,
            mBoardDetails.assignedTo
        )
    }


    override fun onBackPressed() {
        if (anyChangesDone) {
            setResult(Activity.RESULT_OK)
        }
        super.onBackPressed()
    }



    private fun setupActionBar() {

        setSupportActionBar(toolbar_members_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back)
        }

        toolbar_members_activity.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.add_member, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar menu items
        when (item.itemId) {
            R.id.action_add_member -> {

                dialogSearchMember()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    fun setupMembersList(list: ArrayList<User>) {

        mAssignedMembersList = list

        hideProgressDialog()

        rv_members_list.layoutManager = LinearLayoutManager(this@MembersActivity)
        rv_members_list.setHasFixedSize(true)

        val adapter = MemberListItemsAdapter(this@MembersActivity, list)
        rv_members_list.adapter = adapter
    }


    private fun dialogSearchMember() {
        val dialog = Dialog(this)

        dialog.setContentView(R.layout.dialog_search_member)
        dialog.tv_add.setOnClickListener{

            val email = dialog.et_email_search_member.text.toString()

            if (email.isNotEmpty()) {
                dialog.dismiss()

                showProgressDialog(resources.getString(R.string.please_wait))
                FirestoreClass().getMemberDetails(this@MembersActivity, email)
            } else {
                showErrorSnackBar("Please enter members email address.")
                /*Toast.makeText(
                    this@MembersActivity,
                    "Please enter members email address.",
                    Toast.LENGTH_SHORT
                ).show()*/
            }
        }
        dialog.tv_cancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    fun memberDetails(user: User) {

        mBoardDetails.assignedTo.add(user.id)

        FirestoreClass().assignMemberToBoard(this@MembersActivity, mBoardDetails, user)
    }


    fun memberAssignSuccess(user: User) {
        hideProgressDialog()
        mAssignedMembersList.add(user)
        anyChangesDone = true
        setupMembersList(mAssignedMembersList)
    }
}