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
public class Echarts {

    private List<String> legend = new ArrayList<>();

    private List<Series> series = new ArrayList<>();

    public Echarts(List<String> legend, List<Series> series) {
        this.legend = legend;
        this.series = series;
    }

    public List<String> getLegend() {
        return legend;
    }

    public void setLegend(List<String> legend) {
        this.legend = legend;
    }

    public List<Series> getSeries() {
        return series;
    }

    public void setSeries(List<Series> series) {
        this.series = series;
    }
}
