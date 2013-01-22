package org.ahope.cumtlib;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.ahope.cumtlib.Entity.Book;
import org.ahope.cumtlib.Utilities.ScanBook;
import org.ahope.cumtlib.Utilities.StringToBook;
import org.ahope.cumtlib.Utilities.SysApplication;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;

public class ScanResult extends Activity implements OnScrollListener {
	private ListView bookList;
	myAdapter adapter;
	private View loadMoreView;
	private int visibleLastIndex = 0; // 最后的可视项索引
	private int visibleItemCount; // 当前窗口可见项总数
	int j = 2; // 加载更多页码计数
	boolean flag = false; // 是否加载更多标记
	private ImageButton ScanButton;
	private EditText ScanET;

	String scanname = null;
	String ScanResult = null;
	List<Book> list = new ArrayList<Book>();
	String result = null;
	String bookname;
	String booknum;
	String storenum;
	String surplusnum;
	String factory;
	String author;

	//检索书目方法标记，真为按书名检索，假为按作者检索
	boolean scanflag = true;
	RadioGroup group;
	private Timer timer;
	Thread scanbookt;
	private static final int TIMER_EXECUTE = 0;
	private static final int REFRESH_LISTVIEW = 1;
	private static final int LOADFALSE = 2;
	private static final int NOBOOKS = 3;
	private static final int NOMOREBOOKS = 4;
	private final int CHECK_TIME = 20000;
	mHandler handler = new mHandler();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SysApplication.getInstance().addActivity(this);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.scan_result);
		Intent intent = this.getIntent();
		Bundle mybundle = intent.getExtras();
		scanname = mybundle.getString("bookname");
		scanflag = mybundle.getBoolean("scanflag");
		bookList = (ListView) findViewById(R.id.booklist);
		loadMoreView = getLayoutInflater().inflate(R.layout.load_more, null);
		bookList.addFooterView(loadMoreView);
		ScanET = (EditText) findViewById(R.id.scan_book2);
		ScanButton = (ImageButton) findViewById(R.id.scan_button2);
		group = (RadioGroup) findViewById(R.id.rgroup);
		ScanET.setText(scanname);
		LoadBooks();
		adapter = new myAdapter(this);
		bookList.setAdapter(adapter);
		bookList.setOnScrollListener(this);
		group.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				//获取变更后的选中项的ID
				int radioButtonId = group.getCheckedRadioButtonId();
				//根据ID获取RadioButton的实例
				RadioButton rb = (RadioButton)ScanResult.this.findViewById(radioButtonId);
				if(rb.getText().equals("按作者"))
					scanflag = false;
				else
					scanflag = true;
			}
		});
		ScanButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(ScanBook.isConnectInternet(ScanResult.this))
				{
					flag = false;
					scanname = ScanET.getText().toString();
					if(scanname.equals("")||scanname==null)
						Toast.makeText(ScanResult.this, "请输入检索内容。", Toast.LENGTH_SHORT)
						.show();
					else
					    LoadBooks();
				}
				else
					Toast.makeText(ScanResult.this, "您还没有联网。", Toast.LENGTH_SHORT)
					.show();
			}
		});

		bookList.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(ScanResult.this, BDetail.class);
				Bundle bundle = new Bundle();
				bundle.putString("bidnum", list.get(position).getBidNum());
				bundle.putString("bookname", list.get(position).getName());
				bundle.putString("number", list.get(position).getNumber());
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
	}

	private String getData() {
		try {
			ScanResult = ScanBook.Scan(scanname, "1",scanflag);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ScanResult;
	}

	private void LoadBooks() {
		showDialog(0);
		timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				Message msg = new Message();
				msg.what = TIMER_EXECUTE;
				handler.sendMessage(msg);
			}
		}, CHECK_TIME);
		scanbookt = new Thread(new Runnable() {
			public void run() {
				// TODO Auto-generated method stub
				result = getData();
				if (result != null) {
					{
						list = StringToBook.stringtobook(result);
						if (list.size()>0)
							handler.sendEmptyMessage(REFRESH_LISTVIEW);
						else
							handler.sendEmptyMessage(NOBOOKS);
					}
				} else
					handler.sendEmptyMessage(LOADFALSE);
			}
		});
		scanbookt.start();

	}

	// List适配器
	class myAdapter extends BaseAdapter {
		private Context context;
		private LayoutInflater mInflater;

		// public Map<String,String> myCxData;

		public myAdapter(Context context) {
			// TODO Auto-generated constructor stub
			this.context = context;
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

		public void addItem(Book item) {
			list.add(item);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (null == convertView) {
				holder = new ViewHolder();
				mInflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = mInflater.inflate(R.layout.scanbookinfo, null);
				holder.booknameTV = (TextView) convertView
						.findViewById(R.id.bookname);
				holder.numberTV = (TextView) convertView
						.findViewById(R.id.booknum);
				holder.authorTV = (TextView) convertView
						.findViewById(R.id.author);
				holder.storenumTV = (TextView) convertView
						.findViewById(R.id.storenum);
				holder.surplusnumTV = (TextView) convertView
						.findViewById(R.id.surplusnum);
				holder.factoryTV = (TextView) convertView
						.findViewById(R.id.factory);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.booknameTV.setText(list.get(position).getName());
			holder.numberTV.setText("索书号:" + list.get(position).getNumber());
			holder.authorTV.setText(list.get(position).getAuthor());
			holder.storenumTV.setText("馆藏副本:"
					+ list.get(position).getStoreNum());
			holder.surplusnumTV.setText("可借副本:"
					+ list.get(position).getSurplusNum());
			holder.factoryTV.setText(list.get(position).getFactory());
			return convertView;
		}
	}

	static class ViewHolder {
		public TextView booknameTV;
		public TextView authorTV;
		public TextView numberTV;
		public TextView storenumTV;
		public TextView surplusnumTV;
		public TextView factoryTV;

	}

	class mHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case TIMER_EXECUTE:
				removeDialog(0);
				Toast.makeText(ScanResult.this, "联网超时，请重试。", Toast.LENGTH_SHORT)
						.show();
				break;
			case REFRESH_LISTVIEW:
				removeDialog(0);
				timer.cancel();
				if (list.size() > 5)
					flag = true;
				adapter.notifyDataSetChanged();
				break;
			case LOADFALSE:
				removeDialog(0);
				timer.cancel();
				scanbookt.stop();
				Toast.makeText(ScanResult.this, "联网超时，请重试。", Toast.LENGTH_SHORT)
						.show();
				break;
			case NOBOOKS:
				removeDialog(0);
				timer.cancel();
				Toast.makeText(ScanResult.this, "没有要查询的书目。", Toast.LENGTH_SHORT)
						.show();
				break;
			case NOMOREBOOKS:
				removeDialog(0);
				timer.cancel();
				Toast.makeText(ScanResult.this, "没有更多的书了。", Toast.LENGTH_SHORT)
						.show();
				flag = false;
				break;
			}
		}

	}

	@Override
	protected Dialog onCreateDialog(int id) {
		ProgressDialog dialog = new ProgressDialog(this);
		dialog.setMessage("加载中...请稍候");
		dialog.setIndeterminate(true);
		dialog.setCancelable(true);
		return dialog;
	}

	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		this.visibleItemCount = visibleItemCount;
		visibleLastIndex = firstVisibleItem + visibleItemCount - 1;
	}

	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		int itemsLastIndex = adapter.getCount() - 1; // 数据集最后一项的索引
		int lastIndex = itemsLastIndex + 1; // 加上底部的loadMoreView项
		if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
				&& visibleLastIndex == lastIndex && flag) {
			// 如果是自动加载,可以在这里放置异步加载数据的代码
			showDialog(0);
			new Thread(new Runnable() {

				public void run() {
					// TODO Auto-generated method stub
					List<Book> morelist = new ArrayList<Book>();

					try {

						ScanResult = ScanBook.Scan(scanname, String.valueOf(j),scanflag);
						j++;
						if (ScanResult != null) {
							morelist = StringToBook.stringtobook(ScanResult);
							if (morelist != null) {
								if (list.get(list.size() - 1)
										.getBidNum()
										.equals(morelist.get(
												morelist.size() - 1)
												.getBidNum()))
									handler.sendEmptyMessage(NOMOREBOOKS);
								else {
									for (int i = 0; i < morelist.size(); i++)
										adapter.addItem(morelist.get(i));
									handler.sendEmptyMessage(REFRESH_LISTVIEW);
								}
							} else
								handler.sendEmptyMessage(NOBOOKS);

						} else
							handler.sendEmptyMessage(LOADFALSE);
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}).start();
		}
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
}
