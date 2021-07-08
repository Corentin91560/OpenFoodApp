package com.example.openfoodapp.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.openfoodapp.R
import kotlinx.android.synthetic.main.fragment_result_barcode.*

class ResultBarcodeFragment : Fragment() {

    private var barcode: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            barcode = it.getString("barcode")
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_result_barcode,container,false)
        val result_tv: TextView = rootView.findViewById(R.id.result_barcode_txt) as TextView
        if(barcode != null){
            Log.e("TAG", "onCreateView: $barcode", )
            result_tv.text = barcode
        }else{
            result_tv.text = "Error try again later "
        }
        // Inflate the layout for this fragment
        return rootView
    }

}