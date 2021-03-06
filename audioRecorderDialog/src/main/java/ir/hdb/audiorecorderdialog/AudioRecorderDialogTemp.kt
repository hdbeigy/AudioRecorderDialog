package ir.hdb.audiorecorderdialog

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.Window
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.core.content.ContextCompat
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.truizlop.fabreveallayout.FABRevealLayout
import com.truizlop.fabreveallayout.OnRevealChangeListener
import ir.hdb.audiorecorderdialog.databinding.SoundDialogBinding
import java.io.IOException

/**
 * Created by Hadi Beigy on 05/27/21
 */
internal class AudioRecorderDialogTemp(
    private var activity: Activity,
    private var outPutFormat: MediaRecorderDialog.OutputFormat,
    private var audioEncoder: MediaRecorderDialog.AudioEncoder,
    private var onSaveButtonClickListener: OnSaveButtonClickListener

) : Dialog(activity),
    View.OnClickListener,
    OnSeekBarChangeListener, SoundDialogView, OnRevealChangeListener,
    DialogInterface.OnDismissListener {

    private lateinit var binding: SoundDialogBinding

    private var runnable: Runnable? = null

    private val handler = Handler(Looper.getMainLooper())
    private var audioManager: AudioManager? = null
    private var mp: MediaPlayer? = null
    private lateinit var recorder: MediaRecorder
    private var path: String? = null

    private var isSaved = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = SoundDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        runnable = MyCountDownTimer(this, handler)

        setListeners()
        recorder = MediaRecorder()
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        recorder.setOutputFormat(outPutFormat.value)
        recorder.setAudioEncoder(audioEncoder.value)

        path = this.context.cacheDir.toString() + "/" + System.currentTimeMillis() + ".mp4"
        recorder.setOutputFile(path)

        setOnDismissListener(this)
    }

    private fun setListeners() {
        audioManager = activity.getSystemService(Context.AUDIO_SERVICE) as AudioManager?

        binding.fabRevealLayout.setOnRevealChangeListener(this)
        binding.recordProgressBar.setOnSeekBarChangeListener(this)

        binding.recordProgressBar.progress =
            audioManager!!.getStreamVolume(AudioManager.STREAM_MUSIC)
        binding.stopRecording.setOnClickListener(this)

        binding.play.setOnClickListener(this)
        binding.play.tag = R.drawable.ic_play

        binding.stop.setOnClickListener(this)
        binding.save.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        if (view === binding.stopRecording) {
//            stopRecording(length)
        } else if (view === binding.play) {
            if (binding.play.tag as Int == R.drawable.ic_play) {
                binding.play.setImageDrawable(
                    ContextCompat.getDrawable(
                        activity, R.drawable.ic_pause
                    )
                )
                handler.postDelayed(runnable!!, 1000)
                binding.play.tag = R.drawable.ic_pause
                mp!!.start()
            } else {
                binding.play.setImageDrawable(
                    ContextCompat.getDrawable(
                        activity, R.drawable.ic_play
                    )
                )
                handler.removeCallbacks(runnable!!)
                binding.play.tag = R.drawable.ic_play
                mp!!.pause()
            }
        } else if (view === binding.stop) {
            handler.removeCallbacks(runnable!!)
            runnable = MyCountDownTimer(this@AudioRecorderDialogTemp, handler)
            binding.play.setImageDrawable(
                ContextCompat.getDrawable(
                    activity, R.drawable.ic_play
                )
            )
            binding.play.tag = R.drawable.ic_play
            mp!!.stop()
        } else if (view === binding.save) {
            onSaveButtonClickListener.onSucceed(path)
            isSaved = true
            dismiss()
        }
    }

    private fun updateViews() {
        binding.recordingLayout.visibility = View.GONE
        binding.playLayout.visibility = View.VISIBLE
        binding.timer.visibility = View.VISIBLE
        binding.content.visibility = View.GONE
    }

    override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
        audioManager!!.setStreamVolume(AudioManager.STREAM_MUSIC, i, 0)
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {}
    override fun onStopTrackingTouch(seekBar: SeekBar) {}
    override fun updateTimer(value: String?, sec: Int) {
        binding.timer.text = value
    }

    override fun stopRecording() {
        updateViews()
        recorder.stop()
        handler.removeCallbacks(runnable!!)
        runnable = MyCountDownTimer(this@AudioRecorderDialogTemp, handler)
        binding.content.stopRippleAnimation()
        YoYo.with(Techniques.FadeIn)
            .duration(700)
            .playOn(binding.playLayout)
        mp = MediaPlayer.create(activity, Uri.parse(path))
        mp?.setOnCompletionListener { mediaPlayer: MediaPlayer? ->
            handler.removeCallbacks(runnable!!)
            runnable = MyCountDownTimer(this@AudioRecorderDialogTemp, handler)
            binding.play.setImageDrawable(
                ContextCompat.getDrawable(
                    activity,
                    R.drawable.ic_play
                )
            )
            binding.play.tag = R.drawable.ic_play
        }
    }

    override fun onMainViewAppeared(fabRevealLayout: FABRevealLayout, mainView: View) {}
    override fun onSecondaryViewAppeared(fabRevealLayout: FABRevealLayout, secondaryView: View) {
        binding.content.startRippleAnimation()
        binding.content.visibility = View.VISIBLE
        try {
            recorder.prepare()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        recorder.start()
        handler.postDelayed(runnable!!, 1000)
    }

    override fun onDismiss(dialogInterface: DialogInterface) {
        if (!isSaved) onSaveButtonClickListener.onFailure()
    }

}