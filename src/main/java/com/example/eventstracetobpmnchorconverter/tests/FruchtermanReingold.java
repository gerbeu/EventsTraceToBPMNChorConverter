package com.example.eventstracetobpmnchorconverter.tests;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FruchtermanReingold {
    private static final double K = 0.1;
    private static final int ITERATIONS = 1000;
    private static final double TOLERANCE = 1e-5;
    private static final double AREA = 1.0;

    private List<Node> nodes;
    private double temperature;

    public FruchtermanReingold(List<Node> nodes) {
        this.nodes = nodes;
    }

    public void run() {
        temperature = Math.sqrt(AREA) / 10.0;
        for (int i = 0; i < ITERATIONS; i++) {
            double energy = computeEnergy();
            if (temperature < TOLERANCE) {
                break;
            }
            for (Node node : nodes) {
                List<Node> neighbors = getNeighbors(node);
                double deltaX = 0.0;
                double deltaY = 0.0;
                for (Node neighbor : neighbors) {
                    double distance = distance(node, neighbor);
                    double directionX = (neighbor.x - node.x) / distance;
                    double directionY = (neighbor.y - node.y) / distance;
                    deltaX += directionX * repulsiveForce(distance);
                    deltaY += directionY * repulsiveForce(distance);
                }
                for (Node neighbor : neighbors) {
                    double distance = distance(node, neighbor);
                    double directionX = (neighbor.x - node.x) / distance;
                    double directionY = (neighbor.y - node.y) / distance;
                    deltaX -= directionX * attractiveForce(distance) * energy;
                    deltaY -= directionY * attractiveForce(distance) * energy;
                }
                node.x += deltaX;
                node.y += deltaY;
            }
            temperature *= 0.99;
        }
    }

    private List<Node> getNeighbors(Node node) {
        List<Node> neighbors = new ArrayList<>();
        for (Node other : nodes) {
            if (other == node) {
                continue;
            }
            neighbors.add(other);
        }
        return neighbors;
    }

    private double computeEnergy() {
        double energy = 0.0;
        for (int i = 0; i < nodes.size(); i++) {
            Node node1 = nodes.get(i);
            for (int j = i + 1; j < nodes.size(); j++) {
                Node node2 = nodes.get(j);
                double distance = distance(node1, node2);
                energy += repulsiveForce(distance);
                energy += attractiveForce(distance);
            }
        }
        return energy;
    }

    private double repulsiveForce(double distance) {
        return K / distance;
    }

    private double attractiveForce(double distance) {
        return distance * distance / K;
    }

    private double distance(Node node1, Node node2) {
        double deltaX = node2.x - node1.x;
        double deltaY = node2.y - node1.y;
        return Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    }

    public static class Node {
        public double x;
        public double y;

        public Node(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    public static void main(String[] args) {
        List<Node> nodes = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 100; i++) {
            nodes.add(new Node(random.nextDouble(), random.nextDouble()));
        }
        FruchtermanReingold algorithm = new FruchtermanReingold(nodes);
        algorithm.run();
        for (Node node : nodes) {
            System.out.println(node.x + " " + node.y);
        }
    }
}

