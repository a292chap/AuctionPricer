import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

// Object that gets the lots for a given auction
public class AuctionToLots {
	private String rawText;
	
	AuctionToLots(String urlLink) {
		rawText = Webpage.getWebpageString(urlLink);
		parseAuctionIntoLots();
	}
	
	
	private ArrayList<Lot> parseAuctionIntoLots() {
		// An array of lots from given auction to be returned
		ArrayList<Lot> returnArr = new ArrayList<Lot>();
		String[][] tokens = {{"<a name=\"lot-title\" href=\"","\" class=\""},
				{"<span class=\"lot-title\">","</span>"},
				{"<span class=\"lot-number\">", "</span>"},};
		int tokensSize = 3;
		
		// Example Lot Link Element: <a name="lot-title" href="/en-us/auction-catalogues/bscfo/catalogue-id-bscfo10036/lot-a51c2454-c38d-4f8c-a4e4-ad8101818cce" class="update-history-url-after-back click-track lot-link" data-click-type="title" data-lot-id="a51c2454-c38d-4f8c-a4e4-ad8101818cce"> ... </a>
		String tokenLinkStart = "<a name=\"lot-title\" href=\"";
		String tokenLinkEnd   = "\" class=\"";
		
		// Example Lot Title Element: <span class="lot-title">DESCRIPTION: (2) BOXES OF 5/16 - 3/8" LEVER LOAD BINDER GRAB HOOK AND CLEVIS. (5) PER BOX, 10 IN LOT</span>
		String tokenTitleStart = "<span class=\"lot-title\">";
		String tokenTitleEnd   = "</span>";
		
		// Example lot Num Element: <span class="lot-number">246B</span>
		String tokenLNumStart = "<span class=\"lot-number\">";
		String tokenLNumEnd   = "</span>";
		
		// Example Price Element: <span id="openingPrice-365a71db-93b0-41c1-877f-ad7b0135136a"><span><strong>20</strong></span></span>
		//					    : <span id="price-a7424778-06e8-43fa-b2a5-ad7b013510fb"><span><strong>20</strong></span></span>
		String tokenPriceStart1 = "<strong>";
		// These two are used to differentiate between opening and price elements from any other elements with the field "<strong>"
		// Two are needed because there are 2 types and a unique element id sits between it and the "<strong>" tag, complicating matters
		String tokenPriceStart2 = "<span id=\"openingPrice";
		String tokenPriceStart3 = "<span id=\"price";
		String tokenPriceEnd    = "</strong>";
		
		String lotLink;
		String lotTitle;
		String lotNum;
		int lotPrice;
		URL lotURL = null;
		
		String lotPrice2;
		String lotPrice3;
		int index = -2; // Cannot be -1 for default val, as -1 is end of file
		int index2;
		int index3;
		while(index != -1) {
			// Advance the index
			index = rawText.indexOf(tokenLinkStart, index);
			if (index == -1) {
				break;
			}
			index2 = rawText.indexOf(tokenPriceStart2, index);
			index3 = rawText.indexOf(tokenPriceStart3, index);
			lotLink   = Webpage.extractString(rawText, tokenLinkStart,   tokenLinkEnd,  index);
			lotTitle  = Webpage.extractString(rawText, tokenTitleStart,  tokenTitleEnd, index);
			lotNum    = Webpage.extractString(rawText, tokenLNumStart,   tokenLNumEnd,  index);
			lotPrice2 = Webpage.extractString(rawText, tokenPriceStart1, tokenPriceEnd, index2);
			lotPrice3 = Webpage.extractString(rawText, tokenPriceStart1, tokenPriceEnd, index3);
			
			lotLink   = "https://www.bidspotter.com" + lotLink;
			try {
				lotURL = new URL(lotLink);
			} catch (MalformedURLException e1) {
				System.out.println("Bad URL for parsed lot link in AuctionToLots");
				e1.printStackTrace();
			}
			
			//---------------
			// Find Lot Price
			// Search for price following the title
			// Search for both "opening price" and "price"

			if (index2 == -1 && index3 == -1) {
				System.out.println("ERROR, END OF FILE?");
				return null;
			}
			if (index2 == -1) {
				index2 = Integer.MAX_VALUE;
			}
			if (index3 == -1) {
				index3 = Integer.MAX_VALUE;
			}
			
			// Of the two indexes found, take the index closest to where we are now,
			// as that will be the one associated with the lot
			// This way we don't need to check if the current lot has a "current price" or "opening price"
			if (index2 < index3) {
				index = index2;
				lotPrice = Integer.parseInt(lotPrice2);
			} else {
				index = index3;
				lotPrice = Integer.parseInt(lotPrice3);
			}
			
			
			Lot l = new Lot(lotTitle, lotNum, lotPrice, lotURL);
			returnArr.add(l);
			
			// Display values
			System.out.println(lotTitle);
			System.out.println("   Lot:   " + lotNum);
			System.out.println("   Price: " + lotPrice);
			System.out.println("   Link:  " + lotLink);
			
			System.out.println("");
			
		}
		
		return returnArr;
		
	}

}
