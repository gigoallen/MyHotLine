package com.gigoallen.ga2.myhotlinedb;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    //設定屬性
    static final String DB_NAME = "HotlineDB";
    static final String TB_NAME = "hotlist";
    static final int MAX = 8;
    static final String[] FROM = new String[] {"name", "phone", "email"};
    SQLiteDatabase db;
    Cursor cur;
    SimpleCursorAdapter adapter;
    EditText edtName, edtPhone, edtEmail;
    Button btnInsert, btnUpdate, btnDelete;
    ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //取得 views
        findViews();

        //open DB
        db = openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);

        //create Table
        String createTable = "CREATE TABLE IF NOT EXISTS " + TB_NAME +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name VARCHART(32), " +
                "phone VARCHART(16), " +
                "email VARCHART(64))";
        db.execSQL(createTable);

        cur = db.rawQuery("SELECT * FROM " + TB_NAME, null);

        //if db is empty, insert to table
        if(cur.getCount() == 0){
            addData("Allen", "0982110606", "gigo1908@gmail.com");
            addData("Bill", "09881234556", "bill@gmail.com");
        }

        // new Adapter obj
        adapter = new SimpleCursorAdapter(this,
                R.layout.item,
                cur,
                FROM,
                new int[] {R.id.name, R.id.phone, R.id.email},
                0
                );

        lv = findViewById(R.id.listView);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(this);
        requery();//查詢資料

    }


    //新增資料
    private void addData(String name, String phone, String email) {
        ContentValues cv = new ContentValues(3);

        cv.put(FROM[0], name);
        cv.put(FROM[1], phone);
        cv.put(FROM[2], email);

        db.insert(TB_NAME, null, cv);

    }

    //更新資料
    private void update(String name, String phone, String email, int id) {
        ContentValues cv = new ContentValues(3);

        cv.put(FROM[0], name);
        cv.put(FROM[1], phone);
        cv.put(FROM[2], email);

        db.update(TB_NAME, cv, "_id=" + id, null);

    }

    //查詢資料
    private void requery() {
        cur = db.rawQuery("SELECT * FROM " + TB_NAME, null);
        adapter.changeCursor(cur);
        if(cur.getCount() == MAX)
            btnInsert.setEnabled(false);
        else
            btnInsert.setEnabled(true);

        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
    }

    private void findViews() {
        edtName = findViewById(R.id.edtName);
        edtPhone = findViewById(R.id.edtPhone);
        edtEmail = findViewById(R.id.edtEmail);
        btnInsert = findViewById(R.id.btnInsert);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

        cur.moveToPosition(position);

        //select name, phone, email to list on layout
        edtName.setText(cur.getString(cur.getColumnIndex(FROM[0])));
        edtPhone.setText(cur.getString(cur.getColumnIndex(FROM[1])));
        edtEmail.setText(cur.getString(cur.getColumnIndex(FROM[2])));

        btnUpdate.setEnabled(true);
        btnDelete.setEnabled(true);
    }
    //btn onClick method
    public void onInsertUpdate(View view) {
        String strName = edtName.getText().toString().trim();
        String strPhone = edtPhone.getText().toString().trim();
        String strEmail = edtEmail.getText().toString().trim();

        if(strName.length() == 0 || strPhone.length() == 0 || strEmail.length() == 0) return;

        if(view.getId() == R.id.btnUpdate)
            update(strName, strPhone, strEmail, cur.getInt(0));
        else
            addData(strName, strPhone, strEmail);
        requery();
    }

    public void onDelete(View view) {
        db.delete(TB_NAME, "_id=" + cur.getInt(0), null);
        requery();
    }

    public void call(View view) {
        String uri = "tel:" + cur.getString(cur.getColumnIndex(FROM[1]));
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(i);
    }

    public void mail(View view) {
        String uri = "mailto:" + cur.getString(cur.getColumnIndex(FROM[2]));
        Intent i = new Intent(Intent.ACTION_SENDTO, Uri.parse(uri));
        startActivity(i);
    }
}
