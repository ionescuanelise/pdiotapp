package com.specknet.pdiotapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import android.widget.EditText


class MainActivity : AppCompatActivity() {

    lateinit var loginButton: Button
    lateinit var registerButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_login)

        loginButton = findViewById(R.id.btn_login)
        registerButton = findViewById(R.id.swipe_register)

        loginButton.setOnClickListener {
            val email: String =  (findViewById(R.id.et_email) as EditText).getText().toString()
            val password: String = (findViewById(R.id.et_email) as EditText).getText().toString()
            if(email != null && password != null){
                val intent = Intent(this, HomePage::class.java)
                startActivity(intent)
            }
        }

        registerButton.setOnClickListener {
            val intent = Intent(this, RegisterFragment::class.java)
            startActivity(intent)
        }

    }

}