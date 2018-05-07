import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * ����ڰ� �ۼ��� ���α׷� �ڵ带 �ܾ�� ���� �� ��, �ǹ̸� �м��ϰ�, ���� �ڵ�� ��ȯ�ϴ� ������ �Ѱ��ϴ� Ŭ�����̴�. <br>
 * pass2���� object code�� ��ȯ�ϴ� ������ ȥ�� �ذ��� �� ���� symbolTable�� instTable�� ������ �ʿ��ϹǷ�
 * �̸� ��ũ��Ų��.<br>
 * section ���� �ν��Ͻ��� �ϳ��� �Ҵ�ȴ�.
 *
 */
public class TokenTable {
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
	InstTable instTab;

	/** �� line�� �ǹ̺��� �����ϰ� �м��ϴ� ����. */
	ArrayList<Token> tokenList;

	/**
	 * �ʱ�ȭ�ϸ鼭 symTable�� instTable�� ��ũ��Ų��.
	 * 
	 * @param symTab
	 *            : �ش� section�� ����Ǿ��ִ� symbol table
	 * @param instTab
	 *            : instruction ���� ���ǵ� instTable
	 */
	public TokenTable(SymbolTable symTab, InstTable instTab) {
		// ...
		tokenList = new ArrayList<>();
		this.symTab = symTab;
		this.instTab = instTab;
	}

	/**
	 * �Ϲ� ���ڿ��� �޾Ƽ� Token������ �и����� tokenList�� �߰��Ѵ�.
	 * 
	 * @param line
	 *            : �и����� ���� �Ϲ� ���ڿ�
	 */
	public void putToken(String line) {
		tokenList.add(new Token(line, instTab));
	}

	/**
	 * tokenList���� index�� �ش��ϴ� Token�� �����Ѵ�.
	 * 
	 * @param index
	 * @return : index��ȣ�� �ش��ϴ� �ڵ带 �м��� Token Ŭ����
	 */
	public Token getToken(int index) {
		return tokenList.get(index);
	}

	/**
	 * Pass2 �������� ����Ѵ�. instruction table, symbol table ���� �����Ͽ� objectcode�� �����ϰ�, �̸�
	 * �����Ѵ�.
	 * 
	 * @param index
	 */
	public void makeObjectCode(int index) {
		// ...
	}

	/**
	 * index��ȣ�� �ش��ϴ� object code�� �����Ѵ�.
	 * 
	 * @param index
	 * @return : object code
	 */
	public String getObjectCode(int index) {
		return tokenList.get(index).objectCode;
	}

}

/**
 * �� ���κ��� ����� �ڵ带 �ܾ� ������ ������ �� �ǹ̸� �ؼ��ϴ� ���� ���Ǵ� ������ ������ �����Ѵ�. �ǹ� �ؼ��� ������ pass2����
 * object code�� �����Ǿ��� ���� ����Ʈ �ڵ� ���� �����Ѵ�.
 */
class Token {
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
	public Token(String line, InstTable instTable) {
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
	public void parsing(String line) {
		String units[] = line.split("\t");
		
		if (units[0].equals(".")) {
			label = units[0];
			
			if(units.length > 1)
				comment = units[1];
		} else {
			label = units[0];
			operator = units[1];

			if (!(instTable.getNumberOfOperand(operator) == 0)) {
				if (units.length > 2)
					operand = units[2].split(",", TokenTable.MAX_OPERAND);

				if (units.length > 3)
					comment = units[3];
			} else {
				if (units[2] != null)
					comment = units[2];
			}

			if (operator.contains("+")) {
				byteSize = 4;
				setFlag(TokenTable.eFlag, 1);
			} else {
				byteSize = getInstSize(operator);
				
				if(byteSize > 0)
					setFlag(TokenTable.pFlag, 1);
				
			}

			if (instTable.getformat(operator) == 3) {
				if (operand != null) {
					if (operand.length > 1 && operand[1].equals("X")) {
						setFlag(TokenTable.xFlag, 1);
					}

					if (operand[0].contains("#")) {
						setFlag(TokenTable.iFlag, 1);
						setFlag(TokenTable.pFlag, 0);
					} else if (operand[0].contains("@")) {
						setFlag(TokenTable.eFlag, 1);
					} else {
						setFlag(TokenTable.nFlag, 1);
						setFlag(TokenTable.iFlag, 1);
					}

				} else {
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
	public void setFlag(int flag, int value) {
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
	public int getFlag(int flags) {
		return nixbpe & flags;
	}

	public int getInstSize(String operator) {
		int size = 0;

		if (instTable.isInstruction(operator)) {
			size = instTable.getformat(operator);
		} else if (operator.equals("RESB")) {
			size = Integer.parseInt(operand[0]);
		} else if (operator.equals("RESW")) {
			size = Integer.parseInt(operand[0]) * 3;
		} else if (operator.equals("BYTE")) {
			size = 1;
		} else if (operator.equals("WORD")) {
			size = 3;
		}

		return size;
	}
}
