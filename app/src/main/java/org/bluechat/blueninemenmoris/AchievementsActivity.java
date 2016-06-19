package org.bluechat.blueninemenmoris;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Window;
import android.widget.TextView;


/**
 * Created by Samsaini on 12/05/2016.
 */
public class AchievementsActivity extends Activity{
    private static final String TAG = "AchiActivity" ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Setup the window
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.achievements);
        Typeface typeface = Typeface.createFromAsset(getAssets(),
                "CarterOne.ttf");
        Typeface typeface1 = Typeface.createFromAsset(getAssets(),
                "future.otf");
        ((TextView) findViewById(R.id.a1)).setTypeface(typeface1);
        ((TextView) findViewById(R.id.a2)).setTypeface(typeface);
        TextView win = ((TextView) findViewById(R.id.a3));
        win.setTypeface(typeface);
        win.setText(Settings.gamewin + " Wins");
        TextView lost = ((TextView) findViewById(R.id.a4));
        lost.setTypeface(typeface);
        lost.setText(Settings.gamelost + " Lost");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
