/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import javax.swing.JButton;

/**
 *
 * @author Omer
 */
public class Cell extends JButton implements MouseListener
{

    // The row of the cell
    private int row;
    // The column of the cell
    private int col;
    // An array of the neighboring cells - will consist of four cells maximum.
    // The order of the Cells will be: [Top, Bottom, Left, Right].
    private Cell[] neighbors;
    
    // Creates a new cell in a specific row and col of the board.
    // Sets its size and its coordinates based on the row and col given.
    public Cell(int row, int col)
    {
        this.row = row;
        this.col = col;
        this.neighbors = new Cell[4];
        this.setBounds((col*60) + 5, (row*60)+5, 50, 50);
        this.setBackground(Color.BLACK);
        this.addMouseListener(this);
        //this.setText(row+","+col);
    }
    
    // This function adds a neighbor to the current Cell. 
    // In Order to save runTime, the method will only receive the directions for
    // right and bottom from the Board panel.
    // If the Cell Receives a right neighbor, that means that the "other" cell 
    // has a left neighbor that is "this" cell.
    // This is because the board is represented as an undirected graph
    public void addNeighbor(Cell other, Enums.Paths p)
    {
        this.neighbors[p.ordinal()] = other;
        //System.out.println(p.name() + ", "+ p.ordinal()+ ", "+p.values()[p.ordinal()]);
        if (p.name().equals("BOTTOM"))
            other.addNeighbor(this, Enums.Paths.TOP);
        else if(p.name().equals("RIGHT"))
            other.addNeighbor(this, Enums.Paths.LEFT);
    }
    @Override
    public void mouseClicked(MouseEvent e)
    {
        //System.out.println(row+", "+ col+" clicked");
    }

    @Override
    public void mousePressed(MouseEvent e)
    {
        //System.out.println("Button pressed");
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
        //System.out.println("Mouse released");
    }
    @Override
    public void mouseEntered(MouseEvent e)
    {
    }

    @Override
    public void mouseExited(MouseEvent e)
    {
    }
    
}
