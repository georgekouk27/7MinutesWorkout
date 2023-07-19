package gr.georkouk.a7minutesworkout

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import gr.georkouk.a7minutesworkout.databinding.ActivityExcerciseBinding
import java.util.Locale

class ExerciseActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private var binding: ActivityExcerciseBinding? = null
    private var restTimer: CountDownTimer? = null
    private var restProgress: Int = 0
    private var exerciseTimer: CountDownTimer? = null
    private var exerciseProgress: Int = 0
    private var restTimerDuration: Long = 10
    private var exerciseTimerDuration: Long = 30

    private var exercises: ArrayList<ExerciseModel>? = null
    private var currentExercisePosition = -1

    private var tts: TextToSpeech? = null
    private var player: MediaPlayer? = null
    private var adapter: ExerciseStatusAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExcerciseBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        initialize()
    }

    private fun initialize(){
        tts = TextToSpeech(this, this)

        setSupportActionBar(binding?.toolbarExercise)

        if(supportActionBar != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        binding?.toolbarExercise?.setNavigationOnClickListener { onBackPressed() }

        exercises = Constants.defaultExerciseList()

        binding?.rvExerciseStatus?.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        adapter = ExerciseStatusAdapter()
        binding?.rvExerciseStatus?.adapter = adapter
        adapter?.setData(exercises!!)

        setupRestView()
    }

    private fun setRestProgressBar(){
        binding?.progressBar?.progress = restProgress

        restTimer = object: CountDownTimer(restTimerDuration * 1000, 1000){
            override fun onTick(p0: Long) {
                restProgress++
                binding?.progressBar?.progress = 10 - restProgress
                binding?.tvTimer?.text = (10 - restProgress).toString()
            }

            override fun onFinish() {
                currentExercisePosition++

                exercises!!.get(currentExercisePosition).setIsSelected(true)
                adapter!!.notifyItemChanged(currentExercisePosition)

                setupExerciseView()
            }
        }.start()
    }

    private fun setupRestView(){
        try{
            val soundURI = Uri.parse(
                "android.resource://gr.georkouk.a7minutesworkout/" + R.raw.press_start
            )

            player = MediaPlayer.create(applicationContext, soundURI)
            player?.isLooping = false
            player?.start()
        }
        catch (e: Exception){
            e.printStackTrace()
        }

        binding?.flProgressBar?.visibility = View.VISIBLE
        binding?.tvTitle?.text = "GET READY FOR\n\n${exercises!!.get(currentExercisePosition + 1).getName()}"
        binding?.flExerciseView?.visibility = View.INVISIBLE
        binding?.ivImage?.visibility = View.GONE

        if(restTimer != null){
            restTimer?.cancel()
            restProgress = 0
        }

        setRestProgressBar()

        speakOut("Get ready for ${exercises!!.get(currentExercisePosition + 1).getName()}")
    }

    private fun setExerciseProgressBar(){
        binding?.progressBarExercise?.progress = exerciseProgress

        exerciseTimer = object: CountDownTimer(exerciseTimerDuration * 1000, 1000){
            override fun onTick(p0: Long) {
                exerciseProgress++
                binding?.progressBarExercise?.progress = 30 - exerciseProgress
                binding?.tvTimerExercise?.text = (30 - exerciseProgress).toString()
            }

            override fun onFinish() {
                exercises!!.get(currentExercisePosition).setIsSelected(false)
                exercises!!.get(currentExercisePosition).setIsCompleted(true)
                adapter!!.notifyItemChanged(currentExercisePosition)

                if(currentExercisePosition < exercises?.size!! -1){
                    setupRestView()
                }
                else{
                    finish()
                    startActivity(
                        Intent(this@ExerciseActivity, FinishActivity::class.java)
                    )
                }
            }
        }.start()
    }

    private fun setupExerciseView(){
        binding?.flProgressBar?.visibility = View.INVISIBLE
        binding?.flExerciseView?.visibility = View.VISIBLE
        binding?.ivImage?.visibility = View.VISIBLE

        if(exerciseTimer != null){
            exerciseTimer?.cancel()
            exerciseProgress = 0
        }

        speakOut(exercises!!.get(currentExercisePosition).getName())

        binding?.ivImage?.setImageResource(exercises!!.get(currentExercisePosition).getImage())
        binding?.tvTitle?.text = exercises!!.get(currentExercisePosition).getName()

        setExerciseProgressBar()
    }

    override fun onDestroy() {
        super.onDestroy()

        if(restTimer != null){
            restTimer?.cancel()
            restProgress = 0
        }

        if(exerciseTimer != null){
            exerciseTimer?.cancel()
            exerciseProgress = 0
        }

        if(tts != null){
            tts!!.stop()
            tts!!.shutdown()
        }

        if(player != null){
            player!!.stop()
        }

        binding = null
    }

    override fun onInit(status: Int) {
        if(status == TextToSpeech.SUCCESS){
            val result = tts?.setLanguage(Locale.US)

            if(result == TextToSpeech.LANG_MISSING_DATA
                || result ==TextToSpeech.LANG_NOT_SUPPORTED){
                Log.e("---TTS---", "The language is not supported.")
            }
        }
        else{
            Log.e("---TTS---", "Initialization failed.")
        }
    }

    private fun speakOut(text: String){
        tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }

}