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
public class VerticalWall extends Wall
{

    public VerticalWall()
    {
        String path = "C:\\Users\\Omer\\Documents\\Ort Hermelin\\Quoridor Project\\Quoridor_D1\\src\\Images\\";
        path += "Vertical_Wall.png";
        Image wall_image = Toolkit.getDefaultToolkit().getImage(path);
        ImageIcon icon = new ImageIcon(wall_image);
        this.setIcon(icon);
    }
    
    public boolean checkIntersections(int row, int col)
    {
        LogicBoard b = LogicBoard.getInstance();
        // same intersection exists
        if (!b.VerticalIntersections.contains(new Pair(row+1, col)) || !b.HorizontalIntersections.contains(new Pair(row+1, col)))
            return true;
         // intersection of the same wall type exists above the currently attempted intersection
        // first make sure an intersection as such is even possible
        if (row != 0 && col != 0 && !b.VerticalIntersections.contains(new Pair(row, col)))
            return true;
        // intersection of the same wall type exists below the currently attempted intersection
        // first make sure an intersection as such is even possible
        if (row < b.BSize-1 && row != 0 && !b.VerticalIntersections.contains(new Pair(row+2, col)))
            return true;
        return false;
    }
    
    @Override
    public void mouseReleased(MouseEvent e)
    {
        if (this.placed)
            return;
        // Check if location is valid
        Point p = this.getLocation();
        if (!locationBetweenBoards(p.x) || !locationBetweenBoards(p.y) || p.x < 45)
        {
            this.setLocation(this.origin_X, this.origin_Y);
            return;
        }
        // Check whether the placed wall intersects with another placed wall
        int row = Math.round((float)p.y/60);
        int col = Math.round((float)p.x/60);
        if (checkIntersections(row, col))//|| CHECKPATHEXISTS())
        {
            this.setLocation(this.origin_X, this.origin_Y);
            LogicBoard.panel.info.setText("Unavailable space");
            return;
        }
        // Align the wall to its right place
        this.setLocation(col*60 -5, row*60 +5);
        LogicBoard.getInstance().VerticalIntersections.remove(new Pair<>(row+1, col));
        // The wall has been set in place
        this.placed = true;
        this.origin_X = this.getX();
        this.origin_Y = this.getY();
        // CHANGE NEIGHBORS OF CELLS
    }
}
