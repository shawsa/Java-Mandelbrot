import java.awt.image.BufferedImage;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.*;
import javafx.scene.paint.Color;
import java.io.*;
import javax.imageio.ImageIO;
import java.lang.Math;
public class Mandelbrot{
	//default independent parameters
	String status = "";
	double x_center = -.25;
	double y_center = 0;
	double x_width = 4;
	double aspect = 16.0/9.0;
	int iteration_limit = 50;
	int iteration_color_start = 0;
	double limit_square = 4;
	//double res = .5;
	int[][] Spectrum = {{0,0,0},{255,0,0},{255,200,200}};
	int[] setColor = {0,0,0};
	int[][] values;
	
	boolean julia = false;
	boolean mobius = false;
	Complex A = new Complex(1,0);
	Complex B = new Complex(0,0);
	Complex C = new Complex(0,0);
	Complex D = new Complex(1,0);
	
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
	
	public String getStatus(){
		return this.status;
	}
	
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
	
	public void setJulia(double x, double y){julia_center = new Complex(x,y);}
	public void setMobiusA(double x, double y){A = new Complex(x,y);}
	public void setMobiusB(double x, double y){B = new Complex(x,y);}
	public void setMobiusC(double x, double y){C = new Complex(x,y);}
	public void setMobiusD(double x, double y){D = new Complex(x,y);}
	
	public String[] listSpectrums(){
		String[] ret = {"prism",
						"reds",
						"greens",
						"blues",
						"purples",
						"golds",
						"silvers",
						"reef",
						"glacier",
						"fire",
						"treasure",
						"soft",
						"grue",
						"gray-scale",
						"stripes"};
		return ret;
	}
	
	public void setSpectrum(String arg) throws IllegalArgumentException{
		switch(arg){
			case "reds": 
				Spectrum = new int[][]{{10,0,0},{ 255,0,0},{ 255,200,200}}; 
				setColor = new int[]{0,0,0}; 
				break;
			case "greens": Spectrum = new int[][]{{0,10,0},{ 0,255,0},{ 200,255,200}};
				setColor = new int[]{0,0,0};
				break;
			case "blues": Spectrum = new int[][]{{0,0,10},{ 0,0,255},{ 200,200,255}};
				setColor = new int[]{0,0,0};
				break;
			case "reef": Spectrum = new int[][]{{0,0,10},{0,0,255},{0,255,255},{0,255,0},{255,0,255},{255,200,255}};
				setColor = new int[]{0,0,0};
				break;
			case "prism": Spectrum = new int[][]{{0,0,0},{255,0,0},{255,255,0},{0,255,0},{0,255,255},{0,0,255}};
				setColor = new int[]{0,0,0};
				break;
			case "glacier": Spectrum = new int[][]{{200,200,255},{0,0,255},{200,200,255}};
				setColor = new int[]{0,0,0};
				break;
			case "fire": Spectrum = new int[][]{{50,0,0},{150,0,0},{150,50,0},{0,0,150}};
				setColor = new int[]{0,0,0};
				break;
			case "purples": Spectrum = new int[][]{{10,0,10},{255,0,255,255,200,255}};
				setColor = new int[]{0,0,0};
				break;
			case "golds": Spectrum = new int[][]{{0,0,0},{255,150,0},{255,255,255}};
				setColor = new int[]{0,0,0};
				break;
			case "silvers": Spectrum = new int[][]{{0,0,0},{255,255,255},{200,200,255}};
				setColor = new int[]{0,0,0};
				break;
			case "treasure": Spectrum = new int[][]{{0,0,0},{255,150,0},{255,255,255}};
				setColor = new int[]{200,200,200};
				break;
			case "soft": Spectrum = new int[][]{{99,192,255},{107,255,153},{255,171,45}};
				setColor = new int[]{0,0,0};
				break;
			case "grue": Spectrum = new int[][]{{0,0,0},{0,0,255},{0,255,0},{0,255,255}};
				setColor = new int[]{0,0,0};
				break;
			case "gray-scale": Spectrum = new int[][]{{10,10,10},{255,255,255}};
				setColor = new int[]{0,0,0};
				break;
			case "stripes": Spectrum = new int[][]{{0,0,0},{0,0,0},{255,255,255},{255,255,255},{0,0,0},{0,0,0},{255,255,255}};
				setColor = new int[]{0,0,0};
				break;
			default:
				throw new IllegalArgumentException();
		}
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
		Complex ret = new Complex(re,im);
		if(mobius){
			try{
				//Find the number that the mobius transform would map to this pixel point
				ret = (B.sub(D.mult(ret))).divide((C.mult(ret)).sub(A));
			}catch(ArithmeticException e){
				ret = new Complex(100000,100000);
				System.out.println("Divide by Zero");
			}
		}
		return ret;
	}
	
	public int[][] calculateValues(){
		int values[][] = new int[x_pixels][y_pixels];
		System.out.println("Calculating...");
		System.out.print("0%\r");
		this.status = "0%";
		if(!julia){
			for(int x=0; x<x_pixels; x++){
				this.status = 100*x/x_pixels + "%";
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
		this.status = "100%";
		this.values = values;
		return values;
	}
	
	public String[] listScales(){
		String[] ret = {"linear",
						"logarithmic",
						"average"};
		return ret;
	}
	
	public WritableImage colorImage(String scale){
		int colorCount = iteration_limit - iteration_color_start;
		Color colors[] = new Color[colorCount + 1];
		//Set color
		colors[colorCount] = Color.rgb(setColor[0],setColor[1],setColor[2]);
		//Last color
		colors[colorCount-1] = Color.rgb(Spectrum[Spectrum.length-1][0], Spectrum[Spectrum.length-1][1],
				Spectrum[Spectrum.length-1][2]);
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
			case "average":
				//count pixels of each value
				int[] bins = new int[colorCount+1];
				for(int x=0; x<x_pixels; x++){
					for(int y=0; y<y_pixels; y++){
						int shiftedValue = values[x][y] - iteration_color_start;
						if(shiftedValue >= 0){
							bins[values[x][y] - iteration_color_start]++;
						}
					}
				}
				int totalColored = 0; // = x_pixels*y_pixels - bins[bins.length-1];
				for(int i=0; i<bins.length-1; i++){
					totalColored += bins[i];
				}
				
				//last color is the last iteration is already assigned
				int pixelsPerGradient = (totalColored)/(Spectrum.length-1);
				//for each gradient assign colors
				//calculate value ranges for each gradient
				int[] spectrumStarts = new int[Spectrum.length-1];
				int startValue = 0;
				int endValue = 0;
				int rangeTotal = 0;
				for(int grad = 0; grad < Spectrum.length-2; grad++){
					startValue = endValue;
					endValue = startValue;
					//System.out.println(endValue);
					//rangeTotal = 0;
					while(rangeTotal <= pixelsPerGradient * (grad+1)){
						rangeTotal += bins[endValue];
						endValue++;
					}
					spectrumStarts[grad] = startValue;
				}
				//last gradient gets remaining colors
				spectrumStarts[spectrumStarts.length-1] = endValue;
				//Assign colors for each gradient
				for(int grad = 0; grad < Spectrum.length-1; grad++){
					if(grad < Spectrum.length-2){rangeTotal = spectrumStarts[grad+1] - spectrumStarts[grad];}
					else{rangeTotal = colorCount -1 - spectrumStarts[grad];}
					//System.out.println("Spectrum " + grad + " start " + spectrumStarts[grad] + " total " + rangeTotal);
					for(int i=0; i < rangeTotal; i++){
						double weight = 1.0 * i / (rangeTotal);
						int r = (int)((1-weight) * Spectrum[grad][0] + weight * Spectrum[grad+1][0]);
						int g = (int)((1-weight) * Spectrum[grad][1] + weight * Spectrum[grad+1][1]);
						int b = (int)((1-weight) * Spectrum[grad][2] + weight * Spectrum[grad+1][2]);
						colors[spectrumStarts[grad] + i] = Color.rgb(r,g,b);
					}
				}
				
				
				/*Debugging
				for(int i=0; i<bins.length; i++){
					System.out.println(i +": " + bins[i]);
				}
				System.out.println("Total Colored: " + totalColored);
				System.out.println("Pixels per gradient: " + pixelsPerGradient);
				for(int i=0; i<spectrumStarts.length; i++){
					System.out.println("Spectrum " + i + ": " + spectrumStarts[i]);
				}*/
				
				break;
			default:
				System.out.println("Error: " + scale + "is not a vaild scale parameter. Defaulting to linear.");
			case "linear":
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
		//this exists as legacy code for the command line program
		calculateValues();
		return colorImage("linear");
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
						/*}catch(FileNotFoundException e){
							System.err.println("Error: the file spectrums.txt was not found.");
						}catch(IOException e){
							System.err.println("Error: there was an unexpected error reading from the file spectrums.txt");*/
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
