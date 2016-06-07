import java.awt.image.BufferedImage;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.*;
import javafx.scene.paint.Color;
import java.io.*;
import javax.imageio.ImageIO;
import java.lang.Math;
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
	int[] setColor = {0,0,0};
	int[][] values;
	
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
		values = new int[x_pixels][y_pixels];
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
				setColor[j] = Integer.parseInt(current_color[j]);
				if(setColor[j]<0 || setColor[j]>255){throw new IllegalArgumentException();}
			}
		}catch(Exception e){throw new IllegalArgumentException();}
	}
	
	public int Converge(Complex z){
		int i = 0;
		Complex w = z;
		while(w.ModSquare()<limit_square && i< iteration_limit){
			i++;
			w = w.Square().Add(z);
		}
		return i;
	}
	
	public int JuliaConverge(Complex z){
		int i = 0;
		Complex w = z;
		while(w.ModSquare()<limit_square && i< iteration_limit){
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
	
	public int[][] calculateValues(){
		int values[][] = new int[x_pixels][y_pixels];
		System.out.println("Calculating...");
		System.out.print("0%\r");
		if(!julia){
			for(int x=0; x<x_pixels; x++){
				System.out.print(100*x/x_pixels + "%\r");
				for(int y=0; y<y_pixels; y++){
					values[x][y] = Converge(PixelsToComplex(x,y));
				}
			}
		}else{
			for(int x=0; x<x_pixels; x++){
				System.out.print(100*x/x_pixels + "%\r");
				for(int y=0; y<y_pixels; y++){
					values[x][y] = JuliaConverge(PixelsToComplex(x,y));
				}
			}
		}
		System.out.println("100%");
		this.values = values;
		return values;
	}
	
	public WritableImage colorImage(String scale){
		int colorCount = iteration_limit - iteration_color_start;
		Color colors[] = new Color[colorCount + 1];
		//Set color
		colors[colorCount] = Color.rgb(setColor[0],setColor[1],setColor[2]);
		//Last color
		colors[colorCount-1] = Color.rgb(Spectrum[Spectrum.length-1][0], Spectrum[Spectrum.length-1][1],
				Spectrum[Spectrum.length-1][2]);
		/*
			Originally the iteration_limit was recalculated so that their was an equal number of
			iterations assigned to each color gradient and so that each end point of the gradient
			was included. This meant that the iteration limit was increased to a multiple of one
			less than the number of colors in the spectrum (the number of gradients between colors)
			plus one for the end point of the last gradient, plus one again for the set color.
			
			With that method the iteration_limit could change each time a spectrum with a different
			number of colors was applied. It is now a priorety to keep the iteration limit the same.
			We will instead sacrifice the criterion that each end point color must be hit. Since
			we are capable of using iteration limits that are significantly larger than the number
			of color endpoints in the spectrum it is the case that some itteration will come
			sufficiently close to each color endpoint.
		*/
		
		switch(scale){
			case "logarithmic":
				for(int i=0; i<colorCount; i++){
					//Map the colors to the log interval [0,#colors-1)
					double base = Math.pow(colorCount+1.0,1.0/(Spectrum.length-1));
					double myScale = Math.log(1.0+i)/Math.log(base);
					int grad = (int)myScale;
					double weight = myScale - grad;
					int r = (int)((1-weight) * Spectrum[grad][0] + weight * Spectrum[grad+1][0]);
					int g = (int)((1-weight) * Spectrum[grad][1] + weight * Spectrum[grad+1][1]);
					int b = (int)((1-weight) * Spectrum[grad][2] + weight * Spectrum[grad+1][2]);
					colors[i] = Color.rgb(r,g,b);
				}
				break;
			default:
				System.out.println("Error: " + scale + "is not a vaild scale parameter. Defaulting to linear.");
			case "linear":
				//
				for(int i=0; i<colorCount; i++){
					//Map the colors to the interval [0,#colors-1)
					double myScale = (1.0*i) * (Spectrum.length-1) / colorCount;
					int grad = (int)myScale;
					double weight = myScale - grad;
					int r = (int)((1-weight) * Spectrum[grad][0] + weight * Spectrum[grad+1][0]);
					int g = (int)((1-weight) * Spectrum[grad][1] + weight * Spectrum[grad+1][1]);
					int b = (int)((1-weight) * Spectrum[grad][2] + weight * Spectrum[grad+1][2]);
					colors[i] = Color.rgb(r,g,b);
				}
		}
		
		/*int colors_per_gradient = (iteration_limit - iteration_color_start)/(Spectrum.length-1);
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
		colors[colors.length-1] = Color.rgb(setColor[0],setColor[1],setColor[2]);
		*/
		
		WritableImage img = new WritableImage(x_pixels,y_pixels);
		PixelWriter pixelWriter = img.getPixelWriter();
		
		for(int x=0; x<x_pixels; x++){
			for(int y=0; y<y_pixels; y++){
				int my_count;
				my_count = values[x][y] - iteration_color_start;
				if(my_count<0){my_count = 0;}
				pixelWriter.setColor(x,y,colors[my_count]);
			}
		}
		return img;
	}
	
	public WritableImage generateImage(){
		calculateValues();
		return colorImage("logarithmic");
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
