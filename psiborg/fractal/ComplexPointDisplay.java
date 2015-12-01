package psiborg.fractal;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.event.MouseInputListener;

import psiborg.fractal.colors.ColorMap;
import psiborg.fractal.colors.HueMap;
import psiborg.fractal.generators.FractalGenerator;
import psiborg.fractal.generators.MandelbrotGenerator;

public class ComplexPointDisplay extends JFrame implements MouseInputListener, KeyListener {
	private static final long serialVersionUID = 1L;
	
	private final ComplexImage image;

	private double viewX;
	private double viewY;
	private double viewW;
	private double viewH;
	private double ar;

	private double dxStart;
	private double dyStart;
	private double dxEnd;
	private double dyEnd;

	public ComplexPointDisplay() {
		this(new MandelbrotGenerator());
	}

	public ComplexPointDisplay(FractalGenerator fractal) {
		this(fractal, new HueMap());
	}

	public ComplexPointDisplay(FractalGenerator fractal, ColorMap colors) {
		this(fractal, colors, 8, 1);
	}

	public ComplexPointDisplay(FractalGenerator fractal, ColorMap colors, int workers, int supersample) {
		Dimension d = new Dimension(1920, 1080);
		
		image = new ComplexImage(d.width, d.height, fractal, colors, workers, supersample);

		dxStart = -1;

		ar = (double) d.height / (double) d.width;

		viewX = -2.0;
		viewY = -2.0 * ar;
		viewW = 4.0;
		viewH = viewW * ar;

		setSize(d);
		setMinimumSize(d);
		setMaximumSize(d);

		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		addMouseListener(this);
		addMouseMotionListener(this);
		addKeyListener(this);

		startRedraw();
	}

	@Override
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;

		g2d.drawImage(image.getImage(), 0, 0, getWidth(), getHeight(), null);

		if (dxStart != -1) {
			g2d.setColor(Color.BLACK);
			g2d.drawRect((int) dxStart, (int) dyStart, (int) (dxEnd - dxStart), (int) (dxEnd - dxStart));
		}

		if (image.isDirty()) {
			repaint();
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
		case KeyEvent.VK_S:
			File output = new File("out.png");
			try {
				ImageIO.write(image.getImage(), "png", output);
				System.out.println("Wrote to 'out.png'");
			} catch (IOException e1) {
				System.out.println("Failed to write image!");
			}
			break;
		case KeyEvent.VK_R:
			repaint();
			break;
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
			viewH -= lvl * ar;
			startRedraw();
			break;
		case KeyEvent.VK_MINUS:
		case KeyEvent.VK_UNDERSCORE:
			viewX -= lvl;
			viewY -= lvl;
			lvl *= 2;
			viewW += lvl;
			viewH += lvl * ar;
			startRedraw();
			break;
		case KeyEvent.VK_0:
			viewX = -2.0;
			viewY = -2.0 * ar;
			viewW = 4.0;
			viewH = 4.0 * ar;
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
		image.startDraw(new Viewport(viewX, viewY, viewW, viewH));
		repaint();
	}

	@Override
	public void mousePressed(MouseEvent e) {
		dxStart = e.getX();
		dyStart = e.getY();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (Math.abs(dxStart - e.getX()) < 10 && Math.abs(dyStart - e.getY()) < 10) {
			dxStart = -1;
			return;
		}

		dxStart = getX((int) dxStart);
		dyStart = -getY((int) dyStart);
		dxEnd = getX(e.getX());
		dyEnd = -getY(e.getY());

		viewX = Math.min(dxStart, dxEnd);
		viewY = Math.min(dyStart, dyEnd);
		viewW = Math.abs(dxStart - dxEnd);
		viewH = viewW * ar;

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
