import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
public class Mandelbrot{
	//default independent parameters
	static double x_center = -.25;
	static double y_center = 0;
	static double x_width = 4;
	static int iteration_limit = 50;
	static int iteration_color_start = 0;
	static double limit_square = 4;
	static double res = .5;
	static int[][] Spectrum = {{0,0,0},{0,255,0},{200,255,200}};
	static int[] set_color = {0,0,0};

	//dependent parameters
	static double y_height, x_min, x_max, y_min, y_max;
	static int x_pixels, y_pixels;
	/*
	static double y_height = x_width * 9.0/16.0;
	static double x_min = x_center - .5 * x_width;
	static double x_max = x_center + .5 * x_width;
	static double y_min = y_center - .5 * y_height;
	static double y_max = y_center + .5 * y_height;
	static int x_pixels = (int) Math.round(1920*res);
	static int y_pixels = (int) Math.round(x_pixels*.5625);
	*/
		
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
			
		//Parse args
/***************************************************************
	Argument list
	Mandelbrot [-x xcenter] [-y ycenter] [-w width] [-i iteration-limit iteration-color-start] [-r resolution] [file]
		-x		the real coordinate of the center of the image
		-y 		the imaginary coordinate of the center of the image
		-w 		the width of the viewing window
		-i		the iteration limit specifies the nubmer of iterations before the point is considered in the set, while the color start indicates the iteration number where the color spectrum begins
		-r 		the resolution of the image. A resolution of 1 will give a standard 1920x1080 image
		file	the name of the file. Defaults to "image.png" if not specified
***************************************************************/
		String name = "image";
		int argindex = 0;
		boolean file_set = false;
		while(argindex<args.length){
			switch(args[argindex]){
				case "-x":
					if(argindex+1>=args.length){
						System.err.println("Error: -x flag needs coordinate value.");
						return;
					}else{
						try{
							x_center = Double.parseDouble(args[argindex+1]);
						}catch(NumberFormatException e){
							System.err.println("Error: -x needs a floating point decimal argument.");
							return;
						}
					}
					argindex += 2;
					break;
				case "-y":
					if(argindex+1>=args.length){
						System.err.println("Error: -y flag needs coordinate value.");
						return;
					}else{
						try{
							y_center = Double.parseDouble(args[argindex+1]);
						}catch(NumberFormatException e){
							System.err.println("Error: -y needs a floating point decimal argument.");
							return;
						}
					}
					argindex += 2;
					break;
				case "-w":
					if(argindex+1>=args.length){
						System.err.println("Error: -w flag needs window width value.");
						return;
					}else{
						try{
							x_width = Double.parseDouble(args[argindex+1]);
						}catch(NumberFormatException e){
							System.err.println("Error: -w needs a floating point decimal argument.");
							return;
						}
					}
					argindex += 2;
					break;
				case "-i":
					if(argindex+2>=args.length){
						System.err.println("Error: -i flag needs two integer values.");
						return;
					}else{
						try{
							iteration_limit = Integer.parseInt(args[argindex+1]);
							iteration_color_start = Integer.parseInt(args[argindex+2]);
						}catch(NumberFormatException e){
							System.err.println("Error: -i needs two integer arguments.");
							return;
						}
					}
					if(iteration_limit<=iteration_color_start){
						System.err.println("Error: iteration limit must be larger than the color start.");
						return;
					}
					argindex += 3;
					break;
				case "-r":
					if(argindex+1>=args.length){
						System.err.println("Error: -r flag needs floating point value.");
						return;
					}else{
						try{
							res = Double.parseDouble(args[argindex+1]);
						}catch(NumberFormatException e){
							System.err.println("Error: -r needs a floating point decimal argument.");
							return;
						}
					}
					argindex += 2;
					break;
				default:
					if(!file_set){
						name = args[argindex];
						file_set = true;
						argindex++;
					}else{
						System.err.println("Error: to many arguments.");
						return;
					}
					break;
					
			}
		}
		System.out.println("program survived parameter parsing.");
		System.out.println("-x:\t" + x_center);
		System.out.println("-y:\t" + y_center);
		System.out.println("-w:\t" + x_width);
		System.out.println("-i:\t" + iteration_limit + "   " + iteration_color_start);
		System.out.println("-r:\t" + res);
		System.out.println("file:\t" + name);
		
		
		//Adjust program constants
		y_height = x_width * 9.0/16.0;
		x_min = x_center - .5 * x_width;
		x_max = x_center + .5 * x_width;
		y_min = y_center - .5 * y_height;
		y_max = y_center + .5 * y_height;
		x_pixels = (int) Math.round(1920*res);
		y_pixels = (int) Math.round(x_pixels*.5625);
		
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
		

		BufferedImage img = new BufferedImage(x_pixels,y_pixels,BufferedImage.TYPE_INT_RGB);
		
		System.out.println("Calculating...");
		System.out.print("0%\r");
		for(int x=0; x<x_pixels; x++){
			System.out.print(100*x/x_pixels + "%\r");
			for(int y=0; y<y_pixels; y++){
				int my_count = Converge(PixelsToComplex(x,y))-iteration_color_start;
				if(my_count<0){my_count = 0;}
				img.setRGB(x,y,colors[my_count]);
			}
		}
		System.out.println("100%");
		//String name;
		//if(args.length>0){name = args[0];}else{name = "image";}
		File f = new File(name + ".png");
		try{
			ImageIO.write(img, "PNG", f);
		}catch(Exception e){
			System.out.println("Failed to write file.");
		}
	}
}
