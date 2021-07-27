package com.example.openfoodapp.utils

import android.net.Uri
import android.util.Log
import com.example.openfoodapp.models.Achievement
import com.example.openfoodapp.models.Rank
import com.example.openfoodapp.models.UserInfo
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException

import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class FireBase {
    val db = Firebase.firestore
    val stockage = Firebase.storage
    var storageRef = stockage.reference

    suspend fun getLeaderboard(idAchievement: String) : ArrayList<Rank>{

        val unsortRanking = ArrayList<Rank>()
        val userInfo = ArrayList<UserInfo>()

            db.collection("users")
                .get()
                .addOnFailureListener {
                    Log.w("TAG", "Error getting documents.")

                }.addOnSuccessListener { users ->
                    for(user in users){
                        userInfo.add(UserInfo(user.id,user.data["username"] as String))
                    }
                }.await()

        for (user in userInfo) {
            if (idAchievement != "") {
                db.document("users/${user.id}/achievements/$idAchievement")
                    .get()
                    .addOnSuccessListener { document ->
                        val count : Long
                        if (document.data?.get("count") == null){
                            count = 0
                        } else {
                            count = document.data?.get("count") as Long
                        }
                        unsortRanking.add(
                            Rank(
                                user.name ,
                                count
                            )
                        )
                    }
                    .addOnFailureListener { exception ->
                        Log.w("TAG", "Error getting documents.", exception)
                    }.await()

            } else {
                db.document("users/${user.id}")
                    .get()
                    .addOnSuccessListener { document ->
                        val count : Long
                        if (document.data?.get("experience") == null){
                            count = 0
                        }else{
                            count = document.data?.get("experience") as Long
                        }
                        unsortRanking.add(
                            Rank(
                                user.name ,
                                count
                            )
                        )
                    }
                    .addOnFailureListener { exception ->
                        Log.w("TAG", "Error getting documents.", exception)
                    }.await()
            }
        }
        return unsortRanking
    }

    suspend fun getAllAchievements():ArrayList<Achievement> {
        var listAchievement = ArrayList<Achievement>()
        db.collection("achievements")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    listAchievement.add(
                        Achievement(
                            document.id,
                            document.data["name"] as String,
                            document.data["description"] as String,
                            document.data["step1"] as Long,
                            document.data["step2"] as Long?,
                            document.data["step3"] as Long?,
                            document.data["step4"] as Long?
                        )

                    )
                }
            }
            .addOnFailureListener { exception ->
                Log.w("TAG", "Error getting documents.", exception)
            }.await()

        return listAchievement
    }

    suspend fun addUserInfo(id: String, pictureUri: Uri, name : String): Uri{

        db.collection("users")
            .document(id)
            .update("username",name)
            .addOnSuccessListener { Log.d("TAG", "DocumentSnapshot successfully written!$name") }
            .addOnFailureListener { e -> Log.w("TAG", "Error writing document", e) }

        storageRef.child("profile_picture/${id}.png").putFile(pictureUri).await()

        val uri = storageRef.child("profile_picture/${id}.png").downloadUrl.await()

        return uri
    }

    fun setUserCreated(idUser: String, name : String){
        val sdf = SimpleDateFormat("dd/M/yyyy")
        val currentDate = sdf.format(Date())
        val docData = hashMapOf(
            "created_user_date" to currentDate ,
            "username" to name,
            "experience" to 0
        )


        db.collection("users")
            .document(idUser)
            .set(docData)
            .addOnSuccessListener { Log.d("TAG", "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w("TAG", "Error writing document", e) }


    }

    suspend fun getUserExperience(idUser: String): Long {
        var experience: Long = 0

        db.document("users/$idUser")
            .get()
            .addOnSuccessListener { result ->
                if (result.data?.get("experience") != null) {
                    experience = result.data?.get("experience") as Long
                }
            }
            .addOnFailureListener { e -> Log.w("TAG", "Error writing document", e) }.await()

        return experience

    }

    fun incrementUserExperience(idUser: String,increment: Int) {

        db.document("users/$idUser")
            .update("experience",FieldValue.increment(increment.toLong()))
            .addOnSuccessListener {
                Log.d("TAG", "DocumentSnapshot successfully written!.")
            }
            .addOnFailureListener { e -> Log.w("TAG", "Error writing document", e) }

    }

    suspend fun getLastConnection(idUser: String): String{

        var date : String = ""

        db.document("users/$idUser")
            .get()
            .addOnSuccessListener { result ->
                if (result.data?.get("last_connection") != null){
                    date = result.data?.get("last_connection") as String
                }
            }
            .addOnFailureListener { e -> Log.w("TAG", "Error writing document", e) }.await()

        return date
    }

    fun setLastConnetionDate(idUser: String ){
        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        val currentDate = sdf.format(Date())

        db.collection("users")
            .document(idUser)
            .update("last_connection",currentDate)
            .addOnSuccessListener { Log.d("TAG", "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w("TAG", "Error writing document", e) }

    }

    suspend fun checkAchievementExist(userId : String, achievementId : String): Long{
        var check = true
        var value : Long = 0
        db.document("users/$userId/achievements/$achievementId")
            .get()
            .addOnSuccessListener { result ->
                check = result.data != null
                if (check) {
                    value = result.data?.get("count") as Long
                }
                Log.d("TAG", "AddAchievement: ${result.data?.get("count")}")
            }
            .addOnFailureListener {  e ->
                Log.w("TAG", "Error writing document", e)
                check = false
            }.await()

        if(!check){
            val docData = hashMapOf(
                "count" to 0 ,
            )

            db.document("users/$userId/achievements/$achievementId")
                .set(docData)
                .addOnSuccessListener { Log.d("TAG", "DocumentSnapshot successfully written!") }
                .addOnFailureListener { e -> Log.w("TAG", "Error writing document", e) }.await()
        }
        return value
    }

    suspend fun updateAchievementAdvancement(userId : String, achievementId : String, increment : Int, experience : Int){
        if (achievementId == "MBJF8iczBnFbJNrrn4nx"){
            db.document("users/$userId/achievements/$achievementId")
                .update("count",increment.toLong())
                .addOnSuccessListener {
                    Log.d("TAG", "DocumentSnapshot successfully written!.")
                }
                .addOnFailureListener { e -> Log.w("TAG", "Error writing document", e) }.await()
        }else if (achievementId == "7OkMBgULRI5GE1ySHUrz" && increment == 0 ) {
            db.document("users/$userId/achievements/$achievementId")
                .update("count",increment.toLong())
                .addOnSuccessListener {
                    Log.d("TAG", "DocumentSnapshot successfully written!.")
                }
                .addOnFailureListener { e -> Log.w("TAG", "Error writing document", e) }.await()
        }else{
            db.document("users/$userId/achievements/$achievementId")
                .update("count",FieldValue.increment(increment.toLong()))
                .addOnSuccessListener {
                    incrementUserExperience(userId,experience)
                    Log.d("TAG", "DocumentSnapshot successfully written!.")
                }
                .addOnFailureListener { e -> Log.w("TAG", "Error writing document", e) }.await()
        }


        checkAchievementNotification(achievementId,getAchievementProgression(userId,achievementId))
    }

    fun checkAchievementNotification(achievementId : String,count : Long){
        db.document("achievements/$achievementId")
            .get()
            .addOnSuccessListener { result ->
                if (result.data?.get("step1")==count || result.data?.get("step2")==count || result.data?.get("step3")==count || result.data?.get("step4")==count){
                    val client = OkHttpClient()
                    val json = JSONObject("""
                        {
                            "notification": {
                                "title": "Look at this",
                                "body": "You've unlock an achievement"
                            },
                            "registration_ids": ["dwgrCwm0TleFl4I1tKPLYJ:APA91bH2AKGoHX8_CgXPLTh4CoVr5doloekItKhCGhAW_9gK-mPDdZAFwOY3rweETNbZxQbNgjL-wIDujPIWwoUqx_Eq1bVQfpnLDyi1YYhm96AliKIiMR4G9dOn1tRBnyPnrIERECus"]
                        }
""")
                    val body: RequestBody =
                        RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(),json.toString())
                    val request = Request.Builder()
                        .url("https://fcm.googleapis.com/fcm/send")
                        .header("Authorization","key=AAAA6pvj4iY:APA91bHsDZWKurqpayZXq6may8grSjFk9HoA3h7xbrQGw7cSSeYaXTLfzC-sLC_LiKrWrziZfITReX0nb-rVcZfsDLAx-Bx_O-IFKWU_9lhcIhQFCJu0W97daA0GZmlzQGPJ1ndPP3ca")
                        .post(body)
                        .build()
                    Log.d("TAG", "checkAchievementNotification: ${request.body.toString()}")
                    client.newCall(request).enqueue(object : Callback{
                        override fun onFailure(call: Call, e: IOException) {
                            Log.e("TAG", "onFailure: $e", )
                        }

                        override fun onResponse(call: Call, response: Response) {
                            Log.e("TAG", "onResponse: OKAY ", )
                        }
                    })
                }else{
                    Log.d("TAG", "checkAchievementNotification: not ready")
                }

            }
            .addOnFailureListener {

            }
    }

    suspend fun getAchievementProgression(userId : String, achievementId : String) : Long{
        var count :Long = 0
        db.document("users/$userId/achievements/$achievementId")
            .get()
            .addOnSuccessListener { result ->
                if (result.data?.get("count") != null){
                    count = result.data?.get("count") as Long
                }

            }
            .addOnFailureListener { e -> Log.w("TAG", "Error writing document", e) }.await()

        return count
    }

    suspend fun addBarcodeProduct(userId : String,barcode : String){
        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        val currentDate = sdf.format(Date())
        val data = hashMapOf(
            "barcode" to barcode,
            "date_scan" to currentDate
        )

        db.collection("users/$userId/barcodes")
            .add(data)
            .addOnSuccessListener { documentReference ->
                Log.d("TAG", "DocumentSnapshot written with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w("TAG", "Error adding document", e)
            }.await()

    }
    suspend fun checkBarcodeScanned(userId : String,barcode : String):Boolean{
        var check = false
        db.collection("users/$userId/barcodes")
            .whereEqualTo("barcode",barcode)
            .get()
            .addOnSuccessListener { result ->
                for (doc in result){
                    check = true
                }
            }
            .addOnFailureListener { e-> Log.d("TAG", "checkBarcodeScanned: $e") }.await()
        if (!check){
            addBarcodeProduct(userId,barcode)
        }
        Log.e("TAG", "checkBarcodeScanned: $check")

        return check

    }

}