package com.example.openfoodapp.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.openfoodapp.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import android.graphics.BitmapFactory

import android.net.Uri
import android.widget.*
import com.example.openfoodapp.utils.FireBase
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.URL


class ProfileFragment : Fragment() {

    lateinit var imageProfile : ImageView
    private var pictureUrl: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view :View = inflater.inflate(R.layout.fragment_profile, container, false)


        val etName = view.findViewById<EditText>(R.id.et_name_profile)
        etName.setText(Firebase.auth.currentUser?.displayName)
        val etEmail = view.findViewById<EditText>(R.id.et_email_profile)
        etEmail.setText(Firebase.auth.currentUser?.email)
        val etPassword = view.findViewById<EditText>(R.id.et_password_profile)
        val tvExperience = view.findViewById<TextView>(R.id.tv_experience_profile)
        val progressBar = view.findViewById<ProgressBar>(R.id.progress_bar_profile)
        GlobalScope.launch(Dispatchers.Main) {
            val experience = Firebase.auth.currentUser?.let { it1 -> FireBase().getUserExperience(it1.uid) }
            tvExperience.text = "Experience : ${experience.toString()} XP"
        }

        val imageProfile = view.findViewById<ImageView>(R.id.imageView_profile)
        Log.d("TAG", "onCreateView: ${Firebase.auth.currentUser?.photoUrl.toString()}")
        val url = URL("${Firebase.auth.currentUser?.photoUrl.toString()}?alt=media")
        val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())
        imageProfile.setImageBitmap(bmp)

        val uploadButton = view.findViewById<Button>(R.id.upload_picture_button_profile)
        uploadButton.setOnClickListener {
            openGalleryForImage()
        }
        val applyChangeButton = view.findViewById<Button>(R.id.apply_change_button_profile)
        applyChangeButton.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            if (etEmail.text.toString() != ""|| etPassword.text.toString() != ""||etName.text.toString() != ""){
                GlobalScope.launch(Dispatchers.Main) {
                    pictureUrl?.let { pictureUri ->
                        Firebase.auth.currentUser?.uid?.let { uid ->
                            val pictureforFirebase = FireBase().addUserInfo(
                                uid,
                                pictureUri,
                                etName.text.toString()
                            )
                            val profileUpdates =
                                UserProfileChangeRequest.Builder()
                                    .setDisplayName(etName.text.toString())
                                    .setPhotoUri(pictureforFirebase)
                                    .build()
                            Firebase.auth.currentUser?.updateProfile(profileUpdates)
                            Firebase.auth.currentUser?.updateEmail(etEmail.text.toString())
                            Firebase.auth.currentUser?.updatePassword(etPassword.text.toString())
                        }
                    }
                    progressBar.visibility = View.INVISIBLE
                }
            }else{
                Toast.makeText(activity,"Complete every field",Toast.LENGTH_LONG).show()
                progressBar.visibility = View.INVISIBLE
            }

        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 100 && null != data){
            pictureUrl = data.data
            imageProfile.setImageURI(pictureUrl)
        }
    }

    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 100)
    }

}