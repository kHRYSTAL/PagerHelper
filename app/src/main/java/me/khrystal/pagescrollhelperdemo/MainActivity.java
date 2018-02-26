package me.khrystal.pagescrollhelperdemo;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import me.khrystal.widget.PageScrollHelper;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Button button;
    private Context context;
    private int position;
    private List<Integer> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        context = this;
        data = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            data.add(i);
        }
        final PageScrollHelper helper = new PageScrollHelper();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        button = (Button) findViewById(R.id.btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                helper.setUpRecyclerView(recyclerView, position);
            }
        });

        position = 0;
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        helper.setUpRecyclerView(recyclerView, position);
        recyclerView.setAdapter(new A());


        helper.setOnPageChangeListener(new PageScrollHelper.onPageChangeListener() {
            @Override
            public void onPageChange(int index) {
                position = index;
            }
        });
    }

    class A extends RecyclerView.Adapter<VH> {

        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            VH vh = new VH(LayoutInflater.from(context).inflate(R.layout.item, parent, false));
            return vh;
        }

        @Override
        public void onBindViewHolder(VH holder, int position) {
            if (position % 2 == 0)
                holder.bindView(Color.parseColor("#ff0000"), data.get(position));
            else
                holder.bindView(Color.parseColor("#00ff00"), data.get(position));
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    class VH extends RecyclerView.ViewHolder {

        RelativeLayout relativeLayout;
        TextView textView;

        public VH(View itemView) {
            super(itemView);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.layout_item);
            textView = (TextView) itemView.findViewById(R.id.item_tv);
        }

        public void bindView(int color, int position) {
            relativeLayout.setBackgroundColor(color);
            textView.setText("position:" + position);
        }
    }


}
