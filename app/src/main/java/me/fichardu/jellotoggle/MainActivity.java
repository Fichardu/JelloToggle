package me.fichardu.jellotoggle;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends ActionBarActivity {

    private JelloToggle mToggle1;
    private JelloToggle mToggle2;
    private JelloToggle mToggle3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToggle1 = (JelloToggle) findViewById(R.id.jello1);
        mToggle1.setCheckedJelloColor(0xffdb654a);
        mToggle2 = (JelloToggle) findViewById(R.id.jello2);
        mToggle2.setCheckedJelloColor(0xfffb008a);
        mToggle3 = (JelloToggle) findViewById(R.id.jello3);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
