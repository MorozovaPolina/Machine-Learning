package Lab1;

import javafx.scene.chart.BubbleChart;
import javafx.scene.chart.ChartBuilder;
import org.knowm.xchart.*;
import org.knowm.xchart.style.Styler;
import org.knowm.xchart.BubbleChart.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import Lab1.Main1.*;
import Lab1.Element.*;

import static Lab1.Main1.kNN;

/**
 * http://knowm.org/open-source/xchart/xchart-example-code/, "Scatter"
 */
public class MyChart {

    public List<Element> Pattern;
    public void drawIt(List<Element> TestElements) throws IOException {
        MyChart exampleChart = new MyChart();
        XYChart chart = exampleChart.getChart(TestElements);
        new SwingWrapper<XYChart>(chart).displayChart();
    }

    public void Pattern() throws IOException {
        BufferedReader infile = new BufferedReader(new FileReader("pattern.out"));
        Pattern = new LinkedList<>();

        while (true) {
            String InString = infile.readLine();

            if (InString == null || InString=="") break;
            String[] InStringArray = InString.split(" ");
          //  System.out.println(InStringArray[2]);
            Pattern.add(new Element(Float.valueOf(InStringArray[0]), Float.valueOf(InStringArray[1]), Integer.valueOf(InStringArray[2])));
        }
    }

    public XYChart getChart(List<Element> TestElements) throws IOException {
        XYChart chart = new XYChartBuilder().width(1200).height(1200).build();
        Pattern();
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Scatter);
        chart.getStyler().setChartTitleVisible(false);
        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideSW);
        chart.getStyler().setMarkerSize(8);

        Map<Integer, List<Element>> nabor = new HashMap<>();

        for (Element point : Pattern) {
            nabor.computeIfAbsent(point.trueClass, k -> new LinkedList<>());
            nabor.get(point.trueClass).add(point);
        }

        for (Element point : TestElements) {
                if(point.SupposedClass == 0) {
                    nabor.computeIfAbsent(3, k -> new LinkedList<>());
                    nabor.get(3).add(point);
                }
                else {
                    nabor.computeIfAbsent(4, k -> new LinkedList<>());
                    nabor.get(4).add(point);
            }
            nabor.computeIfAbsent(point.trueClass, k -> new LinkedList<>());
            nabor.get(point.trueClass).add(point);
        }

        // Series
        for (Integer TrueClass : nabor.keySet()) {
            List<Element> coordinates = nabor.get(TrueClass);
            List<Double> xData = new LinkedList<>();
            List<Double> yData = new LinkedList<>();

            for (Element point : coordinates) {
                xData.add((double)point.x);
                yData.add((double) point.y);
            }

            if(TrueClass ==0){

                chart.addSeries("Class 0", xData, yData);
            }
            if(TrueClass ==1)
                chart.addSeries("Class 1", xData, yData);
            if(TrueClass ==3)
                chart.addSeries("Test Class is 0", xData, yData);
            if(TrueClass ==4)
                chart.addSeries("Test Class is 1", xData, yData);
        }

        return chart;
    }
}
