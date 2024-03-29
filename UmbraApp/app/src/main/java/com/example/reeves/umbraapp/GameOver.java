package com.example.reeves.umbraapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class GameOver extends Activity implements View.OnClickListener {

    private String last_difficulty;
    private String last_score;
    private int scores[];
    private boolean restart;
    private static final String file_name = "last_game.txt";
    private static final String score_file = "scores.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        restart = false;

        try {
            loadLastGame();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        try {
            scores = new int[10];
            loadScores();
            updateScores();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        // Set Fullscreen Immersive to Remove Navigation Bar, etc.
        // Referenced https://developer.android.com/training/system-ui/immersive
        this.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);

        setContentView(R.layout.activity_game_over);

        TextView title = findViewById(R.id.title);
        title.setText("Game Over");

        TextView score_text = findViewById(R.id.score_text);
        score_text.setText("Score:");

        TextView difficulty_text = findViewById(R.id.difficulty_text);
        difficulty_text.setText("Difficulty:");

        TextView restart_text = findViewById(R.id.restart_text);
        restart_text.setText("Restart?");

        TextView score = findViewById(R.id.score);
        score.setText(last_score);

        TextView difficulty = findViewById(R.id.difficulty);
        difficulty.setText(last_difficulty);

        Button restart_button = findViewById(R.id.restart_button);
        restart_button.setText("Yes");
        restart_button.setOnClickListener(this);

        Button exit_button = findViewById(R.id.exit_button);
        exit_button.setText("No");
        exit_button.setOnClickListener(this);
    }

    // Remove System UI When Playing
    // Referenced https://developer.android.com/training/system-ui/immersive
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus){
            hideSystemUI();
        }
    }

    public void hideSystemUI() {
        this.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (restart) {
            startActivity(new Intent(this, GameLauncher.class));
        }
    }

    @Override
    public void onClick(View v){
        switch(v.getId()) {
            case R.id.restart_button:
                restart = true;
                finish();
                break;
            case R.id.exit_button:
                restart = false;
                finish();
                break;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                hideSystemUI();
        }
        return true;
    }

    public void loadLastGame()
            throws IOException {
        String game_array[] = new String[2];
        String difficulties[] = new String[]{"Very Easy","Easy","Normal","Hard","Very Hard"};

        BufferedReader r = null;
        try {
            InputStream in = openFileInput(file_name);
            r = new BufferedReader(new InputStreamReader(in));
            game_array[0] = r.readLine();
            int difficulty_int = Integer.parseInt(r.readLine());
            game_array[1] = difficulties[difficulty_int];
        }
        catch (FileNotFoundException e) {
            game_array[0] = "0";
            game_array[1] = difficulties[0];
        }
        finally {
            if (r != null) {
                r.close();
            }
        }

        last_score = game_array[0];
        last_difficulty = game_array[1];
    }

    public void loadScores()
            throws IOException {

        BufferedReader r = null;
        try {
            InputStream in = openFileInput(score_file);
            r = new BufferedReader(new InputStreamReader(in));
            for (int i = 0; i < 10; i++) {
                String line = r.readLine();
                scores[i] = Integer.parseInt(line);
            }
        }
        catch (FileNotFoundException e) {
            for (int i = 0; i < 10; i++) {
                scores[i] = (i + 1) * 1000;
            }
        }
        finally {
            if (r != null) {
                r.close();
            }
        }
    }

    public void updateScores() {
        // Find where last score places among the other scores
        int score_placement = -1;
        for (int i = 0; i < 10; i++) {
            if (Integer.parseInt(last_score) > scores[i]) {
                score_placement = i;
            }
            else {
                break;
            }
        }

        if (score_placement >= 0) {
            // Move scores from score_placement one place downwards
            for (int i = 0; i < score_placement; i++) {
                scores[i] = scores[i + 1];
            }
            scores[score_placement] = Integer.parseInt(last_score);
        }

        try {
            saveScores();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveScores()
        throws IOException{

            Writer w = null;
            try {
                OutputStream out = openFileOutput(score_file, MODE_PRIVATE);
                w = new OutputStreamWriter(out);
                for (int i = 0; i < 10; i++) {
                    w.write(Integer.toString(scores[i]));
                    w.write('\n');
                }
            } finally {
                if (w != null) {
                    w.close();
                }
            }
        }
}