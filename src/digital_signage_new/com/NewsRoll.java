package digital_signage_new.com;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class NewsRoll {
	String[] heading;
	int j = 1;

	public String[] yo(String s) throws IOException {
		InputStream in;
		String strTitle = "";
		NodeList textNodes, itemNodes, titleNodes;
		Element titleElement, itemElement;
		Document doc = null;
		DocumentBuilder db;
		DocumentBuilderFactory dbf;

		try {
			in = OpenHttpConnection(s);
			// in =
			// OpenHttpConnection("http://feeds.feedburner.com/NdtvNews-TopStories?format=xml");//For
			// Top Stories
			// in =
			// OpenHttpConnection("http://feeds.feedburner.com/ndtv/Lsgd?format=xml");
			// //For India News
			// in =
			// OpenHttpConnection("http://feeds.feedburner.com/ndtv/TqgX?format=xml");
			// //For World News
			// in =
			// OpenHttpConnection("http://feeds.feedburner.com/NdtvNews-Cities?format=xml");
			// //For Cities News
			// in =
			// OpenHttpConnection("http://feeds.feedburner.com/NDTV-Tech?format=xml");
			// //For Sci & Tech news
			dbf = DocumentBuilderFactory.newInstance();
			db = dbf.newDocumentBuilder();
			doc = db.parse(in);
			doc.getDocumentElement().normalize();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		itemNodes = doc.getElementsByTagName("item");
		heading = new String[itemNodes.getLength()];
		for (int i = 0; i < itemNodes.getLength(); i++) {
			Node itemNode = itemNodes.item(i);
			if (itemNode.getNodeType() == Node.ELEMENT_NODE) {
				itemElement = (Element) itemNode;
				titleNodes = (itemElement).getElementsByTagName("title");
				titleElement = (Element) titleNodes.item(0);
				textNodes = ((Node) titleElement).getChildNodes();
				heading[i] = ((Node) textNodes.item(0)).getNodeValue();
				/*
				 * if(j==0){ strTitle+="; "+((Node)
				 * textNodes.item(0)).getNodeValue(); } else{ strTitle+=((Node)
				 * textNodes.item(0)).getNodeValue(); j=0;}
				 */
			}
		}
		return heading;

	}

	private InputStream OpenHttpConnection(String urlString) throws IOException {
		InputStream in = null;
		int response = -1;
		URL url = new URL(urlString);
		URLConnection conn = url.openConnection();
		if (!(conn instanceof HttpURLConnection))
			throw new IOException("Not an HTTP connection");
		try {
			HttpURLConnection httpConn = (HttpURLConnection) conn;
			httpConn.setAllowUserInteraction(false);
			httpConn.setInstanceFollowRedirects(true);
			httpConn.setRequestMethod("GET");
			httpConn.connect();
			response = httpConn.getResponseCode();
			if (response == HttpURLConnection.HTTP_OK) {
				in = httpConn.getInputStream();
			}
		} catch (Exception ex) {
			throw new IOException("Error connecting");
		}
		return in;
	}

}
