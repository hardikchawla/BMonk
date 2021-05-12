package com.example.bmonk

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_second.*
import kotlinx.android.synthetic.main.level_dialog.view.*
import kotlinx.android.synthetic.main.setting_dialog.view.*
import java.util.*


class SecondActivity : AppCompatActivity() {

    //Timer variables
    var timerRunning = false
    var testNumber = 10L
    var testNumber2 = 20L
    var testNumber3 = 0
    lateinit var countDownTimer: CountDownTimer
    var RemainingTime = 0
    var savedRemainingTime = 40 // 40 is just random

    //Settings variables
    lateinit var dialogBuilder: AlertDialog.Builder
    lateinit var dialog: AlertDialog
    var focNumb = 0
    var breNumb = 0
    var sessNumb = 0
    var currentLevel = 1

    lateinit var handler: Handler

    private lateinit var mRandom: Random
    private lateinit var mHandler: Handler
    private lateinit var mRunnable:Runnable

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        val nAlertDialog = AlertDialog.Builder(this)
        nAlertDialog.setTitle("Information")
        nAlertDialog.setMessage("Go to Settings to choose your desired settings and then click Start Button")
//        nAlertDialog.setIcon(R.mipmap.ic_launcher)
        nAlertDialog.setPositiveButton("Ok") {dialog: DialogInterface?, which: Int ->

            dialog?.dismiss()

        }
        nAlertDialog.show()



//        if(currentLevel == 1) {
//
//            AspiringMonk.text = "Distracted Baby"
//            progressBar.max = 40
//        }
//
//        if(currentLevel == 2) {
//            AspiringMonk.text = "Aspiring Monk"
//            progressBar.max = 2
//            progressTimeLeft.text = "2"
//        }
//
//        if(currentLevel == 3) {
//
//            AspiringMonk.text = "Calm in calamity"
//        }
//
//        if(currentLevel == 4) {
//
//            AspiringMonk.text = "Glimpse of inner peace"
//        }

        loadRemainingTime()
        progressBar.progress = savedRemainingTime

        if(savedRemainingTime <= 0) {
            currentLevel++
        }

//        mRandom = Random()
//        mHandler = Handler()
//        mRunnable = Runnable {
//
//        }

    }

    fun settingButton(view: View) {

//        SettingDialog().show(supportFragmentManager, "settingDialog")
        createSettingButton()
    }

    fun startButton(view: View) {
        startButton.visibility = View.INVISIBLE
        levelUp.visibility = View.INVISIBLE
        progressBar.visibility = View.INVISIBLE
        clock.visibility = View.INVISIBLE
        timer.visibility = View.VISIBLE
        stopButton.visibility = View.VISIBLE
        cancelButton.visibility = View.VISIBLE

//        for (i in 1..testNumber3) {
//            timerRunning = false
//            startStop()
//        }

        startStop()


    }

    @SuppressLint("SetTextI18n")
    fun stopButton(view: View) {

        if (stopButton.text == "RESUME") {
            startStop()
        } else {
            stopTimer()
            stopButton.text = "RESUME"
        }

    }

    @SuppressLint("SetTextI18n")
    fun startStop() {

        if (timerRunning) {
            stopTimer()
        } else {

//            stopButton.text = "STOP"
//
//            for (i in 1..testNumber3) {
//            startTimer()
//        }


            startTimer()
            stopButton.text = "PAUSE"
        }

    }

    fun startTimer() {
        countDownTimer = object : CountDownTimer(testNumber, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                testNumber = millisUntilFinished
                updateTimer()
            }

            override fun onFinish() {

                countDownTimer = object : CountDownTimer(testNumber2, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        testNumber2 = millisUntilFinished
                        updateTimer2()
                    }

                    @SuppressLint("SetTextI18n")
                    override fun onFinish() {

                        if(currentLevel == 1) {
                            RemainingTime = savedRemainingTime - focNumb

                            saveRemainingTime()

                        }

                    }
                }.start()
            }
        }.start()

        timerRunning = true
    }

    fun saveRemainingTime() {

        val sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.apply {

            putInt("REMAINING_TIME", RemainingTime)

        }.apply()

    }

    fun loadRemainingTime() {

        val sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)

         savedRemainingTime = sharedPreferences.getInt("REMAINING_TIME", 40)
        progressTimeLeft.text = savedRemainingTime.toString()

    }

    fun stopTimer() {
        countDownTimer.cancel()
        timerRunning = false
    }

    fun updateTimer() {

        val minutes = (testNumber / 60000).toInt()
        val seconds = (testNumber % 60000 / 1000).toInt()

        var timeLeftText: String

        timeLeftText = "$minutes"
        timeLeftText += ":"
        if (seconds < 10) timeLeftText += "0"
        timeLeftText += seconds

        timer.text = timeLeftText

    }

    fun updateTimer2() {

        val minutes = (testNumber2 / 60000).toInt()
        val seconds = (testNumber2 % 60000 / 1000).toInt()

        var timeLeftText: String

        timeLeftText = "$minutes"
        timeLeftText += ":"
        if (seconds < 10) timeLeftText += "0"
        timeLeftText += seconds

        timer.text = timeLeftText

    }

    @SuppressLint("SetTextI18n")
    fun currentLevelButton(view: View) {

        val mDialogView = LayoutInflater.from(this).inflate(R.layout.level_dialog, null)
        val mBuilder = AlertDialog.Builder(this)
                .setView(mDialogView)

        val mAlertDialog = mBuilder.show()

        if(currentLevel == 1) {

            AspiringMonk.text = "Distracted Baby"

            mDialogView.distractedBabyImage.visibility = View.VISIBLE
            mDialogView.distracted_babyText.visibility = View.VISIBLE
            mDialogView.distracted_baby_description.visibility = View.VISIBLE
            mDialogView.distractedBabyDivider.visibility = View.VISIBLE

        }

        if(currentLevel == 2) {

            AspiringMonk.text = "Aspiring Monk"

            mDialogView.aspiringMonkImage.visibility = View.VISIBLE
            mDialogView.aspiringMonkText.visibility = View.VISIBLE
            mDialogView.aspiring_monk_description.visibility = View.VISIBLE
            mDialogView.aspiringMonkDivider.visibility = View.VISIBLE

        }

        if(currentLevel == 3) {

            AspiringMonk.text = "Calm in calamity"

            mDialogView.calmInCalamityImage.visibility = View.VISIBLE
            mDialogView.calmInCalamityText.visibility = View.VISIBLE
            mDialogView.calmInCalamityDescription.visibility = View.VISIBLE
            mDialogView.calmInCalamityDivider.visibility = View.VISIBLE

        }

        if(currentLevel == 4) {

            AspiringMonk.text = "Glimpse of inner peace"

            mDialogView.glimpseOfInnerPeaceImage.visibility = View.VISIBLE
            mDialogView.glimpseOfInnerPeaceText.visibility = View.VISIBLE
            mDialogView.glimpseOfInnerPeaceDescription.visibility = View.VISIBLE
            mDialogView.glimpseOfInnerPeaceDivider.visibility = View.VISIBLE

        }

    }

    fun createSettingButton() {

//        dialogBuilder = AlertDialog.Builder(this)
//        val settingPopUp : View = layoutInflater.inflate(R.layout.setting_dialog, null)
//
//        dialogBuilder.setView(settingPopUp)
//        dialog = dialogBuilder.create()
//        dialog.show()

        val mDialogView = LayoutInflater.from(this).inflate(R.layout.setting_dialog, null)
        val mBuilder = AlertDialog.Builder(this)
                .setView(mDialogView)

        val mAlertDialog = mBuilder.show()

        //Saving SeekBarValues
        val sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)

        val savedFocus = sharedPreferences.getInt("SAVED_FOCUS", 0)
        val savedBreak = sharedPreferences.getInt("SAVED_BREAK", 0)
        val savedSession = sharedPreferences.getInt("SAVED_SESSION", 0)

        mDialogView.focusBar.progress = savedFocus
        mDialogView.focusNumber.text = savedFocus.toString()

        mDialogView.breakBar.progress = savedBreak
        mDialogView.breakNumber.text = savedBreak.toString()

        mDialogView.sessionsBar.progress = savedSession
        mDialogView.sessionsNumber.text = savedSession.toString()

        //Changing FocusBar
        mDialogView.focusBar.setOnSeekBarChangeListener(object :
                SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar,
                                           progress: Int, fromUser: Boolean) {

                mDialogView.focusNumber.setText(progress.toString())
            }

            override fun onStartTrackingTouch(seek: SeekBar) {
            }

            override fun onStopTrackingTouch(seek: SeekBar) {

                focNumb = (seek.progress.toString()).toInt()
                testNumber = (focNumb*60*1000).toLong()

            }
        })

        //Changing BreakBar
        mDialogView.breakBar.setOnSeekBarChangeListener(object :
                SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar,
                                           progress: Int, fromUser: Boolean) {

                mDialogView.breakNumber.setText(progress.toString())
            }

            override fun onStartTrackingTouch(seek: SeekBar) {
            }

            override fun onStopTrackingTouch(seek: SeekBar) {
                breNumb = (seek.progress.toString()).toInt()
                testNumber2 = (breNumb*60*1000).toLong()
            }
        })

        //Changing SessionBar
        mDialogView.sessionsBar.setOnSeekBarChangeListener(object :
                SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar,
                                           progress: Int, fromUser: Boolean) {

                mDialogView.sessionsNumber.setText(progress.toString())

            }

            override fun onStartTrackingTouch(seek: SeekBar) {
            }

            override fun onStopTrackingTouch(seek: SeekBar) {
                sessNumb = (seek.progress.toString()).toInt()
                testNumber3 = sessNumb
            }
        })

        mDialogView.tickButton.setOnClickListener {

            val sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.apply {

                putInt("SAVED_FOCUS", focNumb)
                putInt("SAVED_BREAK", breNumb)
                putInt("SAVED_SESSION", sessNumb)

            }.apply()

            mAlertDialog.dismiss()

        }
        mDialogView.crossButton.setOnClickListener {
            mAlertDialog.dismiss()
        }

    }

    fun cancelButton(view: View) {
        countDownTimer.cancel()
        testNumber = 0
        testNumber2 = 0

        startButton.visibility = View.VISIBLE
        levelUp.visibility = View.VISIBLE
        progressBar.visibility = View.VISIBLE
        clock.visibility = View.VISIBLE
        timer.visibility = View.INVISIBLE
        stopButton.visibility = View.INVISIBLE
        cancelButton.visibility = View.INVISIBLE
    }

}