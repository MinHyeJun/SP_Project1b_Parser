import java.util.ArrayList;

/**
 * symbol�� ���õ� �����Ϳ� ������ �����Ѵ�. section ���� �ϳ��� �ν��Ͻ��� �Ҵ��Ѵ�.
 */
public class SymbolTable
{
	ArrayList<String> symbolList;  // �ɺ��� ��� ���� ����Ʈ
	ArrayList<Integer> locationList;  // �ش� �ɺ��� �ּҰ��� ��� ���� ����Ʈ
	ArrayList<Integer> modifSizeList;  // modification table���� ������ ����Ʈ�� ũ�⸦ �����ϴ� ����Ʈ
	// ��Ÿ literal, external ���� �� ó������� �����Ѵ�.

	/**
	 * Ŭ���� �� �ʵ�� �����ϴ� ����Ʈ�� ��ü �����Ѵ�.
	 */
	public SymbolTable()
	{
		// ArrayList ��ü�� �����Ͽ� �ʱ�ȭ
		symbolList = new ArrayList<>();
		locationList = new ArrayList<>();
		modifSizeList = new ArrayList<>();
	}

	/**
	 * ���ο� Symbol�� table�� �߰��Ѵ�.
	 * 
	 * @param symbol
	 *            : ���� �߰��Ǵ� symbol�� label
	 * @param location
	 *            : �ش� symbol�� ������ �ּҰ� <br>
	 * 			<br>
	 *            ���� : ���� �ߺ��� symbol�� putSymbol�� ���ؼ� �Էµȴٸ� �̴� ���α׷� �ڵ忡 ������ ������ ��Ÿ����.
	 *            ��Ī�Ǵ� �ּҰ��� ������ modifySymbol()�� ���ؼ� �̷������ �Ѵ�.
	 */
	public void putSymbol(String symbol, int location)
	{
		// ���ڷ� ���� �ɺ��� ����
		String inputSymbol = symbol;

		// �ɺ��� "="�� ���Ե� ���(���ͷ��� ���)
		// "=" ǥ�ø� ������
		if (inputSymbol.contains("="))
			inputSymbol = inputSymbol.replaceAll("=", "");

		// ������ ������ �ɺ��� �ƴ� ���
		if (!symbolList.contains(inputSymbol))
		{
			// �ɺ��� ���ڷ� ���� �ּҰ��� ������
			symbolList.add(inputSymbol);
			locationList.add(location);
		}
	}
	
	/**
	 * modification table�� modification record���� ���� �ɺ�, �ּ�, ���� ����Ʈ ũ�� ������ �����Ѵ�.
	 * 
	 * @param modifSymbol: �ɺ� �̸�
	 * @param location: �ɺ��� ���� �ҽ� �ڵ��� �ּ�
	 * @param modifSize: ������ ����Ʈ ũ��
	 */
	public void putModifSymbol(String modifSymbol, int location, int modifSize)
	{
		symbolList.add(modifSymbol);
		locationList.add(location);
		modifSizeList.add(modifSize);
	}

	/**
	 * ������ �����ϴ� symbol ���� ���ؼ� ����Ű�� �ּҰ��� �����Ѵ�.
	 * 
	 * @param symbol
	 *            : ������ ���ϴ� symbol�� label
	 * @param newLocation
	 *            : ���� �ٲٰ��� �ϴ� �ּҰ�
	 */
	public void modifySymbol(String symbol, int newLocation)
	{
		// ���ڷ� ���� �ɺ��� ����
		String inputSymbol = symbol;

		// �ɺ��� "="�� ���Ե� ���(���ͷ��� ���)
		// "=" ǥ�ø� ������
		if (inputSymbol.contains("="))
			inputSymbol = inputSymbol.replaceAll("=", "");

		// List �� �̹� ����Ǿ��ִ� ��쿡�� ������ ����
		if (symbolList.contains(inputSymbol))
		{
			// ����Ǿ��ִ� �ɺ��� ��ġ�� ã�� ���ڷ� ���� ���ο� �ּҰ��� �־���
			for (int index = 0; index < symbolList.size(); index++)
				if (inputSymbol.equals(symbolList.get(index)))
				{
					symbolList.set(index, inputSymbol);
					locationList.set(index, newLocation);
					break;
				}
		}
	}

	/**
	 * ���ڷ� ���޵� symbol�� � �ּҸ� ��Ī�ϴ��� �˷��ش�.
	 * 
	 * @param symbol
	 *            : �˻��� ���ϴ� symbol�� label
	 * @return symbol�� ������ �ִ� �ּҰ�. �ش� symbol�� ���� ��� -1 ����
	 */
	public int search(String symbol)
	{
		// ����� �ּҰ� ����
		int address = 0;
		
		// ���ڷ� ���� �ɺ��� List �� �ִ� ���
		// �ش� �ɺ��� �ּҰ��� ã�� address�� ����
		if (symbolList.contains(symbol))
		{
			for (int index = 0; index < symbolList.size(); index++)
				if (symbol.equals(symbolList.get(index)))
				{
					address = locationList.get(index);
					break;
				}
		}
		// ���� ��� -1�� address�� ����
		else
			address = -1;

		// address ����
		return address;
	}

	/**
	 * ���ڷ� ���� index�� �ɺ��� �����Ѵ�.
	 * 
	 * @param index: ������ �ɺ��� ��ġ
	 * @return: �ش� ��ġ�� �ɺ�
	 */
	public String getSymbol(int index)
	{
		return symbolList.get(index);
	}

	/**
	 * �ش� index�� �ּҰ��� �����Ѵ�.
	 * 
	 * @param index: ������ �ּҰ��� ��ġ
	 * @return: �ش� ��ġ�� �ּҰ�
	 */
	public int getLocation(int index)
	{
		return locationList.get(index);
	}

	/**
	 * symbol table�� ũ�⸦ �����Ѵ�.
	 * 
	 * @return: symbol table�� ũ��
	 */
	public int getSize()
	{
		return symbolList.size();
	}
	
	/**
	 * literal table���� ����ϴ� �޼ҵ�
	 * 
	 * ���ڷ� ���� �ش� index�� ���ͷ� ������ ũ�⸦ ���Ѵ�
	 * 
	 * @param index: ũ�⸦ ���� ���ͷ��� ���̺� ���� ��ġ
	 * @return: ���ͷ� ������ ũ��
	 */
	public int getLiteralSize(int index)
	{
		int size = 0;
		
		if (symbolList.get(index).contains("X"))
		{
			size = 1;
		}
		else if (symbolList.get(index).contains("C"))
		{
			String literal = symbolList.get(index).replaceAll("C|\'", "");
			size = literal.length();
		}
		
		return size;
	}
	
	/**
	 * modification table �� �ش� �ε����� ������ ����Ʈ ����� �����Ѵ�.
	 * @param index: ������ ����Ʈ ����� ���� �ɺ��� ���̺� ���� ��ġ
	 * @return: ������ ����Ʈ ũ��
	 */
	public int getModifSize(int index)
	{
		return modifSizeList.get(index);
	}
}
