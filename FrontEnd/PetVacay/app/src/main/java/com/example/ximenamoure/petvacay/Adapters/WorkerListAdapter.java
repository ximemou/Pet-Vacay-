package com.example.ximenamoure.petvacay.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ximenamoure.petvacay.BookWorkerActivity;
import com.example.ximenamoure.petvacay.Models.Worker;
import com.example.ximenamoure.petvacay.R;
import com.example.ximenamoure.petvacay.RoundedImage;

import org.w3c.dom.Text;

import java.util.ArrayList;


public class WorkerListAdapter extends RecyclerView.Adapter<WorkerListAdapter.WorkerHolder> {

    private ArrayList<Worker> mWorkers;
    private Context mContext;

    public WorkerListAdapter(Context context, ArrayList<Worker> workers){
        mWorkers = workers;
        mContext = context;
    }
    @Override
    public WorkerListAdapter.WorkerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(mContext).inflate(R.layout.layout_worker_item, parent, false);
        return new WorkerListAdapter.WorkerHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(WorkerListAdapter.WorkerHolder holder, int position) {
        Worker itemWorker = mWorkers.get(position);
        holder.bindWorker(itemWorker);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return mWorkers.size();
    }

    public static class WorkerHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private ImageView mItemProfileImage;
        private TextView mItemNameAndSurname;
        private TextView mItemDescription;
        private TextView mItemBoardingPrice;
        private TextView mItemWalkingPrice;
        private ImageView mItemBannerImage;
        private RatingBar mItemRateBar;
        private Worker mWorker;

        public WorkerHolder(View v){
            super(v);
            mItemProfileImage = (ImageView) v.findViewById(R.id.filteredWorkerProfileImage);
            mItemNameAndSurname = (TextView) v.findViewById(R.id.filteredWorkerNameAndSurname);
            mItemDescription = (TextView) v.findViewById(R.id.filteredWorkerDescription);
            mItemBoardingPrice = (TextView) v.findViewById(R.id.filteredWorkerBoardingPrice);
            mItemWalkingPrice = (TextView) v.findViewById(R.id.filteredWorkerWalkingPrice);
            mItemBannerImage = (ImageView) v.findViewById(R.id.filteredWorkerBanner);
            mItemRateBar = (RatingBar) v.findViewById(R.id.filteredWorkerScore);
            v.setOnClickListener(this);


            mItemRateBar.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
            mItemRateBar.setFocusable(false);
        }

        @Override
        public void onClick(View v) {
            Intent i = new Intent(v.getContext(), BookWorkerActivity.class);
            i.putExtra("SelectedWorker", mWorker.GetWorkerId());
            i.putExtra("SelectedWorkerPhone", mWorker.GetPhoneNumber());
            i.putExtra("WalkingPrice",mWorker.GetWalkingPrice());
            i.putExtra("BoardingPrice",mWorker.GetBoardingPrice());
            v.getContext().startActivity(i);
        }

        public void bindWorker(Worker worker){
            mWorker = worker;
            mItemNameAndSurname.setText(mWorker.GetName() + " " + mWorker.GetSurname());
            mItemDescription.setText(mWorker.GetDescription());
            mItemBoardingPrice.setText("Estadia: $ " + mWorker.GetBoardingPrice());
            if(mWorker.GetWalkingPrice() > 0){
                mItemWalkingPrice.setText("Paseo: $" + mWorker.GetWalkingPrice());
            }
            else{
                mItemWalkingPrice.setText("Paseo: $ --");
            }

            if(mWorker.GetProfileImage() != null){
                Bitmap bmp = BitmapFactory.decodeByteArray(mWorker.GetProfileImage(), 0, mWorker.GetProfileImage().length);
                mItemProfileImage.setImageBitmap(bmp);
            }

            if(mWorker.GetBannerImage() != null){
                Bitmap bmp = BitmapFactory.decodeByteArray(mWorker.GetBannerImage(), 0, mWorker.GetBannerImage().length);
                mItemBannerImage.setImageBitmap(bmp);
            }

            float rating = (float) mWorker.GetAverageScore();
            mItemRateBar.setRating(rating);
        }
    }

}
