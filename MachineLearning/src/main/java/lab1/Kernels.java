package lab1;

public  class Kernels {
    public enum KernelType {
        Gaussian, Quartic, Triangle, Epanechnikov, Rectangular
    }



    public static double QuarticKernalFunction(double u) { //квартическая функция ядра.

        if (u <= 1) {

            return (double) 15 / 16 * Math.pow((1 - Math.pow(u, 2)), 2);
        } else return 0;
    }

    public static double GaussianKernalFunction(double u) { // функция ядра Гауса (где-то называласб Гауссианской функцией)
        return Math.pow((Math.PI * 2), -1 / 2) * Math.exp(-Math.pow(u, 2) / 2);
    }

    public static double TriangleKernelFunction (double u){
        if (u <= 1) {
            return (double)1-Math.abs(u);
        }
        else return 0;
    }
    public static double EpanechnikovKernelFunction (double u){
        if(u<=1) return 0.75*((double)1-Math.pow(u,2));
        else return 0;
    }
    public static double RectangularKernelFunction (double u){
        if(u<=1) return 0.5;
        else return 0;
    }

}
