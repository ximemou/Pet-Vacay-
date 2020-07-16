package com.example.ximenamoure.petvacay.Adapters;

import android.content.Context;
import android.content.Intent;
import android.icu.text.IDNA;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.ximenamoure.petvacay.InformDetailsActivity;
import com.example.ximenamoure.petvacay.Models.Inform;
import com.example.ximenamoure.petvacay.R;

import java.util.ArrayList;


public class InformsAdapter  extends RecyclerView.Adapter<InformsAdapter.InformsHolder>  {

    private ArrayList<Inform> mInforms;
    private Context mContext;


    public InformsAdapter(Context context, ArrayList<Inform> informs){
        mInforms = informs;
        mContext = context;
    }
    @Override
    public InformsAdapter.InformsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(mContext).inflate(R.layout.layout_client_informs, parent, false);
        return new InformsAdapter.InformsHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(InformsAdapter.InformsHolder holder, int position) {
        final Inform itemInform = mInforms.get(position);
        holder.bindInform(itemInform);

        holder.mDetailsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, InformDetailsActivity.class);
                i.putExtra("INFORM_ID", itemInform.GetInformId());
                i.putExtra("INFORM_DATA", itemInform.GetInformData());
                mContext.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mInforms.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class InformsHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView mBookingInfo;
        private TextView mInformDay;
        public Button mDetailsBtn;
        private Inform mInform;

        public InformsHolder(View v){
            super(v);
            mBookingInfo = (TextView) v.findViewById(R.id.informBookingInfo);
            mInformDay = (TextView) v.findViewById(R.id.informDay);
            mDetailsBtn = (Button) v.findViewById(R.id.seeInformDetails);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
        }

        public void bindInform(Inform inform){
            mInform = inform;
            mBookingInfo.setText("Reserva #" + mInform.GetBookingId());
            mInformDay.setText(mInform.GetInformDate());
        }
    }

}
