package insync.syncnote;

import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

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

    public void clickUp(View view) {
        EditText syncText = (EditText) findViewById(R.id.syncText);
        EditText syncKey = (EditText) findViewById(R.id.syncKey);
        if (syncKey == null || syncText == null) return;
        String text = syncText.getText().toString();
        String key = syncKey.getText().toString();
        SyncNoteCore.uploadText(key, text);
    }

    public void clickDown(View view) {
        EditText syncText = (EditText) findViewById(R.id.syncText);
        EditText syncKey = (EditText) findViewById(R.id.syncKey);
        if (syncKey == null || syncText == null) return;
        String key = syncKey.getText().toString();
        String text = SyncNoteCore.downloadText(key);
        syncText.getText().clear();
        syncText.getText().append(text);
    }
}
