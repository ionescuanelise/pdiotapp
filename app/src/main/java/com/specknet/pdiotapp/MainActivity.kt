package com.specknet.pdiotapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import android.widget.EditText


class MainActivity : AppCompatActivity() {

    lateinit var loginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_login)

        loginButton = findViewById(R.id.login)
        loginButton.setOnClickListener {
            val email: String =  (findViewById(R.id.username) as EditText).getText().toString()
            val password: String = (findViewById(R.id.password) as EditText).getText().toString()
            if(email != null && password != null)
                loginButton.setEnabled(true)
            val intent = Intent(this, HomePage::class.java)
            startActivity(intent)
        }

    }

}