package com.example.openfoodapp.screen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.openfoodapp.HomeActivity
import com.example.openfoodapp.R
import kotlinx.android.synthetic.main.signin_layout.*

class SignInActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signin_layout)

        singInButton.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.putExtra("user","user")
            startActivity(intent)
        }
    }
}