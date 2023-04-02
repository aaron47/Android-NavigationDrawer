package com.aaron.navigationdrawer

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class MyService : Service() {

    val TAG = "MyService"

    init {
        Log.d(TAG, "init()")
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val dataStream = intent?.getStringExtra("EXTRA_DATA")
        dataStream?.let {
            Log.d(TAG, "onStartCommand() dataStream: $dataStream")
        }

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy()")
    }
}