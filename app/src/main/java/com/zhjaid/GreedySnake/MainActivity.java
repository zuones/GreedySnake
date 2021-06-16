package com.zhjaid.GreedySnake;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class MainActivity extends Activity implements View.OnClickListener {

    private GreedySnakeView greedySnakeView;
    private RelativeLayout relativeLayout;
    private TextView score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);// 隐藏标题

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏

        Bmob.initialize(this, "8ba1e0266e3998d4cee9f43bfe9fb911");

        setContentView(R.layout.activity_main);

        initViews();

        greedySnakeView.setSleep(200);

        greedySnakeView.setSleepOne(1);

        greedySnakeView.setisOpenLine(false);

        greedySnakeView.setSnakeStyle(true);

        greedySnakeView.setMonitorGestures(true);

        greedySnakeView.setChangeListener(new GreedySnakeView.SpeedChangeListener() {
            @Override
            public void onSpeedChange(int speed, int num, int s) {
                score.setVisibility(View.VISIBLE);
                score.setText("当前速度:" + speed + "   吃掉食物:" + num + "    当前分数:" + s);
                // showToast("速度变化,当前速度" + speed + "   你吃掉了" + num + "个食物");
            }

            @Override
            public void onFinish(int num, int sco) {

                //showToast("游戏结束,你一共吃掉了" + num + "个食物");
                //greedySnakeView.againGame();
                //greedySnakeView.start();
                score.setVisibility(View.GONE);
                greedySnakeView.againGame();
                relativeLayout.setVisibility(View.VISIBLE);
                ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setMessage("正在结算");
                progressDialog.setCancelable(false);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                BmobQuery<Ranking> rankingBmobQuery = new BmobQuery<>();
                rankingBmobQuery.addWhereEqualTo("UserNike", getNike());
                rankingBmobQuery.setLimit(50);
                rankingBmobQuery.findObjects(new FindListener<Ranking>() {
                    @Override
                    public void done(List<Ranking> list, BmobException e) {
                        progressDialog.dismiss();
                        if (e == null) {
                            if (list.size() > 0) {//不是第一次玩游戏，已有游戏记录
                                Ranking ranking = list.get(0);
                                ranking.increment("frequency");
                                int rSceo = ranking.getScore();
                                if (rSceo < sco) {
                                    //这里代表已经打破记录
                                    ranking.setScore(sco);
                                    //showDialog("恭喜你打破了记录", "历史最高得分:" + ranking.getScore() + "\n本次得分：" + sco);
                                }
                                ranking.update(new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        if (rSceo < sco) {
                                            //这里代表已经打破记录
                                            //ranking.setScore(sco);
                                            showDialog("恭喜你打破了记录", "历史最高得分:" + rSceo + "\n本次得分：" + sco);
                                        } else {
                                            showDialog("本次得分", "本次游戏你一共吃掉了" + num + "个食物，获得" + sco + "分");
                                        }
                                    }
                                });
                            } else {//第一次玩游戏
                                //增加数据
                                Ranking ranking = new Ranking();
                                ranking.setUserNike(getNike());
                                ranking.setFrequency(1);
                                ranking.setScore(sco);
                                ranking.save(new SaveListener<String>() {
                                    @Override
                                    public void done(String s, BmobException e) {
                                        showDialog("本次得分", "本次游戏你一共吃掉了" + num + "个食物，获得" + sco + "分");
                                    }
                                });
                            }
                        } else {
                            showDialog("本次得分", "本次游戏你一共吃掉了" + num + "个食物，获得" + sco + "分");
                        }
                    }
                });
            }

            @Override
            public void onDirectionChange(GreedySnakeView.Direction direction) {
                // showToast("方向变化 " + direction.name() + " ");
            }
        });
        findViewById(R.id.startGame).setOnClickListener(this::onClick);
        findViewById(R.id.startRanking).setOnClickListener(this::onClick);
        findViewById(R.id.startRule).setOnClickListener(this::onClick);

        if (getNike() == null) {
            setNike();
        }
        //greedySnakeView.start();
    }

    private void initViews() {
        greedySnakeView = findViewById(R.id.greedySnakeView);
        relativeLayout = findViewById(R.id.bottomVidew);
        score = findViewById(R.id.score);
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.startRanking:
                showRanking();
                break;
            case R.id.startGame:
                relativeLayout.setVisibility(View.GONE);
                greedySnakeView.start();
                break;
            case R.id.startRule:
                showDialog("游戏规则", "开始游戏后通过手指滑动屏幕来移动蛇身，吃的食物越多，移动速度越快");
                break;
        }
    }

    private String SHAFILE = "FILESSSSSSSSSSBBB";
    private String NIKEKEY = "NAKEKEY";

    private void showRanking() {
        ListView listView = new ListView(this);

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在加载排行榜");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        //查询数据
        BmobQuery<Ranking> rankingBmobQuery = new BmobQuery<>();
        rankingBmobQuery.order("-score");
        rankingBmobQuery.setLimit(50);
        rankingBmobQuery.findObjects(new FindListener<Ranking>() {
            @Override
            public void done(List<Ranking> list, BmobException e) {
                progressDialog.dismiss();
                if (e == null) {
                    ArrayList<String> strings = new ArrayList<>();
                    for (Ranking ranking : list) {
                        strings.add(ranking.getUserNike() + "   最高得分" + ranking.getScore() + "游戏次数:" + ranking.getFrequency());
                    }
                    listView.setAdapter(new Adapter(list));
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this).setTitle("排行榜");
                    builder.setView(listView);
                    builder.setCancelable(false);
                    builder.setPositiveButton("了解", null);
                    builder.show();
                } else {
                    showDialog("加载失败errorcode:" + e.getErrorCode() + " msg:" + e.getMessage());
                }
            }
        });
    }

    private void showDialog(String title, String msg) {
        new AlertDialog.Builder(this).setTitle(title).setMessage(msg).setCancelable(false).setPositiveButton("知道了", null).show();

    }

    private void showDialog(String msg) {
        showDialog("提示", msg);
    }


    private String getNike() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHAFILE, MODE_PRIVATE);
        return sharedPreferences.getString(NIKEKEY, null);
    }

    private void setNike() {
        EditText editText = new EditText(this);
        editText.setGravity(Gravity.CENTER);
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        editText.setHint("输入你的昵称");
        AlertDialog show = new AlertDialog.Builder(this).setTitle("设置昵称").setView(editText).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setNegativeButton("退出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        }).setCancelable(false).show();
        show.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = editText.getText().toString();
                if (s.length() == 0 || s.length() > 200) {
                    showDialog("昵称不规范");
                    return;
                }
                SharedPreferences sharedPreferences = getSharedPreferences(SHAFILE, MODE_PRIVATE);
                sharedPreferences.edit().putString(NIKEKEY, s).commit();
                show.dismiss();
                showToast("设置成功");
            }
        });
    }

    //排行榜适配器
    class Adapter extends BaseAdapter {
        public Adapter(List<Ranking> list) {
            this.list = list;
        }

        private List<Ranking> list;

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(MainActivity.this).inflate(R.layout.item_ranking, parent, false);
            TextView title = convertView.findViewById(R.id.rankingTextView);
            Ranking ranking = list.get(position);

            if (position < 3) {
                title.setText(Html.fromHtml("<font color=red>排名" + (position + 1) + "</font>  用户：<font color=red>" + ranking.getUserNike()
                        + "</font><br>最高得分：<font color=green>" + ranking.getScore() +
                        "</font><br>游戏次数：<font color=green>" + ranking.getFrequency() + "</font>"));
            } else {
                title.setText(Html.fromHtml("排名" + (position + 1) + "  用户：<font color=red>" + ranking.getUserNike()
                        + "</font><br>最高得分：<font color=green>" + ranking.getScore() +
                        "</font><br>游戏次数：<font color=green>" + ranking.getFrequency() + "</font>"));
            }


            return convertView;
        }
    }
}
