package sax;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import main.Console;

/**
 * Handles the XML file, reading and displaying it.
 * 
 * @author Patryk Bojar
 * @version 1.0.0, 29 Nov 2021
 */
public class SAXHandler extends DefaultHandler {
	boolean read = true;
	StringBuilder strBld = new StringBuilder();

	/**
	 * Main Constructor with read boolean variable, false by default.
	 */
	public SAXHandler() {
		read = false;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
		if (Console.isShowXMLTags()) {
			strBld.append(qName);
			strBld.append(": ");
			read = true;
		}
		if (atts.getQName(0) != null && atts.getValue(atts.getQName(0)) != null) {
			strBld.append(atts.getQName(0));
			strBld.append("=");
			strBld.append(atts.getValue(atts.getQName(0)));
			read = true;
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		if (read) {
			for (int i = start; i < length + start; i++) {
				strBld.append(ch[i]);
			}
		}
	}
}
