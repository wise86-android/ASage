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

import java.util.Date;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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

import android.app.LoaderManager;

import com.wise.R;
import com.wise.util.RssFeedsDB;
import com.wise.util.SearchUpdateFeed;


/**
 * @author wise
 *
 */
public class FeedExplorerFragment extends OnlineFragment implements
	OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor>{

	private final static String TAG = "FeedExplorer";
	
	private final static String SALVED_GROUP_ID="FeedExplorerFragment_groupId";
	
	private final static int RSS_LOADER =0;
	
	private RssFeedsDB rssDb;
	
	private long folderId;
	private Cursor elementInFolder;
	
	private int rssFavIconColumn;
	private int rssUrlColumn;
	
	
	private int elementIdColumn;
	private int elementNameColumn;
	private int elementIsFeedColumn;
	private int elementHaveUpdateColumn;
	
	private ListView elementList;
	private SimpleCursorAdapter elementsAdapter;

	
	private int iconSize;
	
	public FeedExplorerFragment(){
		this(1);
	}

	public FeedExplorerFragment(long rootId){
		folderId=rootId;
	}
	
	/** @see android.app.Fragment#onCreate(android.os.Bundle) */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if(savedInstanceState!=null)
			folderId = savedInstanceState.getLong(SALVED_GROUP_ID, 1);
		
		
		this.setHasOptionsMenu(true);

		iconSize = getResources().getDimensionPixelSize(R.dimen.iconSize_feedItem);
		int[] showItemRss = new int[] { R.id.feedItem_name, R.id.feedItem_name };
		String[] showValueRss = new String[] { RssFeedsDB.FEED_FAVICON,
				RssFeedsDB.FEED_NAME};

		elementsAdapter = new SimpleCursorAdapter(this.getActivity(), R.layout.feed_item,
				null, showValueRss, showItemRss, 0);
		elementsAdapter.setViewBinder(new BuildItemView());
		
		// start loading the cursor
		//this.getLoaderManager().initLoader(CURSOR_BOOKMARK, null, this);
		rssDb = new RssFeedsDB(this.getActivity());
		
		Bundle loaderParam = new Bundle(1);
		loaderParam.putLong(RssFeedsDB.FOLDER_ID,folderId);
		
		getLoaderManager().initLoader(RSS_LOADER, loaderParam, this);
				
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.feed_list, container, false);
               
        elementList = (ListView) v.findViewById(R.id.list_feed);
        
        elementList.setAdapter(elementsAdapter);
        elementList.setOnItemClickListener(this);
        
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
		super.onCreateOptionsMenu(menu, inflater);
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
		if(isOnline()){
			new SearchUpdateFeed(this,rssDb).execute();
		}else{
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
			boolean isFeed = (cursor.getInt(elementIsFeedColumn)==1);
			if (columnIndex == rssFavIconColumn && isFeed) {
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
			} else if (columnIndex == elementNameColumn) {
				name.setText(cursor.getString(elementNameColumn));
				Log.d(TAG, cursor.getString(elementNameColumn)+" update: "+cursor.getInt(elementHaveUpdateColumn));
				if(cursor.getInt(elementHaveUpdateColumn)==1) // yes
					name.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
			}else{
				return false;
			}
			// if-else-else

			return true;
		}

	}
	
	private void onClickFeed(View feedItem){
		TextView name = (TextView) feedItem.findViewById(R.id.feedItem_name);
		name.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
		
		RssViewFragment rssView = (RssViewFragment) getFragmentManager().findFragmentById(R.id.rss_view_fragment);
		
		if(rssView!=null){
			rssView.viewRss(elementInFolder.getString(rssUrlColumn));
			rssDb.updateVisitDate(elementInFolder.getLong(elementIdColumn),new Date());
		}else{
			Log.d(TAG,"start intent");
			Intent i = new Intent(this.getActivity(),RssViewActivity.class);
			i.putExtra(RssViewActivity.RSS_URL,elementInFolder.getString(rssUrlColumn));
			startActivity(i);
		}

	}

	private void onClickGroup(View groupItem){
		
		long newRoot = elementInFolder.getLong(elementIdColumn);
		Log.d(TAG, "newRoot "+newRoot +" elemet "+folderId );
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
		elementInFolder.moveToPosition(position);
		if (elementInFolder.getInt(elementIsFeedColumn)==1)
			onClickFeed(v);
		else
			onClickGroup(v);
	
	}

	/**
	 * @see android.app.LoaderManager.LoaderCallbacks#onCreateLoader(int, android.os.Bundle)
	 */
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		if(id == RSS_LOADER)
			return rssDb.getAllElementAsync(args.getLong(RssFeedsDB.FOLDER_ID));
		return null; //TODO mettere un eccezione??
	}

	/**
	 * @see android.app.LoaderManager.LoaderCallbacks#onLoadFinished(android.content.Loader, java.lang.Object)
	 */
	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor elementsCursor) {
		
		elementInFolder = elementsCursor;
		
		elementIdColumn=elementInFolder.getColumnIndex(RssFeedsDB.ELEMENT_ID);
		elementNameColumn=elementInFolder.getColumnIndex(RssFeedsDB.ELEMENT_NAME);
		elementIsFeedColumn=elementInFolder.getColumnIndex(RssFeedsDB.ELEMENT_IS_FEED);
		elementHaveUpdateColumn=elementInFolder.getColumnIndex(RssFeedsDB.ELEMENT_HAVEUPDATE);
		rssFavIconColumn = elementInFolder.getColumnIndex(RssFeedsDB.FEED_FAVICON);
		rssUrlColumn = elementInFolder.getColumnIndex(RssFeedsDB.FEED_URL);
		
		elementsAdapter.swapCursor(elementInFolder);
	}

	/**
	 * @see android.app.LoaderManager.LoaderCallbacks#onLoaderReset(android.content.Loader)
	 */
	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		elementsAdapter.swapCursor(null);
		
	}

}
