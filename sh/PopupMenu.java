package com.nmi.__BContapk__;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;

import android.view.MotionEvent;
import android.view.View;
import android.view.KeyEvent;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView; 
import android.widget.AdapterView.OnItemClickListener;

public class PopupMenu extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu);
		
		ListView listview = (ListView)findViewById(R.id.lv);
		
		ArrayList<String> data = new ArrayList<String>();
        data.add("关于");
        data.add("退出");
		
		ArrayAdapter<String> aadr = new ArrayAdapter<String>(this, R.layout.text, data);
		
		listview.setAdapter(aadr);
		
		listview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> av, View v, int position, long id) {
				int resultCode = 999;
				switch ((int)id) {
					case 0:		resultCode = -2; break;
					case 1:		resultCode = 0; break;
					default:	resultCode = 999; break;
				}
				setResult2Parent(resultCode);
			}
		});
	}
	
	private void setResult2Parent(int resultCode) {
		PopupMenu.this.setResult(resultCode, this.getIntent());
		finish();	
	}
	
	private boolean defaultHandler() {
		setResult2Parent(999);
		return true;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return defaultHandler();
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return defaultHandler();
	}
}
