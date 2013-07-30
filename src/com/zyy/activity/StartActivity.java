package com.zyy.activity;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.zyy.constant.FileBiz;
import com.zyy.constant.ProjectType;
import com.zyy.jdbc.DailyDao;
import com.zyy.jdbc.MyDataBase;
import com.zyy.po.Store;
import com.zyy.util.FileUtil;
import com.zyy.util.GetPlist;
import com.zyy.util.NetHelper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

public class StartActivity extends Activity {

	private static final int ERROR = 101;
	private static final int INFORMATION_DOWN_SUCCESS = 100;
	
	
	private static final String GET_ANDROID_ID_TO_SERVER = "http://www.softechallenger.com/admin/index/register"; //����get�ύANDROID�ֻ�ΨһID��������RUL
	private String mDeviceID;// ANDROID�ֻ���ID

	// Connectivity manager to determining, when the phone loses connection
	private ConnectivityManager mConnMan;

	// �ӱ���store.plist�ļ���ȡ��־��Ϣ
	List<Store> stores = new ArrayList<Store>();

	int[] progress_dot_array = { R.id.dot1, R.id.dot2, R.id.dot3, R.id.dot4,
			R.id.dot5, R.id.dot6 };

	private ImageView dotBtn1, dotBtn2, dotBtn3;
	int position1, position2, position3;
	boolean flag;
	MyHandler myHandler;
	MyThread myThread;
	FileUtil fileUtil = new FileUtil();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.start);
		
		/*DailyDao dao = new MyDataBase(this);
		dao.delete();
		dao.close();*/
		
		
		
		mDeviceID = Secure.getString(this.getContentResolver(), Secure.ANDROID_ID); //���ֻ�ID

		mConnMan = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

		Log.i("zyy", "�����Ƿ���ã�" + isNetworkAvailable());

		System.out.println("isWifiConnected===" + isWifiConnected());
		System.out.println("isGprsConnected===" + isGprsConnected());

		
		myHandler = new MyHandler(StartActivity.this);
		flag = true;
		myThread = new MyThread(myHandler);
		myThread.start();
		if (!fileUtil.isFileExist("SportsDaily/store.plist")) {// �ļ����в������ļ�����һ��ʹ�ø�Ӧ��
			if (!NetHelper.IsHaveInternet(getApplicationContext())) {// �ж������Ƿ����
				// �����õ�����
				Toast.makeText(StartActivity.this, "�����õ�����������������",
						Toast.LENGTH_LONG).show();
			}
			// ���õ�����
			// �������϶�ȡ�����ļ�����д���ļ�����
			new Thread(new MyTheadTheFirstUse()).start();
		} else {
			System.out.println("have this plist");
			myHandler.sendEmptyMessageDelayed(100, 5000);
		}

	}

	 
	/**
	 * Check if we are online
	 */
	private boolean isNetworkAvailable() {
		NetworkInfo info = mConnMan.getActiveNetworkInfo();
		if (info == null) {
			return false;
		}

		return info.isConnected();
	}

	/**
	 * �ж��Ƿ�������wifi
	 * 
	 * @return
	 */
	public boolean isWifiConnected() {
		return mConnMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
				.getState() == NetworkInfo.State.CONNECTED ? true : false;
	}

	/**
	 * �Ƿ�������gprs
	 * 
	 * @return
	 */
	public boolean isGprsConnected() {
		return mConnMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
				.getState() == NetworkInfo.State.CONNECTED ? true : false;
	}

	private class MyHandler extends Handler {

		Context context;
		Intent intent = new Intent();
			
		public MyHandler(Context context) {
			super();
			this.context = context;
		}
		@Override
		public void handleMessage(Message msg) {
			int position = msg.what;
			if (position == ERROR) {        //Ӧ�ð�װ�����쳣��ʱ��
				/*intent.putExtra("success_or_fail", 0);
				intent.setClass(StartActivity.this, TabActivity.class);
				StartActivity.this.startActivity(intent);
				StartActivity.this.finish();// �ݻٿ���ҳ��*/
				Toast.makeText(context, "��ǰ����������⣬���Ժ��ٰ�װ", 1).show();
				StartActivity.this.finish();   //�˳�Ӧ��
			} else if (position == 100) {
				flag = false;
				intent.setClass(StartActivity.this, TabActivity.class);
				StartActivity.this.startActivity(intent);
				StartActivity.this.finish();// �ݻٿ���ҳ��
				return;
			} else {
				position1 = position - 1;
				position2 = position;
				position3 = position + 1;
				if (position1 == -1) {
					position1 = 5;
				}
				if (position3 == 6) {
					position3 = 0;
				}
				dotBtn1 = (ImageView) findViewById(progress_dot_array[position1]);
				dotBtn2 = (ImageView) findViewById(progress_dot_array[position2]);
				dotBtn3 = (ImageView) findViewById(progress_dot_array[position3]);
				dotBtn1.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.initdot1));
				dotBtn2.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.initdot2));
				dotBtn3.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.initdot3));
			}
		}
	}

	private class MyThread extends Thread {

		Handler handler;

		int key;

		public MyThread(Handler handler) {
			super();
			this.handler = handler;
			key = 5;
		}

		@Override
		public void run() {
			while (flag) {
				if (key == 6) {
					key = 0;
				}
				handler.sendEmptyMessage(key);
				try {
					Thread.sleep(180);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				key++;
			}
		}

	}

	/**
	 * ��װӦ��ʱ���������߳�
	 * 1�����ڴӷ�����������store.plist�ļ�
	 * 2������store.plist�ļ�
	 * 3�����ݽ������ļ��е�ͼƬurl������������ͼƬ����д�뵽SD����
	 * @author SW
	 *
	 */
	private class MyTheadTheFirstUse implements Runnable {

		GetPlist getPlist = new GetPlist();
		FileBiz fileBiz = new FileBiz();
		boolean success = false;

		@Override
		public void run() {
			// TODO Auto-generated method stub
			boolean ret = getPlist.getPlist(ProjectType.plistUrl);//�����ϻ�ȡstore.plist�ļ�
			if (ret == true) {
				stores = fileBiz.getInformationFromPlist();//����store.plist�ļ�����һ��store���󼯺�
				if (!stores.isEmpty()) {
					success = fileBiz.writePicture2SDCard(stores);//���ݼ�����������url������������ͼƬ����д�뵽�ļ�����
				} else {
					System.out.println("����xml�ļ���������");
					myHandler.obtainMessage(ERROR).sendToTarget();//����store.plist�ļ���������
				}
			} else {
				System.out.println("plist���س�������");
				myHandler.obtainMessage(ERROR).sendToTarget(); //����store.plist�ļ��������� ��UI���߳�ͨ��
			}
			if (success == true) {     //�ж�ͼƬ�Ƿ����سɹ����ɹ�����true ��֮
				IDPostService iPostService = new IDPostService();
				if(iPostService.saveId(mDeviceID, GET_ANDROID_ID_TO_SERVER)){//���ֻ�ID���͵���������
					myHandler.obtainMessage(INFORMATION_DOWN_SUCCESS)
					.sendToTarget();
				}else {
					myHandler.obtainMessage(ERROR).sendToTarget();
				}
			}

		}

	}
	/**
	 * Ӧ�õ�һ�ΰ�װ��ʱ��
	 * ����װ�ɹ���ʱ���û��ֻ�ΨһIDͨ��GET��ʽ������̨��������������
	 * @author SW
	 *
	 */
	private class IDPostService {
		public boolean saveId(String id,String path){
			
			try {
				return this.sendGETRequest(path, id);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		}
		public boolean sendGETRequest(String path,String id) throws MalformedURLException, IOException{
			StringBuffer url = new StringBuffer(path);
			url.append("?");
			url.append("dozen=").append(id);
			HttpURLConnection conn = (HttpURLConnection) new URL(url.toString()).openConnection();
			conn.setConnectTimeout(5000);
			conn.setRequestMethod("GET");
			if(conn.getResponseCode() == 200 ){
				conn.disconnect();
				return true;
			}
			conn.disconnect();
			return false;
		}
	}
	
	

}
