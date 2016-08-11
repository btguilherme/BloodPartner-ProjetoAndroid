package br.com.bloodpartner;

import java.io.IOException;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpResponseException;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Entrar extends Activity {

	private EditText txtUsuario, txtSenha;
	private static String usuario;
	private String username, password;
	private String resultado;

	private final String NAMESPACE = "http://bd.com.br/";
	private final String METHOD_NAME = "login";
	private final String SOAP_ACTION = "http://bd.com.br/login";
	private final String URL = "http://guilherme-pr0gramaca0m0vel.rhcloud.com/WebService/DataBase?wsdl";

	public static String getUsuario() {
		return usuario;
	}
	
	public static void setUsuario(String username){
		usuario = username;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_entrar);

	}

	public void entrar(View v) {

		txtUsuario = (EditText) findViewById(R.id.txtUsuario);
		txtSenha = (EditText) findViewById(R.id.txtSenha);

		username = txtUsuario.getText().toString();
		password = txtSenha.getText().toString();
		
		setUsuario(username);

		Thread threadWS = new Thread() {

			@Override
			public void run() {

				SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

				PropertyInfo pUsuario = new PropertyInfo();
				pUsuario.setName("username");
				pUsuario.setValue(username);
				pUsuario.setType(String.class);
				request.addProperty(pUsuario);

				PropertyInfo pSenha = new PropertyInfo();
				pSenha.setName("password");
				pSenha.setValue(password);
				pSenha.setType(String.class);
				request.addProperty(pSenha);

				SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
						SoapEnvelope.VER11);
				envelope.setOutputSoapObject(request);

				HttpTransportSE ht = new HttpTransportSE(URL);

				try {
					ht.call(SOAP_ACTION, envelope);
					SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
					resultado = response.toString();
					
					if (resultado.equals("Logado")) {
						Intent i = new Intent(Entrar.this, Logado1.class);
						startActivity(i);

					} else {
						
						Entrar.this.runOnUiThread(new Runnable() {
							  public void run() {
								  Toast.makeText(getApplicationContext(),resultado+"\nUsuário inexistente ou senha digitada errada" ,
											Toast.LENGTH_SHORT).show();
								  finish();
							  }
							});
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

	public void cancelar(View v) {
		finish();
	}
}
