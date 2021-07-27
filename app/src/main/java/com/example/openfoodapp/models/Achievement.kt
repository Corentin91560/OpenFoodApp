package com.example.openfoodapp.models


data class Achievement(
    val id : String,
    val name : String,
    val description : String,
    val step1 : Long?,
    val step2 : Long? = 0,
    val step3 : Long? = 0,
    val step4 : Long? = 0,
    var usercount : Long = 0
)