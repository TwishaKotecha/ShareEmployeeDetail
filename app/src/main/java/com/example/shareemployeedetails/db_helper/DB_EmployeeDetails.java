package com.example.shareemployeedetails.db_helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.shareemployeedetails.models.EmployeeDetails;
import com.example.shareemployeedetails.utility.Constants;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;

public class DB_EmployeeDetails extends SQLiteAssetHelper {
    public DB_EmployeeDetails(Context context) {
        super(context, Constants.DB_Name, null, Constants.DB_Version);
    }

    public ArrayList<EmployeeDetails> SelectAllEmployee() {
        ArrayList<EmployeeDetails> empDetailsArrayList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String strQuery = "Select * from Emp_Details order by EmpName";

        Cursor cur = db.rawQuery(strQuery, null);

        if (cur.moveToFirst()) {
            do {
                EmployeeDetails ed = new EmployeeDetails();
                ed.setEmployeeID(cur.getInt(cur.getColumnIndex("EmpID")));
                ed.setEmployeeName(cur.getString(cur.getColumnIndex("EmpName")));
                empDetailsArrayList.add(ed);

            } while (cur.moveToNext());
        }
        db.close();
        return empDetailsArrayList;
    }
}
