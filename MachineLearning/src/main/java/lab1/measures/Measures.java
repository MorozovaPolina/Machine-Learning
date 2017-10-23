package lab1.measures;

import java.util.List;
import java.util.ListIterator;

import lab1.element.Element;
import lab1.element.Element.*;
import lab1.Kernels;

/**
 * Created by Polina on 22.10.2017.
 */
public class Measures {
    public static class Q {
        public Kernels.KernelType kernel;
        public TransformationType transfromation;
        public double Q, Accuricy, Fmeas;
        public int p, numberOfFolders;
        public int numberOfNeibours;

        public Q(Kernels.KernelType kern, double Q, int p, double Acc, double F, int NOF, TransformationType transform, int k) {
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

    public static double[] FMeasure(List <Element> CheckElements) { //accuracy, F-measure
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

        double Recall = TP + FN == 0 ? 1 : ((TP / TP) + FN);
        double Precision = TP + FP == 0 ? 1 : TP / (TP + FP);

        double Accuracy = (TP + TN) / (TP + FN + FP + TN);
        double F = 2 * Precision * Recall / (Precision + Recall);
        if (Double.isNaN(F)) {
            //   System.out.println(Recall+" "+ Precision +" TP "+ TP+" FP "+ FP+" P "+ P+ " N "+ N);
            F = 0;
        }
        result[0] = Accuracy;
        result[1] = F;
        result[2] = FN + FP;
        return result;
    }
}
