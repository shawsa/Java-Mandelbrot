public class Complex{
	public double r,i;
	Complex(double real, double imaginary){
		r = real;
		i = imaginary;
	}

	public Complex Add(Complex z){
		return new Complex(r+z.r,i+z.i);
		
	}

	public Complex Square(){
		return new Complex(r*r-i*i,2*r*i);
	}
	
	public double ModSquare(){
		return r*r+i*i;
	}

	public String toString(){
		return r + "+" + i + "i";
	}
}
