package model;

/**
 * Created by lak1044 on 4/12/2016.
 */
public class Laser {
    int row;
    int col;
    boolean valid;

    /**
     * Constuct a new laser object
     */
    public Laser(int row, int col){
        this.row = row;
        this.col = col;
        this.valid = true;
    }

    public boolean isValid(){ return this.valid; }

    public void setValid(boolean valid){ this.valid = valid; }
}
