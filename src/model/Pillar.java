package model;

/**
 * Created by lak1044 on 4/16/2016.
 */
public class Pillar {
    //Row position
    int row;
    //Column position
    int col;
    //Max lasers allowed to be adjacent to pillar
    int maxLasers;
    //Current number of lasers adjacent to pillar
    int currLasers;
    //Current number of empty spaces adjacent to pillar
    int currEmpty;

    /**
     * Construct a new Pillar object
     */
    public Pillar(int row, int col, int numLasers){
        this.row = row;
        this.col = col;
        this.maxLasers = numLasers;
        this.currLasers = 0;
        //by default this is 4 even though it may not be 4 initially. This will be fixed
        //when updatePillars is called in the backtracker before it is required
        this.currEmpty = 4;
    }

    public Pillar(Pillar other){
        this.row = other.row;
        this.col = other.col;
        this.maxLasers = other.maxLasers;
        this.currLasers = other.currLasers;
        this.currEmpty = other.currEmpty;
    }

    //Returns row position
    public int getRow(){ return this.row; }

    //Returns column position
    public int getCol(){ return this.col; }

    //Returns current number of adjacent lasers
    public int getCurrLasers(){ return this.currLasers; }

    //Sets current number of adjacent lasers
    public void setCurrLasers(int currLasers){ this.currLasers = currLasers; }

    //Gets max possible adjacent lasers
    public int getMaxLasers(){ return this.maxLasers; }

    //Returns the current empty adjacent spaces
    public int getCurrEmpty(){ return this.currEmpty; }

    //Sets the current empty adjacent spaces
    public void setCurrEmpty(int currEmpty){ this.currEmpty = currEmpty; }
}
