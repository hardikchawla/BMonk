package com.example.bmonk

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.widget.CompoundButton
import android.widget.SeekBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_second.*
import kotlinx.android.synthetic.main.level_dialog.view.*
import kotlinx.android.synthetic.main.pomodoro_dialog.view.*
import kotlinx.android.synthetic.main.setting_dialog.view.*
import kotlinx.android.synthetic.main.usage_stats_dialog.view.*
import java.text.SimpleDateFormat
import java.util.*

class SecondActivity : AppCompatActivity() {

    //Timer variables
    var timerRunning = false
    var testNumber = 10L
    var testNumb = 20L
    var testNumb3 = 20
    var testNumber2 = 20L
    var testNumb2 = 30L
    var testNumber3 = 0
    lateinit var countDownTimer: CountDownTimer
    var remainingTime = 0
    var savedRemainingTime = 40 // 40 is just random
    var savedFocus = 0
    var savedBreak = 0
    var savedSession = 0
    private val defaultFocusTime = 10
    private val defaultBreakTime = 2
    private val defaultSessions = 2

    //Repeating Timer variables
    var a = 0L
    var b = 0L
    var breakTb = false
    var session2 = 0

    //Settings variables
    var focNumb = 0
    var breNumb = 0
    var sessNumb = 0

    //Sound variables
     var sound : MediaPlayer? = null

    //Stats variables
    var totalFocusTime = 0

    //Current Level variables
    var currentLevel = 1
    var savedCurrentLevel = 0
    var savedCurrentLevelText = ""

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        pomodoroDialog()

        val sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val firstStart = sharedPreferences.getBoolean("firstStart", true)

        if (firstStart) {
            showStartDialog()
        }

        loadSettings()
        loadStartingVariables()

        if(savedFocus == 0 || savedBreak == 0 || savedSession == 0) {
            showStartDialog()
        }

        loadCurrentLevel()
        loadRemainingTime()

//        if(savedRemainingTime <= 0) {
//            currentLevel++
//            savedRemainingTime = 0
//        }

//        if(currentLevel == 2) {
//            savedCurrentLevel = "Aspiring Monk"
//            progressBar.max = 240
//            progressTimeLeft.text = "240"
//            savedRemainingTime = 240
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


    }

    private fun showStartDialog() {
        val nAlertDialog = AlertDialog.Builder(this)
        nAlertDialog.setTitle("Information")
        nAlertDialog.setMessage("Go to Settings to choose your desired settings and then click Start Button")
//        nAlertDialog.setIcon(R.mipmap.ic_launcher)
        nAlertDialog.setPositiveButton("Ok") {dialog: DialogInterface?, which: Int ->

            dialog?.dismiss()

        }
        nAlertDialog.show()

        val sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.apply {

            putBoolean("firstStart", false)

        }.apply()
    }

    fun settingButton(view: View) {
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

        if(sound == null) {
            sound = MediaPlayer.create(this, R.raw.sound_meditation)

        }
        sound?.start()

        startStop()
        updateTimer()
    }

    private fun stopSound() {
        if(sound != null) {
            sound!!.release()
            sound = null
        }
    }

    fun stopButton(view: View) {
        startStop()
    }

    private fun startStop() {

        if (timerRunning) {
            pauseTimer()
        } else {
            sound?.start()
            startTimer()
        }

    }

    @SuppressLint("SetTextI18n")
    fun pauseTimer() {
        countDownTimer.cancel()
        timerRunning = false
        stopButton.text = "RESUME"

        sound?.pause()
    }

    private fun updateTimer() {

        val minutes = (testNumber / 60000).toInt()
        val seconds = (testNumber % 60000 / 1000).toInt()

        var timeLeftText: String

        timeLeftText = "$minutes"
        timeLeftText += ":"
        if (seconds < 10) timeLeftText += "0"
        timeLeftText += seconds

        timer.text = timeLeftText

    }

    private fun updateTimer2() {

        val minutes = (testNumber2 / 60000).toInt()
        val seconds = (testNumber2 % 60000 / 1000).toInt()

        var timeLeftText: String

        timeLeftText = "$minutes"
        timeLeftText += ":"
        if (seconds < 10) timeLeftText += "0"
        timeLeftText += seconds

        timer.text = timeLeftText

    }

    fun cancelButton(view: View) {
        countDownTimer.cancel()
        testNumber = 0
        testNumber2 = 0
        updateTimer()
        updateTimer2()

        stopSound()

        timerRunning = false

        startButton.visibility = View.VISIBLE
        levelUp.visibility = View.VISIBLE
        progressBar.visibility = View.VISIBLE
        clock.visibility = View.VISIBLE
        timer.visibility = View.INVISIBLE
        stopButton.visibility = View.INVISIBLE
        cancelButton.visibility = View.INVISIBLE
    }

    @SuppressLint("SetTextI18n")
    fun startTimer() {

        if(breakTb) {
            testNumb3--
        }
        repeatTimerOnceAgain(testNumb3)

//            countDownTimer = object : CountDownTimer(testNumber, 1000) {
//                override fun onTick(millisUntilFinished: Long) {
//                    testNumber = millisUntilFinished
//                    updateTimer()
//                }
//
//                override fun onFinish() {
//
//                    countDownTimer = object : CountDownTimer(testNumber2, 1000) {
//                        override fun onTick(millisUntilFinished: Long) {
//                            testNumber2 = millisUntilFinished
//                            updateTimer2()
//
//                        }
//
//                        @SuppressLint("SetTextI18n")
//                        override fun onFinish() {
//
//                            timerRunning = false
//
//                            if (currentLevel == 1) {
//                                RemainingTime = savedRemainingTime - focNumb
//
//                                saveRemainingTime()
//                            }
//
//                            loadRemainingTime()
//
//                            startButton.visibility = View.VISIBLE
//                            levelUp.visibility = View.VISIBLE
//                            progressBar.visibility = View.VISIBLE
//                            clock.visibility = View.VISIBLE
//                            timer.visibility = View.INVISIBLE
//                            stopButton.visibility = View.INVISIBLE
//                            cancelButton.visibility = View.INVISIBLE
//
//                        }
//                    }.start()
//                }
//            }.start()
//
//            timerRunning = true
//            stopButton.text = "PAUSE"

    }

    fun saveRemainingTime() {

        val sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.apply {

            putInt("REMAINING_TIME", remainingTime)

        }.apply()

    }

    fun loadRemainingTime() {

        val sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)


        savedRemainingTime = sharedPreferences.getInt("REMAINING_TIME", 1)

        progressTimeLeft.text = savedRemainingTime.toString()
        progressBar.progress = savedRemainingTime


    }

    fun saveCurrentLevel() {

        val sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.apply {

            putString("CURRENT_LEVEL_TEXT", AspiringMonk.toString())
            putInt("CURRENT_LEVEL", currentLevel)


        }.apply()

    }

    fun loadCurrentLevel() {

        val sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)

        savedCurrentLevelText = sharedPreferences.getString("CURRENT_LEVEL_TEXT", "Distracted Baby").toString()
        AspiringMonk.text = savedCurrentLevelText

        savedCurrentLevel = sharedPreferences.getInt("CURRENT_LEVEL", 1)

    }

    private fun loadSettings() {

        val sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)

        savedFocus = sharedPreferences.getInt("SAVED_FOCUS", defaultFocusTime)
        savedBreak = sharedPreferences.getInt("SAVED_BREAK", defaultBreakTime)
        savedSession = sharedPreferences.getInt("SAVED_SESSION", defaultSessions)

    }

    @SuppressLint("SetTextI18n")
    fun currentLevelButton(view: View) {

        val mDialogView = LayoutInflater.from(this).inflate(R.layout.level_dialog, null)
        val mBuilder = AlertDialog.Builder(this)
                .setView(mDialogView)

        val mAlertDialog = mBuilder.show()

        if(savedCurrentLevel == 1) {

            AspiringMonk.text = "Distracted Baby"

            mDialogView.distractedBabyImage.visibility = View.VISIBLE
            mDialogView.distracted_babyText.visibility = View.VISIBLE
            mDialogView.distracted_baby_description.visibility = View.VISIBLE
            mDialogView.distractedBabyDivider.visibility = View.VISIBLE

        } else if(savedCurrentLevel == 2) {

            AspiringMonk.text = "Aspiring Monk"

            mDialogView.aspiringMonkImage.visibility = View.VISIBLE
            mDialogView.aspiringMonkText.visibility = View.VISIBLE
            mDialogView.aspiring_monk_description.visibility = View.VISIBLE
            mDialogView.aspiringMonkDivider.visibility = View.VISIBLE

        } else if(savedCurrentLevel == 3) {

            AspiringMonk.text = "Calm in calamity"

            mDialogView.calmInCalamityImage.visibility = View.VISIBLE
            mDialogView.calmInCalamityText.visibility = View.VISIBLE
            mDialogView.calmInCalamityDescription.visibility = View.VISIBLE
            mDialogView.calmInCalamityDivider.visibility = View.VISIBLE

        } else {

            AspiringMonk.text = "Glimpse of inner peace"

            mDialogView.glimpseOfInnerPeaceImage.visibility = View.VISIBLE
            mDialogView.glimpseOfInnerPeaceText.visibility = View.VISIBLE
            mDialogView.glimpseOfInnerPeaceDescription.visibility = View.VISIBLE
            mDialogView.glimpseOfInnerPeaceDivider.visibility = View.VISIBLE

        }

    }

    private fun createSettingButton() {

        val mDialogView = LayoutInflater.from(this).inflate(R.layout.setting_dialog, null)
        val mBuilder = AlertDialog.Builder(this)
                .setView(mDialogView)

        val mAlertDialog = mBuilder.show()

        //Loading SeekBarValues
        val sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)

        savedFocus = sharedPreferences.getInt("SAVED_FOCUS", defaultFocusTime)
        savedBreak = sharedPreferences.getInt("SAVED_BREAK", defaultBreakTime)
        savedSession = sharedPreferences.getInt("SAVED_SESSION", defaultSessions)

        mDialogView.focusBar.progress = savedFocus
        mDialogView.focusNumber.text = savedFocus.toString()

        mDialogView.breakBar.progress = savedBreak
        mDialogView.breakNumber.text = savedBreak.toString()

        mDialogView.sessionsBar.progress = savedSession
        mDialogView.sessionsNumber.text = savedSession.toString()

        //Changing FocusBar
        focNumb = savedFocus

        mDialogView.focusBar.setOnSeekBarChangeListener(object :
                SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar,
                                           progress: Int, fromUser: Boolean) {

                mDialogView.focusNumber.text = progress.toString()
            }

            override fun onStartTrackingTouch(seek: SeekBar) {
            }

            override fun onStopTrackingTouch(seek: SeekBar) {

                focNumb = (seek.progress.toString()).toInt()
                testNumber = (focNumb*60*1000).toLong()

                testNumb = (focNumb*60*1000).toLong()
                a = testNumb

            }
        })

        //Changing BreakBar
        breNumb = savedBreak

        mDialogView.breakBar.setOnSeekBarChangeListener(object :
                SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar,
                                           progress: Int, fromUser: Boolean) {

                mDialogView.breakNumber.text = progress.toString()
            }

            override fun onStartTrackingTouch(seek: SeekBar) {
            }

            override fun onStopTrackingTouch(seek: SeekBar) {
                breNumb = (seek.progress.toString()).toInt()
                testNumber2 = (breNumb*60*1000).toLong()

                testNumb2 = (breNumb*60*1000).toLong()
                b = testNumb2

            }
        })

        //Changing SessionBar
        sessNumb = savedSession

        mDialogView.sessionsBar.setOnSeekBarChangeListener(object :
                SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar,
                                           progress: Int, fromUser: Boolean) {

                mDialogView.sessionsNumber.text = progress.toString()

            }

            override fun onStartTrackingTouch(seek: SeekBar) {
            }

            override fun onStopTrackingTouch(seek: SeekBar) {
                sessNumb = (seek.progress.toString()).toInt()

                testNumber3 = sessNumb
                testNumb3 = testNumber3
                session2 = testNumber3
            }
        })

        mDialogView.tickButton.setOnClickListener {

            val preferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
            val editor = preferences.edit()
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

    fun statsButton(view: View) {

        val mDialogView = LayoutInflater.from(this).inflate(R.layout.usage_stats_dialog, null)
        val mBuilder = AlertDialog.Builder(this)
                .setView(mDialogView)

        val mAlertDialog = mBuilder.show()

        val dates1 = getCalculatedDate("dd/MM/yyyy", 0)
        val dates2 = getCalculatedDate("dd/MM/yyyy", -1)
        val dates3 = getCalculatedDate("dd/MM/yyyy", -2)
        val dates4 = getCalculatedDate("dd/MM/yyyy", -3)
        val dates5 = getCalculatedDate("dd/MM/yyyy", -4)
        val dates6 = getCalculatedDate("dd/MM/yyyy", -5)
        val dates7 = getCalculatedDate("dd/MM/yyyy", -6)
        val dates8 = getCalculatedDate("dd/MM/yyyy", -7)
        val dates9 = getCalculatedDate("dd/MM/yyyy", -8)
        val dates10 = getCalculatedDate("dd/MM/yyyy", -9)

        if(dates1 == getCalculatedDate("dd/MM/yyyy", 0)) {
            mDialogView.time1.text = totalFocusTime.toString()
        }

        mDialogView.date1.text = dates1
        mDialogView.date2.text = dates2
        mDialogView.date3.text = dates3
        mDialogView.date4.text = dates4
        mDialogView.date5.text = dates5
        mDialogView.date6.text = dates6
        mDialogView.date7.text = dates7
        mDialogView.date8.text = dates8
        mDialogView.date9.text = dates9
        mDialogView.date10.text = dates10

        mDialogView.closeButton.setOnClickListener {
            mAlertDialog.dismiss()
        }

    }

    @SuppressLint("SimpleDateFormat")
    fun getCalculatedDate(dateFormat: String?, days: Int): String? {
        val cal: Calendar = Calendar.getInstance()
        val s = SimpleDateFormat(dateFormat)
        cal.add(Calendar.DAY_OF_YEAR, days)
        return s.format(Date(cal.timeInMillis))
    }

    @SuppressLint("SetTextI18n")
    fun repeatTimerOnceAgain(noOfSession : Int) {

        val session = session2

        if(noOfSession <= 0) {
            return
        } else {

            countDownTimer = object : CountDownTimer(a, 1000) {
                override fun onTick(millisUntilFinished: Long) {

                    a = millisUntilFinished
                    breakTb = session != noOfSession
                    timerText.text = "Focus Time"
                    timerText.visibility = View.VISIBLE

                    updateTimerOnceAgainFB2(millisUntilFinished)
                }

                override fun onFinish() {

                    countDownTimer = object : CountDownTimer(b, 1000) {
                        override fun onTick(millisUntilFinished: Long) {

                            b = millisUntilFinished
                            breakTb = false
                            timerText.text = "Break Time"

                            updateTimerOnceAgainFB2(millisUntilFinished)
                        }

                        @SuppressLint("SetTextI18n")
                        override fun onFinish() {

                            a = testNumber
                            b = testNumber2

                            breakTb = true

                            //code other part here
                            if(noOfSession == 1) {

                             timerRunning = false
                             testNumb3 = testNumber3

                             totalFocusTime += focNumb*testNumber3

                             remainingTime = savedRemainingTime - (focNumb*testNumber3)

                            saveRemainingTime()
                            loadRemainingTime()

                            if(savedRemainingTime <= 0) {
//                                currentLevel++
                                savedCurrentLevel++

                                if (savedCurrentLevel == 2) {

//                                    savedCurrentLevelText = "Aspiring Monk"
                                    remainingTime = 2
                                    progressBar.max = remainingTime

                                } else if (savedCurrentLevel == 3) {

//                                    savedCurrentLevelText = "Aspiring Monk"
                                    remainingTime = 3
                                    progressBar.max = remainingTime

                                } else if (savedCurrentLevel == 4){

//                                    savedCurrentLevelText = "Aspiring Monk"
                                    remainingTime = 4
                                    progressBar.max = remainingTime


                                }
//                                saveCurrentLevel()

                                saveRemainingTime()
                                loadRemainingTime()

                            }

                            startButton.visibility = View.VISIBLE
                            levelUp.visibility = View.VISIBLE
                            progressBar.visibility = View.VISIBLE
                            clock.visibility = View.VISIBLE
                            timer.visibility = View.INVISIBLE
                            stopButton.visibility = View.INVISIBLE
                            cancelButton.visibility = View.INVISIBLE
                            timerText.visibility = View.INVISIBLE

                            }
                            // till here

                            repeatTimerOnceAgain(noOfSession-1)

                        }
                    }.start()
                }
            }.start()

            timerRunning = true
            stopButton.text = "PAUSE"

        }

    }

    fun updateTimerOnceAgainFB2(focusT : Long) {

        val minutes = (focusT / 60000).toInt()
        val seconds = (focusT % 60000 / 1000).toInt()

        var timeLeftText: String

        timeLeftText = "$minutes"
        timeLeftText += ":"
        if (seconds < 10) timeLeftText += "0"
        timeLeftText += seconds

        timer.text = timeLeftText

    }

    private fun pomodoroDialog() {

        val mDialogView = LayoutInflater.from(this).inflate(R.layout.pomodoro_dialog, null)
        val mBuilder = AlertDialog.Builder(this)
                .setView(mDialogView)

        val mAlertDialog = mBuilder.show()

        mDialogView.closeButtonInPomodoro.setOnClickListener {
            mAlertDialog.dismiss()
        }

        mDialogView.checkBox.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { compoundButton, b ->
            if(compoundButton.isChecked) {
                storePomodoroDialogStatus(true)
            } else {
                storePomodoroDialogStatus(false)
            }
        })

        if (getPomodoroDialogStatus()) {
            mAlertDialog.hide()
        } else {
            mAlertDialog.show()
        }

    }

    private fun storePomodoroDialogStatus(isChecked: Boolean) {

        val sharedPreferences = getSharedPreferences("checkItem", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.apply {

                putBoolean("item", isChecked)

            }.apply()

    }

    private fun getPomodoroDialogStatus() : Boolean {

        val sharedPreferences = getSharedPreferences("checkItem", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("item", false)

    }

    fun loadStartingVariables() {

        focNumb = savedFocus
        a = (focNumb*60*1000).toLong()
        testNumber = a

        breNumb = savedBreak
        b = (breNumb*60*1000).toLong()
        testNumber2 = b

        sessNumb = savedSession
        testNumber3 = sessNumb
        session2 = sessNumb
        testNumb3 = sessNumb
    }

}