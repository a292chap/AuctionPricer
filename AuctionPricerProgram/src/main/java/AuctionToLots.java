import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

// Object that gets the lots for a given auction
public class AuctionToLots {
	private String urlLink;
	private String rawText;
	private URL webPage;
	private InputStream webpageIS;
	private Boolean isHttps;
	
	private int index;
	
	AuctionToLots(String urlLink) {
		/*
		// Check that URL has been given
		if (urlLink == null) {
			// TODO Way to display error
			System.out.println("Error: No URL Given");
			return;
		}
		
		// Make sure URL has appropriate 'http://' prepending it
		urlLink = prepend(urlLink);
		
		// Set up object variables
		this.urlLink   = urlLink;
		this.isHttps   = getHttps();
		this.webPage   = getWebPage();
		this.webpageIS = getWebInputStream();
		this.rawText   = getRawHTML();
		
		if (urlLink == null) {
			System.out.println("urlLink null");
		}
		if (rawText == null) {
			System.out.println("rawText null");
		}
		if (webPage == null) {
			System.out.println("webPage null");
		}
		if (webpageIS == null) {
			System.out.println("webpageIS null");
		}*/
		rawText = Webpage.getWebpageString(urlLink);
		parseAuctionIntoLots();
	}
	
	
	// Determines if connection is http or https
	private Boolean getHttps() {
		return this.urlLink.substring(0, 8).compareTo("https://") == 0;
	}
	
	
	// Create and return a URL object from the given urlLink
	private URL getWebPage() {
		try {
			return new URL(urlLink);
		} catch (MalformedURLException e) {
			// TODO Way to display error
			System.out.println("MalformedURLException");
		}
		return null;
		
	}
	
	// Create and return an input stream for the webPage
	private InputStream getWebInputStream() {
		try {
			if (isHttps) {
				HttpsURLConnection h = (HttpsURLConnection) this.webPage.openConnection();
				h.setHostnameVerifier(h.getHostnameVerifier());
				return h.getInputStream();
			} else {
				return webPage.openStream();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	// Gets the raw text to be parsed for the webPage
	private String getRawHTML() {
		byte[] byteArr = {};		
		try {
			byteArr = this.webpageIS.readAllBytes();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new String(byteArr);
		
	}
	
	
	// Take the url string and, if it (or "https://") is not already prepended
	//		to the string, prepend the string with "http://" and return it back
	private String prepend(String urlLink) {
		String  http  = "http://";
		String  https = "https://";
		String  www   = "www.";
		Boolean s = false; 			// States weather urlLink contains http or https
		
		// Check to see if 'http://' prepend already exists in the string
		if (urlLink.length() == 7) {
			
			// If it does not exist at the beginning, prepend http and return said string
			if (urlLink.substring(0, 7).compareTo(http) != 0) {
				urlLink = http + urlLink;
			}
			// Else, if it contains "http://" to begin with, do nothing 
		}
		
		// Check to see if 'http://' OR 'https://' prepend already exists in the string
		else if (urlLink.length() >= 8) {
			// If it does not exist at the beginning, prepend https and return said string
			if (urlLink.substring(0, 8).compareTo(https) != 0
					&& urlLink.substring(0, 7).compareTo(http) != 0) {
				urlLink = http + urlLink;
			} else {
				if (urlLink.substring(0, 8).compareTo(https) == 0) {
					s = true; // Only case where urlLink already contains "https://"
				}
			}
		}
		
		// As a catch-all, for a string shorter than "http://" or "https://", prepend "http://" onto it 
		else {
			urlLink = http + urlLink;
		}
		
		// Prepend "www."
		int prefixLength;
		prefixLength = 7;
		if (s) { prefixLength = 8;}
		
		// Check if length of urlLink is long enough to contain "http://www."
		if (urlLink.length() >= prefixLength + 4) {
			
			// Check to see if 'www.' is contained in the appropriate position in the string
			if (urlLink.substring(prefixLength, prefixLength + 4).compareTo(www) != 0 ) {
				String urlLink_BeginHalf = urlLink.substring(0, prefixLength);
				String urlLink_EndHalf   = urlLink.substring(prefixLength, urlLink.length());
				urlLink = urlLink_BeginHalf + www + urlLink_EndHalf;
			}
			// If it does not, add "www." by defualt
		} else {
			String urlLink_BeginHalf = urlLink.substring(0, prefixLength);
			String urlLink_EndHalf   = urlLink.substring(prefixLength, urlLink.length());
			urlLink = urlLink_BeginHalf + www + urlLink_EndHalf;
		}
		
		return urlLink;
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
		int index2;
		int index3;
		while(this.index != -1) {
			// Advance the index
			index = rawText.indexOf(tokenLinkStart, index);
			if (index == -1) {
				break;
			}
			index2 = rawText.indexOf(tokenPriceStart2, this.index);
			index3 = rawText.indexOf(tokenPriceStart3, this.index);
			lotLink   = extractString(tokenLinkStart,   tokenLinkEnd,  this.index);
			lotTitle  = extractString(tokenTitleStart,  tokenTitleEnd, this.index);
			lotNum    = extractString(tokenLNumStart,   tokenLNumEnd,  this.index);
			lotPrice2 = extractString(tokenPriceStart1, tokenPriceEnd, index2);
			lotPrice3 = extractString(tokenPriceStart1, tokenPriceEnd, index3);
			
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
				this.index = index2;
				lotPrice = Integer.parseInt(lotPrice2);
			} else {
				this.index = index3;
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
	
	private String extractString(String tokenStart, String tokenEnd, int index) {
		//--------------
		// Find lot link
		int indexStart = rawText.indexOf(tokenStart, index);
		if (indexStart == -1) {
			System.out.println("Could not find start");
			return null;
		}
		// Move the start iterator so the final string does not include the token
		indexStart += tokenStart.length(); // This will also advance the iterator so it will not recognize and skip curr lot on next iteration
		int indexEnd = rawText.indexOf(tokenEnd, indexStart);
		if (indexEnd == -1) {
			System.out.println("ERROR: No tokenLinkEnd found, returning");
			return null;
		}
		// Get lot link
		return rawText.substring(indexStart, indexEnd);
		
	}

}
