package org.example;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ElevatorController implements Runnable {
    private final HashMap<Integer, Elevator> elevators;
    private final ExecutorService executor;
    private final Queue<Request> pendingRequests;
    private volatile boolean isRunning;

    public ElevatorController(int numberOfElevators) {
        this.isRunning = true;
        this.elevators = new HashMap<>();
        this.executor = Executors.newFixedThreadPool(numberOfElevators);
        this.pendingRequests = new LinkedList<>();

        for (int i = 0; i < numberOfElevators; i++) {
            Elevator elevator = new Elevator(i + 1);
            elevators.put(i, elevator);
            // Submit elevator tasks to the executor for concurrent processing
            executor.submit(elevator);
        }
    }

    @Override
    public void run() {
        while (isRunning) {
            try {
                synchronized (this) {
                    Request nextRequest = pendingRequests.peek();
                    if (nextRequest != null && assignRequestToElevator(nextRequest)) {
                        pendingRequests.poll();
                    }
                }
                Thread.sleep(1000); // Wait for a bit before checking again
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break; // Exit loop if interrupted
            }
        }
    }

    public synchronized void addRequest(Request request) {
        pendingRequests.add(request);
    }


    public synchronized boolean assignRequestToElevator(Request request) {
        Elevator selectedElevator = selectElevatorForRequest(request);
        if (selectedElevator != null) {
            System.out.println("Assigning request from floor " + request.startFloor() + " to elevator " + selectedElevator.getId());
            selectedElevator.assignRequest(request);
            return true;
        } else {
            System.out.println("No suitable elevator found for request from floor " + request.startFloor());
            pendingRequests.offer(request);
            return false;
        }
    }


    private Elevator selectElevatorForRequest(Request request) {
        Elevator bestElevator = null;
        // Implement logic to select the most suitable elevator based on criteria like:
        // - Current direction
        // - Proximity to request's start floor

        for (Elevator elevator : elevators.values()) {
            if (isElevatorSuitable(elevator, request)) {
                if (bestElevator == null ||
                        getElevatorProximity(elevator, request) < getElevatorProximity(bestElevator, request)) {
                    bestElevator = elevator;
                }
            }
        }
        return bestElevator;
    }

    private boolean isElevatorSuitable(Elevator elevator, Request request) {
        // Elevator is suitable if it's idle
        if (elevator.getDirection() == Direction.IDLE) {
            return true;
        }

        // If the elevator is already moving in the direction of the request
        // and can accommodate the request based on its current trajectory
        boolean isDirectionCompatible = (elevator.getDirection() == request.direction());
        boolean canAccommodateRequest = false;

        if (isDirectionCompatible) {
            if (elevator.getDirection() == Direction.UP) {
                // The elevator can accommodate the request if it's below the request's start floor
                canAccommodateRequest = elevator.getCurrentFloor() <= request.startFloor();
            } else if (elevator.getDirection() == Direction.DOWN) {
                // The elevator can accommodate the request if it's above the request's start floor
                canAccommodateRequest = elevator.getCurrentFloor() >= request.startFloor();
            }
        }

        return isDirectionCompatible && canAccommodateRequest;
    }

    private int getElevatorProximity(Elevator elevator, Request request) {
        // Calculate and return some measure of proximity between the elevator and request's start floor
        // Could be as simple as the absolute difference in floors
        return Math.abs(elevator.getCurrentFloor() - request.startFloor());
    }

    public void shutdown() {
        // Signal the controller to stop running
        isRunning = false;

        // Attempt to shut down all elevator threads gracefully
        executor.shutdown(); // Disable new tasks from being submitted

        try {
            // Wait a while for existing tasks to terminate
            if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
                executor.shutdownNow(); // Cancel currently executing tasks

                // Wait a while for tasks to respond to being cancelled
                if (!executor.awaitTermination(1, TimeUnit.SECONDS))
                    System.err.println("Elevator executor did not terminate");
            }
        } catch (InterruptedException ie) {
            // Cancel if current thread also interrupted
            executor.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }
}
