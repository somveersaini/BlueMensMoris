package org.bluechat.blueninemenmoris;

import android.app.Activity;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Window;


/**
 * Created by Samsaini on 12/05/2016.
 */
public class AcheivementsActivity extends Activity{
    private static final String TAG = "AchiActivity" ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Setup the window
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.help);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
