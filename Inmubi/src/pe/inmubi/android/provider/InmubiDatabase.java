package pe.inmubi.android.provider;

import java.util.ArrayList;
import java.util.List;

import pe.inmubi.android.models.Anuncio;
import pe.inmubi.android.models.Busqueda;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class InmubiDatabase extends SQLiteOpenHelper {
	
	public InmubiDatabase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Logcat tag
    private static final String LOG = InmubiDatabase.class.getName();
	
	// Database Version
    private static final int DATABASE_VERSION = 1;
 
    // Database Name
    private static final String DATABASE_NAME = "inmubi.db";
    

    // NOTE: carefully update onUpgrade() when bumping database versions to make
    // sure user data is saved.

    private static final int VER_LAUNCH = 1;
    private static final int VER_SESSION_TYPE = 1;
    
    public interface Tables {	
    	String BUSQUEDA = "busqueda";
        String ANUNCIO  = "anuncio";
    }   
    
    public interface BUSQUEDA {
        String id = "id";
        String tipoAnuncio = "tipo_anuncio";	
        String tipoOperacion = "tipo_operacion";
        String ubigeo = "ubigeo";
        String distrito = "distrito";      
    }
    
    public interface ANUNCIO {
    	String codigoUbigeo = "codigo_ubigeo";
    	String fechaCreacion = "fecha_creacion";
    	String departmento = "departmento";
    	String detalle = "detalle";
    	String direccion = "direccion";
    	String distrito = "distrito";
    	String fechaPublicacion = "fecha_publicacion";
    	String origen = "origen";
    	String precio = "precio";
    	String provincia = "provincia";
    	String publicacion = "publicacion";
    	String tipoInmueble = "tipo_inmueble";
    	String tipoOperacion = "tipo_operacion";
    	String titulo = "titulo";
    	String url = "url";
    	String foto = "foto";
    	String id = "id";
    }
    
    
    // Table Create Statements
    // kardex table create statement
    private static final String CREATE_TABLE_BUSQUEDA = "CREATE TABLE "
            + Tables.BUSQUEDA + "(" + 
    		BUSQUEDA.id + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            BUSQUEDA.tipoAnuncio+" TEXT," +
            BUSQUEDA.tipoOperacion+" TEXT," +
            BUSQUEDA.ubigeo+" TEXT," +
            BUSQUEDA.distrito+" TEXT"+
            ")";
    
    // ROUTE table create statement
    private static final String CREATE_TABLE_ANUNCIO = "CREATE TABLE "
            + Tables.ANUNCIO + "(" +
            ANUNCIO.id + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            ANUNCIO.codigoUbigeo+" TEXT," +
            ANUNCIO.fechaCreacion+" TEXT," +
            ANUNCIO.departmento+" TEXT," +
            ANUNCIO.detalle+" TEXT," +
            ANUNCIO.direccion+" TEXT," +
            ANUNCIO.distrito+" TEXT," +
            ANUNCIO.fechaPublicacion+" TEXT," +
            ANUNCIO.origen+" TEXT," +
            ANUNCIO.precio+" TEXT," +
            ANUNCIO.provincia+" TEXT," +
            ANUNCIO.publicacion+" TEXT," +
            ANUNCIO.tipoInmueble+" TEXT," +
            ANUNCIO.tipoOperacion+" TEXT," +
            ANUNCIO.titulo+" TEXT," +
            ANUNCIO.url+" TEXT," +
            ANUNCIO.foto+" TEXT" +
            ")"; 
    
    
    

    @Override
    public void onCreate(SQLiteDatabase db) {
 
        // creating required tables
        db.execSQL(CREATE_TABLE_ANUNCIO);
        db.execSQL(CREATE_TABLE_BUSQUEDA);
    }
 
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + Tables.BUSQUEDA);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.ANUNCIO);
 
        // create new tables
        onCreate(db);
    }
    
    public void executeQuery(String query)
    {
    	SQLiteDatabase db = this.getReadableDatabase();
    	db.execSQL(query);
    	closeDB();
    	
    }
    

    
    /*
     * getting all Busquedas
     * */
    public List<Busqueda> getTopBusqueda() {
        List<Busqueda> busquedas = new ArrayList<Busqueda>();
        String selectQuery = "SELECT  * FROM " + Tables.BUSQUEDA+" order by id desc limit 10";
     
        Log.d(LOG, selectQuery);
        
        int qty = 0;
     
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
     
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Busqueda busqueda = new Busqueda();
                
                busqueda.setId(c.getString(c.getColumnIndex(BUSQUEDA.id)));
                busqueda.setTipoAnuncio(c.getString(c.getColumnIndex(BUSQUEDA.tipoAnuncio)));
                busqueda.setTipoOperacion(c.getString(c.getColumnIndex(BUSQUEDA.tipoOperacion)));
                busqueda.setUbigeo(c.getString(c.getColumnIndex(BUSQUEDA.ubigeo)));
                busqueda.setDistrito(c.getString(c.getColumnIndex(BUSQUEDA.distrito)));
               
                // adding to kardex list
                busquedas.add(busqueda);
                
                qty++;
            } while (c.moveToNext());
        }
        
        Log.d(LOG, "cantidad de registros: "+qty);
        closeDB();
     
        return busquedas;
    }
    
    
    /*
     * Insertar Busqueda
     */
    public void insertBusqueda(Busqueda busqueda) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        
        //validacion
        
        String selectQuery = "SELECT  * FROM " + Tables.BUSQUEDA+" where tipo_anuncio = '"+busqueda.getTipoAnuncio()+
        		"' and tipo_operacion = '"+busqueda.getTipoOperacion()+"' and ubigeo = '"+busqueda.getUbigeo()+"'";
        
       
     
        Cursor c = db.rawQuery(selectQuery, null);
     
        // looping through all rows and adding to list
        if (!c.moveToFirst()) {
            ContentValues values = new ContentValues();
            values.put(BUSQUEDA.distrito, busqueda.getDistrito());
            values.put(BUSQUEDA.tipoAnuncio, busqueda.getTipoAnuncio());
            values.put(BUSQUEDA.tipoOperacion, busqueda.getTipoOperacion());
            values.put(BUSQUEDA.ubigeo, busqueda.getUbigeo());

            db.insert(Tables.BUSQUEDA, null, values);       
        }
        
     

        closeDB();
        
        Log.d(LOG, "Insertado ");
    }

    
 // closing database
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }
    
    
   
    public static void deleteDatabase(Context context) {
        context.deleteDatabase(DATABASE_NAME);
    }
    
    
    
    /*
     * getting all Busquedas
     * */
    public List<Anuncio> getAllAnuncios() {
        List<Anuncio> busquedas = new ArrayList<Anuncio>();
        String selectQuery = "SELECT  * FROM " + Tables.ANUNCIO+" order by id desc limit 1000";
     
        Log.d(LOG, selectQuery);
        
        int qty = 0;
     
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
     
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Anuncio busqueda = new Anuncio();
                
                busqueda.setId(c.getString(c.getColumnIndex(ANUNCIO.id)));
                busqueda.setCodigoUbigeo(c.getString(c.getColumnIndex(ANUNCIO.codigoUbigeo)));
                busqueda.setFechaCreacion(c.getString(c.getColumnIndex(ANUNCIO.fechaCreacion)));
                busqueda.setDepartmento(c.getString(c.getColumnIndex(ANUNCIO.departmento)));
                busqueda.setDetalle(c.getString(c.getColumnIndex(ANUNCIO.detalle)));

                busqueda.setDireccion(c.getString(c.getColumnIndex(ANUNCIO.direccion)));
                busqueda.setDistrito(c.getString(c.getColumnIndex(ANUNCIO.distrito)));
                busqueda.setFechaPublicacion(c.getString(c.getColumnIndex(ANUNCIO.fechaPublicacion)));
                busqueda.setPublicacion(c.getString(c.getColumnIndex(ANUNCIO.publicacion)));
                busqueda.setOrigen(c.getString(c.getColumnIndex(ANUNCIO.origen)));
                busqueda.setPrecio(c.getString(c.getColumnIndex(ANUNCIO.precio)));
                busqueda.setProvincia(c.getString(c.getColumnIndex(ANUNCIO.provincia)));
                busqueda.setTipoInmueble(c.getString(c.getColumnIndex(ANUNCIO.tipoInmueble)));
                busqueda.setTipoOperacion(c.getString(c.getColumnIndex(ANUNCIO.tipoOperacion)));
                busqueda.setTitulo(c.getString(c.getColumnIndex(ANUNCIO.titulo)));
                busqueda.setUrl(c.getString(c.getColumnIndex(ANUNCIO.url)));
                busqueda.setFoto(c.getString(c.getColumnIndex(ANUNCIO.foto)));
               
               
                // adding to kardex list
                busquedas.add(busqueda);
                
                qty++;
            } while (c.moveToNext());
        }
        
        Log.d(LOG, "cantidad de registros anuncios: "+qty);
        closeDB();
     
        return busquedas;
    }
    
    
    /*
     * Insertar Ausencia
     */
    public void insertAusencia(Anuncio busqueda) {
        SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(ANUNCIO.codigoUbigeo, busqueda.getCodigoUbigeo());
            values.put(ANUNCIO.fechaCreacion, busqueda.getFechaCreacion());
            values.put(ANUNCIO.departmento, busqueda.getDepartmento());
            values.put(ANUNCIO.detalle, busqueda.getDetalle());
            values.put(ANUNCIO.direccion, busqueda.getDireccion());

            values.put(ANUNCIO.distrito, busqueda.getDistrito());
            values.put(ANUNCIO.fechaPublicacion, busqueda.getPublicacion());
            values.put(ANUNCIO.direccion, busqueda.getDireccion());

            values.put(ANUNCIO.origen, busqueda.getOrigen());
            values.put(ANUNCIO.precio, busqueda.getPrecio());
            values.put(ANUNCIO.provincia, busqueda.getProvincia());
            values.put(ANUNCIO.publicacion, busqueda.getPublicacion());
            values.put(ANUNCIO.tipoInmueble, busqueda.getTipoInmueble());
            values.put(ANUNCIO.tipoOperacion, busqueda.getTipoOperacion());
            values.put(ANUNCIO.titulo, busqueda.getTitulo());
            values.put(ANUNCIO.url, busqueda.getUrl());
            values.put(ANUNCIO.foto, busqueda.getFoto());
            
            
            db.insert(Tables.ANUNCIO, null, values);       
     

        closeDB();
        
        Log.d(LOG, "Insertado aNUNCIO");
    }
    
    //---deletes a particular title---
    public void deleteAnuncio(int id) 
    {
    	 SQLiteDatabase db = this.getWritableDatabase();
         db.delete(Tables.ANUNCIO, ANUNCIO.id + "=" + id, null);
         
         closeDB();
    }

    
}
