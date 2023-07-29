package com.fridayhouse.snoozz

import android.app.AlertDialog
import com.fridayhouse.snoozz.exoplayer.PlayerService
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.ProgressDialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.IBinder
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.*
import androidx.core.content.edit
import com.airbnb.lottie.LottieAnimationView
import kotlinx.android.synthetic.main.activity_custom.*
import kotlinx.android.synthetic.main.activity_custom.view.*
import java.util.Date
import java.util.Locale
import java.util.Timer
import java.util.TimerTask
import java.util.concurrent.TimeUnit


class CustomActivity : AppCompatActivity() {

    private var selectedTimerDuration: Long = 0

    private val interval: Long = 1000

    private var countdownTimer: CountDownTimer? = null

    private val TIMER_DURATION_KEY = "timer_duration"
    private val TIMER_RUNNING_KEY = "timer_running"


    private var startTime: Long = 0


    private val sharedPreferences: SharedPreferences by lazy {
        getSharedPreferences("TimerPrefs", Context.MODE_PRIVATE)
    }

    private var timer: Timer? = null;
    // timer duration options
    private var timerTimesHumanReadable: Array<String> = arrayOf( "5 minutes", "15 minutes", "30 minutes", "1 hour", "2 hours", "4 hours", "6 hours")
    // and their corresponding durations in ms
    // and their corresponding durations in ms
    private var timerTimesMilliseconds: Array<Long> = arrayOf( 5*60*1000, 15*60*1000, 30*60*1000, 60*60*1000, 120*60*1000, 240*60*1000, 360*60*1000)
    // Declare icAtomAnim variable at the class level
    private lateinit var icAtomAnim: LottieAnimationView

    private lateinit var keyboardVolumeSeekBar: SeekBar
    private lateinit var rainVolumeSeekBar: SeekBar
    private lateinit var thunderVolumeSeekBar: SeekBar
    private lateinit var seaVolumeSeekBar: SeekBar
    private lateinit var windVolumeSeekBar: SeekBar
    private lateinit var musicVolumeSeekBar: SeekBar
    private lateinit var pianoVolumeSeekBar: SeekBar
    private lateinit var fluteVolumeSeekBar: SeekBar
    private lateinit var grassVolumeSeekBar: SeekBar
    private lateinit var bowlVolumeSeekBar: SeekBar
    private lateinit var birdVolumeSeekBar: SeekBar
    private lateinit var herpVolumeSeekBar: SeekBar
    private lateinit var ohmVolumeSeekBar: SeekBar
    private lateinit var trainVolumeSeekBar: SeekBar
    private lateinit var catVolumeSeekBar: SeekBar
    private lateinit var fireVolumeSeekBar: SeekBar
    private lateinit var drumVolumeSeekBar: SeekBar


    // Add more SeekBars if needed

    private var playerService: PlayerService? = null


    companion object {
        private var isKeyboardVisible = false
        private var isRainVisible = false
        private var isThunderVisible = false
        private var isSeaVisible = false
        private var isWindVisible = false
        private var isMusicVisible = false
        private var isPianoVisible = false
        private var isFluteVisible = false
        private var isGrassVisible = false
        private var isBowlVisible = false
        private var isBirdVisible = false
        private var isHerpVisible = false
        private var isOhmVisible = false
        private var isTrainVisible = false
        private var isCatVisible = false
        private var isFireVisible = false
        private var isDrumVisible = false
        // Add more static variables for other SeekBars if needed


        private var keyboardProgress = 100
        private var thunderProgress = 100
        private var seaProgress = 100
        private var windProgress = 100
        private var musicProgress = 100
        private var pianoProgress = 100
        private var fluteProgress = 100
        private var grassProgress = 100
        private var bowlProgress = 100
        private var birdProgress = 100
        private var herpProgress = 100
        private var ohmProgress = 100
        private var trainProgress = 100
        private var catProgress = 100
        private var fireProgress = 100
        private var drumProgress = 100
        private var rainProgress = 100
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {}

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            playerService = (service as PlayerService.PlayerBinder).getService()
            // update the FAB
            if (playerService?.isPlaying() == true) {
                fab.show()
                icAtomAnim.resumeAnimation()
            } else fab.hide()
            playerService?.playerChangeListener = playerChangeListener

            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }

    }



    private val playerChangeListener = {
        if (playerService?.isPlaying() == true) {
            fab.show()
            //icAtomAnim.visibility = View.VISIBLE
            icAtomAnim.resumeAnimation()
        } else {
            fab.hide()
            //icAtomAnim.visibility = View.INVISIBLE
            icAtomAnim.pauseAnimation()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom)

        // Initialize icAtomAnim and set its initial visibility
        icAtomAnim = findViewById(R.id.ic_atom_anim)
        icAtomAnim.pauseAnimation()
        //icAtomAnim.visibility = View.INVISIBLE


       // Log.d("CustomActivity", "onCreate() called")
        // Restore the timer state and time duration from SharedPreferences
        val timerRunning = sharedPreferences.getBoolean("isTimerRunning", false)
        selectedTimerDuration = sharedPreferences.getLong("selectedTimerDuration", 0)
        startTime = sharedPreferences.getLong("startTime", 0)
//        Log.d("CustomActivity", "Restored selectedTimerDuration: $selectedTimerDuration")
//
//
//        Log.d("CustomActivity", "timerTimesMilliseconds: ${timerTimesMilliseconds.joinToString()}")
//        Log.d("CustomActivity", "selectedTimerDuration: $selectedTimerDuration")

        // Verify if 'which' is a valid index for the timerTimesMilliseconds array
        val which = if (selectedTimerDuration.toInt() in timerTimesMilliseconds.map { it.toInt() }) {
            timerTimesMilliseconds.indexOf(selectedTimerDuration)
        } else {
         //   Log.e("CustomActivity", "Invalid timer option selected in onCreate: $selectedTimerDuration, using default duration.")
            // Use the default index of 0 if the selected duration is not found in the array
            0
        }


      //  Log.d("CustomActivity", "onCreate() - timerRunning: $timerRunning, selectedTimerDuration: $selectedTimerDuration, which: $which")

        if (timerRunning && selectedTimerDuration > 0) {
            // If a timer was running before, restart the timer
            val timeMs: Long =   selectedTimerDuration - (System.currentTimeMillis() - startTime)
           // Log.d("CustomActivity", "Calling startTimer with timeMs: $timeMs")
          //  Log.d("CustomActivity", " $timeMs  = selectedTimerDuration($selectedTimerDuration) -  systemTime(${System.currentTimeMillis()}) - startTime($startTime)  ")

            startTimer(timeMs)
        } else {
            // Update the countdown text with the selected timer duration
            val hours = TimeUnit.MILLISECONDS.toHours(selectedTimerDuration)
            val minutes = TimeUnit.MILLISECONDS.toMinutes(selectedTimerDuration) % 60
            val seconds = TimeUnit.MILLISECONDS.toSeconds(selectedTimerDuration) % 60
            val formattedTime = String.format("%02d:%02d:%02d", hours, minutes, seconds)
            updateCountdownText(formattedTime)

            // Update the button state and visibility
            updateTimerButtonState()
            start_timer.visibility = if (countdownTimer != null) View.VISIBLE else View.INVISIBLE
            timer_countdown_text.visibility = if (countdownTimer != null) View.VISIBLE else View.INVISIBLE
            start_timer_icon.visibility = if (countdownTimer == null) View.VISIBLE else View.INVISIBLE
        }




        keyboardVolumeSeekBar = findViewById(R.id.keyboard_volume)
        thunderVolumeSeekBar = findViewById(R.id.thunder_volume)
        seaVolumeSeekBar = findViewById(R.id.ocean_volume)
        windVolumeSeekBar = findViewById(R.id.wind_volume)
        musicVolumeSeekBar = findViewById(R.id.music_volume)
        pianoVolumeSeekBar = findViewById(R.id.piano_volume)
        fluteVolumeSeekBar = findViewById(R.id.flute_volume)
        grassVolumeSeekBar = findViewById(R.id.grass_volume)
        bowlVolumeSeekBar = findViewById(R.id.bowl_volume)
        birdVolumeSeekBar = findViewById(R.id.bird_volume)
        herpVolumeSeekBar = findViewById(R.id.harp_volume)
        ohmVolumeSeekBar = findViewById(R.id.om_volume)
        trainVolumeSeekBar = findViewById(R.id.rail_volume)
        catVolumeSeekBar = findViewById(R.id.cat_volume)
        fireVolumeSeekBar = findViewById(R.id.fire_volume)
        drumVolumeSeekBar = findViewById(R.id.tabla_volume)
        rainVolumeSeekBar = findViewById(R.id.rain_volume)
        // Initialize more SeekBars if needed

        // Restore visibility state from static variables
        keyboardVolumeSeekBar.visibility = if (isKeyboardVisible) View.VISIBLE else View.INVISIBLE
        rainVolumeSeekBar.visibility = if (isRainVisible) View.VISIBLE else View.INVISIBLE
        thunderVolumeSeekBar.visibility = if (isThunderVisible) View.VISIBLE else View.INVISIBLE
        seaVolumeSeekBar.visibility = if (isSeaVisible) View.VISIBLE else View.INVISIBLE
        windVolumeSeekBar.visibility = if (isWindVisible) View.VISIBLE else View.INVISIBLE
        musicVolumeSeekBar.visibility = if (isMusicVisible) View.VISIBLE else View.INVISIBLE
        pianoVolumeSeekBar.visibility = if (isPianoVisible) View.VISIBLE else View.INVISIBLE
        fluteVolumeSeekBar.visibility = if (isFluteVisible) View.VISIBLE else View.INVISIBLE
        grassVolumeSeekBar.visibility = if (isGrassVisible) View.VISIBLE else View.INVISIBLE
        bowlVolumeSeekBar.visibility = if (isBowlVisible) View.VISIBLE else View.INVISIBLE
        birdVolumeSeekBar.visibility = if (isBirdVisible) View.VISIBLE else View.INVISIBLE
        herpVolumeSeekBar.visibility = if (isHerpVisible) View.VISIBLE else View.INVISIBLE
        ohmVolumeSeekBar.visibility = if (isOhmVisible) View.VISIBLE else View.INVISIBLE
        trainVolumeSeekBar.visibility = if (isTrainVisible) View.VISIBLE else View.INVISIBLE
        catVolumeSeekBar.visibility = if (isCatVisible) View.VISIBLE else View.INVISIBLE
        fireVolumeSeekBar.visibility = if (isFireVisible) View.VISIBLE else View.INVISIBLE
        drumVolumeSeekBar.visibility = if (isDrumVisible) View.VISIBLE else View.INVISIBLE
        // Set visibility for more SeekBars if needed

        // Restore progress state from static variables
        keyboardVolumeSeekBar.progress = keyboardProgress
        thunderVolumeSeekBar.progress = thunderProgress
        seaVolumeSeekBar.progress = seaProgress
        windVolumeSeekBar.progress = windProgress
        musicVolumeSeekBar.progress = musicProgress
        pianoVolumeSeekBar.progress = pianoProgress
        fluteVolumeSeekBar.progress = fluteProgress
        grassVolumeSeekBar.progress = grassProgress
        bowlVolumeSeekBar.progress = bowlProgress
        birdVolumeSeekBar.progress = birdProgress
        herpVolumeSeekBar.progress = herpProgress
        ohmVolumeSeekBar.progress = ohmProgress
        trainVolumeSeekBar.progress = trainProgress
        catVolumeSeekBar.progress = catProgress
        fireVolumeSeekBar.progress = fireProgress
        drumVolumeSeekBar.progress = drumProgress
        rainVolumeSeekBar.progress = rainProgress

       createNotificationChannel()
       // val keyboardplay: ImageView = findViewById(R.id.icon_keyboard)
        icon_keyboard.setOnClickListener{
            playerService?.toggleSound(PlayerService.Sound.KEYBOARD)
            toggleProgressBar(keyboard_volume)
            this.updateTimerState()
           // toggleImageView(icon_keyboard)
        }

        //val rainplay: ImageView = findViewById(R.id.icon_rain)
        icon_rain.setOnClickListener{
            playerService?.toggleSound(PlayerService.Sound.RAIN)
            toggleProgressBar(rain_volume)
            this.updateTimerState()
        }

        //val thunderplay: ImageView = findViewById(R.id.icon_thunder)
        icon_thunder.setOnClickListener{
            playerService?.toggleSound(PlayerService.Sound.THUNDER)
            toggleProgressBar(thunder_volume)
            this.updateTimerState()
        }

        //val oceanplay: ImageView = findViewById(R.id.icon_ocean)
        icon_ocean.setOnClickListener{
            playerService?.toggleSound(PlayerService.Sound.OCEAN)
            toggleProgressBar(ocean_volume)
            this.updateTimerState()
        }

       // val windplay: ImageView = findViewById(R.id.icon_wind)
        icon_wind.setOnClickListener{
            playerService?.toggleSound(PlayerService.Sound.WIND)
            toggleProgressBar(wind_volume)
            this.updateTimerState()
        }

       // val musicplay: ImageView = findViewById(R.id.icon_musical)
        icon_musical.setOnClickListener{
            playerService?.toggleSound(PlayerService.Sound.MUSIC)
            toggleProgressBar(music_volume)
            this.updateTimerState()
        }

        //val pianoplay: ImageView = findViewById(R.id.icon_piano)
        icon_piano.setOnClickListener{
            playerService?.toggleSound(PlayerService.Sound.PIANO)
            toggleProgressBar(piano_volume)
            this.updateTimerState()
        }

        //val fluteplay: ImageView = findViewById(R.id.icon_flute)
        icon_flute.setOnClickListener{
            playerService?.toggleSound(PlayerService.Sound.FLUTE)
            toggleProgressBar(flute_volume)
            this.updateTimerState()
        }

        //val bowlplay: ImageView = findViewById(R.id.icon_bowl)
        icon_bowl.setOnClickListener{
            playerService?.toggleSound(PlayerService.Sound.BOWL)
            toggleProgressBar(bowl_volume)
            this.updateTimerState()
        }

        //val grassplay: ImageView = findViewById(R.id.icon_grass)
        icon_grass.setOnClickListener{
            playerService?.toggleSound(PlayerService.Sound.GRASS)
            toggleProgressBar(grass_volume)
            this.updateTimerState()
        }

        //val birdplay: ImageView = findViewById(R.id.icon_birds)
        icon_birds.setOnClickListener{
            playerService?.toggleSound(PlayerService.Sound.BIRD)
            toggleProgressBar(bird_volume)
            this.updateTimerState()
        }

        //val harpplay: ImageView = findViewById(R.id.icon_harp)
        icon_harp.setOnClickListener{
            playerService?.toggleSound(PlayerService.Sound.HARP)
            toggleProgressBar(harp_volume)
            this.updateTimerState()
        }

        //val omplay: ImageView = findViewById(R.id.icon_om)
        icon_om.setOnClickListener{
            playerService?.toggleSound(PlayerService.Sound.OM)
            toggleProgressBar(om_volume)
            this.updateTimerState()
        }

        //val railplay: ImageView = findViewById(R.id.icon_railway)
        icon_railway.setOnClickListener{
            playerService?.toggleSound(PlayerService.Sound.RAIL)
            toggleProgressBar(rail_volume)
            this.updateTimerState()
        }

        //val catplay: ImageView = findViewById(R.id.icon_cat)
        icon_cat.setOnClickListener{
            playerService?.toggleSound(PlayerService.Sound.CAT)
            toggleProgressBar(cat_volume)
            this.updateTimerState()
        }

        //val fireplay: ImageView = findViewById(R.id.icon_fire)
        icon_fire.setOnClickListener{
            playerService?.toggleSound(PlayerService.Sound.FIRE)
            toggleProgressBar(fire_volume)
            this.updateTimerState()
        }

        //val tablaplay: ImageView = findViewById(R.id.icon_tabla)
        icon_tabla.setOnClickListener{
            playerService?.toggleSound(PlayerService.Sound.TABLA)
            toggleProgressBar(tabla_volume)
            this.updateTimerState()
        }

        keyboard_volume.setOnSeekBarChangeListener(VolumeChangeListener(PlayerService.Sound.KEYBOARD))
        rain_volume.setOnSeekBarChangeListener(VolumeChangeListener(PlayerService.Sound.RAIN))
        thunder_volume.setOnSeekBarChangeListener(VolumeChangeListener(PlayerService.Sound.THUNDER))
        ocean_volume.setOnSeekBarChangeListener(VolumeChangeListener(PlayerService.Sound.OCEAN))
        wind_volume.setOnSeekBarChangeListener(VolumeChangeListener(PlayerService.Sound.WIND))
        music_volume.setOnSeekBarChangeListener(VolumeChangeListener(PlayerService.Sound.MUSIC))
        piano_volume.setOnSeekBarChangeListener(VolumeChangeListener(PlayerService.Sound.PIANO))
        flute_volume.setOnSeekBarChangeListener(VolumeChangeListener(PlayerService.Sound.FLUTE))
        grass_volume.setOnSeekBarChangeListener(VolumeChangeListener(PlayerService.Sound.GRASS))
        bowl_volume.setOnSeekBarChangeListener(VolumeChangeListener(PlayerService.Sound.BOWL))
        bird_volume.setOnSeekBarChangeListener(VolumeChangeListener(PlayerService.Sound.BIRD))
        harp_volume.setOnSeekBarChangeListener(VolumeChangeListener(PlayerService.Sound.HARP))
        om_volume.setOnSeekBarChangeListener(VolumeChangeListener(PlayerService.Sound.OM))
        rail_volume.setOnSeekBarChangeListener(VolumeChangeListener(PlayerService.Sound.RAIL))
        cat_volume.setOnSeekBarChangeListener(VolumeChangeListener(PlayerService.Sound.CAT))
        fire_volume.setOnSeekBarChangeListener(VolumeChangeListener(PlayerService.Sound.FIRE))
        tabla_volume.setOnSeekBarChangeListener(VolumeChangeListener(PlayerService.Sound.TABLA))


        fab.setOnClickListener {
            playerService?.stopPlaying()
            fab.hide()
            icAtomAnim.pauseAnimation()
            //fab.visibility = View.INVISIBLE
            // hide all volume bars
            arrayOf(keyboard_volume, rain_volume,thunder_volume,ocean_volume,wind_volume,music_volume,piano_volume,flute_volume,
                grass_volume,bowl_volume,bird_volume,harp_volume,om_volume,rail_volume,cat_volume,fire_volume,tabla_volume,).forEach { bar ->
                bar?.visibility = View.INVISIBLE
            }
            this.stopPlaying()
            this.cancelTimer()
        }

        start_timer.setOnClickListener {
            this.startTimerClickHandler()
        }

        cancel_timer.setOnClickListener {
            this.cancelTimer()
        }

    }

    private fun toggleProgressBar(progressBar: ProgressBar) {
        progressBar.visibility = if (progressBar.visibility == View.VISIBLE) View.INVISIBLE else View.VISIBLE
    }

    private fun stopPlaying() {
        playerService?.stopPlaying()
        icAtomAnim.pauseAnimation()
        fab.hide()
        // hide all volume bars
        arrayOf(keyboard_volume, rain_volume,thunder_volume,ocean_volume,wind_volume,music_volume,piano_volume,flute_volume,
            grass_volume,bowl_volume,bird_volume,harp_volume,om_volume,rail_volume,cat_volume,fire_volume,tabla_volume,).forEach { bar ->
            bar?.visibility = View.INVISIBLE
        }
    }

    private fun updateTimerState() {
        // update the timer state in response to the user enabling/disabling a specific soundtrack
        // if no sound is playing, cancel the timer and update the button state
        // if sound is playing, only update the button state
        if (playerService == null || !(playerService!!.isPlaying()))
            this.cancelTimer()
        this.updateTimerButtonState()
    }


    private fun updateTimerButtonState() {
        // if no sound is playing, both buttons should be invisible
       // Log.d("CustomActivity", "updateTimerButtonState - isPlaying: ${playerService?.isPlaying()}")
        if (playerService == null || !(playerService!!.isPlaying())) {
            start_timer.visibility = View.INVISIBLE
            cancel_timer.visibility = View.INVISIBLE
            timer_countdown_text.visibility = View.INVISIBLE
        } else {
            // sound is playing
            if (this.countdownTimer == null) {
                // No timer is running, show the start button and hide the cancel button

               // Log.d("CustomActivity", "Timer is not running")
                start_timer.visibility = View.VISIBLE
                cancel_timer.visibility = View.INVISIBLE
                start_timer_icon.visibility = View.VISIBLE
                timer_countdown_text.visibility = View.INVISIBLE
            } else {
                // Timer is running, hide the start button and show the timer countdown
               // Log.d("CustomActivity", "Timer is running")

                start_timer.visibility = View.VISIBLE
                cancel_timer.visibility = View.VISIBLE
                timer_countdown_text.visibility = View.VISIBLE
            }
        }
    }


    private fun startTimerClickHandler() {
        // Create a list of items to show in the AlertDialog
        val items = timerTimesHumanReadable

        // Set the custom style for the AlertDialog
        val alertDialog = AlertDialog.Builder(this, R.style.RoundedAlertDialog)

        // Set the title for the AlertDialog
        alertDialog.setTitle("Set timer duration")
        // Use the custom layout for the list items
        val customAdapter = ArrayAdapter<String>(this, R.layout.custom_list_item, items)
        alertDialog.setAdapter(customAdapter) { _, which ->
            if (which in timerTimesMilliseconds.indices) {
                val timeMs = timerTimesMilliseconds[which]
                startTimer(timeMs)
            } else {
               // Log.e("CustomActivity", "Invalid timer option selected: $which")
            }
        }

        alertDialog.show()
    }



    private fun startTimer(timeMs: Long) {
        // Ensure the 'timeMs' parameter is a valid positive value
        if (timeMs <= 0) {
          //  Log.e("CustomActivity", "Invalid timer duration passed to startTimer(): $timeMs")
            return
        }

        // Calculate the new start time
        startTime = System.currentTimeMillis()

        // Calculate the remaining time in milliseconds
        val remainingTimeMs = timeMs - (System.currentTimeMillis() - startTime)

        // Cancel the previous timer if it's running
        countdownTimer?.cancel()


        // Save the timer state and time duration in SharedPreferences
        val editor = sharedPreferences.edit()
        editor.putLong("selectedTimerDuration", timeMs)
        editor.putLong("startTime", System.currentTimeMillis())
        editor.putBoolean("isTimerRunning", true)
        editor.apply()

        // Create a new countdown timer
        countdownTimer = object : CountDownTimer(remainingTimeMs, interval) {
            override fun onTick(millisUntilFinished: Long) {
                val remainingTime = millisUntilFinished
                val hours = TimeUnit.MILLISECONDS.toHours(remainingTime)
                val minutes = TimeUnit.MILLISECONDS.toMinutes(remainingTime) % 60
                val seconds = TimeUnit.MILLISECONDS.toSeconds(remainingTime) % 60
                updateCountdownText(String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds))
            }

            override fun onFinish() {
              //  Log.d("CustomActivity", "onFinish() Called and selectedTimerDuration is $selectedTimerDuration")
                updateCountdownText("00:00:00")

//                if (selectedTimerDuration > 0) {
//                    // The timer finished naturally, so stop the music
//                    stopPlaying()
//                }
                stopPlaying()
                cancelTimer()
            }
        }

        countdownTimer?.start()

        // Update the button state
        updateTimerButtonState()

        // Show the countdown text and hide the timer icon
        start_timer.visibility = View.VISIBLE
        cancel_timer.visibility = View.VISIBLE
        timer_countdown_text.visibility = View.VISIBLE
        start_timer_icon.visibility = View.INVISIBLE

        // Log the final values of selectedTimerDuration and remainingTimeMs
       // Log.d("CustomActivity", "Timer started for duration=$timeMs ms, remainingTime=$remainingTimeMs ms")
    }



    private fun updateCountdownText(countdownText: String) {
        timer_countdown_text.text = countdownText
    }



    private fun cancelTimer() {
        // Cancel the countdown timer if it's running
        countdownTimer?.cancel()

        // Clear the selected timer duration
        selectedTimerDuration = 0L

        // Hide the countdown text
        timer_countdown_text.visibility = View.INVISIBLE

        // Set the countdown timer to null
        countdownTimer = null

        // Update the button state
        updateTimerButtonState()
        playerService?.stopForeground()

        // Save the timer state and time duration in SharedPreferences
        val editor = sharedPreferences.edit()
        editor.putLong("selectedTimerDuration", selectedTimerDuration)
        editor.putBoolean("isTimerRunning", false)
        editor.apply()
    }


    private fun toggleImageView(imageView: ImageView) {
        imageView.isActivated = if(imageView.isActivated == true) false else true

    }


    inner class VolumeChangeListener(private val sound: PlayerService.Sound) : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            playerService?.setVolume(sound, (progress + 1) / 20f)
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {}

        override fun onStopTrackingTouch(seekBar: SeekBar?) {}
    }

    override fun onRestart() {
        super.onRestart()
        if (playerService?.isPlaying() == true) {
            icAtomAnim.resumeAnimation()
        }
       // Log.d("CustomActivity", "onRestart() called")
    }


    override fun onStart() {
        super.onStart()
        val playerIntent = Intent(this, PlayerService::class.java)
        startService(playerIntent)
        bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE)
       // Log.d("CustomActivity", "onStart() called")

    }

    override fun onStop() {
        unbindService(serviceConnection)
        super.onStop()
        icAtomAnim.pauseAnimation()
        //icAtomAnim.visibility = View.INVISIBLE

        // Save visibility state to static variables
        isKeyboardVisible = keyboardVolumeSeekBar.visibility == View.VISIBLE
        isRainVisible = rainVolumeSeekBar.visibility == View.VISIBLE
        isThunderVisible = thunderVolumeSeekBar.visibility == View.VISIBLE
        isSeaVisible = seaVolumeSeekBar.visibility == View.VISIBLE
        isWindVisible = windVolumeSeekBar.visibility == View.VISIBLE
        isMusicVisible = musicVolumeSeekBar.visibility == View.VISIBLE
        isPianoVisible = pianoVolumeSeekBar.visibility == View.VISIBLE
        isFluteVisible = fluteVolumeSeekBar.visibility == View.VISIBLE
        isGrassVisible = grassVolumeSeekBar.visibility == View.VISIBLE
        isBowlVisible = bowlVolumeSeekBar.visibility == View.VISIBLE
        isBirdVisible = birdVolumeSeekBar.visibility == View.VISIBLE
        isHerpVisible = herpVolumeSeekBar.visibility == View.VISIBLE
        isOhmVisible = ohmVolumeSeekBar.visibility == View.VISIBLE
        isTrainVisible = trainVolumeSeekBar.visibility == View.VISIBLE
        isCatVisible = catVolumeSeekBar.visibility == View.VISIBLE
        isFireVisible = fireVolumeSeekBar.visibility == View.VISIBLE
        isDrumVisible = drumVolumeSeekBar.visibility == View.VISIBLE
        // Save visibility state for more SeekBars if needed

        // Save progress state to static variables
        keyboardProgress = keyboardVolumeSeekBar.progress
        thunderProgress = thunderVolumeSeekBar.progress
        seaProgress = seaVolumeSeekBar.progress
        windProgress = windVolumeSeekBar.progress
        musicProgress = musicVolumeSeekBar.progress
        pianoProgress = pianoVolumeSeekBar.progress
        fluteProgress = fluteVolumeSeekBar.progress
        grassProgress = grassVolumeSeekBar.progress
        bowlProgress = bowlVolumeSeekBar.progress
        birdProgress = birdVolumeSeekBar.progress
        herpProgress = herpVolumeSeekBar.progress
        ohmProgress = ohmVolumeSeekBar.progress
        trainProgress = trainVolumeSeekBar.progress
        catProgress = catVolumeSeekBar.progress
        fireProgress = fireVolumeSeekBar.progress
        drumProgress = drumVolumeSeekBar.progress
        rainProgress = rainVolumeSeekBar.progress
        Log.d("CustomActivity", "onStop() called")
    }

    override fun onResume() {
        super.onResume()
        playerService?.stopForeground()
        if (playerService?.isPlaying() == true) {
            icAtomAnim.resumeAnimation()
        }

       // Log.d("CustomActivity", "onResume() called")
    }

    override fun onPause() {
        playerService?.startForeground()
        super.onPause()
        //icAtomAnim.pauseAnimation()

       // Log.d("CustomActivity", "onPause() called")
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.app_name)
            val importance = NotificationManager.IMPORTANCE_MIN
            val channel = NotificationChannel("softsound", name, importance)

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }

}