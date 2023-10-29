package com.example.thraedex;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.thraedex.databinding.ActivityLoginBinding;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.userid.title.setText("아이디");
        binding.password.title.setText("비밀번호");

        binding.register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });

        binding.login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Post post = new Post("https://taemin-backend.run.goorm.site/login");
                try {
                    JSONObject subjsonObject = new JSONObject();
                    subjsonObject.put("userid", binding.userid.edit.getText().toString());
                    subjsonObject.put("password", binding.password.edit.getText().toString());
                    post.execute(subjsonObject.toString()).get();
                    JSONObject response = post.getResponse();
                    if(response.getString("status").equals("success")) {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("userid", binding.userid.edit.getText().toString());
                        startActivity(intent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        binding.test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Post post = new Post("https://taemin-backend.run.goorm.site/update");
                try {
                    JSONObject subjsonObject = new JSONObject();
                    subjsonObject.put("userid", binding.userid.edit.getText().toString());
                    subjsonObject.put("spend", "3000");
                    post.execute(subjsonObject.toString()).get();
                    JSONObject response = post.getResponse();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}