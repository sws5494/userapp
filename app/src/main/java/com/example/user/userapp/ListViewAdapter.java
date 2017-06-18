package com.example.user.userapp;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by user on 2017-04-17.
 */
public class ListViewAdapter extends BaseAdapter {
    LinearLayout linear_list;
    ListViewItem listViewItem;

    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<ListViewItem> listViewItemList = new ArrayList<ListViewItem>();

    // ListViewAdapter의 생성자
    public ListViewAdapter() {

    }

    public void remove() {
        int count = getCount();
        for (int i = 0; i < count; i++) {
            listViewItemList.remove(0);
        }
    }

    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {
        return listViewItemList.size();
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_item, parent, false);
        }

        linear_list = (LinearLayout) convertView.findViewById(R.id.linear_list);

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        TextView tv_start = (TextView) convertView.findViewById(R.id.tv_start);
        TextView descTextView = (TextView) convertView.findViewById(R.id.textView2);
        TextView tv_reason = (TextView) convertView.findViewById(R.id.tv_reason);
        TextView tv_allow = (TextView) convertView.findViewById(R.id.tv_allow);

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        try {
            listViewItem = listViewItemList.get(position);
        } catch (IndexOutOfBoundsException e) {
            Log.d("ERWEREWR", "WERWER");

        }


        if (position % 2 == 0) {
            linear_list.setBackgroundColor(Color.rgb(245, 245, 245));
        } else {
            linear_list.setBackgroundColor(Color.rgb(255, 255, 255));
        }

        tv_start.setText(listViewItem.getStart());
        descTextView.setText(listViewItem.getDesc());
        tv_reason.setText(listViewItem.getReason());
        tv_allow.setText(listViewItem.getAllow());

        if (listViewItem.getAllow().equals("대기")) {
            tv_allow.setTextColor(Color.rgb(250, 225, 0));
        } else if (listViewItem.getAllow().equals("승인")) {
            tv_allow.setTextColor(Color.rgb(32, 114, 69));
        } else if (listViewItem.getAllow().equals("거절")) {
            tv_allow.setTextColor(Color.rgb(210, 69, 37));
        }
        return convertView;
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position;
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position);
    }

    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void addItem(String _start, String desc, String _reason, String _allow) {
        ListViewItem item = new ListViewItem();

        item.setStart(_start);
        item.setDesc(desc);
        item.setReason(_reason);
        item.setAllow(_allow);
        listViewItemList.add(item);
    }
}