package com.emilytrabert.minesweeper;

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
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;


public class Minesweeper {

	public static void main(String[] args) {
		new Minesweeper();
	}

	Space [][] minematrix;
	
	BoardComponent boardcomponent;	
	ActionListener actionlistener;
	
	boolean gameover, won;
	int fieldsize, minenumber, windowsize, tilesize, minesleft;
	
	String difficulty;
	JFrame frame;
	JMenuBar menubar;
	JMenuItem newgame;
	JRadioButtonMenuItem easy, medium, hard;
	
	public Minesweeper() {
		difficulty = "easy";
		
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
		newgame.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_N, ActionEvent.ALT_MASK));
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
		if (difficulty == "easy") {
			fieldsize = 9;
			minenumber = 10;
		}
		else if (difficulty == "medium") {
			fieldsize = 16;
			minenumber = 40;
		}
		
		else if (difficulty == "hard") {
			fieldsize = 22;
			minenumber = 99;
		}
		gameover = false;
		won = false;
		minesleft = minenumber;
		windowsize = fieldsize*tilesize;
		frame.setSize(windowsize, windowsize+44);
		createfield();

		printmines();
	}
	void createfield() {
		char[] minearray = new char[fieldsize*fieldsize];
		
		for (int i = 0; i<(fieldsize*fieldsize); i++) {
			minearray[i] = 0;
		}
		
		for (int i = 0; i < minenumber; i++) {
			minearray[i] = '*';
		}
		
		minearray = shuffleminearray(minearray);
		
		minematrix = new Space[fieldsize][fieldsize];
		
		for (int y = 0; y < fieldsize; y++) {
			for (int x = 0; x < fieldsize; x++) {
				if (minearray[y*fieldsize+x] == '*') {
					minematrix[y][x] = new Space(true, 0, false, false, y, x, false);
				}
				else {
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
		
		for (int i = 0; i<n; i++) {
			r = rand.nextInt(n-i)+i;
			c = minearray[i];
			minearray[i] = minearray[r];
			minearray[r] = c;
		}
		
		return minearray;
	}
	void addwarnings() {
		for (int y = 0; y < fieldsize; y++) {
			for (int x = 0; x < fieldsize; x++) {
				if (minematrix[y][x].mine) {
					if (y-1 >= 0) {
						if (x-1 >= 0) {
							if (!minematrix[y-1][x-1].mine) {
								minematrix[y-1][x-1].nearby += 1;
							}
						}
						if (x+1 < fieldsize) {
							if (!minematrix[y-1][x+1].mine) {
								minematrix[y-1][x+1].nearby += 1;
							}
						}
						if (!minematrix[y-1][x].mine) {
							minematrix[y-1][x].nearby += 1;
						}
					}
					if (y+1 < fieldsize) {
						if (x-1 >= 0) {
							if (!minematrix[y+1][x-1].mine) {
								minematrix[y+1][x-1].nearby += 1;
							}
						}
						if (x+1 < fieldsize) {
							if (!minematrix[y+1][x+1].mine) {
								minematrix[y+1][x+1].nearby += 1;
							}
						}
						if (!minematrix[y+1][x].mine) {
							minematrix[y+1][x].nearby += 1;
						}
					}
					if (x-1 >= 0) {
						if (!minematrix[y][x-1].mine) {
							minematrix[y][x-1].nearby += 1;
						}
					}
					if (x+1 < fieldsize) {
						if (!minematrix[y][x+1].mine) {
							minematrix[y][x+1].nearby += 1;
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
				if (minematrix[y][x].mine) {
					minestring += '*';
				}
				else {
					minestring += (char)('0' + minematrix[y][x].nearby);
				}
			}
			header += y;
		}
		
		System.out.println(header);
		System.out.println();
		
		for (int i = 0; i < fieldsize; i++) {
			System.out.println(i + " " + minestring.substring((i*fieldsize),((i*fieldsize)+fieldsize)));
		}
		System.out.println();
	}

	void processclick(MouseEvent e) {
		if (!gameover) {
			int row = e.getY()/tilesize;
			int col = e.getX()/tilesize;
		
			if (e.getButton() == MouseEvent.BUTTON3) {
				flagspace(row, col);
			}
			else {
				revealspace(row, col);
			}
		
			checkgameover(row, col);
			//System.out.println(gameover);
			//System.out.println(won);
			boardcomponent.repaint();
		}
	}
	void flagspace(int row, int col) {
		minematrix[row][col].flagged = !minematrix[row][col].flagged;
		if (minematrix[row][col].flagged) {
			minesleft--;
		}
		else {
			minesleft++;
		}
	}
	void revealspace(int row, int col) {
		if (!minematrix[row][col].flagged) {
			minematrix[row][col].show = true;
			if (!minematrix[row][col].mine && minematrix[row][col].nearby == 0) {
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
			space = (Space) s.pop();
			row = space.row;
			col = space.col;
			minematrix[row][col].show = true;
			if (minematrix[row][col].nearby == 0) {
				if (row-1 >= 0) {
					if (col-1 >= 0 && !visited[row-1][col-1]) {
						s.push(minematrix[row-1][col-1]);
						visited[row-1][col-1] = true;
					}
					if (col+1 < fieldsize && !visited[row-1][col+1]) {
						s.push(minematrix[row-1][col+1]);
						visited[row-1][col+1] = true;
					}
					if (!visited[row-1][col]) {
						s.push(minematrix[row-1][col]);
						visited[row-1][col] = true;
					}
				}
				if (row+1 < fieldsize) {
					if (col-1 >= 0 && !visited[row+1][col-1]) {
						s.push(minematrix[row+1][col-1]);
						visited[row+1][col-1] = true;
					}
					if (col+1 < fieldsize && !visited[row+1][col+1]) {
						s.push(minematrix[row+1][col+1]);
						visited[row+1][col+1] = true;
					}
					if (!visited[row+1][col]) {
						s.push(minematrix[row+1][col]);
						visited[row+1][col] = true;
					}
				}
				if (col-1 >= 0 && !visited[row][col-1]) {
					s.push(minematrix[row][col-1]);
					visited[row][col-1] = true;
				}
				if (col+1 < fieldsize && !visited[row][col+1]) {
					s.push(minematrix[row][col+1]);
					 visited[row][col+1] = true;
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
		if (minematrix[row][col].mine && minematrix[row][col].show) {
			gameover = true;
			won = false;
			minematrix[row][col].lastmine = true;
		}
		else {
			int count = 0;
			for (int y = 0; y < fieldsize; y++) {
				for (int x = 0; x < fieldsize; x++) {
					if (minematrix[y][x].flagged || !minematrix[y][x].show) {
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

	class Space {
		int nearby, row, col;
		boolean mine, show, flagged, lastmine;
		
		Space (boolean m, int n, boolean s, boolean f, int r, int c, boolean l) {
			mine = m;
			nearby = n;
			show = s;
			flagged = f;
			row = r;
			col = c;
			lastmine = l;
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
	
		
		protected void paintComponent(Graphics g) {
			if (!gameover) {
				for (int y = 0; y < fieldsize; y++) {
					for (int x = 0; x < fieldsize; x++) {
						if (!minematrix[y][x].show) {
							if (minematrix[y][x].flagged) {
								g.drawImage(flag.getImage(), x*tilesize, y*tilesize, this);
							}
							else {
								g.drawImage(unclicked.getImage(), x*tilesize, y*tilesize, this);
							}
						}
						else {
							switch (minematrix[y][x].nearby) {
								case 0: g.drawImage(clicked0.getImage(), x*tilesize, y*tilesize, this); break;
								case 1: g.drawImage(clicked1.getImage(), x*tilesize, y*tilesize, this); break;
								case 2: g.drawImage(clicked2.getImage(), x*tilesize, y*tilesize, this); break;
								case 3: g.drawImage(clicked3.getImage(), x*tilesize, y*tilesize, this); break;
								case 4: g.drawImage(clicked4.getImage(), x*tilesize, y*tilesize, this); break;
								case 5: g.drawImage(clicked5.getImage(), x*tilesize, y*tilesize, this); break;
								case 6: g.drawImage(clicked6.getImage(), x*tilesize, y*tilesize, this); break;
								case 7: g.drawImage(clicked7.getImage(), x*tilesize, y*tilesize, this);	break;
								case 8: g.drawImage(clicked8.getImage(), x*tilesize, y*tilesize, this);	break;
							}
						}
					}
				}
			}
			if (gameover && !won) {
				for (int y = 0; y < fieldsize; y++) {
					for (int x = 0; x < fieldsize; x++) {
						if (!minematrix[y][x].show) {
							if (minematrix[y][x].mine) {
								g.drawImage(mine.getImage(), x*tilesize, y*tilesize, this);
							}
							else {
								g.drawImage(unclicked.getImage(), x*tilesize, y*tilesize, this);
							}
						}
						else if (minematrix[y][x].lastmine) {
							g.drawImage(redmine.getImage(), x*tilesize, y*tilesize, this);
						}
						else {
							switch (minematrix[y][x].nearby) {
								case 0: g.drawImage(clicked0.getImage(), x*tilesize, y*tilesize, this); break;
								case 1: g.drawImage(clicked1.getImage(), x*tilesize, y*tilesize, this); break;
								case 2: g.drawImage(clicked2.getImage(), x*tilesize, y*tilesize, this); break;
								case 3: g.drawImage(clicked3.getImage(), x*tilesize, y*tilesize, this); break;
								case 4: g.drawImage(clicked4.getImage(), x*tilesize, y*tilesize, this); break;
								case 5: g.drawImage(clicked5.getImage(), x*tilesize, y*tilesize, this); break;
								case 6: g.drawImage(clicked6.getImage(), x*tilesize, y*tilesize, this); break;
								case 7: g.drawImage(clicked7.getImage(), x*tilesize, y*tilesize, this);	break;
								case 8: g.drawImage(clicked8.getImage(), x*tilesize, y*tilesize, this);	break;
							}
						}
					}
				}
			}
			else if (gameover && won) {
				for (int y = 0; y < fieldsize; y++) {
					for (int x = 0; x < fieldsize; x++) {
						if (minematrix[y][x].mine) {
							g.drawImage(flag.getImage(), x*tilesize, y*tilesize, this);
						}
						else {
							switch (minematrix[y][x].nearby) {
								case 0: g.drawImage(clicked0.getImage(), x*tilesize, y*tilesize, this); break;
								case 1: g.drawImage(clicked1.getImage(), x*tilesize, y*tilesize, this); break;
								case 2: g.drawImage(clicked2.getImage(), x*tilesize, y*tilesize, this); break;
								case 3: g.drawImage(clicked3.getImage(), x*tilesize, y*tilesize, this); break;
								case 4: g.drawImage(clicked4.getImage(), x*tilesize, y*tilesize, this); break;
								case 5: g.drawImage(clicked5.getImage(), x*tilesize, y*tilesize, this); break;
								case 6: g.drawImage(clicked6.getImage(), x*tilesize, y*tilesize, this); break;
								case 7: g.drawImage(clicked7.getImage(), x*tilesize, y*tilesize, this);	break;
								case 8: g.drawImage(clicked8.getImage(), x*tilesize, y*tilesize, this);	break;
							}
						}
					}
				}
			}
		}
	}
	
	class BoardMouseListener implements MouseListener {
	
		public void mouseClicked(MouseEvent e) {
			processclick(e);
		}
	
		public void mouseEntered(MouseEvent e) {	
		}
		public void mouseExited(MouseEvent e) {
		}
		public void mousePressed(MouseEvent e) {
		}
		public void mouseReleased(MouseEvent e) {
		}
		
	}
	class MenuActionListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == newgame) {
				//System.out.println("Would start new game.");
				newgame();
				boardcomponent.repaint();
			}
			else if (e.getSource() == easy) {
				//System.out.println("Would start new easy game.");
				difficulty = "easy";
				newgame();
			}
			else if (e.getSource() == medium) {
				//System.out.println("Would start new medium game.");
				difficulty = "medium";
				newgame();
			}
			else if (e.getSource() == hard) {
				//System.out.println("Would start new hard game.");
				difficulty = "hard";
				newgame();
			}
		}
		
	}
}