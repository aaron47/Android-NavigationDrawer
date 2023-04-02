package com.aaron.navigationdrawer

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.aaron.navigationdrawer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle

    private val NOTIFICATION_CHANNEL_ID = "my_channel_id"
    private val NOTIFICATION_CHANNEL_NAME = "my_channel_name"
    private val NOTIFICATION_ID = 0

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                lightColor = Color.RED
                enableLights(true)
            }

            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun hasPostNotificationsPermission() = ActivityCompat.checkSelfPermission(
        this,
        Manifest.permission.POST_NOTIFICATIONS
    ) == PackageManager.PERMISSION_GRANTED

    private fun requestPermissions() {
        var permissions = mutableListOf<String>()

        if (!hasPostNotificationsPermission()) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        if (permissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissions.toTypedArray(), 0)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 0 && grantResults.isNotEmpty()) {
            for (i in grantResults.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("MainActivity", "${permissions[i]} granted")
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE)
        }

        createNotificationChannel()
        val notification =
            NotificationCompat
                .Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentTitle("Awesome Notification")
                .setContentText("This is the notification content text")
                .setSmallIcon(R.drawable.ic_star)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .build()

        val notificationManager = NotificationManagerCompat.from(this)
        binding.btnShowNotification.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions()
            }
            notificationManager.notify(NOTIFICATION_ID, notification)
        }


        val sharedPref = getSharedPreferences("My Preferences", MODE_PRIVATE)
        val editor = sharedPref.edit()

        binding.btnSave.setOnClickListener {
            val name = binding.etName.text.toString()
            val age = binding.etAge.text.toString().toInt()
            val isAdult = binding.cbAdult.isChecked

            editor.apply {
                putString("name", name)
                putInt("age", age)
                putBoolean("isAdult", isAdult)
                apply()
            }
        }

        binding.btnLoad.setOnClickListener {
            val name = sharedPref.getString("name", null)
            val age = sharedPref.getInt("age", 0)
            val isAdult = sharedPref.getBoolean("isAdult", false)

            binding.etName.setText(name)
            binding.etAge.setText(age.toString())
            binding.cbAdult.isChecked = isAdult
        }

        toggle = ActionBarDrawerToggle(this, binding.drawerLayout, R.string.open, R.string.close)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.miItem1 -> Toast.makeText(this, "You clicked on item 1", Toast.LENGTH_SHORT).show()
                R.id.miItem2 -> Toast.makeText(this, "You clicked on item 2", Toast.LENGTH_SHORT).show()
                R.id.miItem3 -> Toast.makeText(this, "You clicked on item 3", Toast.LENGTH_SHORT).show()
            }
            true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}