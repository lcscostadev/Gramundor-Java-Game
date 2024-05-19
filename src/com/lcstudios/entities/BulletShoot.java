package com.lcstudios.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.lucstudios.main.Game;
import com.lucstudios.world.Camera;

public class BulletShoot extends Entity{
	
	private double dx;
	private double dy;
	private double spd = 4;
	
	private int life = 40, curLife = 0; // para a bala n ficar viva no jogo para sempre.

	public BulletShoot (int x, int y, int width, int height, BufferedImage sprite, double dx, double dy) {
		super(x, y, width, height, sprite);
		this.dx = dx;
		this.dy =dy;
	}
	
		public void tick () {
			x+=dx*spd;
			y+=dy*spd;
			curLife++;
			if(curLife == life) {
				Game.bullets.remove(this);
				return;
			}
		}
		
		public void render (Graphics g) {
			g.setColor(Color.yellow);
			g.fillOval(this.getX() - Camera.x, this.getY() - Camera.y,width, height);
		}
		
}
