package com.example.ximenamoure.petvacay.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ximenamoure.petvacay.Models.Pet;
import com.example.ximenamoure.petvacay.Models.WorkerImage;
import com.example.ximenamoure.petvacay.R;
import com.example.ximenamoure.petvacay.WorkerImagesActivity;

import java.util.ArrayList;



public class WorkerImageListAdapter extends RecyclerView.Adapter<WorkerImageListAdapter.ImageHolder> {

    private ArrayList<WorkerImage> mImages;
    private Context mContext;

    public WorkerImageListAdapter(Context context, ArrayList<WorkerImage> images){
        mImages = images;
        mContext = context;
    }
    @Override
    public WorkerImageListAdapter.ImageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(mContext).inflate(R.layout.layout_worker_image_item, parent, false);
        return new WorkerImageListAdapter.ImageHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(WorkerImageListAdapter.ImageHolder holder, int position) {
        WorkerImage itemImage = mImages.get(position);
        holder.bindImage(itemImage);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return mImages.size();
    }


    public static class ImageHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private ImageView mItemImage;
        private WorkerImage mImage;

        public ImageHolder(View v){
            super(v);
            mItemImage = (ImageView) v.findViewById(R.id.workerImageItem);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Dialog imgDialog = new Dialog(v.getContext());
            imgDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

            View inflatedView = LayoutInflater.from(v.getContext()).inflate(R.layout.layout_fullscreen_image, null);

            ImageView imgView = (ImageView) inflatedView.findViewById(R.id.fullscreenImage);
            ImageView currentImage = (ImageView) ((ViewGroup)v).getChildAt(0);
            Bitmap bitmap = ((BitmapDrawable)currentImage.getDrawable()).getBitmap();
            imgView.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 900, 900, false));

            imgDialog.setContentView(inflatedView);
            imgDialog.show();
        }

        public void bindImage(WorkerImage image){
            mImage = image;

            Bitmap bmp = BitmapFactory.decodeByteArray(mImage.GetImage(), 0, mImage.GetImage().length);
            mItemImage.setImageBitmap(bmp);
        }
    }

}
