package org.techtown.ColorfulCard;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserCardListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private ArrayList<DataItem.CardData> cardDataList = null;

    protected UserCardListAdapter(ArrayList<DataItem.CardData> dataList)
    {
        cardDataList = dataList;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view;
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(viewType== StateSet.ViewType.mealCard){
            view = inflater.inflate(R.layout.meal_card, parent, false);
            return new CardViewHolder(view);
        }
        else  if(viewType == StateSet.ViewType.sideMealCard)
        {
            view = inflater.inflate(R.layout.sidemeal_card, parent, false);
            return new CardViewHolder(view);
        }
        else if(viewType== StateSet.ViewType.eduCard)
        {
            view = inflater.inflate(R.layout.educationcard, parent, false);
            return new CardViewHolder(view);
        }
        else{
            return null;
        }


    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position)
    {
        if(viewHolder instanceof CardViewHolder){

            DataItem.CardData card =cardDataList.get(position);

            ((CardViewHolder) viewHolder).name.setText(card.getCardName());
            ((CardViewHolder) viewHolder).content.setText(card.getBalance());
            ((CardViewHolder) viewHolder).minus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setMessage("????????? ?????????????????????????");
                    builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteCardOnUser(position, card);
                        }
                    });
                    builder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }
                    });

                    builder.show();
                }
            });

            ((CardViewHolder) viewHolder).button.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    String[] balances = cardDataList.get(position).getBalances();

                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    AlertDialog dialog = builder.setMessage(
                            "?????? ????????????: "+balances[0] +"???"
                                    +"\n?????? ????????????: "+ balances[1] +"???"
                                    +"\n?????? ????????????: "+ balances[2] +"???"
                                    +"\n?????? ????????????: "+ balances[3] +"???"
                                    +"\n?????? ????????????: "+ balances[4] +"???"
                                    +"\n?????? ????????????: "+ balances[5] +"???"
                                    +"\n?????? ????????????: "+ balances[6]).setPositiveButton("??????", null).create();
                    dialog.show();

                }

            });
        }

    }


    void deleteCardOnUser(int position, DataItem.CardData card) {
        Server server = new Server();
        RetrofitService service1 = server.getRetrofitService();
        Call<Integer> call = service1.deleteUserCard(card.getCardNum(), card.getUserID(), card.getCardName(), card.getCardType());

        call.enqueue(new Callback<Integer>() {

            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {

                if (response.isSuccessful()) {

                    HomeActivity.user.deleteCard(card.getCardNum());
                    Log.d("tag", "??????????????????\n");
                    notifyItemRemoved(position);
                    cardDataList.remove(position);
                    notifyItemRangeChanged(position, cardDataList.size());
                    //????????? ????????? ???????????? ????????? ???????????? ???????????? (??????0) ??? ????????? ???????????? ??????
                } else {
                    Log.d("tag", "??????????????????\n");
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                // Log.d("tag", "?????? delete?????? ??? ????????? int ?????? ????????? ??????" + t.getMessage());
                Log.d("tag", " ???????????? ????????? ???????????? ??????" + t.getMessage());

            }
        });
    }

    @Override
    public int getItemCount()
    {
        return cardDataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return cardDataList.get(position).getViewType();
    }


    public class CardViewHolder extends RecyclerView.ViewHolder{
        private TextView name;
        private TextView content;
        private ImageView image;
        private Button button;
        private ImageView minus;

       private CardViewHolder(View itemView)
        {
            super(itemView);
            name = itemView.findViewById(R.id.pid);
            content = itemView.findViewById(R.id.pcontent);
            image = itemView.findViewById(R.id.imageView);
            button = itemView.findViewById(R.id.button);
            minus=itemView.findViewById(R.id.minus);
        }
    }

}