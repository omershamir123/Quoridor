/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import Logic.LogicBoard;
import Logic.Player;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;

/**
 *
 * @author Omer
 */
public class Cell extends JButton
{

    public enum Paths
    {
        TOP, BOTTOM, LEFT, RIGHT
    };

    // // The gameboard itself
    public LogicBoard board;
    public Cell[] neighbors = new Cell[4];
    private int row;
    private int col;

    /**
     * Constructor of the cell, sets the
     *
     * @param i - the row of the cell
     * @param j - the column of the cell
     * @param board - the instance of the logical board
     */
    public Cell(int i, int j, LogicBoard board)
    {
        this.row = i;
        this.col = j;
        this.board = board;
        this.setBounds(j * 60 + 5, i * 60 + 5, 50, 50);
        this.setBackground(Color.black);

    }

    public int getRow()
    {
        return row;
    }

    public int getCol()
    {
        return col;
    }
    
    // Sets the cell icon based on the player number
    public void setCellIcon(int playerNo)
    {
        this.setIcon(new ImageIcon("Images/pawn" + playerNo + ".png"));
    }

    // Sets whether or not a button is clickable based on the variable <set>
    public void setClickable(boolean set)
    {
        if (set)
        {
            this.addActionListener(new ActionListener()
            {
                @Override
                // Once clicked, the cell should call the end Turn function in 
                // The logicBoard
                public void actionPerformed(ActionEvent e)
                {
                    Cell cell = (Cell) e.getSource();
                    board.endTurn(cell, false);
                }
            });
        } else
        {
            for (ActionListener al : this.getActionListeners())
            {
                this.removeActionListener(al);
            }
        }
    }

    /**
     * This function adds a neighbor to the current Cell. In Order to save
     * runTime, the method will only receive the directions for right and bottom
     * from the Board panel. If the Cell Receives a right neighbor, that means
     * that the "other" cell has a left neighbor that is "this" cell. This is
     * because the board is represented as an undirected graph
     */
    public void addNeighbor(Cell other, LogicBoard.Paths p)
    {
        this.neighbors[p.ordinal()] = other;
        if (p.name().equals("BOTTOM"))
        {
            other.addNeighbor(this, LogicBoard.Paths.TOP);
        } else if (p.name().equals("RIGHT"))
        {
            other.addNeighbor(this, LogicBoard.Paths.LEFT);
        }
    }
    
     // This function sets the neighbors of the current player clickable or not
    // Depending on whether it is the beginning of the turn or the end of it
    public void SetOptionsForCurrentPlayer(boolean IsBegin)
    {
        for (int i = 0; i < 4; i++)
        {
            boolean noNeighbor = true;
            // go over all of the players in the game
            for (Player player : this.board.players)
            {
                // if the two players are neighbors
                if (player.place.equals(this.neighbors[i]))
                {
                    noNeighbor = false;
                    // Check whether the current player can jump over the other
                    if (player.place.neighbors[i] != null)
                    {
                        player.place.neighbors[i].setClickable(IsBegin);
                        // If not, go for the other two directions
                    } else
                    {
                        // If the other player is above the current player, and there is
                        // a wall block above the other player, make the right and left
                        // clickable. 
                        // Neighbors {TOP, BOTTOM, LEFT, RIGHT}
                        // If i = 0/1 -> make 2,3 clickable
                        // If i = 2/3 -> make 0,1 clickable
                        int indexToSet = ((i+2)-(i%2))%4;
                        if (player.place.neighbors[indexToSet] != null)
                            player.place.neighbors[indexToSet].setClickable(IsBegin);
                        if (player.place.neighbors[indexToSet+1] != null)
                            player.place.neighbors[indexToSet+1].setClickable(IsBegin);
                        
                    }
                }
            }
            if (this.neighbors[i] != null && noNeighbor)
            {
                this.neighbors[i].setClickable(IsBegin);
            }
        }
    }
}
