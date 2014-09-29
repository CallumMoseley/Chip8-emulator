public class Chip8_Emu
{
	public static void main(String[] args)
	{
		Chip8 chip8 = new Chip8();
		chip8.intialize();
		chip8.loadGame("pong");
		
		for (;;)
		{
			chip8.tick();
			
			if (chip8.drawFlag) {
				drawGraphics();
			}
			
			chip8.setKeys();
		}
	}

	static void drawGraphics()
	{
		
	}
}