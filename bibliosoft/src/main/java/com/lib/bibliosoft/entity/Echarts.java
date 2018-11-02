package com.lib.bibliosoft.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 毛文杰
 * @project bibliosoft
 * @description
 * @date Created in 9:08 PM. 10/25/2018
 * @modify By 毛文杰
 */
public class Echarts<T> {

    private List<String> legend = new ArrayList<>();

    private List<Series<T>> series = new ArrayList<>();

    public Echarts() {
    }

    public List<Series<T>> getSeries() {
        return series;
    }

    public void setSeries(List<Series<T>> series) {
        this.series = series;
    }

    public List<String> getLegend() {
        return legend;
    }

    public void setLegend(List<String> legend) {
        this.legend = legend;
    }

    public Echarts(List<String> legend, List<Series<T>> series) {
        this.legend = legend;
        this.series = series;
    }
}
