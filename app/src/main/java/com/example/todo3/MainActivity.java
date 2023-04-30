package com.example.todo3;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private Button addButton;

    private ArrayList<TodoItem> todoItems;
    private TodoAdapter todoAdapter;
    private EditText taskEditText;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.todo_listview);
        addButton = findViewById(R.id.add_button);
        taskEditText = findViewById(R.id.task_edittext);


        sharedPreferences = getSharedPreferences("todo", Context.MODE_PRIVATE);

        String json = sharedPreferences.getString("tasks", "");
        if (!json.isEmpty()) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<TodoItem>>() {
            }.getType();
            todoItems = gson.fromJson(json, type);
        } else {
            todoItems = new ArrayList<>();
        }

        todoAdapter = new TodoAdapter(this, todoItems);
        listView.setAdapter(todoAdapter);

//        addButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showAddDialog();
//            }
//        });
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String task = taskEditText.getText().toString();
                if (!task.equals("")) {
                    Calendar calendar = Calendar.getInstance();
                    TodoItem item = new TodoItem(task, calendar.getTime());
                    todoItems.add(item);
                    todoAdapter.notifyDataSetChanged();
                    saveTodoItems();
                    taskEditText.setText("");
                }
            }
        });

        addButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showAddDialog();
                return true;
            }
        });

    }
    private void addTask() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_task, null);

        final EditText taskEditText = dialogView.findViewById(R.id.task_edittext);
        final TextView dateTextView = dialogView.findViewById(R.id.due_date_textview);
        final TextView timeTextView = dialogView.findViewById(R.id.due_time_textview);

        final Calendar calendar = Calendar.getInstance();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setTitle("Add Task")
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String task = taskEditText.getText().toString();
                        calendar.set(Calendar.SECOND, 0); // Reset seconds to zero to avoid rounding errors

                        TodoItem item = new TodoItem(task, calendar.getTime());
                        todoItems.add(item);
                        todoAdapter.notifyDataSetChanged();
                        saveTodoItems();
                    }
                })
                .setNegativeButton("Cancel", null);

        final AlertDialog dialog = builder.create();

        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                calendar.set(Calendar.YEAR, year);
                                calendar.set(Calendar.MONTH, month);
                                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                dateTextView.setText(formatDate(calendar.getTime()) + " at " + formatTime(calendar.getTime()));
                            }
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        timeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                calendar.set(Calendar.MINUTE, minute);
                                timeTextView.setText(formatDate(calendar.getTime()) + " at " + formatTime(calendar.getTime()));
                            }
                        },
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        false);
                timePickerDialog.show();
            }
        });

        dialog.show();
    }

    private void showAddDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Task");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_task, null);
        final EditText taskEditText = dialogView.findViewById(R.id.task_edittext);
        final TextView dateTextView = dialogView.findViewById(R.id.due_date_textview);
        final TextView timeTextView = dialogView.findViewById(R.id.due_time_textview);

        final Calendar calendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                dateTextView.setText(formatDate(calendar.getTime()));
            }
        };
        final TimePickerDialog.OnTimeSetListener timeListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                timeTextView.setText(formatTime(calendar.getTime()));
            }
        };

        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(MainActivity.this, dateListener, calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        timeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(MainActivity.this, timeListener, calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE), false).show();
            }
        });

        builder.setView(dialogView);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String task = taskEditText.getText().toString();
                TodoItem item = new TodoItem(task, calendar.getTime());
                todoItems.add(item);
                todoAdapter.notifyDataSetChanged();
                saveTodoItems();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void saveTodoItems() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(todoItems);
        editor.putString("tasks", json);
        editor.apply();
    }

    private String formatDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy");
        return dateFormat.format(date);
    }

    private String formatTime(Date date) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        return timeFormat.format(date);
    }

    private class TodoAdapter extends ArrayAdapter<TodoItem> {

        public TodoAdapter(Context context, ArrayList<TodoItem> items) {
            super(context, 0, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_todo, parent, false);
            }

            final TodoItem item = getItem(position);

            TextView taskTextView = convertView.findViewById(R.id.task_textview);
            CheckBox doneCheckBox = convertView.findViewById(R.id.done_checkbox);
            TextView dateTextView = convertView.findViewById(R.id.date_textview);

            taskTextView.setText(item.getTask());
            doneCheckBox.setChecked(item.isDone());
            dateTextView.setText(formatDate(item.getDate()) + " at " + formatTime(item.getDate()));

            doneCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    item.setDone(!item.isDone());
                    todoAdapter.notifyDataSetChanged();
                    saveTodoItems();
                }
            });

            return convertView;
        }
    }
}