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
private const val REFERENCE_TIMES = 100000

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
    private var localityStartIndex = 0

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
        randomLineChart.description.text = "Random"
        randomLineChart.description.textSize = 16f
        localityLineChart.description.text = "Locality"
        localityLineChart.description.textSize = 16f
        progressBar.visibility = View.VISIBLE
        Thread {
            val referenceStringsTime = Timer("referenceStringsTime")
            val referenceStrings: ArrayList<String> = ArrayList()
            for (j in 1..REFERENCE_TIMES)
                referenceStrings.addAll(random())
            referenceStringsTime.stop()
            updateData(execute(referenceStrings), randomLineChart)
            referenceStrings.clear()
            val pickSize = random.nextInt(5 + 1)
            for (j in 1..REFERENCE_TIMES)
                referenceStrings.addAll(locality(pickSize))
            updateData(execute(referenceStrings), localityLineChart)
            runOnUiThread {
                progressBar.visibility = View.GONE
            }
        }.start()
    }

    private fun execute(referenceStrings: ArrayList<String>): ArrayList<ILineDataSet> {
        val fifoList = ArrayList<FIFO>()
        val optimalList = ArrayList<Optimal>()
        val enhancedSecondChanceList = ArrayList<EnhancesSecondChance>()
        for (numberOfFrames in numberOfFramesArray) {
            fifoList.add(FIFO(numberOfFrames))
            optimalList.add(Optimal(numberOfFrames))
            enhancedSecondChanceList.add(EnhancesSecondChance(numberOfFrames))
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
            Log.e(TAG, "fifoList ${fifoList[i].pageFaults}")
            Log.e(TAG, "optimalList ${optimalList[i].pageFaults}")
            Log.e(TAG, "enhancedSecondChanceList ${enhancedSecondChanceList[i].pageFaults}")
        }
        totalTime.stop()
        val lines = ArrayList<ILineDataSet>()
        lines.add(getLineData(fifoList))
        lines.add(getLineData(optimalList))
        lines.add(getLineData(enhancedSecondChanceList))
        return lines
    }

    private fun random(): MutableList<String> {
        val pickSize = random.nextInt(5 + 1)
        val startIndex = random.nextInt(referenceStrings.size - pickSize)
        return referenceStrings.subList(startIndex, startIndex + pickSize)
    }

    private fun locality(pickSize: Int): MutableList<String> {
        val shift = random.nextInt(16)
        localityStartIndex += (shift - 8)
        if (localityStartIndex < 0)
            localityStartIndex = 0
        else if (localityStartIndex + pickSize >= referenceStrings.size)
            localityStartIndex = referenceStrings.size - pickSize
        return referenceStrings.subList(localityStartIndex, localityStartIndex + pickSize)
    }

    private fun updateData(iLineDataSetList: ArrayList<ILineDataSet>, lineChart: LineChart) {
        val lineData = LineData(iLineDataSetList)
        lineData.setValueFormatter { value, entry, dataSetIndex, viewPortHandler ->
            String.format(
                    "%,d %s",
                    if (value > 1000f) (value / 1000f).toInt() else value.toInt(),
                    if (value > 1000f) "k" else ""
            )
        }
        lineChart.data = lineData
        lineChart.notifyDataSetChanged()
        lineChart.invalidate() // refresh
    }

    @SuppressLint("DefaultLocale")
    private fun getLineData(dataObjects: List<PageReplacement>): ILineDataSet {
        val dataSet = LineDataSet(getLineEntry(dataObjects), dataObjects.first().label)
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

    private fun getLineEntry(dataObjects: List<PageReplacement>): List<Entry> {
        val entries = ArrayList<Entry>()
        for (data in dataObjects) {
            data.apply {
                entries.add(Entry(numberOfFrames.toFloat(), pageFaults.toFloat()))
            }
        }
        return entries
    }
}
