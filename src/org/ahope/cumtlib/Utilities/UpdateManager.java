package org.ahope.cumtlib.Utilities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import org.ahope.cumtlib.R;
import org.ahope.cumtlib.Entity.AppConstant;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

public class UpdateManager
{

	private static final int DOWNLOAD = 1;
    private static final int NO = 3;
    private static final int SHOW = 4;
	private static final int DOWNLOAD_FINISH = 2;

	HashMap<String, String> mHashMap;

	private String mSavePath;

	private boolean cancelUpdate = false;

	private Context mContext;

	private ProgressBar mProgress;
	private Dialog mDownloadDialog;
	
	boolean flag = false;
	
	private Handler mHandler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{

			case DOWNLOAD:
				mProgress.setProgress(100);
				break;
			case DOWNLOAD_FINISH:
				installApk();
				break;
			case NO:
				Toast.makeText(mContext, "已经是最新版本", Toast.LENGTH_LONG).show();
				break;
			case SHOW:
				showNoticeDialog();
				break;
			default:
				break;
			}
		};
	};

	public UpdateManager(Context context)
	{
		this.mContext = context;
	}


	
	public void checkUpdate() throws Exception, Exception
	{
		
		new Thread (new Runnable() {
			
			
			public void run() {
				// TODO Auto-generated method stub
				try {
					flag = isUpdate();
					if (flag)
					{
			     		mHandler.sendEmptyMessage(SHOW);
			     		flag = false;
					} else
					{
						mHandler.sendEmptyMessage(NO);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
		
		
	}


	private boolean isUpdate() throws Exception, Exception
	{

		String versionname = getVersionCode(mContext);
		ParseXmlService service = new ParseXmlService();
		try
		{
			URL url = new URL(AppConstant.UpdateUrl); 
            HttpURLConnection conn = (HttpURLConnection)url.openConnection(); 
            conn.setReadTimeout(5*1000); 
            conn.setRequestMethod("GET"); 
            InputStream inStream = conn.getInputStream();
			mHashMap = service.parseXml(inStream);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		if (null != mHashMap)
		{
			float serviceCode = Float.valueOf(mHashMap.get("version"));
			float a;
            a=Float.parseFloat(versionname);
			if (serviceCode > a)
			{
				return true;
			}
		}
		return false;
	}


	private String getVersionCode(Context context)
	{
		String versionCode = "1.0";
		try
		{

			versionCode = context.getPackageManager().getPackageInfo("org.ahope.cumtlib", 0).versionName;
		} catch (NameNotFoundException e)
		{
			e.printStackTrace();
		}
		return versionCode;
	}


	private void showNoticeDialog()
	{

		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle("软件更新");
		builder.setMessage("检测到新版本，立即更新吗?");

		builder.setPositiveButton("更新", new OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
				showDownloadDialog();
			}
		});

		builder.setNegativeButton("稍后更新", new OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
			}
		});
		Dialog noticeDialog = builder.create();
		noticeDialog.show();
	}


	private void showDownloadDialog()
	{

		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle("正在更新");
		final LayoutInflater inflater = LayoutInflater.from(mContext);
		View v = inflater.inflate(R.layout.softupdate_progress, null);
		mProgress = (ProgressBar) v.findViewById(R.id.update_progress);
		builder.setView(v);

		builder.setNegativeButton("取消", new OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();

				cancelUpdate = true;
			}
		});
		mDownloadDialog = builder.create();
		mDownloadDialog.show();

		downloadApk();
	}


	private void downloadApk()
	{

		new downloadApkThread().start();
	}


	private class downloadApkThread extends Thread
	{
		private int progress;

		@Override
		public void run()
		{
			try
			{

				if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
				{

					String sdpath = Environment.getExternalStorageDirectory() + "/";
					mSavePath = sdpath + "download";
					URL url = new URL(mHashMap.get("url"));

					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.connect();

					int length = conn.getContentLength();

					InputStream is = conn.getInputStream();

					File file = new File(mSavePath);

					if (!file.exists())
					{
						file.mkdir();
					}
					File apkFile = new File(mSavePath, mHashMap.get("name"));
					FileOutputStream fos = new FileOutputStream(apkFile);
					int count = 0;

					byte buf[] = new byte[1024];

					do
					{
						int numread = is.read(buf);
						count += numread;
						progress = (int) (((float) count / length) * 100);

						mHandler.sendEmptyMessage(DOWNLOAD);
						if (numread <= 0)
						{

							mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
							break;
						}

						fos.write(buf, 0, numread);
					} while (!cancelUpdate);
					fos.close();
					is.close();
				}
			} catch (MalformedURLException e)
			{
				e.printStackTrace();
			} catch (IOException e)
			{
				e.printStackTrace();
			}

			mDownloadDialog.dismiss();
		}
	};


	
	
	private void installApk()
	{
		File apkfile = new File(mSavePath, mHashMap.get("name"));
		if (!apkfile.exists())
		{
			return;
		}

		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
		mContext.startActivity(i);
	}
}
