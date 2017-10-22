package Lab1;

import javafx.scene.chart.BubbleChart;
import javafx.scene.chart.ChartBuilder;
import org.knowm.xchart.*;
import org.knowm.xchart.style.Styler;
import org.knowm.xchart.BubbleChart.*;

import java.util.*;

import Lab1.Main1.*;
import Lab1.Element.*;

import static Lab1.Main1.kNN;

/**
 * http://knowm.org/open-source/xchart/xchart-example-code/, "Scatter"
 */
public class MyChart {

    public void drawIt(List<Element> dataList, List<Element> TestElements) {
        MyChart exampleChart = new MyChart();
        XYChart chart = exampleChart.getChart(dataList, TestElements);
        new SwingWrapper<XYChart>(chart).displayChart();
    }

    public XYChart getChart(List<Element> dataList, List<Element> TestElements) {
        XYChart chart = new XYChartBuilder().width(800).height(600).build();

        chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Scatter);
        chart.getStyler().setChartTitleVisible(false);
        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideSW);
        chart.getStyler().setMarkerSize(8);

        Map<Integer, List<Element>> nabor = new HashMap<>();

        for (Element point : dataList) {
            nabor.computeIfAbsent(point.trueClass, k -> new LinkedList<>());
            nabor.get(point.trueClass).add(point);
        }

        for (Element point : TestElements) {
            if(point.trueClass==0){
                if(point.SupposedClass == 0) {
                    nabor.computeIfAbsent(3, k -> new LinkedList<>());
                    nabor.get(3).add(point);
                }
                else {
                    nabor.computeIfAbsent(4, k -> new LinkedList<>());
                    nabor.get(4).add(point);
                }


            }
            else{
                if(point.SupposedClass == 0) {
                    nabor.computeIfAbsent(5, k -> new LinkedList<>());
                    nabor.get(5).add(point);
                }
                else {
                    nabor.computeIfAbsent(6, k -> new LinkedList<>());
                    nabor.get(6).add(point);
                }
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

            if(TrueClass ==0)
                chart.addSeries("Learning Class 0", xData, yData);
            if(TrueClass ==1)
                chart.addSeries("Learning Class 1", xData, yData);
            if(TrueClass ==3)
                chart.addSeries("Test Class is truly 0", xData, yData);
            if(TrueClass ==4)
                chart.addSeries("Test Class is falsely 1", xData, yData);
            if(TrueClass ==5)
                chart.addSeries("Test Class is falsely 0", xData, yData);
            if(TrueClass ==6)
                chart.addSeries("Test Class is truly 1", xData, yData);
        }

        return chart;
    }
}
