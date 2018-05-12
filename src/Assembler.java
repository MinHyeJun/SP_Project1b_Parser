import java.io.*;
import java.util.ArrayList;

/**
 * Assembler : �� ���α׷��� SIC/XE �ӽ��� ���� Assembler ���α׷��� ���� ��ƾ�̴�. ���α׷��� ���� �۾��� ������
 * ����. <br>
 * 1) ó�� �����ϸ� Instruction ���� �о�鿩�� assembler�� �����Ѵ�. <br>
 * 2) ����ڰ� �ۼ��� input ������ �о���� �� �����Ѵ�. <br>
 * 3) input ������ ������� �ܾ�� �����ϰ� �ǹ̸� �ľ��ؼ� �����Ѵ�. (pass1) <br>
 * 4) �м��� ������ �������� ��ǻ�Ͱ� ����� �� �ִ� object code�� �����Ѵ�. (pass2) <br>
 * 
 * <br>
 * <br>
 * �ۼ����� ���ǻ��� : <br>
 * 1) ���ο� Ŭ����, ���ο� ����, ���ο� �Լ� ������ �󸶵��� ����. ��, ������ ������ �Լ����� �����ϰų� ������ ��ü�ϴ� ����
 * �ȵȴ�.<br>
 * 2) ���������� �ۼ��� �ڵ带 �������� ������ �ʿ信 ���� ����ó��, �������̽� �Ǵ� ��� ��� ���� ����.<br>
 * 3) ��� void Ÿ���� ���ϰ��� ������ �ʿ信 ���� �ٸ� ���� Ÿ������ ���� ����.<br>
 * 4) ����, �Ǵ� �ܼ�â�� �ѱ��� ��½�Ű�� �� ��. (ä������ ����. �ּ��� ���Ե� �ѱ��� ��� ����)<br>
 * 
 * <br>
 * <br>
 * + �����ϴ� ���α׷� ������ ��������� �����ϰ� ���� �е��� ������ ��� �޺κп� ÷�� �ٶ��ϴ�. ���뿡 ���� �������� ���� ��
 * �ֽ��ϴ�.
 */
public class Assembler
{
	/** instruction ���� ������ ���� */
	InstTable instTable;
	/** �о���� input ������ ������ �� �� �� �����ϴ� ����. */
	ArrayList<String> lineList;
	/** ���α׷��� section���� symbol table�� �����ϴ� ���� */
	ArrayList<SymbolTable> symtabList;
	/** ���α׷��� section���� ���α׷��� �����ϴ� ���� */
	ArrayList<TokenTable> TokenList;
	/**
	 * Token, �Ǵ� ���þ ���� ������� ������Ʈ �ڵ���� ��� ���·� �����ϴ� ����. <br>
	 * �ʿ��� ��� String ��� ������ Ŭ������ �����Ͽ� ArrayList�� ��ü�ص� ������.
	 */
	ArrayList<String> codeList;

	ArrayList<SymbolTable> literalList;
	ArrayList<SymbolTable> externalList;

	static int locCounter;
	static int programNumber;

	/**
	 * Ŭ���� �ʱ�ȭ. instruction Table�� �ʱ�ȭ�� ���ÿ� �����Ѵ�.
	 * 
	 * @param instFile
	 *            : instruction ���� �ۼ��� ���� �̸�.
	 */
	public Assembler(String instFile)
	{
		instTable = new InstTable(instFile);
		lineList = new ArrayList<String>();
		symtabList = new ArrayList<SymbolTable>();
		literalList = new ArrayList<SymbolTable>();
		externalList = new ArrayList<SymbolTable>();
		TokenList = new ArrayList<TokenTable>();
		codeList = new ArrayList<String>();
	}

	/**
	 * ��U���� ���� ��ƾ
	 */
	public static void main(String[] args)
	{
		Assembler assembler = new Assembler("inst.data");
		assembler.loadInputFile("input.txt");

		assembler.pass1();
		assembler.printSymbolTable("symtab_20160286");

		assembler.pass2();
		assembler.printObjectCode("output_20160286");

	}

	/**
	 * �ۼ��� codeList�� ������¿� �°� ����Ѵ�.<br>
	 * 
	 * @param fileName
	 *            : ����Ǵ� ���� �̸�
	 */
	private void printObjectCode(String fileName)
	{
		// TODO Auto-generated method stub
		try
		{
			File file = new File(fileName);
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
			String output;
			if (file.isFile() && file.canWrite())
			{
				for (int i = 0; i < TokenList.size(); i++)
				{
					for (int j = 0; j < TokenList.get(i).getSize(); j++)
					{
						System.out.println(TokenList.get(i).getObjectCode(j));
					}
				}
				
				for(int i = 0; i < codeList.size(); i++)
				{
					System.out.println(codeList.get(i));
				}
			}
		}
		catch (IOException e)
		{

		}
	}

	/**
	 * �ۼ��� SymbolTable���� ������¿� �°� ����Ѵ�.<br>
	 * 
	 * @param fileName
	 *            : ����Ǵ� ���� �̸�
	 */
	private void printSymbolTable(String fileName)
	{
		// TODO Auto-generated method stub
		try
		{
			File file = new File(fileName);
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
			String output;

			if (file.isFile() && file.canWrite())
			{
				for (int i = 0; i < symtabList.size(); i++)
				{
					for (int j = 0; j < symtabList.get(i).getSize(); j++)
					{
						output = symtabList.get(i).getSymbol(j) + "\t"
								+ Integer.toHexString(symtabList.get(i).getLocation(j)).toUpperCase();

						bufferedWriter.write(output);
						bufferedWriter.newLine();
					}

					bufferedWriter.newLine();
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
	 * 1) ���α׷� �ҽ��� ��ĵ�Ͽ� ��ū������ �и��� �� ��ū���̺� ����<br>
	 * 2) label�� symbolTable�� ����<br>
	 * <br>
	 * <br>
	 * ���ǻ��� : SymbolTable�� TokenTable�� ���α׷��� section���� �ϳ��� ����Ǿ�� �Ѵ�.
	 */
	private void pass1()
	{
		// TODO Auto-generated method stub
		int tokenIndex = 0;
		String line, literal;
		Token currentToken;

		for (int i = 0; i < lineList.size(); i++)
		{
			line = lineList.get(i);

			if (line.contains("START"))
			{
				locCounter = 0;
				symtabList.add(new SymbolTable());
				literalList.add(new SymbolTable());
				externalList.add(new SymbolTable());
				TokenList.add(new TokenTable(symtabList.get(programNumber), literalList.get(programNumber), instTable));
			}
			else if (lineList.get(i).contains("CSECT"))
			{
				programNumber++;
				locCounter = 0;
				tokenIndex = 0;
				symtabList.add(new SymbolTable());
				literalList.add(new SymbolTable());
				externalList.add(new SymbolTable());
				TokenList.add(new TokenTable(symtabList.get(programNumber), literalList.get(programNumber), instTable));
			}

			TokenList.get(programNumber).putToken(line);

			currentToken = TokenList.get(programNumber).getToken(tokenIndex);

			if (!currentToken.label.equals("") && !currentToken.label.equals("."))
			{
				if (currentToken.operator.equals("EQU"))
				{
					symtabList.get(programNumber).putSymbol(currentToken.label,
							operateAddress(currentToken.operand[0]));
				}
				else
				{
					symtabList.get(programNumber).putSymbol(currentToken.label, locCounter);
				}

				if (currentToken.operand != null && currentToken.operand[0].contains("="))
				{
					literalList.get(programNumber).putSymbol(currentToken.operand[0], 0);
				}
			}

			if (currentToken.operator != null)
			{
				if (currentToken.operator.equals("LTORG") || currentToken.operator.equals("END"))
				{
					for (int j = 0; j < literalList.get(programNumber).getSize(); j++)
					{
						literal = literalList.get(programNumber).getSymbol(j);
						literalList.get(programNumber).modifySymbol(literal, locCounter);

						if (literal.contains("X"))
						{
							locCounter++;
						}
						else if (literal.contains("C"))
						{
							literal = literal.replaceAll("C|\'", "");
							locCounter += literal.length();
						}
					}
				}
				else if(currentToken.operator.equals("EXTREF"))
				{
					for(int j = 0; j < currentToken.operand.length; j++)
					externalList.get(programNumber).putSymbol(currentToken.operand[j], 0);
				}
			}
			locCounter += currentToken.byteSize;
			tokenIndex++;
		}
	}

	/**
	 * pass2 ������ �����Ѵ�.<br>
	 * 1) �м��� ������ �������� object code�� �����Ͽ� codeList�� ����.
	 */
	private void pass2()
	{
		// TODO Auto-generated method stub
		Token currentToken;
		String codeLine = "";
		
		for (int i = 0; i < TokenList.size(); i++)
		{
			for (int j = 0; j < TokenList.get(i).getSize(); j++)
			{
				TokenList.get(i).makeObjectCode(j);
			}
			
			for(int j = 0; j < TokenList.get(i).getSize(); j++)
			{
				currentToken = TokenList.get(i).getToken(j);
				
				if(currentToken.label.equals("."))
				{
					continue;
				}
				else if(currentToken.operator.equals("START") || currentToken.operator.equals("CSECT"))
				{
					int startAddress = TokenList.get(i).getToken(0).location;
					int programSize = 0;
					for(int  k = 0; k < TokenList.get(i).getSize(); k++)
						programSize += TokenList.get(i).getToken(k).byteSize;
					
					for(int k = 0; k < literalList.get(i).getSize(); k++)
						programSize += literalList.get(i).getLiteralSize(k);
					
					codeLine = "H" + currentToken.label + String.format("%06X%06X", startAddress, programSize-startAddress);   /////3��° ���α׷� ũ�Ⱑ ���� (���ͷ��� �߰��������)
				}
				else if(currentToken.operator.equals("EXTDEF"))
				{
					codeLine = "D";
					for(int k = 0; k < currentToken.operand.length; k++)
						codeLine += currentToken.operand[k];
				}
				else if(currentToken.operator.equals("EXTREF"))
				{
					codeLine = "R";
					for(int k = 0; k < currentToken.operand.length; k++)
						codeLine += currentToken.operand[k];
				}
				else
				{
					continue;
				}
				
				codeList.add(codeLine);
			}
		}
	}

	/**
	 * inputFile�� �о�鿩�� lineList�� �����Ѵ�.<br>
	 * 
	 * @param inputFile
	 *            : input ���� �̸�.
	 */
	private void loadInputFile(String inputFile)
	{
		// TODO Auto-generated method stub
		try
		{
			File file = new File(inputFile);
			FileReader fileReader = new FileReader(file);
			BufferedReader bufReader = new BufferedReader(fileReader);
			String line = "";

			while ((line = bufReader.readLine()) != null)
			{
				lineList.add(line);
			}
			bufReader.close();
		}
		catch (FileNotFoundException e)
		{
			System.out.println("������ �� �� �����ϴ�.");
		}
		catch (IOException e)
		{
			System.out.println(e);
		}
	}

	private int operateAddress(String inputOperand)
	{
		int result = 0;
		String operands[];

		if (inputOperand.equals("*"))
		{
			result = locCounter;
		}
		else
		{
			if (inputOperand.contains("-"))
			{
				operands = inputOperand.split("-");
				result = symtabList.get(programNumber).search(operands[0])
						- symtabList.get(programNumber).search(operands[1]);
			}
		}
		return result;
	}
}
