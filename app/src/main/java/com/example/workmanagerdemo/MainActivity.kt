package com.example.workmanagerdemo

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.work.*
import com.example.workmanagerdemo.databinding.ActivityMainBinding
const val INPUT_DATA_KEY="input_data_key"
const val OUTPUT_DATA_KEY="output_data_key"
const val WORK_A_NAME="Work A"
const val WORK_B_NAME="Work B"
const val SHARED_PREFERENCES_NAME ="shp_name"

class MainActivity : AppCompatActivity(),SharedPreferences.OnSharedPreferenceChangeListener{
    private lateinit var binding:ActivityMainBinding
    private val workManager =WorkManager.getInstance(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val sp = getSharedPreferences(SHARED_PREFERENCES_NAME,Context.MODE_PRIVATE)
        sp.registerOnSharedPreferenceChangeListener(this)
        updateView()
        binding.button.setOnClickListener {
            val workRequestA = createWork(WORK_A_NAME)
            val workRequestB = createWork(WORK_B_NAME)
            workManager.beginWith(workRequestA)
                .then(workRequestB)
                .enqueue()

//            workManager.enqueue(workRequest)
//            workManager.getWorkInfoByIdLiveData(workRequest.id).observe(this, Observer {
//                Log.d("TAG", "onCreate: ${it.state}")
//                if (it.state ==WorkInfo.State.SUCCEEDED){
//                    Log.d("TAG", "onCreate: ${it.outputData.getString(OUTPUT_DATA_KEY)}")
//                }
//            })
        }

    }
    private fun updateView(){
        val sp = getSharedPreferences(SHARED_PREFERENCES_NAME,Context.MODE_PRIVATE)
       binding.textView.text= sp.getInt(WORK_A_NAME,0).toString()
       binding.textView2.text= sp.getInt(WORK_B_NAME,0).toString()
    }

    private fun createWork(name:String): OneTimeWorkRequest {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        return OneTimeWorkRequestBuilder<MyWorker>()
            .setConstraints(constraints)
            .setInputData(workDataOf(INPUT_DATA_KEY to name))
            .build()
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        updateView()
    }
}