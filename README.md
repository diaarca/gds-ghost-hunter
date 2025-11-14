# gds-ghost-hunter
From our comprehension of the game:
- We have a connected graph G
- The ghost start on a vertex v in V(G)
- The ghost hunter isn't placed in G and cannot see the ghost
- The goal of the ghost hunter is to guess the location of the ghost
- Each turn:
1. The ghost hunter choose a vertex to guess the ghost position
2. If the ghost is on the said-vertex, the ghost hunter wins
3. Otherwise, the ghost move on a neighbor of its current vertex and the ghost hunter have to guess another vertex the next turn
- For now, we consider the case when the ghost have a uniform policy where it has the same probability to move on any neighbor of v

The ghost hunter wins if he has a strategy to catch the ghost in a finite number of rounds. Otherwise the ghost wins. On which graphs does the ghost hunter win?
