/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import Logic.LogicBoard;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JLabel;

/**
 *
 * @author Omer
 */
public class BoardPanel extends javax.swing.JPanel
{

    public LogicBoard board;
    public JLabel info = new JLabel();

    /**
     * Creates new form NewJPanel
     */
    public BoardPanel()
    {
        board = new LogicBoard(this);
        this.setBackground(new Color(128, 49, 49));
        JLabel title = new JLabel("Welcome To Quoridor!!!");
        title.setSize(500,30);
        title.setFont(new Font("ComicSans", 1, 14));
        title.setLocation(board.BSize * 60 + 15, 30);
        this.info.setText("Player 1 Begin");
        this.info.setSize(500, 30);
        this.info.setFont(new Font("Comic Sans", 1, 14));
        this.info.setLocation(board.BSize * 60 + 15, 150);
        this.add(title);
        this.add(this.info);
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}