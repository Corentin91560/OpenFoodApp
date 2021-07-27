package com.example.openfoodapp.screen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.openfoodapp.HomeActivity
import com.example.openfoodapp.R
import com.example.openfoodapp.utils.FireBase
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.signin_layout.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class SignInActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signin_layout)
        //signIn("cpasmoi@gmail.com","cpasmoi91")

        sing_in_button.setOnClickListener {
            signIn(et_email_sign_in.text.toString(),et_password_sign_in.text.toString())
        }
    }

    private fun signIn(email: String, password: String) {
        Log.e("TAG", "signIn: $email")
        progress_bar_sign_in.visibility = View.VISIBLE
        if (email != "" && password != ""){
            Firebase.auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        checkConnectionStreak()
                        progress_bar_sign_in.visibility = View.INVISIBLE
                        val intent = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                    } else {
                        progress_bar_sign_in.visibility = View.INVISIBLE
                        Log.w("TAG", "signInWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                    }
                }
        }
        progress_bar_sign_in.visibility = View.INVISIBLE
    }

    private fun checkConnectionStreak(){
        GlobalScope.launch {
            val lastconnection = Firebase.auth.currentUser?.let { FireBase().getLastConnection(it.uid) }
            if (lastconnection != ""){
                val sdf = SimpleDateFormat("dd/MM/yyyy")
                val currentDate = sdf.format(Date())
                var currentdate = SimpleDateFormat("dd/MM/yyyy").parse(currentDate)
                var lastConnectionDate = SimpleDateFormat("dd/MM/yyyy").parse(lastconnection)
                if (currentdate != lastConnectionDate){
                    val c = Calendar.getInstance()
                    c.time = lastConnectionDate
                    c.add(Calendar.DATE, 1)
                    lastConnectionDate = c.time
                    if (currentdate == lastConnectionDate ){
                        Firebase.auth.currentUser?.let { FireBase().checkAchievementExist(it.uid,"7OkMBgULRI5GE1ySHUrz") } //check to create achievement
                        Firebase.auth.currentUser?.let { FireBase().updateAchievementAdvancement(it.uid,"7OkMBgULRI5GE1ySHUrz",1,0) }//update achievement + add experience to user (update connection streak )
                    }else{
                        Firebase.auth.currentUser?.let { FireBase().updateAchievementAdvancement(it.uid,"7OkMBgULRI5GE1ySHUrz",0,0) }// reset connection streak
                    }
                    Firebase.auth.currentUser?.let { FireBase().checkAchievementExist(it.uid,"9ArAZGqTj1bTJAH7HnBd") }
                    Firebase.auth.currentUser?.let { FireBase().updateAchievementAdvancement(it.uid,"9ArAZGqTj1bTJAH7HnBd",1,0) } // update achievement cumulative connection
                }
            }
            Firebase.auth.uid?.let { FireBase().setLastConnetionDate(it) } // set last connection date
        }
    }
}