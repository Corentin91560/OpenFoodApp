package com.example.openfoodapp.fragment

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.openfoodapp.R
import com.example.openfoodapp.models.Product
import com.example.openfoodapp.utils.FireBase
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.*
import java.net.URL
import com.example.openfoodapp.screen.EditImageActivity
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import java.io.IOException


class ResultBarcodeFragment : Fragment() {

    private var product: Product? = null
    private var croppedImageUri : String? = null
    private var modification : Boolean = false
    private var modificationUrl : String = ""
    private var modificationCount : Int = 0
    lateinit var rootView : View
    private var imageUpdated : Boolean = false

    override fun onResume() {
        super.onResume()
        arguments?.let {
            product = it.getSerializable("product") as Product
        }
        val sharedPref = this.activity?.getSharedPreferences("Sharedpref", Context.MODE_PRIVATE)
        croppedImageUri = sharedPref?.getString("croppedImageUri", "")

        val resultTextViewName: EditText = rootView.findViewById(R.id.result_et_name) as EditText
        val resultTextViewBarcode: EditText = rootView.findViewById(R.id.result_et_barcode) as EditText
        val resultTextViewQuantity: EditText = rootView.findViewById(R.id.result_et_quantity) as EditText
        val resultTextViewBrands: EditText = rootView.findViewById(R.id.result_et_brands) as EditText
        val resultTextViewNutri: EditText = rootView.findViewById(R.id.result_et_nutriscore) as EditText
        val resultTextViewNova: EditText = rootView.findViewById(R.id.result_et_novascore) as EditText
        val resultTextViewIngredient: EditText = rootView.findViewById(R.id.result_et_ingredients) as EditText
        val resultTextViewTrace: EditText = rootView.findViewById(R.id.result_et_trace) as EditText
        val resultTextViewAllegens: EditText = rootView.findViewById(R.id.result_et_allergens) as EditText
        val resultTextViewManufacturing: EditText = rootView.findViewById(R.id.result_et_manufacturingCountries) as EditText
        val resultTextViewOilPalm: EditText = rootView.findViewById(R.id.result_et_oilpalm) as EditText
        val resultImageView: ImageView = rootView.findViewById(R.id.imageView_result) as ImageView

        resultTextViewName.setText(product?.response?.name)
        resultTextViewBarcode.setText(product?.response?.barcode)
        resultTextViewQuantity.setText(product?.response?.quantity)
        resultTextViewBrands.setText(listToString(product?.response?.brands))
        resultTextViewNutri.setText(product?.response?.nutriScore)
        resultTextViewNova.setText(product?.response?.novascore)
        resultTextViewIngredient.setText(listToString(product?.response?.ingredients))
        resultTextViewTrace.setText(listToString(product?.response?.trace))
        resultTextViewAllegens.setText(listToString(product?.response?.allergens))
        resultTextViewManufacturing.setText(listToString(product?.response?.manufacturingCountries))
        if (product?.response?.containsPalmOil == true){
            resultTextViewOilPalm.setText("oui")
        }else{
            resultTextViewOilPalm.setText("non")
        }
        if (croppedImageUri != ""){
            val uri = Uri.parse(croppedImageUri)
            resultImageView.setImageURI(uri)
            imageUpdated = true
            Log.d("TAG", "onResume: croppedimage $croppedImageUri")
        }else{
            Log.d("TAG", "RESUME: error URI $croppedImageUri")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        modificationUrl = "https://world.openfoodfacts.org/cgi/product_jqm2.pl?code=${product?.response?.barcode}&user_id=openfoodappesgi&password=esgi2021"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        arguments?.let {
            product = it.getSerializable("product") as Product
        }

        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        val sharedPref = this.activity?.getSharedPreferences("Sharedpref",Context.MODE_PRIVATE)
        with (sharedPref?.edit()) {
            this?.clear()
            this?.apply()
        }

        Log.d("TAG", "onCreateView: ${product?.response?.barcode}")
        rootView = inflater.inflate(R.layout.fragment_result_barcode,container,false)

        val progressBar = rootView.findViewById<ProgressBar>(R.id.progress_bar_result_scan)

        val resultTextViewError: TextView = rootView.findViewById(R.id.result_tv_error) as TextView
        val resultTextViewName: EditText = rootView.findViewById(R.id.result_et_name) as EditText
        val resultTextViewBarcode: EditText = rootView.findViewById(R.id.result_et_barcode) as EditText
        val resultTextViewQuantity: EditText = rootView.findViewById(R.id.result_et_quantity) as EditText
        val resultTextViewBrands: EditText = rootView.findViewById(R.id.result_et_brands) as EditText
        val resultTextViewNutri: EditText = rootView.findViewById(R.id.result_et_nutriscore) as EditText
        val resultTextViewNova: EditText = rootView.findViewById(R.id.result_et_novascore) as EditText
        val resultTextViewIngredient: EditText = rootView.findViewById(R.id.result_et_ingredients) as EditText
        val resultTextViewTrace: EditText = rootView.findViewById(R.id.result_et_trace) as EditText
        val resultTextViewAllegens: EditText = rootView.findViewById(R.id.result_et_allergens) as EditText
        val resultTextViewManufacturing: EditText = rootView.findViewById(R.id.result_et_manufacturingCountries) as EditText
        val resultTextViewOilPalm: EditText = rootView.findViewById(R.id.result_et_oilpalm) as EditText
        val resultTextViewPicture: ImageView = rootView.findViewById(R.id.imageView_result) as ImageView
        val modifyButton : ImageButton = rootView.findViewById(R.id.modify_button) as ImageButton
        val confirmModificationButton : Button = rootView.findViewById(R.id.confirm_modification_button) as Button
        val editImageButton : Button = rootView.findViewById(R.id.button_edit_image) as Button
        if(product?.error == null){
            GlobalScope.launch {

                Firebase.auth.currentUser?.let { FireBase().checkAchievementExist(it.uid, "wjIijgdTFHe20ilwmAjL") }
                Firebase.auth.currentUser?.let { FireBase().checkAchievementExist(it.uid, "WHsaKTJhzgfdlsBqKZW1") }

                if (Firebase.auth.currentUser?.let { FireBase().checkBarcodeScanned(it.uid, product?.response?.barcode!!)} == false ){
                    Firebase.auth.currentUser?.let { FireBase().updateAchievementAdvancement(it.uid, "wjIijgdTFHe20ilwmAjL",1,3) }
                }
                if (product?.let { checkOilPalmer(it) } == true){
                    Firebase.auth.currentUser?.let { FireBase().updateAchievementAdvancement(it.uid, "WHsaKTJhzgfdlsBqKZW1",1,-2) }
                }
            }

            resultTextViewName.setText(product?.response?.name)
            resultTextViewBarcode.setText(product?.response?.barcode)
            resultTextViewQuantity.setText(product?.response?.quantity)
            resultTextViewBrands.setText(listToString(product?.response?.brands))
            resultTextViewNutri.setText(product?.response?.nutriScore)
            resultTextViewNova.setText(product?.response?.novascore)
            resultTextViewIngredient.setText(listToString(product?.response?.ingredients))
            resultTextViewTrace.setText(listToString(product?.response?.trace))
            resultTextViewAllegens.setText(listToString(product?.response?.allergens))
            resultTextViewManufacturing.setText(listToString(product?.response?.manufacturingCountries))
            if (product?.response?.containsPalmOil == true){
                resultTextViewOilPalm.setText("oui")
            }else{
                resultTextViewOilPalm.setText("non")
            }


            if (croppedImageUri != ""){
                Log.d("TAG", "onCreateView: $croppedImageUri")
            }else{
                Log.d("TAG", "onCreateView: error URI")
            }
            val url = URL(product?.response?.picture)
            val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())
            resultTextViewPicture.setImageBitmap(bmp)
            modifyButton.setOnClickListener {

                if (modification){
                    resultTextViewOilPalm.isEnabled = false
                    resultTextViewName.isEnabled = false
                    resultTextViewBarcode.isEnabled = false
                    resultTextViewQuantity.isEnabled = false
                    resultTextViewBrands.isEnabled = false
                    resultTextViewNutri.isEnabled = false
                    resultTextViewNova.isEnabled = false
                    resultTextViewIngredient.isEnabled = false
                    resultTextViewTrace.isEnabled = false
                    resultTextViewAllegens.isEnabled = false
                    resultTextViewManufacturing.isEnabled = false
                    modification = false
                    confirmModificationButton.visibility = View.INVISIBLE
                    editImageButton.visibility = View.INVISIBLE
                }else{
                    resultTextViewOilPalm.isEnabled = true
                    resultTextViewName.isEnabled = true
                    resultTextViewQuantity.isEnabled = true
                    resultTextViewBrands.isEnabled = true
                    resultTextViewNutri.isEnabled = true
                    resultTextViewNova.isEnabled = true
                    resultTextViewIngredient.isEnabled = true
                    resultTextViewTrace.isEnabled = true
                    resultTextViewAllegens.isEnabled = true
                    resultTextViewManufacturing.isEnabled = true
                    modification = true
                    confirmModificationButton.visibility = View.VISIBLE
                    editImageButton.visibility = View.VISIBLE
                }
            }
            editImageButton.setOnClickListener {
                requireActivity().run{
                    startActivity(Intent(this, EditImageActivity::class.java))
                }
            }

            confirmModificationButton.setOnClickListener {
                progressBar.visibility = View.VISIBLE
                val client = OkHttpClient()
                if (product?.response?.name.toString() != resultTextViewName.text.toString() && product?.response?.name!=null) {
                    modificationCount += 1
                    modificationUrl += "&add_product_name=${resultTextViewName.text}"
                }
                if (product?.response?.quantity.toString() != resultTextViewQuantity.text.toString() && product?.response?.quantity!=null) {
                    modificationCount += 1
                    modificationUrl += "&add_quantity=${resultTextViewQuantity.text}"
                }
                if (listToString(product?.response?.brands) != resultTextViewBrands.text.toString()) {
                    modificationCount += 1
                    modificationUrl += "&add_brands=${resultTextViewBrands.text}"
                }
                if (product?.response?.nutriScore.toString() != resultTextViewNutri.text.toString() && resultTextViewNutri.text.toString()!=""){
                    modificationCount += 1
                    modificationUrl += "&add_nutriScore=${resultTextViewNutri.text}"
                }
                if (product?.response?.novascore.toString() != resultTextViewNova.text.toString() && resultTextViewNova.text.toString()!="") {
                    modificationCount += 1
                    modificationUrl += "&add_novaScore=${resultTextViewNova.text}"
                }
                if (listToString(product?.response?.ingredients) != resultTextViewIngredient.text.toString()) {
                    modificationCount += 1
                    modificationUrl += "&add_ingredients_text=${resultTextViewIngredient.text}"
                }
                if (listToString(product?.response?.trace) != resultTextViewTrace.text.toString()) {
                    modificationCount += 1
                    modificationUrl += "&add_traces=${resultTextViewTrace.text}"
                }
                if (listToString(product?.response?.allergens) != resultTextViewAllegens.text.toString()) {
                    modificationCount += 1
                    modificationUrl += "&add_allergens=${resultTextViewAllegens.text}"
                }
                if (listToString(product?.response?.manufacturingCountries) != resultTextViewManufacturing.text.toString()) {
                    modificationCount += 1
                    modificationUrl += "&add_manufacturingCountries=${resultTextViewManufacturing.text}"
                }
                if(resultTextViewOilPalm.text.toString() == "oui" && product?.response?.containsPalmOil == false){
                    modificationCount += 1
                    modificationUrl += "&add_containPalmOil=true"
                }
                if(resultTextViewOilPalm.text.toString() == "non" && product?.response?.containsPalmOil == true){
                    modificationCount += 1
                    modificationUrl += "&add_containPalmOil=false"
                }
                if (modificationCount>0){
                    GlobalScope.launch {
                        val maxModif = Firebase.auth.currentUser?.let { FireBase().checkAchievementExist(it.uid, "MBJF8iczBnFbJNrrn4nx") }//corrector
                        Firebase.auth.currentUser?.let { FireBase().checkAchievementExist(it.uid, "EULMCPXQksK7j7WLj39Z") }//modificator
                        if (modificationCount> maxModif?.toInt()!!){
                            Firebase.auth.currentUser?.let { FireBase().updateAchievementAdvancement(it.uid, "MBJF8iczBnFbJNrrn4nx", modificationCount,0) }
                        }
                        Firebase.auth.currentUser?.let { FireBase().updateAchievementAdvancement(it.uid, "EULMCPXQksK7j7WLj39Z",modificationCount,4*modificationCount) }
                        modificationCount = 0
                        modificationUrl = "https://world.openfoodfacts.org/cgi/product_jqm2.pl?code=${product?.response?.barcode}&user_id=openfoodappesgi&password=esgi2021"
                    }
                }
                if (imageUpdated){
                    //requete url pour la photo a part
                    GlobalScope.launch {
                        val imageUrl = croppedImageUri?.replace("file:///data/user/0/com.example.openfoodapp/cache/","")
                        val request = Request.Builder().url("https://us.openfoodfacts.org/cgi/product_jqm2.pl?code=${product?.response?.barcode}&product_image_upload.pl/imgupload_front=$imageUrl")
                            .build()
                        client.newCall(request).enqueue(object : Callback{
                            override fun onFailure(call: Call, e: IOException) {
                                activity!!.runOnUiThread {
                                    Toast.makeText(
                                        activity,
                                        "Error try again later",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                            override fun onResponse(call: Call, response: Response) {
                                if (response.code == 200){
                                    activity!!.runOnUiThread {
                                        Toast.makeText(
                                            activity,
                                            "Request have been send",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }

                                    modification = false
                                }else{
                                    activity!!.runOnUiThread {
                                        Toast.makeText(
                                            activity,
                                            "Error try again later",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }

                        })

                        Firebase.auth.currentUser?.let { FireBase().checkAchievementExist(it.uid,"SDmmyQdv04ZmwaFpHCC2") }//modificator
                        Firebase.auth.currentUser?.let {FireBase().updateAchievementAdvancement(it.uid,"SDmmyQdv04ZmwaFpHCC2",1,50) }
                        imageUpdated = false
                    }
                }



                val request = Request.Builder()
                    .url(modificationUrl)
                    .build()
                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        activity!!.runOnUiThread {
                            Toast.makeText(
                                activity,
                                "Error try again later",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onResponse(call: Call, response: Response) {
                        if (response.code == 200){
                            activity!!.runOnUiThread {
                                Toast.makeText(
                                    activity,
                                    "Request have been send",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            modification = false
                        }else{
                            activity!!.runOnUiThread {
                                Toast.makeText(
                                    activity,
                                    "Error try again later",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                })
                progressBar.visibility = View.INVISIBLE
            }
        }else{
            GlobalScope.launch {
                Firebase.auth.currentUser?.let { FireBase().checkAchievementExist(it.uid, "JLgUw5OpMCp8TTpjqo8L") }
                Firebase.auth.currentUser?.let { FireBase().updateAchievementAdvancement(it.uid, "JLgUw5OpMCp8TTpjqo8L",1,1) }
            }
            resultTextViewError.text = product?.error
            resultTextViewError.visibility = View.VISIBLE
        }
        // Inflate the layout for this fragment
        return rootView
    }

    private fun checkOilPalmer(product: Product): Boolean{

        return product.response?.containsPalmOil == true
    }

    private fun listToString(list :List<String>?): String{

        var result = ""
        if (list != null) {
            for (element in list){
                result += "$element \n"
            }
        }
        return result
    }

}