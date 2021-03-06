/*
 * Visualization and HCI
 * Exercise 2: Data Representation and Interpolation
 * Volker Ahlers, HS Hannover (volker.ahlers@hs-hannover.de)
 */

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

@SuppressWarnings("ALL")
public class Interpolator extends JFrame {

	private static final long serialVersionUID = 1L;

	private int xSize;            // grid size
	private int ySize;
	private double xMin;            // grid origin
	private double yMin;
	private double dx;                // grid spacing
	private double dy;
	private double[][] data;        // attribute values f(x,y)

	/**
	 * Main method
	 */
	public static void main(String[] args) {
		// mathematical function
		Interpolator mathFunc = new Interpolator(
				"Math Function", 800, 800, 50, 50, -1.0, 1.0, -1.0, 1.0);
		mathFunc.createMathFuncData();
		mathFunc.setVisible(true);
	}

	/**
	 * Constructor
	 */
	public Interpolator(String title, int width, int height,
	                    int xSize, int ySize, double xMin, double xMax, double yMin, double yMax) {
		super(title);
		setSize(width, height);
		//setResizable(false);
		this.xSize = xSize;
		this.ySize = ySize;
		this.xMin = xMin;
		this.yMin = yMin;
		dx = (xMax - xMin) / (float) (xSize - 1);
		dy = (yMax - yMin) / (float) (ySize - 1);
		data = new double[xSize][ySize];
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}

	/**
	 * Create data
	 */
	public void createMathFuncData() {
		for (int i = 0; i < xSize; i++) {
			double x = xMin + i * dx;
			for (int j = 0; j < ySize; j++) {
				double y = yMin + j * dy;
				data[i][j] = Math.sin(2.5 * Math.PI * x) * Math.sin(2.5 * Math.PI * y);
			}
		}
	}

	/**
	 * Interpolate attribute value f(x,y)
	 */
	public double getInterpolatedData(double x, double y) {
		// (x,y) coordinates of value to be interpolated
		// indices of closest sample points in x direction, x0 <= x <= x1
		int ix1 = (int) ((x - xMin) / dx);
		ix1 = Math.min(Math.max(ix1, 0), xSize - 1);    // ensure 0 <= i0 < xSize
		int ix2 = Math.min(ix1 + 1, xSize - 1);
		// indices of closest sample points in y direction, y0 <= y <= y1
		int iy1 = (int) ((y - yMin) / dy);
		iy1 = Math.min(Math.max(iy1, 0), ySize - 1);    // ensure 0 <= j0 < ySize
		int iy2 = Math.min(iy1 + 1, ySize - 1);

		double x1 = xMin + ix1 * dx;
		double x2 = xMin + ix2 * dx;
		double y1 = yMin + iy1 * dy;
		double y2 = yMin + iy2 * dy;

		double r = (x - x1) / (x2 - x1);
		double s = (y - y1) / (y2 - y1);

		return (1 - r) * (1 - s) * data[ix1][iy1] +
				r * (1 - s) * data[ix2][iy1] +
				r * s * data[ix2][iy2] +
				(1 - r) * s * data[ix1][iy2];
	}

	/**
	 * Create visualization image and show it in frame
	 */
	@Override
	public void paint(Graphics graphics) {
		int width = getWidth();
		int height = getHeight();

		final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		double dxImage = dx * xSize / (double) width;
		double dyImage = dy * ySize / (double) height;
		for (int i = 0; i < width; i++) {
			double x = xMin + i * dxImage;
			for (int j = 0; j < height; j++) {
				double y = yMin + j * dyImage;
				// map f(x,y) in [-1,1] to grayscale RGB value in [0,1]^3
				float color = 0.5f * ((float) getInterpolatedData(x, y) + 1.0f);
				image.setRGB(i, j, new Color(color, color, color).getRGB());
			}
		}
		graphics.drawImage(image, 0, 0, null);
	}
}
