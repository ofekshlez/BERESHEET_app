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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class SignIn extends AppCompatActivity implements View.OnClickListener {
    Button btn_signin, btn_signup;
    String strReceived, jsonString;
    EditText id, password;
    TextView msg;
    Dialog d;
    String letters = "abcdefghijklmnopqrstuvwxyzABCDEFJHIJKLMNOPQRSTUVWXYZאבגדהוזחטיכךלמנסעפףצץקרשת";

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
                RSAEncryptor rsa = new RSAEncryptor(182477, 821);
                int key = (int)(Math.random()*75)+1;
                System.out.println("key: " + key);
                jsonString = new JSONObject()
                        .put("id", encryptID(id.getText().toString()))
                        .put("password", encryption(password.getText().toString(), key))
                        .put("function", "signIn")
                        .put("key", rsa.encrypt("key:" + key))
                        .toString();
                System.out.println(jsonString);
                sendData(jsonString);
                if (strReceived.equals("student")) {
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.putExtra("id", encryptID(id.getText().toString()));
                    startActivity(intent);
                }
                else if (strReceived.equals("teacher")) {
                    Intent intent = new Intent(this, MainActivity2.class);
                    intent.putExtra("id", encryptID(id.getText().toString()));
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

    public String encryption(String message, int key) {
        String newM = "";
        for (int i = 0; i < message.length(); i++) {
            int index = letters.indexOf(message.charAt(i));
            if (index == -1) {
                newM += message.charAt(i);
            }
            else {
                int place =  index + key;
                if (place > letters.length())
                    place = place - letters.length();
                newM += letters.charAt(place);
            }
        }
        return newM;
    }

    public String encryptID(String id) {
        Map<Character, Character> dic = new HashMap<Character, Character>();
        // Inserting pairs in above Map
        dic.put('1', '^');
        dic.put('2', '$');
        dic.put('3', '*');
        dic.put('4', '&');
        dic.put('5', '%');
        dic.put('6', '@');
        dic.put('7', '#');
        dic.put('8', '!');
        dic.put('9', ')');
        dic.put('0', '(');
        String newID = "";
        for (int i = 0; i < id.length(); i++) {
            newID += dic.get(id.charAt(i));
        }
        return newID;
    }
}