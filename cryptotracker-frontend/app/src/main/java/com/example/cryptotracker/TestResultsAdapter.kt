package com.example.cryptotracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class TestResultsAdapter(
    private val testResults: List<TestResult>
) : RecyclerView.Adapter<TestResultsAdapter.TestViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_test_result, parent, false)
        return TestViewHolder(view)
    }

    override fun onBindViewHolder(holder: TestViewHolder, position: Int) {
        holder.bind(testResults[position], position + 1)
    }

    override fun getItemCount() = testResults.size

    inner class TestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTestNumber: TextView = itemView.findViewById(R.id.tvTestNumber)
        private val tvTestName: TextView = itemView.findViewById(R.id.tvTestName)
        private val tvTestTime: TextView = itemView.findViewById(R.id.tvTestTime)
        private val ivTestStatus: ImageView = itemView.findViewById(R.id.ivTestStatus)

        fun bind(testResult: TestResult, position: Int) {
            tvTestNumber.text = "#$position"
            tvTestName.text = testResult.testName

            val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            tvTestTime.text = timeFormat.format(Date(testResult.timestamp))

            if (testResult.passed) {
                ivTestStatus.setImageResource(R.drawable.ic_check_circle)
                ivTestStatus.setColorFilter(
                    itemView.context.getColor(R.color.green_profit)
                )
                itemView.setBackgroundColor(
                    itemView.context.getColor(R.color.test_pass_bg)
                )
            } else {
                ivTestStatus.setImageResource(R.drawable.ic_error_circle)
                ivTestStatus.setColorFilter(
                    itemView.context.getColor(R.color.red_loss)
                )
                itemView.setBackgroundColor(
                    itemView.context.getColor(R.color.test_fail_bg)
                )
            }
        }
    }
}
