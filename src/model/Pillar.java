package model;

/**
 * Created by lak1044 on 4/16/2016.
 */
public class Pillar {
    int row;
    int col;
    int maxLasers;
    int currLasers;
    String hash;

    /**
     * Construct a new Pillar object
     */
    public Pillar(int row, int col, int numLasers){
        this.row = row;
        this.col = col;
        this.maxLasers = numLasers;
        this.currLasers = 0;
        createHash();
    }

    private void createHash(){
        this.hash = "" + Integer.toString(this.row) + "" + Integer.toString(this.col);
    }

    @Override
    public String toString(){
        return Integer.toString(this.maxLasers);
    }

}
