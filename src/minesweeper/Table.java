package minesweeper;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.swing.JOptionPane;

public class Table {
	
	public static final int[][] EIGHT = {
		{-1, -1},
		{-1, 0},
		{-1, 1},
		{0, -1},
		{0, 1},
		{1, -1},
		{1, 0},
		{1, 1}
	};
	
	public Game game;
	
	public HashMap<Point, Block> blocks;
	
	public Point camera;
	public int speed;
	
	public int blockSize = 25;
	public int width, height;
	
	public boolean gameOver;
	
	public int flagCount;
	
	public Table(Game game, int width, int height) {
		this.game = game;
		this.width = width;
		this.height = height;
		
		init();
		
		camera = new Point((blockSize + 2) * width / 2 - game.canvas.getWidth() / 2,
			(blockSize + 2) * height / 2 - game.canvas.getHeight() / 2);
		
		speed = 5;
	}
	
	public void init() {
		blocks = new HashMap<>();
		for (int x = 0; x < width; x++)
			for (int y = 0; y < height; y++) {
				boolean b = new Random().nextInt(100) < 15;
				blocks.put(new Point(x, y), new Block(x, y, (b) ? -1 : 0));
			}
		
		for (Map.Entry<Point, Block> e : blocks.entrySet())
			if (e.getValue().value == -1)
				for (Block b : getEight(e.getValue().x, e.getValue().y))
					if (b.value != -1) b.value++;
		gameOver = false;
	}
	
	public Block[] getEight(int x, int y) {
		ArrayList<Block> eight = new ArrayList<>();
		for (Map.Entry<Point, Block> e : blocks.entrySet()) {
			Point p = e.getKey();
			for (int i = 0; i < EIGHT.length; i++)
				if (p.x == x + EIGHT[i][0] && p.y == y + EIGHT[i][1])
					eight.add(e.getValue());
		}
		return eight.toArray(new Block[eight.size()]);
	}
	
	public void center() {
		camera.setLocation(-(game.canvas.getWidth() - width * (blockSize + 2)) / 2
			, -(game.canvas.getHeight() - height * (blockSize + 2)) / 2);
	}
	
	public void render(Graphics g) {
		int count = 0, all = 0;
		for (Map.Entry<Point, Block> e : blocks.entrySet()) {
			Block b = e.getValue();
			b.render(g, getBlockRectangle(b));
			if (b.value == -1) all++;
			if (b.flaged) count++;
		}
		g.setColor(Color.BLACK);
		g.drawString("flag : " + (all - count) + "/" + all, 10, game.canvas.getHeight() - 10);
		flagCount = all - count;
	}
	
	public Rectangle getBlockRectangle(Block b) {
		return new Rectangle(b.x * (blockSize + 2) - camera.x,
			b.y * (blockSize + 2) - camera.y, blockSize, blockSize);
	}
	
	public Block getBlockByPoint(Point p) {
		for (Map.Entry<Point, Block> e : blocks.entrySet()) {
			Rectangle r = getBlockRectangle(e.getValue());
			if (p.x > r.x && p.x < r.x + r.width && p.y > r.y && p.y < r.y + r.height)
				return e.getValue();
		}
		return null;
	}
	
	public void flag(Block b) {
		if (!b.opened && !gameOver && (b.flaged ? true : flagCount > 0))
			b.flaged = !b.flaged;
		gameOverCheck();
	}
	
	public void openBlock(Block b) {
		if (b.opened || b.flaged || gameOver) return;
		b.opened = true;
		if (b.value == 0)
			openBlank(b);
		if (b.value == -1)
			endGame(false);
		gameOverCheck();
	}
	
	public void openBlank(Block b) {
		for (Block bl : getEight(b.x, b.y))
			openBlock(bl);
	}
	
	public void endGame(boolean win) {
		int point = 0;
		int all = 0;
		for (Map.Entry<Point, Block> e : blocks.entrySet()) {
			Block b = e.getValue();
			if (b.value == -1) {
				if (b.flaged) {
					b.defused = true;
					point++;
				}
				b.opened = true;
				all++;
			}
		}
		JOptionPane.showMessageDialog(null,
			(win ? "Congratulations\n You Win!" : "Game Over")
			+ "\nScore : " + point + "/" + all);
		gameOver = true;
	}
	
	public void gameOverCheck() {
		for (Map.Entry<Point, Block> e : blocks.entrySet()) {
			Block b = e.getValue();
			if (!b.flaged && !b.opened)
				return;
		}
		endGame(true);
	}

}
