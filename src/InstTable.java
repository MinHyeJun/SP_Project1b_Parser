import java.io.*;
import java.util.HashMap;
import java.util.StringTokenizer;


/**
 * ��� instruction�� ������ �����ϴ� Ŭ����. instruction data���� �����Ѵ�. <br>
 * ���� instruction ���� ����, ���� ��� ����� �����ϴ� �Լ�, ���� ������ �����ϴ� �Լ� ���� ���� �Ѵ�.
 */
public class InstTable {
	/** 
	 * inst.data ������ �ҷ��� �����ϴ� ����.
	 *  ��ɾ��� �̸��� ��������� �ش��ϴ� Instruction�� �������� ������ �� �ִ�.
	 */
	HashMap<String, Instruction> instMap;
	
	/**
	 * Ŭ���� �ʱ�ȭ. �Ľ��� ���ÿ� ó���Ѵ�.
	 * @param instFile : instruction�� ���� ���� ����� ���� �̸�
	 */
	public InstTable(String instFile) {
		instMap = new HashMap<String, Instruction>();
		openFile(instFile);
	}
	
	/**
	 * �Է¹��� �̸��� ������ ���� �ش� ������ �Ľ��Ͽ� instMap�� �����Ѵ�.
	 */
	public void openFile(String fileName) {
		try {
			File file = new File(fileName);
			FileReader fileReader = new FileReader(file);
			BufferedReader bufReader = new BufferedReader(fileReader);
			String line = "", instName;
			StringTokenizer tokens;
		
			while((line = bufReader.readLine()) != null){
				tokens = new StringTokenizer(line);
				instName = tokens.nextToken(" ");
				instMap.put(instName, new Instruction(line));
			}
			bufReader.close();
		}
		catch (FileNotFoundException e) {
			System.out.println("������ �� �� �����ϴ�.");
		}
		catch (IOException e) {
			System.out.println(e);
		}
	}
	
	//get, set, search ���� �Լ��� ���� ����
	public int getOpcode(String instName)
	{
		int opcode = 0;
		
		if(instMap.containsKey(instName))
			opcode = instMap.get(instName).opcode;
		else
			opcode = -1;
		
		return opcode;
	}

	public int getNumberOfOperand(String instName)
	{
		int numberOfOperand = 0;
		
		if(instMap.containsKey(instName))
			numberOfOperand = instMap.get(instName).numberOfOperand;
		else
			numberOfOperand = -1;
		
		return numberOfOperand;
	}
	
	public int getformat(String instName)
	{
		int format = 0;
		
		if(instMap.containsKey(instName))
			format = instMap.get(instName).format;
		else
			format = 0;
		
		return format;
	}
	
	public boolean isInstruction(String name)
	{
		return instMap.containsKey(name);
	}
}
/**
 * ��ɾ� �ϳ��ϳ��� ��ü���� ������ InstructionŬ������ ����.
 * instruction�� ���õ� �������� �����ϰ� �������� ������ �����Ѵ�.
 */
class Instruction {
	/* 
	 * ������ inst.data ���Ͽ� �°� �����ϴ� ������ �����Ѵ�.
	 *  
	 * ex)
	 * String instruction;
	 * int opcode;
	 * int numberOfOperand;
	 * String comment;
	 */
	String instruction;
	int opcode;
	int numberOfOperand;
	
	/** instruction�� �� ����Ʈ ��ɾ����� ����. ���� ���Ǽ��� ���� */
	int format;
	
	/**
	 * Ŭ������ �����ϸ鼭 �Ϲݹ��ڿ��� ��� ������ �°� �Ľ��Ѵ�.
	 * @param line : instruction �����Ϸκ��� ���پ� ������ ���ڿ�
	 */
	public Instruction(String line) {
		parsing(line);
	}
	
	/**
	 * �Ϲ� ���ڿ��� �Ľ��Ͽ� instruction ������ �ľ��ϰ� �����Ѵ�.
	 * @param line : instruction �����Ϸκ��� ���پ� ������ ���ڿ�
	 */
	public void parsing(String line) {
		// TODO Auto-generated method stub
		StringTokenizer tokens = new StringTokenizer(line);
		
		instruction = tokens.nextToken(" ");
		format = Integer.parseInt(tokens.nextToken(" "));
		opcode = Integer.parseInt(tokens.nextToken(" "), 16);
		numberOfOperand = Integer.parseInt(tokens.nextToken(" "));
	}
	
		
	//�� �� �Լ� ���� ����
	
}
