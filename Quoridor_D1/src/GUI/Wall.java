/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import Logic.LogicBoard;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;

/**
 *
 * @author Omer
 */
public class Wall extends JButton implements MouseListener, MouseMotionListener
{
    private int x1, y1;                     // original internal coordinates of the dragged wall, within the wall
    public int origin_X, origin_Y;          // original coordinates of the wall in the panel
    protected boolean placed;                 // has the wall been placed already and set in place?
    //private Cell
    
    
    protected Wall()
    {
        this.placed = false;
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
    }
    
    public void SetCoordinates(int x, int y)
    {
        setLocation(x, y);
        origin_X = x;
        origin_Y = y;
    }
    
    // Returns whether a current coordinate is between two cells or not
    // I have decided to include a 5 pixel buffer into the cells
    public boolean locationBetweenBoards(int coordinate)
    {
        // Is the current coordinate between two cells
        if (coordinate % 60 < 45 && coordinate % 60 > 15)
            return false;
        if (coordinate < 0)
            return false;
        // The coordinate is not in the board
        if (coordinate > (LogicBoard.getInstance().BSize - 1)*60 + 5)
            return false;
        return true;
    }
    
    @Override
    public void mouseClicked(MouseEvent e) { }

    @Override
    public void mousePressed(MouseEvent e) {
        x1 = e.getX();
        y1 = e.getY();
    }
    
    @Override
    public void mouseReleased(MouseEvent e)
    {
        // TODO ---> alignment of wall to its right place
        
            
        //System.out.println((X+x1)+" and "+(Y+y1));
        //this.setLocation(X+x1, Y+y1);
    }

    @Override
    public void mouseEntered(MouseEvent e) { }

    @Override
    public void mouseExited(MouseEvent e)  { }

    @Override
    public void mouseDragged(MouseEvent e)
    {        
        if (this.isEnabled() == false)
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
