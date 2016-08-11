package br.com.bloodpartner;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class Banco extends SQLiteOpenHelper {

	private static final String DB_NAME = "bloodpartner.bd";
	private static final int DB_VERSION = 1;

	private SQLiteDatabase readDB = getReadableDatabase();
	private SQLiteDatabase writeDB = getWritableDatabase();

	public Banco(Context context, CursorFactory factory,
			int version) {
		super(context, DB_NAME, null, DB_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase arg0) {

		String SQL = "CREATE TABLE cadastro(nomeCompleto TEXT, sexo TEXT, usuario TEXT PRIMARY KEY, senha TEXT, email TEXT, sangue TEXT, estado TEXT, cidade TEXT)";
		
		arg0.execSQL(SQL);

	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	public void execSQL(String SQL) {
		writeDB.execSQL(SQL);
	}

	public Cursor querySQL(String SQL) {
		return readDB.rawQuery(SQL, null);
	}

}
