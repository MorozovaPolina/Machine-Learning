package lab1.element;

import lab1.point.Point;

public class Element {
    public enum TransformationType {
    Default, Simple, Polar, newZCoordinate
}
    public Point point; //координаты
    public Point truePoint;
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

    public Element(double inX, double inY, int C) {
        this.point = new Point(inX, inY);
        this.truePoint = new Point(inX, inY);
        trueClass = C;
        distance = Double.MAX_VALUE;
        //дабы элементы, данные значения для которых не считались, попали в конец
    }



    public void DefaultTransformation(){
        this.point = truePoint;
    }
    public void SimpleTransformation(){
        this.point.x = 4 * this.truePoint.x + 5 * this.truePoint.y;
        this.point.y = 9 * this.truePoint.x - 3 * this.truePoint.y;
    }
    public void PolarTransformation(){
        this.r = Math.pow(Math.pow(this.truePoint.x, 2) + Math.pow(this.truePoint.y, 2), 0.5);

        this.fi = Math.atan(this.truePoint.y / this.truePoint.x);
    }

    public void NewCoordinate(){
        point.z = truePoint.x + truePoint.y;
        point.x = truePoint.x;
        point.y = truePoint.y;
    }



    public static double XYMinkovsky_Distance(Element el1, Element el2, int p) {
        //расстояние Минковского. Классическая формулка из лекций
        return Math.pow((Math.pow(Math.abs(el1.point.x - el2.point.x), p) +
                Math.pow(Math.abs(el1.point.y - el2.point.y), p)), (1 / p));
    }
    public static double XYZMinkovsky_Distance(Element el1, Element el2, int p) {
        //расстояние Минковского. Классическая формулка из лекций
        return Math.pow(Math.pow(Math.abs(el1.point.x - el2.point.x), p) +
                Math.pow(Math.abs(el1.point.y - el2.point.y), p)+
                Math.pow(Math.abs(el1.point.z - el2.point.z), p), (1 / p));
    }

    public static double Polar_Minkovsky_Distance(Element el1, Element el2, int p){
        return Math.pow((Math.pow(Math.abs(el1.r - el2.r), p) + Math.pow(Math.abs(el1.fi - el2.fi), p)), (1 / p));
    }
}
