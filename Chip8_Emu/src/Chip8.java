import java.nio.file.Files;
import java.nio.file.Paths;

public class Chip8
{
	short opcode;
	
	byte[] memory = new byte[0x1000];
	
	byte[] V = new byte[0x10];
	short I;
	short pc;
	
	byte[] gfx = new byte[0x40 * 0x20];
	byte delay_timer, sound_timer;
	
	short[] stack = new short[0x10];
	short sp;
	
	byte[] key = new byte[0x10];

	boolean drawFlag;

	public void intialize()
	{
		pc = 0x200;
		opcode = 0x0;
		I = 0x0;
		sp = 0x0;		
	}

	public void loadGame(String string)
	{
		try
		{
			byte[] file = Files.readAllBytes(Paths.get(string));
			for (int i = 0x0; i < file.length; i++)
			{
				memory[i + 0x200] = file[i];
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void tick()
	{
		opcode = (short)(memory[b(pc)] << 8 | memory[b(pc) + 1]);
		
		byte X = (byte)(opcode & 0x0F00 >> 8);
		byte Y = (byte)(opcode & 0x00F0 >> 4);
		short NNN = (short)(opcode & 0x0FFF);
		byte NN = (byte)(opcode & 0x00FF);
		byte N = (byte)(opcode & 0x000F);
		
		boolean jump = false;
		
		switch (opcode & 0xF000)
		{
		case 0x0000:
			switch (opcode & 0x000F) {
			case 0x0000:
				
			break;
			case 0x000E:
				
			break;
			}
		break;
		case 0x1000:
			pc = (short)b(NNN);
			jump = true;
		break;
		case 0x2000:
			stack[sp] = pc;
			++sp;
			pc = NNN;
			jump = true;
		break;
		case 0x3000:
			if (V[X] == NN) pc += 2;
		break;
		case 0x4000:
			if (V[X] != NN) pc += 2;
		break;
		case 0x5000:
			if (V[X] == V[Y]) pc += 2;
		break;
		case 0x6000:
			V[X] = NN;
		break;
		case 0x7000:
			V[X] += NN;
		break;
		case 0x8000:
			switch (opcode & 0x000F)
			{
			case 0x0000:
				V[X] = V[Y];
			break;
			case 0x0001:
				V[X] = (byte)(V[X] | V[Y]);
			break;
			case 0x0002:
				V[X] = (byte)(V[X] & V[Y]);
			break;
			case 0x0003:
				V[X] = (byte)(V[X] ^ V[Y]);
			break;
			case 0x0004:
				if (V[Y] > (byte)(0xFF - V[X]))
				{
					V[0xF] = 0x01;
				}
				else
				{
					V[0xF] = 0x00;
				}
				V[X] += V[Y];
			break;
			case 0x0005:
				if (V[Y] > V[X])
				{
					V[0xF] = 0x00;
				}
				else
				{
					V[0xF] = 0x01;
				}
				V[X] -= V[Y];
			break;
			case 0x0006:
				V[0xF] = (byte)(V[X] & 0x0001);
				V[X] >>= 0x01;
			break;
			case 0x0007:
				if (V[X] > V[Y])
				{
					V[0xF] = 0x00;
				}
				else
				{
					V[0xF] = 0x01;
				}
				V[X] = (byte)(V[Y] - V[X]);
			case 0x000E:
				V[0xF] = (byte)(V[X] & 0x8000);
				V[X] <<= 1;
			break;
			}
		break;
		case 0x0009:
			if (V[X] != V[Y]) pc += 2;
		break;
		case 0x000A:
			I = NNN;
		break;
		case 0x000B:
			pc = (short)(NNN + V[0x0]);
		break;
		case 0x000C:
			V[X] = (byte)((byte)(Math.random() * 256) & NN);
		break;
		case 0x000D:
			V[0xF] = 0x00;
			for (int y = 0x00; y < N; y++)
			{
				short pixel = memory[I + y];
				for (int x = 0x00; x < 0x08; x++)
				{
					if ((pixel & (0x80 >> x)) != 0x00)
					{
						if (gfx[(X + x + ((Y + y) * 0x40))] == 0x01)
						{
							V[0xF] = 0x01;
						}
						gfx[X + x + ((Y + y) * 0x40)] ^= 0x01;
					}
				}
			}
			
			drawFlag = true;
		break;
		case 0x000E:
			switch (opcode & 0x00FF)
			{
			case 0x009E:
				if (key[V[X]] != 0x00) pc += 2;
			break;
			case 0x00A1:
				if (key[V[X]] == 0x00) pc += 2;
			break;
			}
		break;
		case 0x000F:
			switch (opcode & 0x00FF)
			{
			case 0x0007:
				V[X] = delay_timer;
			break;
			case 0x000A:
				boolean keyPressed = false;
				for (byte i = 0x00; i < 0x10; i++)
				{
					if (key[i] != 0x0)
					{
						V[X] = i;
						keyPressed = true;
					}
				}
				if (!keyPressed) jump = true;
			break;
			case 0x0015:
				delay_timer = V[X];
			break;
			case 0x0018:
				sound_timer = V[X];
			break;
			case 0x001E:
				I += V[X];
			}
		break;
		}
		if (!jump) pc += 0x02;
	}

	public void setKeys()
	{
		
	}
	static int b(byte a)
	{
		return 0xFF & a;
	}
	static int b(short a)
	{
		return 0xFF & a;
	}
}
