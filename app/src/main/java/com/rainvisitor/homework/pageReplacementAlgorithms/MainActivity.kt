package com.rainvisitor.homework.pageReplacementAlgorithms

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.data.CombinedData
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.rainvisitor.homework.pageReplacementAlgorithms.models.FIFO
import com.rainvisitor.homework.pageReplacementAlgorithms.models.Optimal
import com.rainvisitor.homework.pageReplacementAlgorithms.models.PageReplacement
import com.rainvisitor.homework.pageReplacementAlgorithms.models.Timer
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.ArrayList

private const val TAG = "MainActivity"
private const val REFERENCE_TIMES = 100000

enum class PageReplacementAlgorithm {
    FIFO, Optimal, EnhancedSecondChance, Rainvisitor
}

val numberOfFramesArray = arrayOf(20,40,60,80,100)
val sampleReferenceStrings1 = arrayOf("E","F", "A", "B", "F", "C", "F", "D", "B", "C", "F", "C", "B", "A", "B")
val sampleReferenceStrings2 = arrayOf("7","0", "1", "2", "0", "3", "0", "4", "2", "3", "0", "3", "2", "1", "2", "0", "1", "7", "0", "1")

class MainActivity : AppCompatActivity() {
    private val referenceStrings = ArrayList<String>()

    private val fifoList = ArrayList<FIFO>()
    private val optimalList = ArrayList<Optimal>()

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
        for (numberOfFrames in numberOfFramesArray) {
            fifoList.add(FIFO(numberOfFrames))
            optimalList.add(Optimal(numberOfFrames))
        }
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
        progressBar.visibility = View.VISIBLE
        Thread {
            val totalTime = Timer()
            for (i in 0 until numberOfFramesArray.size) {
                val referenceStrings: ArrayList<String> = ArrayList()
                for (j in 1..REFERENCE_TIMES) {
                    referenceStrings.addAll(random())
                }
                fifoList[i].execute(referenceStrings.toList())
                optimalList[i].execute(referenceStrings.toList())
                Log.e(TAG, "fifoList ${fifoList[i].pageFaults}")
                Log.e(TAG, "optimalList ${optimalList[i].pageFaults}")
                Log.e(TAG, "referenceStrings ${referenceStrings.size}")
            }
            totalTime.stop()
            updateData()
            runOnUiThread {
                progressBar.visibility = View.GONE
            }
        }.start()
    }

    private fun random(): MutableList<String> {
        val pickSize = random.nextInt(5 + 1)
        val startIndex = random.nextInt(referenceStrings.size - pickSize)
        return referenceStrings.subList(startIndex, startIndex + pickSize)
    }

    private fun updateData() {
        val lines = ArrayList<ILineDataSet>()
        lines.add(getLineData(fifoList))
        lines.add(getLineData(optimalList))
        val lineData = LineData(lines)
        lineData.setValueFormatter { value, entry, dataSetIndex, viewPortHandler ->
            String.format(
                "%,d %s",
                if (value > 1000f) (value / 1000f).toInt() else value.toInt(),
                if (value > 1000f) "k" else ""
            )
        }
        combinedChart.data = lineData
        combinedChart.notifyDataSetChanged()
        combinedChart.invalidate() // refresh
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
        //dataSet.setCircleColor(ContextCompat.getColor(mActivity, R.color.red_500))

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
