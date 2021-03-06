package com.emilytrabert.minesweeper;

import static com.emilytrabert.minesweeper.Difficulty.EASY;
import static com.emilytrabert.minesweeper.Difficulty.HARD;
import static com.emilytrabert.minesweeper.Difficulty.MEDIUM;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Random;
import java.util.Stack;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;

public class Minesweeper {

    public static void main(String[] args) {
        new Minesweeper();
    }

    Space[][] minematrix;

    BoardComponent boardcomponent;
    ActionListener actionlistener;

    boolean gameover, won;
    int fieldsize, minenumber, windowsize, tilesize, minesleft;

    Difficulty difficulty;
    JFrame frame;
    JMenuBar menubar;
    JMenuItem newgame;
    JRadioButtonMenuItem easy, medium, hard;

    public Minesweeper() {
        difficulty = EASY;

        menubar = new JMenuBar();
        frame = new JFrame("Minesweeper");
        actionlistener = new MenuActionListener();

        newgame();

        createmenubar();
        frame.setJMenuBar(menubar);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        boardcomponent = new BoardComponent();
        BoardMouseListener mouselistener = new BoardMouseListener();
        boardcomponent.addMouseListener(mouselistener);
        frame.add(boardcomponent);
        frame.setVisible(true);
    }

    void createmenubar() {
        JMenu game;

        game = new JMenu("Game");
        game.setMnemonic(KeyEvent.VK_G);
        menubar.add(game);

        newgame = new JMenuItem("New Game", KeyEvent.VK_N);
        newgame.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.ALT_MASK));
        newgame.addActionListener(actionlistener);
        game.add(newgame);

        game.addSeparator();

        ButtonGroup difficulty = new ButtonGroup();

        easy = new JRadioButtonMenuItem("Easy");
        easy.setSelected(true);
        easy.setMnemonic(KeyEvent.VK_E);
        difficulty.add(easy);
        easy.addActionListener(actionlistener);
        game.add(easy);

        medium = new JRadioButtonMenuItem("Medium");
        medium.setMnemonic(KeyEvent.VK_M);
        difficulty.add(medium);
        medium.addActionListener(actionlistener);
        game.add(medium);

        hard = new JRadioButtonMenuItem("Hard");
        hard.setMnemonic(KeyEvent.VK_H);
        difficulty.add(hard);
        hard.addActionListener(actionlistener);
        game.add(hard);
    }

    void newgame() {
        tilesize = 16;
        fieldsize = difficulty.getFieldSize();
        minenumber = difficulty.getMineCount();

        gameover = false;
        won = false;
        minesleft = minenumber;
        windowsize = fieldsize * tilesize;
        frame.setSize(windowsize, windowsize + 44);
        createfield();

        printmines();
    }

    void createfield() {
        char[] minearray = new char[fieldsize * fieldsize];

        for (int i = 0; i < (fieldsize * fieldsize); i++) {
            minearray[i] = 0;
        }

        for (int i = 0; i < minenumber; i++) {
            minearray[i] = '*';
        }

        minearray = shuffleminearray(minearray);

        minematrix = new Space[fieldsize][fieldsize];

        for (int y = 0; y < fieldsize; y++) {
            for (int x = 0; x < fieldsize; x++) {
                if (minearray[y * fieldsize + x] == '*') {
                    minematrix[y][x] = new Space(true, 0, false, false, y, x, false);
                } else {
                    minematrix[y][x] = new Space(false, 0, false, false, y, x, false);
                }
            }
        }

        addwarnings();
    }

    char[] shuffleminearray(char[] minearray) {
        int r;
        char c;
        int n = minearray.length;
        Random rand = new Random();

        for (int i = 0; i < n; i++) {
            r = rand.nextInt(n - i) + i;
            c = minearray[i];
            minearray[i] = minearray[r];
            minearray[r] = c;
        }

        return minearray;
    }

    void addwarnings() {
        for (int y = 0; y < fieldsize; y++) {
            for (int x = 0; x < fieldsize; x++) {
                if (minematrix[y][x].isMine()) {
                    if (y - 1 >= 0) {
                        if (x - 1 >= 0) {
                            if (!minematrix[y - 1][x - 1].isMine()) {
                                minematrix[y - 1][x - 1].incrementNearby();
                            }
                        }
                        if (x + 1 < fieldsize) {
                            if (!minematrix[y - 1][x + 1].isMine()) {
                                minematrix[y - 1][x + 1].incrementNearby();
                            }
                        }
                        if (!minematrix[y - 1][x].isMine()) {
                            minematrix[y - 1][x].incrementNearby();
                        }
                    }
                    if (y + 1 < fieldsize) {
                        if (x - 1 >= 0) {
                            if (!minematrix[y + 1][x - 1].isMine()) {
                                minematrix[y + 1][x - 1].incrementNearby();
                            }
                        }
                        if (x + 1 < fieldsize) {
                            if (!minematrix[y + 1][x + 1].isMine()) {
                                minematrix[y + 1][x + 1].incrementNearby();
                            }
                        }
                        if (!minematrix[y + 1][x].isMine()) {
                            minematrix[y + 1][x].incrementNearby();
                        }
                    }
                    if (x - 1 >= 0) {
                        if (!minematrix[y][x - 1].isMine()) {
                            minematrix[y][x - 1].incrementNearby();
                        }
                    }
                    if (x + 1 < fieldsize) {
                        if (!minematrix[y][x + 1].isMine()) {
                            minematrix[y][x + 1].incrementNearby();
                        }
                    }
                }
            }
        }
    }

    void printmines() {
        String minestring = "";
        String header = "  ";

        for (int y = 0; y < fieldsize; y++) {
            for (int x = 0; x < fieldsize; x++) {
                if (minematrix[y][x].isMine()) {
                    minestring += '*';
                } else {
                    minestring += (char) ('0' + minematrix[y][x].getNearby());
                }
            }
            header += y;
        }

        System.out.println(header);
        System.out.println();

        for (int i = 0; i < fieldsize; i++) {
            System.out.println(i + " " + minestring.substring((i * fieldsize), ((i * fieldsize) + fieldsize)));
        }
        System.out.println();
    }

    void processclick(MouseEvent e) {
        if (!gameover) {
            int row = e.getY() / tilesize;
            int col = e.getX() / tilesize;

            if (e.getButton() == MouseEvent.BUTTON3) {
                flagspace(row, col);
            } else {
                revealspace(row, col);
            }

            checkgameover(row, col);
            // System.out.println(gameover);
            // System.out.println(won);
            boardcomponent.repaint();
        }
    }

    void flagspace(int row, int col) {
        minematrix[row][col].setFlagged(!minematrix[row][col].isFlagged());
        if (minematrix[row][col].isFlagged()) {
            minesleft--;
        } else {
            minesleft++;
        }
    }

    void revealspace(int row, int col) {
        if (!minematrix[row][col].isFlagged()) {
            minematrix[row][col].setShow(true);
            if (!minematrix[row][col].isMine() && minematrix[row][col].getNearby() == 0) {
                bfs(row, col);
            }
        }
    }

    void bfs(int r, int c) {
        Stack<Space> s = new Stack<Space>();
        int row, col;
        Space space;
        boolean[][] visited = resetvisited();
        s.push(minematrix[r][c]);
        visited[r][c] = true;

        while (!s.isEmpty()) {
            space = s.pop();
            row = space.getRow();
            col = space.getCol();
            minematrix[row][col].setShow(true);
            if (minematrix[row][col].getNearby() == 0) {
                if (row - 1 >= 0) {
                    if (col - 1 >= 0 && !visited[row - 1][col - 1]) {
                        s.push(minematrix[row - 1][col - 1]);
                        visited[row - 1][col - 1] = true;
                    }
                    if (col + 1 < fieldsize && !visited[row - 1][col + 1]) {
                        s.push(minematrix[row - 1][col + 1]);
                        visited[row - 1][col + 1] = true;
                    }
                    if (!visited[row - 1][col]) {
                        s.push(minematrix[row - 1][col]);
                        visited[row - 1][col] = true;
                    }
                }
                if (row + 1 < fieldsize) {
                    if (col - 1 >= 0 && !visited[row + 1][col - 1]) {
                        s.push(minematrix[row + 1][col - 1]);
                        visited[row + 1][col - 1] = true;
                    }
                    if (col + 1 < fieldsize && !visited[row + 1][col + 1]) {
                        s.push(minematrix[row + 1][col + 1]);
                        visited[row + 1][col + 1] = true;
                    }
                    if (!visited[row + 1][col]) {
                        s.push(minematrix[row + 1][col]);
                        visited[row + 1][col] = true;
                    }
                }
                if (col - 1 >= 0 && !visited[row][col - 1]) {
                    s.push(minematrix[row][col - 1]);
                    visited[row][col - 1] = true;
                }
                if (col + 1 < fieldsize && !visited[row][col + 1]) {
                    s.push(minematrix[row][col + 1]);
                    visited[row][col + 1] = true;
                }
            }
        }
    }

    boolean[][] resetvisited() {
        boolean[][] v = new boolean[fieldsize][fieldsize];

        for (int y = 0; y < fieldsize; y++) {
            for (int x = 0; x < fieldsize; x++) {
                v[y][x] = false;
            }
        }

        return v;
    }

    void checkgameover(int row, int col) {
        if (minematrix[row][col].isMine() && minematrix[row][col].isShow()) {
            gameover = true;
            won = false;
            minematrix[row][col].setLastMine(true);
        } else {
            int count = 0;
            for (int y = 0; y < fieldsize; y++) {
                for (int x = 0; x < fieldsize; x++) {
                    if (minematrix[y][x].isFlagged() || !minematrix[y][x].isShow()) {
                        count++;
                    }
                }
            }
            if (count == minenumber) {
                gameover = true;
                won = true;
            }
        }
    }

    class BoardComponent extends JComponent {

        private static final long serialVersionUID = 1L;

        ImageIcon unclicked = new ImageIcon("unclicked.png");
        ImageIcon mine = new ImageIcon("mine.png");
        ImageIcon redmine = new ImageIcon("redmine.png");
        ImageIcon flag = new ImageIcon("flagged.png");
        ImageIcon clicked0 = new ImageIcon("clicked0.png");
        ImageIcon clicked1 = new ImageIcon("clicked1.png");
        ImageIcon clicked2 = new ImageIcon("clicked2.png");
        ImageIcon clicked3 = new ImageIcon("clicked3.png");
        ImageIcon clicked4 = new ImageIcon("clicked4.png");
        ImageIcon clicked5 = new ImageIcon("clicked5.png");
        ImageIcon clicked6 = new ImageIcon("clicked6.png");
        ImageIcon clicked7 = new ImageIcon("clicked7.png");
        ImageIcon clicked8 = new ImageIcon("clicked8.png");

        @Override
        protected void paintComponent(Graphics g) {
            if (!gameover) {
                for (int y = 0; y < fieldsize; y++) {
                    for (int x = 0; x < fieldsize; x++) {
                        if (!minematrix[y][x].isShow()) {
                            if (minematrix[y][x].isFlagged()) {
                                g.drawImage(flag.getImage(), x * tilesize, y * tilesize, this);
                            } else {
                                g.drawImage(unclicked.getImage(), x * tilesize, y * tilesize, this);
                            }
                        } else {
                            switch (minematrix[y][x].getNearby()) {
                            case 0:
                                g.drawImage(clicked0.getImage(), x * tilesize, y * tilesize, this);
                                break;
                            case 1:
                                g.drawImage(clicked1.getImage(), x * tilesize, y * tilesize, this);
                                break;
                            case 2:
                                g.drawImage(clicked2.getImage(), x * tilesize, y * tilesize, this);
                                break;
                            case 3:
                                g.drawImage(clicked3.getImage(), x * tilesize, y * tilesize, this);
                                break;
                            case 4:
                                g.drawImage(clicked4.getImage(), x * tilesize, y * tilesize, this);
                                break;
                            case 5:
                                g.drawImage(clicked5.getImage(), x * tilesize, y * tilesize, this);
                                break;
                            case 6:
                                g.drawImage(clicked6.getImage(), x * tilesize, y * tilesize, this);
                                break;
                            case 7:
                                g.drawImage(clicked7.getImage(), x * tilesize, y * tilesize, this);
                                break;
                            case 8:
                                g.drawImage(clicked8.getImage(), x * tilesize, y * tilesize, this);
                                break;
                            }
                        }
                    }
                }
            }
            if (gameover && !won) {
                for (int y = 0; y < fieldsize; y++) {
                    for (int x = 0; x < fieldsize; x++) {
                        if (!minematrix[y][x].isShow()) {
                            if (minematrix[y][x].isMine()) {
                                g.drawImage(mine.getImage(), x * tilesize, y * tilesize, this);
                            } else {
                                g.drawImage(unclicked.getImage(), x * tilesize, y * tilesize, this);
                            }
                        } else if (minematrix[y][x].isLastMine()) {
                            g.drawImage(redmine.getImage(), x * tilesize, y * tilesize, this);
                        } else {
                            switch (minematrix[y][x].getNearby()) {
                            case 0:
                                g.drawImage(clicked0.getImage(), x * tilesize, y * tilesize, this);
                                break;
                            case 1:
                                g.drawImage(clicked1.getImage(), x * tilesize, y * tilesize, this);
                                break;
                            case 2:
                                g.drawImage(clicked2.getImage(), x * tilesize, y * tilesize, this);
                                break;
                            case 3:
                                g.drawImage(clicked3.getImage(), x * tilesize, y * tilesize, this);
                                break;
                            case 4:
                                g.drawImage(clicked4.getImage(), x * tilesize, y * tilesize, this);
                                break;
                            case 5:
                                g.drawImage(clicked5.getImage(), x * tilesize, y * tilesize, this);
                                break;
                            case 6:
                                g.drawImage(clicked6.getImage(), x * tilesize, y * tilesize, this);
                                break;
                            case 7:
                                g.drawImage(clicked7.getImage(), x * tilesize, y * tilesize, this);
                                break;
                            case 8:
                                g.drawImage(clicked8.getImage(), x * tilesize, y * tilesize, this);
                                break;
                            }
                        }
                    }
                }
            } else if (gameover && won) {
                for (int y = 0; y < fieldsize; y++) {
                    for (int x = 0; x < fieldsize; x++) {
                        if (minematrix[y][x].isMine()) {
                            g.drawImage(flag.getImage(), x * tilesize, y * tilesize, this);
                        } else {
                            switch (minematrix[y][x].getNearby()) {
                            case 0:
                                g.drawImage(clicked0.getImage(), x * tilesize, y * tilesize, this);
                                break;
                            case 1:
                                g.drawImage(clicked1.getImage(), x * tilesize, y * tilesize, this);
                                break;
                            case 2:
                                g.drawImage(clicked2.getImage(), x * tilesize, y * tilesize, this);
                                break;
                            case 3:
                                g.drawImage(clicked3.getImage(), x * tilesize, y * tilesize, this);
                                break;
                            case 4:
                                g.drawImage(clicked4.getImage(), x * tilesize, y * tilesize, this);
                                break;
                            case 5:
                                g.drawImage(clicked5.getImage(), x * tilesize, y * tilesize, this);
                                break;
                            case 6:
                                g.drawImage(clicked6.getImage(), x * tilesize, y * tilesize, this);
                                break;
                            case 7:
                                g.drawImage(clicked7.getImage(), x * tilesize, y * tilesize, this);
                                break;
                            case 8:
                                g.drawImage(clicked8.getImage(), x * tilesize, y * tilesize, this);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    class BoardMouseListener implements MouseListener {

        @Override
        public void mouseClicked(MouseEvent e) {
            processclick(e);
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }

        @Override
        public void mousePressed(MouseEvent e) {
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }

    }

    class MenuActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == newgame) {
                // System.out.println("Would start new game.");
                newgame();
                boardcomponent.repaint();
            } else if (e.getSource() == easy) {
                // System.out.println("Would start new easy game.");
                difficulty = EASY;
                newgame();
            } else if (e.getSource() == medium) {
                // System.out.println("Would start new medium game.");
                difficulty = MEDIUM;
                newgame();
            } else if (e.getSource() == hard) {
                // System.out.println("Would start new hard game.");
                difficulty = HARD;
                newgame();
            }
        }

    }
}