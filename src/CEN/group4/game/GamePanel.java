package CEN.group4.game;

 

import java.awt.*;
import javax.swing.JPanel;
import java.awt.image.BufferedImage;

public class GamePanel extends JPanel implements Runnable
{
	public static int width;
	public static int height;
	
	private Thread thread;
	private boolean running = false;;
	
	private BufferedImage img;
	private Graphics2D g;
	
	public GamePanel(int width, int height) 
	{
		this.width = width;
		this.height = height;
		setPreferredSize(new Dimension(width, height));
		setFocusable(true);
		requestFocus();
	}
	
	public void addNotify()
	{
		super.addNotify();
		
		if (thread == null)
		{
			thread = new Thread(this, "GameThread");
			thread.start();
		}
	}
	
	public void init()
	{
		running = true;
		
		img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		g = (Graphics2D) img.getGraphics();
	}
	
	public void run() 
	{
		init();
		
		final double TICK_RATE = 66.0; //How often the game updates per second
		final double UPDATE_TIME = 1000000000 / TICK_RATE; //Time before update
		final int UPDATE_BEFORE = 5; // Time before update
		
		double lastUpdateTime = System.nanoTime();
		double lastRenderTime;
		
		final double TARGET_FPS = 144;
		final double TOTAL_RENDER_TIME = 1000000000 / TARGET_FPS; //Total time before render
		
		int frameCount = 0;
		int lastSecondTime = (int) lastUpdateTime / 1000000000;
		int oldFrameCount = 0;
		
		while (running)
		{
			double now = System.nanoTime();
			int updateCount = 0;
			while(((now - lastUpdateTime) > UPDATE_TIME) && (updateCount < UPDATE_BEFORE))
			{
				update();
				input();
				lastUpdateTime += UPDATE_BEFORE;
				updateCount++;
			}
			
			if(now - lastUpdateTime > UPDATE_BEFORE)
			{
				lastUpdateTime = now - UPDATE_BEFORE;
			}
			
			input();
			render();
			draw();
			lastRenderTime = now;
			frameCount++;
			
			int thisSecond = (int) lastUpdateTime / 1000000000;
			if (thisSecond > lastSecondTime)
			{
				if (frameCount != oldFrameCount)
				{
					System.out.println("NEW SECOND" + thisSecond + " " + frameCount);
					oldFrameCount = frameCount;
				}
				frameCount = 0;
				lastSecondTime = thisSecond;
			}
			
			while(now - lastRenderTime < UPDATE_BEFORE && now - lastUpdateTime < TICK_RATE)
			{
				Thread.yield();
				
				try 
				{
					Thread.sleep(1);
				}
				catch(Exception e)
				{
					System.out.println("ERROR: yielding thread");
				}
				now = System.nanoTime(); 
			}
		}
	}
	
	private int x = 0;
	
	public void update() 
	{
		
	}
	
	public void input() {}
	
	public void render() 
	{
		if(g != null)
		{
			g.setColor(new Color(33, 66, 255));
			g.fillRect(0, 0, width, height);
		}
	}
	
	public void draw() 
	{
		Graphics g2 = (Graphics) this.getGraphics();
		g2.drawImage(img, 0, 0, width, height, null);
		g2.dispose();
	}
}
