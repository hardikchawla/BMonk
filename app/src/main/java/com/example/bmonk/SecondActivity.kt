package com.example.bmonk

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.widget.CompoundButton
import android.widget.SeekBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_second.*
import kotlinx.android.synthetic.main.level_dialog.view.*
import kotlinx.android.synthetic.main.pomodoro_dialog.view.*
import kotlinx.android.synthetic.main.setting_dialog.view.*


class SecondActivity : AppCompatActivity() {

    //Timer variables
    var timerRunning = false
    var timerFinish = false
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
    val defaultFocusTime = 10
    val defaultBreakTime = 2
    val defaultSessions = 2
    var focusTime = 0

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
    var singleBeep : MediaPlayer? = null
    var doubleBeep : MediaPlayer? = null
    var beeps = false
    var isPlaying = false

    //Stats variables
    var totalFocusTime = 0
    var savedTotalFocusTime = 0

    //Current Level variables
    var currentLevel = 1
    var savedCurrentLevel = 0
    var savedCurrentLevelText = ""
    var asp = "Distracted Baby"
    var progressMax = 40
    var savedProgressMax = 0

    //Dnd variables
    var dndMode = false
    lateinit var mNotificationManager : NotificationManager
    var dndDialog = false

    //FireBase
//    private var mFirebaseAnalytics: FirebaseAnalytics? = null

    //After Stop timer variables
    var afterFocus = 0L
    var afterBreak = 0L
    var afterSession = 0

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        mNotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (!mNotificationManager.isNotificationPolicyAccessGranted) {
            showDNDDialog()
        }

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
        loadTotalFocusTime()

//        alarmTrigger()

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

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//
////            if (checkOnOff) {
////                checkOnOff = false
////                // DND off
////                mNotificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL)
////            } else {
////                checkOnOff = true
////                //DND on
////                mNotificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE)
////
////            }
//
//            if(mNotificationManager.isNotificationPolicyAccessGranted) {
//                mNotificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE)
//                checkOnOff = true
//                dndMode = true
//            }
//
//        }

        startButton.visibility = View.INVISIBLE
        levelUp.visibility = View.INVISIBLE
        progressBar.visibility = View.INVISIBLE
        clock.visibility = View.INVISIBLE
        timer.visibility = View.VISIBLE
        stopButton.visibility = View.VISIBLE
        cancelButton.visibility = View.VISIBLE
        progressText.visibility = View.INVISIBLE

        if(sound == null) {
            sound = MediaPlayer.create(this, R.raw.sound_meditation)

        }

        sound?.start()
        isPlaying = true

        if(singleBeep == null) {
            singleBeep = MediaPlayer.create(this, R.raw.single_beep_2)

        }
        if(doubleBeep == null) {
            doubleBeep = MediaPlayer.create(this, R.raw.double_beep_2)

        }

        afterFocus = a
        afterBreak = b
        afterSession = testNumber3

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

            sound?.setOnCompletionListener {
                isPlaying = false
            }

            if (isPlaying){
                sound?.start()
            }

//            sound?.start()
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
        a = 0
        testNumber2 = 0
        b = 0
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
        progressText.visibility = View.VISIBLE
        timerText.visibility = View.INVISIBLE

//        loadStartingVariables()
        a = afterFocus
        b = afterBreak
        testNumb3 = afterSession
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

        savedRemainingTime = sharedPreferences.getInt("REMAINING_TIME", 40)

        progressTimeLeft.text = savedRemainingTime.toString()
        progressBar.progress = savedRemainingTime

    }

    fun saveTotalFocusTime() {

        val sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.apply {

            putInt("TOTAL_FOCUS", totalFocusTime)

        }.apply()

    }

    fun loadTotalFocusTime() {

        val sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)

        savedTotalFocusTime = sharedPreferences.getInt("TOTAL_FOCUS", 0)

    }

    fun saveCurrentLevel() {

        val sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.apply {

            putString("CURRENT_LEVEL_TEXT", asp)
            putInt("CURRENT_LEVEL", currentLevel)
            putInt("PROGRESS_MAX", progressMax)

        }.apply()

    }

    fun loadCurrentLevel() {

        val sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)

        savedCurrentLevelText = sharedPreferences.getString("CURRENT_LEVEL_TEXT", "Distracted Baby").toString()
        AspiringMonk.text = savedCurrentLevelText

        savedCurrentLevel = sharedPreferences.getInt("CURRENT_LEVEL", 1)
        savedProgressMax = sharedPreferences.getInt("PROGRESS_MAX", 40)

        progressBar.max  = savedProgressMax

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

            mDialogView.distractedBabyImage.visibility = View.VISIBLE
            mDialogView.distracted_babyText.visibility = View.VISIBLE
            mDialogView.distracted_baby_description.visibility = View.VISIBLE
            mDialogView.distractedBabyDivider.visibility = View.VISIBLE

        } else if(savedCurrentLevel == 2) {

            mDialogView.aspiringMonkImage.visibility = View.VISIBLE
            mDialogView.aspiringMonkText.visibility = View.VISIBLE
            mDialogView.aspiring_monk_description.visibility = View.VISIBLE
            mDialogView.aspiringMonkDivider.visibility = View.VISIBLE

        } else if(savedCurrentLevel == 3) {

            mDialogView.calmInCalamityImage.visibility = View.VISIBLE
            mDialogView.calmInCalamityText.visibility = View.VISIBLE
            mDialogView.calmInCalamityDescription.visibility = View.VISIBLE
            mDialogView.calmInCalamityDivider.visibility = View.VISIBLE

        } else {

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

//    fun statsButton(view: View) {
//
//        val mDialogView = LayoutInflater.from(this).inflate(R.layout.usage_stats_dialog, null)
//        val mBuilder = AlertDialog.Builder(this)
//                .setView(mDialogView)
//
//        val mAlertDialog = mBuilder.show()
//
////        TIME_1 = mDialogView.findViewById(R.id.time1)
//
////        alarmTrigger()
//
//        val dates1 = getCalculatedDate("dd/MM/yyyy", 0)
//        val dates2 = getCalculatedDate("dd/MM/yyyy", -1)
//        val dates3 = getCalculatedDate("dd/MM/yyyy", -2)
//        val dates4 = getCalculatedDate("dd/MM/yyyy", -3)
//        val dates5 = getCalculatedDate("dd/MM/yyyy", -4)
//        val dates6 = getCalculatedDate("dd/MM/yyyy", -5)
//        val dates7 = getCalculatedDate("dd/MM/yyyy", -6)
//        val dates8 = getCalculatedDate("dd/MM/yyyy", -7)
//        val dates9 = getCalculatedDate("dd/MM/yyyy", -8)
//        val dates10 = getCalculatedDate("dd/MM/yyyy", -9)
//
//        if(TIME_1 == "0") {
//            savedTotalFocusTime = 0
//        }
//
//        mDialogView.time1.text = savedTotalFocusTime.toString()
//
//        mDialogView.date1.text = dates1
//        mDialogView.date2.text = dates2
//        mDialogView.date3.text = dates3
//        mDialogView.date4.text = dates4
//        mDialogView.date5.text = dates5
//        mDialogView.date6.text = dates6
//        mDialogView.date7.text = dates7
//        mDialogView.date8.text = dates8
//        mDialogView.date9.text = dates9
//        mDialogView.date10.text = dates10
//
//        mDialogView.closeButton.setOnClickListener {
//            mAlertDialog.dismiss()
//        }
//
//    }

//    @SuppressLint("SimpleDateFormat")
//    fun getCalculatedDate(dateFormat: String?, days: Int): String? {
//        val cal: Calendar = Calendar.getInstance()
//        val s = SimpleDateFormat(dateFormat)
//        cal.add(Calendar.DAY_OF_YEAR, days)
//        return s.format(Date(cal.timeInMillis))
//    }

    @SuppressLint("SetTextI18n")
    fun repeatTimerOnceAgain(noOfSession : Int) {

//        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        val firebase = Firebase.analytics


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

                    if(!beeps) {
                        singleBeep?.start()
                        beeps = true
                    }


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                        if (mNotificationManager.isNotificationPolicyAccessGranted) {
                            if (dndMode) {
                                mNotificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL)
                                statsButton.visibility = View.VISIBLE
                                statsButton2.visibility = View.INVISIBLE
                                dndMode = false

                            }
                        }
                    }

                    countDownTimer = object : CountDownTimer(b, 1000) {
                        override fun onTick(millisUntilFinished: Long) {

                            b = millisUntilFinished
                            breakTb = false
                            timerText.text = "Break Time"
                            updateTimerOnceAgainFB2(millisUntilFinished)

                        }

                        @RequiresApi(Build.VERSION_CODES.M)
                        @SuppressLint("SetTextI18n")
                        override fun onFinish() {


                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                                    if (mNotificationManager.isNotificationPolicyAccessGranted) {

                                        if(noOfSession != 1) {
//                                            double_beep?.start()
                                            if (!dndMode) {
                                                mNotificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE)
                                                statsButton2.visibility = View.VISIBLE
                                                statsButton.visibility = View.INVISIBLE
                                                dndMode = true

                                            }
                                        }
                                    }

                                }

//                            if (!checkOnOff) {
//                                double_beep?.start()
//                            }

                            beeps = false

                            a = testNumber
                            b = testNumber2

                            breakTb = true


                            //code other part here
                            if(noOfSession == 1) {

//                                if(mNotificationManager.isNotificationPolicyAccessGranted) {
//                                    mNotificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL)
//                                    checkOnOff = false
//
//                                }

                                breakTb = false

                                 timerRunning = false
                                 timerFinish = true
                                 testNumb3 = testNumber3

//                                 totalFocusTime += focNumb*testNumber3
                                totalFocusTime = savedTotalFocusTime + focNumb*testNumber3
                                saveTotalFocusTime()
                                loadTotalFocusTime()
                                focusTime = focNumb*testNumber3

                                //firebase
                                firebase.logEvent("time") {
                                    param("totalTime", savedTotalFocusTime.toLong())
                                    param("focusTime", focusTime.toLong())
                                }

                                 remainingTime = savedRemainingTime - (focNumb*testNumber3)

                                saveRemainingTime()
                                loadRemainingTime()

                                if(savedRemainingTime <= 0) {

                                    if (savedCurrentLevel == 1) {
                                        currentLevel = 2
                                        asp = "Aspiring Monk"
                                        progressMax = 240

                                    } else if(savedCurrentLevel == 2) {
                                        currentLevel = 3
                                        asp = "Calm in calamity"
                                        progressMax = 600

                                    } else if(savedCurrentLevel == 3) {
                                        currentLevel = 4
                                        asp = "Glimpse of inner peace"
                                        progressMax = 6000
                                    }

                                    saveCurrentLevel()
                                    loadCurrentLevel()

                                    if(savedCurrentLevel == 2) {

                                        remainingTime = 240

                                    } else if (savedCurrentLevel == 3) {

                                        remainingTime = 600

                                    } else if (savedCurrentLevel == 4){

                                        remainingTime = 6000

                                    }

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
                                progressText.visibility = View.VISIBLE

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

    private fun loadStartingVariables() {

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

    @RequiresApi(Build.VERSION_CODES.M)
    fun showDNDDialog() {

        val nAlertDialog = AlertDialog.Builder(this)
        nAlertDialog.setTitle("Request")
        nAlertDialog.setMessage("Please Allow app to turn DND mode for better functioning. After Clicking Allow, Select B Monk in Settings")
        nAlertDialog.setPositiveButton("Allow") { dialog: DialogInterface?, which: Int ->

            val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
            startActivity(intent)

            dialog?.dismiss()

        }
        nAlertDialog.setNegativeButton("Cancel") { dialog: DialogInterface?, which: Int ->
            dialog?.dismiss()
        }
        nAlertDialog.show()

    }

    fun dndButton(view: View) {

        val sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        dndDialog = sharedPreferences.getBoolean("firstLaunch", true)

        if(dndDialog) {
            dndDialog()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if(mNotificationManager.isNotificationPolicyAccessGranted) {

                if(!dndMode) {
                    mNotificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE)
                    statsButton2.visibility = View.VISIBLE
                    statsButton.visibility = View.INVISIBLE
                    Toast.makeText(this,"DND Mode on", Toast.LENGTH_LONG).show()
                    dndMode = true

                } else {
                    mNotificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL)
                    statsButton.visibility = View.VISIBLE
                    statsButton2.visibility = View.INVISIBLE
                    Toast.makeText(this,"DND Mode off", Toast.LENGTH_LONG).show()
                    dndMode = false

                }

            }

        }

    }

    private fun dndDialog() {

        val nAlertDialog = AlertDialog.Builder(this)
//        nAlertDialog.setTitle("Information")
        nAlertDialog.setMessage("This will on your DND mode and all your notifications,calls will be on mute during focus time.")
        nAlertDialog.setPositiveButton("Ok") {dialog: DialogInterface?, which: Int ->
            dialog?.dismiss()
        }
        nAlertDialog.setNeutralButton("Never Show Again") {dialog, which ->

            dialog?.dismiss()

            val sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.apply {

                putBoolean("firstLaunch", false)

            }.apply()

        }

        nAlertDialog.show()
    }

//    private fun alarmTrigger() {
//
//        val calendar: Calendar = Calendar.getInstance()
//        calendar.set(Calendar.HOUR_OF_DAY, 17)
//        calendar.set(Calendar.MINUTE, 21)
//        calendar.set(Calendar.SECOND, 0)
//        calendar.set(Calendar.MILLISECOND, 0)
//
//        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
//        val intent = Intent(this, MyService::class.java)
//        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
//        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent)
//
//    }

}