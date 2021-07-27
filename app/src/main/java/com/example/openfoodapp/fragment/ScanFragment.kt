package com.example.openfoodapp.fragment

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.example.openfoodapp.R
import kotlinx.android.synthetic.main.fragment_scan.*
import java.util.concurrent.ExecutorService
import android.Manifest.permission.CAMERA
import android.app.AlertDialog
import android.text.InputType
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.camera.core.ImageAnalysis
import com.example.openfoodapp.models.Product
import com.example.openfoodapp.utils.BarcodeAnalyzer
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.*
import java.io.IOException
import java.util.concurrent.Executors

typealias BarcodeListener = (barcode: String) -> Unit

class ScanFragment : Fragment() {

    private lateinit var cameraExecutor: ExecutorService
    private val resultBarcodeFragment = ResultBarcodeFragment()
    private var processing : Boolean = false
    lateinit var progressBar : ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cameraExecutor = Executors.newSingleThreadExecutor()
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissions(
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_scan, container, false)

        progressBar = view.findViewById(R.id.progress_bar_scan)

        val floating : FloatingActionButton = view.findViewById(R.id.floating_button_manual_scan)
        floating.setOnClickListener {
            showdialog()
        }

        return view
    }

    companion object {

        private val REQUIRED_PERMISSIONS = arrayOf(CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10

    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

            cameraProviderFuture.addListener({
                val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(
                        fragment_scan_preview_view.surfaceProvider
                    )
                }

                val imageAnalysis = ImageAnalysis.Builder()
                    .build()
                    .also {
                        it.setAnalyzer(cameraExecutor, BarcodeAnalyzer { barcode ->
                            startProcessBarcode(barcode)
                        })
                    }

                // Select back camera
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis)
                } catch (e: Exception) {
                    Log.e("PreviewUseCase", "Binding failed! :(", e)
                }
            }, ContextCompat.getMainExecutor(requireContext()))

    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            requireContext(), it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun showdialog(){

        val builder: AlertDialog.Builder = AlertDialog.Builder(activity!!)
        builder.setTitle("Enter Barcode")

        val input = EditText(activity)
        input.hint = "Enter ScanCode"
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        builder.setPositiveButton("OK") { _, _ ->
            startProcessBarcode(input.text.toString())
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun startProcessBarcode(barcode : String){
        val client = OkHttpClient()
        val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
        val productJsonAdapter = moshi.adapter<Product>(Product::class.java)

        val request = Request.Builder()
            .url("https://api.formation-android.fr/getProduct?barcode=$barcode")
            .build()
        if (!processing) {
            progressBar.visibility = View.VISIBLE
            processing = true
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    progressBar.visibility = View.INVISIBLE
                    processing = false
                    Toast.makeText(activity,"something went wrong, try again later !",Toast.LENGTH_LONG).show()
                    Log.e("TAG", e.printStackTrace().toString())
                }

                override fun onResponse(call: Call, response: Response) {
                    processing = false
                    response.use {

                        val product = productJsonAdapter.fromJson(response.body!!.source())

                        if (product != null) {
                            progressBar.visibility = View.INVISIBLE
                            val bundle = Bundle()
                            bundle.putSerializable("product", product)
                            resultBarcodeFragment.arguments = bundle
                            fragmentManager?.beginTransaction()
                                ?.replace(R.id.home_frame_layout, resultBarcodeFragment)
                                ?.commit()
                        }
                    }
                }
            })
        }
    }

}