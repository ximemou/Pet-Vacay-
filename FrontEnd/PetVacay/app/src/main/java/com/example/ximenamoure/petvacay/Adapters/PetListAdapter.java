package com.example.ximenamoure.petvacay.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ximenamoure.petvacay.AddClientInfoToProfileActivity;
import com.example.ximenamoure.petvacay.AddPetActivity;
import com.example.ximenamoure.petvacay.ClientProfileActivity;
import com.example.ximenamoure.petvacay.Models.Pet;
import com.example.ximenamoure.petvacay.R;
import com.example.ximenamoure.petvacay.UpdatePetActivity;

import java.util.ArrayList;



public class PetListAdapter extends RecyclerView.Adapter<PetListAdapter.PetHolder> {

    private ArrayList<Pet> mPets;
    private Context mContext;

    private int clientId;

    public PetListAdapter(Context context, ArrayList<Pet> pets,int id){
        mPets = pets;
        mContext = context;
        clientId=id;
    }
    @Override
    public PetListAdapter.PetHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(mContext).inflate(R.layout.layout_pet_item, parent, false);
        return new PetHolder(inflatedView,clientId);
    }

    @Override
    public void onBindViewHolder(PetListAdapter.PetHolder holder, int position) {
        Pet itemPet = mPets.get(position);
        holder.bindPet(itemPet);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return mPets.size();
    }

    public static class PetHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private ImageView mItemImage;
        private TextView mItemName;
        private TextView mItemPetType;
        private Pet mPet;
        private int clientId;

        public PetHolder(View v,int id){
            super(v);
            mItemImage = (ImageView) v.findViewById(R.id.petImage);
            mItemName = (TextView) v.findViewById(R.id.petName);
            mItemPetType = (TextView) v.findViewById(R.id.petType);
            clientId=id;
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Context context = itemView.getContext();
            Intent updatePetIntent = new Intent(context, UpdatePetActivity.class);
            updatePetIntent.putExtra("CLIENT_ID", clientId);
            updatePetIntent.putExtra("PET_ID",mPet.GetPetId());
            context.startActivity(updatePetIntent);
        }

        public void bindPet(Pet pet){
            mPet = pet;
            mItemName.setText(mPet.GetPetName());
            mItemPetType.setText(mPet.GetPetType());

             Bitmap bmp = BitmapFactory.decodeByteArray(mPet.GetPetImage(), 0, mPet.GetPetImage().length);
            if(bmp!=null){
                mItemImage.setImageBitmap(bmp);
            }
            else{
                mItemImage.setBackgroundResource(R.drawable.ic_monochrome_photos);
            }
        }
    }
}
