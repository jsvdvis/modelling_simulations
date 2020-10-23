# Traffic Light Simulator
INSERT ABSTRACT

## Requirements
- The project was created by using Java 15. Therefore, we recommend Java 15 at the very least.
- Gradle as dependency manager

## Configuration
Various parameters can be tweaked from the ``nl.rug.modellingsimulations.config`` package.
Below, we give an overview of the configuration files. Individual settings are explained using JavaDoc comments.
- ``CityGridConfig`` is used to tweak parameters specific for the city grid simulation.
- ``DefaultSimulationConfig`` defines settings used for running every simulation.
- ``JunctionSpacingConfig`` is used to determine the offset of lanes and exits from each junction position.
- ``SimulatorConfig`` defines settings over the simulator, i.e. settings determining how a simulation is interacted with.
- ``TrafficLightConfig`` is used to define parameters for all traffic lights in the nework
- ``VehicleRoutingStrategyConfig`` is used to tweak settings related to vehicle behaviour.

## Instructions for execution
Compile the project using your favourite Java IDE by running Gradle, or by using the commandline.
Running the program will start a GUI, as well as save important metrics to CSVs once in a while.