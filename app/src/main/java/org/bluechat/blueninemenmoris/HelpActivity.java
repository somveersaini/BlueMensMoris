package org.bluechat.blueninemenmoris;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;


/**
 * Created by Somveersaini on 12/05/2016.
 */
public class HelpActivity extends Activity {
    private static final String TAG = "HelpActivity" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Setup the window
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.help);

        Typeface typeface = Typeface.createFromAsset(getAssets(),
                "zorque.ttf");
        Typeface typeface1 = Typeface.createFromAsset(getAssets(),
                "future.otf");
        Typeface typeface2 = Typeface.createFromAsset(getAssets(),
                "halo.TTF");
        Typeface typeface3 = Typeface.createFromAsset(getAssets(),
                "Gasalt-Black.ttf");
        ((TextView) findViewById(R.id.help1)).setTypeface(typeface1);
        ((TextView) findViewById(R.id.help2)).setTypeface(typeface);
        ((TextView) findViewById(R.id.help3)).setTypeface(typeface);
        ((TextView) findViewById(R.id.developer)).setTypeface(typeface2);
        ((TextView) findViewById(R.id.email)).setTypeface(typeface);
        ((TextView) findViewById(R.id.cont)).setTypeface(typeface);
        ((TextView) findViewById(R.id.fb)).setTypeface(typeface);
        ((TextView) findViewById(R.id.source)).setTypeface(typeface3);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}
