package pe.inmubi.android;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;

import pe.inmubi.android.adapters.AnuncioListViewAdapter;
import pe.inmubi.android.models.Anuncio;
import pe.inmubi.android.models.Busqueda;
import pe.inmubi.android.models.Ubigeo;
import pe.inmubi.android.provider.InmubiDatabase;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.ShareActionProvider;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class AdvertActivity extends SherlockFragmentActivity implements
		ActionBar.TabListener {

	SectionsPagerAdapter mSectionsPagerAdapter;
	
	ViewPager mViewPager;
	
	private static final String SHARED_FILE_NAME = "shared.png";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_advert);

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		// Show the Up button in the action bar.
		actionBar.setDisplayHomeAsUpEnabled(true);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
		
		copyPrivateRawResourceToPubliclyAccessibleFile();
	}
	
    /**
     * Copies a private raw resource content to a publicly readable
     * file such that the latter can be shared with other applications.
     */
    private void copyPrivateRawResourceToPubliclyAccessibleFile() {
        InputStream inputStream = null;
        FileOutputStream outputStream = null;
        try {
            inputStream = getResources().openRawResource(R.drawable.ic_launcher);
            outputStream = openFileOutput(SHARED_FILE_NAME,
                    Context.MODE_WORLD_READABLE | Context.MODE_APPEND);
            byte[] buffer = new byte[1024];
            int length = 0;
            try {
                while ((length = inputStream.read(buffer)) > 0){
                    outputStream.write(buffer, 0, length);
                }
            } catch (IOException ioe) {
                /* ignore */
            }
        } catch (FileNotFoundException fnfe) {
            /* ignore */
        } finally {
            try {
                inputStream.close();
            } catch (IOException ioe) {
               /* ignore */
            }
            try {
                outputStream.close();
            } catch (IOException ioe) {
               /* ignore */
            }
        }
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.search, menu);

	    MenuItem item = menu.findItem(R.id.search_menu_item_share);
	    ShareActionProvider provider = (ShareActionProvider) item.getActionProvider();
        provider.setShareHistoryFileName(ShareActionProvider.DEFAULT_SHARE_HISTORY_FILE_NAME);
// Note that you can set/change the intent any time,
// say when the user has selected an image.
provider.setShareIntent(createShareIntent());

return true;

	}
	
	private Intent createShareIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        Uri uri = Uri.fromFile(getFileStreamPath("shared.png"));
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        //return shareIntent;
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Inmubi es facil, gratis y util");
       // shareIntent.setType("text/plain");
       
        return shareIntent;
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			Fragment fragment = new DummySectionFragment();
			Bundle args = new Bundle();
			switch (position) {
			case 0:
				
				fragment = new ResultFragment();
				

			//	Bundle args = new Bundle();
			//	args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
			//	fragment.setArguments(args);
				
				break;
			case 1:fragment = new FavoriteFragment();
				
				break;
			}

			return fragment;
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			}
			return null;
		}
	}

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class DummySectionFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";

		public DummySectionFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_advert_dummy,
					container, false);
			TextView dummyTextView = (TextView) rootView
					.findViewById(R.id.section_label);
			dummyTextView.setText(Integer.toString(getArguments().getInt(
					ARG_SECTION_NUMBER)));
			return rootView;
		}
	}
	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class ResultFragment extends SherlockListFragment implements LoaderManager.LoaderCallbacks<List<Anuncio>>, ActionBar.OnNavigationListener{

		
		AnuncioListViewAdapter mAdapter;
    	

    	Integer page = 1;

    	// If non-null, this is the current filter the user has provided.
    	
    	String mCurFilter;

		public static final String ARG_SECTION_NUMBER = "section_number";
		
		
   		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			
			super.onActivityCreated(savedInstanceState);

			// Give some text to display if there is no data. In a real
			// application this would come from a resource.
			setEmptyText("Sin Anuncios");

			// We have a menu item to show in action bar.
			setHasOptionsMenu(true);
		
		

			// Create an empty adapter we will use to display the loaded data.
			mAdapter = new AnuncioListViewAdapter(getActivity());
			
			
			setListAdapter(mAdapter);

			// Start out with a progress indicator.
			setListShown(false);
			
			getLoaderManager().initLoader(0, null, this);

		}
   		
        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            
        	// Insert desired behavior here.
        	
        	Log.i("LoaderCustom", "Item clicked: " + id);
               
            Anuncio kardexSelected = mAdapter.getItem((int) id);
            
            ((InmubiApplication) getActivity().getApplication()).setAnuncio(kardexSelected);
            
			((InmubiApplication) getActivity().getApplication()).setIsLocal("no");

          
            Intent intent = new Intent();
            
            intent.setClass(getActivity(), ShowAdvertActivity.class);
        
            startActivity(intent);
        }
		
	


		@Override
		public boolean onNavigationItemSelected(int itemPosition, long itemId) {
			return false;
		}



		@Override
		public Loader<List<Anuncio>> onCreateLoader(int arg0, Bundle arg1) {
			return new AnuncioListLoader(getActivity(),mCurFilter);
		}



		@Override
		public void onLoadFinished(Loader<List<Anuncio>> arg0,
				List<Anuncio> datos) {
			mAdapter.setData(datos);

			// The list should now be shown.
			if (this.isResumed()) {
				setListShown(true);
			} else {
				setListShownNoAnimation(true);
			}
			
		}



		@Override
		public void onLoaderReset(Loader<List<Anuncio>> arg0) {
			mAdapter.setData(null);
			
		}
		
		
		
		/**
	     * A custom Loader that loads all of the installed applications.
	     */
	    public static class AnuncioListLoader extends AsyncTaskLoader<List<Anuncio>> {

	    	List<Anuncio> mservices;
	    	private String filter;
	    	private Integer page;
	    	
	    	
	    	public AnuncioListLoader(Context context, String textFilter) {
	    		super(context);
	    		filter = textFilter;
	    	}

	    	/**
	    	 * This is where the bulk of our work is done. This function is called
	    	 * in a background thread and should generate a new set of data to be
	    	 * published by the loader.
	    	 */
	    	@Override
	    	public List<Anuncio> loadInBackground() {
	    		
	    		String ubigeoCode = ((InmubiApplication)super.getContext().getApplicationContext()).getUbigeo().getCode();
	    		String tipoInmueble = ((InmubiApplication)super.getContext().getApplicationContext()).getTipoInmueble();
	    		String tipoOperacion = ((InmubiApplication)super.getContext().getApplicationContext()).getTipoOperacion();
	    		
	    		String link = "http://54.207.2.253/api/Anuncios?CodUbigeo="+ubigeoCode+"&TipoOperacion="+tipoOperacion+"&TipoInmueble="+tipoInmueble+"&Pagina=1&Cantidad=30";
	    		
	    		Log.d("link", link);
	    		List<Anuncio> resultList = null;
	    		// ArrayList<String> resultList = null;
	    	//	ArrayList<Ubigeo> resultList = null;

	    		HttpURLConnection conn = null;
	    		StringBuilder jsonResults = new StringBuilder();

	    			Log.d("background anuncios", "is conected");
	    			try {
	    				StringBuilder sb = new StringBuilder(link);

	    				URL url = new URL(sb.toString());
	    				conn = (HttpURLConnection) url.openConnection();
	    				InputStreamReader in = new InputStreamReader(
	    						conn.getInputStream());

	    				// Load the results into a StringBuilder
	    				int read;
	    				char[] buff = new char[1024];
	    				while ((read = in.read(buff)) != -1) {
	    					jsonResults.append(buff, 0, read);
	    				}
	    			} catch (MalformedURLException e) {
	    				Log.e("background anuncios", "Error processing Places API URL", e);
	    				return resultList;
	    			} catch (IOException e) {
	    				Log.e("background anuncios", "Error connecting to Places API", e);
	    				return resultList;
	    			} finally {
	    				if (conn != null) {
	    					conn.disconnect();
	    				}
	    			}

	    			try {
	    				// Create a JSON object hierarchy from the results
	    				// JSONObject jsonObj = new JSONObject(jsonResults.toString());
	    				// JSONArray predsJsonArray = jsonObj.getJSONArray();

	    				JSONArray predsJsonArray = new JSONArray(jsonResults.toString());

	    				// Extract the Place descriptions from the results
	    				resultList = new ArrayList<Anuncio>(predsJsonArray.length());
	    				for (int i = 0; i < predsJsonArray.length(); i++) {
	    				

	    						Anuncio temp = new Anuncio();
	    						
	    						temp.setTitulo(predsJsonArray.getJSONObject(i).getString("Titulo"));
	    						temp.setDireccion(predsJsonArray.getJSONObject(i).getString("Direccion"));
	    						temp.setPrecio(predsJsonArray.getJSONObject(i).getString("Precio"));
	    						temp.setFoto(predsJsonArray.getJSONObject(i).getString("UrlFoto"));
	    						temp.setId(predsJsonArray.getJSONObject(i).getString("id"));
	    						
	    						temp.setCodigoUbigeo(predsJsonArray.getJSONObject(i).getString("CodUbigeo"));
	    						temp.setFechaCreacion(predsJsonArray.getJSONObject(i).getString("Creado"));
	    					    temp.setDepartmento(predsJsonArray.getJSONObject(i).getString("Departamento"));
	    						temp.setDetalle(predsJsonArray.getJSONObject(i).getString("Detalle"));
	    						temp.setDistrito(predsJsonArray.getJSONObject(i).getString("Distrito"));
	    						temp.setFechaPublicacion(predsJsonArray.getJSONObject(i).getString("FecPublicacion"));
	    						temp.setOrigen(predsJsonArray.getJSONObject(i).getString("Origen"));
	    						temp.setProvincia(predsJsonArray.getJSONObject(i).getString("Provincia"));
	    						temp.setPublicacion(predsJsonArray.getJSONObject(i).getString("Publicacion"));
	    						temp.setTipoInmueble(predsJsonArray.getJSONObject(i).getString("TipoInmueble"));
	    						temp.setTipoOperacion(predsJsonArray.getJSONObject(i).getString("TipoOperacion"));
	    						temp.setUrl(predsJsonArray.getJSONObject(i).getString("Url"));


	    						resultList.add(temp);

	    				}
	    			} catch (JSONException e) {
	    				Log.e("background anuncios", "Cannot process JSON results", e);
	    			}
	    			
	    		return resultList;
	    	}

	    	/**
	    	 * Called when there is new data to deliver to the client. The super
	    	 * class will take care of delivering it; the implementation here just
	    	 * adds a little more logic.
	    	 */
	    	@Override
	    	public void deliverResult(List<Anuncio> kardexs) {
	    		if (isReset()) {
	    			// An async query came in while the loader is stopped. We
	    			// don't need the result.
	    			if (kardexs != null) {
	    				onReleaseResources(kardexs);
	    			}
	    		}
	    		List<Anuncio> oldkardexs = kardexs;
	    		mservices = kardexs;

	    		if (isStarted()) {
	    			// If the Loader is currently started, we can immediately
	    			// deliver its results.
	    			super.deliverResult(kardexs);
	    		}

	    		// At this point we can release the resources associated with
	    		// 'oldApps' if needed; now that the new result is delivered we
	    		// know that it is no longer in use.
	    		if (oldkardexs != null) {
	    			onReleaseResources(oldkardexs);
	    		}
	    	}

	    	/**
	    	 * Handles a request to start the Loader.
	    	 */
	    	@Override
	    	protected void onStartLoading() {
	    		if (mservices != null) {
	    			// If we currently have a result available, deliver it
	    			// immediately.
	    			deliverResult(mservices);
	    		}


	    		if (takeContentChanged() || mservices == null ) {
	    			// If the data has changed since the last time it was loaded
	    			// or is not currently available, start a load.
	    			forceLoad();
	    		}
	    	}

	    	/**
	    	 * Handles a request to stop the Loader.
	    	 */
	    	@Override
	    	protected void onStopLoading() {
	    		// Attempt to cancel the current load task if possible.
	    		cancelLoad();
	    	}

	    	/**
	    	 * Handles a request to cancel a load.
	    	 */
	    	@Override
	    	public void onCanceled(List<Anuncio> kardexs) {
	    		super.onCanceled(kardexs);

	    		// At this point we can release the resources associated with 'apps'
	    		// if needed.
	    		onReleaseResources(kardexs);
	    	}

	    	/**
	    	 * Handles a request to completely reset the Loader.
	    	 */
	    	@Override
	    	protected void onReset() {
	    		super.onReset();

	    		// Ensure the loader is stopped
	    		onStopLoading();

	    		// At this point we can release the resources associated with 'apps'
	    		// if needed.
	    		if (mservices != null) {
	    			onReleaseResources(mservices);
	    			mservices = null;
	    		}

	    	}

	    	/**
	    	 * Helper function to take care of releasing resources associated with
	    	 * an actively loaded data set.
	    	 */
	    	protected void onReleaseResources(List<Anuncio> services) {
	    		// For a simple List<> there is nothing to do. For something
	    		// like a Cursor, we would close it here.
	    	}
	    }
	}
	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class FavoriteFragment extends SherlockListFragment implements ActionBar.OnNavigationListener{
		
		
		AnuncioListViewAdapter mAdapter;
		
		
		Integer page = 1;
		
		// If non-null, this is the current filter the user has provided.
		
		String mCurFilter;
		
		public static final String ARG_SECTION_NUMBER = "section_number";
		
		
		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			
			super.onActivityCreated(savedInstanceState);
			
			// Give some text to display if there is no data. In a real
			// application this would come from a resource.
			setEmptyText("Sin Favoritos");
			
			// We have a menu item to show in action bar.
			setHasOptionsMenu(true);
			
			
			
			// Create an empty adapter we will use to display the loaded data.
			mAdapter = new AnuncioListViewAdapter(getActivity());
			
			InmubiDatabase db = new InmubiDatabase(getActivity().getApplication());
			//get
			List<Anuncio> datos = db.getAllAnuncios();
			
			mAdapter.setData(datos);
			
			setListAdapter(mAdapter);
			
			// Start out with a progress indicator.
			setListShown(true);
			
			
			
		}
		
		@Override
		public void onListItemClick(ListView l, View v, int position, long id) {
			
			// Insert desired behavior here.
			
			Log.i("LoaderCustom", "Item clicked: " + id);
			
			Anuncio kardexSelected = mAdapter.getItem((int) id);
			
			((InmubiApplication) getActivity().getApplication()).setAnuncio(kardexSelected);
			
			((InmubiApplication) getActivity().getApplication()).setIsLocal("si");
			
			
			Intent intent = new Intent();
			
			intent.setClass(getActivity(), ShowAdvertActivity.class);
			
			startActivity(intent);
		}
		
		
		
		
		@Override
		public boolean onNavigationItemSelected(int itemPosition, long itemId) {
			return false;
		}
		
	}

}
