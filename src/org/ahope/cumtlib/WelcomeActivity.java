package org.ahope.cumtlib;

import java.util.Timer;
import java.util.TimerTask;

import com.baidu.mobstat.StatService;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;

public class WelcomeActivity extends Activity {
	String name,pwd;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.welcome);
		final Intent i = new Intent(this, LibActivity.class); // 你要转向的Activity
		final Intent j = new Intent(this,MyBook.class);
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				// 执行
				if(isLogin())
				{
					Bundle bundle = new Bundle();
					bundle.putString("mbook", "");
					bundle.putString("user",name);
					bundle.putString("passwd", pwd);
					j.putExtras(bundle);
					startActivity(j);
				}
				else
					startActivity(i);
				finish();
			}
		};
		// 1秒后
		timer.schedule(task, 1000);
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
	//判断是否登陆
		boolean isLogin(){
			SharedPreferences pre = getSharedPreferences("user_msg",
					MODE_WORLD_READABLE);
			name = pre.getString("name", "-1");
			pwd = pre.getString("password", "-1");
			if(name.equals("-1")&&pwd.equals("-1"))
			    return false;
			else
				return true;
		}
}
