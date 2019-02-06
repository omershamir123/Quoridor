/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
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
    
    @Override
    public void mouseReleased(MouseEvent e)
    {
        // TODO ---> alignment of wall to its right place
        Point p = this.getLocation();
        if (!locationBetweenBoards(p.x) || !locationBetweenBoards(p.y))
        {
            this.setLocation(this.origin_X, this.origin_Y);
            return;
        }
        System.out.println("Good");
            
        //System.out.println((X+x1)+" and "+(Y+y1));
        //this.setLocation(X+x1, Y+y1);
    }
}
