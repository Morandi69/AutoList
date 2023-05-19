package com.example.autolist

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.autolist.Auto
import com.example.autolist.AutoDetailViewModel
import com.example.autolist.R
import java.util.Date
import java.util.UUID

private const val TAG = "AutoFragment"
private const val ARG_Auto_ID = "auto_id"

class AutoFragment : Fragment() {

    private lateinit var auto: Auto
    private lateinit var brandField: Spinner
    private lateinit var modelField: Spinner
    private lateinit var yearField:EditText
    private lateinit var priceField:EditText
    private lateinit var brandAdapter: ArrayAdapter<String>
    private lateinit var modelAdapter: ArrayAdapter<String>


    var brands = arrayOf("Toyota","Nissan","Suzuki")
    var tModels= arrayOf("LandCruser 200","LandCruser Prado","LandCruser 300","Corolla Fielder")
    var nModels= arrayOf("Safari","Patrol","Serena","Skyline")
    var sModels= arrayOf("Swift","Jimny","Escudo","SX4")


    private val autoDetailViewModel: AutoDetailViewModel by lazy {
        ViewModelProviders.of(this).get(AutoDetailViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auto = Auto()
        val autoId: UUID = arguments?.getSerializable(ARG_Auto_ID) as UUID
        autoDetailViewModel.loadAuto(autoId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_auto, container, false)

        brandField=view.findViewById(R.id.auto_brand) as Spinner
        modelField=view.findViewById(R.id.auto_model) as Spinner
        yearField=view.findViewById(R.id.auto_year) as EditText
        priceField=view.findViewById(R.id.auto_price) as EditText




        brandAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, brands)
        brandAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        brandField.setAdapter(brandAdapter)

        modelAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, tModels)



        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        super.onViewCreated(view, savedInstanceState)
        autoDetailViewModel.autoLiveData.observe(
            viewLifecycleOwner,
            Observer { auto ->
                auto?.let {
                    this.auto = auto
                    updateUI()
                }
            })

        val appCompatActivity = activity as AppCompatActivity
        appCompatActivity.supportActionBar?.setTitle(R.string.new_auto)
    }

    override fun onStart() {
        super.onStart()
        brandField.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                auto.brand = brandField.selectedItem as String


                if(auto.brand=="Toyota"){
                    modelAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, tModels)
                }
                if(auto.brand=="Nissan"){
                    modelAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, nModels)
                }
                if(auto.brand=="Suzuki"){
                    modelAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, sModels)
                }

                modelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                modelField.setAdapter(modelAdapter);

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                //toast
            }
        }
        modelField.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                auto.model = modelField.selectedItem as String
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                //Toast
            }
        }
        priceField.doAfterTextChanged {
            auto.price = if (it.toString() == "")
                0
            else
                it.toString().toInt()
        }
        yearField.doAfterTextChanged {
            auto.year = if (it.toString() == "")
                0
            else
                it.toString().toInt()
        }


    }

    override fun onStop() {
        super.onStop()
        autoDetailViewModel.saveAuto(auto)
        val appCompatActivity = activity as AppCompatActivity
        appCompatActivity.supportActionBar?.setTitle(R.string.app_name)
    }



    private fun updateUI() {
        brandField.setSelection(brandAdapter.getPosition(auto.brand));
        modelField.setSelection(modelAdapter.getPosition(auto.model));
        priceField.setText(auto.price.toString())
        yearField.setText(auto.year.toString())
    }

    companion object {

        fun newInstance(autoId: UUID): AutoFragment {
            val args = Bundle().apply {
                putSerializable(ARG_Auto_ID, autoId)
            }
            return AutoFragment().apply {
                arguments = args
            }
        }
    }
}