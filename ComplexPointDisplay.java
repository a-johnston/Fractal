import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.event.MouseInputListener;

public class ComplexPointDisplay extends JFrame implements MouseInputListener, KeyListener {
	private static final long serialVersionUID = 1L;
	
	public static final int SUPERSAMPLE = 1;
	public static final int STEPS_PER_FRAME = 25000;

	private BufferedImage image;
	private FractalGenerator fractal;
	private MutableComplexDouble value;
	private WritableRaster raster;
	private ColorMap map;
	private int i, j;
	private double y;

	private double viewX;
	private double viewY;
	private double viewW;
	private double viewH;
	private double ar;

	private double dxStart;
	private double dyStart;
	private double dxEnd;
	private double dyEnd;

	private int chunkMax = 64;
	private int lastSize = Integer.MAX_VALUE;
	private int chunkSize = chunkMax;

	public ComplexPointDisplay(FractalGenerator fractal, ColorMap colors) {
		this.fractal = fractal;
		Dimension d = new Dimension(800, 600);

		image = new BufferedImage(d.width * SUPERSAMPLE, d.height * SUPERSAMPLE, BufferedImage.TYPE_3BYTE_BGR);
		raster = image.getRaster();
		value = new MutableComplexDouble(0, 0);

		this.map = colors;
		this.map.generate(500);

		dxStart = -1;

		ar = (double) d.height / (double) d.width;

		viewX = -2.0;
		viewY = -2.0 * ar;
		viewW = 4.0;
		viewH = viewW * ar;

		setSize(d);
		setMinimumSize(d);
		setMaximumSize(d);
		//		setUndecorated(true);

		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		addMouseListener(this);
		addMouseMotionListener(this);
		addKeyListener(this);
	}

	@Override
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;

		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		
		g2d.drawImage(image, 0, 0, getWidth(), getHeight(), null);

		if (dxStart != -1) {
			g2d.setColor(Color.BLACK);
			g2d.drawRect((int) dxStart, (int) dyStart, (int) (dxEnd - dxStart), (int) (dxEnd - dxStart));
		}

		int steps;

		int[] color;
		int[] black = {0, 0, 0};

		for (int n = 0; n < STEPS_PER_FRAME; n++) {
			if (j < image.getHeight()) {
				y = getY(j);
				if (i < image.getWidth()) {
					if (i % lastSize != 0 || j % lastSize != 0) {
						value.set(getX(i), y);
						steps = fractal.steps(value);

						if (steps == FractalGenerator.THRESHOLD_STEPS) {
							color = black;
						} else {
							color = map.get(steps);
						}
						
						raster.setPixel(i, j, color);
					}
					i += chunkSize;
				} else {
					i = 0;
					j += chunkSize;
				}

				this.repaint();
			} else {
				if (chunkSize > 1) {
					lastSize = chunkSize;
					chunkSize = chunkSize >> 1;
							j = 0;
							this.repaint();
				} else {
					g2d.drawChars("Ready".toCharArray(), 0, 5, 10, 40);
					File output = new File("/home/adam/output.png");
					try {
						ImageIO.write(image, "png", output);
					} catch (IOException e) {
						e.printStackTrace();
					}
					break;
				}
			}
		}
	}

	private double getX(int x) {
		return (x / (double) image.getWidth()) * viewW + viewX;
	}

	private double getY(int y) {
		return -viewY - (y / (double) image.getHeight() * viewH);
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
		i = 0;
		j = 0;
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
		dxStart = getX((int) dxStart * SUPERSAMPLE);
		dyStart = -getY((int) dyStart * SUPERSAMPLE);
		dxEnd = getX(e.getX() * SUPERSAMPLE);
		dyEnd = -getY(e.getY() * SUPERSAMPLE);

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
