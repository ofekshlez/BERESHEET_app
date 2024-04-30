package com.example.trying;

import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUp extends AppCompatActivity implements View.OnClickListener {

    Button btn_back, btn_signup, btn_birthday;
    RadioButton teacher;
    Spinner school;
    Dialog d;
    TextView check_email, check_first_name, check_last_name,
            check_birthday, check_id, check_school, check_password, check_same_password;
    EditText email, first_name, last_name, id, password, same_password;
    String strReceived, jsonString;
    String letters = "abcdefghijklmnopqrstuvwxyzABCDEFJHIJKLMNOPQRSTUVWXYZאבגדהוזחטיכךלמנסעפףצץקרשת";

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);

        btn_back = (Button)findViewById(R.id.btn_back);
        btn_back.setOnClickListener(this);
        btn_signup = (Button)findViewById(R.id.btn_signup);
        btn_signup.setOnClickListener(this);
        btn_birthday = (Button)findViewById(R.id.birthday);
        btn_birthday.setOnClickListener(this);

        school = (Spinner)findViewById(R.id.school);
        String[] schools = getResources().getStringArray(R.array.school_names);
        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.spinner, schools);
        adapter.setDropDownViewResource(androidx.constraintlayout.widget.R.layout.support_simple_spinner_dropdown_item);
        school.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        if (btn_back == view) {
            Intent intent = new Intent(this,SignIn.class);
            startActivity(intent);
        }
        else if (btn_signup == view) {
            if (isValidSignIn()) {
                email = (EditText)findViewById(R.id.email);
                first_name = (EditText)findViewById(R.id.first_name);
                last_name = (EditText)findViewById(R.id.last_name);
                btn_birthday = (Button)findViewById(R.id.birthday);
                id = (EditText)findViewById(R.id.id);
                school = (Spinner)findViewById(R.id.school);
                password = (EditText)findViewById(R.id.password);
                same_password = (EditText)findViewById(R.id.same_password);
                teacher = (RadioButton)findViewById(R.id.teacher);
                String job = "student";
                if (teacher.isChecked()) {
                    job = "teacher";
                }
                try {
                    RSAEncryptor rsa = new RSAEncryptor(182477, 821);
                    int key = (int)(Math.random()*75)+1;
                    jsonString = new JSONObject()
                                .put("id", encryptID(id.getText().toString()))
                                .put("first name", encryption(first_name.getText().toString(), key))
                                .put("last name", encryption(last_name.getText().toString(), key))
                                .put("email", encryption(email.getText().toString(), key))
                                .put("school", school.getSelectedItem().toString())
                                .put("password", encryption(password.getText().toString(), key))
                                .put("birthday", btn_birthday.getText().toString())
                                .put("job", job)
                                .put("function", "signUp")
                                .put("key", rsa.encrypt("key:" + key))
                                .toString();
                    sendData(jsonString);
                    if (strReceived.equals("true")) {
                        if (job.equals("student")) {
                            Intent intent = new Intent(this, MainActivity.class);
                            intent.putExtra("id", encryptID(id.getText().toString()));
                            startActivity(intent);
                        }
                        else {
                            Intent intent = new Intent(this, MainActivity2.class);
                            intent.putExtra("id", encryptID(id.getText().toString()));
                            startActivity(intent);
                        }
                    } else if (strReceived.equals("false")) {
                        System.out.println("else");
                        Toast.makeText(this,"משתמש קיים כבר עם תעודת הזהות", Toast.LENGTH_LONG).show();
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
                                signOut();
                            }
                        });
                        d.show();
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }


        }


        else if (btn_birthday == view) {
            Calendar systemCalender = Calendar.getInstance();
            int year = systemCalender.get(Calendar.YEAR);
            int month = systemCalender.get(Calendar.MONTH);
            int day = systemCalender.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, new SetDate(), year, month, day);
            datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
            datePickerDialog.show();
        }
    }

    public class SetDate implements DatePickerDialog.OnDateSetListener {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            monthOfYear = monthOfYear +1;
            String str = dayOfMonth + "/" + monthOfYear + "/" +year;
            btn_birthday.setText(str);
        }
    }

    public void signOut(){
        Intent intent = new Intent(this, SignIn.class);
        startActivity(intent);
    }
    public boolean isValidSignIn(){
        boolean valid_email = true, valid_name = true, valid_bd = true,
                valid_id = true, valid_school = true, valid_password = true;
        email = (EditText)findViewById(R.id.email);
        first_name = (EditText)findViewById(R.id.first_name);
        last_name = (EditText)findViewById(R.id.last_name);
        btn_birthday = (Button)findViewById(R.id.birthday);
        id = (EditText)findViewById(R.id.id);
        school = (Spinner)findViewById(R.id.school);
        password = (EditText)findViewById(R.id.password);
        same_password = (EditText)findViewById(R.id.same_password);
        if (!checkEmail(email.getText().toString()))
            valid_email = false;
        if (!checkName(first_name.getText().toString(), last_name.getText().toString()))
            valid_name = false;
        if (!checkDate(btn_birthday.getText().toString())) {
            valid_bd = false;
        }
        if (!checkId(id.getText().toString())) {
            valid_id = false;
        }
        if (!checkSchool(school.getSelectedItem().toString())) {
            valid_school = false;
        }
        if (!checkPassword(password.getText().toString(), same_password.getText().toString())) {
            valid_password = false;
        }
        return valid_email && valid_name && valid_bd && valid_school && valid_password && valid_id;
    }

    public static boolean validate(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.matches();
    }
    public boolean checkEmail(String email) {
        String msg = "";
        check_email = (TextView)findViewById(R.id.check_email);
        boolean valid = true;
        if (!validate(email)) {
            msg = "אימייל איני תקין";
            valid = false;
        }
        check_email.setText(msg);
        return valid;
    }

    public boolean checkName(String first_name, String last_name) {
        String msg = "";
        check_first_name = (TextView)findViewById(R.id.check_first_name);
        boolean valid = true;
        if (first_name.length() < 2) {
            msg = "אורך השם קצר מ-2 תווים";
            valid = false;
        }
        check_first_name.setText(msg);
        msg = "";
        check_last_name = (TextView)findViewById(R.id.check_last_name);
        if (last_name.length() < 2) {
            msg = "אורך שם המשפחה קצר מ-2 תווים";
            valid = false;
        }
        check_last_name.setText(msg);
        return valid;
    }

    public boolean checkDate(String date) {
        String msg = "";
        check_birthday = (TextView)findViewById(R.id.check_birthday);
        boolean valid = true;
        if (date.equals("תאריך לידה")) {
            msg = "נא לבחור תאריך לידה";
            valid = false;
        }
        check_birthday.setText(msg);
        return valid;
    }

    public boolean checkSchool(String school) {
        String msg = "";
        check_school = (TextView)findViewById(R.id.check_school);
        boolean valid = true;
        if (school.equals("בית ספר")) {
            msg = "נא לבחור בית ספר";
            valid = false;
        }
        check_school.setText(msg);
        return valid;
    }

    public boolean checkId(String id) {
        String msg = "";
        check_id = (TextView)findViewById(R.id.check_id);
        boolean valid = true;
        if (id.length() != 9) {
            msg = "אורך תעודת זהות הוא 9 תווים";
            valid = false;
        }
        else if (!id.matches("[0-9]+")) {
            msg = "תעודת זהות צריכה להכיל רק מספרים";
            valid = false;
        }
        check_id.setText(msg);
        return valid;
    }

    public boolean checkPassword(String password, String same_password) {
        String msg = "";
        check_password = (TextView)findViewById(R.id.check_password);
        boolean valid = true;
        if (password.length() < 6 || password.length() > 8) {
            msg = "הסיסמה צריכה להיות באורך של 6-8 תווים";
            valid = false;
        }
        else if (!password.matches("[a-zA-Z]*[0-9]+[a-zA-Z]*")) {
            msg = "הסיסמה צריכה להיות באנגלית וחייבת לכלול מספר אחד לפחות";
            valid = false;
        }
        else if (password.matches("[0-9]+")) {
            msg = "הסיסמה חייבת להכיל אותיות באנגלית";
            valid = false;
        }
        check_password.setText(msg);
        msg = "";
        check_same_password = (TextView)findViewById(R.id.check_same_password);
        if (!password.equals(same_password)) {
            msg = "הסיסמאות לא תואמות";
            valid = false;
        }
        check_same_password.setText(msg);
        return valid;
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