package com.example.openfoodapp.screen

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.openfoodapp.R
import com.example.openfoodapp.utils.FireBase
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.signup_layout.*
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class SignUpActivity : AppCompatActivity() {

    private var pictureUrl: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup_layout)

        upload_picture_button.setOnClickListener {
            openGalleryForImage()
        }

        confirm_button_sign_up.setOnClickListener {
            createAccount(et_email_sign_up.text.toString(),et_password_sign_up.text.toString(),et_name_sign_up.text.toString(),pictureUrl)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 100){
            pictureUrl = data?.data
            imageview_sign_up.setImageURI(pictureUrl)
        }
    }

    private fun createAccount(email: String, password: String,name: String, picturePath: Uri?) {
        if (email == "" || password == "" || name == ""){
            Toast.makeText(baseContext, "Name/Email/Password is empty.",
                Toast.LENGTH_SHORT).show()
        }else {
            progress_bar_sign_up.visibility = View.VISIBLE
            Firebase.auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d("TAG", "createUserWithEmail:success")
                        GlobalScope.launch(Dispatchers.Main) {

                            Firebase.auth.currentUser?.let { FireBase().setUserCreated(it.uid, name) }
                            if (picturePath != null) {
                                val pictureUri =
                                    Firebase.auth.currentUser?.let { FireBase().addUserInfo(it.uid, picturePath, name) }
                                Log.d("TAG URI", "createAccount: $pictureUri")
                                val profileUpdates =
                                    UserProfileChangeRequest.Builder().setDisplayName(name)
                                        .setPhotoUri(pictureUri).build()
                                Firebase.auth.currentUser?.updateProfile(profileUpdates)
                            } else {

                                val profileUpdates =
                                    UserProfileChangeRequest.Builder().setDisplayName(name).build()
                                Firebase.auth.currentUser?.updateProfile(profileUpdates)
                            }
                            progress_bar_sign_up.visibility = View.INVISIBLE

                            val intent = Intent(this@SignUpActivity, TutorialActivity::class.java)
                            startActivity(intent)
                        }
                    } else {
                        progress_bar_sign_up.visibility = View.INVISIBLE
                        Log.w("TAG", "createUserWithEmail:failure", task.exception)
                        Toast.makeText(
                            baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }

    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 100)
    }

}