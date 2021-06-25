package ir.hdb.audiorecorderdialog

import android.Manifest
import android.app.Activity
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
        fun setTitle(title: String?): Builder {
            AppConstants.title = title
            return this
        }

        fun setMessage(msg: String?): Builder {
            AppConstants.message = msg
            return this
        }

        fun show(): Builder {
//            new SoundDialog(GenralAtteribute.context).show();
//            new Gota(GenralAtteribute.context).checkPermission(new String[]{Manifest.permission.RECORD_AUDIO
//                    ,Manifest.permission.}, new Gota.OnRequestPermissionsBack() {
//                @Override
//                public void onRequestBack(GotaResponse goaResponse) {
//
//                }
//            });
//            Dexter.withContext(GenralAtteribute.context)
//                    .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                    .withListener(new PermissionListener() {
//                        @Override public void onPermissionGranted(PermissionGrantedResponse response) {
//                            new SoundDialog(GenralAtteribute.context).show();
//                        }
//                        @Override public void onPermissionDenied(PermissionDeniedResponse response) {/* ... */}
//                        @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {/* ... */}
//                    }).check();
            Dexter.withContext(context)
                .withPermissions(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.RECORD_AUDIO
                ).withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                        AudioRecorderDialog(context).show()
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permissions: List<PermissionRequest>,
                        token: PermissionToken
                    ) {
                    }
                }).check()
            return this
        }

        fun setOutputFormat(outputFormat: OutputFormat?): Builder {
            AppConstants.outPutFormat = outputFormat
            return this
        }

        fun setAudioEncoder(audioEncoder: AudioEncoder?): Builder {
            AppConstants.audioEncoder = audioEncoder
            return this
        }

        fun setOnSaveButtonClickListener(onSaveButtonClickListener: OnSaveButtonClickListener?): Builder {
            AppConstants.onSaveButtonClickListener = onSaveButtonClickListener
            return this
        }

        fun setMaxLength(timeUnit: TimeUnit, length: Int): Builder {
            AppConstants.length = length * timeUnit.value
            return this
        }

        init {
            AppConstants.title = ""
            AppConstants.message = ""
            AppConstants.outPutFormat = OutputFormat.MPEG_4
            AppConstants.audioEncoder = AudioEncoder.AAC
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