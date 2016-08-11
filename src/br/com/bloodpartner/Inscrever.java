package br.com.bloodpartner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

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
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

public class Inscrever extends Activity {

	private EditText edtTxtNome, edtTxtUsuario, edtTxtSenha1, edtTxtSenha2,
			edtTxtEmail;
	private Spinner dropdownSangue, dropdownCidades, dropdownEstados;
	private RadioButton radioMasculino, radioFeminino;
	private RadioGroup radioGroup;
	private Button btnCadastrar;
	private String[] cidadesPR, cidadesSP, cidadesRJ;

	private final String NAMESPACE = "http://bd.com.br/";
	private final String URL = "http://guilherme-pr0gramaca0m0vel.rhcloud.com/WebService/DataBase?wsdl";
	private String SOAP_ACTION, METHOD_NAME;

	private final String reg = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
	private final String[] tiposSanguineos = { "A+", "A-", "B+", "B-", "AB+",
			"AB-", "O+", "O-" };
	private final String[] estados = { "SP", "RJ", "PR" };

	private final int SANGUE = 0;
	private final int ESTADO = 1;
	private final int CIDADE = 2;

	private final int SP = 0;
	private final int RJ = 1;
	private final int PR = 2;

	private final int TIMEOUT = 1000;

	private static String nomeCompleto;
	private static String usuario;
	private static String senha;
	private static String email;
	private static String sangue;
	private static String estado;
	private static String cidade;
	private static String resultado;
	private String sexo;

	private final int APOSITIVO = 0;
	private final int ANEGATIVO = 1;
	private final int BPOSITIVO = 2;
	private final int BNEGATIVO = 3;
	private final int ABPOSITIVO = 4;
	private final int ABNEGATIVO = 5;
	private final int OPOSITIVO = 6;
	private final int ONEGATIVO = 7;

	private int cidadeSeleciona;

	private void mapeamento() {
		edtTxtNome = (EditText) findViewById(R.id.edtTxtNome);
		edtTxtUsuario = (EditText) findViewById(R.id.edtTxtUsuario);
		edtTxtSenha1 = (EditText) findViewById(R.id.edtTxtSenha1);
		edtTxtSenha2 = (EditText) findViewById(R.id.edtTxtSenha2);
		edtTxtEmail = (EditText) findViewById(R.id.edtTxtEmail);
		dropdownSangue = (Spinner) findViewById(R.id.dropSangue);
		dropdownCidades = (Spinner) findViewById(R.id.dropCidades);
		dropdownEstados = (Spinner) findViewById(R.id.dropEstados);
		radioGroup = (RadioGroup) findViewById(R.id.rdGrSexo);
		radioMasculino = (RadioButton) findViewById(R.id.radioMasculino);
		radioFeminino = (RadioButton) findViewById(R.id.radioFeminino);
		btnCadastrar = (Button) findViewById(R.id.btnCadastrar);
	}

	private void setDados(String nome, String usuario, String senha1,
			String senha2, String email, String sangue, String cidade,
			String estado, String sexo) {

		if (sangue.equals("A+")) {
			dropdownSangue.setSelection(APOSITIVO);
		} else if (sangue.equals("A-")) {
			dropdownSangue.setSelection(ANEGATIVO);
		} else if (sangue.equals("B+")) {
			dropdownSangue.setSelection(BPOSITIVO);
		} else if (sangue.equals("B-")) {
			dropdownSangue.setSelection(BNEGATIVO);
		} else if (sangue.equals("AB+")) {
			dropdownSangue.setSelection(ABPOSITIVO);
		} else if (sangue.equals("AB-")) {
			dropdownSangue.setSelection(ABNEGATIVO);
		} else if (sangue.equals("O+")) {
			dropdownSangue.setSelection(OPOSITIVO);
		} else {
			dropdownSangue.setSelection(ONEGATIVO);
		}
		
		if(sexo.equals("Masculino")){
			radioMasculino.setChecked(true);
		}else{
			radioFeminino.setChecked(true);
		}

		if (estado.equals("SP")) {
			dropdownEstados.setSelection(SP);
			populaCidades(SP);

			for (int i = 0; i < cidadesSP.length; i++) {
				if (cidadesSP.equals(cidade)) {
					dropdownCidades.setSelection(i);
				}
			}

		} else if (estado.equals("PR")) {
			dropdownEstados.setSelection(PR);
			populaCidades(PR);

			for (int i = 0; i < cidadesPR.length; i++) {
				if (cidadesPR.equals(cidade)) {
					dropdownCidades.setSelection(i);
				}
			}

		} else {
			dropdownEstados.setSelection(RJ);
			populaCidades(RJ);

			for (int i = 0; i < cidadesRJ.length; i++) {
				if (cidadesRJ.equals(cidade)) {
					dropdownCidades.setSelection(i);
				}
			}
		}

		this.edtTxtNome.setText(nome);
		this.edtTxtUsuario.setText(usuario);
		this.edtTxtSenha1.setText(senha1);
		this.edtTxtSenha2.setText(senha2);
		this.edtTxtEmail.setText(email);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inscrever);

		mapeamento();

		boolean alterar = getIntent().getBooleanExtra("alterar", false);

		// se nao for pra alterar, � cadastro
		if (!alterar) {

			// cadastrar
			dropdownSangue = criaDropdown(SANGUE);
			dropdownEstados = criaDropdown(ESTADO);

			// metodo para preencher drop com as cidades dos respctivos estados
			capturaEstado();

		} else {

			// alterar
			dropdownSangue = criaDropdown(SANGUE);
			dropdownEstados = criaDropdown(ESTADO);
			edtTxtUsuario.setEnabled(false);
			capturaEstado();

			btnCadastrar.setText("ALTERAR");
			setDadosParaAlterar();
		}

	}

	private void setDadosParaAlterar() {
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

					/*
					 * "full_name"
					 * "username"
					 * "email"
					 * "blood_type"
					 * "city"
					 * "sex"
					 * "state"
					 */

					setDados(dados[0], dados[1], "", "", dados[2], dados[3], dados[4], dados[6], dados[5]);
					/*
					edtTxtNome.setText(dados[0]);
					edtTxtUsuario.setText(dados[1]);
					edtTxtEmail.setText(dados[2]);
					*/

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

		try {
			Thread.sleep(TIMEOUT);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	private void altera() {

		SOAP_ACTION = "http://bd.com.br/alterar";
		METHOD_NAME = "alterar";

		sangue = dropdownSangue.getSelectedItem().toString();
		estado = dropdownEstados.getSelectedItem().toString();
		cidade = dropdownCidades.getSelectedItem().toString();

		Thread threadWS = new Thread() {

			@Override
			public void run() {

				SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

				PropertyInfo pUsuario = new PropertyInfo();
				pUsuario.setName("username");
				pUsuario.setValue(Entrar.getUsuario());
				pUsuario.setType(String.class);
				request.addProperty(pUsuario);

				PropertyInfo pNomeCompleto = new PropertyInfo();
				pNomeCompleto.setName("full_name");
				pNomeCompleto.setValue(edtTxtNome.getText().toString());
				pNomeCompleto.setType(String.class);
				request.addProperty(pNomeCompleto);
				
				PropertyInfo pSexo = new PropertyInfo();
				pSexo.setName("sex");
				pSexo.setValue(sexo);
				pSexo.setType(String.class);
				request.addProperty(pSexo);

				PropertyInfo pSenha = new PropertyInfo();
				pSenha.setName("password");
				pSenha.setValue(edtTxtSenha1.getText().toString());
				pSenha.setType(String.class);
				request.addProperty(pSenha);

				PropertyInfo pEmail = new PropertyInfo();
				pEmail.setName("email");
				pEmail.setValue(edtTxtEmail.getText().toString());
				pEmail.setType(String.class);
				request.addProperty(pEmail);

				PropertyInfo pSangue = new PropertyInfo();
				pSangue.setName("blood_type");
				pSangue.setValue(sangue);
				pSangue.setType(String.class);
				request.addProperty(pSangue);

				PropertyInfo pEstado = new PropertyInfo();
				pEstado.setName("state");
				pEstado.setValue(estado);
				pEstado.setType(String.class);
				request.addProperty(pEstado);

				PropertyInfo pCidade = new PropertyInfo();
				pCidade.setName("city");
				pCidade.setValue(cidade);
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

					resultado = response.toString();

					runOnUiThread(new Runnable() {

						@Override
						public void run() { // TODO Auto-generated method stub
							Toast.makeText(Inscrever.this, resultado,
									Toast.LENGTH_LONG).show();
						}
					});

				} catch (HttpResponseException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (XmlPullParserException e) {
					e.printStackTrace();
				}
			}
		};
		threadWS.start();
		finish();

	}

	private void capturaEstado() {
		dropdownEstados
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {

						cidadeSeleciona = arg2;
						dropdownCidades = criaDropdown(CIDADE);

					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						// TODO Auto-generated method stub
					}
				});

	}

	private Spinner criaDropdown(int tipo) {

		ArrayAdapter<String> adapter = null;
		Spinner dropdown = null;

		switch (tipo) {
		case SANGUE:
			dropdown = (Spinner) findViewById(R.id.dropSangue);
			adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_dropdown_item_1line,
					tiposSanguineos);
			break;
		case ESTADO:
			dropdown = (Spinner) findViewById(R.id.dropEstados);
			adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_dropdown_item_1line, estados);
			break;
		case CIDADE:
			dropdown = (Spinner) findViewById(R.id.dropCidades);

			if (cidadeSeleciona == PR) {
				populaCidades(PR);
				adapter = new ArrayAdapter<String>(this,
						android.R.layout.simple_dropdown_item_1line, cidadesPR);
			}
			if (cidadeSeleciona == SP) {
				populaCidades(SP);
				adapter = new ArrayAdapter<String>(this,
						android.R.layout.simple_dropdown_item_1line, cidadesSP);
			}
			if (cidadeSeleciona == RJ) {
				populaCidades(RJ);
				adapter = new ArrayAdapter<String>(this,
						android.R.layout.simple_dropdown_item_1line, cidadesRJ);
			}

			break;

		}

		dropdown.setAdapter(adapter);

		return dropdown;

	}

	private void populaCidades(int sP2) {
		switch (sP2) {
		case SP:
			cidadesSP = new String[] { "Botucatu", "Bauru", "Assis", "Marília",
					"Jaú", "Sorocaba", "São Carlos", "Piracicaba", "Avaré",
					"Ourinhos", "Lins", "Campinas", "Tupã", "Ubatuba",
					"São José dos Campos", "Limeira", "São José do Rio Preto",
					"Lençóis Paulista" };
			break;
		case PR:
			cidadesPR = new String[] { "Londrina", "Cornélio Procópio",
					"Bandeirantes", "Maringá", "Santa Mariana", "Andirá",
					"Assaí", "Jataizinho", "Jacarezinho", "Sertanópolis",
					"Congonhinhas", "Uraí", "Cambé", "Arapongas", "Apucarana",
					"Mandaguari", "Tamarana", "Astorga", "Miraselva" };
			break;
		case RJ:
			cidadesRJ = new String[] { "Petrópoles", "Volta Redonda",
					"Angra dos Reis", "Aperibe", "Araruama", "Areal",
					"Armacao de Buzios", "Arraial do Cabo", "Barra Mansa",
					"Barra do Pirai", "Belford Roxo", "Bom Jardim",
					"Bom Jesus do Itabapoana", "Cabo Frio",
					"Cachoeiras de Macacu", "Cardoso Moreira" };
		default:
			break;
		}

	}

	public void cancelar(View v) {
		finish();
	}

	public void setSexo(View v){
		int selectedId = radioGroup.getCheckedRadioButtonId();
		RadioButton rb = (RadioButton) findViewById(selectedId);
		this.sexo = rb.getText().toString();
	}

	public void cadastrar(View v) {

		if (btnCadastrar.getText().equals("ALTERAR")) {
			altera();
		} else {

			METHOD_NAME = "cadastrar";
			SOAP_ACTION = "http://bd.com.br/cadastrar";

			edtTxtNome = (EditText) findViewById(R.id.edtTxtNome);
			edtTxtUsuario = (EditText) findViewById(R.id.edtTxtUsuario);
			edtTxtSenha1 = (EditText) findViewById(R.id.edtTxtSenha1);
			edtTxtSenha2 = (EditText) findViewById(R.id.edtTxtSenha2);
			edtTxtEmail = (EditText) findViewById(R.id.edtTxtEmail);
			radioMasculino = (RadioButton) findViewById(R.id.radioMasculino);
			radioFeminino = (RadioButton) findViewById(R.id.radioFeminino);

			if (edtTxtNome.getText().toString().length() == 0
					|| edtTxtUsuario.getText().toString().length() == 0
					|| edtTxtSenha1.getText().toString().length() == 0
					|| edtTxtSenha2.getText().toString().length() == 0
					|| edtTxtEmail.getText().toString().length() == 0
					|| !(radioFeminino.isChecked() || radioMasculino
							.isChecked())) {

				Toast.makeText(
						this,
						"Algum campo em branco. Por "
								+ "favor corrija para que o cadastro seja realizado "
								+ "com sucesso.", Toast.LENGTH_LONG).show();
			} else if (!edtTxtSenha1.getText().toString()
					.equals(edtTxtSenha2.getText().toString())) {
				Toast.makeText(
						this,
						"As senhas não conferem. Por "
								+ "favor digite-as novamente.",
						Toast.LENGTH_LONG).show();
			} else if (!edtTxtEmail.getText().toString().matches(reg)) {
				Toast.makeText(this, "E-mail não é válido", Toast.LENGTH_LONG)
						.show();
			} else {

				// cadastrar / alterar

				nomeCompleto = edtTxtNome.getText().toString();
				usuario = edtTxtUsuario.getText().toString();
				senha = edtTxtSenha1.getText().toString();
				email = edtTxtEmail.getText().toString();
				sangue = dropdownSangue.getSelectedItem().toString();
				estado = dropdownEstados.getSelectedItem().toString();
				cidade = dropdownCidades.getSelectedItem().toString();

				// Cadastro webservice

				Thread threadWS = new Thread() {

					@Override
					public void run() {

						SoapObject request = new SoapObject(NAMESPACE,
								METHOD_NAME);

						PropertyInfo pUsuario = new PropertyInfo();
						pUsuario.setName("username");
						pUsuario.setValue(usuario);
						pUsuario.setType(String.class);
						request.addProperty(pUsuario);

						PropertyInfo pNomeCompleto = new PropertyInfo();
						pNomeCompleto.setName("full_name");
						pNomeCompleto.setValue(nomeCompleto);
						pNomeCompleto.setType(String.class);
						request.addProperty(pNomeCompleto);

						PropertyInfo pSexo = new PropertyInfo();
						pSexo.setName("sex");
						pSexo.setValue(sexo);
						pSexo.setType(String.class);
						request.addProperty(pSexo);

						PropertyInfo pSenha = new PropertyInfo();
						pSenha.setName("password");
						pSenha.setValue(senha);
						pSenha.setType(String.class);
						request.addProperty(pSenha);

						PropertyInfo pEmail = new PropertyInfo();
						pEmail.setName("email");
						pEmail.setValue(email);
						pEmail.setType(String.class);
						request.addProperty(pEmail);

						PropertyInfo pSangue = new PropertyInfo();
						pSangue.setName("blood_type");
						pSangue.setValue(sangue);
						pSangue.setType(String.class);
						request.addProperty(pSangue);

						PropertyInfo pCidade = new PropertyInfo();
						pCidade.setName("city");
						pCidade.setValue(cidade);
						pCidade.setType(String.class);
						request.addProperty(pCidade);

						PropertyInfo pEstado = new PropertyInfo();
						pEstado.setName("state");
						pEstado.setValue(estado);
						pEstado.setType(String.class);
						request.addProperty(pEstado);

						SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
								SoapEnvelope.VER11);
						envelope.setOutputSoapObject(request);

						HttpTransportSE ht = new HttpTransportSE(URL);

						try {
							ht.call(SOAP_ACTION, envelope);
							SoapPrimitive response = (SoapPrimitive) envelope
									.getResponse();

							resultado = response.toString();

							runOnUiThread(new Runnable() {

								@Override
								public void run() {
									// TODO Auto-generated method stub
									Toast.makeText(Inscrever.this, resultado,
											Toast.LENGTH_LONG).show();
								}
							});

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
				finish();
			}

		}
	}
}
