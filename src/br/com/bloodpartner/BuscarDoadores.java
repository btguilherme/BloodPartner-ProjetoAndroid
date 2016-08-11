package br.com.bloodpartner;

import java.io.IOException;
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
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class BuscarDoadores extends Activity {

	private ListView listViewResultado;
	
	private final String NAMESPACE = "http://bd.com.br/";
	private String METHOD_NAME = null;
	private String SOAP_ACTION = null;
	private final String URL = "http://guilherme-pr0gramaca0m0vel.rhcloud.com/WebService/DataBase?wsdl";
	
	
	private String user_blood_type = null, user_username = null, user_email = null, user_city = null;
	private ArrayList<String> listaUsuarios = new ArrayList<String>();
	private ArrayList<String> friends_user_name = new ArrayList<String>();
	private ArrayList<String> friends_full_name = new ArrayList<String>();
	private final int TIMEOUT = 1000;
	private int index = 0;
	private String r = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_buscar_doadores);

		mapeia();
		getDadosUsuarioAtual();
	}
	
	private void mapeia(){
		listViewResultado = (ListView) findViewById(R.id.listViewResultado);
	}
	
	public void voltar(View v){
		finish();
	}
	
	private void getDadosUsuarioAtual(){
		SOAP_ACTION = "http://bd.com.br/dados";
		METHOD_NAME = "dados";

		Thread threadDadosUsuarios = new Thread() {
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

					String[] dados = response.toString().split("#");
					
					 // 0. full_name 1. username 2. email 3. blood_type 4. city
					 
					user_username = dados[1];
					user_email = dados[2];
					user_blood_type = dados[3];
					user_city = dados[4];

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
		threadDadosUsuarios.start();

	}
	
	
	public void buscaManual(View v){
		Intent i = new Intent(BuscarDoadores.this, BuscaManual.class);
		startActivity(i);
	}

	public void buscarProximos(View v) {
		
		SOAP_ACTION = "http://bd.com.br/buscaProximos";
		METHOD_NAME = "buscaProximos";

		Thread threadWS = new Thread() {
			@Override
			public void run() {

				SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

				PropertyInfo pUsuario = new PropertyInfo();
				pUsuario.setName("username");
				pUsuario.setValue(Entrar.getUsuario());
				pUsuario.setType(String.class);
				request.addProperty(pUsuario);
				
				PropertyInfo pSangue = new PropertyInfo();
				pSangue.setName("blood_type");
				pSangue.setValue(user_blood_type);
				pSangue.setType(String.class);
				request.addProperty(pSangue);
				
				PropertyInfo pCidade = new PropertyInfo();
				pCidade.setName("city");
				pCidade.setValue(user_city);
				pCidade.setType(String.class);
				request.addProperty(pCidade);

				SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
						SoapEnvelope.VER11);
				envelope.setOutputSoapObject(request);

				HttpTransportSE ht = new HttpTransportSE(URL);

				try {
					ht.call(SOAP_ACTION, envelope);
					SoapPrimitive response = (SoapPrimitive) envelope
							.getResponse();

					if(response.toString().equals("Não existe doadores próximos")){
						listaUsuarios.add("Não existe doadores próximos");
					} else{
						String[] usuarios = response.toString().split("\\$");
						
						for (int i = 0; i < usuarios.length; i++) {
							String[] dados = usuarios[i].split("\\#");
							String aux = "Usuário '"+dados[0]+"' ("+dados[1]+"), com tipo de sangue "+dados[2]+".\nE-mail: "+dados[3];
							friends_full_name.add(dados[0]);
							friends_user_name.add(dados[1]);
							listaUsuarios.add(aux);
						}
					}
									
					try {
		                synchronized (this) {
		                    wait(1000);

		                    runOnUiThread(new Runnable() {
		                        @Override
		                        public void run() {
		            				popularUsuarios(listaUsuarios);
		                        }
		                    });
		                }
		            } catch (InterruptedException e) {
		                e.printStackTrace();
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
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.items, menu);
		return true;
	}
	
	private void popularUsuarios(ArrayList<String> listaUsuarios){
		
		ArrayAdapter<String> adpt = 
				new ArrayAdapter<String>(BuscarDoadores.this, android.R.layout.simple_list_item_1, listaUsuarios);
		
		listViewResultado.setAdapter(adpt);		
		
		listViewResultado.setOnItemClickListener(new OnItemClickListener() {

			
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
				index = arg2;
				confirmacao();
			}
		});
	}
	
	private void confirmacao(){
		  AlertDialog.Builder dialog 
		   = new AlertDialog.Builder(BuscarDoadores.this);
		  dialog.setTitle("Confirme");
		  dialog.setMessage("Você realmente gostaria de pedir doação a este usuário?");
		  
		  dialog.setPositiveButton("Sim", new OnClickListener(){

		   @Override
		   public void onClick(DialogInterface dialog, int which) {
			   pedirDoacao();
			   Toast.makeText(BuscarDoadores.this, r, Toast.LENGTH_LONG).show();
		   }});
		  
		  dialog.setNegativeButton("Não", new OnClickListener(){

		   @Override
		   public void onClick(DialogInterface dialog, int which) {
			   dialog.cancel();
		   }});
		  dialog.show();
		 }
	
	public String pedirDoacao(){
		//addDoador
		SOAP_ACTION = "http://bd.com.br/addDoador";
		METHOD_NAME = "addDoador";
		
		Thread threadAddDoador = new Thread() {
			@Override
			public void run() {

				SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
				
				PropertyInfo pUsuario = new PropertyInfo();
				pUsuario.setName("username");
				pUsuario.setValue(Entrar.getUsuario());
				pUsuario.setType(String.class);
				request.addProperty(pUsuario);
				
				PropertyInfo pFriendFullName = new PropertyInfo();
				pFriendFullName.setName("friend_full_name");
				pFriendFullName.setValue(friends_full_name.get(index));
				pFriendFullName.setType(String.class);
				request.addProperty(pFriendFullName);
				
				PropertyInfo pFriendUsername = new PropertyInfo();
				pFriendUsername.setName("friend_username");
				pFriendUsername.setValue(friends_user_name.get(index));
				pFriendUsername.setType(String.class);
				request.addProperty(pFriendUsername);

				SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
						SoapEnvelope.VER11);
				envelope.setOutputSoapObject(request);

				HttpTransportSE ht = new HttpTransportSE(URL);

				try {
					ht.call(SOAP_ACTION, envelope);
					SoapPrimitive response = (SoapPrimitive) envelope
							.getResponse();

					r = response.toString();

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
		threadAddDoador.start();
		
		try {
			Thread.sleep(TIMEOUT);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return r;
	}

}
