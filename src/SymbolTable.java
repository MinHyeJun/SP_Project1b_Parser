import java.util.ArrayList;

/**
 * symbol�� ���õ� �����Ϳ� ������ �����Ѵ�.
 * section ���� �ϳ��� �ν��Ͻ��� �Ҵ��Ѵ�.
 */
public class SymbolTable {
	ArrayList<String> symbolList;
	ArrayList<Integer> locationList;
	// ��Ÿ literal, external ���� �� ó������� �����Ѵ�.
	
	SymbolTable(){
		symbolList = new ArrayList<>();
		locationList = new ArrayList<>();
	}
	/**
	 * ���ο� Symbol�� table�� �߰��Ѵ�.
	 * @param symbol : ���� �߰��Ǵ� symbol�� label
	 * @param location : �ش� symbol�� ������ �ּҰ�
	 * <br><br>
	 * ���� : ���� �ߺ��� symbol�� putSymbol�� ���ؼ� �Էµȴٸ� �̴� ���α׷� �ڵ忡 ������ ������ ��Ÿ����. 
	 * ��Ī�Ǵ� �ּҰ��� ������ modifySymbol()�� ���ؼ� �̷������ �Ѵ�.
	 */
	public void putSymbol(String symbol, int location) {
		if(!symbolList.contains(symbol))
		{
			symbolList.add(symbol);
			locationList.add(location);
		}
	}
	
	/**
	 * ������ �����ϴ� symbol ���� ���ؼ� ����Ű�� �ּҰ��� �����Ѵ�.
	 * @param symbol : ������ ���ϴ� symbol�� label
	 * @param newLocation : ���� �ٲٰ��� �ϴ� �ּҰ�
	 */
	public void modifySymbol(String symbol, int newLocation) {
		if(symbolList.contains(symbol))
		{
			for(int index = 0; index < symbolList.size(); index++)
				if(symbol.equals(symbolList.get(index)))
				{
					symbolList.set(index, symbol);
					locationList.set(index, newLocation);
					break;
				}
		}
	}
	
	/**
	 * ���ڷ� ���޵� symbol�� � �ּҸ� ��Ī�ϴ��� �˷��ش�. 
	 * @param symbol : �˻��� ���ϴ� symbol�� label
	 * @return symbol�� ������ �ִ� �ּҰ�. �ش� symbol�� ���� ��� -1 ����
	 */
	public int search(String symbol) {
		int address = 0;
		//...
		if(symbolList.contains(symbol))
		{
			for(int index = 0; index < symbolList.size(); index++)
				if(symbol.equals(symbolList.get(index)))
				{
					address = locationList.get(index);
					break;
				}
		}
		else
			address = -1;
		
		return address;
	}
	
	public String getSymbol(int index) {
		return symbolList.get(index);
	}
	
	public int getLocation(int index) {
		return locationList.get(index);
	}
	
	public int getSize() {
		return symbolList.size();
	}
	
}