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
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.core.content.ContextCompat
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.truizlop.fabreveallayout.FABRevealLayout
import com.truizlop.fabreveallayout.OnRevealChangeListener
import ir.hdb.audiorecorderdialog.databinding.AudioRecorderDialogBinding
import java.io.IOException

/**
 * Created by Hadi Beigy on 05/27/21
 */
internal class AudioRecorderDialog(
    private var activity: Activity,
    private var outPutFormat: MediaRecorderDialog.OutputFormat,
    private var audioEncoder: MediaRecorderDialog.AudioEncoder,
    private var onSaveButtonClickListener: OnSaveButtonClickListener

) : Dialog(activity),
    View.OnClickListener,
    OnSeekBarChangeListener, SoundDialogView, OnRevealChangeListener,
    DialogInterface.OnDismissListener {

    private lateinit var binding: AudioRecorderDialogBinding

    private var runnable: Runnable? = null

    private val handler = Handler(Looper.getMainLooper())
    private var audioManager: AudioManager? = null
    private var mp: MediaPlayer? = null
    private lateinit var recorder: MediaRecorder
    private var path: String? = null
    private var recording: Boolean = false;

    private var isSaved = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        binding = AudioRecorderDialogBinding.inflate(layoutInflater)
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

//        binding.fabRevealLayout.setOnRevealChangeListener(this)

//        binding.audioSeekbar.setOnSeekBarChangeListener(this)

//        binding.audioSeekbar.progress =
//            audioManager!!.getStreamVolume(AudioManager.STREAM_MUSIC)

        binding.stopRecordingImageview.setOnClickListener(this)

        binding.playImageview.setOnClickListener(this)
        binding.playImageview.tag = R.drawable.ic_play

        binding.stopRecordingImageview.setOnClickListener(this)
        binding.saveImageview.setOnClickListener(this)
        binding.refreshImageview.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        if (view === binding.stopRecordingImageview) {
            stopRecording()
        } else if (view === binding.playImageview) {
            if (!recording) {
                try {
                    recorder.prepare()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                recorder.start()
                handler.postDelayed(runnable!!, 1000)
                binding.stopRecordingImageview.visibility = View.VISIBLE
                binding.playImageview.visibility = View.GONE
                binding.timerTexview.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.audio_dialog_colorPrimary
                    )
                )
                recording = true
            } else if (binding.playImageview.tag as Int == R.drawable.ic_play) {
                binding.playImageview.setImageDrawable(
                    ContextCompat.getDrawable(
                        activity, R.drawable.ic_pause
                    )
                )
                handler.postDelayed(runnable!!, 1000)
                binding.playImageview.tag = R.drawable.ic_pause
                mp!!.start()
            } else {
                binding.playImageview.setImageDrawable(
                    ContextCompat.getDrawable(
                        activity, R.drawable.ic_play
                    )
                )
                handler.removeCallbacks(runnable!!)
                binding.playImageview.tag = R.drawable.ic_play
                mp!!.pause()
            }
        } else if (view === binding.stopImageview) {
            handler.removeCallbacks(runnable!!)
            runnable = MyCountDownTimer(this@AudioRecorderDialog, handler)
            binding.playImageview.setImageDrawable(
                ContextCompat.getDrawable(
                    activity, R.drawable.ic_play
                )
            )
            binding.playImageview.tag = R.drawable.ic_play
            mp!!.stop()
        } else if (view === binding.saveImageview) {
            onSaveButtonClickListener.onSucceed(path)
            isSaved = true
            dismiss()
        } else if (view == binding.refreshImageview) {
            refreshView()
        }
    }

    private fun refreshView() {
        recording = false;
//        binding.stopRecordingImageview.visibility = View.VISIBLE
        binding.messageTexview.visibility = View.VISIBLE
        binding.audioSeekbar.visibility = View.GONE
        binding.saveImageview.visibility = View.GONE
        binding.refreshImageview.visibility = View.GONE
        binding.playImageview.visibility = View.VISIBLE
        binding.playImageview.setImageResource(R.drawable.ic_mic_none)
        recorder.reset()
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        recorder.setOutputFormat(outPutFormat.value)
        recorder.setAudioEncoder(audioEncoder.value)

        path = this.context.cacheDir.toString() + "/" + System.currentTimeMillis() + ".mp4"
        recorder.setOutputFile(path)

        binding.timerTexview.setText("00:00")
//        recorder.reset()
    }

    private fun updateViews() {
        binding.stopRecordingImageview.visibility = View.GONE
        binding.messageTexview.visibility = View.GONE
        binding.audioSeekbar.visibility = View.VISIBLE
        binding.saveImageview.visibility = View.VISIBLE
        binding.refreshImageview.visibility = View.VISIBLE
        binding.playImageview.visibility = View.VISIBLE
        binding.playImageview.setImageResource(R.drawable.ic_play)

//        binding.timerTexview.visibility = View.VISIBLE
//        binding.content.visibility = View.GONE
    }

    override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
        Log.d("hdb---", i.toString())
        audioManager!!.setStreamVolume(AudioManager.STREAM_MUSIC, i, 0)
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {}
    override fun onStopTrackingTouch(seekBar: SeekBar) {}
    override fun updateTimer(value: String?, sec: Int) {
        binding.timerTexview.text = value

        Log.d("hdb---", sec.toString())
        if (recording)
            binding.audioSeekbar.progress = sec

//        Log.d("hdb--", recorder.maxAmplitude.toString())
//        val value = recorder.maxAmplitude
//        handler.post {
//            binding.audioRecordView.update(value)
//        }//redraw view
    }

    override fun stopRecording() {

        updateViews()
        recorder.stop()

        binding.timerTexview.setTextColor(
            ContextCompat.getColor(
                context,
                R.color.black
            )
        )

        handler.removeCallbacks(runnable!!)
        runnable = MyCountDownTimer(this@AudioRecorderDialog, handler)
//        binding.content.stopRippleAnimation()
        YoYo.with(Techniques.FadeIn)
            .duration(700)
            .playOn(binding.playImageview)
        mp = MediaPlayer.create(activity, Uri.parse(path))
        mp?.setOnCompletionListener { mediaPlayer: MediaPlayer? ->
            handler.removeCallbacks(runnable!!)
            runnable = MyCountDownTimer(this@AudioRecorderDialog, handler)
            binding.playImageview.setImageDrawable(
                ContextCompat.getDrawable(
                    activity,
                    R.drawable.ic_play
                )
            )
            binding.playImageview.tag = R.drawable.ic_play
        }
        if (mp != null) {
            Log.d("hdb->>", mp!!.duration.toString())
            binding.audioSeekbar.max = mp!!.duration / 1000
            binding.audioSeekbar.progress = 0

        }
    }

    override fun onMainViewAppeared(fabRevealLayout: FABRevealLayout, mainView: View) {}
    override fun onSecondaryViewAppeared(fabRevealLayout: FABRevealLayout, secondaryView: View) {
//        binding.content.startRippleAnimation()
//        binding.content.visibility = View.VISIBLE
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