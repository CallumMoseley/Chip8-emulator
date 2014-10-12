import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Chip8
{
	final char[] FONT_SET = {
		0xF0, 0x90, 0x90, 0x90, 0xF0,
		0x20, 0x60, 0x20, 0x20, 0x70,
		0xF0, 0x10, 0xF0, 0x80, 0xF0,
		0xF0, 0x10, 0xF0, 0x10, 0xF0,
		0x90, 0x90, 0xF0, 0x10, 0x10,
		0xF0, 0x80, 0xF0, 0x10, 0xF0,
		0xF0, 0x80, 0xF0, 0x90, 0xF0,
		0xF0, 0x10, 0x20, 0x40, 0x40,
		0xF0, 0x90, 0xF0, 0x90, 0xF0,
		0xF0, 0x90, 0xF0, 0x10, 0xF0,
		0xF0, 0x90, 0xF0, 0x90, 0x90,
		0xE0, 0x90, 0xE0, 0x90, 0xE0,
		0xF0, 0x80, 0x80, 0x80, 0xF0,
		0xE0, 0x90, 0x90, 0x90, 0xE0,
		0xF0, 0x80, 0xF0, 0x80, 0xF0,
		0xF0, 0x80, 0xF0, 0x80, 0x80
	};
	
	char[] memory = new char[0x1000];
	char[] V = new char[0x10];
	char[] stack = new char[0x10];
	byte[] gfx = new byte[64 * 32];
	byte[] keys = new byte[0x10];
	
	byte sp;
	char I;
	char pc;
	char opcode;
	char delayTimer;
	char soundTimer;
	
	public boolean drawFlag;
	public boolean beepFlag;
	
	public void initialize()
	{
		sp = 0;
		I = 0x200;
		pc = 0x200;
		delayTimer = 0;
		soundTimer = 0;
		
		drawFlag = true;
		
		for (int i = 0; i < FONT_SET.length; i++)
		{
			memory[0x050 + i] = FONT_SET[i];
		}
	}
	
	public void loadGame(String file)
	{
		byte[] game = null;
		try
		{
			game = Files.readAllBytes(Paths.get(file));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		for (int i = 0; i < game.length; i++) {
			memory[0x200 + i] = (char) (game[i] & 0xFF);
		}
	}
	
	public void tick()
	{
		opcode = (char)((memory[pc] << 8) | (memory[pc + 1]));
		
		char NNN = (char) (opcode & 0x0FFF);
		char NN = (char) (opcode & 0x00FF);
		char N = (char) (opcode & 0x000F);
		char X = (char) ((opcode & 0x0F00) >> 8);
		char Y = (char) ((opcode & 0x00F0) >> 4);
		
		boolean incrementPC = true;
		
		switch (opcode & 0xF000)
		{
		case 0x0000:
			switch (opcode & 0x000F)
			{
			case 0x000E:
				sp--;
				pc = stack[sp];
			break;
			case 0x0000:
				gfx = new byte[64 * 32];
				drawFlag = true;
			break;
			}
		break;
		case 0x1000:
			pc = NNN;
			incrementPC = false;
		break;
		case 0x2000:
			stack[sp] = pc;
			sp++;
			pc = NNN;
			incrementPC = false;
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
			V[X] %= 256;
		break;
		case 0x8000:
			switch (opcode & 0x000F)
			{
			case 0x0000:
				V[X] = V[Y];
			break;
			case 0x0001:
				V[X] = (char) (V[X] | V[Y]);
				V[X] %= 256;
			break;
			case 0x0002:
				V[X] = (char) (V[X] & V[Y]);
				V[X] %= 256;
			break;
			case 0x0003:
				V[X] = (char) (V[X] ^ V[Y]);
				V[X] %= 256;
			break;
			case 0x0004:
				V[0xF] = 0;
				if (V[Y] > 0xFF - V[X]) V[0xF] = 1;
				V[X] += V[Y];
				V[X] %= 256;
			break;
			case 0x0005:
				V[0xF] = 1;
				if (V[Y] > V[X]) V[0xF] = 0;
				V[X] -= V[Y];
				V[X] %= 256;
			break;
			case 0x0006:
				V[0xF] = (char) (V[X] & 0x0001);
				V[X] >>= 1;
				V[X] %= 256;
			break;
			case 0x0007:
				V[0xF] = 1;
				if (V[X] > V[Y]) V[0xF] = 0;
				V[X] = (char) (V[Y] - V[X]);
				V[X] %= 256;
			break;
			case 0x000E:
				V[0xF] = (char) (V[X] >> 15);
				V[X] <<= 1;
				V[X] %= 256;
			break;
			}
		break;
		case 0x9000:
			if (V[X] != V[Y]) pc += 2;
		break;
		case 0xA000:
			I = NNN;
		break;
		case 0xB000:
			pc = (char) (NNN + V[0]);
			incrementPC = false;
		break;
		case 0xC000:
			V[X] = (char)((char)(Math.random() * 256) & NN);
		break;
		case 0xD000:
			V[0xF] = 0;
			for (int yline = 0; yline < N; yline++)
			{
				char line = memory[I + yline];
				for (int xline = 0; xline < 8; xline++)
				{
					if ((line & (0x80 >> xline)) != 0)
					{
						try
						{
							if (gfx[V[X] + xline + ((V[Y] + yline) * 64)] == 1)
							{
								V[0xF] = 1;
							}
							gfx[V[X] + xline + ((V[Y] + yline) * 64)] ^= 1;
						}
						catch (Exception e)
						{
						}
					}
				}
			}
			
			drawFlag = true;
		break;
		case 0xE000:
			switch (opcode & 0x00FF)
			{
			case 0x009E:
				if (keys[V[X]] == 1) pc += 2;
			break;
			case 0x00A1:
				if (keys[V[X]] != 1) pc += 2;
			break;
			}
		break;
		case 0xF000:
			switch (opcode & 0x00FF)
			{
			case 0x0007:
				V[X] = delayTimer;
			break;
			case 0x000A:
				boolean keyPress = false;
				
				for (int i = 0; i < 0x10; i++)
				{
					if (keys[i] == 1)
					{
						V[X] = (char)i;
						keyPress = true;
					}
				}
				
				incrementPC = keyPress;
			break;
			case 0x0015:
				delayTimer = V[X];
			break;
			case 0x0018:
				soundTimer = V[X];
			break;
			case 0x001E:
				I += V[X];
			break;
			case 0x0029:
				I = (char) (0x050 + V[X] * 5);
			break;
			case 0x0033:
				memory[I] = (char) (V[X] / 100);
				memory[I + 1] = (char) ((V[X] / 10) % 10);
				memory[I + 2] = (char) (V[X] % 10);
			break;
			case 0x0055:
				for (int i = 0; i <= X; i++)
				{
					memory[I + i] = V[i];
				}
			break;
			case 0x0065:
				for (int i = 0; i <= X; i++)
				{
					V[i] = memory[I + i];
				}
				I += X + 1;
			break;
			}
		break;
		}
		if (incrementPC) pc += 2;
		
		if (delayTimer > 0) delayTimer--;
		if (soundTimer != 0) beepFlag = true;
		if (soundTimer > 0) soundTimer--;
	}
}
