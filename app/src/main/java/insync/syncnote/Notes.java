package insync.syncnote;

import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Notes extends AppCompatActivity {
    //Does nothing as of yet.
    ListView mListView;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notes);
        mListView = (ListView) findViewById(R.id.listView2);
        List<Note> notes = SyncNoteCore.getInst().getManager().getAllNotes();
        String[] noteIds = new String[notes.size()];
        List<String> noteIdsList = new ArrayList<>();
        for (Note n : notes) {
            noteIdsList.add(n.getId());
        }
        noteIdsList.toArray(noteIds);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, noteIds);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                String item = ((TextView)view).getText().toString();
                Toast.makeText(getBaseContext(), item, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
