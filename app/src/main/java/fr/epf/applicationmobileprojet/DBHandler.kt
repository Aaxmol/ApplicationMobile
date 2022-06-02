package fr.epf.applicationmobileprojet

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHandler
    (context: Context?) :
    SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val query = ("CREATE TABLE " + TABLE_NAME + " ("
                + ID_STATION + " TEXT)")
        db.execSQL(query)
    }

    fun addStation(
        stationID: String?,
    ) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(ID_STATION, stationID)
        db!!.insert(TABLE_NAME, null, values)
        db.close()
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME)
        onCreate(db)
    }

    fun deleteStation(id: Long?) {

        val db = this.writableDatabase
        db!!.delete(
            TABLE_NAME,
            ID_STATION + "= ?",
            arrayOf(id.toString())
        )
    }

    @SuppressLint("Range")
    fun getAllStations(): List<String>? {
        val db = this.writableDatabase
        val stationList: MutableList<String> = ArrayList()
        var cur: Cursor? = null
        db.beginTransaction()
        try {
            cur = db.query(TABLE_NAME, null, null, null, null, null, null, null)
            if (cur != null) {
                if (cur.moveToFirst()) {
                    do {
                        val station: String = cur.getString(cur.getColumnIndex(ID_STATION))
                        stationList.add(station)
                    } while (cur.moveToNext())
                }
            }
        } finally {
            db.endTransaction()
            assert(cur != null)
            cur!!.close()
        }
        return stationList
    }


    companion object {
        private const val DB_NAME = "stationdb"
        private const val DB_VERSION = 1
        private const val TABLE_NAME = "mesfavoris"
        private const val ID_STATION = "id_station"

    }
}
