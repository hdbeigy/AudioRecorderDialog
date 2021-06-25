package ir.hdb.audioRecorderDialogExample

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ir.hdb.audiorecorderdialog.MediaRecorderDialog
import ir.hdb.audiorecorderdialog.OnSaveButtonClickListener


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.button).setOnClickListener {

            MediaRecorderDialog.Builder(this@MainActivity)
                .setOutputFormat(MediaRecorderDialog.OutputFormat.MPEG_4)
                .setAudioEncoder(MediaRecorderDialog.AudioEncoder.AAC)
                .setMaxLength(MediaRecorderDialog.TimeUnit.SECONDS, 10)
                .setTitle("Recording...")
                .setMessage("Press the button")
                .setOnSaveButtonClickListener(object :
                    OnSaveButtonClickListener {
                    override fun onSucceed(path: String?) {
                        Toast.makeText(this@MainActivity, path, Toast.LENGTH_SHORT).show();
                    }

                    override fun onFailure() {
                        Toast.makeText(this@MainActivity, "Failure", Toast.LENGTH_SHORT).show();
                    }
                })
                .show()
        }
    }
}