package model;

import java.util.IntSummaryStatistics;

/**
 * Created by lak1044 on 4/12/2016.
 */
public class Laser {
    int row;
    int col;
    String hash;
    boolean isValid;

    /**
     * Constuct a new laser object
     */
    public Laser(int row, int col){
        this.row = row;
        this.col = col;
        this.isValid = true;
        createHash();
    }

    private void createHash(){
        this.hash = "" + Integer.toString(this.row) + "" + Integer.toString(this.col);
    }
}
