/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import Logic.LogicBoard;
import java.awt.Font;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import javafx.util.Pair;
import javax.swing.ImageIcon;

/**
 *
 * @author Omer
 */
public class VerticalWall extends Wall
{
    
    public VerticalWall(LogicBoard board)
    {
        super(board);
        String path = "Images/Vertical_Wall.png";
        Image wall_image = Toolkit.getDefaultToolkit().getImage(path);
        ImageIcon icon = new ImageIcon(wall_image);
        this.setIcon(icon);
    }
    
    public boolean checkIntersections(int row, int col)
    {
        // same intersection exists
        if (!board.VerticalIntersections.contains(new Pair(row+1, col)) || !board.HorizontalIntersections.contains(new Pair(row+1, col)))
            return true;
        // intersection of the same wall type exists above the currently attempted intersection
        // first make sure an intersection as such is even possible
        if (row != 0 && col != 0 && !board.VerticalIntersections.contains(new Pair(row, col)))
            return true;
        // intersection of the same wall type exists below the currently attempted intersection
        // first make sure an intersection as such is even possible
        if (row < board.BSize-1 && row != 0 && !board.VerticalIntersections.contains(new Pair(row+2, col)))
            return true;
        return false;
    }
    
    /**
     * This function updates the cells' neighbors after a wall has been placed
     * The function receives the row and the column of the upper left corner of the wall
     */
    private void updateNeighbors(int row, int col)
    {
        board.cells[row][col].neighbors[Cell.Paths.LEFT.ordinal()] = null;
        board.cells[row+1][col].neighbors[Cell.Paths.LEFT.ordinal()] = null;
        board.cells[row][col-1].neighbors[Cell.Paths.RIGHT.ordinal()] = null;
        board.cells[row+1][col-1].neighbors[Cell.Paths.RIGHT.ordinal()] = null;
    }
    
    @Override
    public void mouseReleased(MouseEvent e)
    {
        if (this.placed)
            return;
        if (this.board.players[this.board.currentPlayer].getWallsLeft() == 0)
        {
            this.setLocation(this.origin_X, this.origin_Y);
            board.panel.info.setText("OUT OF WALLS");
            return;
        }
        // Check if location is valid
        Point p = this.getLocation();
        if (!locationBetweenBoards(p.x) || !locationBetweenBoards(p.y) || p.x < 45)
        {
            this.setLocation(this.origin_X, this.origin_Y);
            board.panel.info.setText("Wrong Placement");
            return;
        }
        // Check whether the placed wall intersects with another placed wall
        int row = Math.round((float)p.y/60);
        int col = Math.round((float)p.x/60);
        if (checkIntersections(row, col))//|| CHECKPATHEXISTS())
        {
            this.setLocation(this.origin_X, this.origin_Y);
            board.panel.info.setText("Unavailable Space");
            return;
        }
        // Align the wall to its right place
        this.setLocation(col*60 -5, row*60 +5);
        board.VerticalIntersections.remove(new Pair<>(row+1, col));
        // The wall has been set in place
        this.placed = true;
        this.origin_X = this.getX();
        this.origin_Y = this.getY();
        // Make the neighbors of the current cell unclickable first, and then 
        // update the neighbors
        board.players[board.currentPlayer].place.SetOptionsForCurrentPlayer(false);
        updateNeighbors(row, col);
        board.endTurn(board.players[board.currentPlayer].place, true);
    }
}
