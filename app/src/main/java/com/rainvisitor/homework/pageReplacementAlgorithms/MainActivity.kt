package com.rainvisitor.homework.pageReplacementAlgorithms

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.CombinedData
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.rainvisitor.homework.pageReplacementAlgorithms.adapter.ItemAdapter
import com.rainvisitor.homework.pageReplacementAlgorithms.models.*
import com.rainvisitor.homework.pageReplacementAlgorithms.models.Timer
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.ArrayList

private const val TAG = "MainActivity"
//最多不能超過10萬
private const val REFERENCE_MAX_SIZE = 100000
//選取frame 20, 40, 60, 80, 100
val numberOfFramesArray = arrayOf(20, 40, 60, 80, 100)
val sampleReferenceStrings1 =
    arrayOf("E", "F", "A", "B", "F", "C", "F", "D", "B", "C", "F", "C", "B", "A", "B")
val sampleReferenceStrings2 =
    arrayOf("7", "0", "1", "2", "0", "3", "0", "4", "2", "3", "0", "3", "2", "1", "2", "0", "1", "7", "0", "1")

class MainActivity : AppCompatActivity() {
    private val referenceStrings = ArrayList<String>()

    private val random = Random()

    private lateinit var itemAdapter: ItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
        setView()
    }

    private fun init() {
        for (i in 1..500)
            referenceStrings.add(i.toString())
    }

    private fun setView() {

        itemAdapter = ItemAdapter(this)
        recyclerViewItem.layoutManager = LinearLayoutManager(this)
        recyclerViewItem.adapter = itemAdapter
        val combinedData = CombinedData()
        combinedData.setValueFormatter { value, entry, dataSetIndex, viewPortHandler ->
            String.format(
                "%,d %s",
                if (value > 1000f) (value / 1000f).toInt() else value.toInt(),
                if (value > 1000f) "k" else ""
            )
        }
        randomPFChart.description.text = "Random Page Fault"
        randomPFChart.description.textSize = 16f
        localityPFChart.description.text = "Locality Page Fault"
        localityPFChart.description.textSize = 16f
        mySelectPFChart.description.text = "MySelect Page Fault"
        mySelectPFChart.description.textSize = 16f
        randomRDChart.description.text = "Random Disk I/O"
        randomRDChart.description.textSize = 16f
        localityRDChart.description.text = "Locality Disk I/O"
        localityRDChart.description.textSize = 16f
        mySelectRDChart.description.text = "MySelect Disk I/O"
        mySelectRDChart.description.textSize = 16f
        randomInterruptChart.description.text = "Random Interrupt"
        randomInterruptChart.description.textSize = 16f
        localityInterruptChart.description.text = "Locality Interrupt"
        localityInterruptChart.description.textSize = 16f
        mySelectInterruptChart.description.text = "MySelect Interrupt"
        mySelectInterruptChart.description.textSize = 16f
        progressBar.visibility = View.VISIBLE
        Thread {
            val referenceStringsTime = Timer("referenceStringsTime")
            val referenceStrings: ArrayList<Page> = ArrayList()
            while (true) {
                referenceStrings.addAll(random())
                if (referenceStrings.size >= REFERENCE_MAX_SIZE) break
            }
            /*sampleReferenceStrings1.forEach { i ->
                referenceStrings.add(Page(i, random.nextBoolean()))
            }*/
            referenceStringsTime.stop()
            updateData(
                execute(ReferenceType.Random, referenceStrings),
                randomPFChart,
                randomRDChart,
                randomInterruptChart
            )
            referenceStrings.clear()
            val pickSize = random.nextInt(25 + 1) + 25
            val maxCount = random.nextInt(500 + 1) + 500
            while (true) {
                referenceStrings.addAll(locality(pickSize, maxCount))
                if (referenceStrings.size >= REFERENCE_MAX_SIZE) break
            }
            updateData(
                execute(ReferenceType.Locality, referenceStrings),
                localityPFChart,
                localityRDChart,
                localityInterruptChart
            )
            referenceStrings.clear()
            while (true) {
                referenceStrings.addAll(mySelect())
                if (referenceStrings.size >= REFERENCE_MAX_SIZE) break
            }
            updateData(
                execute(ReferenceType.MySelect, referenceStrings),
                mySelectPFChart,
                mySelectRDChart,
                mySelectInterruptChart
            )
            runOnUiThread {
                progressBar.visibility = View.GONE
            }
        }.start()
    }

    private fun execute(
        referenceType: ReferenceType,
        referenceStrings: ArrayList<Page>
    ): Map<String, ArrayList<ILineDataSet>> {
        val fifoList = ArrayList<FIFO>()
        val optimalList = ArrayList<Optimal>()
        val enhancedSecondChanceList = ArrayList<EnhancesSecondChance>()
        val myWayList = ArrayList<MyWay>()
        for (numberOfFrames in numberOfFramesArray) {
            fifoList.add(FIFO(numberOfFrames))
            optimalList.add(Optimal(numberOfFrames))
            enhancedSecondChanceList.add(EnhancesSecondChance(numberOfFrames))
            myWayList.add(MyWay(numberOfFrames))
        }
        val totalTime = Timer("totalTime")
        for (i in 0 until numberOfFramesArray.size) {
            Log.e(TAG, "referenceStrings ${referenceStrings.size}")
            val fifoTime = Timer("FIFO")
            fifoList[i].execute(referenceStrings.toList())
            fifoTime.stop()
            val optimalTime = Timer("Optimal")
            optimalList[i].execute(referenceStrings.toList())
            optimalTime.stop()
            val secondChanceTime = Timer("EnhancedSecondChance")
            enhancedSecondChanceList[i].execute(referenceStrings.toList())
            secondChanceTime.stop()
            val myWayTime = Timer("EnhancedSecondChance")
            myWayList[i].execute(referenceStrings.toList())
            myWayTime.stop()
            Log.e(TAG, "fifoList ${fifoList[i].pageFaults} ${fifoList[i].writeDisk} ${fifoList[i].interrupt}")
            Log.e(
                TAG,
                "optimalList ${optimalList[i].pageFaults} ${optimalList[i].writeDisk} ${optimalList[i].interrupt}"
            )
            Log.e(
                TAG,
                "enhancedSecondChanceList ${enhancedSecondChanceList[i].pageFaults} ${enhancedSecondChanceList[i].writeDisk} ${enhancedSecondChanceList[i].interrupt}"
            )
            Log.e(TAG, "myWayTime ${myWayList[i].pageFaults} ${myWayList[i].writeDisk} ${myWayList[i].interrupt}")
        }
        totalTime.stop()
        val lines = hashMapOf(
            "PF" to ArrayList<ILineDataSet>(),
            "Interrupt" to ArrayList<ILineDataSet>(),
            "RD" to ArrayList<ILineDataSet>()
        )
        var pd = 0
        var rd = 0
        var interrupt = 0
        numberOfFramesArray.forEachIndexed { i, _ ->
            if (fifoList[i].pageFaults > myWayList[i].pageFaults)
                pd++
            if (fifoList[i].writeDisk > myWayList[i].writeDisk)
                rd++
            if (fifoList[i].interrupt > myWayList[i].interrupt)
                interrupt++
        }
        runOnUiThread {
            textTitle.append("\n${pd} ${rd} ${interrupt}")
            itemAdapter.append(referenceType, fifoList, optimalList, enhancedSecondChanceList, myWayList)
        }
        lines["PF"]?.add(getPFData(fifoList))
        lines["PF"]?.add(getPFData(optimalList))
        lines["PF"]?.add(getPFData(enhancedSecondChanceList))
        lines["PF"]?.add(getPFData(myWayList))
        lines["RD"]?.add(getRDData(fifoList))
        lines["RD"]?.add(getRDData(optimalList))
        lines["RD"]?.add(getRDData(enhancedSecondChanceList))
        lines["RD"]?.add(getRDData(myWayList))
        lines["Interrupt"]?.add(getInterruptData(fifoList))
        lines["Interrupt"]?.add(getInterruptData(optimalList))
        lines["Interrupt"]?.add(getInterruptData(enhancedSecondChanceList))
        lines["Interrupt"]?.add(getInterruptData(myWayList))
        return lines
    }

    private fun random(): MutableList<Page> {
        //亂數每次選取連續序列的大小(1~5)
        val pickSize = random.nextInt(5 + 1)
        //亂數起始index
        val startIndex = random.nextInt(referenceStrings.size - pickSize)
        val list: MutableList<Page> = ArrayList()
        //依序放入Reference String
        for (i in startIndex until startIndex + pickSize)
            list.add(Page(referenceStrings[i], random.nextBoolean()))
        return list
    }

    private fun locality(pickSize: Int, loopCount: Int): MutableList<Page> {
        //亂數起始index pickSize為一開始就給定 區域序列固定大小(25~50) loopCount代表這次區間的總數
        val startIndex = random.nextInt(referenceStrings.size - pickSize)
        val list: MutableList<Page> = ArrayList()
        //重複loopCount次 從起始索引的範圍 隨機取得變數 放入Reference String
        for (i in 0..loopCount)
            list.add(Page(referenceStrings[random.nextInt(pickSize + 1) + startIndex], random.nextBoolean()))
        //在兩兩locality 插入一個Random page
        val randomIndex = random.nextInt(referenceStrings.size)
        list.add(Page(referenceStrings[randomIndex], random.nextBoolean()))
        return list
    }

    private fun mySelect(): MutableList<Page> {
        //將序列切割成10等分
        val block = random.nextInt(10)
        val startIndex = block * 50
        val list: MutableList<Page> = ArrayList()
        //每次亂數選取該區間的連續page 放入Reference String
        for (i in startIndex until startIndex + 50)
            list.add(Page(referenceStrings[i], random.nextBoolean()))
        return list
    }

    //以下皆為圖表呈現
    private fun updateData(
        iLineDataSetList: Map<String, ArrayList<ILineDataSet>>,
        PFChart: LineChart,
        RDChart: LineChart,
        InterruptChart: LineChart
    ) {
        PFChart.data = LineData(iLineDataSetList["PF"])
        PFChart.notifyDataSetChanged()
        PFChart.invalidate() // refresh
        RDChart.data = LineData(iLineDataSetList["RD"])
        RDChart.notifyDataSetChanged()
        RDChart.invalidate() // refresh
        InterruptChart.data = LineData(iLineDataSetList["Interrupt"])
        InterruptChart.notifyDataSetChanged()
        InterruptChart.invalidate() // refresh
    }

    @SuppressLint("DefaultLocale")
    private fun getPFData(dataObjects: List<PageReplacement>): ILineDataSet {
        val dataSet = LineDataSet(getPFEntry(dataObjects), dataObjects.first().label)
        dataSet.color = dataObjects.first().color
        dataSet.lineWidth = 2.5f
        dataSet.setDrawCircles(true)
        dataSet.setDrawCircleHole(false)
        dataSet.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
        dataSet.setDrawValues(false)
        dataSet.setCircleColor(dataObjects.first().color)

        //combinedData.setData(new LineData(dataSetB));
        return dataSet
    }

    @SuppressLint("DefaultLocale")
    private fun getRDData(dataObjects: List<PageReplacement>): ILineDataSet {
        val dataSet = LineDataSet(getRDEntry(dataObjects), dataObjects.first().label)
        dataSet.color = dataObjects.first().color
        dataSet.lineWidth = 2.5f
        dataSet.setDrawCircles(true)
        dataSet.setDrawCircleHole(false)
        dataSet.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
        dataSet.setDrawValues(false)
        dataSet.setCircleColor(dataObjects.first().color)

        //combinedData.setData(new LineData(dataSetB));
        return dataSet
    }

    @SuppressLint("DefaultLocale")
    private fun getInterruptData(dataObjects: List<PageReplacement>): ILineDataSet {
        val dataSet = LineDataSet(getInterruptEntry(dataObjects), dataObjects.first().label)
        dataSet.color = dataObjects.first().color
        dataSet.lineWidth = 2.5f
        dataSet.setDrawCircles(true)
        dataSet.setDrawCircleHole(false)
        dataSet.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
        dataSet.setDrawValues(false)
        dataSet.setCircleColor(dataObjects.first().color)

        //combinedData.setData(new LineData(dataSetB));
        return dataSet
    }

    private fun getPFEntry(dataObjects: List<PageReplacement>): List<Entry> {
        val entries = ArrayList<Entry>()
        for (data in dataObjects) {
            data.apply {
                entries.add(Entry(numberOfFrames.toFloat(), pageFaults.toFloat()))
            }
        }
        return entries
    }

    private fun getRDEntry(dataObjects: List<PageReplacement>): List<Entry> {
        val entries = ArrayList<Entry>()
        for (data in dataObjects) {
            data.apply {
                entries.add(Entry(numberOfFrames.toFloat(), writeDisk.toFloat()))
            }
        }
        return entries
    }

    private fun getInterruptEntry(dataObjects: List<PageReplacement>): List<Entry> {
        val entries = ArrayList<Entry>()
        for (data in dataObjects) {
            data.apply {
                entries.add(Entry(numberOfFrames.toFloat(), interrupt.toFloat()))
            }
        }
        return entries
    }
}
