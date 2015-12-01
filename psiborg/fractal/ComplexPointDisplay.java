package psiborg.fractal;

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
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.event.MouseInputListener;

import psiborg.fractal.colors.ColorMap;
import psiborg.fractal.colors.HueMap;
import psiborg.fractal.generators.FractalGenerator;
import psiborg.fractal.generators.MandelbrotGenerator;
import psiborg.fractal.jobs.JobFactory;
import psiborg.fractal.jobs.JobQueue;
import psiborg.fractal.jobs.RenderJob;

public class ComplexPointDisplay extends JFrame implements MouseInputListener, KeyListener {
	private static final long serialVersionUID = 1L;

	private final int SUPERSAMPLE;
	private final int NUM_WORKERS;

	private BufferedImage image;
	private FractalWorker[] workers;
	private ColorMap map;

	private Benchmark bench;

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
		this.NUM_WORKERS = workers;
		this.SUPERSAMPLE = supersample;
		
		Dimension d = new Dimension(1920, 1080);

		image = new BufferedImage(d.width * SUPERSAMPLE, d.height * SUPERSAMPLE, BufferedImage.TYPE_3BYTE_BGR);

		this.map = colors;
		this.map.generate(500);

		dxStart = -1;

		ar = (double) d.height / (double) d.width;

		viewX = -2.0;
		viewY = -2.0 * ar;
		viewW = 4.0;
		viewH = viewW * ar;

		this.workers = new FractalWorker[NUM_WORKERS];

		for (int n = 0; n < NUM_WORKERS; n++) {
			this.workers[n] = new FractalWorker(fractal, colors, image);
			this.workers[n].start();
		}

		setSize(d);
		setMinimumSize(d);
		setMaximumSize(d);

		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		addMouseListener(this);
		addMouseMotionListener(this);
		addKeyListener(this);

		bench = new Benchmark("Frame complete");

		startRedraw();
	}

	@Override
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;

		if (SUPERSAMPLE > 1) {
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		}

		g2d.drawImage(image, 0, 0, getWidth(), getHeight(), null);

		if (dxStart != -1) {
			g2d.setColor(Color.BLACK);
			g2d.drawRect((int) dxStart, (int) dyStart, (int) (dxEnd - dxStart), (int) (dxEnd - dxStart));
		}

		if (JobQueue.isEmpty()) {
			boolean dirty = false;
			boolean free = false;
			for (FractalWorker worker : workers) {
				dirty = dirty || worker.isDirty();
				free = free || !worker.isDirty();
			}

			if (dirty) {
				repaint();

				if (free) {
					FractalWorker w = getSlowestWorker();

					if (w != null) {
						w.split();
					}
				}
				return;
			}
		} else {
			repaint();
			return;
		}

		bench.stop();
	}

	private double getX(int x) {
		return (x / (double) image.getWidth()) * viewW + viewX;
	}

	private double getY(int y) {
		return -viewY - (y / (double) image.getHeight() * viewH);
	}

	private FractalWorker getSlowestWorker() {
		FractalWorker slow = null;
		double left = 10;
		double t;

		for (FractalWorker worker : workers) {
			t = worker.left();
			if (t > left) {
				left = t;
				slow = worker;
			}
		}

		return slow;
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
				ImageIO.write(image, "png", output);
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
		bench.start();

		RenderJob.quitActive();
		JobFactory.chunk(image.getRaster(), new Viewport(viewX, viewY, viewW, viewH), 2);

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
