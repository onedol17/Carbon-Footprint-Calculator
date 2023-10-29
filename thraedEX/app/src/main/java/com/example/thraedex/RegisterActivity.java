package com.example.thraedex;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.thraedex.databinding.ActivityRegisterBinding;

import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.userid.title.setText("아이디");
        binding.password.title.setText("비밀번호");

        binding.register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Post post = new Post("https://taemin-backend.run.goorm.site/register");
                    try {
                            JSONObject subjsonObject = new JSONObject();
                            subjsonObject.put("userID", binding.userid.edit.getText().toString());
                            subjsonObject.put("password", binding.password.edit.getText().toString());
                            post.execute(subjsonObject.toString()).get();
                            JSONObject response = post.getResponse();
                            if(response.getString("status").equals("success")) {
                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
        });
    }
}
