package com.example.video_player_1.com.sdcode.videoplayer.Adaptors;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ArrayRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.video_player_1.R;

import java.io.File;
import java.util.ArrayList;

import com.example.video_player_1.com.sdcode.videoplayer.Folders.folderItem;

import com.example.video_player_1.com.sdcode.videoplayer.globalVar;
import com.example.video_player_1.com.sdcode.videoplayer.kxUtil.kxUtil;
import com.example.video_player_1.com.sdcode.videoplayer.folderDetailActivity;

public class folderAdaptor extends RecyclerView.Adapter<folderAdaptor.Items>
{

    int signal=0;
    ArrayList<folderItem> folders=new ArrayList<>();
    // UI Launching

    private static  Activity activity;

    public folderAdaptor(Activity activity_1)
    {
        if(activity_1!=null)
        {
            signal=1;
            activity=activity_1;
        }
    }

    @NonNull
    @Override
    public Items onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_folder,null);

        return (new Items(v));
        //return null;
    }

    @Override
    public void onBindViewHolder(@NonNull Items holder, int position) {
        // get the element at specific position
        folderItem item=folders.get(position);

        File f=new File(item.getParent());
        holder.name.setText(f.getName());
        holder.path.setText(item.getParent());

        //int total_folder_size=kxUtil.size(item.getTotalFolderSize());

        holder.num_of_videos.setText("  Videos : "+item.first.size()+"          Size : "+item.getTotalFolderSize());

        /* Three Dots and click are remaining  */

        // If Any Container Gets Clicked
        holder.click.setOnClickListener(v -> {
            // Allocate the List of Videos from Current Cell
            globalVar.launchVideo=item.getVideos();
            if(signal==1)
            {

                globalVar.foldername=f.getName();
                launch();
            }

        });



    }

    @Override
    public int getItemCount() {
        return folders.size();
    }

    public class Items extends RecyclerView.ViewHolder {

        View click;
        TextView name,path,num_of_videos;
        ImageView three_dots;

        public Items(@NonNull View itemView) {
            super(itemView);
            click=itemView;
            // get name
            name=itemView.findViewById(R.id.txtFolderName);
            // get path
            path=itemView.findViewById(R.id.txtFolderPath);
            // get number of videos
            num_of_videos=itemView.findViewById(R.id.txtFolderSize);
            // Three Dots

            // This One Do It Later
            three_dots=itemView.findViewById(R.id.imageViewOption);
        }
    }

    public  void  update(ArrayList<folderItem> first)
    {
        int prev,current;
        prev=folders.size();
        current=first.size();

        // first Time being Executed
        if(prev==0)
        {
            folders.addAll(first);
        }
        // Update Due to sort
        else
        {

            folders.clear();
            folders.addAll(first);
            notifyItemRangeRemoved(0,prev);
            notifyItemRangeInserted(0,current);


        }

        folders=first;
    }

    // for debugging

    private void Message_1(String m)
    {
        int duration= Toast.LENGTH_SHORT;
        Toast t=Toast.makeText(activity,m,duration);
        t.show();
    }

    private void launch()
    {

        Intent intent=new Intent(activity,folderDetailActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // Launch Correspond Activity


        // start the activity
        activity.startActivity(intent);

        // Animation

        // entry and exit animation
        activity.overridePendingTransition(R.anim.slide_in_left,R.anim.slide_stay_x);

    }

    // for debugging
}