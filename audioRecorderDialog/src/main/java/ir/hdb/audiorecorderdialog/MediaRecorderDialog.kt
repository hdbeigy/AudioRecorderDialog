package ir.hdb.audiorecorderdialog

import android.Manifest
import android.app.Activity
import androidx.constraintlayout.widget.ConstraintLayout
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener

/**
 * Created by Hadi Beigy on 05/27/21
 */
class MediaRecorderDialog {
    class Builder(private val context: Activity) {

        private var title: String = ""
        private var message: String = ""
        private var outPutFormat: OutputFormat
        private var audioEncoder: AudioEncoder
        private var onSaveButtonClickListener: OnSaveButtonClickListener? = null
        private var length = -1

        fun setTitle(title: String): Builder {
            this.title = title
            return this
        }

        fun setMessage(message: String): Builder {
            this.message = message
            return this
        }

        fun show(): Builder {
            Dexter.withContext(context)
                .withPermissions(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.RECORD_AUDIO
                ).withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                        val dialog = AudioRecorderDialog(
                            context,
                            outPutFormat,
                            audioEncoder,
                            onSaveButtonClickListener!!
                        )
                            dialog.show()

                        dialog.window?.setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permissions: List<PermissionRequest>,
                        token: PermissionToken
                    ) {
                    }
                }).check()
            return this
        }

        fun setOutputFormat(outputFormat: OutputFormat): Builder {
            this.outPutFormat = outputFormat
            return this
        }

        fun setAudioEncoder(audioEncoder: AudioEncoder): Builder {
            this.audioEncoder = audioEncoder
            return this
        }

        fun setOnSaveButtonClickListener(onSaveButtonClickListener: OnSaveButtonClickListener?): Builder {
            this.onSaveButtonClickListener = onSaveButtonClickListener
            return this
        }

        fun setMaxLength(timeUnit: TimeUnit, length: Int): Builder {
            this.length = length * timeUnit.value
            return this
        }

        init {
            this.title = ""
            this.message = ""
            this.outPutFormat = OutputFormat.MPEG_4
            this.audioEncoder = AudioEncoder.AAC
        }
    }

    enum class OutputFormat(val value: Int) {
        AAC_ADTS(6), AMR_NB(3), AMR_WB(4), DEFAULT(0), MPEG_4(2);

    }

    enum class AudioEncoder(val value: Int) {
        AAC(3), AAC_ELD(5), AMR_NB(1), AMR_WB(2), DEFAULT(0), HE_AAC(4), VORBIS(6);

    }

    enum class TimeUnit(val value: Int) {
        MINUTES(60), SECONDS(1);

    }
}