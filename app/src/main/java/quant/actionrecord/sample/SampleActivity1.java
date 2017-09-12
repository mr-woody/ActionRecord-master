package quant.actionrecord.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Created by cz on 11/11/16.
 */

public class SampleActivity1 extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample1);

        ListView listView= (ListView) findViewById(R.id.list_view1);
        listView.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,DataProvider.ITEMS));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(SampleActivity1.this, "Item:"+i, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
