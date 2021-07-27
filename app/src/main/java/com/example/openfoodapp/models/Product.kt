package com.example.openfoodapp.models

import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class Product (
    val response : Res?,
    val error : String?
        ):Serializable{
    @JsonClass(generateAdapter = true)
    data class Res(
        val name : String?,
        val barcode : String?,
        val picture : String?,
        val quantity : String?,
        val brands : List<String>?,
        val manufacturingCountries : List<String>?,
        val nutriScore : String?,
        val novascore : String?,
        val ingredients : List<String>?,
        val trace : List<String>?,
        val allergens : List<String>?,
        val nutrimentLevels : NutrimentLevels?,
        val nutritionFacts : NutritionFacts?,
        val containsPalmOil : Boolean
    ):Serializable{
        @JsonClass(generateAdapter = true)
        data class NutrimentLevels(
            val fat :String?,
            val salt : String?,
            val saturatedFat : String?,
            val sugars : String?
        ):Serializable
        @JsonClass(generateAdapter = true)
        data class NutritionFacts(
            val servingSize : String?,
            val calories : String?,
            val fat : Fat?,
            val saturatedFat : SaturatedFat?,
            val carbohydrate : Carbohydrate?,
            val sugar : Sugar?,
            val fiber : Fiber?,
            val proteins : Proteins?,
            val sodium : Sodium?,
            val salt : Salt?,
            val energy : Energy?
        ):Serializable{
            @JsonClass(generateAdapter = true)
            data class Fat(
                val unit : String?,
                val perServing : String?,
                val per100g : String?,
            ):Serializable
            @JsonClass(generateAdapter = true)
            data class SaturatedFat(
                val unit : String?,
                val perServing : String?,
                val per100g : String?,
                ):Serializable
            @JsonClass(generateAdapter = true)
            data class Carbohydrate(
                val unit : String?,
                val perServing : String?,
                val per100g : String?,
                ):Serializable
            @JsonClass(generateAdapter = true)
            data class Sugar(
                val unit : String?,
                val perServing : String?,
                val per100g : String?,
                ):Serializable
            @JsonClass(generateAdapter = true)
            data class Fiber(
                val unit : String?,
                val perServing : String?,
                val per100g : String?,
                ):Serializable
            @JsonClass(generateAdapter = true)
            data class Proteins(
                val unit : String?,
                val perServing : String?,
                val per100g : String?,
                ):Serializable
            @JsonClass(generateAdapter = true)
            data class Sodium(
                val unit : String?,
                val perServing : String?,
                val per100g : String?,
                ):Serializable
            @JsonClass(generateAdapter = true)
            data class Salt(
                val unit : String?,
                val perServing : String?,
                val per100g : String?,
                ):Serializable
            @JsonClass(generateAdapter = true)
            data class Energy(
                val unit : String?,
                val perServing : String?,
                val per100g : String?,
                ):Serializable
        }
    }
}