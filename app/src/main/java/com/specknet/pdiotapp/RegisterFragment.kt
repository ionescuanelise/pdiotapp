package com.specknet.pdiotapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar

class RegisterFragment: AppCompatActivity() {
    lateinit var registerButton: Button

    private val activity = this@RegisterFragment

    private lateinit var inputValidation: InputValidation
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        databaseHelper = DatabaseHelper(activity)
        inputValidation = InputValidation(activity)

        val name: String =  (findViewById(R.id.et_name) as EditText).getText().toString()
        val email: String =  (findViewById(R.id.et_email) as EditText).getText().toString()
        val password: String = (findViewById(R.id.et_password) as EditText).getText().toString()
        val password_two: String = (findViewById(R.id.et_repassword) as EditText).getText().toString()

        registerButton = findViewById(R.id.btn_register)

        registerButton.setOnClickListener {

            if (name == null) {
                Snackbar.make(it, getString(R.string.error_message_name), Snackbar.LENGTH_LONG).show()
                finish()
            }
            if (email == null) {
                Snackbar.make(it, getString(R.string.error_message_email), Snackbar.LENGTH_LONG).show()
                finish()
            }
            if (password == null) {
                Snackbar.make(it, getString(R.string.error_message_password), Snackbar.LENGTH_LONG).show()
                finish()
            }
            if (password_two == null) {
                Snackbar.make(it, getString(R.string.error_password_match), Snackbar.LENGTH_LONG).show()
                finish()
            }

            if (password.equals(password_two)) {
                if (postDataToSQLite(email, password, name)) {
                    Snackbar.make(it, getString(R.string.success_message), Snackbar.LENGTH_LONG)
                        .show()
                    finish()
                }
                else{
                    Snackbar.make(it, getString(R.string.error_email_exists), Snackbar.LENGTH_LONG).show()
                }
            }
            else {
                Snackbar.make(it, getString(R.string.error_password_match), Snackbar.LENGTH_LONG).show()
            }
        }

    }

    private fun postDataToSQLite(email: String, password: String, name: String) : Boolean {

        if (!databaseHelper!!.checkUser(email)) {
            var user = User(name = name,
                email = email,
                password = password
            )
            databaseHelper!!.addUser(user)
            return true
        }
        return false
    }

}