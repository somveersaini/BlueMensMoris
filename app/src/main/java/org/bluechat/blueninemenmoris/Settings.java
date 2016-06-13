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
 * Created by Samsaini on 12/05/2016.
 */
public class Settings extends Activity {
    private static final String TAG = "SettingsActivity";
    public static boolean sfx;
    public static int theme;
    public static int score;
    public static int currentScore;
    public static boolean next = false;
    public static int gamePlayed = 0;
    public static ArrayList<String> games = new ArrayList<>();
    public static SoundPool sp;
    public static int btnbtn, gamesound, win, conti;
    public static int column = 8;
    public static int row = 8;
    public static boolean boardchange = false;
    Switch ssfx;
    SharedPreferences settings;
    EditText etwidth;
    EditText etheight;

    static void buttonSound() {
        if (sfx) {
            sp.play(btnbtn, 0.5f, 0.5f, 1, 0, 1);
        }
    }

    static void gameSound() {
        if (sfx) {
            sp.play(btnbtn, 0.5f, 0.5f, 1, 0, 1);
        }
    }

    static void winSound() {
        if (sfx) {
            sp.play(win, 0.5f, 0.5f, 1, 0, 1);
        }
    }

    static void contiSound() {
        if (sfx) {
            sp.play(conti, 0.5f, 0.5f, 1, 0, 1);
        }
    }

    public static void addGame(SharedPreferences set, String game) {
        games.add(game);
        ++gamePlayed;
        SharedPreferences.Editor editor = set.edit();
        editor.putInt("gameplayed", gamePlayed);
        for (int i = 0; i < gamePlayed; i++) {
            editor.putString("game" + i, games.get(i));
        }
        editor.apply();
    }

    public static void init(Context context) {
        sp = new SoundPool(6, AudioManager.STREAM_MUSIC, 0);
        btnbtn = sp.load(context, R.raw.btnbtn, 1);
        // gamesound = sp.load(context, R.raw.gamebtn, 1);
        win = sp.load(context, R.raw.win, 1);
        // conti =  sp.load(context, R.raw.conti, 1);


        load(PreferenceManager.getDefaultSharedPreferences(context));
    }

    //loadState
    public static void load(SharedPreferences set) {

        sfx = set.getBoolean("sfx", true);
        row = set.getInt("row", 8);
        column = set.getInt("column", 8);
        score = set.getInt("score", 0);
        gamePlayed = set.getInt("gameplayed", 0);
        ArrayList<String> arrayList = new ArrayList<>();
        for (int i = 0; i < gamePlayed; i++) {
            arrayList.add(i, set.getString("game" + i, null));
        }
        games = arrayList;
        System.out.println(gamePlayed + " " + games);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Setup the window
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.settings);
        etheight = (EditText) findViewById(R.id.numrow);
        etwidth = (EditText) findViewById(R.id.numcol);

        Typeface typeface = Typeface.createFromAsset(getAssets(),
                "Gasalt-Black.ttf");
        etheight.setTypeface(typeface);
        etwidth.setTypeface(typeface);

        ((TextView) findViewById(R.id.gameset1)).setTypeface(typeface);
        ((TextView) findViewById(R.id.gameset2)).setTypeface(typeface);
        ((TextView) findViewById(R.id.gameset3)).setTypeface(typeface);


        settings = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());

        ssfx = (Switch) findViewById(R.id.switch2);
        ssfx.setTypeface(typeface);
        if (sfx) {
            ssfx.setChecked(true);
        }
        load(settings);
        etheight.setText(String.valueOf(row));
        etwidth.setText(String.valueOf(column));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
        if (view instanceof Button) {
            buttonSound();

        }
        save();
    }

    private void save() {
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("sfx", sfx);
        editor.putInt("theme", theme);
        editor.putInt("score", score);
        editor.putInt("column", column);
        editor.putInt("row", row);
        editor.putInt("gameplayed", gamePlayed);
        for (int i = 0; i < gamePlayed; i++) {
            editor.putString("game" + i, games.get(i));
        }
        editor.apply();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Log.d(etwidth.getText().toString(), etwidth.getText().toString());
        if (etheight.getText().length() > 0 && etwidth.getText().length() > 0) {
            int rows = 0;
            int col = 0;
            try {
                col = Integer.parseInt(etwidth.getText().toString());
                rows = Integer.parseInt(etheight.getText().toString());
            } catch (Exception e) {
                //error happen
                //not valid number
            }
            if (col <= 100 && col > 2 && rows > 2 && rows <= 100) {
                column = col;
                row = rows;
                boardchange = true;
            }
        }
        save();
    }
}
