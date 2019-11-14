package org.bluechat.blueninemenmoris

import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.TextView

import androidx.appcompat.app.AppCompatActivity


/**
 * Created by Samsaini on 12/05/2016.
 */
class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS)
        setContentView(R.layout.about)

        val typeface1 = Typeface.createFromAsset(assets,
                "future.otf")
        val typeface2 = Typeface.createFromAsset(assets,
                "Gasalt-Black.ttf")
        (findViewById<View>(R.id.about1) as TextView).setTypeface(typeface1)
        (findViewById<View>(R.id.about2) as TextView).setTypeface(typeface2)
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
