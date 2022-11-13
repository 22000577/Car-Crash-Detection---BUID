package com.example.carcrash;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.example.carcrash.databinding.ActivityMainBinding;
import android.view.Menu;
import android.view.MenuItem;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.net.URL;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    int CALL_PERMISSION_REQUEST_CODE = 1;
    private String server = "http://192.168.1.12:3000";
    private TextView mTextViewResult;
    private TextView mCountDownText;
    private View mLoadingIcon;
    private View mView;
    private View mUpdateText;
    private View mConnectedText;
    private View mSuccessIcon;
    private View mWarningIcon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        try {
            setStatus();
        } catch (IOException e) {
            e.printStackTrace();
        }

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.CALL_PHONE},
                            CALL_PERMISSION_REQUEST_CODE);
                    return;
                } else {
                    Snackbar.make(view, "You have already granted permission!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void setStatus() throws IOException {
        mView = findViewById(R.id.bg01);
        mTextViewResult = findViewById(R.id.textView01);
                OkHttpClient client = new OkHttpClient();
                String url = server;
                Request request = new Request.Builder()
                        .url(url)
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        System.out.println(call);
                        changeStatus("Fail");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            final String myResponse = response.body().string();
                            if(myResponse == "crashDetected") changeStatus("Warning");

                            MainActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    changeStatus("Success");
                                }
                            });
                        }
                    }
                });
    }

    public void changeStatus(String status) {
        mView = findViewById(R.id.bg01);
        mTextViewResult = findViewById(R.id.textView01);
        mUpdateText = findViewById(R.id.textView02);
        mConnectedText = findViewById(R.id.textView03);
        mCountDownText = findViewById(R.id.textView04);
        mLoadingIcon = findViewById(R.id.progressBar);
        mSuccessIcon = findViewById(R.id.imageView);
        mWarningIcon = findViewById(R.id.imageView02);

        if(status == "Fail"){
            mView.setBackgroundColor(Color.parseColor("#FF8F8F"));
            mTextViewResult.setText("Connection Failed!");
            mLoadingIcon.setVisibility(View.VISIBLE);
            mUpdateText.setVisibility(View.VISIBLE);
            mConnectedText.setVisibility(View.INVISIBLE);
            mSuccessIcon.setVisibility(View.INVISIBLE);
            mWarningIcon.setVisibility(View.INVISIBLE);
            mCountDownText.setVisibility(View.INVISIBLE);
        } else if(status == "Success"){
            mView.setBackgroundColor(Color.parseColor("#93F18C"));
            mTextViewResult.setVisibility(View.INVISIBLE);
            mLoadingIcon.setVisibility(View.INVISIBLE);
            mUpdateText.setVisibility(View.INVISIBLE);
            mConnectedText.setVisibility(View.VISIBLE);
            mSuccessIcon.setVisibility(View.VISIBLE);
            mWarningIcon.setVisibility(View.INVISIBLE);
            mCountDownText.setVisibility(View.INVISIBLE);
        } else {
            mView.setBackgroundColor(Color.parseColor("#FF8F8F"));
            mTextViewResult.setVisibility(View.INVISIBLE);
            mLoadingIcon.setVisibility(View.INVISIBLE);
            mUpdateText.setVisibility(View.INVISIBLE);
            mConnectedText.setVisibility(View.INVISIBLE);
            mSuccessIcon.setVisibility(View.INVISIBLE);
            mWarningIcon.setVisibility(View.VISIBLE);
            mCountDownText.setVisibility(View.VISIBLE);
            startCrashHandler();
        }

    }

    public void startCrashHandler()  {
        mCountDownText = findViewById(R.id.textView04);
        for(int i=10; i>=0; i--) {
            try {
                int finalI = i;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(finalI == 0){
                            Toast.makeText(getApplicationContext(),"SOS message has been sent!!!", Toast.LENGTH_SHORT).show();
                            try {
                                setStatus();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        mCountDownText.setText("" + finalI);
                    }
                });
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

    }

}