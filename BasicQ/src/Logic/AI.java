/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Logic;

import GUI.*;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import javafx.util.Pair;

/**
 *
 * @author Omer
 */
public class AI extends Player
{

    private LogicBoard board;

    public AI(int playerNo, int endingRow, int endingCol, int MaxWalls, LogicBoard board)
    {
        super(playerNo, endingRow, endingCol, MaxWalls);
        this.board = board;
    }

    @SuppressWarnings("empty-statement")
    public void computerMove()
    {
        ArrayList<Cell[]> moveOptions = this.place.getMoveOptions();
        ArrayList<Cell> shortestPath = BFS(moveOptions, this);
        ArrayList<Cell[]> opMoveOptions = this.board.players[(this.board.currentPlayer + 1) % 2].place.getMoveOptions();
        ArrayList<Cell> opponentPath = BFS(opMoveOptions, this.board.players[(this.board.currentPlayer + 1) % 2]);
        // If the opponent's shortest path is shorter than the computer by more than one
        // Or the path length of the opponent is only one square, place a wall to block the opponent
        if (shortestPath.size() - 1 > opponentPath.size() || (opponentPath.size() < 3 && shortestPath.size() > opponentPath.size()))
        {
            if (this.wallsLeft == 0 || !blockOpponent(opponentPath, opMoveOptions))
            {
                board.endTurn(shortestPath.get(1), false);
            }
        } else
        {
            board.endTurn(shortestPath.get(1), false);
        }
    }

    // static so that all of the classes could access the method (to chek if a wall placement is possible)
    public static ArrayList<Cell> BFS(ArrayList<Cell[]> moveOptions, Player player)
    {
        boolean pathFound = false;
        Queue<Cell> queue = new LinkedList<>();
        HashSet<Cell> visited = new HashSet<>();
        HashMap<Cell, Cell> parentNodes = new HashMap<>(); // Key - node, value - parent
        Cell[] neighbors;
        Cell nextNode = null;

        queue.add(player.place);
        parentNodes.put(player.place, null);

        // this loop will stop when:
        // we have gone over all of the reachable nodes and did not reach the end line
        // we have reached a possible path and its start will be in the variable c
        while (!queue.isEmpty() && !pathFound)
        {
            nextNode = queue.remove();
            if (nextNode.getRow() == player.getEndingRow() || nextNode.getCol() == player.getEndingCol())
            {
                pathFound = true;
            }
            visited.add(nextNode);
            if (nextNode.equals(player.place))
            {
                neighbors = ConvertOptionsToArray(moveOptions);
            } else
            {
                neighbors = nextNode.neighbors;
            }
            for (Cell neighbor : neighbors)
            {
                if (!visited.contains(neighbor) && neighbor != null)
                {
                    parentNodes.put(neighbor, nextNode);
                    queue.add(neighbor);
                }
            }
        }
        if (!pathFound)
        {
            return null;
        } else
        {
            ArrayList<Cell> shortestPath = new ArrayList<>();
            while (nextNode != null)
            {
                shortestPath.add(nextNode);
                nextNode = parentNodes.get(nextNode);
            }
            Collections.reverse(shortestPath);
            return shortestPath;
        }
    }

    public static Cell[] ConvertOptionsToArray(ArrayList<Cell[]> moveOptions)
    {
        ArrayList<Cell> neighbors = new ArrayList<>();
        for (Iterator<Cell[]> iterator = moveOptions.iterator(); iterator.hasNext();)
        {
            Cell[] next = iterator.next();
            for (int i = 0; i < next.length; i++)
            {
                if (next[i] != null)
                {
                    neighbors.add(next[i]);
                }
            }
        }
        return (Cell[]) neighbors.toArray(new Cell[4]);
    }

    /**
     * This function determines the row and col of the wall based on the
     * neighborIndex. It creates the instance of the appropriate wall
     *
     * @param row - the row of the origin of the move
     * @param col - the column of the origin of the move
     * @param neighborIndex - the index in the neighbor array of the relation to
     * the next cell in the path
     * @return a Pair of the new coordinates of the top left corner of the wall,
     * as well as the appropriate wall
     */
    private Pair<Wall, Pair<Integer, Integer>> determineRowAndColOfWall(int row, int col, int neighborIndex)
    {
        Pair<Integer, Integer> coordinate;
        Wall wall;
        switch (neighborIndex)
        {
            case 0:
                wall = new HorizontalWall(board);
                coordinate = new Pair(row, col);
                break;
            case 1:
                wall = new HorizontalWall(board);
                coordinate = new Pair(row - 1, col);
                break;
            case 2:
                wall = new VerticalWall(board);
                coordinate = new Pair(row, col);
                break;
            case 3:
                wall = new VerticalWall(board);
                coordinate = new Pair(row, col + 1);
                break;
            default:
                coordinate = new Pair(-1, -1);
                wall = null;
                break;
        }
        return new Pair(wall, coordinate);
    }

    // This function determines the direction in which the path o the opponent goes.
    private int determineDirection(int pathIndex, ArrayList<Cell> opponentPath)
    {
        Cell origin = opponentPath.get(pathIndex);
        Cell destination = opponentPath.get(pathIndex+1);
        for (int i = 0; i < origin.neighbors.length; i++)
        {
            if (origin.neighbors[i] != null && origin.neighbors[i].equals(destination))
            {
                return i;
            }
        }
        return -1;
    }

    private boolean blockOpponent(ArrayList<Cell> opponentPath, ArrayList<Cell[]> opMoveOptions)
    {
        int row = opponentPath.get(0).getRow();
        int col = opponentPath.get(0).getCol();
        // neighborsIndex - the direction of the shortest path
        // pathIndex - the cell in the path we are currently trying to block
        int pathIndex = 0, neighborIndex = 0, i = -1;
        Wall wall = null;
        boolean triedBothOptions = false;
        for (Iterator<Cell[]> iterator = opMoveOptions.iterator(); iterator.hasNext();)
        {
            i++;
            Cell[] next = iterator.next();
            if (next[0] != null)
            {
                for (Cell cell : next)
                {
                    if (cell.equals(opponentPath.get(pathIndex + 1)))
                    {
                        neighborIndex = i;
                    }
                }
            }
        }
        Pair<Wall, Pair<Integer, Integer>> coordinate = determineRowAndColOfWall(row, col, neighborIndex);
        wall = coordinate.getKey();
        row = coordinate.getValue().getKey();
        col = coordinate.getValue().getValue();
        Point p = new Point(row * 60, col * 60);
        while ((!wall.isLocationValid(p) || wall.checkIntersections(row, col) || wall.isPlayerBlocked(row, col)) && pathIndex < opponentPath.size() - 1)
        {
            // Each wall has two different positions possible to block a square.
            // If only one has been tried, try the other option
            if (!triedBothOptions)
            {
                if (wall instanceof VerticalWall)
                {
                    row--;
                } else
                {
                    col--;
                }
                p = new Point(row * 60, col * 60);
                triedBothOptions = true;
            } else
            {
                pathIndex++;
                if (pathIndex < opponentPath.size() - 1)
                {
                    neighborIndex = determineDirection(pathIndex, opponentPath);
                    row = opponentPath.get(pathIndex).getRow();
                    col = opponentPath.get(pathIndex).getCol();
                    coordinate = determineRowAndColOfWall(row, col, neighborIndex);
                    wall = coordinate.getKey();
                    row = coordinate.getValue().getKey();
                    col = coordinate.getValue().getValue();
                    p = new Point(row * 60, col * 60);
                    triedBothOptions = false;
                }
            }
        }
        // By this point either we have found a wall to build or there is no move possible
        if (!wall.isLocationValid(p) || wall.checkIntersections(row, col) || wall.isPlayerBlocked(row, col))
        {
            return false;
        }
        board.panel.add(wall);
        wall.placeWall(row, col);
        return true;
    }
}
