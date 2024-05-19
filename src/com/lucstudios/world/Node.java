package com.lucstudios.world;

public class Node {

		public Vector2i tile; // guardar posições para termos mais controle;
		public Node parent;
		public double fCost,gCost,hCost;
		
		public Node (Vector2i tile,Node parent, double gCoste, double hcost) {
			this.tile = tile;
			this.parent = parent;
			this.gCost = gCost;
			this.hCost = hCost;
			this.fCost = gCost + hCost;
		}
}
