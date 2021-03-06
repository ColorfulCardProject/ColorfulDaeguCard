package org.techtown.ColorfulCard;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.app.AlertDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CmentListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<DataItem.CommentData> cmentDataList=null;
    private Posting posting= PostingActivity.posting;
    private String userID;
    public CmentListAdapter(ArrayList<DataItem.CommentData> cmentDataList, String userID){
        this.cmentDataList= cmentDataList;
        this.userID=userID;

        Log.d("tag","userID"+userID);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(viewType==StateSet.ViewType.comment){
            view = inflater.inflate(R.layout.comment, parent, false);
            return new CmentViewHolder(view);

        }else if(viewType==StateSet.ViewType.ccomment){
            view = inflater.inflate(R.layout.ccomment, parent, false);
            return new CcmentViewHolder(view);

        }
        return null;
    }

    @Override
    public void onBindViewHolder( RecyclerView.ViewHolder holder, int position) {

        if(holder instanceof CmentViewHolder){

            Comment comment= cmentDataList.get(position).getComment();

            ((CmentViewHolder)holder).cid.setText(comment.getCid());
            if(posting.getPid().equals(comment.getCid()))
            {
                ((CmentViewHolder)holder).cid.setTextColor(0xFFF9595F);     //???????????? ???????????? ???????????????  0xFFF56E4A
            }

            ((CmentViewHolder)holder).cment.setText(comment.getCment());


            String cdate=comment.getCdate();
            TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
            SimpleDateFormat sdf = new SimpleDateFormat("yy/MM/dd HH:mm");
            String nowTime = sdf.format(System.currentTimeMillis());

            Boolean sameDay=true;
            for(int i=0;i<10;i++){
                if(cdate.charAt(i)!=nowTime.charAt(i))
                {
                    sameDay=false;
                }
            }    //03:01 02:01    03-02= 1    61-08= 53     1????????? ????????? ?????????.
            if(sameDay){

                //????????? true ?????? ?????? ???
                // ?????? 1?????????????????? ????????? ??????        --> ??????????????? ???????????? ??????
                int hourDifference = Integer.parseInt(nowTime.charAt(10) + "") - Integer.parseInt(cdate.charAt(10) + "");

                if(hourDifference ==0){
                    //?????? ???????????????
                    int minuteDifference = Integer.parseInt(nowTime.substring(12)) - Integer.parseInt(cdate.substring(12));
                    if (minuteDifference == 0) {
                        ((CmentViewHolder) holder).cdate.setText("?????? ???");
                    } else {
                        ((CmentViewHolder) holder).cdate.setText(minuteDifference + "??? ???");
                    }
                }
                else if(hourDifference ==1) {

                    //??????????????? ?????????????????? 1???????????? ??? ????????? ???????????? ????????????
                    int nowMinute= Integer.parseInt(nowTime.substring(12))+60;
                    int minuteDifference=  nowMinute- Integer.parseInt(cdate.substring(12));

                    if(minuteDifference<=60) {  //????????? ????????????
                        if (minuteDifference == 60) {
                            ((CmentViewHolder) holder).cdate.setText("????????? ???");
                        } else {
                            ((CmentViewHolder) holder).cdate.setText(minuteDifference + "??? ???");
                        }
                    }
                }
            }
            else{
                ((CmentViewHolder)holder).cdate.setText(cdate);
            }

            //?????? ??? ????????? ??? ??????????????? , ?????? ????????? ?????? ????????????
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {

                    Log.d("tag",comment.getCno()+"???????????????");
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    String[] list;
                    if(comment.getCid().equals(userID)){
                        list= new String[]{"????????? ??????","????????????"};

                    }else {
                        list=new String[]{"????????? ??????"};

                    }

                    PostingActivity.sendBt_Ccment.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            String ccment = PostingActivity.input1.getText().toString();

                            if (ccment.equals("")) {

                                Toast.makeText(v.getContext(), "????????? ??????????????????", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Log.d("tag",comment.getCno()+"cno??? ????????? DB??? ??????????????? ???");
                                //??????????????????

                                insertCcmentOnCment(comment.getPno(),comment.getCno(),userID, ccment,position);

                                if(PostingActivity.getHeartPressState()){
                                    PostingActivity.pinkheartBt.setVisibility(View.VISIBLE);
                                }
                                else
                                {
                                    PostingActivity. grayheartBt.setVisibility(View.VISIBLE);
                                }

                                PostingActivity.cmentBt.setVisibility(View.VISIBLE);
                                PostingActivity.input1.setVisibility(View.INVISIBLE);
                                PostingActivity.input1.setText("");
                                PostingActivity.sendBt_Ccment.setVisibility(View.INVISIBLE);
                                InputMethodManager imm= (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(PostingActivity.input1.getWindowToken(), 0);
                            }

                        }
                    });


                    builder.setItems(list, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            if(which==0){
                                //????????? ?????? ?????????
                                //??????????????? ????????? ???????????????
                                PostingActivity.input1.post(new Runnable() {
                                    @Override
                                    public void run() {

                                        PostingActivity.grayheartBt.setVisibility(View.INVISIBLE);
                                        PostingActivity.pinkheartBt.setVisibility(View.INVISIBLE);
                                        PostingActivity.cmentBt.setVisibility(View.INVISIBLE);
                                        PostingActivity.input1.setVisibility(View.VISIBLE);
                                        PostingActivity.sendBt_Ccment.setVisibility(View.VISIBLE);
                                        PostingActivity.input1.setFocusableInTouchMode(true);
                                        PostingActivity.input1.requestFocus();
                                        InputMethodManager imm= (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                                        imm.showSoftInput(PostingActivity.input1, 0);

                                    }
                                });

                            }else if(which==1){
                                //?????? ???????????? ?????????
                                Log.d("tag","???????????? ????????????");
                                deleteCmentOnPosting(comment.getPno(),comment.getCno(),comment.getCccnt(),position);

                            }
                        }
                    });

                    AlertDialog alertD= builder.create();
                    alertD.show();

                    return false;
                }
            });
        }
        else if(holder instanceof  CcmentViewHolder){


            Ccomment ccomment= cmentDataList.get(position).getCcomment();
            ((CcmentViewHolder)holder).ccid.setText(ccomment.getCcid());
            ((CcmentViewHolder)holder).ccment.setText(ccomment.getCcment());
            if(posting.getPid().equals(ccomment.getCcid()))
            {
                ((CcmentViewHolder)holder).ccid.setTextColor(0xFFF9595F);     //???????????? ??????????????? ???????????????  0xFFF56E4A
            }

            String ccdate=ccomment.getCcdate();
            TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
            SimpleDateFormat sdf = new SimpleDateFormat("yy/MM/dd HH:mm");
            String nowTime = sdf.format(System.currentTimeMillis());

            Boolean sameDay=true;
            for(int i=0;i<10;i++){
                if(ccdate.charAt(i)!=nowTime.charAt(i))
                {
                    sameDay=false;
                }
            }    //03:01 02:01    03-02= 1    61-08= 53     1????????? ????????? ?????????.
            if(sameDay){

                //????????? true ?????? ?????? ???
                // ?????? 1?????????????????? ????????? ??????        --> ??????????????? ???????????? ??????
                int hourDifference = Integer.parseInt(nowTime.charAt(10) + "") - Integer.parseInt(ccdate.charAt(10) + "");

                if(hourDifference ==0){
                    //?????? ???????????????
                    int minuteDifference = Integer.parseInt(nowTime.substring(12)) - Integer.parseInt(ccdate.substring(12));
                    if (minuteDifference == 0) {
                        ((CcmentViewHolder) holder).ccdate.setText("?????? ???");
                    } else {
                        ((CcmentViewHolder) holder).ccdate.setText(minuteDifference + "??? ???");
                    }
                }
                else if(hourDifference ==1) {

                    //??????????????? ?????????????????? 1???????????? ??? ????????? ???????????? ????????????
                    int nowMinute= Integer.parseInt(nowTime.substring(12))+60;
                    int minuteDifference=  nowMinute- Integer.parseInt(ccdate.substring(12));

                    if(minuteDifference<=60) {  //????????? ????????????
                        if (minuteDifference == 60) {
                            ((CcmentViewHolder) holder).ccdate.setText("????????? ???");
                        } else {
                            ((CcmentViewHolder) holder).ccdate.setText(minuteDifference + "??? ???");
                        }
                    }
                }
            }
            else{
                ((CcmentViewHolder)holder).ccdate.setText(ccdate);
            }

            //????????? ??? ????????? ??? ?????? ????????? ????????? ????????????
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Log.d("tag",ccomment.getCno()+"-"+ccomment.getCcno()+"??????????????????");

                    if(ccomment.getCcid().equals(userID)){


                        String list[]={"????????????"};
                        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                        builder.setItems(list, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if(which==0){
                                    //????????? ???????????? ?????????
                                    deleteCcmentOnCment(ccomment.getPno(),ccomment.getCno(),ccomment.getCcno(),position);
                                }
                            }
                        });

                        AlertDialog alertD= builder.create();
                        alertD.show();

                    }

                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return cmentDataList.size();
    }

    @Override
    public int getItemViewType(int position) {

        return cmentDataList.get(position).getViewType();
    }


    public class CmentViewHolder extends RecyclerView.ViewHolder {

        TextView cid, cment, cdate ;

        public CmentViewHolder(View itemView) {
            super(itemView);

            cid= itemView.findViewById(R.id.cid);
            cment= itemView.findViewById(R.id.cment);
            cdate= itemView.findViewById(R.id.cdate);
        }
    }
    public class CcmentViewHolder extends RecyclerView.ViewHolder{

        TextView ccid, ccment,ccdate;

        public CcmentViewHolder(View itemView){
            super(itemView);
            ccid= itemView.findViewById(R.id.ccid);
            ccment= itemView.findViewById(R.id.ccment);
            ccdate= itemView.findViewById(R.id.ccdate);

        }
    }

    void deleteCmentOnPosting(int pno,int cno,int cccnt, int position) {


        if (cccnt > 0) {

            //????????? position ?????? ????????? ???????????? ???????????????.
            for (int i = 1; i <= cccnt; i++) {

                Log.d("tag", "???????????????"+cmentDataList.get(position+1).getCcomment().getCcno()+"ccno?????????");
                //ccnt?????? ????????????
                updateCmentCnt(pno,"minus");
                cmentDataList.remove(position + 1);

            }
        }

        cmentDataList.remove(position);
        notifyDataSetChanged();

        Server server = new Server();
        RetrofitService service = server.getRetrofitService();
        Call<Integer> call = service.deleteComment(pno, cno);

        call.enqueue(new Callback<Integer>() {

            //Comment DB?????? ????????? (delete on Cascade??? DB??? ??????????????? ???????????? ?????????)
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {

                if (response.isSuccessful()) {
                    Integer result = response.body();
                    if (result.intValue() == 1) {

                        Log.d("tag", "??????????????????\n");
                        updateCmentCnt(pno,"minus");             //ccnt?????? ????????????
                    }
                }

            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.d("tag", "??????????????????\n");
            }
        });

    }


    void updateCmentCnt(int pno, String sign){

        Server server= new Server();
        RetrofitService service= server.getRetrofitService();
        Call<Integer> call= service.putCommentCnt(pno,sign);

        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if(response.isSuccessful())
                {
                    if( sign.equals("plus"))
                    {
                        Log.d("tag","ccnt++");
                        //????????? ?????? ??? ?????????
                        PostingActivity.ccnt.setText(String.valueOf(posting.getCcnt()+1));
                        posting.addCcnt(+1);

                    }
                    else {
                        Log.d("tag","ccnt--");
                        //????????? ?????? ??? ?????????
                        PostingActivity.ccnt.setText(String.valueOf(posting.getCcnt()-1));
                        Log.d("tag",PostingActivity.ccnt.getText().toString());
                        posting.addCcnt(-1);

                    }
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {

            }
        });

    }

    void updateCcmentCnt(int pno,int cno, String sign){

        //???????????? ?????? ?????? DB??? ????????????

        Server server= new Server();
        RetrofitService service= server.getRetrofitService();
        Call<Integer> call= service.putCcommentCnt(pno,cno,sign);

        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if(response.isSuccessful())
                {
                    if( sign.equals("plus"))
                    {
                        Log.d("tag","cccnt++");
                    }
                    else {
                        Log.d("tag","cccnt--");
                    }
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {

            }
        });

    }
    void deleteCcmentOnCment(int pno,int cno,int ccno, int position){

        Server server= new Server();
        RetrofitService service= server.getRetrofitService();
        Call<Integer> call= service.deleteCcomment(pno,cno,ccno);
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                Integer result= response.body();

                if(result.intValue()==1){

                    Log.d("tag", "?????????????????????\n");

                    //DB???
                    updateCmentCnt(pno,"minus");              //ccnt ?????????
                    updateCcmentCnt(pno,cno,"minus");         //cccnt ?????????

                    //DataList ???  cccnt?????????
                    for(DataItem.CommentData dataItem: cmentDataList){

                        if(dataItem.getComment()!=null){

                            if(dataItem.getComment().getCno()==cno)
                                dataItem.getComment().addCccnt(-1);
                        }
                    }

                    notifyItemRemoved(position);
                    cmentDataList.remove(position);
                    notifyItemRangeChanged(position,cmentDataList.size());



                }else{
                    Log.d("tag", "?????????????????????\n");
                }

            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.d("tag", "?????????????????????\n");
            }
        });

    }

    void insertCcmentOnCment(int pno,int cno, String ccid, String ccment, int position){

        //cccnt????????? ccnt?????????
        //DB??? - ????????????????????? ?????? ?????????, ?????????????????? ????????? ??? ??????. ????????????????????? ?????? ??? ??????
        //DataList??? - ????????? ????????? ??? ?????? posotion ?????? ????????? ???????????? ????????????????????? ?????????????????? ?????? notify. ?????? ?????? ???????????? ????????? ?????? ?????? ??????

        //1) ccno ?????? ?????????
        int cccnt= cmentDataList.get(position).getComment().getCccnt();
        int ccno=1;

        if(cccnt==0){
            ccno=1;  //1??? ????????? ??????
        }
        else
        {
            for (int i = 1; i <= cccnt; i++) {

                Ccomment ccomment = cmentDataList.get(position + i).getCcomment();

                if (ccno < ccomment.getCcno()) {
                    ccno = ccomment.getCcno();
                }
            }
            ccno++;     //??????????????? 1??????
        }

        //2) ????????????????????? ?????? ?????????
        Server server= new Server();
        RetrofitService service= server.getRetrofitService();

        Call<Ccomment> call= service.postCcomment(pno,cno,ccno,ccid,ccment);
        Log.d("tag",pno+" "+cno+" "+ccno+" "+ccid+" "+ccment+"????????? ?????????????????????");


        call.enqueue(new Callback<Ccomment>() {
            @Override
            public void onResponse(Call<Ccomment> call, Response<Ccomment> response) {
                if(response.isSuccessful()){
                    Ccomment ccomment= response.body();

                    //2) ????????????????????? ???????????????
                    //????????? ?????? ??????????????? ???????????? ?????? ??????        //????????? ?????? ??????????????? ?????? ??? ????????? ?????? ??????
                    cmentDataList.add(position+cccnt+1,new DataItem.CommentData(ccomment, StateSet.ViewType.ccomment));


                    //3) ccnt, cccnt ??? 1?????? ???????????? - DB
                    updateCmentCnt(pno,"plus");
                    updateCcmentCnt(pno,cno,"plus");

                    //3) cccnt ??? 1?????? ???????????? - dataList
                    cmentDataList.get(position).getComment().addCccnt(+1);

                    //4) ?????????????????? ?????? ??????
                    notifyItemRangeChanged(position+1,cmentDataList.size());

                }

            }

            @Override
            public void onFailure(Call<Ccomment> call, Throwable t) {
                Log.d("tag", "??????????????? ??????\n");
            }
        });

    }
}
