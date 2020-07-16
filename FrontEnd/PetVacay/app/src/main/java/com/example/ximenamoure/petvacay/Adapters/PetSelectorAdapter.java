package com.example.ximenamoure.petvacay.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.ximenamoure.petvacay.Models.Pet;
import com.example.ximenamoure.petvacay.R;
import com.example.ximenamoure.petvacay.UpdatePetActivity;

import java.util.ArrayList;


public class PetSelectorAdapter extends RecyclerView.Adapter<PetSelectorAdapter.PetHolder> {
    private ArrayList<Pet> mPets;
    private Context mContext;
    private RadioButton lastCheckedRB;

    public PetSelectorAdapter(Context context, ArrayList<Pet> pets,int id){
        mPets = pets;
        mContext = context;
    }
    @Override
    public PetSelectorAdapter.PetHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(mContext).inflate(R.layout.layout_select_pet, parent, false);
        return new PetSelectorAdapter.PetHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(PetSelectorAdapter.PetHolder holder, int position) {
        final Pet itemPet = mPets.get(position);
        holder.bindPet(itemPet);

        holder.radioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences m = PreferenceManager.getDefaultSharedPreferences(mContext);
                SharedPreferences.Editor editor = m.edit();
                editor.putInt("SelectedPet", itemPet.GetPetId());
                editor.commit();

                if(lastCheckedRB != null){
                    lastCheckedRB.setChecked(false);
                }
                lastCheckedRB = (RadioButton) v;
            }
        });
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
        public RadioButton radioBtn;
        private Pet mPet;

        public PetHolder(View v){
            super(v);
            mItemImage = (ImageView) v.findViewById(R.id.petSelImage);
            mItemName = (TextView) v.findViewById(R.id.petSelName);
            mItemPetType = (TextView) v.findViewById(R.id.petSelType);
            radioBtn = (RadioButton) v.findViewById(R.id.radioSelectPet);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
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
