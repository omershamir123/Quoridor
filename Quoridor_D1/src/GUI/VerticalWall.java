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
    
    
    
    @Override
    public void mouseReleased(MouseEvent e)
    {
        if (this.isEnabled() == false)
            return;
        // Check if location is valid
        Point p = this.getLocation();
        if (!locationBetweenBoards(p.x) || !locationBetweenBoards(p.y) || p.x < 45)
        {
            this.setLocation(this.origin_X, this.origin_Y);
            return;
        }
        // Check whether the placed wall intersects with another placed wall
        if (false )// || CHECKINTERSECTIONS() || CHECKPATHEXISTS())
        {
            this.setLocation(this.origin_X, this.origin_Y);
            LogicBoard.panel.info.setText("Unavailable space");
            return;
        }
        // Align the wall to its right place
        int row = Math.round((float)p.y/60);
        int col = Math.round((float)p.x/60);
        this.setLocation(col*60 -5, row*60 +5);
        // The wall has been set in place
        this.placed = true;
        this.setEnabled(false);
        // CHANGE NEIGHBORS OF CELLS
    }
}
