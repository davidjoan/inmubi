package pe.inmubi.android.adapters;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.List;

import com.androidquery.AQuery;

import pe.inmubi.android.R;
import pe.inmubi.android.models.Anuncio;
import pe.inmubi.android.models.Busqueda;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class BusquedaListViewAdapter extends ArrayAdapter<Busqueda> {

	Context context;
	private final LayoutInflater mInflater;
	
	
	public BusquedaListViewAdapter(Context context) {
		super(context, android.R.layout.simple_list_item_2);
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.context = context;
	}
	
	public void setData(List<Busqueda> datos) {
		this.clear();
		if (datos != null) {
			for (Busqueda dato : datos) {
				add(dato);
			}
		}
	}
	
	

	/*private view holder class*/
	private class ViewHolder {
		TextView titulo;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		Busqueda rowItem = getItem(position);

		
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.busqueda_item, parent, false);
			holder = new ViewHolder();
			holder.titulo  = (TextView) convertView.findViewById(R.id.busqueda_titulo);
			
			convertView.setTag(holder);
		} else
			holder = (ViewHolder) convertView.getTag();

		String titulo = rowItem.getTipoOperacion()+" "+rowItem.getTipoAnuncio()+" "+rowItem.getDistrito();
		holder.titulo.setText(titulo);
	

		return convertView;
	}
}
