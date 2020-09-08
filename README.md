# Convoy1
This project measures the percent of unsatisfied agents as a function of the number of convoys they participated in.
While running the repeated Sequential Online Chore Division (SOCD) game.
Test1.java is the main file that contains all of the definitions and runs the simulation.
It prints the statistics to the console, specifically it outputs data to plot a figure showing the percent of unsatisfied costumers as a function of the number of participation.
For each number of participations it runs several trial and averages out the percent of unsatisfied costumers.
An unsatisfied costumer is one who lead for more that 10% of its ex-post proportional share in practice.


The experimental setup has 100 stations where vehicles can enter and exit. The stations are uniformly distributed on a ring road with a total length of 100km. 
100 vehicles are randomly distributed over these 100 stations (using a uniform distribution).
A single convoy cycles through the stations, and when it reaches a station, the vehicles in that station have a 0.1 probability of entering the road and joining the convoy.
The distance they join for is also randomly generated from a uniform distribution [0,100]. Once a vehicle completes its distance, it leaves the convoy and parks at its destination station until the convoy passes it again, at which point it has a 0.1 probability of rejoining.
If multiple vehicles join at the same station, their order is randomized since the last one in becomes the leader.
Leaders are replaced when another vehicle joins or when they reach their destination. 
For every section between consecutive stations, every vehicle in the convoy accumulates 1/n to its proportional share, and the leader also accumulates 1 to its actual share.
Whenever a vehicle exits, the accumulated actual and ex-post proportional shares are recorded, and added to the list of convoys that the vehicle has participated in.
We measure how many participations on average it takes for the ratio of actual over ex-post proportional share to converge to 1. 
Specifically, we measure what is the percentage of vehicles that lead for 10% or more than what they should have, which we refer to as unsatisfied agents.
