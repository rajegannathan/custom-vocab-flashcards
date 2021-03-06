package in.rajegannathan.grewordcards;

import in.rajegannathan.grewordcards.DatabaseContract.Words;
import in.rajegannathan.grewordcards.localdb.DBHelper;

import java.util.logging.Logger;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;


public class ListWordsActivity extends ListActivity {

	private static final Logger logger = Logger
			.getLogger(ListWordsActivity.class.getName());
	private DBHelper mDbHelper;
	private SimpleCursorAdapter mAdapter;
    public final static String EXTRA_MESSAGE = "POSITION";
	
	private Cursor getCursorForListView(){
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		String[] projection = { BaseColumns._ID, Words.COLUMN_WORD,
				Words.COLUMN_VIEWS };
		String sortOrder = Words.COLUMN_CREATED_AT + " ASC";
		//Cursor cursor = db.query(Words.TABLE_NAME, projection, null, null,
				//null, null, sortOrder);
        Cursor cursor = db.rawQuery("Select ( Select COUNT(0) from " + Words.TABLE_NAME +
                        " w Where w._id < w2._id ) as 'RN' , "+Words.COLUMN_WORD + " from "+
                        Words.TABLE_NAME +" w2 ORDER BY "+ Words.COLUMN_CREATED_AT  + "ASC", null);

        return cursor;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mDbHelper = new DBHelper(getApplicationContext());

        Cursor cursor = getCursorForListView();


		String[] fromColumns = { Words.COLUMN_WORD , "RN" };
		int[] toViews = { R.id.singleWord , R.id.index};

		mAdapter = new SimpleCursorAdapter(this,
				R.layout.fragment_list_words, cursor, fromColumns, toViews, 0);

		setListAdapter(mAdapter);
		
		ListView wordList = getListView();
		registerForContextMenu(wordList);
		
/*		if (wordList != null) {
			wordList.setOnItemLongClickListener(new OnItemLongClickListener() {

				@Override
				public boolean onItemLongClick(AdapterView<?> parent,
						View view, int position, long id) {
					logger.info("longPress on item. Position is "+position+" id is "+id);
					return false;
				}
			});
		} else {
			logger.info("wordList is empty");
			ListView listView = getListView();
			logger.info("listview is "+listView.toString());
		}*/
	}

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent intent = new Intent(this, FlashCardActivity.class);
        intent.putExtra(EXTRA_MESSAGE , position);
        startActivity(intent);

    }

    @Override
	public void onCreateContextMenu(android.view.ContextMenu menu, View v, android.view.ContextMenu.ContextMenuInfo menuInfo) {
		logger.info("in create context view method");
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.word_context_menu, menu);
	};
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		logger.info("Selected item is " + item.getItemId());
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		logger.info("selected item is at "+info.id);
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		db.delete(Words.TABLE_NAME, Words.COLUMN_ID+" = ?", new String[]{"" +info.id});
		
		Cursor cursor = getCursorForListView();
		mAdapter.changeCursor(cursor);
		
		return super.onContextItemSelected(item);
	};
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.list_words, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
