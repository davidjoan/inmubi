/**
 * 
 */
package pe.inmubi.android;

import android.app.Application;
import pe.inmubi.android.models.Anuncio;
import pe.inmubi.android.models.Ubigeo;

/**
 * @author David
 *
 */
public class InmubiApplication extends Application {
	
	Ubigeo ubigeo;

	
	String TipoOperacion;
	
	String TipoInmueble;
	
	Anuncio anuncio;
	
	String isLocal;

	public Ubigeo getUbigeo() {
		return ubigeo;
	}

	public void setUbigeo(Ubigeo ubigeo) {
		this.ubigeo = ubigeo;
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

	public Anuncio getAnuncio() {
		return anuncio;
	}

	public void setAnuncio(Anuncio anuncio) {
		this.anuncio = anuncio;
	}

	public String getIsLocal() {
		return isLocal;
	}

	public void setIsLocal(String isLocal) {
		this.isLocal = isLocal;
	}
	
	
}
