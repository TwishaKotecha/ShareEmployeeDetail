package com.example.shareemployeedetails.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ajts.androidmads.library.SQLiteToExcel;
import com.example.shareemployeedetails.BuildConfig;
import com.example.shareemployeedetails.R;
import com.example.shareemployeedetails.adapter.EmployeeAdapter;
import com.example.shareemployeedetails.db_helper.DB_EmployeeDetails;
import com.example.shareemployeedetails.models.EmployeeDetails;
import com.example.shareemployeedetails.utility.Constants;
import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.internal.Utils;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.recycler_emp)
    RecyclerView recycler_emp;
    @BindView(R.id.btn_email)
    MaterialButton btn_email;
    Context context;
    ArrayList<EmployeeDetails> employeeDetailsList;
    EmployeeAdapter empAdapter;
    DB_EmployeeDetails db_employeeDetails;
    private static final int PERMISSION_REQ_WRITE_EXTERNAL_STORAGE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        init();
        fillData();
    }

    private void fillData() {
        employeeDetailsList = new ArrayList<>();


        db_employeeDetails = new DB_EmployeeDetails(context);
        employeeDetailsList = db_employeeDetails.SelectAllEmployee();

        empAdapter = new EmployeeAdapter(context, employeeDetailsList);
        recycler_emp.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        recycler_emp.setAdapter(empAdapter);
    }

    private void init() {
        context = MainActivity.this;
    }

    @OnClick({R.id.btn_email})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_email:
                if (hasStoragePermission(PERMISSION_REQ_WRITE_EXTERNAL_STORAGE)) {
                    convertToExcel();
                    shareFile();
                }
                break;

        }
    }

    private boolean hasStoragePermission(int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Build.VERSION.SDK_INT >= 30) {
                if (!Environment.isExternalStorageManager()) {
                    Intent getPermission = new Intent();
                    getPermission.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                    startActivity(getPermission);
                }
            }
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, requestCode);
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode ==PERMISSION_REQ_WRITE_EXTERNAL_STORAGE){

                convertToExcel();
                shareFile();
            }

        }
    }


    private void shareFile() {
        try {
            File root = Environment.getExternalStorageDirectory();
            String filelocation = root.getAbsolutePath() + "/Backup" + "/" + "Emp_Details.xls";

            File filePath = new File(filelocation);

            if(!filePath.exists()){
                filePath.createNewFile();
            }

            FileOutputStream fileOutputStream = new FileOutputStream(filePath);

            fileOutputStream.flush();
            fileOutputStream.close();

            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            Uri uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID+".provider", filePath);
            emailIntent.setType("application/excel");
            emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.Email_Subject_Line));
            emailIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.startActivity(emailIntent);

//            Intent intent = new Intent(Intent.ACTION_SENDTO);
//            intent.setType("text/plain");
//            String message = "File to be shared is " + "Emp_Details.xls" + ".";
//            intent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
//            intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + filelocation));
//            intent.putExtra(Intent.EXTRA_TEXT, message);
//            intent.setData(Uri.parse("mailto:twishakotecha21@gmail.com"));
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//            startActivity(intent);
        } catch (Exception e) {
            System.out.println("is exception raises during sending mail" + e);
            Log.e("macro","m->"+e.getMessage());
        }
    }


    private void convertToExcel() {
        String directory_path = Environment.getExternalStorageDirectory().getPath() + "/Backup/";
        File file = new File(directory_path);
        if (!file.exists()) {
            file.mkdirs();
        }
        // Export SQLite DB as EXCEL FILE
        SQLiteToExcel sqliteToExcel = new SQLiteToExcel(getApplicationContext(), Constants.DB_Name, directory_path);
        sqliteToExcel.exportSingleTable("Emp_Details", "Emp_Details.xls", new SQLiteToExcel.ExportListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onCompleted(String filePath) {
                Toast.makeText(context, "Success-> " + filePath, Toast.LENGTH_SHORT).show();
                Log.e("macro", "fp->" + filePath);
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(context, "Error-> " + e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("macro", "e->" + e.getMessage());
            }
        });
    }
}