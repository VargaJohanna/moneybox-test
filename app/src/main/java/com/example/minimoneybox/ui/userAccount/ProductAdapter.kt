package com.example.minimoneybox.ui.userAccount

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.minimoneybox.R
import com.example.minimoneybox.data.InvestorProductData
import kotlinx.android.synthetic.main.row_account.view.*

class ProductAdapter(
    private var productList: List<InvestorProductData>,
    private val itemClickListener: ItemClickListener
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ProductViewHolder(inflater.inflate(R.layout.row_account, parent, false))
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(productList[position])
    }

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(product: InvestorProductData) {
            itemView.product_friendly_name.text = product.name
            itemView.product_plan_value.text =
                String.format(itemView.context.getString(R.string.product_plan_value), product.planValue.toString())
            itemView.moneybox_value.text = String.format(
                itemView.context.getString(R.string.product_moneybox_value),
                product.moneyBoxValue.toString()
            )
            itemView.account_card.setCardBackgroundColor(Color.parseColor(product.productColour))
        }

        init {
            itemView.setOnClickListener {
                itemClickListener.onItemClick(productList[adapterPosition])
            }
        }
    }

    fun updateList(list: List<InvestorProductData>) {
        this.productList = list
        notifyDataSetChanged()
    }

    interface ItemClickListener {
        fun onItemClick(product: InvestorProductData)
    }
}