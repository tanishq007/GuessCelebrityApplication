package com.tanishqmittal.guesscelebrityapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    String[] imagesArray = new String[100];
    String[] namesArray = new String[100];
    Button btn1;
    Button btn2;
    Button btn3;
    Button btn4;
    Button all[] = new Button[4];
    int correctButton = -1;
    int totalNames = -1;

    public class DownloadImage extends  AsyncTask<String, Void, Bitmap> {


        @Override
        protected Bitmap doInBackground(String... urls) {

            try {
                URL url = new URL(urls[0]);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.connect();

                InputStream inputStream = connection.getInputStream();

                Bitmap myBitmap = BitmapFactory.decodeStream(inputStream);

                return myBitmap;



            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;

        }
    }

    public class DownloadTask extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... urls) {

            try {

                String result = "";

                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                //connection.connect();

                InputStream inputStream = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream);

                int data = reader.read();

                while(data != -1) {

                    char current = (char) data;

                    result += current;

                    data = reader.read();

                }


                return result;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;


        }
    }

    public int getRandomNumber(int a, int b) {

        Random rand = new Random();

        int  n = rand.nextInt(a);

        if(n == b) {

            if(n == totalNames) {
                return n - 1;
            } else {

                return n + 1;
            }

        }

        return n;

    }

    public void getName(int i) {

        // setting right option
        int n = getRandomNumber(4, -1);
        all[n].setText(namesArray[i]);
        correctButton = n;


        // setting wrong options
        int a = 4;
        while(a != 0) {

            if( a == n + 1) {
                a--;
                continue;
            }

            int b = getRandomNumber(totalNames, i);
            all[a - 1].setText(namesArray[b]);
            a--;


        }



    }

    public void getImage() {

        int i = getRandomNumber(totalNames, -1);

        DownloadImage image = new DownloadImage();
        Bitmap myBitmap;

        try {

            myBitmap = image.execute(imagesArray[i]).get();
            imageView.setImageBitmap(myBitmap);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        getName(i);


    }


    public void buttonPressed(View view) {

        Button btn = (Button) findViewById(view.getId());
        //String text = btn.getText().toString();

        if(btn == all[correctButton]) {

            Toast.makeText(this, "Correct Answer !", Toast.LENGTH_SHORT).show();
            getImage();
        } else {

            Toast.makeText(this, "Wrong ! Correct ans is " + all[correctButton].getText(), Toast.LENGTH_SHORT).show();
            getImage();
        }

        //Log.i("Button Pressed", text);



    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn1 = (Button) findViewById(R.id.button1);
        btn2 = (Button) findViewById(R.id.button2);
        btn3 = (Button) findViewById(R.id.button3);
        btn4 = (Button) findViewById(R.id.button4);
        all[0] = btn1;
        all[1] = btn2;
        all[2] = btn3;
        all[3] = btn4;


        imageView = (ImageView) findViewById(R.id.imageView);

        DownloadTask task = new DownloadTask();

        try {
            String result = task.execute("http://www.posh24.se/kandisar").get();

            Pattern p = Pattern.compile("<img src=\"(.*?)\" alt=");
            Matcher m = p.matcher(result);
            int i = 0;

            while(m.find()) {

                imagesArray[i++] = m.group(1);
                Log.i("ImagesURL", m.group(1));
            }

            totalNames = i;

            p = Pattern.compile("alt=\"(.*?)\"/>");
            m = p.matcher(result);
            i = 0;

            while(m.find()) {

                namesArray[i++] = m.group(1);
                Log.i("Names", m.group(1));

            }


            //Log.i("WEB CONTENT", result);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        getImage();



    }
}
