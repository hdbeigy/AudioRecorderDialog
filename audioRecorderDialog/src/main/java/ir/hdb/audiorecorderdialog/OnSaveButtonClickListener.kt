package ir.hdb.audiorecorderdialog

/**
 * Created by Hadi Beigy on 05/27/21
 */
interface OnSaveButtonClickListener {
    fun onSucceed(path: String?)
    fun onFailure()
}