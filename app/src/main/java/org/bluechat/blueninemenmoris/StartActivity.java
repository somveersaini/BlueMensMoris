package org.bluechat.blueninemenmoris;

import android.content.Intent;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.SoundPool;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartActivity extends AppCompatActivity {

    SoundPool sp;
    int btnbtn, scb, scs;
    private boolean start = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        Typeface typeface = Typeface.createFromAsset(getAssets(),
                "Gasalt-Black.ttf");
        ((Button) findViewById(R.id.play)).setTypeface(typeface);

        sp = new SoundPool(6, AudioManager.STREAM_MUSIC, 0);
        btnbtn = sp.load(this, R.raw.btnbtn, 1);

    }

    @Override
    public void onWindowFocusChanged (boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
//        if (hasFocus) {
//            YoYo.with(Techniques.SlideInDown)
//                    .duration(500)
//                    .playOn(findViewById(R.id.play));
//            YoYo.with(Techniques.SlideInUp)
//                    .duration(550)
//                    .playOn(findViewById(R.id.setting));
//            YoYo.with(Techniques.SlideInUp)
//                    .duration(600)
//                    .playOn(findViewById(R.id.HelpActivity));
//            YoYo.with(Techniques.SlideInUp)
//                    .duration(650)
//                    .playOn(findViewById(R.id.AboutActivity));
//            YoYo.with(Techniques.SlideInUp)
//                    .duration(700)
//                    .playOn(findViewById(R.id.trophy));
//        }
    }

    public void onResume(){
        super.onResume();
//        YoYo.with(Techniques.SlideInDown)
//                .duration(500)
//                .playOn(findViewById(R.id.play));
//        YoYo.with(Techniques.SlideInUp)
//                .duration(550)
//                .playOn(findViewById(R.id.setting));
//        YoYo.with(Techniques.SlideInUp)
//                .duration(600)
//                .playOn(findViewById(R.id.HelpActivity));
//        YoYo.with(Techniques.SlideInUp)
//                .duration(650)
//                .playOn(findViewById(R.id.AboutActivity));
//        YoYo.with(Techniques.SlideInUp)
//                .duration(700)
//                .playOn(findViewById(R.id.trophy));
    }
    public void buttonhandler(View view) {
        int id = view.getId();
        sp.play(btnbtn, 1,1,1, 0,1);
        switch(id) {
            case R.id.play:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.setting:
                break;
            case R.id.help:
                startActivity(new Intent(this, HelpActivity.class));
                break;
            case R.id.about:
                startActivity(new Intent(this, AboutActivity.class));
                break;
            case R.id.trophy:
                startActivity(new Intent(this, AcheivementsActivity.class));
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

}
