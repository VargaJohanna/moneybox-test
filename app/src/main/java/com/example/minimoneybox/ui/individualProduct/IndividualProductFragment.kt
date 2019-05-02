package com.example.minimoneybox.ui.individualProduct

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.example.minimoneybox.R
import kotlinx.android.synthetic.main.fragment_individual_product.view.*
import org.koin.androidx.viewmodel.ext.viewModel
import org.koin.core.parameter.parametersOf

class IndividualProductFragment : Fragment() {
    private val args: IndividualProductFragmentArgs by navArgs()
    private val productViewModel: IndividualProductViewModel by viewModel {
        parametersOf(args.productId)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_individual_product, container, false).apply {
            product_name.text = args.productName
            individual_plan_value.text = String.format(getString(R.string.product_plan_value), args.planValue)
            individual_moneybox_value.text =
                String.format(getString(R.string.product_moneybox_value), args.moneyboxValue)
            addButtonListener(add_money_button)
            observeMoneyBoxValue(individual_moneybox_value)
            showErrorMessage()
        }
    }

    private fun addButtonListener(button: Button) {
        button.setOnClickListener {
            productViewModel.payMoneybox(10)
        }
    }

    private fun observeMoneyBoxValue(moneyboxView: TextView) {
        productViewModel.getMoneyboxValue().observe(this, Observer {
            moneyboxView.text = String.format(getString(R.string.product_moneybox_value), it.toString())
        })
    }

    /**
     * Observe the error message and show the returned text in a toast
     */
    private fun showErrorMessage() {
        productViewModel.getErrorMessage().observe(this, Observer {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        })
    }

}