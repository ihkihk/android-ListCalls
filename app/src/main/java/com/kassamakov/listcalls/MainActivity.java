package com.kassamakov.listcalls;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CallLog;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity
        implements OnClickListener {
    Button btnAll;
    Button btnIncoming;
    Button btnOutgoing;
    Button btnMissed;

    TableLayout tblMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            btnAll = (Button) findViewById(R.id.btnAll);
            btnAll.setOnClickListener(this);

            btnIncoming = (Button) findViewById(R.id.btnIncoming);
            btnIncoming.setOnClickListener(this);

            btnMissed = (Button) findViewById(R.id.btnMissed);
            btnMissed.setOnClickListener(this);

            btnOutgoing = (Button) findViewById(R.id.btnOutgoing);
            btnOutgoing.setOnClickListener(this);

            tblMain = (TableLayout) findViewById(R.id.tblMain);
        } catch (Exception ex) {
            Toast.makeText(this, "Error in MainActivity.onCreate: " + ex.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        Cursor cursor;

        String[] cols = new String[]{"date", "name", "number", "geocoded_location", "duration"};

        try {
            switch (v.getId()) {
                case R.id.btnAll:
                    cursor = this.getContentResolver().query(CallLog.Calls.CONTENT_URI,
                            cols,
                            null,
                            null,
                            CallLog.Calls.DATE + " DESC");

                    CursorToTable(cursor, tblMain);
                    break;

                case R.id.btnIncoming:
                    cursor = this.getContentResolver().query(CallLog.Calls.CONTENT_URI,
                            cols,
                            CallLog.Calls.TYPE + " = " + CallLog.Calls.INCOMING_TYPE,
                            null,
                            CallLog.Calls.DATE + " DESC");

                    CursorToTable(cursor, tblMain);
                    break;

                case R.id.btnOutgoing:
                    cursor = this.getContentResolver().query(CallLog.Calls.CONTENT_URI,
                            cols,
                            CallLog.Calls.TYPE + " = " + CallLog.Calls.OUTGOING_TYPE,
                            null,
                            CallLog.Calls.DATE + " DESC");

                    CursorToTable(cursor, tblMain);
                    break;

                case R.id.btnMissed:
                    cursor = this.getContentResolver().query(CallLog.Calls.CONTENT_URI,
                            cols,
                            CallLog.Calls.TYPE + " = " + CallLog.Calls.MISSED_TYPE,
                            null,
                            CallLog.Calls.DATE + " DESC");

                    CursorToTable(cursor, tblMain);
                    break;
            }
        } catch (SecurityException sec) {
            Toast.makeText(this, "Permission missing: " + sec.getMessage(),
                    Toast.LENGTH_SHORT).show();
        } catch (Exception ex) {
            Toast.makeText(this, "Error in MainActivity.onClick: " + ex.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void CursorToTable(Cursor cursor, TableLayout tbl) {
        // Clear table if already populated
        tbl.removeAllViews();

        if (!cursor.moveToFirst())
            // Empty cursor
            return;

        // Column Headers
        TableRow headerRow = new TableRow(this);

        for (int i = 0; i < cursor.getColumnCount(); i++) {
            TextView textView = new TextView(this);

            textView.setGravity(Gravity.CENTER_HORIZONTAL);
            textView.setText(cursor.getColumnName(i));
            textView.setPadding(0, 0, 5, 0);
            textView.setAlpha(0.8f);

            headerRow.addView(textView);
        }

        headerRow.setPadding(10, 10, 10, 10);

        tbl.addView(headerRow);

        // Rows

        for (int i = 0; i < cursor.getCount(); i++) {
            TableRow row = new TableRow(this);

            for (int j = 0; j < cursor.getColumnCount(); j++) {
                TextView textView = new TextView(this);

                textView.setGravity(Gravity.CENTER_HORIZONTAL);

                if (cursor.getColumnName(j).equals("date")) {
                    textView.setText(DateFormat.getDateTimeInstance().format(new Date(cursor.getLong(j))));
                } else if (cursor.getColumnName(j).equals("duration")) {
                    textView.setText(new SimpleDateFormat("mm:ss", Locale.US).format(new Date(cursor.getLong(j) * 1000)));
                } else {
                    textView.setText(cursor.getString(j));
                }
                textView.setPadding(0, 0, 5, 0);

                row.addView(textView);
            }

            row.setPadding(10, 10, 10, 10);

            tbl.addView(row);

            cursor.moveToNext();
        }

        cursor.close();

    }
}
