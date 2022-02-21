package com.trofimm.decorate68;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;

public class OrderActivity extends AppCompatActivity {
    DrawerLayout drawer;
    DatabaseReference dbOrder;
    private List<String> listData;
    private List<Order> listTemp;
    private ListView listView;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        init();
        getDataFromDB();
        setOnClickItem();

        FirebaseMessaging.getInstance().subscribeToTopic("private");
    }

    @Override
    protected void onStart() {
        super.onStart();
        getDataFromDB();
    }

    public void init(){
        drawer = findViewById(R.id.drawer);
        dbOrder = FirebaseDatabase.getInstance().getReference("Orders");
        listView = findViewById(R.id.orders_list);
        listData = new ArrayList<>();
        listTemp = new ArrayList<>();
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
        Intent i = new Intent(OrderActivity.this, AddOrderActivity.class);
        startActivity(i);
    }

    public void onOrdersInWorkClick(MenuItem item) {
        Toast toast = Toast.makeText(getApplicationContext(),
                "Эта вкладка открыта!",
                Toast.LENGTH_SHORT);
        toast.show();
    }

    public void onArchiveFilesClick(MenuItem item) {
        Intent i = new Intent(OrderActivity.this, ArchiveActivity.class);
        startActivity(i);
    }

    private void getDataFromDB() {
        ValueEventListener vListener =new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (listData.size() > 0) listData.clear();
                if (listTemp.size() > 0) listTemp.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Order order = ds.getValue(Order.class);
                    assert order != null;
                    listData.add(order.id_order);
                    listTemp.add(order);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("!!!ERROR!!!", String.valueOf(error));
            }
        };
        dbOrder.addValueEventListener(vListener);
    }



    public void setOnClickItem() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Order order = listTemp.get(position);
                Intent i = new Intent(OrderActivity.this, ShowOrderActivity.class);
                i.putExtra("id_order", order.id_order);
                i.putExtra("customer", order.customer);
                i.putExtra("comm", order.comment);
                i.putExtra("contacts", order.contact);
                i.putExtra("box", order.korobox);
                i.putExtra("cost", order.cost);
                i.putExtra("precost", order.preCost);
                i.putExtra("fDate", order.firstDate);
                i.putExtra("lDate", order.lastDate);
                startActivity(i);
            }
        });
    }
}