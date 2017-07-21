package com.example.chiangj.rxjavaexample;

import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.Gson;

import org.reactivestreams.Subscriber;

import java.io.IOException;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getMemberObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Member[]>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Member[] members) {
                        StringBuilder builder = new StringBuilder();
                        for(Member m : members){
                            builder.append(m.login);
                            builder.append("\n");
                        }
                        TextView textView = (TextView) findViewById(R.id.text_rxjava_result);
                        textView.setText(builder.toString());
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e(TAG, e.getMessage(), e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });

        /*new AsyncTask<Void, Void, Member[]>(){

            @Override
            protected Member[] doInBackground(Void... params) {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url("https://api.github.com/orgs/square/members")
                        .build();
                try {
                    Response response = client.newCall(request).execute();

                    if(response.isSuccessful()){
                        Member[] members = new Gson().fromJson(response.body().charStream(), Member[].class);
                        return members;
                    }
                } catch (IOException e) {
                    return null;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Member[] members) {
                super.onPostExecute(members);

                StringBuilder builder = new StringBuilder();
                for(Member m : members){
                    builder.append(m.login);
                    builder.append("\n");
                }
                TextView textView = (TextView) findViewById(R.id.text_rxjava_result);
                textView.setText(builder.toString());
            }
        }.execute();*/
    }

    @Nullable
    private Member[] getMembers() throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.github.com/orgs/square/members")
                .build();
            Response response = client.newCall(request).execute();

            if(response.isSuccessful()){
                Member[] members = new Gson().fromJson(response.body().charStream(), Member[].class);
                return members;
            }

        return null;
    }

    public Observable<Member[]> getMemberObservable(){
        return Observable.defer(new Callable<ObservableSource<? extends Member[]>>() {
            @Override
            public ObservableSource<? extends Member[]> call() throws Exception {
                try{
                    return Observable.just(getMembers());
                }catch (IOException e){
                    return null;
                }
            }
        });

    }
}
