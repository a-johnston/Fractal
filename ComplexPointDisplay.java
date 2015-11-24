import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.event.MouseInputListener;

public class ComplexPointDisplay extends JFrame implements MouseInputListener, KeyListener {
	private static final long serialVersionUID = 1L;

	private BufferedImage image;
	private FractalGenerator fractal;
	private MutableComplexDouble value;
	private Graphics2D imageGraphics;
	private int line;
	private double y;
	
	private double viewX;
	private double viewY;
	private double viewW;
	private double viewH;
	
	private double dxStart;
	private double dyStart;
	private double dxEnd;
	private double dyEnd;
	
	private int chunkMax = 16;
	private int lastSize = Integer.MAX_VALUE;
	private int chunkSize = chunkMax;

	public ComplexPointDisplay(FractalGenerator fractal) {
		this.fractal = fractal;
		Dimension d = new Dimension(600, 600);
		
		image = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_RGB);
		imageGraphics = image.createGraphics();
		value = new MutableComplexDouble(0, 0);

		dxStart = -1;
		
		viewX = -2.0;
		viewY = -2.0 * (double) d.height / (double) d.width;
		viewW = 4.0;
		viewH = viewW * (double) d.height / (double) d.width;
		
		setSize(d);
		setMinimumSize(d);
		setMaximumSize(d);

		setVisible(true);
		
		addMouseListener(this);
		addMouseMotionListener(this);
		addKeyListener(this);
	}

	@Override
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;

		g2d.drawImage(image, 0, 0, null);
		
		if (dxStart != -1) {
			g2d.setColor(Color.BLACK);
			g2d.drawRect((int) dxStart, (int) dyStart, (int) (dxEnd - dxStart), (int) (dxEnd - dxStart));
		}

		if (line < getHeight()) {
			y = getY(line);
			for (int i = 0; i < getWidth(); i += chunkSize) {
				if (i % lastSize == 0 && line % lastSize == 0) {
					continue;
				}
				value.set(getX(i), y);
				int steps = fractal.steps(value);
				
				imageGraphics.setColor(Color.getHSBColor((steps / 100f), 1f, steps == FractalGenerator.THRESHOLD_STEPS ? 0f : 1f));
				
				if (chunkSize > 1) {
					imageGraphics.fillRect(i, line, chunkSize, chunkSize);
				} else {
					imageGraphics.drawLine(i, line, i, line);
				}
			}
			line += chunkSize;
			
			System.out.println("Drawing line " + chunkSize);
			
			this.repaint();
		} else {
			if (chunkSize > 1) {
				lastSize = chunkSize;
				chunkSize = chunkSize >> 1;
				
				line = 0;
				
				System.out.println("Chunk: " + chunkSize);
				this.repaint();
			} else if (chunkSize == 0) {
				System.out.println("Done!");
			}
		}
	}
	
	private double getX(int x) {
		return (x / (double) getWidth()) * viewW + viewX;
	}
	
	private double getY(int y) {
		return -viewY - (y / (double) getHeight() * viewH);
	}

	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyPressed(KeyEvent e) {
		double lvl = viewW / 100.0;
		if ((e.getModifiersEx() & InputEvent.SHIFT_DOWN_MASK) != 0) {
			lvl *= 2.0;
		}
		switch (e.getKeyCode()) {
		case KeyEvent.VK_LEFT:
			viewX -= lvl;
			startRedraw();
			break;
		case KeyEvent.VK_RIGHT:
			viewX += lvl;
			startRedraw();
			break;
		case KeyEvent.VK_UP:
			viewY -= lvl;
			startRedraw();
			break;
		case KeyEvent.VK_DOWN:
			viewY += lvl;
			startRedraw();
			break;
		case KeyEvent.VK_PLUS:
		case KeyEvent.VK_EQUALS:
			viewX += lvl;
			viewY += lvl;
			lvl *= 2;
			viewW -= lvl;
			viewH -= lvl;
			startRedraw();
			break;
		case KeyEvent.VK_MINUS:
		case KeyEvent.VK_UNDERSCORE:
			viewX -= lvl;
			viewY -= lvl;
			lvl *= 2;
			viewW += lvl;
			viewH += lvl;
			startRedraw();
			break;
		case KeyEvent.VK_0:
			viewX = -2.0;
			viewY = -2.0;
			viewW = 4.0;
			viewH = 4.0;
			startRedraw();
			break;
		case KeyEvent.VK_ESCAPE:
			System.exit(0);
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {}
	
	private void startRedraw() {
		line = 0;
		chunkSize = chunkMax;
		lastSize = Integer.MAX_VALUE;
		repaint();
	}

	@Override
	public void mousePressed(MouseEvent e) {
		dxStart = e.getX();
		dyStart = e.getY();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		dxStart = getX((int) dxStart);
		dyStart = -getY((int) dyStart);
		dxEnd = getX(e.getX());
		dyEnd = -getY(e.getY());
		
		System.out.print(viewX + " " + viewY);
		
		viewX = Math.min(dxStart, dxEnd);
		viewY = Math.min(dyStart, dyEnd);
		viewW = Math.abs(dxStart - dxEnd);
		viewH = viewW * getHeight() / getWidth();
		
		System.out.println(" -> " + viewX + " " + viewY);
		
		dxStart = -1;
		
		startRedraw();
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		dxEnd = e.getX();
		dyEnd = e.getY();
		
		repaint();
		
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mouseMoved(MouseEvent e) {}
}
