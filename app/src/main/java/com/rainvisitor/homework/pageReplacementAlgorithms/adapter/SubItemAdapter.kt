package com.rainvisitor.homework.pageReplacementAlgorithms.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rainvisitor.homework.pageReplacementAlgorithms.R
import com.rainvisitor.homework.pageReplacementAlgorithms.models.*
import kotlinx.android.synthetic.main.sub_item.view.*

class SubItemAdapter(
        private val context: Context,
        private val dataType: DataType,
        private val fifoList: MutableList<FIFO>,
        private val optimalList: MutableList<Optimal>,
        private val escList: MutableList<EnhancesSecondChance>,
        private val myWayList: MutableList<MyWay>) : RecyclerView.Adapter<SubItemAdapter.ViewHolder>() {

    init {

    }

    override fun getItemCount(): Int {
        return fifoList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.sub_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        holder.bindView(context, dataType, fifoList[position], optimalList[position], escList[position], myWayList[position])
    }

    class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        fun bindView(context: Context,
                     dataType: DataType, fifo: PageReplacement, optimal: PageReplacement, esc: PageReplacement, myWay: PageReplacement) {
            mView.textTitle.text = fifo.frames.size.toString()
            when (dataType) {
                DataType.PageFault -> {
                    mView.textFIFO.text = fifo.pageFaults.toString()
                    mView.textOptimal.text = optimal.pageFaults.toString()
                    mView.textESC.text = esc.pageFaults.toString()
                    mView.textMyWay.text = myWay.pageFaults.toString()
                }
                DataType.DiskIO -> {
                    mView.textFIFO.text = fifo.writeDisk.toString()
                    mView.textOptimal.text = optimal.writeDisk.toString()
                    mView.textESC.text = esc.writeDisk.toString()
                    mView.textMyWay.text = myWay.writeDisk.toString()
                }
                DataType.Interrupt -> {
                    mView.textFIFO.text = fifo.interrupt.toString()
                    mView.textOptimal.text = optimal.interrupt.toString()
                    mView.textESC.text = esc.interrupt.toString()
                    mView.textMyWay.text = myWay.interrupt.toString()
                }
            }
        }
    }
}
