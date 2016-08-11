package br.com.bloodpartner;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Activity;
import android.content.Context;
import android.location.*;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class BuscaManual extends Activity implements LocationListener {

	private LatLng frameworkSystemLocation = new LatLng(0,0);
	private LocationManager posicao;
	private GoogleMap map;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_busca_manual);
		
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		posicao = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		posicao.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 10, this);
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		frameworkSystemLocation = new LatLng(location.getLatitude(), location.getLongitude());
		Marker frameworkSystem = map.addMarker(new MarkerOptions().position(frameworkSystemLocation).title("Você está aqui!").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher)));
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}
}
