package com.projemanag.activities

import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.projemanag.R
import kotlinx.android.synthetic.main.activity_intro.*

class IntroActivity : BaseActivity() {


    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)


        val typeface: Typeface =
            Typeface.createFromAsset(assets, "carbon bl.ttf")
        tv_app_name.typeface = typeface

        btn_sign_in.setOnClickListener {

            startActivity(Intent(this@IntroActivity, SignInActivity::class.java))
        }

        btn_sign_up.setOnClickListener {

            startActivity(Intent(this@IntroActivity, SignUpActivity::class.java))
        }
    }
}