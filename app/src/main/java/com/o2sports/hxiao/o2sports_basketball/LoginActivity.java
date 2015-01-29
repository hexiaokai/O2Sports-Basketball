package com.o2sports.hxiao.o2sports_basketball;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableOperationCallback;
import com.microsoft.windowsazure.mobileservices.TableQueryCallback;
import com.o2sports.hxiao.o2sports_basketball.entity.Player;
import com.o2sports.hxiao.o2sports_basketball.entity.PlayerSkill;

import java.io.FileOutputStream;
import java.util.List;


public class LoginActivity extends ActionBarActivity {

    public static MobileServiceClient mClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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


    public void signClicked(View v)
    {
        try {
            mClient = new MobileServiceClient(
                    "https://o2service.azure-mobile.net/",
                    "qJNqJihCYMDTfwYsbHbfURxaOfUNwh32",
                    this
            );
        }
        catch (Exception e)
        {
            //messageDialog("Cannot connect to service");
        }

        final EditText mName = (EditText) findViewById(R.id.editText_user_name);
        EditText mPassword = (EditText) findViewById(R.id.editText_password);

        if(mName.getText().toString() == "")
        {
            // error message
            return;
        }
        else {

            MobileServiceTable<Player> mPlayer = mClient.getTable(Player.class);

            mPlayer.where()
                    .field("name").eq(mName.getText().toString())
                    .execute(new TableQueryCallback<Player>() {

                        public void onCompleted(List<Player> result,
                                                int count,
                                                Exception exception,
                                                ServiceFilterResponse response) {


                            if (exception == null && !result.isEmpty()) {
                                foundPlayer(result.get(0).id);
                            } else {
                                if (exception != null) {
                                    //messageDialog(exception.getMessage() + response.getContent());
                                } else {
                                    addPlayer(mName.getText().toString());
                                }


                            }
                        }
                    });
        }

    }

    public void foundPlayer(String id)
    {

        String FILENAME = "playerID";
        try
        {
            FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fos.write(id.getBytes());
            fos.close();
        }
        catch (Exception e)
        {
            // TODO
        }

        Intent mIntent = getIntent().putExtra("ID", id);

        setResult(20, mIntent);

        //this.onDestroy();
    }

    public void addPlayer(String name)
    {
        MobileServiceTable<Player> mPlayer = mClient.getTable(Player.class);
        MobileServiceTable<PlayerSkill> mPlayerSkill = mClient.getTable(PlayerSkill.class);

        final Player p = new Player();
        p.name = name;
        p.gender = true;
        p.height = 180;
        p.weight = 150;
        p.position = 3;

        mPlayer.insert(p, new TableOperationCallback<Player>() {
            public void onCompleted(Player entity,
                                    Exception exception,
                                    ServiceFilterResponse response) {
                if (exception == null) {
                    p.id = entity.id;
                }
                else
                {
                    //messageDialog(exception.getMessage() + response.getContent());
                }
            }
        });


        PlayerSkill ps = new PlayerSkill();
        ps.playerID = p.id;
        ps.scoreCount = 0;
        ps.setDefaultScore(5);

        mPlayerSkill.insert(ps, new TableOperationCallback<PlayerSkill>() {
            public void onCompleted(PlayerSkill entity,
                                    Exception exception,
                                    ServiceFilterResponse response) {
                if (exception == null) {
                    //TODO
                }
                else
                {
                    //messageDialog(exception.getMessage() + response.getContent());
                }
            }
        });


        foundPlayer(p.id);
    }
}
