import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;


/**
 * Assembler : 
 * �� ���α׷��� SIC/XE �ӽ��� ���� Assembler ���α׷��� ���� ��ƾ�̴�.
 * ���α׷��� ���� �۾��� ������ ����. <br>
 * 1) ó�� �����ϸ� Instruction ���� �о�鿩�� assembler�� �����Ѵ�. <br>
 * 2) ����ڰ� �ۼ��� input ������ �о���� �� �����Ѵ�. <br>
 * 3) input ������ ������� �ܾ�� �����ϰ� �ǹ̸� �ľ��ؼ� �����Ѵ�. (pass1) <br>
 * 4) �м��� ������ �������� ��ǻ�Ͱ� ����� �� �ִ� object code�� �����Ѵ�. (pass2) <br>
 * 
 * <br><br>
 * �ۼ����� ���ǻ��� : <br>
 *  1) ���ο� Ŭ����, ���ο� ����, ���ο� �Լ� ������ �󸶵��� ����. ��, ������ ������ �Լ����� �����ϰų� ������ ��ü�ϴ� ���� �ȵȴ�.<br>
 *  2) ���������� �ۼ��� �ڵ带 �������� ������ �ʿ信 ���� ����ó��, �������̽� �Ǵ� ��� ��� ���� ����.<br>
 *  3) ��� void Ÿ���� ���ϰ��� ������ �ʿ信 ���� �ٸ� ���� Ÿ������ ���� ����.<br>
 *  4) ����, �Ǵ� �ܼ�â�� �ѱ��� ��½�Ű�� �� ��. (ä������ ����. �ּ��� ���Ե� �ѱ��� ��� ����)<br>
 * 
 * <br><br>
 *  + �����ϴ� ���α׷� ������ ��������� �����ϰ� ���� �е��� ������ ��� �޺κп� ÷�� �ٶ��ϴ�. ���뿡 ���� �������� ���� �� �ֽ��ϴ�.
 */
public class Assembler {
	/** instruction ���� ������ ���� */
	InstTable instTable;
	/** �о���� input ������ ������ �� �� �� �����ϴ� ����. */
	ArrayList<String> lineList;
	/** ���α׷��� section���� symbol table�� �����ϴ� ����*/
	ArrayList<SymbolTable> symtabList;
	/** ���α׷��� section���� ���α׷��� �����ϴ� ����*/
	ArrayList<TokenTable> TokenList;
	/** 
	 * Token, �Ǵ� ���þ ���� ������� ������Ʈ �ڵ���� ��� ���·� �����ϴ� ����. <br>
	 * �ʿ��� ��� String ��� ������ Ŭ������ �����Ͽ� ArrayList�� ��ü�ص� ������.
	 */
	ArrayList<String> codeList;
	
	static int locCounter;
	
	/**
	 * Ŭ���� �ʱ�ȭ. instruction Table�� �ʱ�ȭ�� ���ÿ� �����Ѵ�.
	 * 
	 * @param instFile : instruction ���� �ۼ��� ���� �̸�. 
	 */
	public Assembler(String instFile) {
		instTable = new InstTable(instFile);
		lineList = new ArrayList<String>();
		symtabList = new ArrayList<SymbolTable>();
		TokenList = new ArrayList<TokenTable>();
		codeList = new ArrayList<String>();
	}

	/** 
	 * ��U���� ���� ��ƾ
	 */
	public static void main(String[] args) {
		Assembler assembler = new Assembler("inst.data");
		assembler.loadInputFile("input.txt");
		
		assembler.pass1();
		assembler.printSymbolTable("symtab_00000000");
		
		assembler.pass2();
		assembler.printObjectCode("output_00000000");
		
	}

	/**
	 * �ۼ��� codeList�� ������¿� �°� ����Ѵ�.<br>
	 * @param fileName : ����Ǵ� ���� �̸�
	 */
	private void printObjectCode(String fileName) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * �ۼ��� SymbolTable���� ������¿� �°� ����Ѵ�.<br>
	 * @param fileName : ����Ǵ� ���� �̸�
	 */
	private void printSymbolTable(String fileName) {
		// TODO Auto-generated method stub
		try
		{
			File file = new File(fileName);
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
			String output;
		
			if(file.isFile() && file.canWrite())
			{
				for(int i = 0; i < symtabList.size(); i++)
				{
					for(int j = 0; j < symtabList.get(i).getSize(); j++)
					{
						output = symtabList.get(i).getSymbol(j) + "\t" + symtabList.get(i).getLocation(j);
						
						bufferedWriter.write(output);
						System.out.println(output);
					}
					
					bufferedWriter.newLine();
					System.out.println();
				}
			}
			
			bufferedWriter.close();
		}
		catch (IOException e)
		{
			System.err.println(e);
		}
		
	}

	/** 
	 * pass1 ������ �����Ѵ�.<br>
	 *   1) ���α׷� �ҽ��� ��ĵ�Ͽ� ��ū������ �и��� �� ��ū���̺� ����<br>
	 *   2) label�� symbolTable�� ����<br>
	 *   <br><br>
	 *    ���ǻ��� : SymbolTable�� TokenTable�� ���α׷��� section���� �ϳ��� ����Ǿ�� �Ѵ�.
	 */
	private void pass1() {
		int program_num = 0, tokenIndex = 0;
		// TODO Auto-generated method stub
		String line;
		for(int i  = 0; i < lineList.size(); i++)
		{
			line = lineList.get(i);
			
			if(line.contains("START"))
			{
				locCounter = 0;
				symtabList.add(new SymbolTable());
				TokenList.add(new TokenTable(symtabList.get(program_num), instTable));
			}
			else if(lineList.get(i).contains("CSECT"))
			{
				program_num ++;
				locCounter = 0;
				tokenIndex = 0;
				symtabList.add(new SymbolTable());
				TokenList.add(new TokenTable(symtabList.get(program_num), instTable));
			}
			
			TokenList.get(program_num).putToken(line);
			
			if(TokenList.get(program_num).getToken(tokenIndex).label != null)
			{
				symtabList.get(program_num).putSymbol(TokenList.get(program_num).getToken(tokenIndex).label, locCounter);
				locCounter += TokenList.get(program_num).getToken(tokenIndex).byteSize;
			}
			tokenIndex ++;
		}
	}
	
	/**
	 * pass2 ������ �����Ѵ�.<br>
	 *   1) �м��� ������ �������� object code�� �����Ͽ� codeList�� ����.
	 */
	private void pass2() {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * inputFile�� �о�鿩�� lineList�� �����Ѵ�.<br>
	 * @param inputFile : input ���� �̸�.
	 */
	private void loadInputFile(String inputFile) {
		// TODO Auto-generated method stub
		try {
			File file = new File(inputFile);
			FileReader fileReader = new FileReader(file);
			BufferedReader bufReader = new BufferedReader(fileReader);
			String line = "";
		
			while((line = bufReader.readLine()) != null){
				lineList.add(line);
			}
		}
		catch (FileNotFoundException e) {
			System.out.println("������ �� �� �����ϴ�.");
		}
		catch (IOException e) {
			System.out.println(e);
		}
	}
}
