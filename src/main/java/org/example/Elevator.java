package org.example;

import java.util.PriorityQueue;
import java.util.Comparator;

public class Elevator implements Runnable {
    private final int id;
    private int currentFloor;
    private Direction direction;
    private final PriorityQueue<Request> requests;

    public Elevator(int id) {
        this.id = id;
        this.currentFloor = 0; // Starting at ground floor
        this.direction = Direction.IDLE;
        // Prioritize requests based on proximity to the current floor
        this.requests = new PriorityQueue<>(Comparator.comparingInt(
                r -> Math.abs(r.startFloor() - this.currentFloor)));
    }

    @Override
    public void run() {
        System.out.println("Elevator " + id + " starting.");
        while (!Thread.currentThread().isInterrupted()) {
            try {
                processNextRequest();
                Thread.sleep(1000); // Simulate delay
            } catch (InterruptedException e) {
                System.out.println("Elevator " + id + " interrupted.");
                Thread.currentThread().interrupt();
            }
        }
        System.out.println("Elevator " + id + " stopped.");
    }

    public synchronized void assignRequest(Request request) {
        if (direction == Direction.IDLE) {
            direction = request.direction(); // Set direction based on the first request
        }
        requests.offer(request);
        System.out.println("Elevator " + id + ": Request added - " + request.startFloor() + " to " + request.destinationFloor() + " [" + request.direction() + "]");
    }

    private void processNextRequest() {
        Request request = requests.poll();
        if (request != null) {
            if (this.currentFloor != request.startFloor()) {
                System.out.println("Elevator " + id + " moving from " + currentFloor + " to " + request.startFloor());
                this.currentFloor = request.startFloor(); // Simulate moving to the request's start floor
            }
            System.out.println("Elevator " + id + " moving from " + currentFloor + " to " + request.destinationFloor());
            currentFloor = request.destinationFloor(); // Simulate moving to the request's destination floor
            if (requests.isEmpty()) {
                direction = Direction.IDLE; // No more requests, elevator becomes idle
            }
        }
    }

    // Getters and additional methods as necessary
    public int getCurrentFloor() {
        return currentFloor;
    }

    public Direction getDirection() {
        return direction;
    }

    public int getId() {
        return id;
    }
}
