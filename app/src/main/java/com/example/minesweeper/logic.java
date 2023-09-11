package com.example.minesweeper;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.TextView;

import java.util.Set;

public class logic {
    private static final int MINE_COUNT = 4;
    private static int[][] mines = new int[MINE_COUNT][2];
    //a function that place 4 mines randomly in 10 by 12 2d grid
    public static void placeMines(TextView[][] grid, int i, int j) {
        int count = 0;
        while (count < MINE_COUNT) {
            int x = (int) (Math.random() * 11);
            int y = (int) (Math.random() * 9);
            if (!isMine(x, y) && x != i && y != j) {
                //grid[x][y].setText("\uD83D\uDCA3");
                mines[count] = new int[]{x, y};
                count++;
            }
        }
    }



    //a function that calculate the number of mines around a cell
public static void calculateNumbers(TextView[][] grid) {
        for (int i = 0; i <= 11; i++) {
            for (int j = 0; j <= 9; j++) {
                if (!isMine(i, j)) {
                    int count = 0;
                    if (i > 0 && j > 0 && isMine(i-1, j-1)) {
                        count++;
                    }
                    if (i > 0 && isMine(i-1, j)) {
                        count++;
                    }
                    if (i > 0 && j < 9 && isMine(i-1, j+1)) {
                        count++;
                    }
                    if (j > 0 && isMine(i, j-1)) {
                        count++;
                    }
                    if (j < 9 && isMine(i, j+1)) {
                        count++;
                    }
                    if (i < 11 && j > 0 && isMine(i+1, j-1)) {
                        count++;
                    }
                    if (i < 11 && isMine(i+1, j)) {
                        count++;
                    }
                    if (i < 11 && j < 9 && isMine(i+1, j+1)) {
                        count++;
                    }
                    if (count > 0) {
                        grid[i][j].setText(String.valueOf(count));
                    } else {
                        grid[i][j].setText("");
                    }
                }
            }
        }
}


    //a function that initialize the grid
    public static void initializeGrid(TextView[][] grid, int i, int j, Set<String> visited) {
        placeMines(grid, i, j);
        calculateNumbers(grid);
        revealCell(grid,i,j, visited);
    }

    //a function that check if the cell is a mine use i j
    public static boolean isMine(int i, int j) {
        for(int k = 0; k < MINE_COUNT; k++){
            if(mines[k][0] == i && mines[k][1] == j){
                return true;
            }
        }
        return false;
    }

    //a function that check if the cell is a number use i j
    public static boolean isNumber(TextView[][] grid, int i, int j) {
        return !grid[i][j].getText().toString().equals("");
    }

    //reveal the cells around the cell that has no mines around it
    //check 8 cells around it and if the cell is already grey or has a number then stop
    public static void revealCell(TextView[][] grid, int i, int j, Set<String> visited) {
        if (i < 0 || i >= grid.length || j < 0 || j >= grid[0].length) return;

        String cellKey = i + "," + j;

        if (visited.contains(cellKey) || isMine(i, j)) {
            return;
        }

        visited.add(cellKey);

        if (isNumber(grid, i, j)) {
            grid[i][j].setBackgroundColor(Color.parseColor("lightgray"));
            return;
        }

        grid[i][j].setBackgroundColor(Color.parseColor("lightgray"));

        // Reveal all eight neighbors
        revealCell(grid, i - 1, j, visited);
        revealCell(grid, i + 1, j, visited);
        revealCell(grid, i, j - 1, visited);
        revealCell(grid, i, j + 1, visited);
        revealCell(grid, i - 1, j - 1, visited);
        revealCell(grid, i - 1, j + 1, visited);
        revealCell(grid, i + 1, j - 1, visited);
        revealCell(grid, i + 1, j + 1, visited);
    }

    //a function that count the total number of flags
    public static int countFlags(TextView[][] grid, int total) {
        int count = 0;
        for(int i = 0; i < 12; i++){
            for(int j = 0; j < 10; j++){
                if(grid[i][j].getText().toString().equals("🚩")){
                    count++;
                }
            }
        }
        return total - count;
    }

    //a function that check if the player win by checking if flag 2d array of mines
    //is the same as the mines 2d array
    public static boolean checkWin(TextView[][] grid) {
        for(int i = 0; i < MINE_COUNT; i++){
            if(grid[mines[i][0]][mines[i][1]].getText().toString().equals("🚩")){
                continue;
            }
            else{
                return false;
            }
        }
        revealWin(grid);
        return true;
    }

    //reveal if win and show flowers on the mines
    public static void revealWin(TextView[][] grid) {
        for(int i = 0; i < MINE_COUNT; i++){
            grid[mines[i][0]][mines[i][1]].setText("\uD83C\uDF38");
        }
    }

    //check if lose by checking if the cell is a mine
    public static boolean checkLose( int i, int j, TextView[][] grid) {
        if(isMine(i,j)){
            revealMines(grid);
            return true;
        }
        return false;
    }

    //reveal all the mines
    public static void revealMines(TextView[][] grid) {
        for(int i = 0; i < MINE_COUNT; i++){
            grid[mines[i][0]][mines[i][1]].setText("\uD83D\uDCA3");
        }
    }

    //check if all cells are revealed by checking if the cell is grey beside the mine
    public static boolean checkAllRevealed(TextView[][] grid) {
        int count = 0;
        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 10; j++) {
                ColorDrawable background = (ColorDrawable) grid[i][j].getBackground();
                int colorCode = background.getColor();
                if (colorCode == Color.parseColor("lightgray")) {
                    if (!isMine(i, j)) {
                        count++;
                    }
                }
            }
        }
        if(count == MINE_COUNT){
            return true;
        }
        return false;
    }

}

