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

package com.wise.gui;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Intent;

import android.content.Loader;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.wise.R;
import com.wise.util.FullBookmarks;
import com.wise.xml.ExtractRssUpdateDate;

/**
 * Fragment that show the list bookmarks
 * 
 * @author Giovanni Visentini
 */
public class FeedListFragment extends OnlineFragment implements
		LoaderManager.LoaderCallbacks<Cursor>,OnItemClickListener{

	private final static String TAG = "FeedList";
	private final static int CURSOR_BOOKMARK = 0;

	private Cursor rssBookmark;
	private int favIconColumn;
	private int titleColumn;
	private int urlColumn;
	private int lastVisitColumn;
	
	private ListView feedsList;
	private SimpleCursorAdapter feeds;
	
	private int iconSize;

	/** @see android.app.Fragment#onCreate(android.os.Bundle) */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.setHasOptionsMenu(true);

		iconSize = getResources().getDimensionPixelSize(R.dimen.iconSize_feedItem);
		int[] showItem = new int[] { R.id.feedItem_name, R.id.feedItem_name };
		String[] showValue = new String[] { FullBookmarks.FAVICON,
				FullBookmarks.TITLE };

		feeds = new SimpleCursorAdapter(this.getActivity(), R.layout.feed_item,
				null, showValue, showItem, 0);
		feeds.setViewBinder(new BuildItemView());

		
		// start loading the cursor
		this.getLoaderManager().initLoader(CURSOR_BOOKMARK, null, this);

	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.feed_list, container, false);
        
        feedsList = (ListView) v.findViewById(R.id.feed_list);
        feedsList.setAdapter(feeds);
        feedsList.setOnItemClickListener(this);        
        
        return v;
    }

	/**
	 * create the menu for sync or add a voice
	 * 
	 * @see android.app.Fragment#onCreateOptionsMenu(android.view.Menu,
	 *      android.view.MenuInflater)
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.feed_list, menu);
	}
	

/**
	 * @see android.app.Fragment#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.sync_menu:
			checkSync();
			return true;
		case R.id.addFeed_menu:
			addNewFeed();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * call back method for add a new feed
	 */
	private void addNewFeed() {
		Log.d(TAG, "click addNewFeed\n");

	}

	/**
	 * call back method for check if there are update
	 */
	private void checkSync() {
		Log.d(TAG, "click checkSync\n");
		//start the thread
		if(isOnline())
			new CheckRssUpdate(this.getActivity(),feedsList).execute(rssBookmark);
		else{
			showError(ERROR_NETWORK);
		}
	}

	/**
	 *  class that build a itemView of the list, is neccessary for manage the bookmarms image
	 */
	private class BuildItemView implements SimpleCursorAdapter.ViewBinder {

		/**
		 * @see android.widget.SimpleCursorAdapter.ViewBinder#setViewValue(android.view.View,
		 *      android.database.Cursor, int)
		 */
		public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
			TextView name = (TextView) view
					.findViewById(R.id.feedItem_name);

			if (columnIndex == favIconColumn) {
				byte[] image = cursor.getBlob(favIconColumn);
				Drawable d;
				if (image != null){
					 d= new BitmapDrawable(view.getResources(),BitmapFactory.decodeByteArray(image, 0,
							image.length));
				}else{
					//set the default icon
					d = view.getResources().getDrawable(R.drawable.feed_icon);
				}
				
				d.setBounds(0, 0, iconSize, iconSize);
				name.setCompoundDrawables(d, null,null,null);
			} else if (columnIndex == titleColumn) {
				name.setText(cursor.getString(titleColumn));
			}else{
				return false;
			}
			// if-else-else

			return true;
		}

	}

	
	/**
	 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
	 */
	@Override
	public void onItemClick(AdapterView<?> l, View feedItem, int position, long id) {
			
		TextView name = (TextView) feedItem.findViewById(R.id.feedItem_name);
		name.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));

		rssBookmark.moveToPosition(position);
		Log.d(TAG,"Click position: "+position+" url: "+rssBookmark.moveToPosition(position));
		
		RssViewFragment rssView = (RssViewFragment) getFragmentManager().findFragmentById(R.id.rss_view_fragment);
		
		if(rssView!=null){
			rssView.viewRss(rssBookmark.getString(urlColumn));
		}else{
			Log.d(TAG,"start intent");
			Intent i = new Intent(this.getActivity(),RssViewActivity.class);
			i.putExtra(RssViewActivity.RSS_URL,rssBookmark.getString(urlColumn));
			startActivity(i);
		}

	}

	/**
	 * create the cursor for load the bookmarks, it show the id,icon,date,title and url
	 * @see android.app.LoaderManager.LoaderCallbacks#onCreateLoader(int,
	 *      android.os.Bundle)
	 */
	public Loader<Cursor> onCreateLoader(int id, Bundle arg1) {
		switch (id) {
		case CURSOR_BOOKMARK:
			/*return new CursorLoader(this.getActivity(),
					android.provider.Browser.BOOKMARKS_URI, new String[] {
							Browser.BookmarkColumns._ID,
							Browser.BookmarkColumns.FAVICON,
							Browser.BookmarkColumns.DATE,
							Browser.BookmarkColumns.TITLE,
							Browser.BookmarkColumns.URL }, 
							//show only the bookmarks
							"("	+ Browser.BookmarkColumns.BOOKMARK + "=1)",
							//order by title
							null,Browser.BookmarkColumns.TITLE);
							*/
			return new FullBookmarks(this.getActivity());
		default:
			return null;
		}

	}

	/**
	 * save the loaded cursor, and init the variable tha maps the column name
	 * @see android.app.LoaderManager.LoaderCallbacks#onLoadFinished(android.content.Loader, java.lang.Object)
	 */
	public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
		favIconColumn = c.getColumnIndex(FullBookmarks.FAVICON);
		titleColumn = c.getColumnIndex(FullBookmarks.TITLE);
		urlColumn = c.getColumnIndex(FullBookmarks.URL);
		lastVisitColumn = c.getColumnIndex(FullBookmarks.LAST_VISIT);
		rssBookmark = c;
		//build the listview
		feeds.swapCursor(c);
	}

	/**
	 * invalidate the cursor
	 * @see android.app.LoaderManager.LoaderCallbacks#onLoaderReset(android.content.Loader)
	 */
	public void onLoaderReset(Loader<Cursor> loader) {
		rssBookmark = null;
		feeds.swapCursor(null);

	}

	/**
	 * scann all the rss for check update
	 * don't work because the date from the bookmars isn update when you load the feed from the app
	 */
	private class CheckRssUpdate extends AsyncTask<Cursor, Integer, Integer> {

		private ListView list;
		private Activity context;

		/**
		 * @param lf fragment that show the list to update
		 */
		public CheckRssUpdate(Activity c,ListView l) {
			list = l;
			context = c;
		}

		/**
		 * for each bookmarks load the xml and save the update data,
		 * after that check if the data is after the last vist
		 * TODO: save the loaded xml for use it when the user clik in the item view
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Integer doInBackground(Cursor... arg) {
			int nUpdate = 0;
			long lastVisitDate;
			Cursor c = arg[0];
			int nFeed = c.getCount();

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
						url = new URL(c.getString(urlColumn));
						HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();	
						urlConnection.addRequestProperty("Cache-Control", "no-cache"); // fresh data
						xr.parse(new InputSource(urlConnection.getInputStream()));
					} catch (MalformedURLException e) {
						showError(ERROR_URL,c.getString(urlColumn));
					} catch (IOException e) {
						showError(ERROR_NETWORK);
					} catch (SAXException e) { // early stop of the parser

					}

					lastVisitDate = c.getLong(lastVisitColumn);

					if (lastVisitDate < xmlHandler.getLastUpdate().getTime()) {

						context.runOnUiThread(new UpdateUi(list, i));

						nUpdate++;
					}

					publishProgress((int) ((i * 100) / (float) nFeed));
					
				}

			} catch (ParserConfigurationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (SAXException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			return nUpdate;
		}

		/**
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(Integer notUsed) {

		}

		/**
		 * thread that change change the view of the item,
		 * the name will be write in bold
		 */
		private class UpdateUi implements Runnable {

			private int index;
			private ListView list;

			/**
			 * @param l
			 * @param i
			 */
			public UpdateUi(ListView l, int i) {
				index = i;
				list = l;
			}

			/**
			 * @see java.lang.Runnable#run()
			 */
			public void run() {
				View feedItem = list.getChildAt(index);
				TextView name = (TextView) feedItem
						.findViewById(R.id.feedItem_name);
				name.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
			} //run

		}//updateUi

	}//CheckRssUpdate

	

}
