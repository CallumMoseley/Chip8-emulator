import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Chip8_Emu
{
	static Chip8 chip8 = null; 
	public static void main(String[] args)
	{
		final int FPS = 300;
		final String GAME = "Cave";
		
		JFrame game = new JFrame("Chip8");
		game.setResizable(false);
		game.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		game.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e)
			{
				
			}
			@Override
			public void keyPressed(KeyEvent e)
			{
				switch(e.getKeyChar())
				{
				case '1':
					chip8.keys[0x01] = 1;
				break;
				case '2':
					chip8.keys[0x02] = 1;
				break;
				case '3':
					chip8.keys[0x03] = 1;
				break;
				case '4':
					chip8.keys[0x0C] = 1;
				break;
				case 'q':
					chip8.keys[0x04] = 1;
				break;
				case 'w':
					chip8.keys[0x05] = 1;
				break;
				case 'e':
					chip8.keys[0x06] = 1;
				break;
				case 'r':
					chip8.keys[0x0D] = 1;
				break;
				case 'a':
					chip8.keys[0x07] = 1;
				break;
				case 's':
					chip8.keys[0x08] = 1;
				break;
				case 'd':
					chip8.keys[0x09] = 1;
				break;
				case 'f':
					chip8.keys[0x0E] = 1;
				break;
				case 'z':
					chip8.keys[0x0A] = 1;
				break;
				case 'x':
					chip8.keys[0x00] = 1;
				break;
				case 'c':
					chip8.keys[0x0B] = 1;
				break;
				case 'v':
					chip8.keys[0x0F] = 1;
				break;
				}
			}

			@Override
			public void keyReleased(KeyEvent e)
			{
				switch(e.getKeyChar())
				{
				case '1':
					chip8.keys[0x01] = 0;
				break;
				case '2':
					chip8.keys[0x02] = 0;
				break;
				case '3':
					chip8.keys[0x03] = 0;
				break;
				case '4':
					chip8.keys[0x0C] = 0;
				break;
				case 'q':
					chip8.keys[0x04] = 0;
				break;
				case 'w':
					chip8.keys[0x05] = 0;
				break;
				case 'e':
					chip8.keys[0x06] = 0;
				break;
				case 'r':
					chip8.keys[0x0D] = 0;
				break;
				case 'a':
					chip8.keys[0x07] = 0;
				break;
				case 's':
					chip8.keys[0x08] = 0;
				break;
				case 'd':
					chip8.keys[0x09] = 0;
				break;
				case 'f':
					chip8.keys[0x0E] = 0;
				break;
				case 'z':
					chip8.keys[0x0A] = 0;
				break;
				case 'x':
					chip8.keys[0x00] = 0;
				break;
				case 'c':
					chip8.keys[0x0B] = 0;
				break;
				case 'v':
					chip8.keys[0x0F] = 0;
				break;
				}
			}
		});
		JPanel panel = (JPanel) game.getContentPane();
		panel.setPreferredSize(new Dimension(64 * 8, 32 * 8));
		
		GameCanvas canvas = new GameCanvas();
		canvas.setBounds(0, 0, 64 * 8, 32 * 8);
		panel.add(canvas);
		
		game.pack();
		game.setVisible(true);
		
		chip8 = new Chip8();
		chip8.initialize();
		chip8.loadGame("Programs/Chip-8 Games/" + GAME + ".ch8");
		
		for (;;)
		{
			long startTime = System.currentTimeMillis();
			chip8.tick();
			
			if (chip8.drawFlag) {
				chip8.drawFlag = false;
				canvas.gfx = chip8.gfx;
				canvas.repaint();
			}
			
			try
			{
				Thread.sleep(Math.max(0, (1000 / 300) - (System.currentTimeMillis() - startTime)));
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
}
class GameCanvas extends Canvas
{
	public byte[] gfx;
	public GameCanvas()
	{
		gfx = new byte[64 * 48];
	}
	@Override
	public void paint(Graphics g)
	{
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, 64 * 8, 32 * 8);
		for (int x = 0; x < 64; x++)
		{
			for (int y = 0; y < 32; y++)
			{
				g.setColor(Color.BLACK);
				if (gfx[x + y * 64] != 0) g.setColor(Color.WHITE);
				g.fillRect(x * 8, y * 8, 8, 8);
			}
		}
	}
}