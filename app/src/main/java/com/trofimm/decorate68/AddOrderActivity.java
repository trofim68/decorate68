package com.trofimm.decorate68;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.trofimm.decorate68.service.MyFireBaseInstanceService;

import java.util.HashMap;

import static android.text.TextUtils.isEmpty;

public class AddOrderActivity extends AppCompatActivity {
    private DatabaseReference dbOrder, dbArchive;
    private DrawerLayout drawer;
    private EditText add_firstDate, add_lastDate, customer, add_comment, contacts, add_cost, add_precost;
    private CheckBox standart, woody;
    private TextView id_order;
    private int countOrders, countArchive, countFull;
    private String orderId;
    private CountRecords countRecords;
    private ImageView imageView;
    public static final int CAMERA_PERMISSION =100;
    public static final int REQUEST_IMAGE_CAPTURE =101;
    public static final int READ_STORAGE_PERMISSION =102;
    public static final int REQUEST_IMAGE_PICK =103 ;
    private Dialog mCameraDialog;
    private Uri mImageURI;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_order);
        init();
        FirebaseMessaging.getInstance().subscribeToTopic("private");
//        setTextToFields();

        Dialog calendar = new Dialog(AddOrderActivity.this);
        calendar.setTitle("Выбери дату и нажми на нее");
        calendar.setContentView(R.layout.calendar_dialog);
        calendar.setCancelable(false);
        CalendarView calendarView = calendar.findViewById(R.id.calendar);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                int mYear = year;
                int mMonth = month;
                int mDay = dayOfMonth;
                String selectedDate = new StringBuilder().append(mDay).append(".")
                        .append(mMonth + 1).append(".").append(mYear)
                        .append(" ").toString();
                View focus = AddOrderActivity.this.getCurrentFocus();
                if (focus == add_firstDate)
                    add_firstDate.setText(selectedDate);
                else if (focus == add_lastDate)
                    add_lastDate.setText(selectedDate);
                calendar.dismiss();
            }
        });

        add_firstDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                add_firstDate.setShowSoftInputOnFocus(false);
                if (hasFocus)
                    calendar.show();
            }
        });

        add_lastDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                add_lastDate.setShowSoftInputOnFocus(false);
                if (hasFocus)
                    calendar.show();
            }
        });

        dbOrder.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                countOrders = (int) snapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        dbArchive.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                countArchive = (int) snapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        standart.setChecked(true);

        standart.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    woody.setChecked(false);
                } else {
                    woody.setChecked(true);
                }
            }
        });

        woody.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    standart.setChecked(false);
                } else {
                    standart.setChecked(true);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        setTextToFields();
    }

    public void init(){
        drawer = findViewById(R.id.drawer);
        add_firstDate = findViewById(R.id.add_firstDate);
        add_lastDate = findViewById(R.id.add_lastDate);
        dbOrder = FirebaseDatabase.getInstance().getReference("Orders");
        dbArchive = FirebaseDatabase.getInstance().getReference("Archive");
        customer = findViewById(R.id.add_Customer);
        add_comment = findViewById(R.id.add_Comment);
        contacts = findViewById(R.id.add_Contacts);
        add_cost = findViewById(R.id.add_cost);
        add_precost = findViewById(R.id.add_precost);
        standart = findViewById(R.id.standart);
        woody = findViewById(R.id.woody);
        id_order = findViewById(R.id.id_order);
        countRecords = new CountRecords();
//        pictureLoadBtn = findViewById(R.id.btn_LoadImage);
//        imageView = findViewById(R.id.imageView);
    }

    public void onClickMenuOpen(View view) {
        drawer.openDrawer(GravityCompat.END);
    }

    public void onCloseNavMenuClick(MenuItem item) {
        drawer.closeDrawer(GravityCompat.END);
    }

    public void onAddOrderClick(MenuItem item) {
        Toast toast = Toast.makeText(getApplicationContext(),
                "Эта вкладка открыта!",
                Toast.LENGTH_SHORT);
        toast.show();
    }

    public void onOrdersInWorkClick(MenuItem item) {
        Intent i = new Intent(AddOrderActivity.this, OrderActivity.class);
        startActivity(i);
    }

    public void onArchiveFilesClick(MenuItem item) {
        Intent i = new Intent(AddOrderActivity.this, ArchiveActivity.class);
        startActivity(i);
    }

    private void setTextToFields() {
        dbArchive.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                countArchive =0;
                countArchive = (int) snapshot.getChildrenCount();
                Log.d("!!!!!!!!!!!!!!!!!!!!!!!", String.valueOf((int) snapshot.getChildrenCount()));
                Log.d("!!!!!!!!!!!!!!!!!!!!!!!countArchive", String.valueOf(countArchive));

                dbOrder.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        countOrders = 0;
                        countOrders = (int) snapshot.getChildrenCount();
                        Log.d("!!!!!!!!!!!!!!!!!!!!!!!", String.valueOf((int) snapshot.getChildrenCount()));
                        Log.d("!!!!!!!!!!!!!!!!!!!!!!!countOrders", String.valueOf(countOrders));

                        countFull = countRecords.countFull(countOrders, countArchive);
                        Log.d("!!!!!!!!!!!!!!tag2222222222", String.valueOf(countFull));
                        orderId = "Заказ № " + countFull;
                        id_order.setText(orderId);
                        customer.setText("");
                        add_comment.setText("");
                        contacts.setText("");
                        standart.setChecked(true);
                        add_cost.setText("");
                        add_precost.setText("");
                        add_firstDate.setText("");
                        add_lastDate.setText("");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void onClickBtnAddOrder(View view) {
        String box = null;
        if (standart.isChecked()){
            box = "Стандартная картонная коробка";
        } else if (woody.isChecked()) {
            box = "Коробка из дерева";
        }
        
        String id = id_order.getText().toString();
        String custom = String.valueOf(customer.getText());
        String comm = String.valueOf(add_comment.getText());
        String contact = String.valueOf(contacts.getText());
        String money = String.valueOf(add_cost.getText());
        String premoney = String.valueOf(add_precost.getText());
        String fDate = String.valueOf(add_firstDate.getText());
        String lDate = String.valueOf(add_lastDate.getText());
        if (!isEmpty(id) && !isEmpty(custom)
                && (!isEmpty(comm)) && (!isEmpty(contact))
                && (!isEmpty(money)) && (!isEmpty(premoney))
                && (!isEmpty(fDate)) && (!isEmpty(lDate))) {
            addNewOrder(id,
                    custom,
                    comm,
                    contact,
                    box,
                    money,
                    premoney,
                    fDate,
                    lDate);
        } else {
            Toast.makeText(AddOrderActivity.this, "Заполни все поля!",
                    Toast.LENGTH_SHORT).show();
        }
        setTextToFields();
    }

    public void onClickBtnCancelOrder(View view) {
        setTextToFields();
    }

    private void addNewOrder(String id,
                             String custom,
                             String comm,
                             String contact,
                             String box,
                             String money,
                             String preMoney,
                             String fDate,
                             String lDate) {
        Order newOrder = new Order(id,
                custom,
                comm,
                contact,
                box,
                money,
                preMoney,
                fDate,
                lDate);

        String titleText = "Добавлен новый заказ";
        String bodyText = id + ". Заказчик: " + custom;
        dbOrder.child(id).setValue(newOrder).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(AddOrderActivity.this, id+" добавлен в базу данных",
                            Toast.LENGTH_SHORT).show();


                    //notification
                    HashMap<String, String> dataSet = new HashMap<>();
                    dataSet.put("title", titleText);
                    dataSet.put("body", bodyText);
                    MyFireBaseInstanceService.sendPushToSingleInstance(AddOrderActivity.this, dataSet);

                    setTextToFields();
                } else {
                    Toast.makeText(AddOrderActivity.this, "Произошла ошибка(((",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
/*
    public void onClickBtnLoadImage(View view) {
        
    }

    public void onClickBtnMakePhoto(View view) {

    }*/

}