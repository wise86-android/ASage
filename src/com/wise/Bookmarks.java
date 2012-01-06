package com.wise;

import android.content.ContentProvider;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.RemoteException;
import android.provider.Browser;

public class Bookmarks {
	
	private ContentProviderClient bookmarksDb;
	
	public Bookmarks(ContentResolver cr){
		bookmarksDb = cr.acquireContentProviderClient(Browser.BOOKMARKS_URI);
	}
	
	public Cursor getDirectory(){
		try {
			return bookmarksDb.query(Browser.BOOKMARKS_URI,
					 new String[] {Browser.BookmarkColumns._ID, Browser.BookmarkColumns.TITLE},
					   "("+Browser.BookmarkColumns.BOOKMARK+"=1) AND (bookmarks.folder=1)",null,
					   Browser.BookmarkColumns.TITLE);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public Cursor getSubDirectory(int parentId){
		return null;
	}

	public Cursor getSubDirectory(String parent){
		return null;
	}
	
	public Cursor getAllBookmarks(){
		try {
			return bookmarksDb.query(Browser.BOOKMARKS_URI,
					 new String[] {Browser.BookmarkColumns._ID,Browser.BookmarkColumns.FAVICON, Browser.BookmarkColumns.DATE,
					   Browser.BookmarkColumns.TITLE,Browser.BookmarkColumns.URL},
					   "("+Browser.BookmarkColumns.BOOKMARK+"=1)",null,
					   Browser.BookmarkColumns.TITLE);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public Cursor getBookmarks(int parentId){
		return null;
	}
	
	public Cursor getBookmarks(String parent){
		return null;
	}
	
	public void updateLastAccess(int bookmarkId){
		
	}
	
	public void updateLastAccess(String name){
	}
		
}
