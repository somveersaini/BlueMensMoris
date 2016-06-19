package org.bluechat.blueninemenmoris;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class StartActivity extends AppCompatActivity {

    private boolean start = true;
    Typeface typeface, typeface1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        typeface = Typeface.createFromAsset(getAssets(),
                "CarterOne.ttf");
        typeface1 = Typeface.createFromAsset(getAssets(),
                "future.otf");
        ((Button) findViewById(R.id.play)).setTypeface(typeface);
        ((Button) findViewById(R.id.aiplayer)).setTypeface(typeface);
        ((Button) findViewById(R.id.netPlay)).setTypeface(typeface);
        ((Button) findViewById(R.id.bluePlay)).setTypeface(typeface);
        ((TextView) findViewById(R.id.app)).setTypeface(typeface1);

        Settings.init(getApplicationContext());

    }

    public void onResume(){
        super.onResume();
        setContentView(R.layout.activity_start);
        ((Button) findViewById(R.id.play)).setTypeface(typeface);
        ((Button) findViewById(R.id.aiplayer)).setTypeface(typeface);
        ((Button) findViewById(R.id.netPlay)).setTypeface(typeface);
        ((Button) findViewById(R.id.bluePlay)).setTypeface(typeface);
        ((TextView) findViewById(R.id.app)).setTypeface(typeface1);

    }

    public void buttonhandler(View view) {
        int id = view.getId();
        Settings.buttonSound();
        switch(id) {
            case R.id.play:
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("isAI", false);
                startActivity(intent);
                break;
            case R.id.aiplayer:
                setContentView(R.layout.singleplayer);
                break;
            case R.id.setting:
                startActivity(new Intent(this, Settings.class));
                break;
            case R.id.netPlay:
                startActivity(new Intent(this, NetMainActivity.class));
                break;
            case R.id.help:
                startActivity(new Intent(this, AboutActivity.class));
                break;
            case R.id.about:
                startActivity(new Intent(this, HelpActivity.class));
                break;
            case R.id.trophy:
                startActivity(new Intent(this, AchievementsActivity.class));
                break;
            case R.id.bluePlay:
                startActivity(new Intent(this, BlueMainActivity.class));
                break;
            default:
                break;
        }
    }

    public void onBackPressed(){
        if(!start){
            setContentView(R.layout.activity_start);
        }
        else{
            this.finish();
        }
    }

    public void levelchooser(View view) {
        int id = view.getId();
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("isAI", true);


        switch (id) {
            case R.id.easy:
                intent.putExtra("level", 3);
                startActivity(intent);
                break;
            case R.id.medium:
                intent.putExtra("level", 4);
                startActivity(intent);
                break;
            case R.id.hard:
                intent.putExtra("level", 5);
                startActivity(intent);
                break;
            default:
                startActivity(intent);
                break;
        }
    }
}
