package org.bluechat.blueninemenmoris

import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import android.view.Window
import android.widget.TextView


/**
 * Created by Samsaini on 12/05/2016.
 */
class AchievementsActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Setup the window
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS)
        setContentView(R.layout.achievements)
        val typeface = Typeface.createFromAsset(assets,
                "CarterOne.ttf")
        val typeface1 = Typeface.createFromAsset(assets,
                "future.otf")
        (findViewById<View>(R.id.a1) as TextView).setTypeface(typeface1)
        (findViewById<View>(R.id.a2) as TextView).setTypeface(typeface)
        val win = findViewById<View>(R.id.a3) as TextView
        win.setTypeface(typeface)
        win.text = Settings.gamewin.toString() + " Wins"
        val lost = findViewById<View>(R.id.a4) as TextView
        lost.setTypeface(typeface)
        lost.text = Settings.gamelost.toString() + " Lost"

    }

    override fun onDestroy() {
        super.onDestroy()
    }

    companion object {
        private val TAG = "AchiActivity"
    }
}
