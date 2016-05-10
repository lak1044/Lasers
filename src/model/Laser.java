package model;

/**
 * Created by lak1044 on 4/12/2016.
 */
public class Laser {
    int row;
    int col;
    boolean valid;

    /** Construct a new laser object */
    public Laser(int row, int col){
        this.row = row;
        this.col = col;
        this.valid = true;
    }

    /** Copy constructor from another Laser object */
    public Laser(Laser other){
        this.row = other.row;
        this.col = other.col;
        this.valid = other.valid;
    }

    /** Returns the row state */
    public int getRow() { return this.row; }

    /** Returns the col state */
    public int getCol() { return this.col; }

    /** Returns the valid state */
    public boolean isValid(){ return this.valid; }

    /** Sets the valid state */
    public void setValid(boolean valid){ this.valid = valid; }
}
