package br.com.bloodpartner;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpResponseException;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class Historico extends Activity {

	private ListView doaram;
	private ListView doei;
	
	private final String NAMESPACE = "http://bd.com.br/";
	private String METHOD_NAME = null;
	private String SOAP_ACTION = null;
	private final String URL = "http://guilherme-pr0gramaca0m0vel.rhcloud.com/WebService/DataBase?wsdl";
	
	private final int TIMEOUT = 1000;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_historico);
		
		mapeia();
		popularDoaram();
		popularDoei();
	}
	
	public void mapeia(){
		doaram = (ListView) findViewById(R.id.listViewDoaram);
		doei = (ListView) findViewById(R.id.listViewDoei);
	}
	
	public void voltar (View v){
		finish();
	}
	
	public void popularDoaram(){
		ArrayList<String> listaDoaram = doaram();
		
		ArrayAdapter<String> adptDoaram = 
				new ArrayAdapter<String>(Historico.this, android.R.layout.simple_list_item_1, listaDoaram);
		
		doaram.setAdapter(adptDoaram);
				
		
		doaram.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				String nome = arg0.getItemAtPosition(arg2).toString();
				
				Toast.makeText(Historico.this, "Nome selecionado foi: " + nome, Toast.LENGTH_LONG).show();
			}
		});
		
		
	}
	
	public void popularDoei(){
		ArrayList<String> listaDoei = doei();
		
		ArrayAdapter<String> adptDoaram = 
				new ArrayAdapter<String>(Historico.this, android.R.layout.simple_list_item_1, listaDoei);
		
		doei.setAdapter(adptDoaram);
				
		doei.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
				
				
				
			}
		});
			
	}
	
	private ArrayList<String> doei(){
		//procurar f.friend_username
		
		SOAP_ACTION = "http://bd.com.br/listarDoeiPara";
		METHOD_NAME = "listarDoeiPara";
		
		final ArrayList<String> list = new ArrayList<String>();

		Thread threadWS = new Thread() {
			@Override
			public void run() {

				SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

				PropertyInfo pUsuario = new PropertyInfo();
				pUsuario.setName("friend_username");
				pUsuario.setValue(Entrar.getUsuario());
				pUsuario.setType(String.class);
				request.addProperty(pUsuario);

				SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
						SoapEnvelope.VER11);
				envelope.setOutputSoapObject(request);

				HttpTransportSE ht = new HttpTransportSE(URL);

				try {
					ht.call(SOAP_ACTION, envelope);
					SoapPrimitive response = (SoapPrimitive) envelope
							.getResponse();
					
					String[] usuarios = response.toString().split("\\#");
					
					for (int i = 0; i < usuarios.length; i++) {
						//0. username
						String aux = ""+usuarios[i]+"";
						list.add(aux);
					}
					
				} catch (HttpResponseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (XmlPullParserException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		};
		threadWS.start();
		
		try {
			Thread.sleep(TIMEOUT);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return list;
		
		
		
	}
	
	private ArrayList<String> doaram(){
		//procurar f.username
		
		SOAP_ACTION = "http://bd.com.br/listarDoaramPraMim";
		METHOD_NAME = "listarDoaramPraMim";
		
		final ArrayList<String> list = new ArrayList<String>();

		Thread threadWS = new Thread() {
			@Override
			public void run() {

				SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

				PropertyInfo pUsuario = new PropertyInfo();
				pUsuario.setName("username");
				pUsuario.setValue(Entrar.getUsuario());
				pUsuario.setType(String.class);
				request.addProperty(pUsuario);

				SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
						SoapEnvelope.VER11);
				envelope.setOutputSoapObject(request);

				HttpTransportSE ht = new HttpTransportSE(URL);

				try {
					ht.call(SOAP_ACTION, envelope);
					SoapPrimitive response = (SoapPrimitive) envelope
							.getResponse();
					
					String[] usuarios = response.toString().split("\\$");
					
					for (int i = 0; i < usuarios.length; i++) {
						//0.friend_full_name 1.friend_username
						String[] dados = usuarios[i].split("\\#");
						
						if(dados[0].equals("Ninguém doou sangue à você")){
							list.add("Ninguém doou sangue à você");
						} else {
							String aux = "'"+dados[0]+"' ("+dados[1]+")";
							list.add(aux);
						}
					}
					
				} catch (HttpResponseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (XmlPullParserException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		};
		threadWS.start();
		
		try {
			Thread.sleep(TIMEOUT);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return list;

	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.items, menu);
		return true;
	}
	
	
}
