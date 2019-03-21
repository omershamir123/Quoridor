/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Logic;

import GUI.Cell;
import javax.swing.JLabel;

/**
 *
 * @author Omer
 */
public class Player
{
    public Cell place;
    public int playerNo;
    protected int endingRow;
    protected int endingCol;
    protected int wallsLeft;
    protected JLabel wallsInfo;
    
    /**
     * Constructor of the player
     * @param playerNo - player number used for the icons of the players
     * @param endingRow - player's ending row (if there are 4 players, it might equal -1)
     * @param endingCol - player's ending col (if there are 2 players, it will equal -1)
     * @param MaxWalls - the maximum number of walls for the starting player
     * 
     */
    public Player(int playerNo, int endingRow, int endingCol, int MaxWalls)
    {
        this.playerNo = playerNo;
        this.endingRow = endingRow;
        this.endingCol = endingCol;
        this.wallsLeft = MaxWalls;
        this.wallsInfo = null;
    }
    
    // Sets the place of the current player to a different cell
    // Sets its new Icon as well
    public void setPlace(Cell place)
    {
        this.place = place;
        place.setCellIcon(this.playerNo);
    }
    
    // Getter of the endingRow
    public int getEndingRow()
    {
        return endingRow;
    }
    
    // Setter of the endingCol
    public int getEndingCol()
    {
        return endingCol;
    }

    public int getWallsLeft()
    {
        return wallsLeft;
    }

    public void setWallsLeft(int wallsLeft)
    {
        this.wallsLeft = wallsLeft;
        this.wallsInfo.setText("Player "+playerNo+" walls Left: "+getWallsLeft());
    }
    
    public JLabel getWallsInfo()
    {
        return wallsInfo;
    }

    public void setWallsInfo(JLabel wallsInfo)
    {
        this.wallsInfo = wallsInfo;
    }
    
}
