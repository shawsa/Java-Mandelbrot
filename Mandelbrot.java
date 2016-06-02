import java.awt.image.BufferedImage;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.*;
import javafx.scene.paint.Color;
import java.io.*;
import javax.imageio.ImageIO;
public class Mandelbrot{
	//default independent parameters
	double x_center = -.25;
	double y_center = 0;
	double x_width = 4;
	double aspect = 16.0/9.0;
	int iteration_limit = 50;
	int iteration_color_start = 0;
	double limit_square = 4;
	//double res = .5;
	int[][] Spectrum = {{0,0,0},{255,0,0},{255,255,0},{0,255,0},{0,255,255},{0,0,255}};
	int[] set_color = {0,0,0};
	
	boolean julia = false;
	Complex julia_center = new Complex(0,0);

	//dependent parameters
	double y_height, x_min, x_max, y_min, y_max;
	int x_pixels, y_pixels;
		
	//Constructor
	Mandelbrot(){
		y_height = x_width / aspect;
		x_pixels = 1920/2;
		y_pixels = 1080/2;
		recalculateWindow();
	}
	//Setters
		//Sets window ranges from center and width/height
	public void recalculateWindow(){
		x_min = x_center - .5 * x_width;
		x_max = x_center + .5 * x_width;
		y_min = y_center - .5 * y_height;
		y_max = y_center + .5 * y_height;
	}
	public void setCenter(double x, double y){
		x_center = x;
		y_center = y;
		recalculateWindow();
	}
	public void setAspect(double aspect){
		this.aspect = aspect;
		y_height = x_width / aspect;
		y_pixels = (int) Math.round(x_pixels/aspect);
		recalculateWindow();
	}
	public void setWidth(double width){
		x_width = width;
		y_height = width/aspect;
		y_pixels = (int) Math.round(x_pixels/aspect);
		recalculateWindow();
	}
	public void setHeight(double height){
		y_height = height;
		x_width = height * aspect;
		x_pixels = (int) Math.round(y_pixels*aspect);
		recalculateWindow();
	}
	public void setWidthHeight(double width, double height){
		x_width = width;
		y_height = height;
		aspect = width/height;
		y_pixels = (int) Math.round(x_pixels/aspect);
		recalculateWindow();
	}
	public void setWindow(double x_min, double x_max, double y_min, double y_max){
		this.x_min = x_min;
		this.x_max = x_max;
		this.y_min = y_min;
		this.y_max = y_max;
		x_center = (x_max + x_min)/2;
		y_center = (y_max + y_min)/2;
		x_width = x_max - x_min;
		y_height = y_max - y_min;
		aspect = x_width / y_height;
		y_pixels = (int) Math.round(x_pixels/aspect);
	}
	public void setPixels(int x, int y){
		x_pixels = x;
		y_pixels = y;
	}
	public void setJulia(double x, double y){
		julia_center = new Complex(x,y);
	}	
		
	public void setSpectrum(String arg) throws IllegalArgumentException,FileNotFoundException, IOException{
		String color_file = "spectrums.txt";
		String line = null;
		String textArray = "";
		FileReader fileReader = new FileReader(color_file);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		boolean found_spectrum = false;
		while((line = bufferedReader.readLine()) != null){
			if(line.equals(arg)){
				textArray = bufferedReader.readLine();
				found_spectrum = true;
				break;
			}
		}
		if(!found_spectrum){throw new IllegalArgumentException();}
		textArray = textArray.replaceAll("\\s","");
		//parse text array to spectrum
		try{
			String colors[] = textArray.split(";");
			//for(int i=0; i<colors.length-1; i++){System.out.println(colors[i]);}
			Spectrum = new int[colors.length-1][3];
			for(int i=0; i<colors.length-1; i++){
				String[] current_color = colors[i].split(",");
				for(int j=0; j<3; j++){
					Spectrum[i][j] = Integer.parseInt(current_color[j]);
					if(Spectrum[i][j]<0 || Spectrum[i][j]>255){throw new IllegalArgumentException();}
				}
			}
			String[] current_color = colors[colors.length-1].split(",");
			for(int j=0; j<3; j++){
				set_color[j] = Integer.parseInt(current_color[j]);
				if(set_color[j]<0 || set_color[j]>255){throw new IllegalArgumentException();}
			}
		}catch(Exception e){throw new IllegalArgumentException();}
	}
	
	public int Converge(Complex z){
		int i = 0;
		Complex w = z;
		while(w.ModSquare()<limit_square && i< iteration_limit-1){
			i++;
			w = w.Square().Add(z);
		}
		return i;
	}
	
	public int JuliaConverge(Complex z){
		int i = 0;
		Complex w = z;
		while(w.ModSquare()<limit_square && i< iteration_limit-1){
			i++;
			w = w.Square().Add(julia_center);
		}
		return i;
	}
	
	public Complex PixelsToComplex(int x, int y){
		double re = x_min + x * x_width / (x_pixels-1);
		double im = y_max - y * y_height / (y_pixels-1);
		return new Complex(re,im);
	}
		
	/*public BufferedImage generateImage(){
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
				int my_count;
				if(!julia){
					my_count = Converge(PixelsToComplex(x,y))-iteration_color_start;
				}else{
					my_count = JuliaConverge(PixelsToComplex(x,y))-iteration_color_start;
				}
				if(my_count<0){my_count = 0;}
				img.setRGB(x,y,colors[my_count]);
			}
		}
		System.out.println("100%");
		return img;
	}*/
	
	public WritableImage generateImage(){
		int colors_per_gradient = (iteration_limit - iteration_color_start)/(Spectrum.length-1);
		//Each gradent includes the lower color, but not the upper number.
		//To include the final number as well as the color for the set (iteration_limit) we add 2
		iteration_limit = colors_per_gradient * (Spectrum.length-1) + iteration_color_start + 2;
		//int colors[] = new int[iteration_limit - iteration_color_start + 1];
		Color colors[] = new Color[iteration_limit - iteration_color_start];

		//Generate all colors
		int r,g,b,colors_left;
		for(int grad=0; grad<Spectrum.length-1; grad++){
			for(int c=0; c<colors_per_gradient; c++){
				colors_left = colors_per_gradient - c;
				r = (colors_left * Spectrum[grad][0] + c * Spectrum[grad+1][0])/colors_per_gradient;
				g = (colors_left * Spectrum[grad][1] + c * Spectrum[grad+1][1])/colors_per_gradient;
				b = (colors_left * Spectrum[grad][2] + c * Spectrum[grad+1][2])/colors_per_gradient;
				//colors[grad*colors_per_gradient+c] = (r<<16) + (g<<8) + b;
				colors[grad*colors_per_gradient+c] = Color.rgb(r,g,b);
			}
		}
		//include the last gradent color
		r  = Spectrum[Spectrum.length-1][0];
		g  = Spectrum[Spectrum.length-1][1];
		b  = Spectrum[Spectrum.length-1][2];
		//colors[colors.length-2] = (r<<16) + (g<<8) + b;
		colors[colors.length-2] = Color.rgb(r,g,b);
		//the last iteration is set to the set color
		//colors[colors.length-1] = (set_color[0]<<16) + (set_color[1]<<8) + set_color[2];
		colors[colors.length-1] = Color.rgb(set_color[0],set_color[1],set_color[2]);
		
		WritableImage img = new WritableImage(x_pixels,y_pixels);
		PixelWriter pixelWriter = img.getPixelWriter();
		
		System.out.println("Calculating...");
		System.out.print("0%\r");
		for(int x=0; x<x_pixels; x++){
			System.out.print(100*x/x_pixels + "%\r");
			for(int y=0; y<y_pixels; y++){
				int my_count;
				if(!julia){
					my_count = Converge(PixelsToComplex(x,y))-iteration_color_start;
				}else{
					my_count = JuliaConverge(PixelsToComplex(x,y))-iteration_color_start;
				}
				if(my_count<0){my_count = 0;}
				//System.out.println(colors[my_count].toString());
				pixelWriter.setColor(x,y,colors[my_count]);
				//img.setRGB(x,y,colors[my_count]);
			}
		}
		System.out.println("100%");
		return img;
	}

	public static void main(String args[]){
		Mandelbrot mandelbrot = new Mandelbrot();
		
		//Parse args
/***************************************************************
	Argument list
	Mandelbrot [-x xcenter] [-y ycenter] [-w width] [-i iteration-limit iteration-color-start] [-r resolution] [file]
		-x		the real coordinate of the center of the image
		-y 		the imaginary coordinate of the center of the image
		-w 		the width of the viewing window
		-i		the iteration limit specifies the nubmer of iterations before the point is considered in the set, while the color start indicates the iteration number where the color spectrum begins
		file	the name of the file. Defaults to "image.png" if not specified
		-j 		specifies the c value of a julia set and generates a juila set instead. Take two floating point args: real and immaginary of the c value.
		-color 		specifies the color spectrum found in spectrums.txt
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
							mandelbrot.x_center = Double.parseDouble(args[argindex+1]);
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
							mandelbrot.y_center = Double.parseDouble(args[argindex+1]);
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
							mandelbrot.setWidth(Double.parseDouble(args[argindex+1]));
							//x_width = Double.parseDouble(args[argindex+1]);
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
							mandelbrot.iteration_limit = Integer.parseInt(args[argindex+1]);
							mandelbrot.iteration_color_start = Integer.parseInt(args[argindex+2]);
						}catch(NumberFormatException e){
							System.err.println("Error: -i needs two integer arguments.");
							return;
						}
					}
					if(mandelbrot.iteration_limit<=mandelbrot.iteration_color_start){
						System.err.println("Error: iteration limit must be larger than the color start.");
						return;
					}
					argindex += 3;
					break;
				case "-j":
					if(argindex+2>=args.length){
						System.err.println("Error: -j flag needs two floating point values.");
						return;
					}else{
						try{
							mandelbrot.setJulia(Double.parseDouble(args[argindex+1]),Double.parseDouble(args[argindex+2]));
							mandelbrot.julia = true;
						}catch(NumberFormatException e){
							System.err.println("Error: -j needs two floating point arguments.");
							return;
						}
					}
					argindex += 3;
					break;
				/*case "-r":
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
					break;*/
				case "-c":
					if(argindex+1>=args.length){
						System.err.println("Error: -c flag needs string value.");
						return;
					}else{
						try{
							mandelbrot.setSpectrum(args[argindex+1]);
						}catch(IllegalArgumentException e){
							System.err.println("Error: the color parameter cannot be found in the list of spectrums, or the spectrum text is improperly formatted.");
						}catch(FileNotFoundException e){
							System.err.println("Error: the file spectrums.txt was not found.");
						}catch(IOException e){
							System.err.println("Error: there was an unexpected error reading from the file spectrums.txt");
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
		
		BufferedImage img = SwingFXUtils.fromFXImage(mandelbrot.generateImage(), null);
		
		File f = new File(name + ".png");
		try{
			ImageIO.write(img, "PNG", f);
		}catch(Exception e){
			System.out.println("Failed to write file.");
		}
	}
}
