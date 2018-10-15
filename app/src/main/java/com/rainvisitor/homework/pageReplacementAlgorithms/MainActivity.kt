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
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.ArrayList

private const val TAG = "MainActivity"
private const val REFERENCE_TIMES = 100

enum class PageReplacementAlgorithm {
    FIFO, Optimal, EnhancedSecondChance, Rainvisitor
}

val numberOfFramesArray = arrayOf(3)

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
            String.format("%,d %s", if (value > 1000f) (value / 1000f).toInt() else value.toInt(), if (value > 1000f) "k" else "")
        }
        progressBar.visibility = View.VISIBLE
        Thread {
            val start = Date()
            for (i in 0 until numberOfFramesArray.size) {
                val referenceStrings: ArrayList<String> = ArrayList()
                /*for (j in 1..REFERENCE_TIMES) {
                    referenceStrings.addAll(random())
                }*/
                referenceStrings.add("E")
                referenceStrings.add("F")
                referenceStrings.add("A")
                referenceStrings.add("B")
                referenceStrings.add("F")
                referenceStrings.add("C")
                referenceStrings.add("F")
                referenceStrings.add("D")
                referenceStrings.add("B")
                referenceStrings.add("C")
                referenceStrings.add("F")
                referenceStrings.add("C")
                referenceStrings.add("B")
                referenceStrings.add("A")
                referenceStrings.add("B")
                fifoList[i].execute(referenceStrings)
                optimalList[i].execute(referenceStrings)
                Log.e(TAG, "fifoList ${fifoList[i].pageFaults}")
                Log.e(TAG, "optimalList ${optimalList[i].pageFaults}")
                Log.e(TAG, "referenceStrings ${referenceStrings.size}")
            }
            val end = Date()
            val lines = ArrayList<ILineDataSet>()
            lines.add(getLineData(fifoList))
            lines.add(getLineData(optimalList))
            combinedChart.data = LineData(lines)
            combinedChart.notifyDataSetChanged()
            combinedChart.invalidate() // refresh
            runOnUiThread {
                Log.e(TAG, "$start")
                Log.e(TAG, "$end")
                Log.e(TAG, "${Date(end.time - start.time)}")
                progressBar.visibility = View.GONE
            }
        }.start()
    }

    private fun random(): MutableList<String> {
        val pickSize = 5
        val startIndex = random.nextInt(referenceStrings.size - pickSize)
        return referenceStrings.subList(startIndex, startIndex + random.nextInt(pickSize + 1))
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
