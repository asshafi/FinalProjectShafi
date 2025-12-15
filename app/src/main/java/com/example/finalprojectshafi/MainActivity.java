package com.example.finalprojectshafi;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalprojectshafi.adapter.TaskAdapter;
import com.example.finalprojectshafi.database.Task;
import com.example.finalprojectshafi.database.TaskDatabase;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TaskAdapter.OnTaskActionListener {

    RecyclerView recyclerView;
    FloatingActionButton fabAdd;
    SearchView searchView;
    PieChart pieChart;
    BarChart barChart;
    Button btnReminders;
    Spinner spinnerSort;
    TextView tvSortTitle;

    TaskAdapter adapter;
    TaskDatabase db;
    List<Task> taskList;
    String[] categories = {"Work", "Personal", "Shopping", "Health"};
    String[] priorities = {"High", "Medium", "Low"};
    String[] statuses = {"Pending", "Completed"};
    String[] sortOptions = {"Due Date", "Duration", "Money Spent", "Start Time", "End Time"};
    long startTime, endTime, dueDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initDatabase();
        initRecyclerView();
        initListeners();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        fabAdd = findViewById(R.id.fabAdd);
        searchView = findViewById(R.id.searchView);
        pieChart = findViewById(R.id.pieChart);
        barChart = findViewById(R.id.barChart);
        btnReminders = findViewById(R.id.btnReminders);
        spinnerSort = findViewById(R.id.spinnerSort);
        tvSortTitle = findViewById(R.id.tvSortTitle);

        ArrayAdapter<String> sortAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, sortOptions);
        spinnerSort.setAdapter(sortAdapter);
    }

    private void initDatabase() {
        db = TaskDatabase.getInstance(this);
    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        refreshData(); // Initial load which also calls loadCharts()
    }

    private void initListeners() {
        fabAdd.setOnClickListener(v -> showUpdateDialog(null, true));
        btnReminders.setOnClickListener(v -> startActivity(new Intent(this, ReminderActivity.class)));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (adapter != null) {
                    adapter.getFilter().filter(newText);
                }
                return true;
            }
        });

        spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                refreshData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    public void onEditClick(Task task) {
        showUpdateDialog(task, false);
    }

    @Override
    public void onDeleteClick(Task task, int position) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Task")
                .setMessage("Are you sure you want to delete this task?")
                .setPositiveButton("Delete", (d, w) -> {
                    db.taskDao().delete(task);
                    refreshData();
                    Snackbar.make(recyclerView, "Task deleted", Snackbar.LENGTH_LONG)
                            .setAction("Undo", view -> {
                                db.taskDao().insert(task);
                                refreshData();
                            }).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showUpdateDialog(final Task task, final boolean isNewTask) {
        View view = getLayoutInflater().inflate(R.layout.dialog_add_task, null);

        final EditText etTitle = view.findViewById(R.id.etTitle);
        final EditText etDescription = view.findViewById(R.id.etDescription);
        final Button btnStartTime = view.findViewById(R.id.btnStartTime);
        final Button btnEndTime = view.findViewById(R.id.btnEndTime);
        final Button btnDueDate = view.findViewById(R.id.btnDueDate);
        final EditText etMoney = view.findViewById(R.id.etMoney);
        final Spinner spinnerCategory = view.findViewById(R.id.spinnerCategory);
        final Spinner spinnerPriority = view.findViewById(R.id.spinnerPriority);
        final Spinner spinnerStatus = view.findViewById(R.id.spinnerStatus);

        if (isNewTask) {
            Calendar cal = Calendar.getInstance();
            startTime = cal.getTimeInMillis();
            endTime = cal.getTimeInMillis();
            dueDate = cal.getTimeInMillis();
            btnStartTime.setText("Start Time");
            btnEndTime.setText("End Time");
            btnDueDate.setText("Due Date");
        } else {
            etTitle.setText(task.title);
            etDescription.setText(task.description);
            etMoney.setText(String.valueOf(task.moneySpent));
            startTime = task.startTime;
            endTime = task.endTime;
            dueDate = task.dueDate;

            SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            btnStartTime.setText(sdfTime.format(startTime));
            btnEndTime.setText(sdfTime.format(endTime));

            SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            btnDueDate.setText(sdfDate.format(dueDate));
        }

        btnStartTime.setOnClickListener(v -> showTimePickerDialog(true, btnStartTime));
        btnEndTime.setOnClickListener(v -> showTimePickerDialog(false, btnEndTime));
        btnDueDate.setOnClickListener(v -> showDatePickerDialog(btnDueDate));

        spinnerCategory.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories));
        spinnerPriority.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, priorities));
        spinnerStatus.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, statuses));

        if (!isNewTask) {
            spinnerCategory.setSelection(Arrays.asList(categories).indexOf(task.category));
            spinnerPriority.setSelection(Arrays.asList(priorities).indexOf(task.priority));
            spinnerStatus.setSelection(Arrays.asList(statuses).indexOf(task.status));
        }

        new AlertDialog.Builder(this)
                .setTitle(isNewTask ? "Add New Task" : "Update Task")
                .setView(view)
                .setPositiveButton(isNewTask ? "Save" : "Update", (dialog, which) -> {
                    String title = etTitle.getText().toString();
                    String description = etDescription.getText().toString();
                    String category = spinnerCategory.getSelectedItem().toString();
                    String priority = spinnerPriority.getSelectedItem().toString();
                    String moneyString = etMoney.getText().toString();
                    double money = TextUtils.isEmpty(moneyString) ? 0.0 : Double.parseDouble(moneyString);
                    String status = spinnerStatus.getSelectedItem().toString();

                    if (isNewTask) {
                        Task newTask = new Task(title, description, status, category, priority, startTime, endTime, dueDate, money);
                        db.taskDao().insert(newTask);
                    } else {
                        task.title = title;
                        task.description = description;
                        task.category = category;
                        task.priority = priority;
                        task.startTime = startTime;
                        task.endTime = endTime;
                        task.dueDate = dueDate;
                        task.moneySpent = money;
                        task.status = status;
                        db.taskDao().update(task);
                    }
                    refreshData();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    public void refreshData() {
        String sortBy = spinnerSort.getSelectedItem().toString();
        tvSortTitle.setText("Sorted by: " + sortBy);
        switch (sortBy) {
            case "Duration":
                taskList = db.taskDao().getTasksSortedByPriorityThenDuration();
                break;
            case "Money Spent":
                taskList = db.taskDao().getTasksSortedByPriorityThenMoney();
                break;
            case "Start Time":
                taskList = db.taskDao().getTasksSortedByPriorityThenStartTime();
                break;
            case "End Time":
                taskList = db.taskDao().getTasksSortedByPriorityThenEndTime();
                break;
            default: // Due Date
                taskList = db.taskDao().getTasksSortedByPriorityAndDueDate();
                break;
        }
        if (adapter == null) {
            adapter = new TaskAdapter(this, taskList, this);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.list.clear();
            adapter.list.addAll(taskList);
            adapter.listFull.clear();
            adapter.listFull.addAll(taskList);
            adapter.notifyDataSetChanged();
        }
        loadCharts();
    }

    private void loadCharts() {
        loadPieChart();
        loadBarChart();
    }

    private void loadPieChart() {
        if (pieChart == null) return;
        int completed = db.taskDao().successCount();
        int pending = db.taskDao().failureCount();

        List<PieEntry> entries = new ArrayList<>();
        if (completed == 0 && pending == 0) {
            pieChart.clear();
            pieChart.invalidate();
            return;
        }
        entries.add(new PieEntry(completed, "Completed"));
        entries.add(new PieEntry(pending, "Pending"));

        PieDataSet dataSet = new PieDataSet(entries, "Task Status");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(12f);

        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.getDescription().setEnabled(false);
        pieChart.animateY(1000);
        pieChart.invalidate();
    }

    private void loadBarChart() {
        if (barChart == null) return;
        List<BarEntry> entries = new ArrayList<>();
        boolean hasData = false;
        for (int i = 0; i < categories.length; i++) {
            double money = db.taskDao().getMoneySpentByCategory(categories[i]);
            if (money > 0) {
                hasData = true;
            }
            entries.add(new BarEntry(i, (float) money));
        }

        if (!hasData) {
            barChart.clear();
            barChart.invalidate();
            return;
        }

        BarDataSet dataSet = new BarDataSet(entries, "Money Spent per Category");
        dataSet.setColors(ColorTemplate.PASTEL_COLORS);
        dataSet.setValueTextSize(12f);

        BarData data = new BarData(dataSet);
        barChart.setData(data);
        barChart.getDescription().setEnabled(false);
        barChart.animateY(1000);
        barChart.invalidate();
    }

    private void showTimePickerDialog(final boolean isStart, Button button) {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog.OnTimeSetListener listener = (view, hourOfDay, minute) -> {
            Calendar selectedTime = Calendar.getInstance();
            selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            selectedTime.set(Calendar.MINUTE, minute);
            if (isStart) {
                startTime = selectedTime.getTimeInMillis();
            } else {
                endTime = selectedTime.getTimeInMillis();
            }
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            button.setText(sdf.format(selectedTime.getTime()));
        };
        new TimePickerDialog(this, listener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
    }

    private void showDatePickerDialog(Button button) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener listener = (view, year, month, dayOfMonth) -> {
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(year, month, dayOfMonth);
            dueDate = selectedDate.getTimeInMillis();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            button.setText(sdf.format(selectedDate.getTime()));
        };
        new DatePickerDialog(this, listener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshData();
    }
}
