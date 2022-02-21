package com.trofimm.decorate68;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ArchiveActivity extends AppCompatActivity {
    DrawerLayout drawer;
    DatabaseReference dbArchive;
    private List<String> listData;
    private List<Archive> listTemp;
    private ListView listView;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archive);
        init();
        getDataFromDB();
        setOnClickItem();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getDataFromDB();
    }

    public void init(){
        drawer = findViewById(R.id.drawer);
        dbArchive = FirebaseDatabase.getInstance().getReference("Archive");
        listView = findViewById(R.id.archive_list);
        listData = new ArrayList<>();
        listTemp = new ArrayList<Archive>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listData);
        listView.setAdapter(adapter);
    }

    public void onClickMenuOpen(View view) {
        drawer.openDrawer(GravityCompat.END);
    }

    public void onCloseNavMenuClick(MenuItem item) {
        drawer.closeDrawer(GravityCompat.END);
    }
    public void onAddOrderClick(MenuItem item) {
        Intent i = new Intent(ArchiveActivity.this, AddOrderActivity.class);
        startActivity(i);
    }

    public void onOrdersInWorkClick(MenuItem item) {
        Intent i = new Intent(ArchiveActivity.this, OrderActivity.class);
        startActivity(i);
    }

    public void onArchiveFilesClick(MenuItem item) {
        Toast toast = Toast.makeText(getApplicationContext(),
                "Эта вкладка открыта!",
                Toast.LENGTH_SHORT);
        toast.show();
    }

    private void getDataFromDB(){
        ValueEventListener vListener =new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (listData.size() > 0) listData.clear();
                if (listTemp.size() > 0) listTemp.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Archive archive = ds.getValue(Archive.class);
                    assert archive != null;
                    listData.add(archive.id_archive);
                    listTemp.add(archive);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("!!!ERROR!!!", String.valueOf(error));
            }
        };
        dbArchive.addValueEventListener(vListener);
    }

    public void setOnClickItem() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Archive archive = listTemp.get(position);
                Intent i = new Intent(ArchiveActivity.this, ShowArchiveActivity.class);
                i.putExtra("id_archive", archive.id_archive);
                i.putExtra("customer_archive", archive.customer_archive);
                i.putExtra("comment_archive", archive.comment_archive);
                i.putExtra("contact_archive", archive.contact_archive);
                i.putExtra("box_archive", archive.box_archive);
                i.putExtra("cost_archive", archive.cost_archive);
                i.putExtra("precost_archive", archive.precost_archive);
                i.putExtra("fDate_archive", archive.fDate_archive);
                i.putExtra("lDate_archive", archive.lDate_archive);
                startActivity(i);
            }
        });
    }
}