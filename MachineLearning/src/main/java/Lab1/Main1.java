package Lab1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Main1 {

    public static List<Element> Elements, TestElements, LearningElements, CheckElements, CheckLearningElements; //весь набор исходных элементов, элементы, на которых будет тестировать, элементы для обучения, элементы для проверки (они же те, которые выбраны для тестирования)
    public static int NumberOfLines, NumberOfFolders, Row, k, TestElementsNumber; //количесвто элементов, количество фолдеров для кросс-валидации, количество соседей, количество элементов тестовой выборки
    public static Comparator<Element> DistanceComparator;
    public static Comparator<Measures> MeasuresComparator;
    public static Comparator<Q> QComparator;
    public static ArrayList<Q> QS;

    public static class Q {
        KernelType kernel;
        double Q, Accuricy, Fmeas;
        int p;
        int numberOfNeibours;
        public Q(KernelType kern, double Q, int p, double Acc, double F){
            this.kernel = kern;
            Accuricy=Acc;
            Fmeas=F;
            this.Q=Q;
            this.p=p;
            numberOfNeibours=k;
        }
    }

    public static class Measures {
        public int numberOfNeibours;
        public KernelType kernel;
        public int p;
        public double L;
        public double Accuracy, FMeasure;

        public Measures(int p, KernelType kern, double acc, double FM, double L) {
            this.p = p;
            this.L = L;
            kernel = kern;
            numberOfNeibours = k;
            Accuracy = acc;
            FMeasure = FM;
        }

    }

    public enum KernelType {
        Gaussian, Quartic
    }

    public static class Element {
        public float x, y; //координаты
        public int trueClass, SupposedClass; //настоящий класс, класс, информация о котором вычислена при помощи расстояния Манхэттена и квартической функции ядра; при помощи расстояния Евклида и квартической функции ядра; при помощи расстояния Манхэттена и Гауссианской функции ядра; при помощи расстояния Евклида и Гауссианской функции ядра

        public double distance; // расстояние Манхэттена, расстояние Евклида, значение квартичной функции при расстоянии Манхэттена, значение квартичной функции при расстоянии Евклида, значение Гауссовской функции при расстоянии Манхэттена, значение Гауссовской функции при расстоянии Евклида

        public Element(float InX, float InY, int C) {
            x = InX;
            y = InY;
            trueClass = C;
            distance = Double.MAX_VALUE; //дабы элементы, данные значения для которых не считались, попали в конец
        }
    }

    public static double Minkovsky_Distance(Element x, Element y, int p) { //расстояние Минковского. Классическая формулка из лекций
        return Math.pow((Math.pow(Math.abs(x.x - y.x), p) + Math.pow(Math.abs(x.y - y.y), p)), (1 / p));
    }

    public static double QuarticKernalFunction(double u) { //квартическая функция ядра. Подробнее: http://www.machinelearning.ru/wiki/index.php?title=%D0%9D%D0%B5%D0%BF%D0%B0%D1%80%D0%B0%D0%BC%D0%B5%D1%82%D1%80%D0%B8%D1%87%D0%B5%D1%81%D0%BA%D0%B0%D1%8F_%D1%80%D0%B5%D0%B3%D1%80%D0%B5%D1%81%D1%81%D0%B8%D1%8F:_%D1%8F%D0%B4%D0%B5%D1%80%D0%BD%D0%BE%D0%B5_%D1%81%D0%B3%D0%BB%D0%B0%D0%B6%D0%B8%D0%B2%D0%B0%D0%BD%D0%B8%D0%B5

        if (u <= 1) {

            // System.out.println(u+ " "+ 15 / 16 * Math.pow((1 - Math.pow(u, 2)), 2));
            return (double) 15 / 16 * Math.pow((1 - Math.pow(u, 2)), 2);
        } else return 0;
    }

    public static double GaussianKernalFunction(double u) { // функция ядра Гауса (где-то называласб Гауссианской функцией)
        return Math.pow((Math.PI * 2), -1 / 2) * Math.exp(-Math.pow(u, 2) / 2);
    }

    public static void Cross_Validation() { //кросс-валидация
        LearningElements = new ArrayList<>(); //создаем новую ссылка на обучающую выборку, дабы значения из предыдущих шагов не попали к нам
        TestElements = new ArrayList<>(); //аналогично
        if (Row != NumberOfFolders) { //если не последняя проверка. Т.к., поскольку количество элементов не делится нацело на количество фолдеров для кросс-валидации, в последний фолдер попадает меньше значений. Это хорошо бы отслеживать
            List<Element> FirstHelpLearning = Elements.subList(0, NumberOfLines - Row * TestElementsNumber); //вытаскиваем те элементы, которые находятся в выборке до тех элементов, которые мы оставляем для тестирования
            List<Element> TestHelp = Elements.subList(NumberOfLines - Row * TestElementsNumber, NumberOfLines - (Row - 1) * TestElementsNumber); // элементы для тестирования
            TestElements.addAll(TestHelp); //добавляем. Если делать так, то джава не ругается
            if (Row != 1) { //если мы будем тестировать алгоритм не на последних значениях, то есть есть еще значения, которые нужно добавить в обучающую выборку
                List<Element> helplist = Elements.subList(NumberOfLines - (Row - 1) * TestElementsNumber, NumberOfLines);
                LearningElements.addAll(helplist);
            }
            LearningElements.addAll(FirstHelpLearning);
        } else { //последний фолдер для кросс-валидации
            int AlreadySeenElements = (Row - 1) * TestElementsNumber; //те элементы, на которых мы уже тестировали
            List<Element> TestHelp = Elements.subList(0, NumberOfLines - AlreadySeenElements); //те элементы, на которых будем
            TestElements.addAll(TestHelp);
            List<Element> FirstHelpLearning = Elements.subList(NumberOfLines - AlreadySeenElements, NumberOfLines);
            LearningElements.addAll(FirstHelpLearning);
        }
        ListIterator<Element> LearningIt = LearningElements.listIterator(0);
        while (LearningIt.hasNext()) { //для всей обучающей выборки присвоим значения для классов для всех рассматирваемых функций ядер и расстояний, равные настоящему значению, полученному нами изначально
            Element e = LearningIt.next();
            e.SupposedClass = e.trueClass;
        }

        CheckElements = new ArrayList<Element>(); //очищаем массив, который будем использовать для проверки
        CheckElements.addAll(TestElements);//добавляем тестовые элементы
        CheckLearningElements = new ArrayList<>();
        CheckLearningElements.addAll(LearningElements);
    }

    public static void kNN(KernelType kernel, int p) {
        ListIterator<Element> TestIt = TestElements.listIterator(0);
        while (TestIt.hasNext()) {//для каждого элемента из тестовой выборки считаем расстояния Евклида и Манхэттена до всех элементов из обучающей выборки
            Element t = TestIt.next();
            ListIterator<Element> LearningIt = LearningElements.listIterator(0);
            while (LearningIt.hasNext()) {
                Element e = LearningIt.next();
                e.distance = Minkovsky_Distance(e, t, p);
            }

            LearningElements.sort(DistanceComparator);//сортируем обучающую выборку по длине расстояния(наименьшая длина в начало)

            double QuantityOfAClass = 0;// класс 0
            double QuantityOfBClass = 0;// класс 1
            LearningIt = LearningElements.listIterator(0);
            int numberOfNeibours = 0;//сколько соседий уже просмотрели
            while (LearningIt.hasNext() && numberOfNeibours != k) {
                Element e = LearningIt.next();
                double w = 0;
                switch (kernel) {
                    case Gaussian: {
                        w = GaussianKernalFunction(e.distance / LearningElements.get(numberOfNeibours + 1).distance);
                        break;
                    }
                    case Quartic: {
                        w = QuarticKernalFunction(e.distance / LearningElements.get(numberOfNeibours + 1).distance);
                        //        System.out.println(kernel+ " "+w+" "+e.distance+" "+LearningElements.get(numberOfNeibours + 1).distance+" "+ e.distance / LearningElements.get(numberOfNeibours + 1).distance);
                        break;
                    }
                }
                // System.out.println(kernel+" "+e.distance+" "+LearningElements.get(numberOfNeibours + 1).distance+" "+w+ " "+ e.distance / LearningElements.get(numberOfNeibours + 1).distance+ " "+ " "+  QuarticKernalFunction(e.distance / LearningElements.get(numberOfNeibours + 1).distance) +" "+ GaussianKernalFunction(e.distance / LearningElements.get(numberOfNeibours + 1).distance) );
                // System.out.println(w);
                if (e.SupposedClass == 0)
                    QuantityOfAClass += w; //еси класс соседа - 0, то увеличиваем счетчик количества соседий с классом 0
                else QuantityOfBClass += w; // иначе увеличиваем счетчик соседей с классом 1
                numberOfNeibours++;
            }
            if (QuantityOfAClass > QuantityOfBClass)
                t.SupposedClass = 0; // если соседей с классом 0 больше, то это наш класс
            else t.SupposedClass = 1; // иначе нам подойдет класс 1
            LearningElements.add(t);
            //  System.out.println(QuantityOfAClass + " " + QuantityOfBClass + " " + t.SupposedClass);
        }
    }

    public static double[] FMeasure() { //accuracy, F-measure
        ListIterator<Element> CheckingIt = CheckElements.listIterator(); //идем по массиву проверочных элементов
        double TP = 0;//действительно положительные
        double FP = 0;//ложно положительные
        double TN = 0;//действительно отриательные
        double FN = 0;//ложно отрицательные
        while (CheckingIt.hasNext()) {
            Element e = CheckingIt.next();
            if (e.trueClass == 0) {
                if (e.SupposedClass == 0) TN++;
                else FP++;
            } else {
                if (e.SupposedClass == 0) FN++;
                else TP++;
            }

        }

        double[] result = new double[3];
        double P = TP + FN;
        double N = FP + TN;
        double L = FN + FP;
        System.out.println("TP " + TP + " FN " + FN + " FP " + FP + " TN " + TN + " P " + P + " N " + N);
        double Recall;
        if (P == 0) Recall = 1;
        else Recall = TP / P;
        double Precision;
        if (TP + FP == 0) Precision = 1;
        else Precision = TP / (TP + FP);
        // System.out.println("Recall " + Recall + " Pecision " + Precision);
        double Accuracy = (TP + TN) / (P + N);
        double F = 2 * Precision * Recall / (Precision + Recall);
        result[0] = Accuracy;
        result[1] = F;
        result[2] = L;
        return result;
    }


    public static void main(String[] args) throws IOException {
        BufferedReader infile = new BufferedReader(new FileReader("infile.in"));
        QS = new ArrayList<Q>();
        NumberOfLines = 0;
        NumberOfFolders = 10;
        Elements = new ArrayList<Element>();
        ArrayList<Measures> results = new ArrayList<>();
        k = 2;
        createComparators();


        while (true) {
            String InString = infile.readLine();
            if (InString == null) break;
            NumberOfLines++;
            String[] InStringArray = InString.split(",");
            Elements.add(new Element(Float.valueOf(InStringArray[0]), Float.valueOf(InStringArray[1]), Integer.valueOf(InStringArray[2])));
        }
        Collections.shuffle(Elements);

        for (k = 5; k < 15; k++) {
            Row = 0;
            TestElementsNumber = (int) Math.ceil((double) NumberOfLines / NumberOfFolders);
            for (int p = 1; p < 3; p++) {
                for (KernelType kernel : KernelType.values()) {
                    Row = 0;
                    double L = 0;
                    double Accuricy =0;
                    double F=0;
                    for (int i = 0; i < NumberOfFolders; i++) {
                        Row++;
                        System.out.println(Row);

                        Cross_Validation();
                        kNN(kernel, p);
                        double[] Fmeas = FMeasure();
                        Accuricy+=Fmeas[0];
                        F+=Fmeas[1];
                        L += Fmeas[2];
                        results.add(new Measures(p, kernel, Fmeas[0], Fmeas[1], Fmeas[2]));

                    }
                    QS.add(new Q(kernel, L/NumberOfFolders, p, Accuricy/NumberOfFolders, F/NumberOfFolders));
                }

            }
        }
       // results.sort(MeasuresComparator);
        MyChart chart = new MyChart();

//        Measures Mes = results.get(0);
        QS.sort(QComparator);
        Q best = QS.get(0);
        //System.out.println("k=" + Mes.numberOfNeibours + ", p=" + Mes.p + ", kernel = " + Mes.kernel + ", Accuracy = " + Mes.Accuracy + ", F-measure = " + Mes.FMeasure);
        System.out.println("k=" + best.numberOfNeibours+ ", p=" + best.p + ", kernel = " + best.kernel + ", Acc = "+ best.Accuricy+ ", F-measure "+ best.Fmeas );

        Cross_Validation();
        kNN(best.kernel, best.p);
        chart.drawIt(CheckLearningElements, TestElements);



    }

    public static void createComparators() {
        DistanceComparator = new Comparator<Element>() {
            @Override
            public int compare(Element o1, Element o2) {
                double difference = o1.distance - o2.distance;
                if (difference > 0) return 1;
                else if (difference == 0) return 0;
                else
                    return -1;
            }

        };

        MeasuresComparator = new Comparator<Measures>() {
            @Override
            public int compare(Measures o1, Measures o2) {
                if (o1.FMeasure > o2.FMeasure) return -1;
                else if (o1.FMeasure < o2.FMeasure) return 1;
                else if (o1.Accuracy > o2.Accuracy) return -1;
                else if (o1.Accuracy < o2.Accuracy) return -1;
                else return 0;
            }
        };

        QComparator = new Comparator<Q>() {
            @Override
            public int compare(Q o1, Q o2) {
                if(o1.Accuricy > o2.Accuricy) return 1;
                else if(o1.Accuricy < o2.Accuricy) return -1;
                else if (o1.Fmeas> o2.Fmeas) return 1;
                else if (o1.Fmeas <o2.Fmeas) return -1;
                else return 0;
            }
        };
    }


}
