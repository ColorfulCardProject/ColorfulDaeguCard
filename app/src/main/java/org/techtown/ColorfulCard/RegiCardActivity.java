package org.techtown.ColorfulCard;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegiCardActivity extends AppCompatActivity {

    private Intent intent;
    private UserCard user;


    private MainHandler handler;

    private EditText et_cardName, cardNo1, cardNo2, cardNo3, cardNo4;
    private TextView NotifyWordCnt;
    private RadioButton chMeal,chBusic,chEdu;
    private Button RegisterBt, vaildCheckBT;
    private TextView vaildCheckResult;
    private AlertDialog dialog;
    private boolean validate = false;
    private boolean checkCardTitle= false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        intent = getIntent();

        user = (UserCard)intent.getSerializableExtra("user");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_card);

        et_cardName = findViewById(R.id.et_cardName);
        cardNo1 =  findViewById(R.id.cardNo1);
        cardNo2 = findViewById(R.id.cardNo2);
        cardNo3 = findViewById(R.id.cardNo3);
        cardNo4 = findViewById(R.id.cardNo4);

        chMeal = findViewById(R.id.chMeal);
        chBusic = findViewById(R.id.chBusic);
        chEdu = findViewById(R.id.chEdu);

        RegisterBt = findViewById(R.id.button);
        vaildCheckBT = findViewById(R.id.vaildCheckBT);
        vaildCheckResult= findViewById(R.id.vaildCheckResult);
        NotifyWordCnt = findViewById(R.id.NotifyWordCnt);

        handler = new MainHandler();

        et_cardName.addTextChangedListener(new TextWatcher() {
            //???????????? ??? ???????????? ????????? ???????????? ?????????

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //???????????? ?????? ?????????
                if (s.toString().length() > 0) {
                    NotifyWordCnt.setText("("+s.toString().length() + "/20)");
                    checkCardTitle = true;
                } else {//????????????
                    NotifyWordCnt.setText("(0/20)");
                    checkCardTitle = false;
                }
            }
        });


        //???????????? ????????????
        vaildCheckBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validate) {
                    return; //?????? ??????
                }

                if(cardNo1.getText().toString().equals("") || cardNo2.getText().toString().equals("") || cardNo3.getText().toString().equals("") || cardNo4.getText().toString().equals(""))
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegiCardActivity.this);
                    dialog = builder.setMessage("??????????????? ?????? ???????????????.").setPositiveButton("??????", null).create();
                    dialog.show();
                    return;
                }

                CertifyCheckCardThread thread= new CertifyCheckCardThread();
                thread.start();

            }
        });

        //???????????? ?????? ??????
        RegisterBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String cardName = et_cardName.getText().toString();
                StringBuilder cardNo = new StringBuilder();
                cardNo.append(cardNo1.getText().toString());
                cardNo.append(cardNo2.getText().toString());
                cardNo.append(cardNo3.getText().toString());
                cardNo.append(cardNo4.getText().toString());

                String cardType="";
                //??????:0      ??????:1        ??????:2
                if(chMeal.isChecked()) {
                    cardType = "0";
                }
                else if(chBusic.isChecked()){
                    cardType = "1";
                }
                else if(chEdu.isChecked()) {
                    cardType = "2";
                }


                if(cardName.equals("") || cardType.equals(""))
                {
                    Toast.makeText(getApplicationContext(), "?????? ?????????????????? ???????????????", Toast.LENGTH_SHORT).show();
                }
                else if(validate == false){
                    if(vaildCheckResult.getText().toString().equals("??????????????? ????????? ?????????????????????"))
                        Toast.makeText(getApplicationContext(), "????????? ????????? ???????????? ?????????", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(getApplicationContext(), "??????????????? ??????????????????", Toast.LENGTH_SHORT).show();
                }
                else
                {

                   InsertUserCard(cardNo, cardName, cardType);

                }
            }
        });

    }

    void InsertUserCard(StringBuilder cardNo, String cardName, String cardType){


        Server server = new Server();
        RetrofitService service1 = server.getRetrofitService();
        Call<Card> call = service1.postUserCard(cardNo.toString(),user.getId(),cardName,cardType);
        call.enqueue(new Callback<Card>() {

            @Override
            public void onResponse(Call<Card> call, Response<Card> response) {
                if (response.isSuccessful()) {
                    Intent intent = new Intent(RegiCardActivity.this, HomeActivity.class);
                    user.clearCardBalances();
                    intent.putExtra("user",user);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
                    finish();

                } else {
                    Log.d("tag", "get ??? ??? ???????????? ??? getByNum??? ????????? ????????? ???????????? ???????????? ?????? ???????????? ?????? ??????");
                    Intent intent = new Intent(RegiCardActivity.this, HomeActivity.class);
                    user.clearCardBalances();
                    intent.putExtra("user",user);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
                    finish();
                }

            }

            @Override
            public void onFailure(Call<Card> call, Throwable t) {
                Log.d("tag", "??????2" + t.getMessage());
            }
        });

    }


    class CertifyCheckCardThread extends Thread{ //????????? ?????? ????????? ????????? ?????????

        @Override
        public void run()
        {
            Message message=handler.obtainMessage();

            try {
                BalanceCheck checker = new BalanceCheck(cardNo1.getText().toString(),cardNo2.getText().toString(),cardNo3.getText().toString(),cardNo4.getText().toString());

                if (checker.tryBalanceCheck(2).equals("success")) {
                    message.what = StateSet.RegisterCardMsg.MSG_SUCCESS_VAILCHECK;
                    handler.sendMessage(message); //??????????????? ???????????? ????????? ?????????
                } else { //????????? ??????
                    message.what = StateSet.RegisterCardMsg.MSG_FAIL;
                    handler.sendMessage(message);
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class MainHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            switch (message.what)
            {
                case StateSet.RegisterCardMsg.MSG_SUCCESS_VAILCHECK:
                    vaildCheckResult.setTextColor(0xFF000000);
                    vaildCheckResult.setText("??????????????? ?????????????????????");
                    cardNo1.setEnabled(false); //??????????????? ??????
                    cardNo2.setEnabled(false);
                    cardNo3.setEnabled(false);
                    cardNo4.setEnabled(false);
                    validate = true; //?????? ??????
                    break;
                case StateSet.RegisterCardMsg.MSG_FAIL:
                    vaildCheckResult.setText("??????????????? ????????? ?????????????????????");
                    vaildCheckResult.setTextColor(0xAAef484a);
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
    }
}

