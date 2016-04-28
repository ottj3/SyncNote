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
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.action_menu)
        {
            Intent intent = new Intent(this, MainActivity2.class);
            this.startActivity(intent);
            return super.onOptionsItemSelected(item);
        }
        if (item.getItemId() == R.id.syncUp)
        {
            EditText syncText = (EditText) findViewById(R.id.syncText);
            EditText syncKey = (EditText) findViewById(R.id.syncKey);
//            if (syncKey == null || syncText == null) return false;
            String text = syncText.getText().toString();
            String key = syncKey.getText().toString();
            try {
                HTTPTasks.uploadText(key, text);
            } catch (RequestForbiddenException e) {
                e.printStackTrace();
            } catch (RequestInvalidException e) {
                e.printStackTrace();
            }
            return true;
        }
        if (item.getItemId() == R.id.syncDown)
        {
            EditText syncText = (EditText) findViewById(R.id.syncText);
            EditText syncKey = (EditText) findViewById(R.id.syncKey);
//            if (syncKey == null || syncText == null) return false;
            String key = syncKey.getText().toString();
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

//    public void clickUp(View view) {
//        EditText syncText = (EditText) findViewById(R.id.syncText);
//        EditText syncKey = (EditText) findViewById(R.id.syncKey);
//        if (syncKey == null || syncText == null) return;
//        String text = syncText.getText().toString();
//        String key = syncKey.getText().toString();
//        try {
//            HTTPTasks.uploadText(key, text);
//        } catch (RequestForbiddenException e) {
//            e.printStackTrace();
//        } catch (RequestInvalidException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void clickDown(View view) {
//        EditText syncText = (EditText) findViewById(R.id.syncText);
//        EditText syncKey = (EditText) findViewById(R.id.syncKey);
//        if (syncKey == null || syncText == null) return;
//        String key = syncKey.getText().toString();
//        String text = null;
//        try {
//            text = HTTPTasks.downloadText(key);
//        } catch (RequestForbiddenException e) {
//            e.printStackTrace();
//        } catch (RequestInvalidException e) {
//            e.printStackTrace();
//        }
//        syncText.getText().clear();
//        syncText.getText().append(text);
//    }

}
