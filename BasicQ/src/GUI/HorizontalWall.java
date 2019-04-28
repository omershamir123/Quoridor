/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import Logic.LogicBoard;
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
public class HorizontalWall extends Wall
{

    public HorizontalWall(LogicBoard board)
    {   
        super(board);
        String path = "Images/Horizontal_Wall.png";
        Image wall_image = Toolkit.getDefaultToolkit().getImage(path);
        ImageIcon icon = new ImageIcon(wall_image);
        this.setIcon(icon);
        this.setSize(110, 10);
    }
    
    @Override
    public boolean isLocationValid(Point p)
    {
        return locationBetweenBoards(p.x) && locationBetweenBoards(p.y) && p.y >= 45 && p.x < (board.BSize-1)*Cell.CELL_WIDTH;
    }
    
    /**
     * This function checks whether a wall placement is possible based on other walls on the board
     * @param row - upper left row
     * @param col - upper left col
     * @return 
     */
    @Override
    public boolean checkIntersections(int row, int col)
    {
        if (row == board.BSize - 1 && col == board.BSize - 1)
            return true;
        // same intersection exists
        if (board.HorizontalIntersections.contains(new Pair(row, col+1)) || board.VerticalIntersections.contains(new Pair(row, col+1)))
            return true;
        // intersection of the same wall type exists to the left of the currently attempted intersection
        // first make sure an intersection as such is even possible
        if (row != 0 && col != 0 && board.HorizontalIntersections.contains(new Pair(row, col)))
            return true;
        // intersection of the same wall type exists to the right of the currently attempted intersection
        // first make sure an intersection as such is even possible
        if (col < board.BSize-2 && board.HorizontalIntersections.contains(new Pair(row, col+2)))
            return true;
        return false;
    }
    
    /**
     * This function updates the cells' neighbors after a wall has been placed and there is not available pah for a player
    * The function receives the row and the column of the upper left corner of the wall
    */
    @Override
    public void resetNeighbors(int row, int col)
    {
        board.cells[row][col].neighbors[Cell.Paths.TOP.ordinal()] = board.cells[row-1][col];
        board.cells[row][col+1].neighbors[Cell.Paths.TOP.ordinal()] = board.cells[row-1][col+1];
        board.cells[row-1][col].neighbors[Cell.Paths.BOTTOM.ordinal()] = board.cells[row][col];
        board.cells[row-1][col+1].neighbors[Cell.Paths.BOTTOM.ordinal()] = board.cells[row][col+1];
    }
    
    /**
     * This function updates the cells' neighbors after a wall has been placed
    * The function receives the row and the column of the upper left corner of the wall
    */
    @Override
    public void deleteNeighbors(int row, int col)
    {
        try 
        {
        board.cells[row][col].neighbors[Cell.Paths.TOP.ordinal()] = null;
        board.cells[row][col+1].neighbors[Cell.Paths.TOP.ordinal()] = null;
        board.cells[row-1][col].neighbors[Cell.Paths.BOTTOM.ordinal()] = null;
        board.cells[row-1][col+1].neighbors[Cell.Paths.BOTTOM.ordinal()] = null;
        }
        catch (Exception e)
        {
            System.out.println(row + " " + col);
        }
    }
    
    @Override
    /**
     * This function places the walls in the right place, if it is possible
     * Checks if the dragged location is valid
     * Checks if the placement is not taken
     * Checks whether a path to the end exists or not
     * Checks if the current player has enough walls
     */
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
        Point p = this.getLocation();
        if (!isLocationValid(p))
        {
            this.setLocation(this.origin_X, this.origin_Y);
            board.panel.info.setText("Wrong Placement");
            return;
        }
         int row = Math.round((float)p.y/Cell.CELL_WIDTH);
        int col = Math.round((float)p.x/Cell.CELL_WIDTH);
        // Check whether the placed wall intersects with another placed wall
        if (checkIntersections(row, col) || isPlayerBlocked(row, col))
        {
            this.setLocation(this.origin_X, this.origin_Y);
            board.panel.info.setText("Unavailable space");
            return;
        }
        // The wall has been set in place
        this.placed = true;
        this.origin_X = this.getX();
        this.origin_Y = this.getY();
        placeWall(row, col);
    }

    public void placeWall(int row, int col)
    {
        // Align the wall to its right place
        this.setLocation(col*Cell.CELL_WIDTH + 5, row*Cell.CELL_WIDTH - 5);
        board.HorizontalIntersections.add(new Pair<>(row, col+1));
        // Make the neighbors of the current cell unclickable first, and then 
        // update the neighbors
        board.players[board.currentPlayer].place.SetOptionsForCurrentPlayer(false);
        deleteNeighbors(row, col);
        board.endTurn(board.players[board.currentPlayer].place, true);
    }
}
