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

import org.json.JSONArray;
import org.json.JSONException;

import pe.inmubi.android.models.Ubigeo;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.actionbarsherlock.widget.ShareActionProvider;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.content.Context;
import android.content.Intent;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

public class SearchActivity extends SherlockActivity implements OnItemClickListener{
	
	private static final String LOG_TAG = "SearchActivity";
    
	private static final String PLACES_API_BASE = "http://54.207.2.253/api/Ubigeo?query=";
	
	Spinner spinnerSearchType;
	Spinner spinnerPlace;
	
	Button searchbutton;
	AutoCompleteTextView autoCompView;
	
	private ShareActionProvider mShareActionProvider;
	
	private static final String SHARED_FILE_NAME = "shared.png";

	
//	ProgressBar barProgress;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		
		
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		setProgressBarIndeterminateVisibility(Boolean.FALSE); 
		
		 
		//barProgress = (ProgressBar) findViewById(R.id.search_progress_bar);
		
		spinnerSearchType = (Spinner) findViewById(R.id.search_type);
		spinnerPlace = (Spinner) findViewById(R.id.search_place);
		
		searchbutton = (Button) findViewById(R.id.search_button);
		
		((InmubiApplication)getApplication()).setTipoInmueble("Departamentos");
		((InmubiApplication)getApplication()).setTipoOperacion("Alquiler");
		
		autoCompView = (AutoCompleteTextView) findViewById(R.id.search_autocompleter_ubigeo);
		//((AutoCompleteLoadding)autoCompView).setLoadingIndicator(barProgress);
	    autoCompView.setAdapter(new UbigeoAutoCompleteAdapter(this, R.layout.item_ubigeo_autocompleter));
	    
	    
	    autoCompView.setOnItemClickListener(this);
	    
	    
	    autoCompView.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				searchbutton.setEnabled(false);
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});

		
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
		        R.array.search_item_array, android.R.layout.simple_spinner_item);
		
		ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
		        R.array.place_array, android.R.layout.simple_spinner_item);
		
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		
		spinnerSearchType.setEnabled(true);
		spinnerSearchType.setClickable(true);
		spinnerSearchType.setOnItemSelectedListener(new SearchTypeListener());
		
		
		spinnerPlace.setEnabled(true);
		spinnerPlace.setClickable(true);
		spinnerPlace.setOnItemSelectedListener(new SearchPlaceListener());
		
		spinnerSearchType.setAdapter(adapter);
		spinnerPlace.setAdapter(adapter2);
		
		searchbutton.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View view) {
		    	
		    	
		    	

		    	startActivity(new Intent(SearchActivity.this,AdvertActivity.class));
		    	Log.d("click", "click");
		    }
		});
		
	 
		
		
	//			barProgress.setVisibility(View.GONE);
		copyPrivateRawResourceToPubliclyAccessibleFile();
		
		
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
	
	
	
	
	private ArrayList<Ubigeo> autocomplete(String input) {
		
		
		 
	   // ArrayList<String> resultList = null;
	    ArrayList<Ubigeo> resultList = null;
	    
	    HttpURLConnection conn = null;
	    StringBuilder jsonResults = new StringBuilder();
	    
	    
	    ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo networkInfo                 = connectivityManager.getActiveNetworkInfo();
	    
	    Log.d(LOG_TAG, "autocompleter");

	    if (networkInfo != null && networkInfo.isConnected() && networkInfo.isAvailable())
	    {
	    	
	    	Log.d(LOG_TAG, "is conected");
	    	 try {
	 	        StringBuilder sb = new StringBuilder(PLACES_API_BASE);
	 	        sb.append(URLEncoder.encode(input, "utf8"));
	 	        
	 	        URL url = new URL(sb.toString());
	 	        conn = (HttpURLConnection) url.openConnection();
	 	        InputStreamReader in = new InputStreamReader(conn.getInputStream());
	 	        
	 	        // Load the results into a StringBuilder
	 	        int read;
	 	        char[] buff = new char[1024];
	 	        while ((read = in.read(buff)) != -1) {
	 	            jsonResults.append(buff, 0, read);
	 	        }
	 	    } catch (MalformedURLException e) {
	 	        Log.e(LOG_TAG, "Error processing Places API URL", e);
	 	        return resultList;
	 	    } catch (IOException e) {
	 	        Log.e(LOG_TAG, "Error connecting to Places API", e);
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
	 	        resultList = new ArrayList<Ubigeo>(predsJsonArray.length());
	 	        for (int i = 0; i < predsJsonArray.length(); i++) {
	 	        	if(i < 15)
	 	        	{
	 	        		
	 	        		Ubigeo temp = new Ubigeo();
	 	        		temp.setCode(predsJsonArray.getJSONObject(i).getString("CODUBIGEO"));
	 	        		
	 	        		temp.setDescription(predsJsonArray.getJSONObject(i).getString("DESCRIPCION"));
	 	        		temp.setId(predsJsonArray.getJSONObject(i).getString("id"));
	 	        		
	 	        		resultList.add(temp);
	 	        		
	 	        		//resultList.add(predsJsonArray.getJSONObject(i).getString("DESCRIPCION"));
	 	        	}
	 	            
	 	        }
	 	    } catch (JSONException e) {
	 	        Log.e(LOG_TAG, "Cannot process JSON results", e);
	 	    }
	    }
	    else
	    {
	    	
	    	Log.d(LOG_TAG, "not works");
	        // PROMPT USER THAT NETWORK IS DISCONNECT

	            Toast.makeText(this, "This is no active network connection!", 5000).show();
	    }
	    
	  
		 

	    
	    return resultList;
	}
	

	public class UbigeoAutoCompleteAdapter extends ArrayAdapter<Ubigeo> implements Filterable {
	    private ArrayList<Ubigeo> resultList;

	    
	    public UbigeoAutoCompleteAdapter(Context context, int textViewResourceId) {
	        super(context, textViewResourceId);
	    }
	    
	    @Override
	    public int getCount() {
	        return resultList.size();
	    }

	    @Override
	    public Ubigeo getItem(int index) {
	        return resultList.get(index);
	    }

	    @Override
	    public Filter getFilter() {
	        Filter filter = new Filter() {
	            @Override
	            protected FilterResults performFiltering(CharSequence constraint) {
	            	
	 
	                FilterResults filterResults = new FilterResults();
	                if (constraint != null) {
	                    // Retrieve the autocomplete results.
	                    resultList = autocomplete(constraint.toString());
	                    
	                    ArrayList<String> resultListText = new ArrayList<String>(resultList.size());
	                    
	                    for (int i = 0; i < resultList.size(); i++) {
	                    	resultListText.add(resultList.get(i).getDescription());
							
						}
	                   
	                    
	                    // Assign the data to the FilterResults
	                    filterResults.values = resultListText;
	                    filterResults.count = resultListText.size();
	                    
	                    
	                }
	                
	                return filterResults;
	            }

	            @Override
	            protected void publishResults(CharSequence constraint, FilterResults results) {
	                if (results != null && results.count > 0) {
	                    notifyDataSetChanged();
	                }
	                else {
	                    notifyDataSetInvalidated();
	                }
	            }};
	            
	         
	        return filter;
	    }
	}


	@Override
	  public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        String str = ((Ubigeo) adapterView.getItemAtPosition(position)).getCode();
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
        
        ((InmubiApplication)getApplication()).setUbigeo((Ubigeo) adapterView.getItemAtPosition(position));
        searchbutton.setEnabled(true);
    }
	
	public class SearchTypeListener implements OnItemSelectedListener{

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			
			switch (arg2) {
			case 0:
				((InmubiApplication)getApplication()).setTipoOperacion("Departamentos");
				break;
			case 1:
				((InmubiApplication)getApplication()).setTipoOperacion("Casas");
				break;
			}
			
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
		}
		
	}
	public class SearchPlaceListener implements OnItemSelectedListener{
		
		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			
			switch (arg2) {
			case 0:
				((InmubiApplication)getApplication()).setTipoOperacion("Alquiler");
				break;
			case 1:
				((InmubiApplication)getApplication()).setTipoOperacion("Venta");
				break;
			}

		}
		
		@Override
		public void onNothingSelected(AdapterView<?> arg0) {

		}
		
	}
	
	
	public class AutoCompleteLoadding extends AutoCompleteTextView {


		public AutoCompleteLoadding(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		}

		private ProgressBar mLoadingIndicator;

	    public void setLoadingIndicator(ProgressBar view) {
	        mLoadingIndicator = view;
	    }

	    @Override
	    protected void performFiltering(CharSequence text, int keyCode) {
	        // the AutoCompleteTextview is about to start the filtering so show
	        // the ProgressPager
	        mLoadingIndicator.setVisibility(View.VISIBLE);
	        super.performFiltering(text, keyCode);
	    }

	    @Override
	    public void onFilterComplete(int count) {
	        // the AutoCompleteTextView has done its job and it's about to show
	        // the drop down so close/hide the ProgreeBar
	        mLoadingIndicator.setVisibility(View.INVISIBLE);
	        super.onFilterComplete(count);
	    }

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

}
