package com.rainvisitor.homework.pageReplacementAlgorithms.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rainvisitor.homework.pageReplacementAlgorithms.R
import com.rainvisitor.homework.pageReplacementAlgorithms.models.*
import kotlinx.android.synthetic.main.item.view.*

class ItemAdapter(
        private val context: Context) : RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

    private val dataTypeList: ArrayList<DataType> = ArrayList()
    private val fifoList: ArrayList<MutableList<FIFO>> = ArrayList()
    private val optimalList: ArrayList<MutableList<Optimal>> = ArrayList()
    private val escList: ArrayList<MutableList<EnhancesSecondChance>> = ArrayList()
    private val myWayList: ArrayList<MutableList<MyWay>> = ArrayList()

    init {

    }

    override fun getItemCount(): Int {
        return fifoList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        holder.bindView(context, dataTypeList[position], fifoList[position], optimalList[position], escList[position], myWayList[position])
    }

    fun append(fifo: ArrayList<FIFO>, optimal: ArrayList<Optimal>, esc: ArrayList<EnhancesSecondChance>, myWay: ArrayList<MyWay>) {
        dataTypeList.add(DataType.PageFault)
        fifoList.add(fifo)
        optimalList.add(optimal)
        escList.add(esc)
        myWayList.add(myWay)
        dataTypeList.add(DataType.DiskIO)
        fifoList.add(fifo)
        optimalList.add(optimal)
        escList.add(esc)
        myWayList.add(myWay)
        dataTypeList.add(DataType.Interrupt)
        fifoList.add(fifo)
        optimalList.add(optimal)
        escList.add(esc)
        myWayList.add(myWay)
        notifyDataSetChanged()
    }

    class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        fun bindView(context: Context,
                     dataType: DataType, fifo: MutableList<FIFO>, optimal: MutableList<Optimal>, esc: MutableList<EnhancesSecondChance>, myWay: MutableList<MyWay>) {
            mView.textTitle.text = dataType.name
            mView.recyclerViewSubItem.adapter = SubItemAdapter(context, dataType, fifo, optimal, esc, myWay)
            mView.recyclerViewSubItem.layoutManager = LinearLayoutManager(context)
        }
    }
}
