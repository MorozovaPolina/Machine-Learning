package lab1;

import lab1.element.Element;
import lab1.measures.Measures;
import lab1.mychart.MyChart;
import lab1.Kernels.*;
import static lab1.Kernels.*;
import static lab1.element.Element.*;
import static lab1.outfilewriting.OutfileWriting.writeOut;

import lab1.kdtree.KDTree;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;




public class Main1 {

    public static List<Element> Elements, LearningElements, CheckElements, CheckLearningElements;
    //весь набор исходных элементов, элементы, на которых будет тестировать,
    // элементы для обучения, элементы для проверки (они же те, которые выбраны для тестирования)

    public static int NumberOfLines, NumberOfFolders, Row, k, TestElementsNumber;
    //количесвто элементов, количество фолдеров для кросс-валидации, количество соседей,
    // количество элементов тестовой выборки

    public static Comparator<Element> DistanceComparator;
    public static Comparator<Measures.Q> QComparator;
    public static ArrayList<Measures.Q> QS;



    public static List<Element> CrossValidation(List<Element> TestElements) {
        LearningElements = new ArrayList<>();
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
                if(transformation == TransformationType.Polar) e.distance = Polar_Minkovsky_Distance(e, t, p);

                else if(transformation != TransformationType.newZCoordinate)
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
        QS = new ArrayList<Measures.Q>();
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
        for (TransformationType transformation : TransformationType.values()) {
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

            for (NumberOfFolders = 3; NumberOfFolders < 10; NumberOfFolders++) {
                TestElementsNumber = (int) Math.floor((double) NumberOfLines / NumberOfFolders);
                for (k = 5; k < 15; k++) {
                    for (int p = 1; p < 4; p++) {
                        for (KernelType kernel : KernelType.values()) {
                            Row = 0;
                            double L = 0;
                            double Accuricy = 0;
                            double F = 0;
                            for (int i = 0; i < NumberOfFolders; i++) {
                                Row++;
                              //  System.out.println(Row);
                                TestElements = Cross_Validation(TestElements);
                                tree = new KDTree()
                                kNN(TestElements, kernel, p, transformation);
                                double[] Fmeas = Measures.FMeasure(CheckElements);
                                Accuricy += Fmeas[0];
                                F += Fmeas[1];
                                L += Fmeas[2];

                            }
                            QS.add(new Measures.Q(kernel, L / NumberOfFolders, p, Accuricy / NumberOfFolders, F / NumberOfFolders, NumberOfFolders, transformation, k));
                        }

                    }
                }
            }
        }
        QS.sort(QComparator);
        writeOut(QS);
        MyChart chart = new MyChart();

        Measures.Q best = QS.get(0);
        System.out.println("k=" + best.numberOfNeibours + ", NumberOfFolders = "+ best.numberOfFolders+", " +
                "p=" + best.p + ", kernel = " + best.kernel + ", Transformation = "+best.transfromation+", " +
                "Acc = " + best.Accuricy + ", F-measure " + best.Fmeas);
        System.out.println(QS.size());
        NumberOfFolders = best.numberOfFolders;
        k=best.numberOfNeibours;
        Row=0;
        List<Element> ToDraw = new ArrayList<>();
        TestElementsNumber = (int) Math.floor((double) NumberOfLines / NumberOfFolders);
        for(int i=0;i<NumberOfFolders; i++) {
            Row++;
            TestElements =Cross_Validation(TestElements);
            kNN(TestElements, best.kernel, best.p, best.transfromation);
            ListIterator ToDrawIterator = TestElements.listIterator();
            ToDraw.addAll(TestElements);
        }
        chart.drawIt(ToDraw);


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


        QComparator = new Comparator<Measures.Q>() {
            @Override
            public int compare(Measures.Q o1, Measures.Q o2) {
                if (o1.Fmeas < o2.Fmeas) return 1;
                else if (o1.Fmeas > o2.Fmeas) return -1;
                else if (o1.Accuricy < o2.Accuricy) return 1;
                else if (o1.Accuricy > o2.Accuricy) return -1;
                else return 0;
            }
        };
    }


}
