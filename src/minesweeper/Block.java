package minesweeper;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;

public class Block {

	public int x, y, value;

	public boolean opened, flaged, defused;

	public Block(int x, int y, int value) {
		this.x = x;
		this.y = y;
		this.value = value;
	}

	public void render(Graphics g, Rectangle r) {
		g.setFont(new Font("DialogInput", Font.CENTER_BASELINE, (r.width + r.height) / 2));
		if (opened) {
			g.setColor(Color.LIGHT_GRAY);
			g.fillRect(r.x, r.y, r.width, r.height);

			switch (value) {
			case -1:
				g.setColor(defused ? Color.WHITE : Color.RED);
				break;
			case 0:
				return;
			case 1:
				g.setColor(Color.CYAN);
				break;
			case 2:
				g.setColor(Color.BLUE);
				break;
			case 3:
				g.setColor(Color.GREEN);
				break;
			case 4:
				g.setColor(Color.PINK);
				break;
			case 5:
				g.setColor(Color.ORANGE);
				break;
			case 6:
				g.setColor(Color.MAGENTA);
				break;
			case 7:
				g.setColor(Color.GRAY);
				break;
			case 8:
				g.setColor(Color.BLACK);
				break;
			}
			String s = (value != -1) ? value + "" : (defused) ? "D" : "b";
			Rectangle sb = getStringBounds(g, s, r.x, r.y);
			g.drawString(s, r.x + (r.width - sb.width) / 2,
					r.y + sb.height + (r.height - sb.height) / 2);
		} else {
			g.setColor(Color.DARK_GRAY);
			g.fillRect(r.x, r.y, r.width, r.height);

			if (flaged) {
				g.setColor(Color.RED);
				String s = "F";
				Rectangle sb = getStringBounds(g, s, r.x, r.y);
				g.drawString(s, r.x + (r.width - sb.width) / 2,
						r.y + sb.height + (r.height - sb.height) / 2);
			}
		}
	}

	private Rectangle getStringBounds(Graphics g, String str, int x, int y) {
		FontRenderContext frc = ((Graphics2D) g).getFontRenderContext();
		GlyphVector gv = g.getFont().createGlyphVector(frc, str);
		return gv.getPixelBounds(null, x, y);
	}

	public Point getPoint() {
		return new Point(x, y);
	}

}