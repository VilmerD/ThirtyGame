package se.lth.solid.vilmer.thirtygame

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    private val CLICKED_TINT: String = "@color/purple_700"
    private val UNCLICKED_TINT: String = "@color/green"
    private lateinit var gameModel: GameModel
    private lateinit var buttonArray: Array<Button>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gameModel = GameModel()

    }

}