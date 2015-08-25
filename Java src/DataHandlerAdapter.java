package twitterapp;

/**
 * Created by adamyaziji on 24/04/2015.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by adamyaziji on 19/04/2015.
 */
public class DataHandlerAdapter {

    static DataHandler dataHandler;

    public DataHandlerAdapter(Context context) {
        dataHandler = new DataHandler(context);
    }

    public long insertData(String groupname, String groupmembers, Long grouplistid) {

        SQLiteDatabase sqLiteDatabase = dataHandler.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DataHandler.GROUP_NAME, groupname);
        contentValues.put(DataHandler.GROUP_MEMBERS, groupmembers);
        contentValues.put(DataHandler.GROUP_LISTID, grouplistid);
        long id = sqLiteDatabase.insert(DataHandler.TABLE_NAME, null, contentValues);
        return id;
    }

    class DataHandler extends SQLiteOpenHelper {
        private static final String DATA_BASE_NAME = "Twitter Database";
        public static final String TABLE_NAME = "Twittertable";
        private static final int DATABASE_VERSION = 2;
        public static final String GROUP_ID = "_id";
        public static final String GROUP_NAME = "Groupname";
        public static final String GROUP_MEMBERS = "Groupmembers";
        public static final String GROUP_LISTID = "GroupListID";

        String groupUsernames, position_ID;
        long list_id;
        Cursor c;

        public String[] columns = {GROUP_ID, GROUP_NAME, GROUP_MEMBERS,GROUP_LISTID};

        SQLiteDatabase db;

        private static final String CREATE_TABLE = "create table if not exists "
                + TABLE_NAME + " ( "
                + GROUP_ID + " integer primary key autoincrement, "
                + GROUP_NAME + " text, "
                + GROUP_MEMBERS + " text, "
                + GROUP_LISTID + " INTEGER)";

        public DataHandler(Context context) {
            super(context, DATA_BASE_NAME, null, DATABASE_VERSION);
        }

        public DataHandler open() {
            dataHandler.getWritableDatabase();
            return this;
        }

        public Cursor getallRows() {
            SQLiteDatabase db = this.getReadableDatabase();
            c = db.query(DataHandler.TABLE_NAME, columns, null, null, null, null,
                    null, null);

            return c;
        }

        public String getRowUsernames(long position){
            SQLiteDatabase db = this.getReadableDatabase();
            position_ID = String.valueOf(position);
            Cursor c = db.rawQuery("select "+GROUP_MEMBERS+" from " + TABLE_NAME +
                    " where " + GROUP_ID + "=" + position_ID, null);

            if (c != null) {
               if(c.moveToFirst()){
                    groupUsernames = c.getString(c.getColumnIndex(GROUP_MEMBERS));

               }
            }
            c.close();
            return groupUsernames;

        }

        public long getRowListID(long position){
            SQLiteDatabase db = this.getReadableDatabase();
            position_ID = String.valueOf(position);
            Log.d("DataBase", "position is" + position_ID);
            Cursor c = db.rawQuery("select "+ GROUP_LISTID +" from " + TABLE_NAME +
                    " where " + GROUP_ID + "=" + position_ID, null);

            if (c != null) {
                if(c.moveToFirst()){
                    list_id = c.getInt(c.getColumnIndex(GROUP_LISTID));
                }
            }

            Log.d("DataBase", "List Id is" + list_id);

            return list_id;
        }

        public String getusernames(){
            return groupUsernames;
        }

        public long getlistID(){
            return list_id;
        }

        public void onCreate(SQLiteDatabase db) {

            db.execSQL(CREATE_TABLE);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            db.execSQL("DROP TABLE " + TABLE_NAME + "IF EXISTS");
            onCreate(db);
        }

        public void deleteAllDB() {

            SQLiteDatabase db = this.getReadableDatabase();
            db.delete("Twittertable", null, null);

        }

    }
}

