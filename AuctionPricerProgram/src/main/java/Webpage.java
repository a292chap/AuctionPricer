import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class Webpage {
		
	public static String getWebpageString(String inputUrlLink) {
		String urlLink = inputUrlLink;
		String rawText;
		URL webPage;
		InputStream webpageIS;
		Boolean isHttps;
		if (urlLink == null) {
			// TODO Way to display error
			System.out.println("Error: No URL Given");
			return null;
		}
		
		// Make sure URL has appropriate 'http://' prepending it
		urlLink = prepend(urlLink);
		
		// Set up object variables
		urlLink   = inputUrlLink;
		isHttps   = getHttps(urlLink);
		webPage   = getWebPage(urlLink);
		webpageIS = getWebInputStream(isHttps, webPage);
		rawText   = getRawHTML(webpageIS);
		
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
		}
		return rawText;
	}
	
	// Determines if connection is http or https
	private static Boolean getHttps(String urlLink) {
		return urlLink.substring(0, 8).compareTo("https://") == 0;
	}
	
	
	// Create and return a URL object from the given urlLink
	private static URL getWebPage(String urlLink) {
		try {
			return new URL(urlLink);
		} catch (MalformedURLException e) {
			// TODO Way to display error
			System.out.println("MalformedURLException");
		}
		return null;
		
	}
	
	// Create and return an input stream for the webPage
	private static InputStream getWebInputStream(boolean isHttps, URL webPage) {
		try {
			if (isHttps) {
				HttpsURLConnection h = (HttpsURLConnection) webPage.openConnection();
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
	private static String getRawHTML(InputStream webpageIS) {
		byte[] byteArr = {};		
		try {
			byteArr = webpageIS.readAllBytes();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new String(byteArr);
		
	}
	
	// Take the url string and, if it (or "https://") is not already prepended
	//		to the string, prepend the string with "http://" and return it back
	private static String prepend(String urlLink) {
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
	
	public static String extractString(String rawText, String tokenStart, String tokenEnd, int index) {
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
