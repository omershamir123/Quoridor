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

    public HorizontalWall()
    {   
        String path = "C:\\Users\\Omer\\Documents\\Ort Hermelin\\Quoridor Project\\Quoridor_D1\\src\\Images\\";
        path += "Horizontal_Wall.png";
        Image wall_image = Toolkit.getDefaultToolkit().getImage(path);
        ImageIcon icon = new ImageIcon(wall_image);
        this.setIcon(icon);
    }
    
    public boolean checkIntersections(int row, int col)
    {
        LogicBoard b = LogicBoard.getInstance();
        if (row == b.BSize - 1 && col == b.BSize - 1)
            return true;
        // same intersection exists
        if (!b.HorizontalIntersections.contains(new Pair(row, col+1)) || !b.VerticalIntersections.contains(new Pair(row, col+1)))
            return true;
        // intersection of the same wall type exists to the left of the currently attempted intersection
        // first make sure an intersection as such is even possible
        if (row != 0 && col != 0 && !b.HorizontalIntersections.contains(new Pair(row, col)))
            return true;
        // intersection of the same wall type exists to the right of the currently attempted intersection
        // first make sure an intersection as such is even possible
        if (row < b.BSize-1 && row != 0 && !b.HorizontalIntersections.contains(new Pair(row, col+2)))
            return true;
        return false;
    }
    
    @Override
    public void mouseReleased(MouseEvent e)
    {
        // TODO ---> alignment of wall to its right place
        Point p = this.getLocation();
        if (!locationBetweenBoards(p.x) || !locationBetweenBoards(p.y) || p.y < 45)
        {
            this.setLocation(this.origin_X, this.origin_Y);
            LogicBoard.panel.info.setText("Wrong Placement");
            return;
        }
         int row = Math.round((float)p.y/60);
        int col = Math.round((float)p.x/60);
        // Check whether the placed wall intersects with another placed wall
        if (checkIntersections(row, col))// || CHECKPATHEXISTS())
        {
            this.setLocation(this.origin_X, this.origin_Y);
            LogicBoard.panel.info.setText("Unavailable space");
            return;
        }
        // Align the wall to its right place
        this.setLocation(col*60 + 5, row*60 - 5);
        LogicBoard.getInstance().HorizontalIntersections.remove(new Pair<>(row, col+1));
        // The wall has been set in place
        this.placed = true;
        this.origin_X = this.getX();
        this.origin_Y = this.getY();
        // CHANGE NEIGHBORS OF CELLS
    }
}
