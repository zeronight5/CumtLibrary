package org.ahope.cumtlib;

import java.util.ArrayList;
import java.util.List;

import org.ahope.cumtlib.Entity.MBook;
import org.ahope.cumtlib.Utilities.BooksDB;
import org.ahope.cumtlib.Utilities.ScanBook;
import org.ahope.cumtlib.Utilities.StringToBook;
import org.ahope.cumtlib.Utilities.SysApplication;
import org.ahope.cumtlib.Utilities.UpdateManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;

public class MyBook extends Activity {
	private ProgressDialog proDialog;
	private ImageButton ScanButton;
	private EditText ScanET;
	private ListView mbookList;
	myAdapter adapter;
	String mbook = null;
	String user ;
	String passwd;
	String rebookresult;
	private List<MBook> mlist = new ArrayList<MBook>();
	private List<MBook> list = new ArrayList<MBook>();
	mHandler handler = new mHandler();
	//检索书目方法标记，真为按书名检索，假为按作者检索
	boolean scanflag = true;
	RadioGroup group;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		SysApplication.getInstance().addActivity(this);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.mybook);
		Intent intent = this.getIntent();
		Bundle mybundle = intent.getExtras();
		mbook = mybundle.getString("mbook");
		user = mybundle.getString("user");
		passwd = mybundle.getString("passwd");
		ScanET = (EditText) findViewById(R.id.scan_book3);
		mbookList = (ListView) findViewById(R.id.mbooklist);
		ScanButton = (ImageButton) findViewById(R.id.scan_button3);
		group = (RadioGroup) findViewById(R.id.rgroup);
		group.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				//获取变更后的选中项的ID
				int radioButtonId = group.getCheckedRadioButtonId();
				//根据ID获取RadioButton的实例
				RadioButton rb = (RadioButton)MyBook.this.findViewById(radioButtonId);
				if(rb.getText().equals("按作者"))
					scanflag = false;
				else
					scanflag = true;
			}
		});
		ScanButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if(ScanBook.isConnectInternet(MyBook.this))
				{
					String bookname = ScanET.getText().toString();
					if(bookname.equals("")||bookname==null)
						Toast.makeText(MyBook.this, "请输入检索内容。", Toast.LENGTH_SHORT)
						.show();
					else
					{
						Intent i = new Intent(MyBook.this, ScanResult.class);
						Bundle bundle = new Bundle();
						bundle.putString("bookname", bookname);
						bundle.putBoolean("scanflag", scanflag);
						i.putExtras(bundle);
						startActivity(i);
					}
				}
				else
					Toast.makeText(MyBook.this, "您还没有联网。", Toast.LENGTH_SHORT)
					.show();
			}
		});
		adapter = new myAdapter(this, list);
		mbookList.setAdapter(adapter);
		if(mbook.equals(""))
		{
			BooksDB bookdb = new BooksDB(MyBook.this);
			SQLiteDatabase db = bookdb.getReadableDatabase();
			Cursor cursor = db.query("books_table", null, null, null, null, null, null);
			while (cursor.moveToNext()) { 
				MBook info = new MBook();
                String book_name = cursor.getString(cursor.getColumnIndex("book_name"));  
                String borrow_time = cursor.getString(cursor.getColumnIndex("borrow_time"));
                String back_time = cursor.getString(cursor.getColumnIndex("back_time"));
                String borrow_times = cursor.getString(cursor.getColumnIndex("borrow_times"));
                String cno = cursor.getString(cursor.getColumnIndex("con"));
                info.setName(book_name);
                info.setBorrowTime(borrow_time);
                info.setBackTime(back_time);
                info.setRebookTimes(borrow_times);
                info.setBarcode(cno);
                list.add(info);
            }  
			db.close();
			cursor.close();
		}
		else 
		{mlist = StringToBook.StringtoMybook(mbook);
		if (mlist.size() > 1) {
			BooksDB bookdb = new BooksDB(MyBook.this);
			for (int i = 1; i < mlist.size(); i++)
			{
				bookdb.insert(mlist.get(i).getName(), mlist.get(i).getBorrowTime(), mlist.get(i).getBackTime(), mlist.get(i).getRebookTimes(), mlist.get(i).getBarcode());
			}
		}
		
		new Thread(new Runnable() {
			public void run() {
				// TODO Auto-generated method stub
				if (mlist.size() > 1) {
					for (int i = 1; i < mlist.size(); i++)
					{
						adapter.addItem(mlist.get(i));
					}
					handler.sendEmptyMessage(1);
				} else
					handler.sendEmptyMessage(0);
			}
		}).start();}
		
	}

	// List适配器
	class myAdapter extends BaseAdapter {
		private Context context;
		private List<MBook> list;
		private LayoutInflater mInflater;

		// public Map<String,String> myCxData;

		public myAdapter(Context context, List<MBook> list) {
			// TODO Auto-generated constructor stub
			this.context = context;
			this.list = list;
			mInflater = LayoutInflater.from(context);
		}

		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public void addItem(MBook item) {
			list.add(item);
		}

		public void clean(){
			list.clear();
		}
		
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (null == convertView) {
				holder = new ViewHolder();
				mInflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = mInflater.inflate(R.layout.mbookinfo, null);
				holder.booknameTV = (TextView) convertView
						.findViewById(R.id.bookname);
				holder.borrowtime = (TextView) convertView
						.findViewById(R.id.borrowtime);
				holder.returntime = (TextView) convertView
						.findViewById(R.id.returntime);
				holder.rebooktimes = (TextView) convertView
						.findViewById(R.id.rebooktimes);
				holder.rebookBT = (ImageButton) convertView.findViewById(R.id.rebookbutton);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.booknameTV.setText(list.get(position).getName());
			holder.borrowtime.setText("借阅日期:"
					+ list.get(position).getBorrowTime());
			holder.returntime.setText("应还日期:"
					+ list.get(position).getBackTime());
			holder.rebooktimes.setText("续借次数:"
					+ list.get(position).getRebookTimes());
			holder.rebookBT.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					// TODO Auto-generated method stub
					final String barcode = list.get(position).getBarcode();
					if(ScanBook.isConnectInternet(MyBook.this))
					{
					if(list.get(position).getRebookTimes().equals("0"))
					{
						showDialog(0);
						new Thread(new Runnable() {
							public void run() {
								rebookresult = ScanBook.reBook(barcode, user, passwd);
								handler.sendEmptyMessage(2);
							}
						}).start();
					}
					else
						handler.sendEmptyMessage(3);
					}
					else
						Toast.makeText(MyBook.this, "您还没有联网。",
								Toast.LENGTH_SHORT).show();
				}
			});
			return convertView;
		}
	}

	static class ViewHolder {
		public TextView booknameTV;
		public TextView borrowtime;
		public TextView returntime;
		public TextView rebooktimes;
		public ImageButton rebookBT;

	}

	class mHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				if (proDialog != null) {
					proDialog.dismiss();
				}
				Toast.makeText(MyBook.this, "您还没有借书。",
						Toast.LENGTH_SHORT).show();
				break;
			case 1:
				if (proDialog != null) {
					proDialog.dismiss();
				}
				adapter.notifyDataSetChanged();
				break;
			case 2:
				removeDialog(0);
				Toast.makeText(MyBook.this, rebookresult,
						Toast.LENGTH_SHORT).show();
				break;
			case 3:
				Toast.makeText(MyBook.this, "超过最大借阅次数，请到图书馆续借。",
						Toast.LENGTH_SHORT).show();
				break;
			case 4:
				if (proDialog != null) {
					proDialog.dismiss();
				}
				Toast.makeText(MyBook.this, "加载失败。",
						Toast.LENGTH_SHORT).show();
				break;
			}
		}
	}
	
	void refresh(){
		proDialog = ProgressDialog.show(MyBook.this, null,
				"刷新中..请稍后....", true, true);
		new Thread(new Runnable() {
			public void run() {
				// TODO Auto-generated method stub
				String r = ScanBook.mybook(user, passwd);
				if(r.equals("1") || r.equals("0"))
				{
					handler.sendEmptyMessage(4);
				}
				else
				{
				List<MBook> l = StringToBook.StringtoMybook(r);
				if (l.size() > 1) {
					adapter.clean();
					BooksDB bookdb = new BooksDB(MyBook.this);
					SQLiteDatabase db = bookdb.getWritableDatabase();
					String sql = "DROP TABLE IF EXISTS books_table;";
					String sqlc = "CREATE TABLE books_table(book_id INTEGER primary key autoincrement,book_name text,borrow_time text,back_time text,borrow_times text,con text);";
			        db.execSQL(sql);
			        db.execSQL(sqlc);
					for (int i = 1; i < l.size(); i++)
					{
						adapter.addItem(l.get(i));
						bookdb.insert(l.get(i).getName(), l.get(i).getBorrowTime(), l.get(i).getBackTime(), l.get(i).getRebookTimes(), l.get(i).getBarcode());
					}
					db.close();
					handler.sendEmptyMessage(1);
				} else
					handler.sendEmptyMessage(0);
				}
			}
		}).start();
	}
	
	void logoff(){
		//清除数据库
		BooksDB bookdb = new BooksDB(MyBook.this);
		SQLiteDatabase db = bookdb.getWritableDatabase();
		String sql = "DROP TABLE IF EXISTS books_table;";
		String sqlc = "CREATE TABLE books_table(book_id INTEGER primary key autoincrement,book_name text,borrow_time text,back_time text,borrow_times text,con text);";
        db.execSQL(sql);
        db.execSQL(sqlc);
        db.close();
        //清除用户信息
        SharedPreferences pre = getSharedPreferences("user_msg",
				MODE_WORLD_WRITEABLE);
		SharedPreferences.Editor editor = pre.edit();
		editor.putString("name", "-1");
		editor.putString("password", "-1");
		editor.commit();
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		ProgressDialog dialog = new ProgressDialog(this);
		dialog.setMessage("续借中...请稍候");
		dialog.setIndeterminate(true);
		dialog.setCancelable(true);
		return dialog;
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		StatService.onResume(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		StatService.onPause(this);
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu);
		menu.add(0,Menu.FIRST,0,"刷新");
		menu.add(0,Menu.FIRST+1,1,"注销");
		menu.add(0,Menu.FIRST+2,2,"反馈");
		menu.add(0,Menu.FIRST+3,3,"更新");
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId()){
		case Menu.FIRST:
			if(ScanBook.isConnectInternet(this))
				refresh();
			else 
				Toast.makeText(MyBook.this, "您还没有联网。",
						Toast.LENGTH_SHORT).show();	
			break;
		case Menu.FIRST+1:
			logoff();
			Intent i=new Intent();
			i.setClass(MyBook.this, LibActivity.class);
			startActivity(i);
			finish();
			break;
		case Menu.FIRST+2:
			Intent j=new Intent();
			j.setClass(MyBook.this, FeedbackActivity.class);
			startActivity(j);
			break;
		case Menu.FIRST+3:
			UpdateManager manager = new UpdateManager(MyBook.this);
			try {
				manager.checkUpdate();
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Dialog alertDialog = new AlertDialog.Builder(this)
					.setTitle("确定退出？")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									SysApplication.getInstance().exit();
								}
							})
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
								}
							}).create();
			alertDialog.show();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
