import java.net.URL;
import java.util.ArrayList;

public class Lot {
	String auctType; // "Timed" or "Live"
	
	URL link;
	String rawText;
	
	String title;
	String number;
	
	String[] tags;
	
	int dataPts;
	ArrayList<ArrayList<Integer>> priceHistory;
	ArrayList<ArrayList<Integer>> watcherHistory;
	ArrayList<Integer> innerListElement;
	
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
		
		initLot();
	}
	
	private void initLot() {
		
		rawText = Webpage.getWebpageString(link.toString());
		// Parse the initial price and start time
		//  Live opening 
		//		https://www.bidspotter.com/en-us/auction-catalogues/m-davis-group/catalogue-id-bscm-10306/lot-3945cc87-16f5-43dc-82dd-ad5c0132e88b
		//		<strong id="currentBidLabel">Opening price</strong>
		//		<span id="price" class="amount">5</span>
		//	Live opening QTY
		//		https://www.bidspotter.com/en-us/auction-catalogues/aaron-pos/catalogue-id-bscaa10214/lot-aff40e15-72ce-4f94-b5c5-ad800101c481
		//		Same bid label as above
		//		Same price label
		//		(For qty of 2) <div id="quantity-value" class="Rtable-cell Rtable-cell--2of3">2</div>
		//  Timed Current current Qty
		//		https://www.bidspotter.com/en-us/auction-catalogues/aaron-pos/catalogue-id-bscaa10214/lot-22623b5b-db18-483a-8b75-ad800101c481
		// 		<strong id="currentBidLabel">Current bid</strong>
		//		(Same as above) <span id="price" class="amount">5</span>
		//  ?Live current - Auction actually live
		//  Timed opening
		//		https://www.bidspotter.com/en-us/auction-catalogues/m-davis-group/catalogue-id-bscm-10296/lot-deaeebfd-578f-4860-b63e-ad10014ebb6d
		// 		<div class="Rtable-cell Rtable-cell--1of3 Rtable-cell--rowEnd hammer">
        //			<strong>Current bid</strong>
		//		</div>
		 
		
	}
}
