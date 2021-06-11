package se.lth.solid.vilmer.thirtygame

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    private val TEXT_KEY: String = "TEXT"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val button = findViewById<Button>(R.id.button)
        var textView = findViewById<TextView>(R.id.textView)
        if (savedInstanceState != null) {
            textView.text = savedInstanceState.getString(TEXT_KEY)
        }
        button.setOnClickListener {
            textView.text = "Hejsan världen"
        }
    }

}