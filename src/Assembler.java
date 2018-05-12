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
	ArrayList<SymbolTable> modifList;

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
		modifList = new ArrayList<SymbolTable>();
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
			if (file.isFile() && file.canWrite())
			{
				for (int i = 0; i < codeList.size(); i++)
				{
					bufferedWriter.write(codeList.get(i));
					bufferedWriter.newLine();
					
					if(codeList.get(i).charAt(0) == 'E')
					{
						bufferedWriter.newLine();
					}
				}
			}
			bufferedWriter.close();
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
				modifList.add(new SymbolTable());
				TokenList.add(new TokenTable(symtabList.get(programNumber), literalList.get(programNumber), externalList.get(programNumber), instTable));
			}
			else if (lineList.get(i).contains("CSECT"))
			{
				programNumber++;
				locCounter = 0;
				tokenIndex = 0;
				symtabList.add(new SymbolTable());
				literalList.add(new SymbolTable());
				externalList.add(new SymbolTable());
				modifList.add(new SymbolTable());
				TokenList.add(new TokenTable(symtabList.get(programNumber), literalList.get(programNumber), externalList.get(programNumber), instTable));
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
				else if (currentToken.operator.equals("EXTREF"))
				{
					for (int j = 0; j < currentToken.operand.length; j++)
						externalList.get(programNumber).putSymbol(currentToken.operand[j], 0);
				}
				else if(currentToken.operand != null)
				{
					for(int j = 0; j < externalList.get(programNumber).getSize(); j++)
					{
						if(currentToken.operand[0].contains(externalList.get(programNumber).getSymbol(j)))
						{
							int modifSize = 6;
							
							if(currentToken.operator.contains("+"))
							{
								modifSize = 5;
							}
							
							if(currentToken.operand[0].contains("-"))
							{
								String opSymbols[] = currentToken.operand[0].split("-");
								modifList.get(programNumber).putModifSymbol("+"+opSymbols[0], locCounter + (6-modifSize), modifSize);
								modifList.get(programNumber).putModifSymbol("-"+opSymbols[1], locCounter + (6-modifSize), modifSize);
							}
							else
								modifList.get(programNumber).putModifSymbol("+"+currentToken.operand[0], locCounter + (6-modifSize), modifSize);
							break;
						}
					}
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
		int tokenIndex = 0, lineSize = 0;

		for (int i = 0; i < TokenList.size(); i++)
		{
			for (int j = 0; j < TokenList.get(i).getSize(); j++)
			{
				TokenList.get(i).makeObjectCode(j);
			}

			for (int j = 0; j < TokenList.get(i).getSize(); j++)
			{
				currentToken = TokenList.get(i).getToken(j);

				if (currentToken.label.equals("."))
				{
					continue;
				}
				else if (currentToken.operator.equals("START") || currentToken.operator.equals("CSECT"))
				{
					tokenIndex = 0;

					int startAddress = TokenList.get(i).getToken(0).location;
					int programSize = 0;
					for (int k = 0; k < TokenList.get(i).getSize(); k++)
						programSize += TokenList.get(i).getToken(k).byteSize;

					for (int k = 0; k < literalList.get(i).getSize(); k++)
						programSize += literalList.get(i).getLiteralSize(k);

					codeLine = "H" + currentToken.label + " "
							+ String.format("%06X%06X", startAddress, programSize - startAddress);
				}
				else if (currentToken.operator.equals("EXTDEF"))
				{
					codeLine = "D";
					for (int k = 0; k < currentToken.operand.length; k++)
						codeLine += currentToken.operand[k]
								+ String.format("%06X", symtabList.get(i).search(currentToken.operand[k]));
				}
				else if (currentToken.operator.equals("EXTREF"))
				{
					codeLine = "R";
					for (int k = 0; k < currentToken.operand.length; k++)
						codeLine += currentToken.operand[k];
				}
				else if (instTable.isInstruction(currentToken.operator))
				{
					lineSize = 0;
					tokenIndex = j;
					while (tokenIndex < TokenList.get(i).getSize())
					{
						if (TokenList.get(i).getToken(tokenIndex).byteSize == 0
								|| TokenList.get(i).getToken(tokenIndex).operator.equals("RESW")
								|| TokenList.get(i).getToken(tokenIndex).operator.equals("RESB")
								|| (lineSize + TokenList.get(i).getToken(tokenIndex).byteSize) > 30)
							break;

						lineSize += TokenList.get(i).getToken(tokenIndex).byteSize;
						tokenIndex++;
					}

					codeLine = "T" + String.format("%06X%02X", currentToken.location, lineSize);

					for (int k = j; k < tokenIndex; k++, j++)
					{
						codeLine += TokenList.get(i).getToken(k).objectCode;
					}

					j--;
				}
				else if (currentToken.operator.equals("BYTE") | currentToken.operator.equals("WORD"))
				{
					lineSize = 0;
				}

				else if (currentToken.operator.equals("LTORG") || currentToken.operator.equals("END"))
				{
					lineSize = 0;
					for (int k = 0; k < literalList.get(i).getSize(); k++)
					{
						lineSize += literalList.get(i).getLiteralSize(k);
					}

					codeLine = "T" + String.format("%06X%02X", currentToken.location, lineSize);

					for (int k = 0; k < literalList.get(i).getSize(); k++)
					{
						String literalData = literalList.get(i).getSymbol(k);
						if (literalData.contains("X"))
						{
							literalData = literalData.replaceAll("X|\'", "");
						}
						else if (literalData.contains("C"))
						{
							String temp = "";
							literalData = literalData.replaceAll("C|\'", "");

							for (int l = 0; l < literalList.get(i).getLiteralSize(k); l++)
								temp += String.format("%02X", (int) literalData.charAt(l));
							literalData = temp;
						}
						codeLine += literalData;
					}
				}
				else
					continue;

				codeList.add(codeLine);
			}
			
			for(int j = 0; j < modifList.get(i).getSize(); j++)
				codeList.add("M" + String.format("%06X%02X", modifList.get(i).getLocation(j), modifList.get(i).getModifSize(j)) + modifList.get(i).getSymbol(j));

			if (i == 0)
				codeList.add("E" + String.format("%06X", TokenList.get(i).getToken(0).location));
			else
				codeList.add("E");
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
