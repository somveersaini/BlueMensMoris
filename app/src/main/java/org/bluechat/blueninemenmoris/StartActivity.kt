package org.bluechat.blueninemenmoris

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.TextView

class StartActivity : AppCompatActivity() {

    private val start = true
    lateinit var typeface: Typeface
    lateinit var typeface1: Typeface
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        typeface = Typeface.createFromAsset(assets,
                "CarterOne.ttf")
        typeface1 = Typeface.createFromAsset(assets,
                "future.otf")
        (findViewById<View>(R.id.play) as Button).typeface = typeface
        (findViewById<View>(R.id.aiplayer) as Button).typeface = typeface
        (findViewById<View>(R.id.netPlay) as Button).typeface = typeface
        (findViewById<View>(R.id.bluePlay) as Button).typeface = typeface
        (findViewById<View>(R.id.app) as TextView).typeface = typeface1

        Settings.init(applicationContext)

    }

    public override fun onResume() {
        super.onResume()
        setContentView(R.layout.activity_start)
        (findViewById<View>(R.id.play) as Button).typeface = typeface
        (findViewById<View>(R.id.aiplayer) as Button).typeface = typeface
        (findViewById<View>(R.id.netPlay) as Button).typeface = typeface
        (findViewById<View>(R.id.bluePlay) as Button).typeface = typeface
        (findViewById<View>(R.id.app) as TextView).typeface = typeface1

    }

    fun buttonhandler(view: View) {
        val id = view.id
        Settings.buttonSound()
        when (id) {
            R.id.play -> {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("isAI", false)
                startActivity(intent)
            }
            R.id.aiplayer -> setContentView(R.layout.singleplayer)
            R.id.setting -> startActivity(Intent(this, Settings::class.java))
            //            case R.id.netPlay:
            //                startActivity(new Intent(this, NetMainActivity.class));
            //                break;
            R.id.help -> startActivity(Intent(this, AboutActivity::class.java))
            R.id.about -> startActivity(Intent(this, HelpActivity::class.java))
            R.id.trophy -> startActivity(Intent(this, AchievementsActivity::class.java))
            R.id.bluePlay -> startActivity(Intent(this, BlueMainActivity::class.java))
            else -> {
            }
        }
    }

    override fun onBackPressed() {
        if (!start) {
            setContentView(R.layout.activity_start)
        } else {
            this.finish()
        }
    }

    fun levelchooser(view: View) {
        val id = view.id
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("isAI", true)


        when (id) {
            R.id.easy -> {
                intent.putExtra("level", 3)
                startActivity(intent)
            }
            R.id.medium -> {
                intent.putExtra("level", 4)
                startActivity(intent)
            }
            R.id.hard -> {
                intent.putExtra("level", 5)
                startActivity(intent)
            }
            else -> startActivity(intent)
        }
    }
}
