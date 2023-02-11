package com.example.eventstracetobpmnchorconverter.tests.furchterman_reingold;

import java.util.LinkedList;

public class Node {
    private LinkedList<Node> incidentList; //has 30 elements for 30 vertices. 1 if incident, 0 if not
    private int color;
    private Coordinates coord;
    private Coordinates disp;

    public Coordinates getDisp() {
        return disp;
    }

    public void setDisp(float x, float y) {
        disp.setX(x);
        disp.setY(y);
    }
    public void setDisp(Coordinates d) {
        disp = d;
    }

    private int id;
    public Node(){
        incidentList = new LinkedList<Node>();
        color = 0;
        coord = new Coordinates(0,0);
        disp = new Coordinates(0,0);
        id = -1;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public LinkedList<Node> getIncidentList() {
        return incidentList;
    }
    public void addEdge(Node n) {
        incidentList.add(n);
    }
    public void removeEdge(Node n){
        incidentList.remove(n);
    }
    public int getColor() {
        return color;
    }
    public void setColor(int color) {
        this.color = color;
    }
    public Coordinates getCoord() {
        return coord;
    }
    public void setCoord(float x, float y) {
        coord.setX(x);
        coord.setY(y);
    }
    public int getDegree(){
        return incidentList.size();
    }

    public boolean isAdjacent(Node n){
        return incidentList.contains(n);
    }

}
