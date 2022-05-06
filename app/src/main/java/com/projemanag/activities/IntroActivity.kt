package com.projemanag.activities

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.projemanag.R
import kotlinx.android.synthetic.main.activity_intro.*

class IntroActivity : BaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        // This is used to hide the status bar and make the splash screen as a full screen activity.
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

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