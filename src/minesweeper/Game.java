package minesweeper;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferStrategy;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;

public class Game {
	
	public static void main(String[] args) {
		new Game();
	}
	
	public JFrame frame;
	public Canvas canvas;
	
	public int fps = 0;
	
	public Table table;
	public boolean init;
	
	public HashMap<Integer, KeyEvent> keys;
	
	public Game() {
		frame = new JFrame();
		frame.setSize(800, 800);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		canvas = new Canvas();
		frame.add(canvas);
		canvas.requestFocus();
		canvas.createBufferStrategy(3);
		
		keys = new HashMap<>();
		canvas.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				keys.put(e.getKeyCode(), e);
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				keys.remove(e.getKeyCode());
			}
		});
		
		canvas.addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				table.blockSize -= e.getWheelRotation();
				if (table.blockSize < 1) table.blockSize = 1;
			}
		});
		
		canvas.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (table.gameOver) init = true;
				else {
					Block b = table.getBlockByPoint(e.getPoint());
					if (b == null) return;
					switch (e.getButton()) {
					case MouseEvent.BUTTON1:
						table.openBlock(b);
						break;
					case MouseEvent.BUTTON3:
						table.flag(b);
					}
				}
			}
		});
		
		table = new Table(this, 20, 20);
		
		long lt = System.nanoTime();
		long ltm = System.currentTimeMillis();
		double d = 0;
		while (true) {
			long n = System.nanoTime();
			d += (n - lt) / (1000000000D / 60D);
			lt = n;
			while (d > 0) {
				update();
				render();
				d--;
			}
			long nm = System.currentTimeMillis();
			if (nm - ltm >= 1000) {
				frame.setTitle("Minesweeper || FPS:" + fps);
				ltm = nm;
				fps = 0;
			}
		}
	}
	
	public void update() {
		for (Map.Entry<Integer, KeyEvent> e : keys.entrySet()) {
			switch (e.getKey()) {
			case KeyEvent.VK_W:
			case KeyEvent.VK_UP:
				table.camera.y -= table.speed;
				break;
			case KeyEvent.VK_A:
			case KeyEvent.VK_LEFT:
				table.camera.x -= table.speed;
				break;
			case KeyEvent.VK_S:
			case KeyEvent.VK_DOWN:
				table.camera.y += table.speed;
				break;
			case KeyEvent.VK_D:
			case KeyEvent.VK_RIGHT:
				table.camera.x += table.speed;
				break;
			case KeyEvent.VK_SPACE:
				table.center();
				break;
			}
		}
		
		if (init) {
			table.init();
			init = false;
		}
	}
	
	public void render() {
		fps++;
		BufferStrategy bs = canvas.getBufferStrategy();
		Graphics g = bs.getDrawGraphics();
		
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
		
		table.render(g);
		
		g.dispose();
		bs.show();
	}

}
