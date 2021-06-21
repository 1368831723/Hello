package com.pwj.helloya;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.pwj.BaseActivity;
import com.pwj.bean.Customer;


import java.util.ArrayList;

public class ResultDetailList extends BaseActivity{

    private ArrayList<Customer> resultCustomerList;
    private SearchView searchView;
    private ListView resultListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_detail_list);

        resultCustomerList = (ArrayList<Customer>) getIntent().getSerializableExtra("resultlist");

        Log.d("+++++Jun++++", "信息接收成功，共有" + resultCustomerList.size() +"个客户");

        searchView = (SearchView) findViewById(R.id.searchView);
        resultListView = (ListView) findViewById(R.id.result_listView);
        resultListView.setTextFilterEnabled(true);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!TextUtils.isEmpty(newText)){
                    resultListView.setFilterText(newText);
                }else{
                    resultListView.clearTextFilter();
                }
                return true;
            }
        });



        final MyResultAdapter myResultAdapter = new MyResultAdapter(this, resultCustomerList);
        resultListView.setAdapter(myResultAdapter);

        resultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(ResultDetailList.this, InfoPathActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("customer_info", resultCustomerList.get(position));
            intent.putExtras(bundle);
            startActivity(intent);
            }
        });



    }

    public class MyResultAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<Customer> listCustomer;
        private LayoutInflater layoutInflater;

        MyResultAdapter(Context context, ArrayList<Customer> listCustomer) {
            this.context = context;
            this.listCustomer = listCustomer;
            this.layoutInflater = LayoutInflater.from(context);
        }

        public int getCount(){
            return listCustomer.size();
        }

        @Override
        public Object getItem(int position) {
            return listCustomer.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ResultViewHolder resultViewHolder = null;

            if(convertView == null) {
                convertView = layoutInflater.inflate(R.layout.result_company_name, null);
                resultViewHolder= new ResultViewHolder();
                resultViewHolder.textView = (TextView) convertView.findViewById(R.id.detail_result_textview);
                resultViewHolder.imageView = (ImageView) convertView.findViewById(R.id.detail_result_image);

                convertView.setTag(resultViewHolder);
            }else {
                resultViewHolder = (ResultViewHolder) convertView.getTag();
            }

            resultViewHolder.imageView.setImageResource(R.mipmap.more);
            resultViewHolder.textView.setText(listCustomer.get(position).getCompanyname());
            return convertView;
        }

    }

    class ResultViewHolder{
        TextView textView;
        ImageView imageView;
    }

//    public void onResume() {
//        super.onResume();
//        MobclickAgent.onPageStart("ResultDetailList"); //统计页面("MainScreen"为页面名称，可自定义)
//        MobclickAgent.onResume(this); //统计时长
//    }
//    public void onPause() {
//        super.onPause();
//        MobclickAgent.onPageEnd("ResultDetailList");
//        MobclickAgent.onPause(this); //统计时长
//    }

}
