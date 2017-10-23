package Lab1;

public class Element {
    public enum TransformationType {
    Default, Simple, Polar, newZCoordinate
}
    public double x, y, z; //координаты
    public double trueX, trueY;
    public double r, fi;
    public int trueClass, SupposedClass;
    //настоящий класс, класс, информация о котором вычислена при помощи
    // расстояния Манхэттена и квартической функции ядра; при помощи расстояния
    // Евклида и квартической функции ядра; при помощи расстояния Манхэттена и
    // Гауссианской функции ядра; при помощи расстояния Евклида и Гауссианской функции ядра

    public double distance;
    // расстояние Манхэттена, расстояние Евклида, значение квартичной функции при расстоянии
    // Манхэттена, значение квартичной функции при расстоянии Евклида, значение Гауссовской
    // функции при расстоянии Манхэттена, значение Гауссовской функции при расстоянии Евклида

    public Element(double InX, double InY, int C) {
        x = InX;
        trueX = InX;
        y = InY;
        trueY = InY;
        trueClass = C;
        distance = Double.MAX_VALUE;
        //дабы элементы, данные значения для которых не считались, попали в конец
    }



    public void DefaultTransformation(){
        this.x = this.trueX;
        this.y = this.trueY;
    }
    public void SimpleTransformation(){
        this.x = 4*this.trueX+ 5*this.trueY;
        this.y = 9*this.trueX- 3*this.trueY;
    }
    public void PolarTransformation(){
        this.r = Math.pow(Math.pow(this.trueX, 2) +Math.pow(this.trueY, 2), 0.5);

        this.fi = Math.atan(this.trueY/this.trueX);
    }

    public void NewCoordinate(){
        this.z = trueX+trueY;
        x=trueX;
        y=trueY;
    }



    public static double XYMinkovsky_Distance(Element x, Element y, int p) {
        //расстояние Минковского. Классическая формулка из лекций
        return Math.pow((Math.pow(Math.abs(x.x - y.x), p) + Math.pow(Math.abs(x.y - y.y), p)), (1 / p));
    }
    public static double XYZMinkovsky_Distance(Element x, Element y, int p) {
        //расстояние Минковского. Классическая формулка из лекций
        return Math.pow((Math.pow(Math.abs(x.x - y.x), p) + Math.pow(Math.abs(x.y - y.y), p)+(Math.pow(Math.abs(x.z - y.z), p))), (1 / p));
    }

    public static double Polar_Minkovsky_Distance(Element x, Element y, int p){
        return Math.pow((Math.pow(Math.abs(x.r - y.r), p) + Math.pow(Math.abs(x.fi - y.fi), p)), (1 / p));
    }
}
