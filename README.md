# Traffic Light Simulator
Maintaining  a  continuous  traffic  flow  in  city  gridshas proven to be a challenging task. Gridlocks in a traffic networkshould  be  prevented.  The  aim  of  this  paper  is  to  analyze  fourdifferent  algorithms  for  both  traffic  lights  and  four  for  vehiclebehaviour, to study the strategies that do not work well, as well asstrategies that are beneficial to the road network. To tackle thisproblem,  a  microscopic  model  is  created  that  models  individualtraffic lights, roads and road users. Discrete time steps are usedto  transition  the  model  in  our  simulation.  We  find  that  vehiclebehaviour can have a negative effect on the traffic flow, especiallywhen  vehicles  are  waiting  for  a  long  time  without  taking  adifferent  route.  However,  the  strategies  of  traffic  lights  has  amuch  greater  impact  on  the  traffic  flow.  We  find  that  a  trafficlight that sets individual lights to green, as opposed to a per-sidebasis, using a First-In, First-Out queue works best within a citygrid.

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
