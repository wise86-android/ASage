/*
 *
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
 *   Copyright 2012 wise 
 *
 *
 */

/**
SearchUpdateFeed.java
*/
package com.wise.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.wise.gui.OnlineFragment;
import com.wise.xml.ExtractRssUpdateDate;

import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;

/**
 * @author wise
 *
 */
public class SearchUpdateFeed extends AsyncTask<Void,Void, Integer> {

	private final static String TAG="SearchUpdateFeed";

	private RssFeedsDB db;
	private OnlineFragment fragment;
	
	/**
	 * @param lf fragment that show the list to update
	 */
	public SearchUpdateFeed(OnlineFragment f,RssFeedsDB db) {
		this.db=db;
		fragment=f;	
	}

	/**
	 * for each bookmarks load the xml and save the update data,
	 * after that check if the data is after the last vist
	 * TODO: save the loaded xml for use it when the user clik in the item view
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected Integer doInBackground(Void... arg) {
		int nUpdate = 0;
		
		Cursor c = db.getAllFeed();
		int idColumn = c.getColumnIndex(RssFeedsDB.FEED_ID);
		int rssUrlColumn = c.getColumnIndex(RssFeedsDB.FEED_URL);
		int rssLastVisitColumn = c.getColumnIndex(RssFeedsDB.FEED_LASTACCESS_DATE);
		int nFeed = c.getCount();
		Vector<Pair<Long,Date>> updateRow = new Vector<Pair<Long,Date>>(nFeed);
		SAXParserFactory spf = SAXParserFactory.newInstance();
		SAXParser sp;
		XMLReader xr = null;
		try {
			sp = spf.newSAXParser();
			xr = sp.getXMLReader();

			/** Create handler to handle XML Tags ( extends DefaultHandler ) */
			ExtractRssUpdateDate xmlHandler = new ExtractRssUpdateDate();
			xr.setContentHandler(xmlHandler);

			for (int i = 0; i < nFeed; i++) {
				c.moveToPosition(i);
				URL url;
				try {
					url = new URL(c.getString(rssUrlColumn));
					HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();	
					urlConnection.addRequestProperty("Cache-Control", "no-cache"); // fresh data
					xr.parse(new InputSource(urlConnection.getInputStream()));
				} catch (MalformedURLException e) {
					fragment.showError(OnlineFragment.ERROR_URL,c.getString(rssUrlColumn));
				} catch (IOException e) {
					fragment.showError(OnlineFragment.ERROR_NETWORK);
				} catch (SAXException e) { // early stop of the parser

				}

				Date lastUpdateDate =xmlHandler.getLastUpdate();
				
				if ( c.getLong(rssLastVisitColumn)<lastUpdateDate.getTime()) {
					updateRow.add(new Pair<Long,Date>(c.getLong(idColumn),lastUpdateDate));
				}

				publishProgress((int) ((i * 100) / (float) nFeed));
				
			}

			db.updateRssUpdateDate(updateRow);
			
		} catch (ParserConfigurationException e1) {
			Log.e(TAG, e1.getMessage());		
		} catch (SAXException e1) {
			Log.e(TAG, e1.getMessage());
		}
		
		return nUpdate;
	}

	/**
	 * @param i
	 */
	private void publishProgress(int i) {
		// TODO Auto-generated method stub
		
	}


}
