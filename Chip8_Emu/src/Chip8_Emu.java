import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Chip8_Emu
{
	final static int FPS = 60;
	final static int PIXEL_SIZE = 8;
	final static String GAME = "Pong (alt)";
	
	static Chip8 chip8 = null; 
	public static void main(String[] args)
	{
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
		JPanel cpanel = (JPanel) game.getContentPane();
		cpanel.setPreferredSize(new Dimension(64 * PIXEL_SIZE, 32 * PIXEL_SIZE));
		
	    AudioInputStream stream;
	    AudioFormat format;
	    DataLine.Info info;
	    Clip clip = null;
	    
		try {
		    File beep = new File("beep.aiff");

		    stream = AudioSystem.getAudioInputStream(beep);
		    format = stream.getFormat();
		    info = new DataLine.Info(Clip.class, format);
		    clip = (Clip) AudioSystem.getLine(info);
		    clip.open(stream);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		GamePanel panel = new GamePanel();
		panel.setBounds(0, 0, 64 * PIXEL_SIZE, 32 * PIXEL_SIZE);
		cpanel.add(panel);
		
		game.pack();
		game.setVisible(true);
		
		chip8 = new Chip8();
		chip8.initialize();
		chip8.loadGame("Programs/Chip-8 Games/" + GAME + ".ch8");
		
		for (;;)
		{
			long startTime = System.currentTimeMillis();
			chip8.tick();
			
			if (chip8.drawFlag)
			{
				chip8.drawFlag = false;
				panel.gfx = chip8.gfx;
				panel.repaint();
			}
			
			if (chip8.beepFlag)
			{
				clip.stop();
				clip.setMicrosecondPosition(0);
				chip8.beepFlag = false;
				clip.start();
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
class GamePanel extends JPanel
{
	public byte[] gfx;
	public GamePanel()
	{
		gfx = new byte[64 * 48];
	}
	@Override
	public void paintComponent(Graphics g)
	{
		BufferedImage bi = new BufferedImage(64 * Chip8_Emu.PIXEL_SIZE, 32 * Chip8_Emu.PIXEL_SIZE, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < 64; x++)
		{
			for (int y = 0; y < 32; y++)
			{
				for (int i = 0; i < Chip8_Emu.PIXEL_SIZE; i++)
				{
					for (int j = 0; j < Chip8_Emu.PIXEL_SIZE; j++)
					{
						int rgb = 0x000000;
						if (gfx[x + 64 * y] == 1) rgb = 0xFFFFFF;
						bi.setRGB(x * Chip8_Emu.PIXEL_SIZE + i, y * Chip8_Emu.PIXEL_SIZE + j, rgb);
					}
				}
			}
		}
		g.drawImage(bi, 0, 0, null);
	}
}