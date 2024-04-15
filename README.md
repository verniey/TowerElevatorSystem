## Elevator System

### Overview

The elevator system is designed to efficiently manage multiple elevators, responding to floor requests with minimal wait times and optimizing energy use. The core of the system's functionality lies in its Elevator Movement Logic, which determines how elevators decide to move, stop, and handle requests from users.
## Input / Output Examples
- For entering the building: `[current floor: 0, destination floor: N, direction: UP]`
- For leaving the building from the Nth floor: `[current floor: N, destination floor: 0, direction: DOWN]`

### Key Components

- **Directionality**: Elevators operate with a simple direction logic: Up, Down, or Idle. An elevator will continue in its current direction until all requests in that direction have been fulfilled before considering a change in direction or going idle.

- **Request Handling**: When a request is made, the system evaluates all elevators to select the most appropriate one based on proximity, direction, and current load. Requests are then queued per elevator, prioritized by their direction and the floor sequence.

- **Proximity Priority**: The system prioritizes requests based on the proximity to the current floor of each elevator. This ensures that the nearest elevator can respond to a request, reducing wait times.

- **Movement Simulation**: Each elevator simulates movement floor by floor, stopping at floors where requests are to be fulfilled. Movement simulation accounts for the time it takes to travel between floors and the time spent with doors open.

### Movement Algorithm

1. **Request Received**: Upon receiving a floor request, the system identifies the best elevator to service the request using the Elevator Selection Algorithm.

2. **Evaluate Direction**: The selected elevator evaluates if the request aligns with its current direction. If it does, the request is added to the current direction queue; otherwise, it's queued for future direction change.

3. **Move Towards Request**: The elevator moves floor by floor towards the request's start floor, stopping if needed to fulfill other requests along the way.

4. **Servicing Requests**: Once at a request's start floor, the elevator opens doors, then moves to the destination floor, stopping as needed for queued requests.

5. **Direction Reevaluation**: After fulfilling a request, the elevator reevaluates its direction based on remaining requests. If no further requests align with the current direction, the elevator either changes direction to fulfill other requests or goes idle.

### Implementation Details

- The system is implemented in Java, leveraging concurrent programming paradigms to manage multiple elevators and requests simultaneously.
- A priority queue manages requests for each elevator, sorted by direction and proximity to the current floor.
- Elevator movement and state changes are logged for monitoring and debugging purposes.
