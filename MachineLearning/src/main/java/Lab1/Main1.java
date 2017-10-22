package Lab1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


import static Lab1.Kernels.*;
import static Lab1.Measures.*;
import static Lab1.OutfileWriting.*;
import static Lab1.Element.*;

public class Main1 {

    public static List<Element> Elements, LearningElements, CheckElements, CheckLearningElements;
    //весь набор исходных элементов, элементы, на которых будет тестировать,
    // элементы для обучения, элементы для проверки (они же те, которые выбраны для тестирования)

    public static int NumberOfLines, NumberOfFolders, Row, k, TestElementsNumber;
    //количесвто элементов, количество фолдеров для кросс-валидации, количество соседей,
    // количество элементов тестовой выборки

    public static Comparator<Element> DistanceComparator;
    public static Comparator<Q> QComparator;
    public static ArrayList<Q> QS;


    public static class Q {
        KernelType kernel;
        TransformationType transfromation;
        double Q, Accuricy, Fmeas;
        int p, numberOfFolders;
        int numberOfNeibours;

        public Q(KernelType kern, double Q, int p, double Acc, double F, int NOF, TransformationType transform) {
            this.kernel = kern;
            Accuricy = Acc;
            Fmeas = F;
            this.Q = Q;
            this.p = p;
            numberOfFolders = NOF;
            numberOfNeibours = k;
            transfromation=transform;
        }
    }


    public static double XYMinkovsky_Distance(Element x, Element y, int p) {
        //расстояние Минковского. Классическая формулка из лекций
        return Math.pow((Math.pow(Math.abs(x.x - y.x), p) + Math.pow(Math.abs(x.y - y.y), p)), (1 / p));
    }
    public static double XYZMinkovsky_Distance(Element x, Element y, int p) {
        //расстояние Минковского. Классическая формулка из лекций
        return Math.pow((Math.pow(Math.abs(x.x - y.x), p) + Math.pow(Math.abs(x.y - y.y), p)+(Math.pow(Math.abs(x.z - y.z), p))), (1 / p));
    }

    public static double PolarDistance(Element x, Element y){
        return Math.sqrt(Math.pow(x.r, 2)+ Math.pow(y.r,2)-2*x.r*y.r*Math.cos(x.fi*y.fi));
    }


    public static List<Element> Cross_Validation(List<Element> TestElements) { //кросс-валидация
        LearningElements = new ArrayList<>();
        //    System.out.println(Row+" " + NumberOfFolders +" "+ (Row != NumberOfFolders));
        //создаем новую ссылка на обучающую выборку, дабы значения из предыдущих шагов не попали к нам
        TestElements = new ArrayList<>();
        if (Row != NumberOfFolders) {
            //если не последняя проверка. Т.к., поскольку количество элементов не делится нацело на количество
            // фолдеров для кросс-валидации, в последний фолдер попадает меньше значений. Это хорошо бы отслеживать
            List<Element> FirstHelpLearning;
            List<Element> TestHelp;
            FirstHelpLearning = Elements.subList(0, NumberOfLines - Row * TestElementsNumber);
            TestHelp = Elements.subList(NumberOfLines - Row * TestElementsNumber, NumberOfLines - (Row - 1) * TestElementsNumber);

            //вытаскиваем те элементы, которые находятся в выборке до тех элементов, которые мы оставляем
            // для тестирования
            // элементы для тестирования
            TestElements.addAll(TestHelp); //добавляем. Если делать так, то джава не ругается
            if (Row != 1) {
                //если мы будем тестировать алгоритм не на последних значениях, то есть есть еще значения,
                // которые нужно добавить в обучающую выборку
                List<Element> helplist = Elements.subList(NumberOfLines - (Row - 1) * TestElementsNumber, NumberOfLines);
                LearningElements.addAll(helplist);
            }
            LearningElements.addAll(FirstHelpLearning);
        } else { //последний фолдер для кросс-валидации
            int AlreadySeenElements = (Row - 1) * TestElementsNumber;
            //те элементы, на которых мы уже тестировали
            List<Element> TestHelp = Elements.subList(0, NumberOfLines - AlreadySeenElements); //те элементы, на которых будем
            TestElements.addAll(TestHelp);
            List<Element> FirstHelpLearning = Elements.subList(NumberOfLines - AlreadySeenElements, NumberOfLines);
            LearningElements.addAll(FirstHelpLearning);
        }
        ListIterator<Element> LearningIt = LearningElements.listIterator(0);
        while (LearningIt.hasNext()) {
            //для всей обучающей выборки присвоим значения для классов для всех рассматирваемых функций ядер и
            // расстояний, равные настоящему значению, полученному нами изначально
            Element e = LearningIt.next();
            e.SupposedClass = e.trueClass;
        }

        CheckElements = new ArrayList<Element>(); //очищаем массив, который будем использовать для проверки
        CheckElements.addAll(TestElements);//добавляем тестовые элементы
        CheckLearningElements = new ArrayList<>();
        CheckLearningElements.addAll(LearningElements);
      //  System.out.println("CV Size " + TestElements.size());
        return TestElements;
    }

    public static void kNN(List<Element> TestElements, KernelType kernel, int p, TransformationType transformation) {
       // System.out.println("size " + TestElements.size());
        ListIterator<Element> TestIt = TestElements.listIterator(0);
        while (TestIt.hasNext()) {
            Element t = TestIt.next();
            ListIterator<Element> LearningIt = LearningElements.listIterator(0);
            while (LearningIt.hasNext()) {
                Element e = LearningIt.next();
                if(transformation==TransformationType.Polar)e.distance = PolarDistance(e, t);

                else if(transformation!=TransformationType.newZCoordinate)
                    e.distance = XYMinkovsky_Distance(e, t, p);
                else e.distance = XYZMinkovsky_Distance(e, t, p);
            }

            LearningElements.sort(DistanceComparator);

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
                        break;
                    }
                    case Epanechnikov: {
                        w = EpanechnikovKernelFunction(e.distance / LearningElements.get(numberOfNeibours + 1).distance);
                        break;
                    }
                    case Rectangular: {
                        w = RectangularKernelFunction(e.distance / LearningElements.get(numberOfNeibours + 1).distance);
                        break;
                    }
                    case Triangle: {
                        w = TriangleKernelFunction(e.distance / LearningElements.get(numberOfNeibours + 1).distance);
                        break;
                    }
                }

                if (e.SupposedClass == 0)
                    QuantityOfAClass += w;
                    //если класс соседа - 0, то увеличиваем счетчик количества соседий с классом 0
                else QuantityOfBClass += w;
                // иначе увеличиваем счетчик соседей с классом 1
                numberOfNeibours++;
            }
            if (QuantityOfAClass > QuantityOfBClass)
                t.SupposedClass = 0; // если соседей с классом 0 больше, то это наш класс
            else t.SupposedClass = 1; // иначе нам подойдет класс 1
            // LearningElements.add(t);
            //  System.out.println(QuantityOfAClass + " " + QuantityOfBClass + " " + t.SupposedClass);
        }
    }


    public static void main(String[] args) throws IOException {
        BufferedReader infile = new BufferedReader(new FileReader("infile.in"));
        List<Element> TestElements = new ArrayList<>();
        QS = new ArrayList<Q>();
        NumberOfLines = 0;
        NumberOfFolders = 10;
        Elements = new ArrayList<Element>();
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
        for (TransformationType transformation:TransformationType.values()) {
            switch (transformation){
                case Default:{
                    ListIterator<Element> transformIterator = Elements.listIterator();
                    while (transformIterator.hasNext()){
                        Element e = transformIterator.next();
                        e.DefaultTransformation();
                    }
                    break;
                }
                case Polar:{
                    ListIterator<Element> transformIterator = Elements.listIterator();
                    while (transformIterator.hasNext()){
                        Element e = transformIterator.next();
                        e.PolarTransformation();
                    }
                    break;
                }
                case Simple:{
                    ListIterator<Element> transformIterator = Elements.listIterator();
                    while (transformIterator.hasNext()){
                        Element e = transformIterator.next();
                        e.SimpleTransformation();
                    }
                    break;
                }
                case newZCoordinate:{
                    ListIterator<Element> transformIterator = Elements.listIterator();
                    while (transformIterator.hasNext()){
                        Element e = transformIterator.next();
                        e.NewCoordinate();
                    }
                    break;
                }
            }

        for (NumberOfFolders = 3; NumberOfFolders < 90; NumberOfFolders++) {
            TestElementsNumber = (int) Math.floor((double) NumberOfLines / NumberOfFolders);
            for (k = 5; k < 15; k++) {
                Row = 0;

                    for (int p = 1; p < 4; p++) {
                        if(p>1& transformation==TransformationType.Polar) break;
                        for (KernelType kernel : KernelType.values()) {
                            Row = 0;
                            double L = 0;
                            double Accuricy = 0;
                            double F = 0;
                            for (int i = 0; i < NumberOfFolders; i++) {
                                Row++;
                                TestElements = Cross_Validation(TestElements);
                                kNN(TestElements, kernel, p, transformation);
                                double[] Fmeas = FMeasure(CheckElements);
                                Accuricy += Fmeas[0];
                                F += Fmeas[1];
                                L += Fmeas[2];

                            }
                            QS.add(new Q(kernel, L / NumberOfFolders, p, Accuricy / NumberOfFolders, F / NumberOfFolders, NumberOfFolders, transformation));
                        }

                    }
                }
            }
        }
        QS.sort(QComparator);
        writeOut(QS);
        MyChart chart = new MyChart();

        Q best = QS.get(0);
        System.out.println("k=" + best.numberOfNeibours + ", NumberOfFolders = "+ best.numberOfFolders+", p=" + best.p + ", kernel = " + best.kernel + ", Transformation = "+best.transfromation+", Acc = " + best.Accuricy + ", F-measure " + best.Fmeas);
        System.out.println(QS.size());
        Cross_Validation(TestElements);
        kNN(TestElements, best.kernel, best.p, best.transfromation);
        chart.drawIt(LearningElements, TestElements);


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


        QComparator = new Comparator<Q>() {
            @Override
            public int compare(Q o1, Q o2) {
                if (o1.Fmeas < o2.Fmeas) return 1;
                else if (o1.Fmeas > o2.Fmeas) return -1;
                else if (o1.Accuricy < o2.Accuricy) return 1;
                else if (o1.Accuricy > o2.Accuricy) return -1;
                else return 0;
            }
        };
    }


}
