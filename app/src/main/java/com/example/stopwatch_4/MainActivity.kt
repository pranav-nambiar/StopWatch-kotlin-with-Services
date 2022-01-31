package com.example.stopwatch_4

import android.animation.Animator
import android.animation.AnimatorInflater
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.RelativeSizeSpan
import android.view.Window
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.stopwatch_4.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private var running: Boolean = false
    private var milliseconds: Int = 0
    private lateinit var binding: ActivityMainBinding
    private lateinit var serviceIntent: Intent


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // This is to remove the title from the app
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        // Handle setting the display to the full screen after the title bar has been removed.
        // Handles both new and old APIs. FLAG_FULLSCREEN is now deprecated
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        // Set the layout when the app is launched
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val playbtn: Button = binding.startStopButton
        val resetbtn: Button = binding.resetButton
        val stopwatch: TextView = binding.stopwatch

        val blinkinganimator = AnimatorInflater.loadAnimator(this, R.animator.blink)
        blinkinganimator.setTarget(stopwatch)


        playbtn.setOnClickListener {
            if (!running) {
                startTimer(blinkinganimator, stopwatch, playbtn)

            } else {
                stopTimer(blinkinganimator, stopwatch, playbtn)
            }
        }

        resetbtn.setOnClickListener {
            resetTimer(blinkinganimator, stopwatch, playbtn)
        }

        stopwatch.text = getTimeStringFromInt(milliseconds)
        serviceIntent = Intent(applicationContext, TimerService::class.java)
        registerReceiver(updateTime, IntentFilter(TimerService.TIMER_UPDATED))

    }

    private fun startTimer(blinkinganimator: Animator, stopwatch: TextView, playbtn: Button)
    {
        serviceIntent.putExtra(TimerService.TIME_EXTRA, milliseconds)
        startService(serviceIntent)

        blinkinganimator.cancel()

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            stopwatch.setTextColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.white
                )
            )
        } else {
            stopwatch.setTextColor(getResources().getColor(R.color.white))
        }
        setPause(playbtn)
    }

    private fun stopTimer(blinkinganimator: Animator, stopwatch: TextView, playbtn: Button)
    {
        stopService(serviceIntent)
        blinkinganimator.start()
        setPlay(playbtn)
    }

    private fun resetTimer(blinkinganimator: Animator, stopwatch: TextView, playbtn: Button)
    {
        milliseconds = 0
        stopService(serviceIntent)
        blinkinganimator.cancel()

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            stopwatch.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
        } else {
            stopwatch.setTextColor(getResources().getColor(R.color.white))
        }

        setPlay(playbtn)

        stopwatch.text = getTimeStringFromInt(milliseconds)

    }

    private val updateTime: BroadcastReceiver = object : BroadcastReceiver()
    {
        override fun onReceive(context: Context, intent: Intent)
        {
            milliseconds = intent.getIntExtra(TimerService.TIME_EXTRA, 0)
            binding.stopwatch.text = getTimeStringFromInt(milliseconds)
        }
    }


    private fun setPause(playbtn: Button) {
        running = true
        playbtn.setText(R.string.pause)
        playbtn.setCompoundDrawablesWithIntrinsicBounds(
            R.drawable.ic_baseline_pause_24,
            0,
            0,
            0
        )
        playbtn.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.yellow))
    }

    private fun setPlay(playbtn: Button) {
        running = false
        playbtn.setText(R.string.play)
        playbtn.setCompoundDrawablesWithIntrinsicBounds(
            R.drawable.ic_baseline_play_arrow_24,
            0,
            0,
            0
        )
        playbtn.setBackgroundColor(
            ContextCompat.getColor(
                applicationContext,
                android.R.color.holo_green_dark
            )
        )
    }

    fun getTimeStringFromInt(milliseconds: Int): SpannableString
    {
        val hours = ((milliseconds / (1000 * 60 * 60))).toString().padStart(2, '0')
        val minutes = ((milliseconds / (1000 * 60)) % 60).toString().padStart(2, '0')
        val seconds = ((milliseconds / 1000) % 60).toString().padStart(2, '0')
        val milli = ((milliseconds % 1000) / 10).toString().padStart(2, '0')

        var time = ""
        time += if (hours == "00") "" else "$hours:"
        time += if (minutes != "00" || time != "") "$minutes:" else ""
        time += "$seconds:$milli"


        val timeSize = SpannableString(time)
        val len = timeSize.length
        timeSize.setSpan(
            RelativeSizeSpan(0.5f),
            len - 3,
            len,
            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
        )
        return timeSize
    }
}
