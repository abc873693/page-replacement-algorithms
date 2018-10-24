package com.rainvisitor.homework.pageReplacementAlgorithms

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.CombinedData
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.rainvisitor.homework.pageReplacementAlgorithms.models.*
import com.rainvisitor.homework.pageReplacementAlgorithms.models.Timer
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.ArrayList

private const val TAG = "MainActivity"
private const val REFERENCE_MAX_SIZE = 100000

enum class PageReplacementAlgorithm {
    FIFO, Optimal, EnhancedSecondChance, Rainvisitor
}

val numberOfFramesArray = arrayOf(20, 40, 60, 80, 100)
val sampleReferenceStrings1 =
    arrayOf("E", "F", "A", "B", "F", "C", "F", "D", "B", "C", "F", "C", "B", "A", "B")
val sampleReferenceStrings2 =
    arrayOf("7", "0", "1", "2", "0", "3", "0", "4", "2", "3", "0", "3", "2", "1", "2", "0", "1", "7", "0", "1")

class MainActivity : AppCompatActivity() {
    private val referenceStrings = ArrayList<String>()

    private val random = Random()

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
        val combinedData = CombinedData()
        combinedData.setValueFormatter { value, entry, dataSetIndex, viewPortHandler ->
            String.format(
                "%,d %s",
                if (value > 1000f) (value / 1000f).toInt() else value.toInt(),
                if (value > 1000f) "k" else ""
            )
        }
        randomPFChart.description.text = "Random"
        randomPFChart.description.textSize = 16f
        localityPFChart.description.text = "Locality"
        localityPFChart.description.textSize = 16f
        mySelectPFChart.description.text = "MySelect"
        mySelectPFChart.description.textSize = 16f
        randomRDChart.description.text = "Random"
        randomRDChart.description.textSize = 16f
        localityRDChart.description.text = "Locality"
        localityRDChart.description.textSize = 16f
        mySelectRDChart.description.text = "MySelect"
        mySelectRDChart.description.textSize = 16f
        progressBar.visibility = View.VISIBLE
        Thread {
            val referenceStringsTime = Timer("referenceStringsTime")
            val referenceStrings: ArrayList<Page> = ArrayList()
            while (true) {
                referenceStrings.addAll(random())
                if (referenceStrings.size >= REFERENCE_MAX_SIZE) break
            }
            referenceStringsTime.stop()
            updateData(execute(referenceStrings), randomPFChart, randomRDChart)
            referenceStrings.clear()
            val pickSize = random.nextInt(25 + 1) + 25
            val maxCount = random.nextInt(500 + 1) + 500
            while (true) {
                referenceStrings.addAll(locality(pickSize, maxCount))
                if (referenceStrings.size >= REFERENCE_MAX_SIZE) break
            }
            updateData(execute(referenceStrings), localityPFChart, localityRDChart)
            referenceStrings.clear()
            while (true) {
                referenceStrings.addAll(mySelect())
                if (referenceStrings.size >= REFERENCE_MAX_SIZE) break
            }
            updateData(execute(referenceStrings), mySelectPFChart, mySelectRDChart)
            runOnUiThread {
                progressBar.visibility = View.GONE
            }
        }.start()
    }

    private fun execute(referenceStrings: ArrayList<Page>): Map<String, ArrayList<ILineDataSet>> {
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
            Log.e(TAG, "fifoList ${fifoList[i].pageFaults} ${fifoList[i].writeDisk}")
            Log.e(TAG, "optimalList ${optimalList[i].pageFaults} ${optimalList[i].writeDisk}")
            Log.e(
                TAG,
                "enhancedSecondChanceList ${enhancedSecondChanceList[i].pageFaults} ${enhancedSecondChanceList[i].writeDisk}"
            )
            Log.e(TAG, "myWayTime ${myWayList[i].pageFaults} ${myWayList[i].writeDisk}")
        }
        totalTime.stop()
        val lines = hashMapOf("PF" to ArrayList<ILineDataSet>(), "RD" to ArrayList<ILineDataSet>())
        lines["PF"]?.add(getPFData(fifoList))
        lines["PF"]?.add(getPFData(optimalList))
        lines["PF"]?.add(getPFData(enhancedSecondChanceList))
        lines["PF"]?.add(getPFData(myWayList))
        lines["RD"]?.add(getRDData(fifoList))
        lines["RD"]?.add(getRDData(optimalList))
        lines["RD"]?.add(getRDData(enhancedSecondChanceList))
        lines["RD"]?.add(getRDData(myWayList))
        return lines
    }

    private fun random(): MutableList<Page> {
        val pickSize = random.nextInt(5 + 1)
        val startIndex = random.nextInt(referenceStrings.size - pickSize)
        val list: MutableList<Page> = ArrayList()
        for (i in startIndex until startIndex + pickSize)
            list.add(Page(referenceStrings[i], random.nextBoolean()))
        return list
    }

    private fun locality(pickSize: Int, loopCount: Int): MutableList<Page> {
        val startIndex = random.nextInt(referenceStrings.size - pickSize)
        val list: MutableList<Page> = ArrayList()
        for (i in 1..loopCount)
            list.add(Page(referenceStrings[random.nextInt(startIndex + pickSize + 1)], random.nextBoolean()))
        val randomIndex = random.nextInt(referenceStrings.size)
        list.add(Page(referenceStrings[randomIndex], random.nextBoolean()))
        return list
    }

    private fun mySelect(): MutableList<Page> {
        val block = random.nextInt(10)
        val startIndex = block * 50
        val list: MutableList<Page> = ArrayList()
        for (i in startIndex until startIndex + 50)
            list.add(Page(referenceStrings[i], random.nextBoolean()))
        return list
    }

    private fun updateData(
        iLineDataSetList: Map<String, ArrayList<ILineDataSet>>,
        PFChart: LineChart,
        RDChart: LineChart
    ) {
        PFChart.data = LineData(iLineDataSetList["PF"])
        PFChart.notifyDataSetChanged()
        PFChart.invalidate() // refresh
        RDChart.data = LineData(iLineDataSetList["RD"])
        RDChart.notifyDataSetChanged()
        RDChart.invalidate() // refresh
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
}
