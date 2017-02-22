package mcgroup10.com.batroid;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by abhishekzambre on 19/11/16.
 */

public class Settings extends AppCompatActivity implements OnItemClickListener {

    //Declaration of variables used for Database access
    DatabaseHelper myDB;
    String table_name = "geofence_records";
    Button bt1;
    ArrayList<String> checkedValue;

    int i = 0;
    ListView lv;
    Model[] modelItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.action_settings);

        myDB = new DatabaseHelper(this);

        lv = (ListView) findViewById(R.id.listView1);
        bt1 = (Button) findViewById(R.id.button4);
        int profile_counts = myDB.getRowCount(table_name);

        modelItems = new Model[profile_counts];
        Cursor res = myDB.getAllData(table_name);
        while (res.moveToNext()) {
            modelItems[i] = new Model(res.getString(0), 0);
            i++;
        }
        res.close();
        final CustomAdapter adapter = new CustomAdapter(this, modelItems);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(this);
        bt1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ArrayList<String> arr = adapter.getChecked();
                for (String key : arr) {
                    myDB.deleteGeofence(table_name, key);
                    Toast.makeText(getApplicationContext(), "Deleted : " + key, Toast.LENGTH_SHORT).show();
                }
                Intent intent = getIntent();
                finish();
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onItemClick(AdapterView arg0, View v, int position, long arg3) {
        CheckBox cb = (CheckBox) v.findViewById(R.id.checkBox1);
        TextView tv = (TextView) v.findViewById(R.id.textView1);
        Toast.makeText(getApplicationContext(), "Came here.", Toast.LENGTH_SHORT).show();
        cb.performClick();
        if (cb.isChecked()) {
            checkedValue.add(tv.getText().toString());
        } else if (!cb.isChecked()) {
            checkedValue.remove(tv.getText().toString());
        }
    }

}
