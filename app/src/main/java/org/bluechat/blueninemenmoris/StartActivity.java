package org.bluechat.blueninemenmoris;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class StartActivity extends AppCompatActivity {

    private boolean start = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        Typeface typeface = Typeface.createFromAsset(getAssets(),
                "Gasalt-Black.ttf");
        ((Button) findViewById(R.id.play)).setTypeface(typeface);
        ((Button) findViewById(R.id.aiplayer)).setTypeface(typeface);
        ((Button) findViewById(R.id.netPlay)).setTypeface(typeface);
        ((Button) findViewById(R.id.bluePlay)).setTypeface(typeface);
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
      //  sp.play(btnbtn, 1,1,1, 0,1);
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
               // startActivity(new Intent(this, AiMainActivity.class));
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
                startActivity(new Intent(this, AcheivementsActivity.class));
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
        setContentView(R.layout.activity_start);
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
