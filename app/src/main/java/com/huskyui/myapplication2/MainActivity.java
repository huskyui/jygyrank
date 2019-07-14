package com.huskyui.myapplication2;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;



public class MainActivity extends AppCompatActivity {
    private EditText cardNo;
    private Button getInfoBtn;
    private TextView rankText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initArgs();
        getInfoBtn.setOnClickListener((v)->{
            switch (v.getId()){
                case R.id.clickBtn:
                    getInfo();
                    break;
            }
        });
        SharedPreferences preferences=getSharedPreferences("user", Context.MODE_PRIVATE);
        String id = preferences.getString("id",null);
        if(id != null){
            cardNo.setText(id);
            netWork(id);
        }
    }

    public void initArgs(){
        cardNo = (EditText) findViewById(R.id.idCard);
        getInfoBtn = (Button) findViewById(R.id.clickBtn);
        rankText = (TextView)findViewById(R.id.rank);
    }

    public void getInfo() {

        String id = cardNo.getText().toString();
        Log.i("id",id);
        netWork(id);
        SharedPreferences preferences=getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit= preferences.edit();
        edit.putString("id",id);
        edit.commit();
    }

    public void netWork(String id){
        JSONObject jsonObject =  new JSONObject();
        try {
            jsonObject.put("CertNo", id);
        }catch (Exception e){
            Log.e("exception",e.getMessage());
        }
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("http://ent.sipprh.com:8000/ModuleDefaultCompany/RentManage/SearchRentNo",
                jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("success", response.toString());
                StringBuilder rank= new StringBuilder();
                try {
                    String content = ((String)response.get("prompWord")).trim();
                    for (int i = 0; i < content.length(); i++) {
                        if (content.charAt(i) >= 48 && content.charAt(i) <= 57) {
                            rank.append(content.charAt(i));
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    rank.append("异常");
                }
                rankText.setText(rank.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("fail", error.getMessage(), error);
                rankText.setText(error.getMessage());
            }
        });
        queue.add(jsonObjectRequest);
    }



}
