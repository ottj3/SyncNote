package insync.syncnote;

        import android.content.Intent;
        import android.os.Bundle;
        import android.support.v7.app.AppCompatActivity;
        import android.view.ContextMenu;
        import android.view.MenuItem;
        import android.view.View;
        import android.widget.ArrayAdapter;
        import android.widget.AdapterView;
        import android.widget.EditText;
        import android.widget.ListView;
        import android.widget.TextView;
        import android.widget.Toast;

public class MainActivity2 extends AppCompatActivity {
    //This creates the list view menu for SyncNoteAppDemo that handles account information and provides framework for future handling of notes and settings
    String[] menuItemsArray = {"Account", "Notes", "Settings"};
    ListView mListView;
    public String key;
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
                    //Opens a submenu that contains information for registering, logging in, and logging out.
                    registerForContextMenu(mListView);
                    openContextMenu(mListView);
                    unregisterForContextMenu(mListView);
                }
                if (item == "Notes")
                {
                    //Takes to separate notes menu
                    Intent intent = new Intent(MainActivity2.this, Notes.class);
                    MainActivity2.this.startActivity(intent);
                }
                if (item == "Settings")
                {
                    //Unimplemented feature
                    Toast.makeText(getBaseContext(), "Does nothing yet", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        //Creates context menu for Account
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Select Action");
        menu.add(0, v.getId(), 0, "Register");
        menu.add(0, v.getId(), 0, "Log in");
        menu.add(0, v.getId(), 0, "Log out");
    }
    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        if (item.getTitle() == "Register")
        {
            //Does nothing yet
            Toast.makeText(getApplicationContext(),"1",Toast.LENGTH_LONG).show();
            return true;
        }

        else if (item.getTitle() == "Log in")
        {
            //Logs in to the system at tcnj.edu/~ottj3/ authority token changes frequently, so will need to be updated to continue working
            SyncNoteCore.getInst().getConfig().setAuthToken("5726d35201043");
            Toast.makeText(getApplicationContext(),SyncNoteCore.getInst().getConfig().getAuthToken(),Toast.LENGTH_LONG).show();
            return true;
        }
        else if (item.getTitle() == "Log out")
        {
            //Clears the authority token
            SyncNoteCore.getInst().getConfig().setAuthToken("");
            SyncNoteCore.getInst().getManager().removeAll();
            Toast.makeText(getApplicationContext(),"Successfully logged out",Toast.LENGTH_LONG).show();
            return true;
        }
        else
        {
            return false;
        }
    }
    public String getString()
    {
        return key;
    }
}
