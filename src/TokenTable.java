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
	SymbolTable symTab;
	SymbolTable litTab;
	InstTable instTab;

	/** �� line�� �ǹ̺��� �����ϰ� �м��ϴ� ����. */
	ArrayList<Token> tokenList;
	
	int programCounter;

	/**
	 * �ʱ�ȭ�ϸ鼭 symTable�� instTable�� ��ũ��Ų��.
	 * 
	 * @param symTab
	 *            : �ش� section�� ����Ǿ��ִ� symbol table
	 * @param instTab
	 *            : instruction ���� ���ǵ� instTable
	 */
	public TokenTable(SymbolTable symTab, SymbolTable litTab, InstTable instTab)
	{
		// ...
		tokenList = new ArrayList<>();
		this.symTab = symTab;
		this.litTab = litTab;
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
	 * Pass2 �������� ����Ѵ�. instruction table, symbol table ���� �����Ͽ� objectcode�� �����ϰ�, �̸�
	 * �����Ѵ�.
	 * 
	 * @param index
	 */
	public void makeObjectCode(int index)
	{
		// ...
		programCounter += tokenList.get(index).byteSize;
		Token currentToken = tokenList.get(index);
		String operator = currentToken.operator;
		int targetAddress = 0;
		String operandData, addressData;
		
		if(operator == null)
			return;
		
		if (operator.contains("+"))
			operator = operator.replaceAll("[+]", "");

		if (instTab.isInstruction(operator))
		{
			int opcode = instTab.getOpcode(operator);

			if (instTab.getformat(operator) == 3)
			{
				opcode += currentToken.getFlag(nFlag) / iFlag;
				opcode += currentToken.getFlag(iFlag) / iFlag;

				int xbpe = 0;
				xbpe += currentToken.getFlag(xFlag);
				xbpe += currentToken.getFlag(bFlag);
				xbpe += currentToken.getFlag(pFlag);
				xbpe += currentToken.getFlag(eFlag);

				if (instTab.getNumberOfOperand(operator) >= 1)
				{

					if (currentToken.getFlag(nFlag) == nFlag)
					{
						operandData = currentToken.operand[0];

						if (operandData.contains("@"))
							operandData = operandData.replaceAll("@", "");
						
						if(operandData.contains("="))
						{
							operandData = operandData.replaceAll("=", "");
							targetAddress = litTab.search(operandData);
						}
						else
							targetAddress = symTab.search(operandData);
					}
					else if (currentToken.getFlag(iFlag) == iFlag)
					{
						operandData = currentToken.operand[0];

						if (operandData.contains("#"))
							operandData = operandData.replaceAll("#", "");

						targetAddress = Integer.parseInt(operandData);
					}

					if (currentToken.getFlag(pFlag) == pFlag)
					{
						targetAddress -= programCounter;
					}
					else if(currentToken.getFlag(eFlag) == eFlag)
					{
						targetAddress = 0;
					}
				}
				else
					targetAddress = 0;
				
				addressData = addressToString(targetAddress, currentToken.byteSize);

				currentToken.objectCode = String.format("%02X%01X", opcode, xbpe) + addressData;
				// �ڹ� String Format ����� ��
			}
			else
			{
				int register1 = 0, register2 = 0;

				if (instTab.getNumberOfOperand(operator) == 1)
				{
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
				}
				else
				{
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

				currentToken.objectCode = String.format("%02X%01X%01X", opcode, register1, register2);
				// currentToken.objectCode = Integer.toHexString(opcode).toUpperCase() +
				// register1 + register2;
			}
		}
		else if (operator.equals("BYTE") || operator.equals("WORD"))
		{
			if(currentToken.operand[0].contains("X"))
			{
				operandData = currentToken.operand[0].replaceAll("X|\'", "");
				currentToken.objectCode = operandData;
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

	public int getSize()
	{
		return tokenList.size();
	}
	
	private String addressToString(int address, int size)
	{
		String addressData;
		
		if(size == 4)
			addressData = String.format("%05X", address);
		else
			addressData = String.format("%03X", address);
		
		if(address < 0)
		{
			if(size == 3)
				addressData = addressData.substring(addressData.length()-3);
			else if(size == 4)
				addressData = addressData.substring(addressData.length()-5);
		}

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

	InstTable instTable;

	/**
	 * Ŭ������ �ʱ�ȭ �ϸ鼭 �ٷ� line�� �ǹ� �м��� �����Ѵ�.
	 * 
	 * @param line
	 *            ��������� ����� ���α׷� �ڵ�
	 */
	public Token(String line, InstTable instTable)
	{
		// initialize �߰�
		this.instTable = instTable;
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
		String units[] = line.split("\t");

		if (units[0].equals("."))
		{
			label = units[0];

			if (units.length > 1)
				comment = units[1];
		}
		else
		{
			label = units[0];
			operator = units[1];

			if (!(instTable.getNumberOfOperand(operator) == 0))
			{
				if (units.length > 2)
					operand = units[2].split(",", TokenTable.MAX_OPERAND);

				if (units.length > 3)
					comment = units[3];
			}
			else
			{
				if (units[2] != null)
					comment = units[2];
			}

			if (operator.contains("+"))
			{
				byteSize = 4;
				setFlag(TokenTable.eFlag, 1);
			}
			else
			{
				byteSize = getInstSize(operator);

				if (operand != null && byteSize > 0)
					setFlag(TokenTable.pFlag, 1);
			}

			if (byteSize >= 3)
			{
				if (operand != null)
				{
					if (operand.length > 1 && operand[1].equals("X"))
					{
						setFlag(TokenTable.xFlag, 1);
					}

					if (operand[0].contains("#"))
					{
						setFlag(TokenTable.iFlag, 1);
						setFlag(TokenTable.pFlag, 0);
					}
					else if (operand[0].contains("@"))
					{
						setFlag(TokenTable.nFlag, 1);
					}
					else
					{
						setFlag(TokenTable.nFlag, 1);
						setFlag(TokenTable.iFlag, 1);
					}

				}
				else
				{
					setFlag(TokenTable.nFlag, 1);
					setFlag(TokenTable.iFlag, 1);
				}
			}
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
		// ...
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

	public int getInstSize(String operator)
	{
		int size = 0;

		if (instTable.isInstruction(operator))
		{
			size = instTable.getformat(operator);
		}
		else if (operator.equals("RESB"))
		{
			size = Integer.parseInt(operand[0]);
		}
		else if (operator.equals("RESW"))
		{
			size = Integer.parseInt(operand[0]) * 3;
		}
		else if (operator.equals("BYTE"))
		{
			size = 1;
		}
		else if (operator.equals("WORD"))
		{
			size = 3;
		}
		else 
			size = 0;

		return size;
	}
}
