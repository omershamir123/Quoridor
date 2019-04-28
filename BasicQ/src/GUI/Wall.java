/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import Logic.LogicBoard;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JButton;

/**
 *
 * @author Omer
 */
public abstract class Wall extends JButton implements MouseListener, MouseMotionListener
{
    private int x1, y1;                     // original internal coordinates of the dragged wall, within the wall
    public int origin_X, origin_Y;          // original coordinates of the wall in the panel
    protected boolean placed;                 // has the wall been placed already and set in place?
    protected LogicBoard board;
    
    
    protected Wall(LogicBoard board)
    {
        this.placed = false;
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.board = board;
    }
    
    public void SetCoordinates(int x, int y)
    {
        setLocation(x, y);
        origin_X = x;
        origin_Y = y;
    }
    
    public abstract boolean isLocationValid(Point p);
    public abstract boolean checkIntersections(int row, int col);
    public boolean isPlayerBlocked(int row, int col)
    {
        boolean blocked = false;
        deleteNeighbors(row, col);
        for (Logic.Player player : board.players)
        {
            if (Logic.AI.BFS(player.place.getMoveOptions(), player, null) == null)
                blocked = true;
        }
        resetNeighbors(row, col);
        return blocked;
    }
    public abstract void placeWall(int row, int col);
    public abstract void resetNeighbors(int row, int col);
    public abstract void deleteNeighbors(int row, int col);
    
    /**
     * Returns whether a current coordinate is between two cells or not
     * The program lets the user a 10 pixel buffer
     */
    public boolean locationBetweenBoards(int coordinate)
    {
        // Is the current coordinate between two cells
        if (coordinate % Cell.CELL_WIDTH < 45 && coordinate % Cell.CELL_WIDTH > 15)
            return false;
        if (coordinate < 0)
            return false;
        // The coordinate is not in the board
        if (coordinate > (board.BSize - 1)*Cell.CELL_WIDTH)
            return false;
        return true;
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        x1 = e.getX();
        y1 = e.getY();
    }
    
    @Override
    public void mouseReleased(MouseEvent e) { }

    @Override
    public void mouseEntered(MouseEvent e) { }

    @Override
    public void mouseExited(MouseEvent e)  { }

    @Override
    public void mouseDragged(MouseEvent e)
    {        
        if (this.placed || board.gameOver)
            return;
        Point   POld =  getLocation();
        int XNew , YNew;
        if (e.getX() < x1)
            XNew = POld.x - (x1 - e.getX());
        else
            XNew = POld.x + (e.getX() - x1);

        if (e.getY() < y1)
            YNew = POld.y - (y1 - e.getY());
        else
            YNew = POld.y + (e.getY() - y1);

        setLocation(XNew, YNew);
    }

    @Override
    public void mouseMoved(MouseEvent e)   {  }
    
}
