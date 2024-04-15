package org.example;

import org.example.util.Constants;

public class ElevatorSystem {
    public static void main(String[] args) throws InterruptedException {
        ElevatorController controller = new ElevatorController(Constants.NUM_ELEVATORS);

        Thread controllerThread = new Thread(controller);
        controllerThread.start();

        // Simulate the elevator system operation by submitting requests
        System.out.println("Elevator system is starting.");

        // Submit some example requests
        controller.addRequest(new Request(0, 10, Direction.UP));
        controller.addRequest(new Request(10, 0, Direction.DOWN));
        controller.addRequest(new Request(5, 0, Direction.DOWN));
        controller.addRequest(new Request(8, 11, Direction.UP));
        controller.addRequest(new Request(11, 8, Direction.DOWN));
        controller.addRequest(new Request(3, 0, Direction.DOWN));
        controller.addRequest(new Request(13, 0, Direction.DOWN));
        // Wait a bit
        Thread.sleep(10000); // Wait for 10 seconds


        System.out.println("Shutting down the elevator system.");
        controller.shutdown();

        // Ensure the controller thread also stops
        controllerThread.interrupt();
        controllerThread.join();

        System.out.println("Elevator system has been shut down.");
    }
}
