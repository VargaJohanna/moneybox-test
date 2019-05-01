package com.example.minimoneybox.ui.userAccount

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.minimoneybox.R
import com.example.minimoneybox.ext.display
import kotlinx.android.synthetic.main.fragment_user_account.*
import org.koin.androidx.viewmodel.ext.viewModel

class UserAccountFragment: Fragment() {
    private val userAccountViewModel: UserAccountViewModel by viewModel()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_user_account, container, false).apply {
            showName()
            showTotalPlanValue()
        }
    }

    private fun showName() {
        userAccountViewModel.getName().observe(requireActivity(), Observer {
            if(it.isNotEmpty()) {
                user_name.display(true)
                user_name.text = it
            }
        })
    }

    private fun showTotalPlanValue() {
        userAccountViewModel.getTotalPlanValue().observe(requireActivity(), Observer {
            if(it != 0f) total_plan_value.text = it.toString()
        })
    }
}