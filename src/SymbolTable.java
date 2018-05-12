import java.util.ArrayList;

/**
 * symbol�� ���õ� �����Ϳ� ������ �����Ѵ�. section ���� �ϳ��� �ν��Ͻ��� �Ҵ��Ѵ�.
 */
public class SymbolTable
{
	ArrayList<String> symbolList;
	ArrayList<Integer> locationList;
	ArrayList<Integer> modifSizeList;
	// ��Ÿ literal, external ���� �� ó������� �����Ѵ�.

	public SymbolTable()
	{
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
		String inputSymbol = symbol;

		if (symbol.contains("="))
			inputSymbol = inputSymbol.replaceAll("=", "");

		if (!symbolList.contains(inputSymbol))
		{
			symbolList.add(inputSymbol);
			locationList.add(location);
		}
	}
	
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
		String inputSymbol = symbol;

		if (symbol.contains("="))
			inputSymbol = inputSymbol.replaceAll("=", "");

		if (symbolList.contains(inputSymbol))
		{
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
		int address = 0;
		// ...
		if (symbolList.contains(symbol))
		{
			for (int index = 0; index < symbolList.size(); index++)
				if (symbol.equals(symbolList.get(index)))
				{
					address = locationList.get(index);
					break;
				}
		}
		else
			address = -1;

		return address;
	}

	public String getSymbol(int index)
	{
		return symbolList.get(index);
	}

	public int getLocation(int index)
	{
		return locationList.get(index);
	}

	public int getSize()
	{
		return symbolList.size();
	}
	
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
	
	public int getModifSize(int index)
	{
		return modifSizeList.get(index);
	}
}
