package insync.syncnote;

        import android.content.Intent;
        import android.os.Bundle;
        import android.support.v7.app.AppCompatActivity;
        import android.view.View;
        import android.widget.ArrayAdapter;
        import android.widget.AdapterView;
        import android.widget.ListView;
        import android.widget.TextView;
        import android.widget.Toast;

public class MainActivity2 extends AppCompatActivity {
    String[] menuItemsArray = {"Account", "Notes", "TBD"};
    ListView mListView;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_2);
        mListView = (ListView) findViewById(R.id.listView);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, menuItemsArray);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                String item = ((TextView)view).getText().toString();
                if (item == "Account")
                {
                    Toast.makeText(getBaseContext(), "Blah", Toast.LENGTH_SHORT).show();
                }
                if (item == "Notes")
                {
                    Intent intent = new Intent(MainActivity2.this, Notes.class);
                    MainActivity2.this.startActivity(intent);
                }
                else
                {
                    Toast.makeText(getBaseContext(), item, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
