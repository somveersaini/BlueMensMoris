package org.bluechat.blueninemenmoris

import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import android.view.Window
import android.widget.TextView


/**
 * Created by Somveersaini on 12/05/2016.
 */
class HelpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Setup the window
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS)
        setContentView(R.layout.help)

        val typeface = Typeface.createFromAsset(assets,
                "zorque.ttf")
        val typeface1 = Typeface.createFromAsset(assets,
                "future.otf")
        val typeface2 = Typeface.createFromAsset(assets,
                "halo.TTF")
        val typeface3 = Typeface.createFromAsset(assets,
                "Gasalt-Black.ttf")
        (findViewById<View>(R.id.help1) as TextView).setTypeface(typeface1)
        (findViewById<View>(R.id.help2) as TextView).setTypeface(typeface)
        (findViewById<View>(R.id.help3) as TextView).setTypeface(typeface)
        (findViewById<View>(R.id.developer) as TextView).setTypeface(typeface2)
        (findViewById<View>(R.id.email) as TextView).setTypeface(typeface)
        (findViewById<View>(R.id.cont) as TextView).setTypeface(typeface)
        (findViewById<View>(R.id.fb) as TextView).setTypeface(typeface)
        (findViewById<View>(R.id.source) as TextView).setTypeface(typeface3)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    companion object {
        private val TAG = "HelpActivity"
    }


}
