package ir.hdb.audiorecorderdialog

/**
 * Created by Hadi Beigy on 05/27/21
 */
internal interface SoundDialogView {
    fun updateTimer(value: String?, sec: Int)
    fun stopRecording()
}