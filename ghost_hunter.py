#!/usr/bin/python3

from utils.graph import Graph

if __name__ == "__main__":
    G = Graph(5, [(1, 3), (2, 4), (5, 3), (1, 2)])
    G.__str__()
