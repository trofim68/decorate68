package com.trofimm.decorate68;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ShowArchiveActivity extends AppCompatActivity {
    private DatabaseReference dbOrder, dbArchive;
    private TextView firstDate, lastDate, customer, comment, contacts, cost, precost, box;
    private TextView orderID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_archive);
        init();
        getIntentOrder();
    }

    private void init(){
        dbOrder = FirebaseDatabase.getInstance().getReference("Orders");
        dbArchive = FirebaseDatabase.getInstance().getReference("Archive");
        orderID = findViewById(R.id.orderID);
        customer = findViewById(R.id.archiveShowCustomer);
        comment = findViewById(R.id.archiveShowComment);
        contacts = findViewById(R.id.archiveShowContact);
        box = findViewById(R.id.archiveShowBox);
        cost = findViewById(R.id.archiveShowCost);
        precost = findViewById(R.id.archiveShowPrecost);
        firstDate = findViewById(R.id.archiveShowFirstdate);
        lastDate = findViewById(R.id.archiveShowLastdate);
    }

    public void onClickPopupMenuOpen(View view) {
        String id = orderID.getText().toString();
        String custom = customer.getText().toString();
        String comm = comment.getText().toString();
        String contact = contacts.getText().toString();
        String korobox = box.getText().toString();
        String money = cost.getText().toString();
        String preMoney = precost.getText().toString();
        String fDate = firstDate.getText().toString();
        String lDate = lastDate.getText().toString();

        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.inflate(R.menu.archive_popup);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.recover:
                        recoverData(id,
                                custom,
                                comm,
                                contact,
                                korobox,
                                money,
                                preMoney,
                                fDate,
                                lDate);
                        return true;
                    default:
                        return false;
                }

            }
        });
        popupMenu.show();
    }

    private void recoverData(String id,
                             String custom,
                             String comm,
                             String contact,
                             String korobox,
                             String money,
                             String preMoney,
                             String fDate,
                             String lDate){
        Order order = new Order(id,
                custom,
                comm,
                contact,
                korobox,
                money,
                preMoney,
                fDate,
                lDate);
        dbOrder.child(id).setValue(order).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(ShowArchiveActivity.this, id+" восстановлен в базу",
                            Toast.LENGTH_SHORT).show();
                    //удалить из архива
                    dbArchive.child(id).removeValue();
                    Intent i = new Intent(ShowArchiveActivity.this, OrderActivity.class);
                    startActivity(i);
                } else {
                    Toast.makeText(ShowArchiveActivity.this, "Произошла ошибка(((",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getIntentOrder() {
        Intent i =getIntent();
        if (i != null) {
            orderID.setText(i.getStringExtra("id_archive"));
            customer.setText(i.getStringExtra("customer_archive"));
            comment.setText(i.getStringExtra("comment_archive"));
            contacts.setText(i.getStringExtra("contact_archive"));
            box.setText(i.getStringExtra("box_archive"));
            cost.setText(i.getStringExtra("cost_archive"));
            precost.setText(i.getStringExtra("precost_archive"));
            firstDate.setText(i.getStringExtra("fDate_archive"));
            lastDate.setText(i.getStringExtra("lDate_archive"));
        }
    }

}