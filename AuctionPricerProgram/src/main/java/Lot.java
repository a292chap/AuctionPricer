import java.net.URL;

public class Lot {
	
	URL link;
	
	String title;
	String number;
	
	String[] tags;
	
	int dataPts;
	int[][] priceHistory;
	int[][] watcherHistory;
	
	int startTime;
	int origEndTime;
	int realEndTime;
	
	Lot(String title, String  num, int price, URL link) {
		
		// Make sure link exists
		if (link == null) {
			System.out.println("NULL URL given to Lot constructor");
			return;
		}
		
		this.link    = link;
		this.title   = title;
		this.number  = num;
		priceHistory = new int[2][];
		
		initLot();
	}
	
	private void initLot() {
		
		 Webpage.getWebpageString(link.toString());
		
	}
}
