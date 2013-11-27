package pe.inmubi.android.adapters;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.List;

import com.androidquery.AQuery;

import pe.inmubi.android.R;
import pe.inmubi.android.models.Anuncio;
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

public class AnuncioListViewAdapter extends ArrayAdapter<Anuncio> {

	Context context;
	private final LayoutInflater mInflater;
	
	
	public AnuncioListViewAdapter(Context context) {
		super(context, android.R.layout.simple_list_item_2);
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.context = context;
	}
	
	public void setData(List<Anuncio> datos) {
		this.clear();
		if (datos != null) {
			for (Anuncio dato : datos) {
				add(dato);
			}
		}
	}
	
	

	/*private view holder class*/
	private class ViewHolder {
		TextView title;
		TextView address;
		TextView price;
        ImageView photo;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		Anuncio rowItem = getItem(position);

		
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.anuncio_item, parent, false);
			holder = new ViewHolder();
			holder.price  = (TextView) convertView.findViewById(R.id.price);
			holder.address = (TextView) convertView.findViewById(R.id.address);
			holder.title = (TextView) convertView.findViewById(R.id.title);
			holder.photo = (ImageView) convertView.findViewById(R.id.photo);
			
			convertView.setTag(holder);
		} else
			holder = (ViewHolder) convertView.getTag();

		holder.title.setText(rowItem.getTitulo());
        holder.address.setText(rowItem.getDireccion());
		holder.price.setText(rowItem.getPrecio());
		
		
		
		AQuery aq = new AQuery(convertView);
		
		aq.id(holder.photo.getId()).image(rowItem.getFoto());

		//Uri uri = new Uri(rowItem.getFoto());
		
	/*	URL url;
		try {
			url = new URL(rowItem.getFoto());
			Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
			holder.photo.setImageBitmap(bmp);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/

		
	//	holder.photo.setImageURI(uri);

		
		

		return convertView;
	}
}
