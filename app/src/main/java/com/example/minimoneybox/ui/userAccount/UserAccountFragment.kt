package com.example.minimoneybox.ui.userAccount

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.minimoneybox.R
import com.example.minimoneybox.ext.display
import com.example.minimoneybox.ext.restart
import com.example.minimoneybox.ext.show
import com.example.minimoneybox.model.InvestorProduct
import kotlinx.android.synthetic.main.fragment_user_account.*
import kotlinx.android.synthetic.main.fragment_user_account.view.*
import org.koin.androidx.viewmodel.ext.viewModel


class UserAccountFragment : Fragment(), ProductAdapter.ItemClickListener {
    private val userAccountViewModel: UserAccountViewModel by viewModel()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val adapter = ProductAdapter(ArrayList(), this)
        return inflater.inflate(R.layout.fragment_user_account, container, false).apply {
            userAccountViewModel.observeProductList()
            showName()
            showTotalPlanValue()
            generateProductList(adapter, account_recycler_view)
            displayProductList(adapter, account_progress_bar)
            showErrorMessage()
            logoutUser()
            swipe_refresh.setOnRefreshListener { // This is to allow the user to retry and get the results in case of a server side error.
                userAccountViewModel.observeProductList()
                Handler().postDelayed({ swipe_refresh.isRefreshing = false }, 3000)
            }
        }
    }

    private fun generateProductList(productAdapter: ProductAdapter, recyclerView: RecyclerView) {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = productAdapter
        }
    }

    private fun displayProductList(adapter: ProductAdapter, progressBar: ProgressBar) {
        progressBar.show(true)
        userAccountViewModel.getProductList().observe(this, Observer {
            adapter.updateList(it)
            progressBar.show(false)
        })
    }

    private fun showName() {
        userAccountViewModel.getName().observe(this, Observer {
            if (it.isNotEmpty()) {
                user_name.display(true)
                user_name.text = String.format(getString(R.string.welcome_text), it)
            }
        })
    }

    private fun showTotalPlanValue() {
        userAccountViewModel.getTotalPlanValue().observe(this, Observer {
            if (it != 0f) total_plan_value.text = String.format(getString(R.string.total_plan_value), it.toString())
        })
    }

    override fun onItemClick(product: InvestorProduct) {
        val action = UserAccountFragmentDirections.fromUserAccountToIndividualProduct(
            product.id,
            product.name,
            product.planValue.toString(),
            product.moneyBoxValue.toString()
        )
        findNavController().navigate(action)
    }

    /**
     * Observe the error message and show the returned text in a toast
     */
    private fun showErrorMessage() {
        userAccountViewModel.getErrorMessage().observe(this, Observer {
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
        })
    }

    /**
     * When the session expired then clear the data and force the user to login again
     */
    private fun logoutUser() {
        userAccountViewModel.logoutUser().observe(this, Observer {
            if (it) {
                userAccountViewModel.clearData()
                requireActivity().restart()
            }
        })
    }
}