This is a java program for generating images of the mandelbrot set. It supports both a command line 
interface and a GUI. 



IMAGE
Drag a box to zoom in on a specific area of the set. Note that there is no visible response from the 
program until the calculations are complete. 



Center, Window, and Aspect
These images are colorings of sections of the complex plane generated from the Mandelbrot Set or Julia
Sets. The window is generated from the center coordinate (real-x and imaginary-y) along with a Width
and either a heigth or aspect ratio. With the Aspect box checked, generating a new image will override
the height parameter, using the width and aspect ratio to calculate an appropriate height.



Image
The images generated and displayed will follow the pixel dimentions specified here. As with the window
height, the Aspect box will override the y-pixel parameter based on the x-pixel parameter and the 
aspec ratio.



Iteration Limit
Essentially this defines the resolution of the image. If the set (often colored black) seems to have
jagged edges, try increasing the resolution. Note that this will afect the color distribution. Points
closer to the set are assigned colors toward the end of the pallet, while points farther away are
assigned colors toward the beginning of the pallet. Often the set itself will be colored black.

More technically, the Mandelbrot Set (or Julia Sets) is the set of points that generating a 
convergence sequence fromtheir iterative definition. Convergence is dificult to determine, so it is 
approximated. The index of the first element in the sequence that has a magnitude greater than 2, is 
the escape value of that point. If a point has an escape value greater than the iteration limit, then
it is assumed to be in the set and colored accordingly. The escape value of any given point (pixel) 
is used to determine its color.



Coloring
The coloring of each image is determined by a pallet, a scaling, and a "color start" parameter.
Every point in the image is either in the Mandelbrot (or Juila) Set or not. If it's not in the Set
then its escape index is used to gadge how "close" it is to the set. Each pallet defines a color
for the Set as well as a spectrum of colors for the points outside. Points farther from the Set are
given colors toward the beginning of the spectrum, while points closer to the set are given colors
toward the end of the spectrum. 

The Color Start parameter defines the iteration count where the
spectrum will start to color. Every point farther away than that limit will be colored the first
color in the spectrum (usually black). This can be used when zooming in, to ensure that all of the
colors in the pallet are included in the image.

Often with high iteration limits, colors toward the end of the pallet spectrum will not be as
prominent when the pallet is linearly scaled to the iteration count. The log scale will more
heavily weight the end of the spectrum so that the spectrum appears more evenly distributed in the
image.

The recolor button will recolor the image acording to the color parameters without recalculating
the set. This allows for quick experimenting with colorings.



Saving Images
The textbox will take full paths of files to save. If a relative path is given, it will save it
in the same place as the .jar or .class file the program was ran from. Note that this will currently
only save .PNG files, so it is advisable to give the file a .png file extension.
