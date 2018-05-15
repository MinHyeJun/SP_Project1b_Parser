import java.util.ArrayList;

/**
 * ����ڰ� �ۼ��� ���α׷� �ڵ带 �ܾ�� ���� �� ��, �ǹ̸� �м��ϰ�, ���� �ڵ�� ��ȯ�ϴ� ������ �Ѱ��ϴ� Ŭ�����̴�. <br>
 * pass2���� object code�� ��ȯ�ϴ� ������ ȥ�� �ذ��� �� ���� symbolTable�� instTable�� ������ �ʿ��ϹǷ�
 * �̸� ��ũ��Ų��.<br>
 * section ���� �ν��Ͻ��� �ϳ��� �Ҵ�ȴ�.
 *
 */
public class TokenTable
{
	public static final int MAX_OPERAND = 3;

	/* bit ������ �������� ���� ���� */
	public static final int nFlag = 32;
	public static final int iFlag = 16;
	public static final int xFlag = 8;
	public static final int bFlag = 4;
	public static final int pFlag = 2;
	public static final int eFlag = 1;

	/* Token�� �ٷ� �� �ʿ��� ���̺���� ��ũ��Ų��. */
	SymbolTable symTab; // symbol table
	SymbolTable litTab;  // literal table
	SymbolTable extTab;  // external (reference) table
	InstTable instTab;  // instruction table

	/** �� line�� �ǹ̺��� �����ϰ� �м��ϴ� ����. */
	ArrayList<Token> tokenList;
	
	// Program Counter �������Ͱ��� �����ϴ� ����
	int programCounter;

	/**
	 * �ʱ�ȭ�ϸ鼭 symTable�� instTable�� ��ũ��Ų��.
	 * 
	 * @param symTab
	 *            : �ش� section�� ����Ǿ��ִ� symbol table
	 * @param instTab
	 *            : instruction ���� ���ǵ� instTable
	 */
	public TokenTable(SymbolTable symTab, SymbolTable litTab, SymbolTable extTab, InstTable instTab)
	{
		//  ArrayList ��ü �Ҵ�, �ʿ��� Table�� ��ũ, �� PC ���� 0���� �ʱ�ȭ
		tokenList = new ArrayList<>();
		this.symTab = symTab;
		this.litTab = litTab;
		this.extTab = extTab;
		this.instTab = instTab;
		programCounter = 0;
	}

	/**
	 * �Ϲ� ���ڿ��� �޾Ƽ� Token������ �и����� tokenList�� �߰��Ѵ�.
	 * 
	 * @param line
	 *            : �и����� ���� �Ϲ� ���ڿ�
	 */
	public void putToken(String line)
	{
		tokenList.add(new Token(line, instTab));
	}

	/**
	 * tokenList���� index�� �ش��ϴ� Token�� �����Ѵ�.
	 * 
	 * @param index
	 * @return : index��ȣ�� �ش��ϴ� �ڵ带 �м��� Token Ŭ����
	 */
	public Token getToken(int index)
	{
		return tokenList.get(index);
	}

	/**
	 * Pass2 �������� ����Ѵ�. instruction table, symbol table ���� �����Ͽ� object code�� �����ϰ�, �̸�
	 * �����Ѵ�.
	 * 
	 * @param index
	 */
	public void makeObjectCode(int index)
	{
		// �ش� index�� ���α׷� �ҽ� ũ�⸦ ���� PC �������� �� ����
		programCounter += tokenList.get(index).byteSize;
		
		// currentToken: �ش� index�� token ����
		// operator: �ش� index�� token operator ����
		// targetAddress: �ش� �ҽ��ڵ��� target address
		// operandData: �ش� �ҽ��ڵ��� operand ����
		// addressData: object code�� ���� �ּҰ��� String���� ����
		Token currentToken = tokenList.get(index);
		String operator = currentToken.operator;
		int targetAddress = 0;
		String operandData, addressData;

		// operator�� ���ٸ� object code�� ���� �ʿ䰡 �����Ƿ� �޼ҵ� ����
		if (operator == null)
			return;

		// operator�� '+'�� ǥ�õǾ� �ִٸ� ����
		// instTable �󿡼� �˻��� ���ϰ� �ϱ� ����
		if (operator.contains("+"))
			operator = operator.replaceAll("[+]", "");

		// operator�� ��ɾ��� ���
		if (instTab.isInstruction(operator))
		{
			// opcode: object code ���� ���� ù��° ����Ʈ ǥ��
			// ��ɾ� ���� �ڵ带 ����
			int opcode = instTab.getOpcode(operator);

			// 3�Ǵ� 4���� ��ɾ��� ���
			if (instTab.getFormat(operator) == 3)
			{
				// ���� token�� nFlag�� iFlag ������ opcode�� ǥ��
				opcode += currentToken.getFlag(nFlag) / iFlag;
				opcode += currentToken.getFlag(iFlag) / iFlag;
				
				// xbpe: object code ���� ���� �ι�° ����Ʈ ǥ��
				// ���� token�� xFlag, bFlag, pFlag, eFlag ������ xbpe�� ǥ��
				int xbpe = 0;
				xbpe += currentToken.getFlag(xFlag);
				xbpe += currentToken.getFlag(bFlag);
				xbpe += currentToken.getFlag(pFlag);
				xbpe += currentToken.getFlag(eFlag);
				
				// ��ɾ��� �ǿ����� ������ 1�� �̻��� ���
				if (instTab.getNumberOfOperand(operator) >= 1)
				{
					// ���� token�� indirect addressing�̳� simple addressing�� ���
					// �� ��� ��� nFlag�� �����Ǿ� �ִ� ����, operand�� ���� �ɺ��� ����
					if (currentToken.getFlag(nFlag) == nFlag)
					{
						// ���� token�� operand�� ����
						operandData = currentToken.operand[0];

						// operand�� '@'�� ǥ�õǾ� �ִٸ� ����
						if (operandData.contains("@"))
							operandData = operandData.replaceAll("@", "");

						// operand�� '=' ǥ�ð� �ִٸ� ����
						// '=' ǥ�ð� �ִٸ� ���ͷ��̶�� ���̹Ƿ�
						// literal table���� ���ͷ� �ּҸ� ã�� Ÿ���ּҷ� ����
						if (operandData.contains("="))
						{
							operandData = operandData.replaceAll("=", "");
							targetAddress = litTab.search(operandData);
						}
						// '='ǥ�ð� ���ٸ� �ɺ��̶�� ���̹Ƿ�
						// symbol table���� �ɺ� �ּҸ� ã�� Ÿ���ּҷ� ����
						else  
							targetAddress = symTab.search(operandData);
					}
					// ���� token�� immediate addressing�� ���
					// iFlag�� �����Ǿ��ִ� ����, operand�� ���� ������ ����
					else if (currentToken.getFlag(iFlag) == iFlag)
					{
						// ���� token�� operand�� ����
						operandData = currentToken.operand[0];

						// operand�� '#'�� ǥ�õǾ� �����Ƿ� ����
						if (operandData.contains("#"))
							operandData = operandData.replaceAll("#", "");

						// operand�� '#'�� ���ŵ� ���� ����κ��� Ÿ���ּҷ� ����
						targetAddress = Integer.parseInt(operandData);
					}
					
					// ���� token�� PC relative�� ���
					// pFlag�� �����Ǿ��ִ� ����
					if (currentToken.getFlag(pFlag) == pFlag)
					{
						// Ÿ�� �ּҿ��� PC ���� ��
						targetAddress -= programCounter;
					}
					// ���� token�� 4������ ���
					// eFlag�� �����Ǿ��ִ� ����
					else if (currentToken.getFlag(eFlag) == eFlag)
					{
						// Ÿ�� �ּҸ� 0���� ����
						targetAddress = 0;
					}
				}
				// �̿��� ���� Ÿ�� �ּҸ� 0���� ����
				else
					targetAddress = 0;
				
				// Ÿ�� �ּҸ� �ش� token�� ����Ʈ ����� ���� �ּҸ� String���� ��ȯ��
				addressData = addressToString(targetAddress, currentToken.byteSize);
				
				// �������� ���� opcode, xbpe, address ������ �����Ͽ� ���� token�� object code�� ����
				currentToken.objectCode = String.format("%02X%01X", opcode, xbpe) + addressData;
			}
			// 2���� ��ɾ��� ���
			else if (instTab.getFormat(operator) == 2)
			{
				// register1, register2: �������� ��ȣ�� ����
				int register1 = 0, register2 = 0;

				// operand�� ������ 1���� ��� 
				if (instTab.getNumberOfOperand(operator) == 1)
				{
					// operand�� �������� ������ ���� �������� ��ȣ ����
					if (currentToken.operand[0].equals("A"))
						register1 = 0;
					else if (currentToken.operand[0].equals("X"))
						register1 = 1;
					else if (currentToken.operand[0].equals("L"))
						register1 = 2;
					else if (currentToken.operand[0].equals("B"))
						register1 = 3;
					else if (currentToken.operand[0].equals("S"))
						register1 = 4;
					else if (currentToken.operand[0].equals("T"))
						register1 = 5;
					
					// �ι�° �������� ��ȣ�� 0���� ����
					register2 = 0;
				}
				// operand�� ������ 2���� ���
				else if (instTab.getNumberOfOperand(operator) == 2)
				{
					// ù��° operand�� �������� ������ ���� ù���� �������� ��ȣ ����
					if (currentToken.operand[0].equals("A"))
						register1 = 0;
					else if (currentToken.operand[0].equals("X"))
						register1 = 1;
					else if (currentToken.operand[0].equals("L"))
						register1 = 2;
					else if (currentToken.operand[0].equals("B"))
						register1 = 3;
					else if (currentToken.operand[0].equals("S"))
						register1 = 4;
					else if (currentToken.operand[0].equals("T"))
						register1 = 5;

					// �ι�° operand�� �������� ������ ���� �ι��� �������� ��ȣ ����
					if (currentToken.operand[1].equals("A"))
						register2 = 0;
					else if (currentToken.operand[1].equals("X"))
						register2 = 1;
					else if (currentToken.operand[1].equals("L"))
						register2 = 2;
					else if (currentToken.operand[1].equals("B"))
						register2 = 3;
					else if (currentToken.operand[1].equals("S"))
						register2 = 4;
					else if (currentToken.operand[1].equals("T"))
						register2 = 5;
				}
				
				// �������� ���� opcode, �� register ������ �����Ͽ� ���� token�� object code�� ���� 
				currentToken.objectCode = String.format("%02X%01X%01X", opcode, register1, register2);
			}
		}
		// operand�� "BYTE"�� "WORD" ���þ��� ���
		else if (operator.equals("BYTE") || operator.equals("WORD"))
		{
			// "BYTE" ���þ��� ���
			if (operator.equals("BYTE"))
			{
				// operand�� ù��° ���ڰ� 'X'�� ��� 16���� ���̶�� ���̹Ƿ�
				if (currentToken.operand[0].charAt(0) == 'X')
				{
					// ���ͷ��� ������ �κи��� ������ �����Ͽ� object code�� ����
					operandData = currentToken.operand[0].replaceAll("X|\'", "");
					currentToken.objectCode = operandData;
				}
			}
			// "WORD" ���þ��� ���
			else if (operator.equals("WORD"))
			{
				// �����ϴ� �ɺ��� operand�� ����ִ��� �˻�
				int i;
				for (i = 0; i < extTab.getSize(); i++)
				{
					if (currentToken.operand[0].contains(extTab.getSymbol(i)))
						break;
				}

				// operand�� �����ϴ� �ɺ��� ���� ��� object code�� 0�� �ڸ����� �°� ����
				if (i < extTab.getSize())
					currentToken.objectCode = String.format("%06X", 0);
			}
		}
	}

	/**
	 * index��ȣ�� �ش��ϴ� object code�� �����Ѵ�.
	 * 
	 * @param index
	 * @return : object code
	 */
	public String getObjectCode(int index)
	{
		return tokenList.get(index).objectCode;
	}

	/**
	 * token table�� ũ�⸦ �����Ѵ�.
	 * 
	 * @return: token table�� ������
	 */
	public int getSize()
	{
		return tokenList.size();
	}

	/**
	 * int������ ���� �ּҰ��� �ʿ��� �ڸ�����ŭ String������ ��ȯ�Ѵ�.
	 * 
	 * @param address: ��ȯ�� �ּҰ�
	 * @param size: �ش� �ּҰ��� ���̴� �ҽ� �ڵ��� byte size
	 * @return String ������ ��ȯ�� �ּҰ�
	 */
	private String addressToString(int address, int size)
	{
		String addressData = "";

		// �ҽ��ڵ尡 4���� ��ɾ��� ��� �ּҰ����� 5�ڸ� ���
		if (size == 4)
			addressData = String.format("%05X", address);
		// 3���� ��ɾ��� ��� �ּҰ����� 3�ڸ� ���
		else
			addressData = String.format("%03X", address);

		// address ���� ������ ��� ����� �ڸ��� ������ �Ұ����ϱ� ������
		// String ������ ��ȯ�� �ּҰ����� ���������� �ʿ��� ��ŭ ���� ������
		if (address < 0)
		{
			if (size == 3)
				addressData = addressData.substring(addressData.length() - 3);
			else if (size == 4)
				addressData = addressData.substring(addressData.length() - 5);
		}
		
		// String ������ ��ȯ�� �ּҰ� ����
		return addressData;
	}
}

/**
 * �� ���κ��� ����� �ڵ带 �ܾ� ������ ������ �� �ǹ̸� �ؼ��ϴ� ���� ���Ǵ� ������ ������ �����Ѵ�. �ǹ� �ؼ��� ������ pass2����
 * object code�� �����Ǿ��� ���� ����Ʈ �ڵ� ���� �����Ѵ�.
 */
class Token
{
	// �ǹ� �м� �ܰ迡�� ���Ǵ� ������
	int location;
	String label;
	String operator;
	String[] operand;
	String comment;
	char nixbpe;

	// object code ���� �ܰ迡�� ���Ǵ� ������
	String objectCode;
	int byteSize;

	// operator�� ��ɾ�����, ���۷��� ������ �� ������
	// Ȯ���ϱ� ���� ��ɾ� ���̺�
	InstTable instTable;

	/**
	 * Ŭ������ �ʱ�ȭ �ϸ鼭 �ٷ� line�� �ǹ� �м��� �����Ѵ�.
	 * 
	 * @param line
	 *            ��������� ����� ���α׷� �ڵ�
	 */
	public Token(String line, InstTable instTable)
	{
		// token parsing�� ���� instruction table�� ��ũ
		this.instTable = instTable;
		// ���ڷ� ���� line�� �Ľ�
		parsing(line);
	}

	/**
	 * line�� �������� �м��� �����ϴ� �Լ�. Token�� �� ������ �м��� ����� �����Ѵ�.
	 * 
	 * @param line
	 *            ��������� ����� ���α׷� �ڵ�.
	 */
	public void parsing(String line)
	{
		// ���ڷ� ���� �ҽ��ڵ� �� ������ �� ������ �и���Ŵ
		// split �޼ҵ�� ������ ������ �Ǵ� String �� ���̿� �ƹ� ���� ���� ��� null�� ��ȯ��
		String units[] = line.split("\t");

		// �и��� ������ ù��°�� "."�� ���(�ּ�)
		if (units[0].equals("."))
		{
			// "." �� label�� ����
			label = units[0];

			// "." �� �ڸ�Ʈ�� �����ϴ� ��� �ڸ�Ʈ�� ����
			if (units.length > 1)
				comment = units[1];
		}
		// "."�� �ƴ� �ҽ��ڵ���� ���
		else
		{
			// label�� operator ���� �־���
			// label�� �������� �ʴ� ��쿡�� null�� ���� ��
			label = units[0];
			operator = units[1];

			// �ش� line�� ���ִ� �����ڰ� ��ɾ��� �ǿ����� ������ 0�� �ƴ� ��쿡�� operand�� �־���
			// RSUB�� ����, ��ɾ������� �ǿ����� ������ 0���� ��
			// �ڸ�Ʈ�� operand�� �߸� ���� ���� �����ϱ� ����
			if (!(instTable.getNumberOfOperand(operator) == 0))
			{
				// �ǿ����ڰ� ������ �� ","�� �������� ������ operand�� ����
				if (units.length > 2)
					operand = units[2].split(",", TokenTable.MAX_OPERAND);

				// �ڸ�Ʈ�� ������ ��� comment�� ����
				if (units.length > 3)
					comment = units[3];
			}
			// �ǿ����ڰ� ���� ��ɾ ���� �ҽ��ڵ尡 �ڸ�Ʈ�� �����ϸ� comment�� �־���
			else
			{
				if (units[2] != null)
					comment = units[2];
			}

			// operator�� "+"�� ǥ�õǾ��ִ� ��� 4����
			// �ҽ��ڵ� ũ��� 4 ����
			// eFlag ����
			if (operator.contains("+"))
			{
				byteSize = 4;
				setFlag(TokenTable.eFlag, 1);
			}
			// �̿��� ��� �ش� ��ɾ��� ������ �ҽ��ڵ� ũ��� ����
			// ��ɾ �ƴ� ��� 0�� ����
			// �ǿ����ڰ� �����ϸ鼭�� ũ�Ⱑ 0���� ũ��(�����ڰ� ��ɾ��� ���)
			// PC relative�� ���� pFalg ����
			else
			{
				byteSize = getInstSize(operator);

				if (operand != null && byteSize > 0)
					setFlag(TokenTable.pFlag, 1);
			}
			
			// �ҽ� �ڵ��� ũ�Ⱑ 3�̻��� ���,
			// �ش� �ҽ� �ڵ��� �����ڰ� 3�Ǵ� 4������ �����ϴ� ��ɾ��̹Ƿ�
			// ��Ȳ�� ���� Flag���� ����
			if (byteSize >= 3)
			{
				// �ǿ����ڰ� �ִ� ���
				if (operand != null)
				{
					// �ǿ����ڰ� 1�� �̻��ε�, �ι��� �ǿ����ڰ� 'X'�� ���
					// looping�� ���� indexed addressing�� ����ϹǷ� xFlag ǥ��
					if (operand.length > 1 && operand[1].equals("X"))
					{
						setFlag(TokenTable.xFlag, 1);
					}

					// �ǿ����ڿ� "#"�� ǥ�õ� ���
					// immediate addressing�� ����ϹǷ� iFlag ǥ�� �� pFlag ǥ�� ����
					if (operand[0].contains("#"))
					{
						setFlag(TokenTable.iFlag, 1);
						setFlag(TokenTable.pFlag, 0);
					}
					// �ǿ����ڿ� "@"�� ǥ�õ� ���
					// indirect addressing�� ����ϹǷ� nFlag ǥ��
					else if (operand[0].contains("@"))
					{
						setFlag(TokenTable.nFlag, 1);
					}
					// �� �̿��� ���� simple addressing�̹Ƿ� nFlag�� iFlag ǥ��
					else
					{
						setFlag(TokenTable.nFlag, 1);
						setFlag(TokenTable.iFlag, 1);
					}
				}
			}
			// �ҽ� �ڵ� �ּҰ����� ���� token�� �ּҰ��� �����ϰ� �ִ�
			// Assembler class ���� locCounter ���� ����
			location = Assembler.locCounter;
		}
	}

	/**
	 * n,i,x,b,p,e flag�� �����Ѵ�. <br>
	 * <br>
	 * 
	 * ��� �� : setFlag(nFlag, 1); <br>
	 * �Ǵ� setFlag(TokenTable.nFlag, 1);
	 * 
	 * @param flag
	 *            : ���ϴ� ��Ʈ ��ġ
	 * @param value
	 *            : ����ְ��� �ϴ� ��. 1�Ǵ� 0���� �����Ѵ�.
	 */
	public void setFlag(int flag, int value)
	{
		if (value == 1)
			nixbpe |= flag;
		else
			nixbpe ^= flag;
	}

	/**
	 * ���ϴ� flag���� ���� ���� �� �ִ�. flag�� ������ ���� ���ÿ� �������� �÷��׸� ��� �� ���� �����ϴ� <br>
	 * <br>
	 * 
	 * ��� �� : getFlag(nFlag) <br>
	 * �Ǵ� getFlag(nFlag|iFlag)
	 * 
	 * @param flags
	 *            : ���� Ȯ���ϰ��� �ϴ� ��Ʈ ��ġ
	 * @return : ��Ʈ��ġ�� �� �ִ� ��. �÷��׺��� ���� 32, 16, 8, 4, 2, 1�� ���� ������ ����.
	 */
	public int getFlag(int flags)
	{
		return nixbpe & flags;
	}

	/**
	 * �ش� operator�� ����Ʈ ũ�⸦ ���ϱ� ���� �޼ҵ��̴�.
	 * 
	 * @param operator: ũ�⸦ ���� operator
	 * @return: operator�� ����Ʈ ũ��
	 */
	public int getInstSize(String operator)
	{
		int size = 0;

		// ��ɾ��� ���
		if (instTable.isInstruction(operator))
		{
			// instruction table ���� ������ ũ�Ⱚ���μ� ����
			size = instTable.getFormat(operator);
		}
		// "RESB" ���þ��� ���
		else if (operator.equals("RESB"))
		{
			// operand�� ������ ũ�⸸ŭ�� ũ�Ⱚ���� ����
			size = Integer.parseInt(operand[0]);
		}
		// "RESW" ���þ��� ���
		else if (operator.equals("RESW"))
		{
			// operand�� ������ ũ�⿡ 3�� ���� ���� ũ�Ⱚ���� ����
			size = Integer.parseInt(operand[0]) * 3;
		}
		// "BYTE" ���þ��� ���
		else if (operator.equals("BYTE"))
		{
			// BYTE�� ũ�Ⱑ 1�̹Ƿ� 1�� ũ�Ⱚ���� ���� 
			size = 1;
		}
		// "WORD" ���þ��� ���
		else if (operator.equals("WORD"))
		{
			// WORD�� ũ�Ⱑ 3�̹Ƿ� 3�� ũ�Ⱚ���� ����
			size = 3;
		}
		// �̿��� �����ڴ� �޸𸮸� �������� �����Ƿ� 0���� ����
		else
			size = 0;

		return size;
	}
}
