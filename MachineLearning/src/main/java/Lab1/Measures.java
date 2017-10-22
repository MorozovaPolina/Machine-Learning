package Lab1;

import java.util.List;
import java.util.ListIterator;
import Lab1.Element.*;

/**
 * Created by Polina on 22.10.2017.
 */
public class Measures {
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
        double P = TP + FN;
        double N = FP + TN;
        double L = FN + FP;
        //  System.out.println("TP " + TP + " FN " + FN + " FP " + FP + " TN " + TN + " P " + P + " N " + N);
        double Recall;
        if (P == 0) Recall = 1;
        else Recall = TP / P;
        double Precision;
        if (TP + FP == 0) Precision = 1;
        else Precision = TP / (TP + FP);
        double Accuracy = (TP + TN) / (P + N);
        double F = 2 * Precision * Recall / (Precision + Recall);
        if (Double.isNaN(F)) {
            //   System.out.println(Recall+" "+ Precision +" TP "+ TP+" FP "+ FP+" P "+ P+ " N "+ N);
            F = 0;
        }
        result[0] = Accuracy;
        result[1] = F;
        result[2] = L;
        return result;
    }
}
