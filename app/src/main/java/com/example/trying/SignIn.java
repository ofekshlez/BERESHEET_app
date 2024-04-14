package com.example.trying;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.concurrent.ExecutionException;

public class SignIn extends AppCompatActivity implements View.OnClickListener {
    Button btn_signin, btn_signup;
    String strReceived, jsonString;
    EditText id, password;
    TextView msg;
    Dialog d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in);

        btn_signin = (Button)findViewById(R.id.btn_signin);
        btn_signin.setOnClickListener(this);
        btn_signup = (Button)findViewById(R.id.btn_signup);
        btn_signup.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(btn_signin == view)
        {
            id = (EditText)findViewById(R.id.id);
            password = (EditText)findViewById(R.id.password);
            try {
                jsonString = new JSONObject()
                        .put("id", id.getText().toString())
                        .put("password", password.getText().toString())
                        .put("function", "signIn")
                        .toString();
                sendData(jsonString);
                if (strReceived.equals("true")) {
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.putExtra("id", id.getText().toString());
                    startActivity(intent);
                }
                else if (strReceived.equals("false")){
                    System.out.println("else");
                    msg = (TextView)findViewById(R.id.msg);
                    msg.setVisibility(View.VISIBLE);
                }
                else{
                    d = new Dialog(view.getContext());
                    d.setContentView(R.layout.error);
                    d.setTitle("Error");
                    d.setCancelable(false);
                    Button btn_back = (Button)d.findViewById(R.id.btn_signIn);
                    btn_back.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            d.dismiss();
                        }
                    });
                    d.show();
                }
            } catch (JSONException e) {
                d = new Dialog(view.getContext());
                d.setContentView(R.layout.error);
                d.setTitle("Error");
                d.setCancelable(false);
                Button btn_back = (Button)d.findViewById(R.id.btn_signIn);
                btn_back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        d.dismiss();
                    }
                });
                d.show();
            }

        }
        else if(btn_signup == view)
        {
            Intent intent = new Intent(this,SignUp.class);
            startActivity(intent);
        }
    }

    private void sendData(String signUP)
    {
        try {
            SocketTask send1 = new SocketTask(signUP);
            strReceived = send1.execute().get();
        }
        catch (ExecutionException | InterruptedException e)
        {
            strReceived = "error";
            Toast.makeText(this,"could not send", Toast.LENGTH_LONG).show();
            Log.e("Exception", e.toString());
        }
    }
}