package org.bluechat.blueninemenmoris

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Typeface
import android.media.AudioManager
import android.media.SoundPool
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView


/**
 * Created by Samsaini on 12/06/2016.
 */
class Settings : AppCompatActivity() {

    internal lateinit var ssfx: Switch
    internal lateinit var settings: SharedPreferences
    internal lateinit var name: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Setup the window
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS)
        setContentView(R.layout.settings)
        name = findViewById<View>(R.id.name) as EditText

        val typeface = Typeface.createFromAsset(assets,
                "Gasalt-Black.ttf")
        val typeface1 = Typeface.createFromAsset(assets,
                "future.otf")

        name.typeface = typeface

        (findViewById<View>(R.id.gameset1) as TextView).typeface = typeface
        (findViewById<View>(R.id.gameset2) as TextView).typeface = typeface
        (findViewById<View>(R.id.setapp) as TextView).typeface = typeface1

        settings = PreferenceManager.getDefaultSharedPreferences(this.applicationContext)

        ssfx = findViewById<View>(R.id.switch2) as Switch
        ssfx.typeface = typeface
        if (sfx) {
            ssfx.isChecked = true
        }
        load(this.applicationContext)
        name.setText(pName)
    }

    fun buttonsettings(view: View) {
        val id = view.id

        if (view is Switch) {
            buttonSound()
            val isChecked = view.isChecked
            if (id == R.id.switch2) {
                sfx = isChecked
            }
        }
        save()
    }

    //saveState
    private fun save() {
        val editor = settings.edit()
        editor.putBoolean("sfx", sfx)
       // editor.putInt("theme", theme)
        editor.putInt("score", score)
        editor.putInt("gamewin", gamewin)
        editor.putInt("gamedraw", gamedraw)
        editor.putInt("gamelost", gamelost)
        editor.putString("name", pName)
        editor.apply()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (name.text != null) {
            val playername = name.text.toString()
            if (playername.length == 0) {
                pName = "Sam"
            } else {
                Log.d(TAG, "onBackPressed: saving name $playername")
                pName = playername
            }
        }
        save()
    }

    companion object {
        private val TAG = "SettingsActivity"
        var sfx: Boolean = false
        var theme: Int = 0
        var score: Int = 0

        var gamewin = 0
        var gamedraw = 0
        var gamelost = 0
        lateinit var sp: SoundPool
        var place: Int = 0
        var select: Int = 0
        var win: Int = 0
        var click: Int = 0
        var move: Int = 0
        var phasechange: Int = 0
        var mill: Int = 0
        var pName: String = "Sam"

        fun init(context: Context) {
            sp = SoundPool(9, AudioManager.STREAM_MUSIC, 0)
            click = sp.load(context, R.raw.btnbtn, 1)
            move = sp.load(context, R.raw.move, 1)
            phasechange = sp.load(context, R.raw.phasechange, 1)
            place = sp.load(context, R.raw.place, 1)
            //win = sp.load(context, R.raw.win, 1);
            select = sp.load(context, R.raw.btnbtn, 1)
            mill = sp.load(context, R.raw.mill, 1)

            load(context)
        }

        //loadState
        fun load(context: Context) {
            val set = PreferenceManager.getDefaultSharedPreferences(context)
            sfx = set.getBoolean("sfx", true)
            score = set.getInt("score", 0)
            gamewin = set.getInt("gamewin", 0)
            gamedraw = set.getInt("gamedraw", 0)
            gamelost = set.getInt("gamelost", 0)
            pName = set.getString("name", "Sam") ?: "sam"
        }

        fun addGame(context: Context, game: String) {
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            if (game == "draw") {
                ++gamedraw
                editor.putInt("gamedraw", gamedraw)
            } else if (game == "win") {
                ++gamewin
                editor.putInt("gamewin", gamewin)
            } else {
                ++gamelost
                editor.putInt("gamelost", gamelost)
            }
            editor.apply()
        }

        //sounds
        internal fun buttonSound() {
            if (sfx) {
                sp.play(click, 1f, 1f, 1, 0, 1f)
            }
        }

        internal fun moveSound() {
            if (sfx) {
                sp.play(move, 1f, 1f, 1, 0, 1f)
            }
        }

        internal fun placeSound() {
            if (sfx) {
                sp.play(place, 1f, 1f, 1, 0, 1f)
            }
        }

        internal fun place1Sound() {
            if (sfx) {
                sp.play(place, 0.5f, 0.5f, 1, 0, 1f)
            }
        }

        internal fun removeSound() {
            if (sfx) {
                sp.play(place, 0.5f, 0.5f, 1, 0, 1f)
            }
        }

        internal fun phaseSound() {
            if (sfx) {
                sp.play(phasechange, 0.5f, 0.5f, 1, 0, 1f)
            }
        }

        internal fun selectSound() {
            if (sfx) {
                sp.play(select, 1f, 1f, 1, 0, 1f)
            }
        }

        internal fun winSound() {
            if (sfx) {
                sp.play(win, 0.5f, 0.5f, 1, 0, 1f)
            }
        }

        internal fun millSound() {
            if (sfx) {
                sp.play(mill, 0.5f, 0.5f, 1, 0, 1f)
            }
        }
    }
}
