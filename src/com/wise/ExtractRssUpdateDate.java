package com.wise;

import java.util.Date;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

public class ExtractRssUpdateDate extends DefaultHandler {
	
	private Date lastUpdate;
	
	private boolean lastBuildDateIsOpen;
	
	public Date getLastUpdate(){
		return lastUpdate;
	}
	
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if (localName.equalsIgnoreCase("lastBuildDate"))
			lastBuildDateIsOpen = true;
		
	}//if
	
	
	
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		if(lastBuildDateIsOpen){
			Log.d("extractRss","Date: "+new String(ch,start,length));
			lastUpdate = new Date(new String(ch,start,length));
			lastBuildDateIsOpen=false;
			throw new SAXException("Date acquired, stop the parser");
		}//if
	}// characters
	
	
	
}
