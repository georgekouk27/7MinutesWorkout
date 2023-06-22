package gr.georkouk.a7minutesworkout

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Toast
import gr.georkouk.a7minutesworkout.databinding.ActivityExcerciseBinding

class ExerciseActivity : AppCompatActivity() {

    private var binding: ActivityExcerciseBinding? = null
    private var restTimer: CountDownTimer? = null
    private var restProgress: Int = 0
    private var exerciseTimer: CountDownTimer? = null
    private var exerciseProgress: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExcerciseBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        initialize()
    }

    private fun initialize(){
        setSupportActionBar(binding?.toolbarExercise)

        if(supportActionBar != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        binding?.toolbarExercise?.setNavigationOnClickListener { onBackPressed() }

        setupRestView()
    }

    private fun setRestProgressBar(){
        binding?.progressBar?.progress = restProgress

        restTimer = object: CountDownTimer(10000, 1000){
            override fun onTick(p0: Long) {
                restProgress++
                binding?.progressBar?.progress = 10 - restProgress
                binding?.tvTimer?.text = (10 - restProgress).toString()
            }

            override fun onFinish() {
                setupExerciseView()
            }
        }.start()
    }

    private fun setupRestView(){
        if(restTimer != null){
            restTimer?.cancel()
            restProgress = 0
        }

        setRestProgressBar()
    }

    private fun setExerciseProgressBar(){
        binding?.progressBarExercise?.progress = exerciseProgress

        exerciseTimer = object: CountDownTimer(30000, 1000){
            override fun onTick(p0: Long) {
                exerciseProgress++
                binding?.progressBarExercise?.progress = 30 - exerciseProgress
                binding?.tvTimerExercise?.text = (30 - exerciseProgress).toString()
            }

            override fun onFinish() {
                Toast.makeText(
                    this@ExerciseActivity,
                    "30 seconds are over, lets go to the rest view.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }.start()
    }

    private fun setupExerciseView(){
        binding?.flProgressBar?.visibility = View.INVISIBLE
        binding?.tvTitle?.text = "Exercise Name"
        binding?.flExerciseView?.visibility = View.VISIBLE

        if(exerciseTimer != null){
            exerciseTimer?.cancel()
            exerciseProgress = 0
        }

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

        binding = null
    }

}