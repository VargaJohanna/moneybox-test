package com.example.minimoneybox.ui.userAccount

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.minimoneybox.R
import com.example.minimoneybox.data.InvestorProductData
import com.example.minimoneybox.ext.display
import com.example.minimoneybox.ext.show
import kotlinx.android.synthetic.main.fragment_user_account.*
import kotlinx.android.synthetic.main.fragment_user_account.view.*
import org.koin.androidx.viewmodel.ext.viewModel

class UserAccountFragment : Fragment(), ProductAdapter.ItemClickListener {
    private val userAccountViewModel: UserAccountViewModel by viewModel()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val adapter = ProductAdapter(ArrayList(), this)
        return inflater.inflate(R.layout.fragment_user_account, container, false).apply {
            showName(account_progress_bar)
            showTotalPlanValue(account_progress_bar)
            generateProductList(adapter, account_recycler_view)
            observeProductList(adapter, account_progress_bar)
        }
    }

    private fun generateProductList(productAdapter: ProductAdapter, recyclerView: RecyclerView) {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = productAdapter
        }
    }

    private fun observeProductList(adapter: ProductAdapter, progressBar: ProgressBar) {
        progressBar.show(true)
        userAccountViewModel.getProductList().observe(requireActivity(), Observer {
            adapter.updateList(it)
            progressBar.show(false)
        })
    }

    private fun showName(progressbar: ProgressBar) {
        userAccountViewModel.getName().observe(requireActivity(), Observer {
            if (it.isNotEmpty()) {
                progressbar.show(false)
                user_name.display(true)
                user_name.text = String.format(getString(R.string.welcome_text), it)
            }
        })
    }

    private fun showTotalPlanValue(progressbar: ProgressBar) {
        userAccountViewModel.getTotalPlanValue().observe(requireActivity(), Observer {
            progressbar.show(false)
            if (it != 0f) total_plan_value.text = it.toString()
        })
    }

    override fun onItemClick(product: InvestorProductData) {

    }
}