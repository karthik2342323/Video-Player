package com.example.video_player_1.com.sdcode.videoplayer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.Toast;
//import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.video_player_1.R;
import com.example.video_player_1.com.sdcode.videoplayer.customizeUI.WrapContentLinearLayoutManager;
import com.example.video_player_1.com.sdcode.videoplayer.Videos.videoItem;
import com.example.video_player_1.com.sdcode.videoplayer.Adaptors.videoAdaptor;

import java.util.ArrayList;

public class searchActivity extends baseAcitivity implements SearchView.OnQueryTextListener, View.OnTouchListener
{
    SearchView searchView;
    videoAdaptor v;



    static int i=0;
     String prev,current;

    private InputMethodManager inputMethodManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        inputMethodManager= (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        setContentView(R.layout.activity_search);
        Toolbar toolbar=findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        if(getSupportActionBar()!=null)
        {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        RecyclerView recyclerView=findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));

        v=new videoAdaptor(this);
        recyclerView.setAdapter(v);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.search_menu,menu);

        MenuItem menuItem=menu.findItem(R.id.action_search);


        menuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {

                finish();
                return false;
            }
        });

        searchView= (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint("Search");
        searchView.setIconifiedByDefault(false);
        searchView.setIconified(false);
        searchView.setMaxWidth(Integer.MAX_VALUE);

        menu.findItem(R.id.action_search).expandActionView();


        return true;

        //return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home)
        {
            finish();
            return true;
        }

        return false;
        //return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        hideInput();
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        onQueryTextChange(query);
        hideInput();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        int signal=1;
        current=newText.trim().toLowerCase();

        ArrayList<videoItem> first=new ArrayList<>();
        ArrayList<videoItem> allVideo=new ArrayList<>();

        allVideo.addAll(globalVar.allVideos);

        // First Time System Executed So Make both Empty
        if(i==0)
        {
            prev="";
            current="";


            i++;
            return false;
        }
        else
        {
            if(current==null)
            {
                current="";
            }
            if(prev==null)
            {
                prev="";
            }

            if(prev.equals(current) || current.equals(""))
            {
                return false;
            }
        }


        // Update Current
        prev=current;

        for(videoItem videoItem:allVideo)
        {
            if(videoItem.getName().toLowerCase().contains(current))
            {
                first.add(videoItem);
            }
        }

        // update

        v.update(first);




        i++;

        return true;
    }

    private void hideInput()
    {
        if(searchView!=null)
        {
            if(inputMethodManager!=null)
            {
                inputMethodManager.hideSoftInputFromWindow(searchView.getWindowToken(),0);
            }
            searchView.clearFocus();
        }
    }

    // for debugging

    private void Message_1(String m)
    {

        Context context=getApplicationContext();
        int duration= Toast.LENGTH_SHORT;
        Toast t=Toast.makeText(context,m,duration);
        t.show();
    }

    // for debugging
}
