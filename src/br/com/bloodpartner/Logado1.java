package br.com.bloodpartner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

public class Logado1 extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_logado1);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.items, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		super.onOptionsItemSelected(item);

		switch (item.getItemId()) {
		case R.id.Sair:
			Intent i = new Intent(Logado1.this, MainActivity.class);
			startActivity(i);
			break;

		case R.id.Alterar:
			Intent intent = new Intent(Logado1.this, Inscrever.class);
			intent.putExtra("alterar", true);
			startActivity(intent);
			break;
			
		}
		return true;

	}
	
	public void historico(View v){
		Intent i = new Intent(Logado1.this, Historico.class);
		startActivity(i);
	}
	
	public void buscarDoadores(View v){
		Intent i = new Intent(Logado1.this, BuscarDoadores.class);
		startActivity(i);
	}
	
	public void voltar(View v){
		finish();
	}
}
