package com.example.room;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Database;

import com.example.room.RoomDatabase.DataBase;
import com.example.room.RoomDatabase.MyData;
import com.facebook.stetho.Stetho;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    MyAdapter myAdapter;
    MyData nowSelectedData;//取得在畫面上顯示中的資料內容

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this);}*/
        Stetho.initializeWithDefaults(this);//設置資料庫監視

        Button btCreate = findViewById(R.id.button_Create);
        Button btModify = findViewById(R.id.button_Modify);
        Button btClear = findViewById(R.id.button_Clear);
        EditText edName = findViewById(R.id.editText_Name);
        EditText edCountry = findViewById(R.id.editText_Country);
        EditText edDistrict = findViewById(R.id.editText_District);
        EditText edRoad = findViewById(R.id.editText_Road);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        //EditText edAge = findViewById(R.id.editText_age);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));//設置分隔線
        setRecyclerFunction(recyclerView);//設置RecyclerView左滑刪除

        /**=======================================================================================*/
        /**設置修改資料的事件*/
        btModify.setOnClickListener((v) -> {
            new Thread(() -> {
                if(nowSelectedData ==null) return;//如果目前沒前台沒有資料，則以下程序不執行

                String name = edName.getText().toString();
                String country = edCountry.getText().toString();
                String district = edDistrict.getText().toString();
                String road = edRoad.getText().toString();
                MyData data = new MyData(nowSelectedData.getId(), name, country, district, road);
                DataBase.getInstance(this).getDataUao().updateData(data);
                runOnUiThread(() -> {
                    edName.setText("");
                    edCountry.setText("");
                    edDistrict.setText("");
                    edRoad.setText("");
                    nowSelectedData = null;
                    myAdapter.refreshView();
                    Toast.makeText(this, "已更新資訊！", Toast.LENGTH_LONG).show();
                });
            }).start();

        });
        /**=======================================================================================*/
        /**清空資料*/
        btClear.setOnClickListener((v -> {
            edName.setText("");
            edCountry.setText("");
            edDistrict.setText("");
            edRoad.setText("");
            nowSelectedData = null;
        }));
        /**=======================================================================================*/
        /**新增資料*/
        btCreate.setOnClickListener((v -> {
            new Thread(() -> {

                String name = edName.getText().toString();
                String country = edCountry.getText().toString();
                String district = edDistrict.getText().toString();
                String road = edRoad.getText().toString();
                if (name.length() == 0) return;//如果名字欄沒填入任何東西，則不執行下面的程序
                MyData data = new MyData(name, country, district, road);
                DataBase.getInstance(this).getDataUao().insertData(data);
                runOnUiThread(() -> {
                    myAdapter.refreshView();
                    edName.setText("");
                    edCountry.setText("");
                    edDistrict.setText("");
                    edRoad.setText("");
                });
            }).start();
        }));
        /**=======================================================================================*/
        /**初始化RecyclerView*/
        new Thread(() -> {
            List<MyData> data = DataBase.getInstance(this).getDataUao().displayAll();
            myAdapter = new MyAdapter(this, data);
            runOnUiThread(() -> {
                recyclerView.setAdapter(myAdapter);
                /**===============================================================================*/
                myAdapter.setOnItemClickListener(new MyAdapter.OnItemClickListener() {//原本的樣貌
                    @Override
                    public void onItemClick(MyData myData) {}
                });
                /**===============================================================================*/
                /**取得被選中的資料，並顯示於畫面*/
                myAdapter.setOnItemClickListener((myData)-> {//匿名函式(原貌在上方)
                    nowSelectedData = myData;
                    edName.setText(myData.getName());
                    edCountry.setText(myData.getCountry());
                    edDistrict.setText(myData.getDistrict());
                    edRoad.setText(myData.getRoad());
                });
                /**===============================================================================*/
            });
        }).start();
        /**=======================================================================================*/

    }

    private static class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

        private List<MyData> myData;
        private Activity activity;
        private OnItemClickListener onItemClickListener;

        public MyAdapter(Activity activity, List<MyData> myData) {
            this.activity = activity;
            this.myData = myData;
        }
        /**建立對外接口*/
        public void setOnItemClickListener(OnItemClickListener onItemClickListener){
            this.onItemClickListener = onItemClickListener;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvTitle;
            View view;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvTitle = itemView.findViewById(android.R.id.text1);
                view = itemView;
            }
        }
        /**更新資料*/
        public void refreshView() {
            new Thread(()->{
                List<MyData> data = DataBase.getInstance(activity).getDataUao().displayAll();
                this.myData = data;
                activity.runOnUiThread(() -> {
                    notifyDataSetChanged();
                });
            }).start();
        }
        /**刪除資料*/
        public void deleteData(int position){
            new Thread(()->{
                DataBase.getInstance(activity).getDataUao().deleteData(myData.get(position).getId());
                activity.runOnUiThread(()->{
                    notifyItemRemoved(position);
                    refreshView();
                });
            }).start();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_1, null);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.tvTitle.setText(myData.get(position).getName());
            holder.view.setOnClickListener((v)->{
                onItemClickListener.onItemClick(myData.get(position));
            });

        }
        @Override
        public int getItemCount() {
            return myData.size();
        }
        /**建立對外接口*/
        public interface OnItemClickListener {
            void onItemClick(MyData myData);
        }

    }

    /**設置RecyclerView的左滑刪除行為*/
    private void setRecyclerFunction(RecyclerView recyclerView){
        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.Callback() {//設置RecyclerView手勢功能
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                return makeMovementFlags(0,ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                switch (direction){
                    case ItemTouchHelper.LEFT:
                    case ItemTouchHelper.RIGHT:
                        myAdapter.deleteData(position);
                        break;

                }
            }
        });
        helper.attachToRecyclerView(recyclerView);
    }
}