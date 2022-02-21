package com.trofimm.decorate68;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.trofimm.decorate68.service.MyFireBaseInstanceService;

import java.util.HashMap;

public class ShowOrderActivity extends AppCompatActivity {
    private DatabaseReference dbOrder, dbArchive;
    private EditText firstDate, lastDate, customer, comment, contacts, cost, precost, box;
    private LinearLayout checkedBox;
    private CheckBox standart, woody;
    private TextView orderID;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_order);
        init();
        getIntentOrder();

        box.setShowSoftInputOnFocus(false);
        box.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    checkedBox.setVisibility(View.VISIBLE);
                } else {
                    checkedBox.setVisibility(View.GONE);
                }
            }
        });

        Dialog calendar = new Dialog(ShowOrderActivity.this);
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
                View focus = ShowOrderActivity.this.getCurrentFocus();
                if (focus == firstDate)
                    firstDate.setText(selectedDate);
                else if (focus == lastDate)
                    lastDate.setText(selectedDate);
                calendar.dismiss();
            }
        });

        firstDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                firstDate.setShowSoftInputOnFocus(false);
                if (hasFocus)
                    calendar.show();
            }
        });

        lastDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                lastDate.setShowSoftInputOnFocus(false);
                if (hasFocus)
                    calendar.show();
            }
        });

        comment.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (view.getId() ==R.id.Comment) {
                    view.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction()&MotionEvent.ACTION_MASK){
                        case MotionEvent.ACTION_UP:
                            view.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }
                }
                return false;
            }
        });

        if (box.getText().toString().equals("Стандартная картонная коробка")) {
            standart.setChecked(true);
        } else
            if (box.getText().toString().equals("Коробка из дерева")) {
            woody.setChecked(true);
        }

        standart.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    woody.setChecked(false);
                    box.setText("Стандартная картонная коробка");
                    checkedBox.setVisibility(View.GONE);
                } else {
                    woody.setChecked(true);
                    box.setText("Коробка из дерева");
                    checkedBox.setVisibility(View.GONE);
                }
            }
        });

        woody.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    standart.setChecked(false);
                    box.setText("Коробка из дерева");
                    checkedBox.setVisibility(View.GONE);
                } else {
                    standart.setChecked(true);
                    box.setText("Стандартная картонная коробка");
                    checkedBox.setVisibility(View.GONE);
                }
            }
        });
    }

    private void init() {
        dbOrder = FirebaseDatabase.getInstance().getReference("Orders");
        dbArchive = FirebaseDatabase.getInstance().getReference("Archive");
        firstDate = findViewById(R.id.firstDate);
        lastDate = findViewById(R.id.lastDate);
        customer = findViewById(R.id.Customer);
        comment = findViewById(R.id.Comment);
        contacts = findViewById(R.id.Contacts);
        cost = findViewById(R.id.cost);
        precost = findViewById(R.id.precost);
        box = findViewById(R.id.Box);
        checkedBox = findViewById(R.id.boxLayout);
        standart = findViewById(R.id.Standart);
        woody = findViewById(R.id.Woody);
        orderID = findViewById(R.id.orderID);
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
        popupMenu.inflate(R.menu.order_popup);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.addChanges:
                        updateData(id,
                                custom,
                                comm,
                                contact,
                                korobox,
                                money,
                                preMoney,
                                fDate,
                                lDate);
                        return true;
                    case R.id.toArchive:
                        toArchive(id,
                                custom,
                                comm,
                                contact,
                                korobox,
                                money,
                                preMoney,
                                fDate,
                                lDate);
                        return true;
                    case R.id.delete:
                        removeData(id);
                    default:
                        return false;
                }

            }
        });
        popupMenu.show();
    }

    private void getIntentOrder() {
        Intent i =getIntent();
        if (i != null) {
            orderID.setText(i.getStringExtra("id_order"));
            customer.setText(i.getStringExtra("customer"));
            comment.setText(i.getStringExtra("comm"));
            contacts.setText(i.getStringExtra("contacts"));
            box.setText(i.getStringExtra("box"));
            cost.setText(i.getStringExtra("cost"));
            precost.setText(i.getStringExtra("precost"));
            firstDate.setText(i.getStringExtra("fDate"));
            lastDate.setText(i.getStringExtra("lDate"));
        }
    }

    private void toArchive(String id,
                             String custom,
                             String comm,
                             String contact,
                             String korobox,
                             String money,
                             String preMoney,
                             String fDate,
                             String lDate) {
        Archive newArchive = new Archive(id,
                custom,
                comm,
                contact,
                korobox,
                money,
                preMoney,
                fDate,
                lDate);
        String titleText = "Заказ отправлен в архив";
        String bodyText = id + ". Заказчик: " + custom;
        dbArchive.child(id).setValue(newArchive).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(ShowOrderActivity.this, id+" отправлен в архив",
                            Toast.LENGTH_SHORT).show();
                    //удалить из заказов
                    dbOrder.child(id).removeValue();
                    Intent i = new Intent(ShowOrderActivity.this, OrderActivity.class);
                    startActivity(i);
                    //notification
                    HashMap<String, String> dataSet = new HashMap<>();
                    dataSet.put("title", titleText);
                    dataSet.put("body", bodyText);
                    MyFireBaseInstanceService.sendPushToSingleInstance(ShowOrderActivity.this, dataSet);
                } else {
                    Toast.makeText(ShowOrderActivity.this, "Произошла ошибка(((",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateData(String id,
                            String custom,
                            String comm,
                            String contact,
                            String korobox,
                            String money,
                            String preMoney,
                            String fDate,
                            String lDate) {

        HashMap<String, Object> Order = new HashMap<>();
        Order.put("id_order", id);
        Order.put("customer", custom);
        Order.put("comment", comm);
        Order.put("contact", contact);
        Order.put("korobox", korobox);
        Order.put("cost", money);
        Order.put("preCost", preMoney);
        Order.put("firstDate", fDate);
        Order.put("lastDate", lDate);

        String titleText = "Изменены данные заказа";
        String bodyText = id + ". Заказчик: " + custom;

        dbOrder.child(id).updateChildren(Order).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {

                    //notification
                    HashMap<String, String> dataSet = new HashMap<>();
                    dataSet.put("title", titleText);
                    dataSet.put("body", bodyText);
                    MyFireBaseInstanceService.sendPushToSingleInstance(ShowOrderActivity.this, dataSet);

                    Toast.makeText(ShowOrderActivity.this, "Запись изменена)))", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(ShowOrderActivity.this, OrderActivity.class);
                    startActivity(i);
                } else {
                    Toast.makeText(ShowOrderActivity.this, "Не получилось изменить(((", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void removeData(String id){
        String title = "Вы уверены?";
        String message = "Удалить "+id+" из списка заказов?\nВосстановить нельзя.";
        String button1String = "Да, уверен(-а)";
        String button2String = "Нет, я случайно";
        AlertDialog.Builder builder = new AlertDialog.Builder(ShowOrderActivity.this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(button1String, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dbOrder.child(id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(ShowOrderActivity.this, "Запись удалена.\nВосстановлению не подлежит"
                                ,Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(ShowOrderActivity.this, OrderActivity.class);
                        startActivity(i);
                    }
                });
            }
        });
        builder.setNegativeButton(button2String, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(ShowOrderActivity.this, "В следующий раз смотри куда жмешь))"
                        ,Toast.LENGTH_SHORT).show();
            }
        });
        builder.setCancelable(true);
        builder.show();
    }
}