package insync.syncnote;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import insync.syncnote.exceptions.RequestForbiddenException;
import insync.syncnote.exceptions.RequestInvalidException;

public class SyncNoteAppDemo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_note_demo);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }
    //Creates the options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }
    //Determines what option is selected and handles accordingly
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        //Takes to MainActivity2 menu
        if (item.getItemId() == R.id.action_menu)
        {
            Intent intent = new Intent(this, MainActivity2.class);
            this.startActivity(intent);
            return super.onOptionsItemSelected(item);
        }
        //Upload message to authority token. Does not work if authority token is invalid
        if (item.getItemId() == R.id.syncUp)
        {
            EditText syncText = (EditText) findViewById(R.id.syncText);
            String text = syncText.getText().toString();
            String key = SyncNoteCore.getInst().getConfig().getAuthToken();
            try {
                HTTPTasks.uploadText(key, text);
            } catch (RequestForbiddenException e) {
                e.printStackTrace();
            } catch (RequestInvalidException e) {
                e.printStackTrace();
            }
            return true;
        }
        //Download message from authority token. Does not work if authority token is invalid
        if (item.getItemId() == R.id.syncDown)
        {
            EditText syncText = (EditText) findViewById(R.id.syncText);
            String key = SyncNoteCore.getInst().getConfig().getAuthToken();
            String text = null;
            try {
                text = HTTPTasks.downloadText(key);
            } catch (RequestForbiddenException e) {
                e.printStackTrace();
            } catch (RequestInvalidException e) {
                e.printStackTrace();
            }
            syncText.getText().clear();
            syncText.getText().append(text);
            return true;
        }
        return true;
    }
}
