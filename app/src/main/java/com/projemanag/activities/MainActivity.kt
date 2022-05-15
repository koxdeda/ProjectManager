package com.projemanag.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.projemanag.R
import com.projemanag.adapters.BoardItemsAdapter
import com.projemanag.firebase.FirestoreClass
import com.projemanag.model.Board
import com.projemanag.model.User
import com.projemanag.utils.Constants
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_name.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header_main.*

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {


    private lateinit var mUserName: String

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)


        setupActionBar()

        nav_view.setNavigationItemSelectedListener(this)

        FirestoreClass().loadUserData(this, true)

        fab_create_board.setOnClickListener {
            val intent = Intent(this, CreateBoardActivity::class.java)
            intent.putExtra(Constants.NAME, mUserName)
            resultLauncher1.launch(intent)
        }
    }

    private var resultLauncher1 = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            FirestoreClass().getBoardsList(this)
        }else{
            Log.e("Cancelled", "Cancelled")
        }
    }

    fun populateBoardsListToUI(boardsList: ArrayList<Board>){
        hideProgressDialog()

        if(boardsList.size > 0){
            rv_boards_list.visibility = View.VISIBLE
            tv_no_boards.visibility = View.GONE

            rv_boards_list.layoutManager = LinearLayoutManager(this)
            rv_boards_list.setHasFixedSize(true)

            val adapter = BoardItemsAdapter(this, boardsList)
            rv_boards_list.adapter = adapter

            adapter.setOnClickListener(object: BoardItemsAdapter.OnClickListener{
                override fun onClick(position: Int, model: Board) {
                    startActivity(Intent(this@MainActivity, TaskListActivity::class.java))
                }
            })


        }else{
            rv_boards_list.visibility = View.GONE
            tv_no_boards.visibility = View.VISIBLE
        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            // A double back press function is added in Base Activity.
            doubleBackToExit()
        }
    }


    fun updateNavigationUserDetails(user: User, readBoardsList: Boolean){

        mUserName = user.name

        Glide
            .with(this)
            .load(user.image)
            .circleCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(iv_user_image)

        tv_username.text = user.name

        if(readBoardsList){
            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().getBoardsList(this)

        }
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {

        when (menuItem.itemId) {
            R.id.nav_my_profile -> {
                val intent = Intent(this,
                        MyProfileActivity::class.java)

                resultLauncher.launch(intent)
            }

            R.id.nav_sign_out -> {
                // Here sign outs the user from firebase in this device.
                FirebaseAuth.getInstance().signOut()

                // Send the user to the intro screen of the application.
                val intent = Intent(this, IntroActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)

        return true
    }

    private fun setupActionBar() {

        setSupportActionBar(toolbar_main_activity)
        toolbar_main_activity.setNavigationIcon(R.drawable.ic_action_navigation)


        toolbar_main_activity.setNavigationOnClickListener {
            toggleDrawer()
        }

    }



    private fun toggleDrawer() {

        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            drawer_layout.openDrawer(GravityCompat.START)
        }
    }


    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            FirestoreClass().loadUserData(this@MainActivity)
        }else{
            Log.e("Cancelled", "Cancelled")
        }
    }

    companion object{
        const val CREATE_BOARD_REQUEST_CODE: Int = 12
    }

}