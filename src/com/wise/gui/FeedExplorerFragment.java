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
FeedExplorer.java
*/
package com.wise.gui;

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

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
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

import com.wise.R;
import com.wise.util.RssFeedsDB;
import com.wise.xml.ExtractRssUpdateDate;



/**
 * @author wise
 *
 */
public class FeedExplorerFragment extends OnlineFragment implements
	OnItemClickListener{

	private final static String TAG = "FeedExplorer";
	
	private final static String SALVED_GROUP_ID="FeedExplorerFragment_groupId";

	private RssFeedsDB rssDb;
	
	private int folderId;
	private Cursor rssInFolder;
	private Cursor groupInFolder;
	
	private int rssIdColumn;
	private int rssFavIconColumn;
	private int rssTitleColumn;
	private int rssUrlColumn;
	private int rssLastVisitColumn;
	
	private int folderNameColumn;
	private int folderIdColumn;
	
	private ListView feedsList;
	private ListView folderList;
	private SimpleCursorAdapter feeds;
	private SimpleCursorAdapter folders;
	
	private int iconSize;
	
	public FeedExplorerFragment(){
		this(1);
	}

	public FeedExplorerFragment(int rootId){
		folderId=rootId;
	}
	
	/** @see android.app.Fragment#onCreate(android.os.Bundle) */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if(savedInstanceState!=null)
			folderId = savedInstanceState.getInt(SALVED_GROUP_ID, 1);
		
		
		this.setHasOptionsMenu(true);

		iconSize = getResources().getDimensionPixelSize(R.dimen.iconSize_feedItem);
		int[] showItemRss = new int[] { R.id.feedItem_name, R.id.feedItem_name };
		String[] showValueRss = new String[] { RssFeedsDB.FEED_FAVICON,
				RssFeedsDB.FEED_NAME};

		feeds = new SimpleCursorAdapter(this.getActivity(), R.layout.feed_item,
				null, showValueRss, showItemRss, 0);
		feeds.setViewBinder(new BuildItemView());
		
		int[] showItemGroup = new int[] {R.id.feedGroup_name};
		String[] showValueGroup = new String[] {RssFeedsDB.FOLDER_NAME};
		folders= new SimpleCursorAdapter(this.getActivity(), R.layout.feed_folder,
				null, showValueGroup, showItemGroup, 0);

		// start loading the cursor
		//this.getLoaderManager().initLoader(CURSOR_BOOKMARK, null, this);
		rssDb = new RssFeedsDB(this.getActivity());
			
		rssInFolder = rssDb.getAllFeed(folderId);
		
		
		Log.d(TAG, "N FEED ="+rssInFolder.getCount());
		rssFavIconColumn = rssInFolder.getColumnIndex(RssFeedsDB.FEED_FAVICON);
		rssTitleColumn = rssInFolder.getColumnIndex(RssFeedsDB.FEED_NAME);
		rssUrlColumn = rssInFolder.getColumnIndex(RssFeedsDB.FEED_URL);
		rssLastVisitColumn = rssInFolder.getColumnIndex(RssFeedsDB.FEED_LASTACCESS);
		
		feeds.swapCursor(rssInFolder);
		
		groupInFolder = rssDb.getAllFolder(folderId);
		folderIdColumn = groupInFolder.getColumnIndex(RssFeedsDB.FOLDER_ID);
		folderNameColumn = groupInFolder.getColumnIndex(RssFeedsDB.FOLDER_NAME);
		
		Log.d(TAG, "N fodler ="+groupInFolder.getCount());
		folders.swapCursor(groupInFolder);
		
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.feed_explorer, container, false);
        
        
        feedsList = (ListView) v.findViewById(R.id.list_feed);
        Log.d(TAG, "LIST: "+feedsList+" feeds "+feeds);
        feedsList.setAdapter(feeds);
        feedsList.setOnItemClickListener(this);
        
        folderList = (ListView) v.findViewById(R.id.list_group);
        folderList.setAdapter(folders);
        folderList.setOnItemClickListener(this);
        
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
			new CheckRssUpdate(this.getActivity(),feedsList).execute(rssInFolder);
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

			if (columnIndex == rssFavIconColumn) {
				byte[] image = cursor.getBlob(rssFavIconColumn);
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
			} else if (columnIndex == rssTitleColumn) {
				name.setText(cursor.getString(rssTitleColumn));
			}else{
				return false;
			}
			// if-else-else

			return true;
		}

	}
	
	private void onClickFeed(View feedItem,int position){
		TextView name = (TextView) feedItem.findViewById(R.id.feedItem_name);
		name.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));

		rssInFolder.moveToPosition(position);
		Log.d(TAG,"Click position: "+position+" url: "+rssInFolder.moveToPosition(position));
		
		RssViewFragment rssView = (RssViewFragment) getFragmentManager().findFragmentById(R.id.rss_view_fragment);
		
		if(rssView!=null){
			rssView.viewRss(rssInFolder.getString(rssUrlColumn));
		}else{
			Log.d(TAG,"start intent");
			Intent i = new Intent(this.getActivity(),RssViewActivity.class);
			i.putExtra(RssViewActivity.RSS_URL,rssInFolder.getString(rssUrlColumn));
			startActivity(i);
		}

	}

	private void onClickGroup(View groupItem,int position){
	
		
		
		boolean ret = groupInFolder.moveToPosition(position);
		
		Log.d(TAG, "position "+position+" size "+groupInFolder.getCount()+"move "+ret);
		
		int newRoot = groupInFolder.getInt(folderIdColumn);
		
		Fragment f = new FeedExplorerFragment(newRoot);
		
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		ft.replace(R.id.feed_list_contaner, f);
		ft.addToBackStack(null);
		ft.commit();
		
	}
	
	/**
	 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
	 */
	@Override
	public void onItemClick(AdapterView<?> l, View v, int position, long id) {
		if (l==feedsList)
			onClickFeed(v,position);
		else
			onClickGroup(v,position);
	
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
						url = new URL(c.getString(rssUrlColumn));
						HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();	
						urlConnection.addRequestProperty("Cache-Control", "no-cache"); // fresh data
						xr.parse(new InputSource(urlConnection.getInputStream()));
					} catch (MalformedURLException e) {
						showError(ERROR_URL,c.getString(rssUrlColumn));
					} catch (IOException e) {
						showError(ERROR_NETWORK);
					} catch (SAXException e) { // early stop of the parser

					}

					lastVisitDate = c.getLong(rssLastVisitColumn);

					if (lastVisitDate < xmlHandler.getLastUpdate().getTime()) {
						Log.d(TAG, "lastVisit:"+lastVisitDate+" update"+xmlHandler.getLastUpdate().getTime());
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
