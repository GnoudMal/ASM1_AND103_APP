package com.vdsl.asm1_and103;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vdsl.asm1_and103.databinding.ActivityMainBinding;
import com.vdsl.asm1_and103.databinding.DialogAddFoodBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    List<foodModel> list = new ArrayList<>();

    FoodAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIService.DOMAIN)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        binding.recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        APIService apiService = retrofit.create(APIService.class);
        Call<List<foodModel>> call = apiService.getFoods();
        call.enqueue(new Callback<List<foodModel>>() {
            @Override
            public void onResponse(Call<List<foodModel>> call, Response<List<foodModel>> response) {
                if ((response.isSuccessful())){
                    list = response.body();
                    adapter = new FoodAdapter(MainActivity.this,list);
                    binding.recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<foodModel>> call, Throwable t) {
                Log.e("Main", t.getMessage());
            }
        });

        binding.flAddFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddFoodDialog();
            }
        });

    }

    private void showAddFoodDialog() {
        // Inflate the dialog layout using DialogAddFoodBinding
        DialogAddFoodBinding dialogBinding = DialogAddFoodBinding.inflate(LayoutInflater.from(this));
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setView(dialogBinding.getRoot());

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        dialogBinding.btnAddFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String foodName = dialogBinding.etFoodName.getText().toString().trim();
                String servingSize = dialogBinding.etServingSize.getText().toString().trim();
                String imgFood = dialogBinding.etFoodImage.getText().toString().trim();
                String priceString = dialogBinding.etPrice.getText().toString().trim();
                double price = 0.0;

                if (foodName.isEmpty()) {
                    dialogBinding.etFoodName.setError("Tên món ăn không được để trống");
                    return;
                }

                if (servingSize.isEmpty()) {
                    dialogBinding.etServingSize.setError("Khẩu phần không được để trống");
                    return;
                }

                if (imgFood.isEmpty()) {
                    dialogBinding.etFoodImage.setError("URL hình ảnh không được để trống");
                    return;
                }

                if (priceString.isEmpty()) {
                    dialogBinding.etPrice.setError("Giá không được để trống");
                    return;
                }
                try {
                    price = Double.parseDouble(dialogBinding.etPrice.getText().toString().trim());
                } catch (NumberFormatException e) {
                    Log.e("Main", "Định dạng giá không hợp lệ: " + e.getMessage());
                    alertDialog.dismiss();
                    return;
                }

                foodModel newFood = new foodModel(foodName, price, servingSize,imgFood);
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(APIService.DOMAIN)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                APIService apiService = retrofit.create(APIService.class);
                Call<foodModel> call = apiService.addFood(newFood);
                call.enqueue(new Callback<foodModel>() {
                    @Override
                    public void onResponse(Call<foodModel> call, Response<foodModel> response) {
                        if (response.isSuccessful()) {
                            list.add(response.body());
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.e("Main", "Thêm món ăn thất bại: " + response.code() + " " + response.message());
                        }
                        alertDialog.dismiss();
                    }

                    @Override
                    public void onFailure(Call<foodModel> call, Throwable t) {
                        Log.e("Main", "Thêm món ăn thất bại: " + t.getMessage(), t);
                        alertDialog.dismiss();
                    }
                });
            }
        });

    }

}