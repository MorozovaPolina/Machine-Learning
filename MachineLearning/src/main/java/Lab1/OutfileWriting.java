package Lab1;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.ListIterator;

import Lab1.Main1.*;
import Lab1.Measures.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * Created by Polina on 22.10.2017.
 */
public class OutfileWriting {
    public static void writeOut(List<Q> QS) throws IOException {
        int outfileRow = 0;
        Workbook outfile = new HSSFWorkbook();
        Sheet mesures = outfile.createSheet();
        org.apache.poi.ss.usermodel.Row startRow;
        startRow = mesures.createRow(0);
        Cell numON = startRow.createCell(0);
        numON.setCellValue("Количество соседей");
        Cell numOF = startRow.createCell(1);
        numOF.setCellValue("Количество фолдеров");
        Cell pM = startRow.createCell(2);
        pM.setCellValue("p");
        Cell kernal = startRow.createCell(3);
        kernal.setCellValue("Ядро");
        Cell transformation = startRow.createCell(4);
        transformation.setCellValue("Преобразование");
        Cell Q = startRow.createCell(5);
        Q.setCellValue("Q");
        Cell Accur = startRow.createCell(6);
        Accur.setCellValue("Accuracy");
        Cell Fm = startRow.createCell(7);
        Fm.setCellValue("F-measure");


        ListIterator<Q> QIterator = QS.listIterator();
        while (QIterator.hasNext()) {
            Q res = QIterator.next();
            outfileRow++;
            org.apache.poi.ss.usermodel.Row currentRow = mesures.createRow(outfileRow);
            numON = currentRow.createCell(0);
            numON.setCellValue(res.numberOfNeibours);
            numOF = currentRow.createCell(1);
            numOF.setCellValue(res.numberOfFolders);
            pM = currentRow.createCell(2);
            pM.setCellValue(res.p);
            kernal = currentRow.createCell(3);
            kernal.setCellValue(String.valueOf(res.kernel));
            transformation = currentRow.createCell(4);
            transformation.setCellValue(String.valueOf(res.transfromation));
            Q = currentRow.createCell(5);
            Q.setCellValue(res.Q);
            Accur = currentRow.createCell(6);
            Accur.setCellValue(res.Accuricy);
            Fm = currentRow.createCell(7);
            Fm.setCellValue(res.Fmeas);

        }

        outfile.write(new FileOutputStream("results.xls"));
        outfile.close();

    }
}
