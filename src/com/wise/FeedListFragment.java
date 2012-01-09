package com.wise;

import android.app.Activity;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.CursorLoader;

import android.content.Loader;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Browser;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;



public class FeedListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>{
	
	
		private final static int CURSOR_BOOKMARK=0;
	
		private Cursor rssBookmark;
		private int  favIconColumn;
	    private int titleColumn;
	    private int urlColumn;
	    private int lastVisitColumn;
	    private SimpleCursorAdapter feeds;
	    
	
	   public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);      

	        this.setHasOptionsMenu(true);
	     
	     
	     //Log.d("Cursor","size: "+c.getCount());
	     int[] showItem = new int[] {R.id.feedItem_favIcon,R.id.feedItem_name};
	     String[] showValue = new String[]{Browser.BookmarkColumns.FAVICON,Browser.BookmarkColumns.TITLE};
	     feeds =  new SimpleCursorAdapter(this.getActivity(), R.layout.feed_item, null, showValue, showItem,0);
	     
	     feeds.setViewBinder(new BuildItemView());
	     
	     this.setListAdapter(feeds);
	       
	     this.getLoaderManager().initLoader(CURSOR_BOOKMARK, null, this);
	   } 
	   
	   
	   
	   @Override
	   public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){

		   inflater.inflate(R.menu.feed_list, menu);

	   }

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
		   
		     
		private void addNewFeed() {
			Log.d("menuFeedList","click addNewFeed\n");
			
		}

		private void checkSync() {
			Log.d("menuFeedList","click checkSync\n");
			new CheckRssUpdate(this).execute(rssBookmark);
		}



	private class BuildItemView implements SimpleCursorAdapter.ViewBinder{
		   
		   public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
				
			   if(columnIndex ==  favIconColumn){
					   ImageView icon = (ImageView) view.findViewById(R.id.feedItem_favIcon);
					   byte[] image = cursor.getBlob(columnIndex);
					   if (image!=null)
						   icon.setImageBitmap(BitmapFactory.decodeByteArray(image,0,image.length));
			   }else if (columnIndex ==  titleColumn){
					   TextView name = (TextView) view.findViewById(R.id.feedItem_name);
					   name.setText(cursor.getString(columnIndex));
			   }else
				   return false;
			   //if-else-else
	
			   return true;
		   }
		   
	   }
	   
	   	
		public void onItemClick(AdapterView<?> arg0, View feedItem, int position,
				long id) {
			
	   		TextView name = (TextView) feedItem.findViewById(R.id.feedItem_name);
			name.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
	   		
			
			
			/*Intent i = new Intent(this.getActivity(),ASageActivity.class);
			Log.d("Clik","Position: "+position+" id: "+id+" size:"+rssBookmark+"\n");
			rssBookmark.moveToPosition(position);
			i.putExtra(ASageActivity.ASAGE_URL,rssBookmark.getString(urlColumn));
			
			startActivity(i);
			*/
			
		}

		
		public Loader<Cursor> onCreateLoader(int id, Bundle arg1) {
			switch(id){
			case CURSOR_BOOKMARK:
				return new CursorLoader(this.getActivity(),android.provider.Browser.BOOKMARKS_URI,
			    		 new String[] {Browser.BookmarkColumns._ID,Browser.BookmarkColumns.FAVICON, Browser.BookmarkColumns.DATE,
			    		 			   Browser.BookmarkColumns.TITLE,Browser.BookmarkColumns.URL},
			    		 			   "("+Browser.BookmarkColumns.BOOKMARK+"=1)",null,
			    		 			   //null,null,
			    		 Browser.BookmarkColumns.TITLE);
			default:
				return null;
			}
				
		}

		
		public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
			favIconColumn = c.getColumnIndex(Browser.BookmarkColumns.FAVICON);
			titleColumn = c.getColumnIndex(Browser.BookmarkColumns.TITLE);
		    urlColumn = c.getColumnIndex(Browser.BookmarkColumns.URL);
		    lastVisitColumn = c.getColumnIndex(Browser.BookmarkColumns.DATE);
		    rssBookmark=c;
			feeds.swapCursor(c);
		}

		
		public void onLoaderReset(Loader<Cursor> loader) {
			rssBookmark=null;
			feeds.swapCursor(null);
			
		}

		private class CheckRssUpdate extends AsyncTask<Cursor,Integer,Integer>{

			private ListView list;
			private Activity context;
			
			public CheckRssUpdate(ListFragment lf){
				list = lf.getListView();
				context = lf.getActivity();
			}
			
			@Override
			protected Integer doInBackground(Cursor... arg) {
				int nUpdate=0;
				long lastVisitDate;
				Cursor c= arg[0];
				int nFeed = c.getCount();
				
				SAXParserFactory spf = SAXParserFactory.newInstance();
				SAXParser sp;
				XMLReader xr=null;
				try {
					sp = spf.newSAXParser();
					xr = sp.getXMLReader();		

				
					/** Create handler to handle XML Tags ( extends DefaultHandler ) */
					ExtractRssUpdateDate xmlHandler = new ExtractRssUpdateDate();
					xr.setContentHandler(xmlHandler);
					
					
					Log.d("Update","nFeed:"+nFeed);
					for(int i =0 ; i <nFeed; i++){
						c.moveToPosition(i);	
						URL url;
						try {
							url = new URL(c.getString(urlColumn));
							xr.parse(new InputSource(url.openStream()));
						} catch (MalformedURLException e) {
							Toast.makeText(context,R.string.url_error,5);
						} catch (IOException e) {
							Toast.makeText(context,R.string.network_error,5);
						} catch (SAXException e) { // early stop of the parser
							
						}

						lastVisitDate = c.getLong(lastVisitColumn);
						
						if(lastVisitDate < xmlHandler.getLastUpdate().getTime()){
							
						
							context.runOnUiThread(new UpdateUi(list,i));
							
							nUpdate++;
						}

						publishProgress((int) ((i*100) / (float) nFeed));
				
						Log.d("Update","i:"+i+"url:"+c.getPosition());
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
			
			@Override
			protected void onPostExecute(Integer notUsed){
				
			}
			
			private class UpdateUi implements Runnable{
				
				private int index;
				private ListView list;
				
				public UpdateUi(ListView l,int i){
					index = i;
					list =l;
				}
				
				
			    public void run() {
			    	View feedItem = list.getChildAt(index);
					TextView name = (TextView) feedItem.findViewById(R.id.feedItem_name);
					name.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
			    }
					
				
				
			}
						
		}

		   

}
