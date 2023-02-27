package com.example.eventstracetobpmnchorconverter.layout;

import com.example.eventstracetobpmnchorconverter.producing.information.bpmn.definitions.choreography.ChoreographyShape;
import com.google.common.graph.Graph;
import lombok.extern.slf4j.Slf4j;

import java.text.MessageFormat;
import java.util.ArrayDeque;
import java.util.Queue;

@Slf4j
public class BPMNChoreography2DGraphLayoutCreator implements GraphLayoutCreator<ChoreographyShape> {

    private static final int DEFAULT_X_LEVEL = 20;
    private static final int DEFAULT_Y_LEVEL = 100;
    private static final int X_INCREMENT_PER_LEVEL =  120;
    private static final int Y_INCREMENT_PER_LEVEL = 200;


    @Override
    public void layout(Graph<ChoreographyShape> graph, ChoreographyShape root) {
        log.info("Creating layout for BPMNChoreographyGraph");
        Queue<ChoreographyShape> queue = new ArrayDeque<>();
        queue.add(root);
        int level = 0;
        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                ChoreographyShape node = queue.poll();
                final var xCoordinate = DEFAULT_X_LEVEL + X_INCREMENT_PER_LEVEL * level;
                final var yCoordinate = DEFAULT_Y_LEVEL + Y_INCREMENT_PER_LEVEL * i;
                node.setX(xCoordinate);
                node.setY(yCoordinate);
                final var message = MessageFormat.format("Setting x and y coordinates for ChoreographyShape {0} to " +
                        "{1}x and {2}y", node.getId(), node.getX(), node.getY());
                log.info(message);
                for (ChoreographyShape neighbor : graph.successors(node)) {
                    queue.add(neighbor);
                }
            }
            level++;
        }
    }


}
