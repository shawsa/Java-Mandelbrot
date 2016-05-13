This is a java program for generating images of the mandelbrot set.

NAME
	Mandelbrot - generates images of the mandelbrot set
	
SYNOPSIS
	Mandelbrot [-x xcenter] [-y ycenter] [-w width] 
		[-i iteration-limit iteration-color-start] [-r resolution] [file]

OPTIONS
	-x		the real coordinate of the center of the image
	
	-y 		the imaginary coordinate of the center of the image
	
	-w 		the width of the viewing window
	
	-i		the iteration limit specifies the nubmer of iterations before the 
				point is considered in the set, while the color start indicates 
				the iteration number where the color spectrum begins
				
	-r 		the resolution of the image. A resolution of 1 will give a standard 1920x1080 image
	
	file	the name of the file. Defaults to "image.png" if not specified
	
	-j 		specifies the c value of a julia set and generates a juila set instead. Take two floating point args:
				real and immaginary of the c value.