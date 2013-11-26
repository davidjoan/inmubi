/**
 * 
 */
package pe.inmubi.android;

import android.app.Application;
import pe.inmubi.android.models.Ubigeo;

/**
 * @author David
 *
 */
public class InmubiApplication extends Application {
	
	Ubigeo Ubigeo;
	
	String TipoOperacion;
	
	String TipoInmueble;

	public Ubigeo getUbigeo() {
		return Ubigeo;
	}

	public void setUbigeo(Ubigeo ubigeo) {
		Ubigeo = ubigeo;
	}

	public String getTipoOperacion() {
		return TipoOperacion;
	}

	public void setTipoOperacion(String tipoOperacion) {
		TipoOperacion = tipoOperacion;
	}

	public String getTipoInmueble() {
		return TipoInmueble;
	}

	public void setTipoInmueble(String tipoInmueble) {
		TipoInmueble = tipoInmueble;
	}
}
