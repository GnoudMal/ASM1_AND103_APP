package com.vdsl.asm1_and103;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.vdsl.asm1_and103.databinding.DialogAddFoodBinding;
import com.vdsl.asm1_and103.databinding.ItemFoodBinding;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {

    private Context context;
    private List<foodModel> foodList;

    public FoodAdapter(Context context, List<foodModel> foodList) {
        this.context = context;
        this.foodList = foodList;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        ItemFoodBinding binding = ItemFoodBinding.inflate(inflater, parent, false);
        return new FoodViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        foodModel food = foodList.get(position);
        holder.binding.nameFood.setText(food.getName());
        holder.binding.servingSize.setText(food.getServingSize());
        Double mainPrice = Double.parseDouble(String.valueOf(food.getPrice()));
        holder.binding.priceFood.setText("$ " + mainPrice);
        Log.d("FoodAdapter", "Image URL: " + food.getImage());
        Glide.with(context)
                .load(food.getImage())
                .placeholder(R.drawable.loading)
                .error(R.drawable.mark)
                .into(holder.binding.imgProduct);



        holder.binding.btnDelete.setOnClickListener(v -> {
            showDeleteConfirmationDialog(food.get_id(), position);
        });

        holder.itemView.setOnLongClickListener(v -> {
            showUpdateFoodDialog(context, food);
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    private void showDeleteConfirmationDialog(String id, int position) {
        new AlertDialog.Builder(context)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa món ăn này không?")
                .setPositiveButton("Xóa", (dialog, which) -> deleteFood(id, position))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteFood(String id, int position) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIService.DOMAIN)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIService apiService = retrofit.create(APIService.class);
        Call<Void> call = apiService.deleteFood(id);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    foodList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, foodList.size());
                    Toast.makeText(context, "Xóa món ăn thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("FoodAdapter", "Failed to delete food: " + response.message());
                    Toast.makeText(context, "Xóa món ăn thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("FoodAdapter", "Failed to delete food: " + t.getMessage());
                Toast.makeText(context, "Xóa món ăn thất bại", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showUpdateFoodDialog(Context context, foodModel food) {
        DialogAddFoodBinding dialogBinding = DialogAddFoodBinding.inflate(LayoutInflater.from(context));
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        dialogBuilder.setView(dialogBinding.getRoot());

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        dialogBinding.etFoodName.setText(food.getName());
        dialogBinding.etServingSize.setText(food.getServingSize());
        dialogBinding.etFoodImage.setText(food.getImage());
        dialogBinding.etPrice.setText(String.valueOf(food.getPrice()));

        dialogBinding.btnAddFood.setText("Update Food");
        dialogBinding.btnAddFood.setOnClickListener(v -> {
            String foodName = dialogBinding.etFoodName.getText().toString().trim();
            String servingSize = dialogBinding.etServingSize.getText().toString().trim();
            String imgFood = dialogBinding.etFoodImage.getText().toString().trim();
            double price;
            try {
                price = Double.parseDouble(dialogBinding.etPrice.getText().toString().trim());
            } catch (NumberFormatException e) {
                Log.e("FoodAdapter", "Định dạng giá không hợp lệ: " + e.getMessage());
                alertDialog.dismiss();
                return;
            }

            foodModel updatedFood = new foodModel(food.get_id(), foodName,price,servingSize, imgFood);

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(APIService.DOMAIN)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            APIService apiService = retrofit.create(APIService.class);
            Call<foodModel> call = apiService.updateFood(food.get_id(), updatedFood);
            call.enqueue(new Callback<foodModel>() {
                @Override
                public void onResponse(Call<foodModel> call, Response<foodModel> response) {
                    if (response.isSuccessful()) {
                        int index = foodList.indexOf(food);
                        if (index != -1) {
                            foodList.set(index, response.body());
                            notifyDataSetChanged();
                        }
                        alertDialog.dismiss();
                    } else {
                        Log.e("FoodAdapter", "Cập nhật món ăn thất bại: " + response.code() + " " + response.message());
                        alertDialog.dismiss();
                    }
                }

                @Override
                public void onFailure(Call<foodModel> call, Throwable t) {
                    Log.e("FoodAdapter", "Cập nhật món ăn thất bại: " + t.getMessage(), t);
                    alertDialog.dismiss();
                }
            });
        });
    }

    public static class FoodViewHolder extends RecyclerView.ViewHolder {

        private final ItemFoodBinding binding;

        public FoodViewHolder(ItemFoodBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
