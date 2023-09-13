package com.example.minesweeper;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;

import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private static final int COLUMN_COUNT = 10;
    private int clock = 0;
    private final int numFlag = 4;
    private boolean running = false;
    private boolean init = false;
    private boolean result;
    private boolean end = false;
    public static int revealed = 120;
    private Set<String> visited = new HashSet<>();
    public static TextView[][] cell_tvs = new TextView[12][10];

    // save the TextViews of all cells in an array, so later on,
    // when a TextView is clicked, we know which cell it is

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //fill the grid
        setContentView(R.layout.activity_main);
        LayoutInflater li = LayoutInflater.from(this);
        GridLayout grid = findViewById(R.id.gridLayout01);
        for (int i = 0; i <= 11; i++) {
            for (int j = 0; j <= 9; j++) {
                TextView tv = (TextView) li.inflate(R.layout.custom_cell_layout, grid, false);
                tv.setTextColor(Color.GRAY);
                tv.setBackgroundColor(Color.GRAY);
                tv.setOnClickListener(this::onClickTV);

                GridLayout.LayoutParams lp = (GridLayout.LayoutParams) tv.getLayoutParams();
                lp.rowSpec = GridLayout.spec(i);
                lp.columnSpec = GridLayout.spec(j);

                grid.addView(tv, lp);

                //populate the 2d array
                cell_tvs[i][j] = tv;
            }
        }
        //set change flag to pickaxe function
        TextView action = findViewById(R.id.textView6);
        action.setOnClickListener(this::onClickAction);
        //retrieve old timer if there is one
        if (savedInstanceState != null) {
            clock = savedInstanceState.getInt("clock");
            running = savedInstanceState.getBoolean("running");
        }
        runTimer();
        updateFlag();
    }


    //return true for flag false for pickaxe
    public boolean checkAction(){
        TextView tv = findViewById(R.id.textView6);
        return tv.getText().toString().equals("🚩");
    }

//find the index of the textview in the 2d array
    public int findIndexOfCellTextView(TextView tv) {
        for (int i = 0; i <= 11; i++) {
            for (int j = 0; j <= 9; j++) {
                if (cell_tvs[i][j] == tv) {
                    return i * COLUMN_COUNT + j;
                }
            }
        }
        return -1;
    }

    public void onClickTV(View view){
        if(!running && end){
            sendMessage(result);
        }
        if(!running){
            running = true;
        }
        TextView tv = (TextView) view;
        int n = findIndexOfCellTextView(tv);
        int i = n/COLUMN_COUNT;
        int j = n%COLUMN_COUNT;
        if(checkAction() && !end && running){
            if(!tv.getText().toString().equals("🚩") && !visited.contains(i + "," + j)){
                tv.setText("🚩");
                updateFlag();
            }
            else if(tv.getText().toString().equals("🚩")){
                tv.setText("");
                updateFlag();
            }
        }
        else if(!tv.getText().toString().equals("🚩") && !end && running){
            if (!init) {
                logic.initializeGrid(cell_tvs, i, j, visited);
                updateFlag();
                init = true;
            }
            else{
                if(logic.checkLose(i, j, cell_tvs)){
                    running = false;
                    end = true;
                    result = false;
                }
                else{
                    logic.calculateNumbers(cell_tvs);
                    logic.revealCell(cell_tvs, i, j, visited);
                    updateFlag();
                }
            }
        }
        if(logic.checkAllRevealed(cell_tvs)){
            logic.revealWin(cell_tvs);
            running = false;
            end = true;
            result = true;
        }
    }

    //when user click on the textview, swap 🚩 to ⛏️
    public void onClickAction(View view){
        TextView tv = (TextView) view;
        if (tv.getText().toString().equals("🚩")){
            tv.setText("⛏️️");
        }else {
            tv.setText("🚩");
        }

    }

    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("clock", clock);
        savedInstanceState.putBoolean("running", running);
    }
    //keep the number of flags updated
    private void updateFlag(){
        TextView timeView = findViewById(R.id.textView3);
        timeView.setText(String.valueOf(logic.countFlags(cell_tvs, numFlag)));

    }

    private void runTimer() {
        final TextView timeView = findViewById(R.id.textView5);
        final Handler handler = new Handler();

        handler.post(new Runnable() {
            @Override
            public void run() {
                int seconds = clock;
                //convert seconds to string
                String time;
                if(seconds <= 999){
                    time = Integer.toString(seconds);
                }
                else{
                    time = "999";
                }
                timeView.setText(time);
                if (running) {
                    clock++;
                }
                handler.postDelayed(this, 1000);
            }
        });
    }

    public void sendMessage(boolean win){
        Intent intent = new Intent(this, FinalActivity.class);
        if(win){
            intent.putExtra("com.example.sendmessage.MESSAGE", "Used " + clock + " seconds.\nYou Win!\nGood Job!");
        }
        else{
            intent.putExtra("com.example.sendmessage.MESSAGE", "Bummer! You Lose!");
        }
        startActivity(intent);

    }
}