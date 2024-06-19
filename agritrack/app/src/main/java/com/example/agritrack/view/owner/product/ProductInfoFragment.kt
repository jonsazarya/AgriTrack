package com.example.agritrack.view.owner.product

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.agritrack.R
import com.example.agritrack.data.response.ProductsItem
import com.example.agritrack.databinding.FragmentProductInfoBinding
import com.example.agritrack.di.Result
import com.example.agritrack.view.ViewModelFactory

class ProductInfoFragment : Fragment() {

    private lateinit var binding: FragmentProductInfoBinding
    private val viewModel by viewModels<ProductViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentProductInfoBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvItemProducts.layoutManager = LinearLayoutManager(requireActivity())
        val adapter = ProductAdapter()
        binding.rvItemProducts.adapter = adapter

        val fragmentManager = parentFragmentManager
        val addProductFragment = AddProductFragment()

        binding.addProduct.setOnClickListener {
            fragmentManager.commit {
                addToBackStack(null)
                replace(R.id.frame_container, addProductFragment, AddProductFragment::class.java.simpleName)
            }
        }

        viewModel.getUserProducts().observe(requireActivity()) {
            if (it != null) {
                when (it) {
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE

                        adapter.submitList(it.data)
                    }
                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE

                        AlertDialog.Builder(requireActivity()).apply {
                            setTitle(it.error)
                            setPositiveButton("Ok") { dialog, _ ->
                                dialog.dismiss()
                            }
                            create()
                            show()
                        }
                    }
                }
            }
        }
    }
}