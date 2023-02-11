package com.example.eventstracetobpmnchorconverter.tests.furchterman_reingold;

import java.util.ArrayList;
import java.util.Random;


public class MyGraph {
    private final int DISRESPECT = -1;
    private final int MORECOLOR = -3;
    private final float EPSILON = 0.003f;
    private ArrayList<Node> graphNodes; //maximum of 30 vertices
    private int nVertices = 0;
    private int score = 50;
    int maxColor = 0;
    int[] colorPopulation = new int[15];
    double boundx, boundy, C;

    public double getBoundx() {
        return boundx;
    }

    public void setBoundx(double boundx) {
        this.boundx = boundx;
    }

    public double getBoundy() {
        return boundy;
    }

    public void setBoundy(double boundy) {
        this.boundy = boundy;
    }

    public double getC() {
        return C;
    }

    public void setC(double c) {
        C = c;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getnVertices() {
        return nVertices;
    }

    public MyGraph() {
        graphNodes = new ArrayList<Node>();
    }

    public ArrayList<Node> getGraphNodes() {
        return graphNodes;
    }

    //add a new node into the graph
    //also set the id of that node
    public void addNode(Node n) {
        graphNodes.add(n);
        n.setId(nVertices++);
    }

    public void addEdge(Node n1, Node n2) {
        n1.addEdge(n2);
        n2.addEdge(n1);
    }

    //randomly generate a graph with parsity
    public void randomGenerating(double parse) { //parse is between 0 and 1
        Random gen = new Random(System.currentTimeMillis());
        int tempNVertices = 6; //CHANGE HERE TO BECOME A RANDOM NUMBER
        for (int i = 0; i < tempNVertices; i++) {
            Node n = new Node();
            float x = 0, y = 0;
            while (true) {
                boolean flag = true;
                x = gen.nextFloat();
                y = gen.nextFloat();
                for (int j = 0; j < i; j++) {
                    if (x * boundx == graphNodes.get(j).getCoord().getX() && y * boundx == graphNodes.get(j).getCoord().getY())
                        flag = false;
                    break;
                }
                if (flag)
                    break;
            }
            n.setCoord((float) (x * boundx), (float) (y * boundy));
            addNode(n);
        }
        for (int i = 0; i < tempNVertices; i++) {
            for (int j = i + 1; j < tempNVertices; j++) {
                if (gen.nextDouble() < parse) {
                    addEdge(graphNodes.get(i), graphNodes.get(j));
                }
            }
        }
    }

    public void frLayout() {
        double w = boundx, h = boundy;
        double area = w * h;
        double k = C * Math.sqrt(area / nVertices);
        double temperature = 1000;
        for (int i = 0; i < nVertices; i++)
            System.out.println(graphNodes.get(i).getCoord().getX() + " " + graphNodes.get(i).getCoord().getY());
        System.out.println("------------------------------");
        for (int m = 0; m < 900; m++) {
            for (int i = 0; i < nVertices; i++) {
                Node v = graphNodes.get(i);
                v.setDisp(0, 0);
                for (int j = 0; j < nVertices; j++) {
                    Node u = graphNodes.get(j);
                    if (i != j) {
                        Coordinates delta = v.getCoord().subtract(u.getCoord());
                        double myFr = fr(u, v, k);

                        v.setDisp(v.getDisp().add(delta.unit().scale((float) myFr)));
                        if (Double.isNaN(v.getDisp().getX())) {
                            System.out.println("PANIC: " + u.getCoord().getX() + " " + u.getCoord().getY() + " " + delta.getX() + " " + v.getCoord().getX());
                            return;
                        }
                    }
                }
            }
            for (int i = 0; i < nVertices; i++) {
                Node v = graphNodes.get(i);
                for (int j = i + 1; j < nVertices; j++) {
                    Node u = graphNodes.get(j);
                    if (v.isAdjacent(u)) {
                        Coordinates delta = v.getCoord().subtract(u.getCoord());
                        double myFa = fa(u, v, k);
                        v.setDisp(v.getDisp().subtract(delta.unit().scale((float) myFa)));
                        u.setDisp(u.getDisp().add(delta.unit().scale((float) myFa)));

                    }
                }
            }

            for (int i = 0; i < nVertices; i++) {
                //actually adjusting the nodes
                Node v = graphNodes.get(i);
                if (i == 0) {
                    System.out.println(v.getCoord().getX() + " " + v.getCoord().getY());
                    Coordinates disp = v.getDisp().unit().scale((float) Math.min(v.getDisp().length(), temperature));
                    System.out.println(">>" + disp.getX() + " " + disp.getY());
                }
                Coordinates newCoord = (v.getCoord().add(v.getDisp().unit().scale((float) Math.min(v.getDisp().length(), temperature))));
                v.setCoord(newCoord.getX(), newCoord.getY());

//                //prevent from going outside of bound
//                float x = (float)Math.min(w, Math.max(0,v.getCoord().getX()));
//                float y = (float)Math.min(h, Math.max(0, v.getCoord().getY()));
//
//                v.setCoord(x,y);
                if (i == 0) {
                    System.out.println(v.getCoord().getX() + " " + v.getCoord().getY());
                }
            }
            temperature *= 0.9;
            System.out.println("TEMPERATURE = " + temperature);
        }
        for (int i = 0; i < nVertices; i++) {
            Node v = graphNodes.get(i);
            System.out.println(v.getCoord().getX() + " " + v.getCoord().getY());
            ;
        }
    }

    private double fa(Node ni, Node nj, double k) {
        double distance = ni.getCoord().distance(nj.getCoord());
        return distance * distance / k;
    }

    private double fr(Node ni, Node nj, double k) {
        double distance = ni.getCoord().distance(nj.getCoord());
        return k * k / (distance + 0.000001);
    }

    public static void main(String[] args) {
        MyGraph grph = new MyGraph();
        grph.setBoundx(480);
        grph.setBoundy(480);
        grph.setC(1);
        grph.randomGenerating(1);
        grph.frLayout();
    }

}
