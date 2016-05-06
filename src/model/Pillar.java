package model;

/**
 * Created by lak1044 on 4/16/2016.
 */
public class Pillar {
    int row;
    int col;
    int maxLasers;
    int currLasers;

    /**
     * Construct a new Pillar object
     */
    public Pillar(int row, int col, int numLasers){
        this.row = row;
        this.col = col;
        this.maxLasers = numLasers;
        this.currLasers = 0;
    }

    public int getRow(){ return this.row; }

    public int getCol(){ return this.col; }

    public int getCurrLasers(){ return this.currLasers; }

    public void setCurrLasers(int currLasers){ this.currLasers = currLasers; }

    public int getMaxLasers(){ return this.maxLasers; }
}
