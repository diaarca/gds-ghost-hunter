class Graph:

    def __init__(self, n: int, edges: list[tuple[int, int]]) -> None:
        self.vertices: list[int] = [i for i in range(n)]
        self.edges: list[tuple[int, int]] = edges
        self.neighbors: dict = {u:[] for u in self.vertices}

        for u, v in edges:
            self.neighbors[u] += [v]
            self.neighbors[v] += [u]

    def __str__(self):
        print(f"V: {self.vertices}")
        print(f"E: {self.edges}")
        print(f"N: {self.neighbors}")
