package com.example.chapter3.homework;


import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.chapter3.homework.Cycler.*;

public class PlaceholderFragment extends Fragment {
    private RecyclerView recyclerView;
    private MyAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private GridLayoutManager gridLayoutManager;
    private View view;
    private AnimatorSet animatorSet;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO ex3-3: 修改 fragment_placeholder，添加 loading 控件和列表视图控件
        view = inflater.inflate(R.layout.fragment_placeholder, container, false);
        initView();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getView().postDelayed(new Runnable() {
            @Override
            public void run() {
                // 这里会在 5s 后执行
                // TODO ex3-4：实现动画，将 lottie 控件淡出，列表数据淡入
                View target1, target2;
                target1 = view.findViewById(R.id.animation_view_0);
                ObjectAnimator animator1 = ObjectAnimator.ofFloat(target1, "Alpha", 1.0f, 0.0f);
                animator1.setDuration(300);

                target2 = view.findViewById(R.id.recycler);
                target2.setAlpha(0);
                target2.setVisibility(View.VISIBLE);
                ObjectAnimator animator2 = ObjectAnimator.ofFloat(target2, "Alpha", 0.0f, 1.0f);
                animator2.setDuration(300);

                animatorSet = new AnimatorSet();
                animatorSet.playTogether(animator1, animator2);
                animatorSet.start();
            }
        }, 5000);
    }

    private void initView() {
        //获取实例
        recyclerView = view.findViewById(R.id.recycler);
        //更改数据时不会变更宽高
        recyclerView.setHasFixedSize(true);
        //创建线性布局管理器
        layoutManager = new LinearLayoutManager(getActivity());
        //创建格网布局管理器
        gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        //设置布局管理器
        recyclerView.setLayoutManager(layoutManager);
        //创建Adapter
        mAdapter = new MyAdapter(TestDataSet.getData());
        //设置Adapter每个item的点击事件
//        mAdapter.setOnItemClickListener((MyAdapter.IOnItemClickListener) getActivity());
        //设置Adapter
        recyclerView.setAdapter(mAdapter);
        //分割线
//        LinearItemDecoration itemDecoration = new LinearItemDecoration(Color.GRAY);
//        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        //动画
        DefaultItemAnimator animator = new DefaultItemAnimator();
        animator.setAddDuration(3000);
        recyclerView.setItemAnimator(animator);
    }

}
