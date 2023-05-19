package com.example.autolist

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.autolist.AutoListViewModel
import com.example.autolist.R
import java.util.*
import kotlin.collections.ArrayList


private const val TAG = "AutoListFragment"

class AutoListFragment : Fragment() {

    interface Callbacks {
        fun onAutoSelected(autoId: UUID)
    }


    private lateinit var brandField: Spinner
    private lateinit var modelField: Spinner
    private lateinit var brandAdapter: ArrayAdapter<String>
    private lateinit var modelAdapter: ArrayAdapter<String>
    private lateinit var filterButton:Button

    var brands = arrayOf("","Toyota","Nissan","Suzuki")
    var tModels= arrayOf("","LandCruser 200","LandCruser Prado","LandCruser 300","Corolla Fielder")
    var nModels= arrayOf("","Safari","Patrol","Serena","Skyline")
    var sModels= arrayOf("","Swift","Jimny","Escudo","SX4")

    private var callbacks: Callbacks? = null
    private lateinit var autoRecyclerView: RecyclerView
    private var adapter: AutoAdapter? = AutoAdapter(emptyList())

    private val autoListViewModel: AutoListViewModel by lazy {
        ViewModelProviders.of(this).get(AutoListViewModel::class.java)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_auto_list, container, false)

        brandField=view.findViewById(R.id.auto_brand) as Spinner
        modelField=view.findViewById(R.id.auto_model) as Spinner
        filterButton=view.findViewById(R.id.fButton) as Button

        brandAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, brands)
        brandAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        brandField.setAdapter(brandAdapter)

        modelAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, tModels)
        brandField.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(brandField.selectedItem as String=="Toyota"){
                    modelAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, tModels)
                }
                if(brandField.selectedItem as String=="Nissan"){
                    modelAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, nModels)
                }
                if(brandField.selectedItem as String=="Suzuki"){
                    modelAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, sModels)
                }

                modelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                modelField.setAdapter(modelAdapter);

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                //toast
            }
        }
        filterButton.setOnClickListener{
            autoListViewModel.autoListLiveData.observe(
                viewLifecycleOwner,
                Observer { autos ->
                    autos?.let {
                        var sortedAutos=ArrayList<Auto>()
                        for (i in 0..autos.size-1){
                            if(autos[i].brand.toString().contains(brandField.selectedItem.toString()) && autos[i].model.toString().contains(modelField.selectedItem.toString())){
                                sortedAutos.add(autos[i])
                            }
                        }
                        updateUI(sortedAutos)
                    }
                })
        }




        autoRecyclerView =
            view.findViewById(R.id.auto_recycler_view) as RecyclerView
        autoRecyclerView.layoutManager = LinearLayoutManager(context)
        autoRecyclerView.adapter = adapter
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        autoListViewModel.autoListLiveData.observe(
            viewLifecycleOwner,
            Observer { autos ->
                autos?.let {
                    updateUI(autos)
                }
            })
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_auto_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.new_auto -> {
                val auto = Auto()
                autoListViewModel.addAuto(auto)
                callbacks?.onAutoSelected(auto.id)
                true
            }
            R.id.sort -> {
                autoListViewModel.autoListLiveData.observe(
                    viewLifecycleOwner,
                    Observer { autos ->
                        autos?.let {
                            var sortedAutos=autos.sortedByDescending { it.price }
                            updateUI(sortedAutos)
                        }
                    })
                true
            }
            R.id.sort_by_alphabet ->{
                autoListViewModel.autoListLiveData.observe(
                    viewLifecycleOwner,
                    Observer { autos ->
                        autos?.let {
                            var sortedAutos=autos.sortedByDescending { it.brand }
                            updateUI(sortedAutos)
                        }
                    })
                true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    companion object {
        fun newInstance(): AutoListFragment {
            return AutoListFragment()
        }
    }

    private fun updateUI(autos: List<Auto>) {
        adapter = AutoAdapter(autos)
        autoRecyclerView.adapter = adapter
    }

    private inner class AutoHolder(view: View)
        : RecyclerView.ViewHolder(view), View.OnClickListener {

        private lateinit var auto: Auto

        private val brandField: TextView = itemView.findViewById(R.id.auto_brand)
        private val priceField: TextView = itemView.findViewById(R.id.auto_price)
        private val yearField:TextView=itemView.findViewById(R.id.auto_year)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(auto: Auto) {
            this.auto = auto
            brandField.text = this.auto.brand+"  "+this.auto.model
            priceField.text = this.auto.price.toString() + "$"
            yearField.text = this.auto.year.toString()+"г"
        }

        override fun onClick(v: View) {
            callbacks?.onAutoSelected(auto.id)
        }
    }

    private inner class AutoAdapter(var autos: List<Auto>)
        : RecyclerView.Adapter<AutoHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AutoHolder {

            val view = layoutInflater.inflate(R.layout.list_item_auto, parent, false)
            return AutoHolder(view)
        }

        override fun onBindViewHolder(holder: AutoHolder, position: Int) {
            val auto = autos[position]
            holder.bind(auto)
        }


        override fun getItemCount() = autos.size
    }


}