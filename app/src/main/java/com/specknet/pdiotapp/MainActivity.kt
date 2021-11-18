package com.specknet.pdiotapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import android.widget.EditText
import com.google.android.material.snackbar.Snackbar


class MainActivity : AppCompatActivity() {

    lateinit var loginButton: Button
    lateinit var registerButton: Button

    private val activity = this@MainActivity

    private lateinit var inputValidation: InputValidation
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_login)

        databaseHelper = DatabaseHelper(activity)
        inputValidation = InputValidation(activity)

        loginButton = findViewById(R.id.btn_login)
        registerButton = findViewById(R.id.btn_register)

        registerButton.setOnClickListener {
            val intent = Intent(this, RegisterFragment::class.java)
            startActivity(intent)
        }

        loginButton.setOnClickListener {
            val email: String = (findViewById(R.id.et_email) as EditText).getText().toString()
            val password: String = (findViewById(R.id.et_password) as EditText).getText().toString()

            if (email != null && password != null && !android.util.Patterns.EMAIL_ADDRESS.matcher(
                    password
                ).matches() && verifyFromSQLite(email, password)
            ) {
                val intent = Intent(this, HomePage::class.java)
                startActivity(intent)
            } else {
                Snackbar.make(
                    it,
                    getString(R.string.error_valid_email_password),
                    Snackbar.LENGTH_LONG
                ).show()
            }

        }
    }

    private fun verifyFromSQLite(email: String, password: String): Boolean {
        return databaseHelper!!.checkUser(email, password);
    }

}