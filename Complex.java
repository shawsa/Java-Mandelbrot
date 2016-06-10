public class Complex{
	public double r,i;
	Complex(double real, double imaginary){
		r = real;
		i = imaginary;
	}

	public Complex Add(Complex z){
		return new Complex(r+z.r,i+z.i);
	}
	public Complex sub(Complex z){
		return new Complex(r-z.r, i-z.i);
	}
	public Complex mult(Complex z){
		return new Complex(r*z.r - i*z.i, r*z.i + i*z.r);
	}
	private Complex inverse() throws ArithmeticException{
		double den = r*r + i*i;
		if(den==0){throw new ArithmeticException("Error, divide by zero.");}
		return new Complex(r/den,-1*i/den);
	}
	public Complex divide(Complex z) throws ArithmeticException{
		return this.mult(z.inverse());
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
