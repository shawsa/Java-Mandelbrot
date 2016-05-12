import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
public class Mandelbrot{
	static double x_center = -.748;
	static double y_center = .10025;
	static double x_width = .0002;
	static int iteration_limit = 2000;
	static int iteration_color_start = 0;
	static double limit_square = 4;
	static double res = 8;
	static int[][] Spectrum = {{100,0,0},{150,0,0},{255,0,255},{0,50,50},{255,200,200}};
	static int[] set_color = {255,255,255};

	static double y_height = x_width * 9.0/16.0;
	static double x_min = x_center - .5 * x_width;
	static double x_max = x_center + .5 * x_width;
	static double y_min = y_center - .5 * y_height;
	static double y_max = y_center + .5 * y_height;
	static int x_pixels = (int) Math.round(1920*res);
	static int y_pixels = (int) Math.round(x_pixels*.5625);
	
		
	public static int Converge(Complex z){
		int i = 0;
		//System.out.println(iteration_limit);
		Complex w = z;
		while(w.ModSquare()<limit_square && i< iteration_limit){
			i++;
			w = w.Square().Add(z);
		}
		return i;
	}
	public static Complex PixelsToComplex(int x, int y){
		double re = x_min + x * x_width / (x_pixels-1);
		double im = y_max - y * y_height / (y_pixels-1);
		return new Complex(re,im);
	}
		

	public static void main(String args[]){
		
		int colors_per_gradient = (iteration_limit - iteration_color_start)/(Spectrum.length-1);
		//Each gradent includes the lower color, but on the upper number.
		//To include the final number as well as the color for the set (iteration_limit) we add 2
		iteration_limit = colors_per_gradient * (Spectrum.length-1) + iteration_color_start + 2;
		int colors[] = new int[iteration_limit - iteration_color_start + 1];

		//Generate all colors
		int r,g,b,colors_left;
		for(int grad=0; grad<Spectrum.length-1; grad++){
			for(int c=0; c<colors_per_gradient; c++){
				colors_left = colors_per_gradient - c;
				r = (colors_left * Spectrum[grad][0] + c * Spectrum[grad+1][0])/colors_per_gradient;
				g = (colors_left * Spectrum[grad][1] + c * Spectrum[grad+1][1])/colors_per_gradient;
				b = (colors_left * Spectrum[grad][2] + c * Spectrum[grad+1][2])/colors_per_gradient;
				colors[grad*colors_per_gradient+c] = (r<<16) + (g<<8) + b;
			}
		}
		//include the last gradent color
		r  = Spectrum[Spectrum.length-1][0];
		g  = Spectrum[Spectrum.length-1][1];
		b  = Spectrum[Spectrum.length-1][2];
		colors[colors.length-2] = (r<<16) + (g<<8) + b;
		//the last iteration is set to the set color
		colors[colors.length-1] = (set_color[0]<<16) + (set_color[1]<<8) + set_color[2];
		
		//for(int c=0; c<colors.length; c++){System.out.println(c + ": " + Integer.toHexString(colors[c]));}

		BufferedImage img = new BufferedImage(x_pixels,y_pixels,BufferedImage.TYPE_INT_RGB);
		
		System.out.println("Calculating...");
		System.out.print("0%\r");
		for(int x=0; x<x_pixels; x++){
			System.out.print(100*x/x_pixels + "%\r");
			for(int y=0; y<y_pixels; y++){
				int my_count = Converge(PixelsToComplex(x,y))-iteration_color_start;
				if(my_count<0){my_count = 0;}
				//System.out.println(my_count);
				img.setRGB(x,y,colors[my_count]);
			}
		}
		System.out.println("100%");
		String name;
		if(args.length>0){name = args[0];}else{name = "image";}
		File f = new File(name + ".png");
		try{
			ImageIO.write(img, "PNG", f);
		}catch(Exception e){
			System.out.println("Failed to write file.");
		}
	}
}
