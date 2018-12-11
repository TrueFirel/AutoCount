package com.jdmc.client.controllers;

import com.jdmc.entities.Automobile;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class ChartViewController {

    private ObservableList<String> marks;
    private ArrayList<Automobile> automobiles;

    @FXML
    private Label exceptionLabel;


    @FXML
    private BarChart<String, Integer> markAmountChart;

    @FXML
    private CategoryAxis xAxis;

    public ChartViewController(ArrayList<Automobile> automobiles) {
        ArrayList<Automobile> tempAutomobiles = new ArrayList<>();
        for(Automobile auto : automobiles) {
            if(tempAutomobiles.size() == 0) {
                tempAutomobiles.add(auto);
                continue;
            }

            Iterator<Automobile> it = tempAutomobiles.iterator();
            int consistFlag = -1;
            while(it.hasNext()) {
                Automobile matchAuto = it.next();
                if(matchAuto.getMark().equals(auto.getMark())) {
                    matchAuto.setAmount(matchAuto.getAmount() + auto.getAmount());
                    consistFlag++;
                    break;
                }
            }
            if(consistFlag == -1) tempAutomobiles.add(auto);

//            for(Automobile matchAuto : tempAutomobiles) {
//                if(matchAuto.getMark().equals(auto.getMark())) matchAuto.setAmount(matchAuto.getAmount() + auto.getAmount());
//                else tempAutomobiles.add(auto);
//            }
        }
        this.automobiles = tempAutomobiles;
        this.marks = FXCollections.observableArrayList();
        this.xAxis = new CategoryAxis();
    }

    @FXML
    void initialize() {

        XYChart.Series<String, Integer> series = new XYChart.Series<>();
        for(int i = 0; i < this.automobiles.size(); i++) {
            this.marks.add(this.automobiles.get(i).getMark());
            series.getData().add(new XYChart.Data<>(this.automobiles.get(i).getMark(), this.automobiles.get(i).getAmount()));
        }
        this.xAxis.setCategories(this.marks);
        this.markAmountChart.getData().add(series);

        for(int i = 0; i < this.automobiles.size(); i++) {
            Random rand = new Random();
            int r = (int)(Math.random()*256);
            int g = (int)(Math.random()*256);
            int b = (int)(Math.random()*256);
            Color randomColor = new Color(r, g, b);
            Node n = this.markAmountChart.lookup(".data" + i + ".chart-bar");
            n.setStyle("-fx-bar-fill: rgb(" + r + ", " + g + ", " + b + ")");
        }

        markAmountChart.setLegendVisible(false);
    }
}