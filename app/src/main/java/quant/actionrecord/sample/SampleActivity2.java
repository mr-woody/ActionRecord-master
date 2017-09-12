package quant.actionrecord.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 * Created by cz on 11/11/16.
 */

public class SampleActivity2 extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample2);
        ViewGroup layout= (ViewGroup) findViewById(R.id.ll_container);
        for(int i=0;i<layout.getChildCount();i++){
            final int finaIndex=i;
            layout.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(SampleActivity2.this, "Item:"+finaIndex, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
