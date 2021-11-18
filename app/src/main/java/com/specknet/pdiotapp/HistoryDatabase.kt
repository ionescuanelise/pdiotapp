package com.specknet.pdiotapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.specknet.pdiotapp.Activity
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

    /**
     * This method is to fetch all user and return the list of user records
     *
     * @return list
     */
    fun getAllUser(): List<Activity> {

        // array of columns to fetch
        val columns = arrayOf(COLUMN_ACTIVITY_ID, COLUMN_ACTIVITY_NAME, COLUMN_DURATION, COLUMN_TIMESTAMP)

        // sorting orders
        val sortOrder = "$COLUMN_ACTIVITY_NAME ASC"
        val activityList = ArrayList<Activity>()

        val db = this.readableDatabase

        // query the user table
        val cursor = db.query(TABLE_HISTORY, //Table to query
            columns,            //columns to return
            null,     //columns for the WHERE clause
            null,  //The values for the WHERE clause
            null,      //group the rows
            null,       //filter by row groups
            sortOrder)         //The sort order
        if (cursor.moveToFirst()) {
            do {
                val user = Activity(id = cursor.getString(cursor.getColumnIndex(COLUMN_ACTIVITY_ID)).toInt(),
                    activity_name = cursor.getString(cursor.getColumnIndex(COLUMN_ACTIVITY_NAME)),
                    duration = cursor.getLong(cursor.getColumnIndex(COLUMN_DURATION)),
                    date = cursor.getString(cursor.getColumnIndex(COLUMN_TIMESTAMP)))

                activityList.add(user)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return activityList
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


    fun updateActivity(activity: Activity) {
        val db = this.writableDatabase

        val values = ContentValues()
        values.put(COLUMN_ACTIVITY_NAME, activity.activity_name)
        values.put(COLUMN_DURATION, activity.duration)
        values.put(COLUMN_TIMESTAMP, activity.date)

        // updating row
        db.update(TABLE_HISTORY, values, "$COLUMN_ACTIVITY_ID = ?",
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

    /**
     * This method to check user exist or not
     *
     * @param email
     * @return true/false
     */
//    fun checkUser(email: String): Boolean {
//
//        // array of columns to fetch
//        val columns = arrayOf(COLUMN_USER_ID)
//        val db = this.readableDatabase
//
//        // selection criteria
//        val selection = "$COLUMN_USER_EMAIL = ?"
//
//        // selection argument
//        val selectionArgs = arrayOf(email)
//
//        // query user table with condition
//        /**
//         * Here query function is used to fetch records from user table this function works like we use sql query.
//         * SQL query equivalent to this query function is
//         * SELECT user_id FROM user WHERE user_email = 'jack@androidtutorialshub.com';
//         */
//        val cursor = db.query(TABLE_HISTORY, //Table to query
//            columns,        //columns to return
//            selection,      //columns for the WHERE clause
//            selectionArgs,  //The values for the WHERE clause
//            null,  //group the rows
//            null,   //filter by row groups
//            null)  //The sort order
//
//
//        val cursorCount = cursor.count
//        cursor.close()
//        db.close()
//
//        if (cursorCount > 0) {
//            return true
//        }
//
//        return false
//    }

    /**
     * This method to check user exist or not
     *
     * @param email
     * @param password
     * @return true/false
     */
//    fun checkUser(email: String, password: String): Boolean {
//
//        // array of columns to fetch
//        val columns = arrayOf(COLUMN_USER_ID)
//
//        val db = this.readableDatabase
//
//        // selection criteria
//        val selection = "$COLUMN_USER_EMAIL = ? AND $COLUMN_USER_PASSWORD = ?"
//
//        // selection arguments
//        val selectionArgs = arrayOf(email, password)
//
//        // query user table with conditions
//        /**
//         * Here query function is used to fetch records from user table this function works like we use sql query.
//         * SQL query equivalent to this query function is
//         * SELECT user_id FROM user WHERE user_email = 'jack@androidtutorialshub.com' AND user_password = 'qwerty';
//         */
//        val cursor = db.query(TABLE_HISTORY, //Table to query
//            columns, //columns to return
//            selection, //columns for the WHERE clause
//            selectionArgs, //The values for the WHERE clause
//            null,  //group the rows
//            null, //filter by row groups
//            null) //The sort order
//
//        val cursorCount = cursor.count
//        cursor.close()
//        db.close()
//
//        if (cursorCount > 0)
//            return true
//
//        return false
//
//    }

    companion object {

        // Database Version
        private val DATABASE_VERSION = 1

        // Database Name
        private val DATABASE_NAME = "UserManager.db"

        // User table name
        private val TABLE_HISTORY = "activity"

        // User Table Columns names
        private val COLUMN_ACTIVITY_ID = "activity_id"
        private val COLUMN_ACTIVITY_NAME = "activity_name"
        private val COLUMN_DURATION = "activity_duration"
        private val COLUMN_TIMESTAMP = "activity_timestamp"
    }
}
