package Lab1;

/**
 * Created by Polina on 21.10.2017.
 */
public  class Kernels {
    public enum KernelType {
        Gaussian, Quartic, Triangle, Epanechnikov, Rectangular
    }



    public static double QuarticKernalFunction(double u) { //квартическая функция ядра. Подробнее: http://www.machinelearning.ru/wiki/index.php?title=%D0%9D%D0%B5%D0%BF%D0%B0%D1%80%D0%B0%D0%BC%D0%B5%D1%82%D1%80%D0%B8%D1%87%D0%B5%D1%81%D0%BA%D0%B0%D1%8F_%D1%80%D0%B5%D0%B3%D1%80%D0%B5%D1%81%D1%81%D0%B8%D1%8F:_%D1%8F%D0%B4%D0%B5%D1%80%D0%BD%D0%BE%D0%B5_%D1%81%D0%B3%D0%BB%D0%B0%D0%B6%D0%B8%D0%B2%D0%B0%D0%BD%D0%B8%D0%B5

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
