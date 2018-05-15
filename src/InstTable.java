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
			// ���ڷ� ���� �̸��� ������ ����, ��ɾ� ������ �о���� ���� BufferedReader�� ����
			File file = new File(fileName);
			FileReader fileReader = new FileReader(file);
			BufferedReader bufReader = new BufferedReader(fileReader);
			// line: inst.data ���Ͽ��� �о���� �� ����
			// instName: ������ instruction
			// tokens: line�� �и��ϱ� ���� StringTokenizer
			String line = "", instName;
			StringTokenizer tokens;
		
			// ���Ͽ��� �о���� ���� ���� ������ �� �پ� �о� ����
			while((line = bufReader.readLine()) != null){
				// �о� ���� ������ ����(" ") �������� �� �� �и��Ͽ�
				// instruction �̸��� ��� ����
				tokens = new StringTokenizer(line);
				instName = tokens.nextToken(" ");
				// instruction �̸��� key�� �ϴ� �ش� ���ο� ���� instruction ��ü�� HashMap�� ����
				instMap.put(instName, new Instruction(line));
			}
			// �Է¹��۸� ����
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
	
	/**
	 * ���ڷ� ���� ��ɾ��� opcode�� ����
	 * 
	 * @param instName: opcode�� ���� ��ɾ�
	 * @return: �ش� ��ɾ��� opcode ��
	 */
	public int getOpcode(String instName)
	{
		int opcode = 0;
		
		// HashMap���� ���ڷ� ���� ��ɾ key������ ������ �ִ� ���
		// �ش� ��ɾ key�� instruction ��ü�� ���� opcode ����
		if(instMap.containsKey(instName))
			opcode = instMap.get(instName).opcode;
		// �ش� ��ɾ key ������ ������ ���� ���� ���
		// ������ �ǹ̷� -1 ����
		else
			opcode = -1;
		
		// opcode ����
		return opcode;
	}

	/**
	 * ���ڷ� ���� ��ɾ��� �ǿ����� ������ ���Ѵ�.
	 * 
	 * @param instName: �ǿ����� ������ ���� ��ɾ�
	 * @return: �ش� ��ɾ��� �ǿ����� ����
	 */
	public int getNumberOfOperand(String instName)
	{
		int numberOfOperand = 0;
		
		// HashMap���� ���ڷ� ���� ��ɾ key������ ������ �ִ� ���
		// �ش� ��ɾ key�� instruction ��ü�� ���� �ǿ����� ���� ����
		if(instMap.containsKey(instName))
			numberOfOperand = instMap.get(instName).numberOfOperand;
		// �ش� ��ɾ key ������ ������ ���� ���� ���
		// ������ �ǹ̷� -1 ����
		else
			numberOfOperand = -1;
		
		// ���۷��� ���� ����
		return numberOfOperand;
	}
	
	/**
	 * ���ڷ� ���� ��ɾ��� ������ ���Ѵ�.
	 * 
	 * @param instName: ������ ���� ��ɾ�
	 * @return: �ش� ��ɾ��� ����
	 */
	public int getformat(String instName)
	{
		int format = 0;
		
		// HashMap���� ���ڷ� ���� ��ɾ key������ ������ �ִ� ���
		// �ش� ��ɾ key�� instruction ��ü�� ���� ���� ����
		if(instMap.containsKey(instName))
			format = instMap.get(instName).format;
		// �ش� ��ɾ key ������ ������ ���� ���� ���
		// ������ �����Ƿ� 0 ����
		else
			format = 0;
		
		// ���� ����
		return format;
	}
	
	/**
	 * ���ڷ� ���� operator�� ��ɾ����� ���θ� ���Ѵ�.
	 * hash map �󿡼� key�� �����ϴ��� ���Ѵ�.
	 * 
	 * @param name: Ȯ���� operator �̸�
	 * @return: hash map �� �����Ѵٸ� 1, ���ٸ� 0
	 */
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
