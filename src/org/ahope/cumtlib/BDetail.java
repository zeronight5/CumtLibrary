package org.ahope.cumtlib;

import java.util.ArrayList;
import java.util.List;

import org.ahope.cumtlib.Entity.BookDetail;
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
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;

public class BDetail extends Activity {
	private TextView booknameTV;
	private TextView booknumTV;
	private ListView list;
	private String bookname;
	private String bidnum;
	private String num;
	myAdapter adapter;
	mHandler handler = new mHandler();
	List<BookDetail> detlist = new ArrayList<BookDetail>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		SysApplication.getInstance().addActivity(this);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.bdetail);
		Intent intent = this.getIntent();
		Bundle mybundle = intent.getExtras();
		bookname = mybundle.getString("bookname");
		bidnum = mybundle.getString("bidnum");
		num = mybundle.getString("number");
		list = (ListView) findViewById(R.id.bookdetail);
		booknameTV = (TextView) findViewById(R.id.bookname);
		booknumTV = (TextView) findViewById(R.id.booknum);
		booknameTV.setText(bookname);
		booknumTV.setText(num);
		adapter = new myAdapter(this, detlist);
		list.setAdapter(adapter);
		LoadDetail(bidnum);
	}

	private void LoadDetail(final String bidnum) {
		showDialog(0);
		new Thread(new Runnable() {

			public void run() {
				// TODO Auto-generated method stub
				String result;
				List<BookDetail> bd = null;
				result = ScanBook.getDetail(bidnum);

				if (result != null) {
					bd = StringToBook.StringtoDetail(result);
					if (bd.size() > 1) {
						for (int i = 1; i < bd.size(); i++)
						{
							adapter.addItem(bd.get(i));
							System.out.println("--------->"+bd.get(i).toString());
						}
						handler.sendEmptyMessage(1);
					} else
						handler.sendEmptyMessage(0);
				}
			}
		}).start();
	}

	// List  ≈‰∆˜
	class myAdapter extends BaseAdapter {
		private Context context;
		private List<BookDetail> list;
		private LayoutInflater mInflater;

		// public Map<String,String> myCxData;

		public myAdapter(Context context, List<BookDetail> list) {
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

		public void addItem(BookDetail item) {
			list.add(item);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (null == convertView) {
				holder = new ViewHolder();
				mInflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = mInflater.inflate(R.layout.detail, null);
				holder.location = (TextView) convertView
						.findViewById(R.id.location);
				holder.state = (TextView) convertView.findViewById(R.id.state);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.location.setText("π›≤ÿµÿ:" + list.get(position).getLocation());
			holder.state.setText(" ÈøØ◊¥Ã¨:" + list.get(position).getState());

			return convertView;
		}
	}

	static class ViewHolder {
		public TextView location;
		public TextView state;

	}

	class mHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				removeDialog(0);
				Toast.makeText(BDetail.this, "º”‘ÿ ß∞‹°£", Toast.LENGTH_SHORT)
						.show();
				break;
			case 1:
				removeDialog(0);
				adapter.notifyDataSetChanged();
				break;
			}
		}

	}

	@Override
	protected Dialog onCreateDialog(int id) {
		ProgressDialog dialog = new ProgressDialog(this);
		dialog.setMessage("º”‘ÿ÷–...«Î…‘∫Ú...”–µ„¬˝≈∂...");
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
}
