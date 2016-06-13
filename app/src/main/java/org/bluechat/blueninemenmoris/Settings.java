package org.bluechat.blueninemenmoris;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * Created by Samsaini on 12/06/2016.
 */
public class Settings extends Activity {
    private static final String TAG = "SettingsActivity";
    public static boolean sfx;
    public static int theme;
    public static int score;

    public static int gamewin = 0, gamedraw = 0, gamelost = 0;
    public static SoundPool sp;
    public static int  place, place1, removestone, select, win,click, move, phasechange, mill;
    public static String pName = "Sam";

    Switch ssfx;
    SharedPreferences settings;
    EditText name;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Setup the window
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.settings);
        name = (EditText) findViewById(R.id.name);

        Typeface typeface = Typeface.createFromAsset(getAssets(),
                "Gasalt-Black.ttf");
        name.setTypeface(typeface);

        ((TextView) findViewById(R.id.gameset1)).setTypeface(typeface);
        ((TextView) findViewById(R.id.gameset2)).setTypeface(typeface);

        settings = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());

        ssfx = (Switch) findViewById(R.id.switch2);
        ssfx.setTypeface(typeface);
        if (sfx) {
            ssfx.setChecked(true);
        }
        load(this.getApplicationContext());
    }

    public static void init(Context context) {
        sp = new SoundPool(9, AudioManager.STREAM_MUSIC, 0);
        click = sp.load(context, R.raw.click, 1);
        move = sp.load(context, R.raw.move, 1);
        phasechange = sp.load(context, R.raw.phasechange, 1);
        place = sp.load(context, R.raw.place, 1);
        place1 = sp.load(context, R.raw.place1, 1);
        win = sp.load(context, R.raw.win, 1);
        select = sp.load(context, R.raw.select, 1);
        removestone = sp.load(context, R.raw.removestone, 1);
        mill = sp.load(context, R.raw.mill, 1);

        load(context);
    }

    public void buttonsettings(View view) {
        final int id = view.getId();

        if (view instanceof Switch) {
            buttonSound();
            Switch s = (Switch) view;
            boolean isChecked = s.isChecked();
            if (id == R.id.switch2) {
                sfx = isChecked;
            }
        }
        save();
    }

    //loadState
    public static void load(Context context) {
        SharedPreferences set = PreferenceManager.getDefaultSharedPreferences(context);
        sfx = set.getBoolean("sfx", true);
        score = set.getInt("score", 0);
        gamewin = set.getInt("gamewin", 0);
        gamedraw = set.getInt("gamedraw", 0);
        gamelost = set.getInt("gamelost", 0);
        pName = set.getString("name", "Sam");
    }
    //saveState
    private void save() {
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("sfx", sfx);
        editor.putInt("theme", theme);
        editor.putInt("score", score);
        editor.putInt("gamewin", gamewin);
        editor.putInt("gamedraw", gamedraw);
        editor.putInt("gamelost", gamelost);
        editor.putString("name", pName);
        editor.apply();
    }

    public static void addGame(Context context, String game) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        if(game.equals("draw")){
            ++gamedraw;
            editor.putInt("gamedraw", gamedraw);
        }else if(game.equals("win")){
            ++gamewin;
            editor.putInt("gamewin", gamewin);
        }else {
            ++gamelost;
            editor.putInt("gamelost", gamelost);
        }
        editor.apply();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(name.getText() != null) {
            String playername = name.getText().toString();
            if(playername.length() < 20) {
                Log.d(TAG, "onBackPressed: saving name " + playername);
                pName = playername;
            }
        }
        save();
    }

    //sounds
    static void buttonSound() {
        if (sfx) {
            sp.play(click, 0.5f, 0.5f, 1, 0, 1);
        }
    }
    static void moveSound() {
        if (sfx) {
            sp.play(move, 0.5f, 0.5f, 1, 0, 1);
        }
    }
    static void placeSound() {
        if (sfx) {
            sp.play(place, 0.5f, 0.5f, 1, 0, 1);
        }
    }
    static void place1Sound() {
        if (sfx) {
            sp.play(place1, 0.5f, 0.5f, 1, 0, 1);
        }
    }
    static void removeSound() {
        if (sfx) {
            sp.play(removestone, 0.5f, 0.5f, 1, 0, 1);
        }
    }
    static void phaseSound() {
        if (sfx) {
            sp.play(phasechange, 0.5f, 0.5f, 1, 0, 1);
        }
    }
    static void selectSound() {
        if (sfx) {
            sp.play(select, 0.5f, 0.5f, 1, 0, 1);
        }
    }
    static void winSound() {
        if (sfx) {
            sp.play(win, 0.5f, 0.5f, 1, 0, 1);
        }
    }
    static void millSound() {
        if (sfx) {
            sp.play(mill, 0.5f, 0.5f, 1, 0, 1);
        }
    }
}
