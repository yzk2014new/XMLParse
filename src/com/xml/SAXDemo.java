package com.xml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;

/**
 * SAX ½âÎöXMLÎÄµµ
 */
public class SAXDemo implements XmlDocument {
	
	public static void main(String[] args) {
    	SAXDemo sax = new SAXDemo();
		//sax.parserXml("Test.xml");
    	sax.parserXml("AndroidManifest.xml");
    }

	public void parserXml(String fileName) {
		SAXParserFactory saxfac = SAXParserFactory.newInstance();

		try {
			SAXParser saxparser = saxfac.newSAXParser();
			InputStream is = new FileInputStream(fileName);
			MySAXHandler saxHandler = new MySAXHandler();
			saxparser.parse(is, saxHandler);
			saxHandler.printXML();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

class MySAXHandler extends DefaultHandler {
	public static final String[] sTargetElement = { "activity", "service",
			"receiver", "provider" };

	private String pkgName = "default";
	private HashMap<String, HashMap<String, ArrayList<String>>> procTable = new HashMap<String, HashMap<String, ArrayList<String>>>();

	public void startDocument() throws SAXException {
		System.out.println("start parse xml.");
	}

	public void endDocument() throws SAXException {
		System.out.println("end parse xml.");
	}

	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if (qName.equals("manifest")) {
			int packIndex = attributes.getIndex("package");
			if (packIndex >= 0) {
				pkgName = attributes.getValue(packIndex);
			}
		}
		if (isTargetElement(qName)) {
			int contIndex = attributes.getIndex("android:name");
			String contentName = attributes.getValue(contIndex);

			int procIndex = attributes.getIndex("android:process");
			String procKey = procIndex >= 0 ? pkgName + attributes.getValue(procIndex) : pkgName;
			if (procTable.containsKey(procKey)) {
				// hashmap has contain this process
				HashMap<String, ArrayList<String>> contentTable = procTable.get(procKey);
				if (contentTable.containsKey(qName)) {
					// this process has contain this content type: such as
					// activity, service...
					contentTable.get(qName).add(contentName);
				} else {
					// this process has not contain this content type, should
					// create new one
					ArrayList<String> itemList = new ArrayList<String>();
					itemList.add(contentName);
					contentTable.put(qName, itemList);
				}
			} else {
				// hashmap has not contain this process, should create new
				// process item
				ArrayList<String> itemList = new ArrayList<String>();
				itemList.add(contentName);
				HashMap<String, ArrayList<String>> contentTable = new HashMap<String, ArrayList<String>>();
				contentTable.put(qName, itemList);
				procTable.put(procKey, contentTable);
			}
		}
	}

	public boolean isTargetElement(String name) {
		for (String elem : sTargetElement) {
			if (name.equals(elem)) {
				return true;
			}
		}
		return false;
	}

	public void endElement(String uri, String localName, String qName)
			throws SAXException {
	}

	public void characters(char[] ch, int start, int length)
			throws SAXException {
		// System.out.print(new String(ch, start, length));
	}
	
	public void printXML() {
		System.out.println("There are " + procTable.size() + " process in all.");
		Iterator iter = procTable.entrySet().iterator();
		while(iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			String procName = (String)entry.getKey();
			System.out.println(procName + ":");
			HashMap<String, ArrayList<String>> contentTable = (HashMap<String, ArrayList<String>>) entry.getValue();
			Iterator newIter = contentTable.entrySet().iterator();
			while(newIter.hasNext()) {
				Map.Entry newEntry = (Map.Entry) newIter.next();
				String contentName = (String)newEntry.getKey();
				System.out.println("    " + contentName + ":");
				ArrayList<String> itemList = (ArrayList<String>)newEntry.getValue();
				for (String item : itemList) {
					System.out.println("        " + item);
				}
			}
		}
	}
}