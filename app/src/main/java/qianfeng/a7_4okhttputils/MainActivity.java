package qianfeng.a7_4okhttputils;

import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private PullToRefreshListView refreshListView;
    private List<String> list;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");

    private String lastUpdateTime = simpleDateFormat.format(new Date(System.currentTimeMillis()));


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case 0:
                    refreshListView.onRefreshComplete(); // 如果不加这行代码的话，动画是不会消失的，而且，所有动作都没有完成， 下一次的上拉和下拉的监听都是监听不到的！！
                    layoutProxy.setLastUpdatedLabel("最后一次刷新时间:" + lastUpdateTime);
                    adapter.notifyDataSetChanged();
                    break;
            }

        }
    };
    private ArrayAdapter<String> adapter;
    private ILoadingLayout layoutProxy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        refreshListView = ((PullToRefreshListView) findViewById(R.id.pullto_refresh_listview));


        initData();


        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);

        refreshListView.setMode(PullToRefreshBase.Mode.BOTH); // 设置模式，现在是可上拉加载，下拉刷新！ 默认不设置只是上拉加载

        refreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() { // 记住这里是 OnRefreshListener2
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) { // 下拉刷新得到数据时，才会调用这个方法

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SystemClock.sleep(2000);
                        list.add(0, "王五");
                        mHandler.sendEmptyMessage(0);
                        lastUpdateTime = simpleDateFormat.format(new Date(System.currentTimeMillis()));
                    }
                }).start();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SystemClock.sleep(2000);
                        list.add("李四");
                        mHandler.sendEmptyMessage(0);
                        lastUpdateTime = simpleDateFormat.format(new Date(System.currentTimeMillis()));
                    }
                }).start();
            }
        });

        layoutProxy = refreshListView.getLoadingLayoutProxy();

        // 设置刷新时的旋转动画的图片
        layoutProxy.setLoadingDrawable(new BitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher)));

        layoutProxy.setLastUpdatedLabel("最后一次刷新时间:" + lastUpdateTime);

        layoutProxy.setPullLabel("下拉文本"); // 第1、3段动画

        layoutProxy.setRefreshingLabel("正在加载数据的文本"); // 即第4段动画

        layoutProxy.setReleaseLabel("释放手指以刷新"); // 第2段动画

        layoutProxy.setTextTypeface(Typeface.createFromAsset(getAssets(),"mycustom.ttf"));// 设置ttf字体

        refreshListView.setAdapter(adapter);

    }

    private void initData() {

        list = new ArrayList<>();

        for (int i = 0; i < 50; i++) {
            list.add("张三:" + i);
        }
    }
}
