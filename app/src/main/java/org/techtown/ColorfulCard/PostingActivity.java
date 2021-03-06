package org.techtown.ColorfulCard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.util.Log.d;

public class PostingActivity extends AppCompatActivity {

    Context mContext;
    public static Posting posting;
    private TextView pid,pcontent,pdate;
    public static TextView hcnt,ccnt,vcnt;
    private ImageButton deleteBt;

    private String userID;
    private ArrayList<Comment> comments = new ArrayList<>();
    private ArrayList<Ccomment> ccomments = new ArrayList<>();
    private MainHandler handler;
    private ArrayList<DataItem.CommentData> dataList = new ArrayList<>();;
    private RecyclerView recyclerView;
    private CmentListAdapter adapter;
    public  boolean isHeartPosting = false;
    public  static boolean isHeartPress=false;

    public static Button grayheartBt;
    public static Button pinkheartBt;
    public static Button cmentBt;
    public  ImageButton sendBt;
    public static EditText input1;
    public static ImageButton sendBt_Ccment;

    private String prevActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posting);
        posting= (Posting) getIntent().getSerializableExtra("choicePosting");
        userID= getIntent().getStringExtra("userID");
        prevActivity= getIntent().getStringExtra("prevActivity");

        d("tag",userID+"???????????????");
        mContext= this.getApplicationContext();

        pid=findViewById(R.id.pid);
        pcontent= findViewById(R.id.pcontent);
        pdate=findViewById(R.id.pdate);
        hcnt=findViewById(R.id.hcnt);
        ccnt=findViewById(R.id.ccnt);
        vcnt=findViewById(R.id.vcnt);
        deleteBt= findViewById(R.id.deleteBt);

        grayheartBt = (Button)findViewById(R.id.grayHeart_btn);
        pinkheartBt = (Button)findViewById(R.id.pintHeart_btn);
        sendBt = (ImageButton)findViewById(R.id.imageButton2);
        sendBt_Ccment= (ImageButton)findViewById(R.id.imageButton3);
        cmentBt = (Button)findViewById(R.id.comment_btn);
        input1 = (EditText) findViewById(R.id.input1);

        recyclerView= findViewById(R.id.recyclerView4);

        LinearLayoutManager manager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);

        recyclerView.setLayoutManager(manager); // LayoutManager ??????

        pid.setText(posting.getPid());
        pcontent.setText(posting.getPcontent());
        pdate.setText(posting.getPdate());
        hcnt.setText(String.valueOf(posting.getHcnt()));
        ccnt.setText(String.valueOf(posting.getCcnt()));
        vcnt.setText(String.valueOf(posting.getVcnt()+1));



        grayheartBt.setOnClickListener(new View.OnClickListener() {
            //???????????? ?????????
            @Override
            public void onClick(View v)
            {
                grayheartBt.setVisibility(View.INVISIBLE);
                pinkheartBt.setVisibility(View.VISIBLE);

                posting.addHcnt(+1);
                hcnt.setText(String.valueOf(posting.getHcnt()));

                isHeartPress = true;
            }
        });

        pinkheartBt.setOnClickListener(new View.OnClickListener() {
            //???????????? ?????????
            @Override
            public void onClick(View v)
            {
                grayheartBt.setVisibility(View.VISIBLE);
                pinkheartBt.setVisibility(View.INVISIBLE);

                posting.addHcnt(-1);
                hcnt.setText(String.valueOf(posting.getHcnt()));

                isHeartPress = false;
            }
        });


        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        //???????????? ????????? ?????????+????????? ?????????
        cmentBt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                input1.post(new Runnable() {
                    @Override
                    public void run() {

                        grayheartBt.setVisibility(View.INVISIBLE);
                        pinkheartBt.setVisibility(View.INVISIBLE);
                        cmentBt.setVisibility(View.GONE);

                        input1.setVisibility(View.VISIBLE);
                        sendBt.setVisibility(View.VISIBLE);
                        input1.setFocusableInTouchMode(true);
                        input1.requestFocus();
                        InputMethodManager imm= (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(input1, 0);
                    }
                });
            }

        });


        sendBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String cment = input1.getText().toString();

                if (cment.equals("")) {

                    Toast.makeText(getApplicationContext(), "????????? ??????????????????", Toast.LENGTH_SHORT).show();
                }
                else {

                    InsertCmentThread insertCmentThread= new InsertCmentThread(posting.getPno(), cment);
                    insertCmentThread.start();

                    if(isHeartPress){
                        pinkheartBt.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        grayheartBt.setVisibility(View.INVISIBLE);
                    }

                    cmentBt.setVisibility(View.VISIBLE);
                    input1.setVisibility(View.GONE);
                    input1.setText("");
                    sendBt.setVisibility(View.GONE);

                    imm.hideSoftInputFromWindow(input1.getWindowToken(), 0);
                }
            }
        });


        handler = new MainHandler();

        if(posting.getPid().equals(userID)){


            deleteBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(PostingActivity.this);
                    builder.setMessage("???????????? ?????? ?????????????????????????");
                    builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            DeletePostingThread  thread = new DeletePostingThread(posting.getPno());
                            thread.start();
                        }
                    });
                    builder.setNegativeButton("??????",null);
                    builder.show();
                }
            });

            deleteBt.setVisibility(View.VISIBLE);
            //?????? ????????? ???????????? ?????? ?????????
        }


        //????????? ??? ?????????????????? ?????? ?????? ?????????????????? ??????????????????.
        if(posting.getCcnt()>0){

            GetCmentsThread getCmentsThread= new GetCmentsThread(posting.getPno());
            getCmentsThread.start();
        }
        else
        {
            //?????? ?????????????????? ?????????.
            d("tag","???????????? ?????? ????????????");
            adapter= new CmentListAdapter(dataList,userID);
            recyclerView.setAdapter(adapter);
            recyclerView.setVisibility(View.VISIBLE);

        }

        if(posting.getHcnt()>0) {
            //????????? ??? ?????????????????? ???????????? ????????? ????????? ???????????? ??????????????????.
            CheckHeartPostingThread checkHeartThread = new CheckHeartPostingThread(posting.getPno());
            checkHeartThread.start();
        }
    }// onCreate()..

    class GetCmentsThread extends Thread{

        int pno;
        public GetCmentsThread(int pno){
            this.pno=pno;
        }

        @Override
        public void run() {
            super.run();

            Message message = handler.obtainMessage();

            Server server = new Server();
            RetrofitService service = server.getRetrofitService();
            Call<List<Comment>> call = service.getComment(pno);

            call.enqueue(new Callback<List<Comment>>() {
                @Override
                public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {

                    if(response.isSuccessful()){

                        comments= (ArrayList<Comment>)response.body();
                        message.what= StateSet.BoardMsg.MSG_SUCCESS_GET_CMENTS;
                        handler.sendMessage(message);
                    }
                }

                @Override
                public void onFailure(Call<List<Comment>> call, Throwable t) {
                    message.what= StateSet.BoardMsg.MSG_FAIL;
                    handler.sendMessage(message);

                }
            });

        }
    }

    class GetCcmentsThread extends Thread {

        private int pno;
        private int cno;

        public GetCcmentsThread(int pno, int cno){

            this.pno=pno;
            this.cno=cno;
        }

        @Override
        public void run() {
            super.run();

            Message message = handler.obtainMessage();

            Server server= new Server();
            RetrofitService service= server.getRetrofitService();
            Call<List<Ccomment>> call= service.getCcomment(pno);

            call.enqueue(new Callback<List<Ccomment>>() {
                @Override
                public void onResponse(Call<List<Ccomment>> call, Response<List<Ccomment>> response) {

                    if(response.isSuccessful()){

                        List<Ccomment> results= response.body();
                        ccomments= (ArrayList<Ccomment>)results;
                        message.what= StateSet.BoardMsg.MSG_SUCCESS_GET_CCMENTS;
                        handler.sendMessage(message);
                    }
                }
                @Override
                public void onFailure(Call<List<Ccomment>> call, Throwable t) {
                    message.what= StateSet.BoardMsg.MSG_FAIL;
                    handler.sendMessage(message);
                }
            });


        }

    }

    class CheckHeartPostingThread extends Thread{

        private int pno;

        public CheckHeartPostingThread(int pno){

            this.pno=pno;
        }

        @Override
        public void run() {
            super.run();

            Message message= handler.obtainMessage();

            Server server = new Server();
            RetrofitService service= server.getRetrofitService();
            Call<List<Integer>> call = service.getHeartPress(userID);

            call.enqueue(new Callback<List<Integer>>() {
                @Override
                public void onResponse(Call<List<Integer>> call, Response<List<Integer>> response) {

                    if(response.isSuccessful()){
                        List<Integer> heartPostings= response.body();

                        if(heartPostings.isEmpty()){
                            return;     //???????????? ?????? ??????
                        }

                        for(Integer results: heartPostings){
                            if(results.equals(pno)){
                                //???????????? ????????????
                                message.what= StateSet.BoardMsg.MSG_SUCCESS_HEARTPRESS;
                                handler.sendMessage(message);
                                d("tag","???????????????.");

                                break;
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<Integer>> call, Throwable t) {

                    message.what= StateSet.BoardMsg.MSG_FAIL;
                    handler.sendMessage(message);

                }
            });
        }
    }

    class InsertCmentThread extends Thread{

        private int pno;
        private String cment;
        private int cno=1;

        public InsertCmentThread(int pno, String cment){

            this.pno=pno;
            this.cment=cment;
            d("tag",comments.toArray()+"");
            if(comments.isEmpty()){
                d("tag","empty??????");
                cno=1;
            }else{

                for(Comment comment: comments){         //????????? cno??????
                    if(comment.getCno()>cno)
                    {
                        cno=comment.getCno();
                    }
                }
                cno++;
            }
        }

        @Override
        public void run() {
            super.run();

            Message message= handler.obtainMessage();

            Server server = new Server();
            RetrofitService service= server.getRetrofitService();
            d("tag",pno+" "+cno+" "+ userID+" "+cment+"???????????????");
            Call<Comment>call = service.postComment(pno,cno,userID,cment);

            call.enqueue(new Callback<Comment>() {
                @Override
                public void onResponse(Call<Comment> call, Response<Comment> response) {

                    Log.d("tag",response.errorBody()+" ");

                    if(response.isSuccessful()){

                        Comment result= (Comment)response.body();

                        d("tag",result.getCno()+"?????????");
                        d("tag","???????????????");
                        updateCmentCnt(pno,"plus");

                        comments.add(result);
                        dataList.add(new DataItem.CommentData(result,StateSet.ViewType.comment));

                        message.what= StateSet.BoardMsg.MSG_SUCCESS_INSERT_CMENT;
                        handler.sendMessage(message);

                    }
                }

                @Override
                public void onFailure(Call<Comment> call, Throwable t) {
                    message.what= StateSet.BoardMsg.MSG_FAIL;
                    handler.sendMessage(message);
                }
            });

        }
    }

    class DeletePostingThread extends Thread{

        int pno;
        public DeletePostingThread(int pno){
            this.pno=pno;
        }

        @Override
        public void run() {
            super.run();

            Message message = handler.obtainMessage();

            Server server= new Server();
            RetrofitService service= server.getRetrofitService();
            Call<Integer> call= service.deleteBoardPosting(pno);

            call.enqueue(new Callback<Integer>() {
                @Override
                public void onResponse(Call<Integer> call, Response<Integer> response) {

                    if(response.isSuccessful()){
                        Integer result= response.body();

                        if(result.intValue()==1){

                            message.what= StateSet.BoardMsg.MSG_SUCCESS_DEL_POSTING;
                            handler.sendMessage(message);

                        }else{
                            message.what= StateSet.BoardMsg.MSG_FAIL;
                            handler.sendMessage(message);
                        }
                    }
                }

                @Override
                public void onFailure(Call<Integer> call, Throwable t) {
                    message.what= StateSet.BoardMsg.MSG_FAIL;
                    handler.sendMessage(message);

                }
            });

        }
    }

    @Override
    public void onBackPressed() {

        if(input1.getVisibility()==View.VISIBLE){

            if(isHeartPress){   //????????? ??????
                pinkheartBt.setVisibility(View.VISIBLE);
                grayheartBt.setVisibility(View.INVISIBLE);
            }
            else
            {
                grayheartBt.setVisibility(View.VISIBLE);
                pinkheartBt.setVisibility(View.INVISIBLE);
            }
            cmentBt.setVisibility(View.VISIBLE);
            input1.setVisibility(View.INVISIBLE);
            input1.setText("");
            sendBt.setVisibility(View.INVISIBLE);
        }
        else {

            //????????????,????????? ????????????????????? ????????????
            updatePostingOfBoard(posting.getPno());


            //?????? ????????? ????????? ??????????????????
            if(prevActivity.equals("SearchBoardActivity")){

                isHeartPress=false;
                //????????? ????????? ?????? ??????????????? ??????????????? ????????? ????????????.
                super.onBackPressed();
                finish();
                overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);

            }
            else {

                //????????? ???????????? ?????? ????????? ?????????????????? ?????????????????? boardActivity ?????????
                super.onBackPressed();

                isHeartPress=false;
                Intent intent= new Intent(PostingActivity.this, BoardActivity.class);
                intent.putExtra("userID",userID);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
                finish();
            }

        }
    }

    private void updateCmentCnt(int pno, String sign){

        Server server =new Server();
        RetrofitService service= server.getRetrofitService();
        Call<Integer> call= service.putCommentCnt(pno,sign);
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                d("tag","????????? 1?????????");
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {

            }
        });
    }

    private void updatePostingOfBoard(int pno){


        Server server =new Server();
        RetrofitService service= server.getRetrofitService();

        Call<Integer> call= service.putViewsCnt(posting.getPno());
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if(response.isSuccessful())
                {
                    //????????? 1??????
                    d("tag","????????? 1?????????");
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {

            }
        });

        //?????? ????????????
        Call<Integer> call2;
        Call<Integer> call3;

        if(isHeartPosting)  //?????? ???????????????
        {
            if(!isHeartPress)    //?????? ????????? ????????????
            {
                call2 = service.putHeartCnt(pno,"minus");
                call2.enqueue(new Callback<Integer>() {
                    @Override
                    public void onResponse(Call<Integer> call, Response<Integer> response) {
                        if(response.isSuccessful())
                        {
                            d("tag","hcnt--???");
                        }
                    }

                    @Override
                    public void onFailure(Call<Integer> call, Throwable t) {

                    }
                });

                call3= service.deleteHeartPress(pno,userID);
                call3.enqueue(new Callback<Integer>() {
                    @Override
                    public void onResponse(Call<Integer> call, Response<Integer> response) {
                        if(response.isSuccessful())
                        {
                            d("tag","???????????? ?????????");
                        }
                    }

                    @Override
                    public void onFailure(Call<Integer> call, Throwable t) {

                    }
                });

            }
        }else{
            //?????? ???????????? ?????????
            if(isHeartPress)    //?????? ????????? ????????????
            {
                call2=service.putHeartCnt(pno,"plus");
                call2.enqueue(new Callback<Integer>() {
                    @Override
                    public void onResponse(Call<Integer> call, Response<Integer> response) {
                        if(response.isSuccessful())
                        {
                            d("tag","hcnt++???");
                        }
                    }

                    @Override
                    public void onFailure(Call<Integer> call, Throwable t) {

                    }
                });

                call3= service.postHeartPress(pno,userID);
                call3.enqueue(new Callback<Integer>() {
                    @Override
                    public void onResponse(Call<Integer> call, Response<Integer> response) {
                        if(response.isSuccessful())
                        {
                            d("tag","???????????? ?????????");
                        }
                    }

                    @Override
                    public void onFailure(Call<Integer> call, Throwable t) {

                    }
                });

            }
        }
    }


    class MainHandler extends Handler{

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            switch(msg.what){
                case StateSet.BoardMsg.MSG_SUCCESS_GET_CMENTS:

                    //????????? ????????????
                    Boolean cmentWithCcment=false;
                    for(Comment cment:comments){

                        if(cment.getCccnt()>0){

                            GetCcmentsThread getCcmentsThread = new GetCcmentsThread(posting.getPno(),cment.getCno());
                            getCcmentsThread.start();
                            cmentWithCcment=true;
                            break; //???????????? ????????? ?????? ????????? ????????? ??? ???????????? ?????? ?????? ???????????? ?????? ??? ?????????
                        }
                    }

                    if(cmentWithCcment==false){ //????????? ????????? ?????? ?????? ?????? ?????????

                        for(Comment cment:comments){

                            dataList.add( new DataItem.CommentData(cment,StateSet.ViewType.comment));
                            d("tag","dataList??? cno:"+cment.getCno()+"?????????");
                            d("tag",cment.getCccnt()+"=cccnt");

                        }

                        recyclerView= findViewById(R.id.recyclerView4);
                        recyclerView.setVisibility(View.VISIBLE);
                        adapter=new CmentListAdapter(dataList,userID);
                        recyclerView.setAdapter(adapter);

                    }
                    break;

                case StateSet.BoardMsg.MSG_SUCCESS_GET_CCMENTS:

                    //????????? ???????????? dataList??? ?????????????????? ????????? ???????????? ?????? ???????????????.
                    //?????? ??? DB?????? ???????????? ????????? ?????? ??????????????? ?????? ??????????????? ????????? ?????? ??? ??????????????? ?????? ??? ????????? ???.  ---->????????? 12/01

                    for(Comment cment: comments){

                        dataList.add( new DataItem.CommentData(cment,StateSet.ViewType.comment));
                        d("tag","dataList??? cno:"+cment.getCno()+" ????????? cccnt="+cment.getCccnt());

                        for(Ccomment ccment: ccomments)
                        {
                            if(cment.getCno()==ccment.getCno()){
                                dataList.add(new DataItem.CommentData(ccment,StateSet.ViewType.ccomment));
                                d("tag", "dataList??? cno:" + ccment.getCno() + "??? ccno:" + ccment.getCcno() + " ?????????");

                            }
                        }
                    }

                    d("tag",dataList.size()+"");
                    //????????? ccnt ??? ????????? ???????????? ???????????? ???????????? ?????????????????? ???????????? ???????????? ???????????? ?????????
                    if(dataList.size()==posting.getCcnt())
                    {
                        //?????? ?????? ???????????????
                        d("tag","???????????????????????????");
                        recyclerView.setVisibility(View.VISIBLE);
                        adapter = new CmentListAdapter(dataList,userID);
                        recyclerView.setAdapter(adapter);

                    }
                    break;


                case StateSet.BoardMsg.MSG_SUCCESS_DEL_POSTING:

                    Toast.makeText(getApplicationContext(),"???????????? ?????????????????????.",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(PostingActivity.this,BoardActivity.class);
                    intent.putExtra("userID",userID);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
                    finish();
                    break;

                case StateSet.BoardMsg.MSG_SUCCESS_INSERT_CMENT:
                    //?????????????????? ???????????? ?????? ???????????? ?????? ????????????

                    ccnt.setText(String.valueOf((posting.getCcnt()+1)));
                    posting.addCcnt(+1);
                    adapter.notifyDataSetChanged();

                    break;


                case StateSet.BoardMsg.MSG_SUCCESS_HEARTPRESS:
                    d("tag","??????????????? ?????????");
                    isHeartPosting = true;
                    isHeartPress=true;
                    //????????? ?????????.. ?????? ????????? ????????? ????????????!
                    grayheartBt.setVisibility(View.INVISIBLE);
                    pinkheartBt.setVisibility(View.VISIBLE);
                    break;

                case StateSet.BoardMsg.MSG_FAIL:
                    Toast.makeText(getApplicationContext(),"??????????????? ???????????????.",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    static Boolean getHeartPressState(){
        return isHeartPress;
    }
}// MainActivity class..

