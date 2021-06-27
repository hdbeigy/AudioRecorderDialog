package ir.hdb.audiorecorderdialog

import android.os.Handler

/**
 * Created by Hadi Beigy on 05/27/21
 */
internal class MyCountDownTimer(private val mView: SoundDialogView, private val handler: Handler, private var length:Int = -1) :
    Runnable {
    private var sec = 0
    private var min = 0
    override fun run() {
        sec++
        if (sec == 60) {
            min++
            sec = 0
        }
        if (length != -1) {
            if (length < sec + min * 60) mView.stopRecording()
        }
        mView.updateTimer(String.format("%02d", min) + ":" + String.format("%02d", sec))
        handler.postDelayed(this, 1000)
    }
}