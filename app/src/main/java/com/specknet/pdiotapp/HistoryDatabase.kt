package com.specknet.pdiotapp

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.util.*

class HistoryDatabase(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    // create table sql query
    private val CREATE_ACTIVITY_TABLE = ("CREATE TABLE " + TABLE_HISTORY + "("
            + COLUMN_ACTIVITY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_ACTIVITY_NAME + " TEXT,"
            + COLUMN_DURATION + " LONG," + COLUMN_TIMESTAMP + " DATE" + ")")

    // drop table sql query
    private val DROP_ACTIVITY_TABLE = "DROP TABLE IF EXISTS $TABLE_HISTORY"

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_ACTIVITY_TABLE)
    }


    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

        //Drop User Table if exist
        db.execSQL(DROP_ACTIVITY_TABLE)

        // Create tables again
        onCreate(db)

    }

    fun addActivity(activity: Activity) {
        val db = this.writableDatabase

        val values = ContentValues()
        values.put(COLUMN_ACTIVITY_NAME, activity.activity_name)
        values.put(COLUMN_DURATION, activity.duration)
        values.put(COLUMN_TIMESTAMP, activity.date)

        // Inserting Row
        db.insert(TABLE_HISTORY, null, values)
        db.close()
    }

    fun getDuration(activity: Activity): Long {
        val name = activity.activity_name
        val date = activity.date
        val query =
            "SELECT * FROM $TABLE_HISTORY WHERE $COLUMN_ACTIVITY_NAME =  \"$name\" AND $COLUMN_TIMESTAMP =  \"$date\""

        val db = this.writableDatabase

        val cursor = db.rawQuery(query, null)

        if( cursor != null && cursor.moveToFirst() ){
//            cursor.moveToFirst()
            val duration = cursor.getLong(3)
            return duration
        }
        return 0
    }

    // Increment score by 1
    fun updateDuration(activity: Activity) {
        val database = this.writableDatabase
        var initial_duration: Long = getDuration(activity)
        val duration = initial_duration + 1
        val values = ContentValues()
        values.put(COLUMN_DURATION, duration)
        database.update(
            TABLE_HISTORY,
            values,
            "$COLUMN_ACTIVITY_NAME = ? AND $COLUMN_TIMESTAMP = ?",
            arrayOf(activity.activity_name, activity.date)
        )
        database.close()
    }


    fun updateActivity(activity: Activity) {
        val db = this.writableDatabase

        val values = ContentValues()
        val initial_duration = getDuration(activity)
        values.put(COLUMN_ACTIVITY_NAME, activity.activity_name)
        values.put(COLUMN_DURATION, initial_duration + activity.duration)
        values.put(COLUMN_TIMESTAMP, activity.date)

        // updating row
        db.update(TABLE_HISTORY, values, "$COLUMN_ACTIVITY_NAME = ? AND $COLUMN_TIMESTAMP = ?",
            arrayOf(activity.id.toString()))
        db.close()
    }


    fun deleteActivity(activity: Activity) {

        val db = this.writableDatabase
        // delete user record by id
        db.delete(TABLE_HISTORY, "$COLUMN_ACTIVITY_ID = ?",
            arrayOf(activity.id.toString()))
        db.close()

    }

    fun checkActivityToday(activityName: String, date:String): Boolean {
        val columns = arrayOf(COLUMN_ACTIVITY_NAME, COLUMN_TIMESTAMP)
        val db = this.readableDatabase

//        selection criteria
        val selection = "$COLUMN_ACTIVITY_NAME = ? AND $COLUMN_TIMESTAMP = ?"

        // selection argument
        val selectionArgs = arrayOf(activityName, date)

        // query user table with condition
        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id FROM user WHERE user_email = 'jack@androidtutorialshub.com';
         */
        val cursor = db.query(TABLE_HISTORY, //Table to query
            columns,        //columns to return
            selection,      //columns for the WHERE clause
            selectionArgs,  //The values for the WHERE clause
            null,  //group the rows
            null,   //filter by row groups
            null)  //The sort order


        val cursorCount = cursor.count
        cursor.close()
        db.close()

        if (cursorCount > 0) {
            return true
        }

        return false
    }



    companion object {

        // Database Version
        private val DATABASE_VERSION = 1

        // Database Name
        private val DATABASE_NAME = "HistoryManager.db"

        // User table name
        private val TABLE_HISTORY = "activity"

        // User Table Columns names
        private val COLUMN_ACTIVITY_ID = "activity_id"
        private val COLUMN_ACTIVITY_NAME = "activity_name"
        private val COLUMN_DURATION = "activity_duration"
        private val COLUMN_TIMESTAMP = "activity_timestamp"
    }
}
