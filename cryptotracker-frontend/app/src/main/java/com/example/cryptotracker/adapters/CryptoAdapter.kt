package com.example.cryptotracker.adapters

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cryptotracker.R
import com.example.cryptotracker.models.CryptoPrice
import java.text.NumberFormat
import java.util.*

class CryptoAdapter(
    private val onItemClick: (CryptoPrice) -> Unit,
    private val onFavoriteClick: (CryptoPrice) -> Unit
) : ListAdapter<CryptoPrice, CryptoAdapter.CryptoViewHolder>(CryptoDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CryptoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_crypto, parent, false)
        Log.d("CryptoAdapter", "✅ ViewHolder created")
        return CryptoViewHolder(view)
    }

    override fun onBindViewHolder(holder: CryptoViewHolder, position: Int) {
        val crypto = getItem(position)
        Log.d("CryptoAdapter", "🔄 Binding item at position $position: ${crypto.name}")
        holder.bind(crypto)
    }

    override fun getItemCount(): Int {
        val count = super.getItemCount()
        Log.d("CryptoAdapter", "📊 Item count: $count")
        return count
    }

    inner class CryptoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivIcon: ImageView = itemView.findViewById(R.id.ivCryptoIcon)
        private val tvName: TextView = itemView.findViewById(R.id.tvCryptoName)
        private val tvSymbol: TextView = itemView.findViewById(R.id.tvCryptoSymbol)
        private val tvPrice: TextView = itemView.findViewById(R.id.tvCryptoPrice)
        private val tvChange: TextView = itemView.findViewById(R.id.tvPriceChange)
        private val ivFavorite: ImageView = itemView.findViewById(R.id.ivFavorite)
        private val ivChart: ImageView = itemView.findViewById(R.id.ivMiniChart)

        fun bind(crypto: CryptoPrice) {
            Log.d("CryptoAdapter", "📝 Binding: ${crypto.name} - $${crypto.price}")

            tvName.text = crypto.name
            tvSymbol.text = crypto.symbol

            // Format price
            val priceFormat = NumberFormat.getCurrencyInstance(Locale.US)
            tvPrice.text = priceFormat.format(crypto.price)

            // Format percentage change
            val percentChange = String.format("%.2f%%", crypto.priceChangePercent24h)
            tvChange.text = if (crypto.priceChangePercent24h >= 0) "+$percentChange" else percentChange

            // Set color
            val changeColor = if (crypto.priceChangePercent24h >= 0) {
                Color.parseColor("#00C853")
            } else {
                Color.parseColor("#FF3D00")
            }
            tvChange.setTextColor(changeColor)

            // Set icons
            when (crypto.symbol) {
                "BTC" -> ivIcon.setImageResource(R.drawable.ic_bitcoin)
                "ETH" -> ivIcon.setImageResource(R.drawable.ic_ethereum)
                "ADA" -> ivIcon.setImageResource(R.drawable.ic_cardano)
                else -> ivIcon.setImageResource(R.drawable.ic_crypto_placeholder)
            }

            ivFavorite.setImageResource(
                if (crypto.isFavorite) R.drawable.ic_star_filled
                else R.drawable.ic_star_outline
            )

            ivChart.setImageResource(
                if (crypto.priceChangePercent24h >= 0) R.drawable.ic_chart_up
                else R.drawable.ic_chart_down
            )
            ivChart.setColorFilter(changeColor)

            itemView.setOnClickListener { onItemClick(crypto) }
            ivFavorite.setOnClickListener { onFavoriteClick(crypto) }
        }
    }

    class CryptoDiffCallback : DiffUtil.ItemCallback<CryptoPrice>() {
        override fun areItemsTheSame(oldItem: CryptoPrice, newItem: CryptoPrice): Boolean {
            return oldItem.symbol == newItem.symbol
        }

        override fun areContentsTheSame(oldItem: CryptoPrice, newItem: CryptoPrice): Boolean {
            return oldItem == newItem
        }
    }
}