package com.lucstudios.main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.awt.image.DataBufferInt;

import javax.imageio.ImageIO;
import javax.swing.JFrame;


import com.lcstudios.entities.BulletShoot;
import com.lcstudios.entities.Enemy;
import com.lcstudios.entities.Entity;
import com.lcstudios.entities.Player;
import com.lcstudios.graficos.Spritesheet;
import com.lcstudios.graficos.UI;
import com.lucstudios.world.Camera;
import com.lucstudios.world.World;


public class Game extends Canvas implements Runnable, KeyListener, MouseListener,MouseMotionListener{
	

	private static final long serialVersionUID = 1L;
	public static JFrame frame;
	private Thread thread;
	public boolean isRunning = true;
	public static final int WIDTH = 240;
	public static final int HEIGHT = 160;
	public static final int SCALE = 3;
	
	private int CUR_LEVEL = 1, MAX_LEVEL = 2;

	
	private BufferedImage image;
	
	
	public static List <Entity> entities;
	public static List <Enemy> enemies;
	public static List <BulletShoot> bullets;
	
	public static Spritesheet spritesheet;
	
	public static World world;
	
	public static Player player;
	
	public static Random rand;
	
	public UI ui;
	
	// public int xx,yy;
	
	// public InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream("pixelart.ttf");
	// public Font newfont;
	
	public static String gameState = "MENU";
	private boolean showMessageGameOver = true;
	private int framesGameOver = 0;
	private boolean restartGame = false;
	
	
	public Menu menu;
	
	public static int[] pixels;
	public int [] lightMapPixels;
	public BufferedImage lightmap;
	public boolean saveGame = false;

	public int mx, my;
	
	public Game () { // inicializar;
		Sound.musicBackground.loop();
		rand = new Random();
		addKeyListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		this.setPreferredSize(new Dimension(WIDTH*SCALE, HEIGHT*SCALE));
		initFrame();
		//inicializando objetos;
		
		ui = new UI();
		image = new BufferedImage(WIDTH,HEIGHT,BufferedImage.TYPE_INT_RGB);
		pixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();
		try {
			lightmap = ImageIO.read(getClass().getResource("/lightmap.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		lightMapPixels = new int [lightmap.getWidth() * lightmap.getHeight()];
		lightmap.getRGB(0,0,lightmap.getWidth(), lightmap.getHeight(), lightMapPixels, 0, lightmap.getWidth());
		entities = new ArrayList<Entity>();
		enemies = new ArrayList<Enemy>();
		bullets = new ArrayList<BulletShoot>();
		spritesheet = new Spritesheet ("/spritesheet.png");
		player = new Player(0,0,16,16,spritesheet.getSprite(32, 0, 16, 16));	
		entities.add(player);
		world = new World ("/level1.png");
		
		/*try {
			newfont = Font.createFont(Font.TRUETYPE_FONT, stream).deriveFont(30f);
		} catch (FontFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		
		menu = new Menu();
	}
	
	public void initFrame() {
		frame = new JFrame ("The Gramundor");
		frame.add(this);
		frame.setResizable(false); // the user can't re-size the window
		frame.pack();
		frame.setLocationRelativeTo(null); // window on center
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	public synchronized void start() {
		
		thread = new Thread (this);
		isRunning = true;
		thread.start();
	}
	
	public synchronized void stop() {
		isRunning = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
		
	}
	
	public static void main (String []args) {
		Game game = new Game ();
		game.start();
		
	}
	
	public void tick() {
		if(gameState == "NORMAL") {
		//	xx++;
			if(this.saveGame) {
				this.saveGame = false;
				String[] opt1 = {"level","life"};
				int []opt2 = {this.CUR_LEVEL,(int) player.life};
				Menu.saveGame(opt1,opt2,10);
				System.out.println("Jogo salvo com sucesso!");
			}
			this.restartGame = false;
	for (int i = 0; i < entities.size();i++) {
		Entity e = entities.get(i);
		e.tick();
		}
	
		for (int i = 0; i< bullets.size();i++) {
			bullets.get(i).tick();
		}
		
		if(enemies.size() == 0) {
			// Avançar para o proximo level;
			CUR_LEVEL++;
			if(CUR_LEVEL > MAX_LEVEL){
				CUR_LEVEL = 1;
			}
			String newWorld= "level"+CUR_LEVEL +".png";
			System.out.println(newWorld);
			World.restartGame(newWorld);
		}
		}else if(gameState == "GAME_OVER") {
			this.framesGameOver++;
			if(this.framesGameOver ==30) {
				this.framesGameOver = 0;
					if(this.showMessageGameOver)
						this.showMessageGameOver = false;
						else
							this.showMessageGameOver =true;
			}
			if(restartGame) {
				this.restartGame = false;
				this.gameState = "NORMAL";
				CUR_LEVEL =1;
				String newWorld= "level"+CUR_LEVEL +".png";
				//System.out.println(newWorld);
				World.restartGame(newWorld);
			}
		}else if(gameState == "MENU") {
			//executa menu
			player.updateCamera();
			menu.tick();
			
		}
	}
	// exemplo de manipulação de pixels
	/*
	public void drawRectangleExample(int xoff,int yoff) {
		for(int xx = 0; xx <32; xx++) {
			for(int yy = 0; yy < 32; yy++) {
				int xOff = xx + xoff;
				int yOff = yy + yoff;
				if(xOff < 0 || yOff < 0 || xOff >=WIDTH || yOff >=HEIGHT)
					continue;
				pixels [xOff + (yOff*WIDTH)] = 0xff0000;
			}
		}
	}
	*/
	
	// aplicar luz no jogo
	/*public void applyLight () {
		for(int xx = 0; xx<Game.WIDTH; xx++) {
			for(int yy = 0; yy <Game.HEIGHT; yy++) {
				if(lightMapPixels[xx +(yy * Game.WIDTH)] == 0xffffffff) {
					pixels [xx+(yy * Game.WIDTH)] = 0;
				}
			}
		}
	}
	*/
	public void render () {
		BufferStrategy bs = this.getBufferStrategy();
		if (bs == null) {
			this.createBufferStrategy(3);
			return;
		}
		Graphics g = image.getGraphics();
		g.setColor(new Color (0,0,0));
		g.fillRect(0, 0, WIDTH, HEIGHT);
		
		/*Renderização do jogo */
		
	
		// Graphics2D g2 =(Graphics2D) g; // objeto2D cast
		
		/***/
		world.render(g);
		for (int i = 0; i < entities.size();i++) {
			Entity e = entities.get(i);
			e.render(g);
			}
		for (int i = 0; i< bullets.size();i++) {
			bullets.get(i).render(g);
		}
		// applyLight();
		ui.render(g);
		g.dispose();
		g = bs.getDrawGraphics();
	//	drawRectangleExample(xx, yy);
		g.drawImage(image, 0,0,WIDTH*SCALE, HEIGHT*SCALE, null);
		g.setFont(new Font("arial",Font.BOLD,20));
		g.setColor(Color.white);
		g.drawString("Munição: " + player.ammo, 580, 20);
	//	g.setFont(newfont);
	//	g.drawString("Teste com a nova fonte", 20, 20);
		if(gameState == "GAME_OVER") {
			Graphics2D g2 = (Graphics2D) g;
			g2.setColor(new Color(0,0,0,100));
			g2.fillRect(0, 0, WIDTH * SCALE, HEIGHT * SCALE);
			g.setFont(new Font("arial",Font.BOLD,36));
			g.setColor(Color.white);
			g.drawString("Game Over",WIDTH * SCALE /2 - 83, HEIGHT * SCALE /2 - 18);
			g.setFont(new Font("arial",Font.BOLD,29));
			if(showMessageGameOver)
				g.drawString(">Pressione Enter para reiniciar<",WIDTH * SCALE /2 -190, HEIGHT * SCALE /2 + 40);
		}else if(gameState == "MENU") {
			menu.render(g);
		}
		/*Graphics2D g2 = (Graphics2D) g;
		double angleMouse = Math.atan2(200+25 - my, 200+25 - mx);
		g2.rotate(angleMouse, 200+25,200+25);
		g.setColor(Color.red);
		g.fillRect(200, 200,50,50);
		*/
		bs.show(); 
	}
	
	
	public void run() {
		// looping profissional
		long lastTime = System.nanoTime();
		double amountOfTicks = 60.0;
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;
		int frames = 0;
		double timer = System.currentTimeMillis();
		requestFocus();
		while(isRunning) {
			long now = System.nanoTime();
			delta+= (now - lastTime) / ns;
			lastTime = now;
			if(delta >= 1) {
				tick ();
				render();
				frames++;
				delta--;
			}
			if(System.currentTimeMillis() - timer >= 1000){
				System.out.println("FPS: "+ frames);
				frames = 0;
				timer +=1000;
				
			}
		}
		stop();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_Z)
			player.jump = true;
		if(e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
			player.right = true;
		}else if(e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
			player.left = true;
		}
		
		if(e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
			player.up = true;
			if(gameState =="MENU") {
				menu.up = true;
			}
		}else if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
			player.down = true;
			
			if(gameState =="MENU") {
				menu.down = true;
			}
		}
		
		if(e.getKeyCode() == KeyEvent.VK_Q) {
			player.shoot = true;
		}
		
		if(e.getKeyCode() == KeyEvent.VK_ENTER) {
			this.restartGame = true;
			if(gameState == "MENU") {
				menu.enter = true;
			}
		}
		
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {	
			gameState = "MENU";
			menu.pause = true;
		}
		if(e.getKeyCode() ==KeyEvent.VK_SPACE) {
			if(gameState == "NORMAL")
				this.saveGame = true;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
			player.right = false;
		}else if(e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
			player.left = false;
		}
		
		if(e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
			player.up = false;
		}else if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
			player.down = false;
		}
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
		
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		player.mouseShoot = true;
		player.mx=(e.getX() /3);
		player.my=(e.getY() /3);
		//	System.out.println(player.mx); 
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		
		
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
	
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		this.mx= e.getX();
		this.my= e.getY();
	}
}
	

