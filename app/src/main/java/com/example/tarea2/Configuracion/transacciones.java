package com.example.tarea2.Configuracion;

public class transacciones {
    public static final String NameDatabase = "PMO1DB";

    //tablas de la DB en SQLite

    public static final String tablaPhotograh = "photograh";

    //campo de la tabla personas de la DB en SQLite
    public static final String id = "id";
    public static final String imagen = "imagen";
    public static final String descripcion = "descripcion";


    //transacciones DDL(DATA DEFINITION LENGUAGE) tabla personas
    public static final String CreateTablePhotograh = "CREATE TABLE photograh (id INTEGER PRIMARY KEY AUTOINCREMENT "+
            ",imagen BLOB, descripcion TEXT)";
    public static final String DROPTablePhotograh = "DROP TABLE IF EXISTS photograh";
}
