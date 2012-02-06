/*
 * 	This file is part of ASage.
 *
 *    ASage is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    ASage is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with ASage.  If not, see <http://www.gnu.org/licenses/>.
 *
 *   Copyright 2012 Giovanni Visentini 
 */

package com.wise.xml;

import java.util.Date;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

/**
 * this class parse the rss xml for extract the data of the last update,
 * when the tag is close the class throw and exception for finish the parse
 * 
 * @author Giovanni Visentini
 */
public class ExtractRssUpdateDate extends DefaultHandler {
	
	private Date lastUpdate;
	
	private boolean lastBuildDateIsOpen;
	
	/**
	 * get the data of the last update
	 * @return return the data inside the lastBuildDate tag
	 */
	public Date getLastUpdate(){
		return lastUpdate;
	}
	
	/**
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if (localName.equalsIgnoreCase("lastBuildDate"))
			lastBuildDateIsOpen = true;
		
	}//if
	
	
	
	/**
	 * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
	 */
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
