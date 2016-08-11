package br.com.bloodpartner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {
	private Intent i;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);	
	}
	
	public void entrar(View v){
		i = new Intent(MainActivity.this, Entrar.class);
		startActivity(i);
	}
	public void increver(View v){
		i = new Intent(MainActivity.this, Inscrever.class);
		startActivity(i);
	}
	
}
