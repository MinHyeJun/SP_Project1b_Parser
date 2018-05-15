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

	// ���α׷��� section���� literal table�� �����ϴ� ����
	ArrayList<SymbolTable> literalList;
	// ���α׷��� section���� �����ϴ�(reference) �ɺ� table �����ϴ� ����
	ArrayList<SymbolTable> externalList;
	// ���α׷��� section���� modification record�� �ۼ��ϱ� ���� ���� table �����ϴ� ����
	ArrayList<SymbolTable> modifList;

	static int locCounter; // location counter
	static int programNumber;  // section program ��ȣ �����ϴ� ����

	/**
	 * Ŭ���� �ʱ�ȭ. instruction Table�� �ʱ�ȭ�� ���ÿ� �����Ѵ�.
	 * 
	 * @param instFile
	 *            : instruction ���� �ۼ��� ���� �̸�.
	 */
	public Assembler(String instFile)
	{
		// �ʿ��� ���� �Ҵ�
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
	 * ������� ���� ��ƾ
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
			// ���ڷ� ���� �̸��� ������ ����
			// ������ ������Ʈ �ڵ���� ���� ���� BufferedWriter�� ����
			File file = new File(fileName);
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
			if (file.isFile() && file.canWrite())
			{
				// �ڵ� ����Ʈ�� ���ִ� �ڵ� �� ��ŭ ���� ���
				for (int i = 0; i < codeList.size(); i++)
				{
					bufferedWriter.write(codeList.get(i));
					bufferedWriter.newLine();
					
					// ���� end record�ν� ù��° ���ڰ� 'E'���
					// �� ���α׷� �������� ����
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
			// ���ڷ� ���� �̸��� ������ ����
			// �ɺ� ���̺��� ���� ���� BufferedWriter�� ����
			File file = new File(fileName);
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
			// output: ����� �ɺ� ������ ���� ���ڿ�
			String output;

			if (file.isFile() && file.canWrite())
			{
				for (int i = 0; i < symtabList.size(); i++)
				{
					for (int j = 0; j < symtabList.get(i).getSize(); j++)
					{
						// output = <Symbol>	<location> ���·� ����
						// location ���� 16���� ���� �빮�ڷ� ���
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
		// tokenIndex: ���Ǻ� ��ū ���̺� ���� �ֱ� ������ ��ū�� �ε���
		// line: �ش� ��ū �Ľ��� �ҽ� �ڵ� ����
		// literal: ���۷��忡 ����ִ� ���ͷ�
		// currentToken: �ֱ� ������ ��ū
		int tokenIndex = 0;
		String line, literal;
		Token currentToken;

		// input������ ���� �о���� �ҽ��ڵ��� ���� ����ŭ �ݺ�
		for (int i = 0; i < lineList.size(); i++)
		{
			// lineList�� ���� ��ū �Ľ��� ������ ������
			line = lineList.get(i);

			// ���ο� "START" ���ڿ��� ���Ե� ���
			if (line.contains("START"))
			{
				// ���� �ҽ��ڵ��� �ּҰ��� �����ϴ� locCounter �ʱ�ȭ
				// ���Ǻ� ��ū ���̺� ���� �ֱ� ������ ��ū�� �ε��� �ʱ�ȭ
				// �ʿ��� ���̺� ��ü���� �����Ͽ� �� List�� �־���
				locCounter = 0;
				tokenIndex = 0;
				symtabList.add(new SymbolTable());
				literalList.add(new SymbolTable());
				externalList.add(new SymbolTable());
				modifList.add(new SymbolTable());
				TokenList.add(new TokenTable(symtabList.get(programNumber), literalList.get(programNumber), externalList.get(programNumber), instTable));
			}
			else if (lineList.get(i).contains("CSECT"))
			{
				// ���� ������ ���� programNumber �� ����
				// ���� �ҽ��ڵ��� �ּҰ��� �����ϴ� locCounter �ʱ�ȭ
				// ���Ǻ� ��ū ���̺� ���� �ֱ� ������ ��ū�� �ε��� �ʱ�ȭ
				// �ʿ��� ���̺� ��ü���� �����Ͽ� �� List�� �־���
				programNumber++;
				locCounter = 0;
				tokenIndex = 0;
				symtabList.add(new SymbolTable());
				literalList.add(new SymbolTable());
				externalList.add(new SymbolTable());
				modifList.add(new SymbolTable());
				TokenList.add(new TokenTable(symtabList.get(programNumber), literalList.get(programNumber), externalList.get(programNumber), instTable));
			}
			
			// �ش� �ҽ� �ڵ� ������ token���� �߰�
			TokenList.get(programNumber).putToken(line);
			
			// ������ ������ ��ū�� ������ ����
			currentToken = TokenList.get(programNumber).getToken(tokenIndex);

			// ���̺��� �����ϴµ�, '.'�� �ƴ� ��� �ش� ���̺��� �ɺ����̺� �־���
			if (!currentToken.label.equals("") && !currentToken.label.equals("."))
			{
				// �����ڰ� EQU ���þ��� ���
				// ���̺��� �ɺ���, �ǿ����ڸ� ����Ͽ� �ɺ� �ּҷ� �־���
				if (currentToken.operator.equals("EQU"))
				{
					symtabList.get(programNumber).putSymbol(currentToken.label, operateAddress(currentToken.operand[0]));
				}
				// �̿��� ���
				// ���̺��� �ɺ���, locCounter ���� �ɺ��� �ּҷ� �־���
				else
				{
					symtabList.get(programNumber).putSymbol(currentToken.label, locCounter);
				}

				// �ǿ����ڿ� "=" ǥ�ð� �ִ� ��� (���ͷ��� ���)
				// �ش� �ǿ����ڸ� ���ͷ���, ���ͷ� �ּҷ� 0�� �־���
				if (currentToken.operand != null && currentToken.operand[0].contains("="))
				{
					literalList.get(programNumber).putSymbol(currentToken.operand[0], 0);
				}
			}
			
			// �����ڰ� �����ϴ� ���
			if (currentToken.operator != null)
			{
				// �ش� �����ڰ� "LTORG"�� "END" ���þ��� ���
				if (currentToken.operator.equals("LTORG") || currentToken.operator.equals("END"))
				{
					// �ش� ���α׷����� �����Ǿ��� ���ͷ��� �ּҰ��� locCouner ������ ������
					for (int j = 0; j < literalList.get(programNumber).getSize(); j++)
					{
						literal = literalList.get(programNumber).getSymbol(j);
						literalList.get(programNumber).modifySymbol(literal, locCounter);

						// ���ͷ��� ���� locCounter ���� ������Ŵ
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
				// �����ڰ� "EXTREF" ���þ��� ���
				else if (currentToken.operator.equals("EXTREF"))
				{
					// �ǿ����ڷ� ���� �ɺ� ���� ��ŭ external ���̺� �־���
					for (int j = 0; j < currentToken.operand.length; j++)
						externalList.get(programNumber).putSymbol(currentToken.operand[j], 0);
				}
				// �� ���� ��� �ǿ����ڰ� �����Ѵٸ�
				else if(currentToken.operand != null)
				{
					// external ���̺� ����ִ� ���� ��ŭ modification ���� ���̺� �ۼ�
					for(int j = 0; j < externalList.get(programNumber).getSize(); j++)
					{
						// �ǿ����ڿ� �ɺ��� extref ���� ������ �ɺ��� �ִ� ���
						if(currentToken.operand[0].contains(externalList.get(programNumber).getSymbol(j)))
						{
							// �⺻���� ���� ����� 6���� ����
							int modifSize = 6;
							
							// 4���Ŀ��� �ǿ����ڷ� ����� ��� ���� ������� 5
							if(currentToken.operator.contains("+"))
							{
								modifSize = 5;
							}
							
							// ���۷��忡 (-)������ ���Ե� ���
							// �ɺ� ���꿡 ���� ���ʷ� modif ���̺� �߰�
							// �ɺ�, ������ �ּҰ�, ������ ������
							// +�ɺ�1 / -�ɺ�2
							if(currentToken.operand[0].contains("-"))
							{
								String opSymbols[] = currentToken.operand[0].split("-");
								modifList.get(programNumber).putModifSymbol("+"+opSymbols[0], locCounter + (6-modifSize), modifSize);
								modifList.get(programNumber).putModifSymbol("-"+opSymbols[1], locCounter + (6-modifSize), modifSize);
							}
							// �̿��� ��� +�ɺ� ���·θ� �߰�
							else
								modifList.get(programNumber).putModifSymbol("+"+currentToken.operand[0], locCounter + (6-modifSize), modifSize);
							break;
						}
					}
				}
			}
			// ���� ��ū�� ũ�⸸ŭ locCounter �� ����
			locCounter += currentToken.byteSize;
			//��ū �ε��� �� ����
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
		// currentToken: ������Ʈ �ڵ带 ������ ��ū
		// codeLine: ������Ʈ ���α׷����� ����� �� �ڵ� ����
		// tokenIndex: �� �ڵ���ο� ����� ������Ʈ �ڵ�(��ū) ����
		// lineSize: �� ���ο� ������ �ڵ��� ����Ʈ ��
		Token currentToken;
		String codeLine = "";
		int tokenIndex = 0, lineSize = 0;

		// ���� ���� ��ū���� ������Ʈ �ڵ� ���� ��, ������Ʈ ���α׷� �ڵ� ���� �ۼ�
		for (int i = 0; i < TokenList.size(); i++)
		{
			// �ش� ������ ��ū���� ������Ʈ �ڵ� ����
			for (int j = 0; j < TokenList.get(i).getSize(); j++)
			{
				TokenList.get(i).makeObjectCode(j);
			}

			// �ش� ������ ��ū ���� ��ŭ ����
			for (int j = 0; j < TokenList.get(i).getSize(); j++)
			{
				// ��ū�� �ϳ� ������
				currentToken = TokenList.get(i).getToken(j);

				// �ش� ��ū�� ���̺��� "."�� ���,
				// ���� �ҽ� �ڵ尡 �ƴ� �ּ��̹Ƿ� ����
				if (currentToken.label.equals("."))
				{
					continue;
				}
				// ��ū�� �����ڰ� "START"���þ "CSECT"���þ��� ���
				// Header record �ۼ�
				else if (currentToken.operator.equals("START") || currentToken.operator.equals("CSECT"))
				{
					// ���ο� ���� ���α׷��� �����̹Ƿ� ��ū �ε��� 0���� �ʱ�ȭ
					tokenIndex = 0;

					// ���� �ּҷ� �ش� ���� ���α׷��� ù��° ��ū�� ����ִ� �ּҰ��� ������
					int startAddress = TokenList.get(i).getToken(0).location;
					// �ش� ���� ���α׷��� ��ū���� ����Ʈ ������� ���ͷ����� ũ�⸦ ��� ����
					// ���� ���α׷��� ũ�⸦ ����
					int programSize = 0;
					for (int k = 0; k < TokenList.get(i).getSize(); k++)
						programSize += TokenList.get(i).getToken(k).byteSize;

					for (int k = 0; k < literalList.get(i).getSize(); k++)
						programSize += literalList.get(i).getLiteralSize(k);

					// ���� ���α׷� �̸�, ���� �ּ�, ���α׷� ũ�⸦ ������ Header record �ۼ�
					codeLine = "H" + currentToken.label + " "
							+ String.format("%06X%06X", startAddress, programSize - startAddress);
				}
				// ��ū�� �����ڰ� "EXTDEF" ���þ��� ���
				// Define record �ۼ�
				else if (currentToken.operator.equals("EXTDEF"))
				{
					// �ش� ��ū�� �ǿ����ڷ� ����ִ� ������ �ɺ����� ������ Define record �ۼ�
					codeLine = "D";
					for (int k = 0; k < currentToken.operand.length; k++)
						codeLine += currentToken.operand[k]
								+ String.format("%06X", symtabList.get(i).search(currentToken.operand[k]));
				}
				// ��ū�� �����ڰ� "EXTREF" ���þ��� ���
				// Refer record �ۼ�
				else if (currentToken.operator.equals("EXTREF"))
				{
					// �ش� ��ū�� �ǿ����ڷ� ����ִ� ���� �ɺ����� ������ Refer record �ۼ�
					codeLine = "R";
					for (int k = 0; k < currentToken.operand.length; k++)
						codeLine += currentToken.operand[k];
				}
				// ��ū�� �����ڰ� ��ɾ��� ���
				// Text record �ۼ�
				else if (instTable.isInstruction(currentToken.operator))
				{
					lineSize = 0;
					tokenIndex = j;
					// �� �ڵ� �������� �� ���� ��ū�� �ۼ��� ������ ��
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

					// ���� ��ū �ּҰ�, �� ���ο� ���� ��ū ����Ʈ ũ���� ��, ��ū�� ������Ʈ �ڵ���
					// Text record �ۼ�
					codeLine = "T" + String.format("%06X%02X", currentToken.location, lineSize);

					for (int k = j; k < tokenIndex; k++, j++)
					{
						codeLine += TokenList.get(i).getToken(k).objectCode;
					}

					j--;
				}
				// ��ū�� �����ڰ� "BYTE" ���þ "WORD" ���þ��� ���
				// �� �ٸ� ���� ���� �Ѿ
				else if (currentToken.operator.equals("BYTE") | currentToken.operator.equals("WORD"))
				{
					lineSize = 0;
				}
				// ��ū�� �����ڰ� "LTORG" ���þ "END" ���þ��� ���
				// �ش� ���� ���α׷��� ���ͷ� ������ ������Ʈ �ڵ�� ����
				else if (currentToken.operator.equals("LTORG") || currentToken.operator.equals("END"))
				{
					lineSize = 0;
					// ����� ���ͷ����� ����Ʈ ũ�� ���� ����
					for (int k = 0; k < literalList.get(i).getSize(); k++)
					{
						lineSize += literalList.get(i).getLiteralSize(k);
					}

					// Text record �ۼ�
					codeLine = "T" + String.format("%06X%02X", currentToken.location, lineSize);

					for (int k = 0; k < literalList.get(i).getSize(); k++)
					{
						// �� ���ͷ��� ������ �κи��� ������ ������Ʈ �ڵ� ������
						String literalData = literalList.get(i).getSymbol(k);
						// "X"�� ���·� ǥ��� �������� ��� �� �����ͺκ��� �״�� ������Ʈ �ڵ�� ���
						if (literalData.contains("X"))
						{
							literalData = literalData.replaceAll("X|\'", "");
						}
						// "C"�� ���·� ǥ��� �������� ��� ������ �κ��� �� �ڸ��� �ƽ�Ű�ڵ尪���� ��ȯ�Ͽ�
						// ������Ʈ �ڵ�� ���
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
				else  // �̿��� ���� ������
					continue;

				// ������ ������ �ڵ� ������ code list�� �߰�
				codeList.add(codeLine);
			}
			
			// �� ���α׷��� ���� ������Ʈ ���α׷� �ۼ��� ������ ����
			// Modification record �ۼ�
			// modif table�� �����ص� ���� �������� ��� ���
			for(int j = 0; j < modifList.get(i).getSize(); j++)
				codeList.add("M" + String.format("%06X%02X", modifList.get(i).getLocation(j), modifList.get(i).getModifSize(j)) + modifList.get(i).getSymbol(j));

			// ù��° ���α׷��� ���� ��쿡�� End record�� �Բ� �����ּ� ǥ��
			// �̿��� ���α׷��� ���� ��쿡�� End record�� ǥ��
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
			// ���ڷ� ���� �̸��� ������ ����, input �ҽ��ڵ带 �о���� ���� BufferedReader�� ����
			File file = new File(inputFile);
			FileReader fileReader = new FileReader(file);
			BufferedReader bufReader = new BufferedReader(fileReader);
			// line: input ���Ͽ��� �о���� �� ����
			String line = "";

			// �о���� ���ε��� line list�� ������
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

	/**
	 * �ǿ����ڷ� ������ ���� ��� �ּҰ��� ����Ѵ�
	 * 
	 * @param inputOperand: ������ �ǿ�����
	 * @return: ���� �ּҰ�
	 */
	private int operateAddress(String inputOperand)
	{
		// result: ������ ���� �ּҰ�
		// operands: �ǿ����� ������ �����ڸ� ������ ����� �ɺ���
		int result = 0;
		String operands[];

		// �ǿ����ڰ� "*" �� ��� ���� locCounter�� �ּҰ����� ��ȯ 
		if (inputOperand.equals("*"))
		{
			result = locCounter;
		}
		// �̿��� ���(������ �ʿ��� ���)
		else
		{
			// ���Ե� ������ (-) �� ���
			if (inputOperand.contains("-"))
			{
				// �����ڸ� �������� ����� �ɺ����� ���Ͽ�
				// �ش� �ɺ����� �ּҰ��� ������ (-) ���� ����
				operands = inputOperand.split("-");
				result = symtabList.get(programNumber).search(operands[0])
						- symtabList.get(programNumber).search(operands[1]);
			}
		}
		
		// �ּҰ� ��ȯ
		return result;
	}
}
