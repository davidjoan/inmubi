package pe.inmubi.android;


import pe.inmubi.android.models.Anuncio;
import pe.inmubi.android.provider.InmubiDatabase;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.androidquery.AQuery;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class ShowAdvertActivity extends SherlockActivity {
	Anuncio anuncio;

	Button linkUrlButton;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_advert);
		// Show the Up button in the action bar.
		setupActionBar();
		
		linkUrlButton = (Button) findViewById(R.id.link_url);
		
		
		AQuery aq = new AQuery(this);
		
		 anuncio =  ((InmubiApplication) getApplication()).getAnuncio();
		
		// setTitle(anuncio.getTitulo());
		 String subtitle = anuncio.getTipoOperacion()+" "+anuncio.getTipoInmueble();
	     getSupportActionBar().setSubtitle(subtitle);
	     
	     aq.id(R.id.show_titulo).text(anuncio.getTitulo());
	     aq.id(R.id.show_precio).text(anuncio.getPrecio());
	     aq.id(R.id.show_photo).image(anuncio.getFoto());
	    
	    	 
	    	 String detalle = anuncio.getDetalle();
	    	 
	    	 if(detalle.contains("&nbsp;"))
	    	 {
	    		 detalle.replace("&nbsp;", " ");
	    		 detalle.replace("&Ntilde;", "Ñ");
	    	 }
			aq.id(R.id.show_detalle).text(detalle);

	    // aq.id(R.id.show_departamento).text(anuncio.getDepartmento());
	    // aq.id(R.id.show_provincia).text(anuncio.getProvincia());
	    // aq.id(R.id.show_distrito).text(anuncio.getDistrito());
	     aq.id(R.id.show_address).text(anuncio.getDireccion()+" - "+anuncio.getDistrito());
	     
	   //  Log.d("fecha_publicacion", anuncio.getPublicacion());
	     aq.id(R.id.show_publicacion).text(anuncio.getFechaPublicacion());
	     aq.id(R.id.show_origen).text(anuncio.getOrigen());
	     
	     aq.id(R.id.link_url).clicked(this, "linkUrl");  
	     
	     linkUrlButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.d("link",anuncio.getUrl());
				Intent webIntent = new Intent( Intent.ACTION_VIEW );
				webIntent.setAction(Intent.ACTION_VIEW);
				webIntent.addCategory(Intent.CATEGORY_BROWSABLE);
		        webIntent.setData( Uri.parse(anuncio.getUrl()) );
		        startActivity( webIntent );
			}
		});
			
	}
	/*
	private void linkUrl(View view)
	{
		
	}*/

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		String isLocal = ((InmubiApplication)getApplication()).getIsLocal();
		if(isLocal.equals("si"))
		{
			getSupportMenuInflater().inflate(R.menu.show_advert_local, menu);
		}
		else
		{
			getSupportMenuInflater().inflate(R.menu.show_advert, menu);	
		}
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		case R.id.action_favorite:
			InmubiDatabase db = new InmubiDatabase(getApplicationContext());
			
			Anuncio favoriteAnuncio = ((InmubiApplication) getApplication()).getAnuncio();
			
			
			
	        db.insertAusencia(favoriteAnuncio);
	        
	        
	        Toast.makeText(this, "Este Anuncio fue marcado como favorito!", Toast.LENGTH_SHORT).show();
			return true;
		case R.id.action_delete:
			InmubiDatabase dbdelete = new InmubiDatabase(getApplicationContext());
			
			Anuncio AnuncioToDelete = ((InmubiApplication) getApplication()).getAnuncio();
			
			
			
			dbdelete.deleteAnuncio(Integer.parseInt(AnuncioToDelete.getId()));
			
			NavUtils.navigateUpFromSameTask(this);
			
			
			Toast.makeText(this, "Se elimino este Anuncio!", Toast.LENGTH_SHORT).show();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
