package com.example.openfoodapp.utils

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.example.openfoodapp.fragment.BarcodeListener
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

class BarcodeAnalyzer(private val barcodeListener: BarcodeListener) : ImageAnalysis.Analyzer {

    private val scanner = BarcodeScanning.getClient()

    @SuppressLint("UnsafeExperimentalUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        try {
            val mediaImage = imageProxy.image
            if (mediaImage != null) {
                val image =
                    InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                // Pass image to the scanner and have it do its thing
                scanner.process(image)
                    .addOnSuccessListener { barcodes ->
                        // Task completed successfully
                        for (barcode in barcodes) {
                            barcodeListener(barcode.rawValue ?: "")
                        }
                    }
                    .addOnFailureListener {
                        // You should really do something about Exceptions
                    }
                    .addOnCompleteListener {
                        // It's important to close the imageProxy
                        imageProxy.close()
                    }
            }
        }catch (error: NullPointerException){
            Log.e("TAG", "analyze: ${error.stackTrace}", )
        }
    }
}