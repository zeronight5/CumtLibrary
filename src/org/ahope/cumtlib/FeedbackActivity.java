package org.ahope.cumtlib;

import org.ahope.cumtlib.Utilities.Feedback;
import org.ahope.cumtlib.Utilities.ScanBook;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.mobstat.StatService;

public class FeedbackActivity extends Activity{
	EditText feedback;
	Button commit;
	String contents;
	String result = null;
	mHandler handler = new mHandler();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.feedback);
		feedback = (EditText) findViewById(R.id.feedback_edit);
		commit = (Button) findViewById(R.id.commit);
		commit.setOnClickListener(new OnClickListener() {
			
		
			public void onClick(View v) {
				// TODO Auto-generated method stub
				contents = feedback.getText().toString();
				if(!contents.equals("")&&contents!=null)
				{
					if(ScanBook.isConnectInternet(FeedbackActivity.this))
					{
					showDialog(0);
					new Thread(new Runnable() {
						
						public void run() {
							// TODO Auto-generated method stub
							try {
								result = Feedback.feedback(contents);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							handler.sendEmptyMessage(1);
						}
					}).start();
					}
					else 
						Toast.makeText(FeedbackActivity.this, "您还没有联网。", Toast.LENGTH_LONG).show();
				}
				else 
					Toast.makeText(FeedbackActivity.this, "请输入反馈内容。", Toast.LENGTH_LONG).show();
			}
		});
	}

	class mHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
			    if(result.equals("1"))
			    {
			    	removeDialog(0);
			    	Toast.makeText(FeedbackActivity.this, "谢谢您的反馈。", Toast.LENGTH_LONG).show();
			        finish();
			    }	
			    else
			    {
			    	removeDialog(0);
					Toast.makeText(FeedbackActivity.this, "反馈失败。", Toast.LENGTH_LONG).show();
			    }
				break;
			}
		}
	}
	protected Dialog onCreateDialog(int id) {
		ProgressDialog dialog = new ProgressDialog(this);
		dialog.setMessage("发送中...请稍候");

		dialog.setIndeterminate(true);
		dialog.setCancelable(true);
		return dialog;
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		StatService.onPause(this);
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		StatService.onResume(this);
	}
}
