package com.example.video_player_1.com.sdcode.videoplayer.Adaptors;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.video_player_1.R;
import com.example.video_player_1.com.sdcode.videoplayer.Videos.videoItem;
import com.example.video_player_1.com.sdcode.videoplayer.globalVar;
import com.example.video_player_1.com.sdcode.videoplayer.videoService;
import com.example.video_player_1.com.sdcode.videoplayer.videoPlayerMain;
import com.example.video_player_1.com.sdcode.videoplayer.kxUtil.kxUtil;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.example.video_player_1.com.sdcode.videoplayer.preference;

import java.io.File;
import java.util.ArrayList;

public class videoAdaptor extends RecyclerView.Adapter<videoAdaptor.items>
{
    Activity context;
    public videoAdaptor(Activity context)
    {
        this.context=context;
    }
    ArrayList<videoItem> videos=new ArrayList<>();

    public static int i=0;

    // UI Launcher
    @NonNull
    @Override
    public items onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
       // View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video,null);

        View v;

        if(new preference(context).getGrid())
        {
            v= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_grid,null);
        }
        else
        {
             v= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video,null);
        }

        // Grid Implementation is Remaining
        return (new items(v));
    }

    @Override
    public void onBindViewHolder(@NonNull items holder, int position) {
        videoItem video=videos.get(position);
        // Loading ThumbNail
        Glide.with(context.getApplicationContext()).load(video.getPath()).into(holder.thumbNail);

        // duration
        holder.duration.setText(video.getDuration());

        // title
        holder.title.setText(video.getName());

        // folder name with size

        File f=new File(video.getParent());

        holder.folder.setText(" "+f.getName()+"\t \t \t  Size : "+video.getFilesize());


        // for debugging

        holder.click.setBackgroundColor(video.selected? ContextCompat.getColor(context,R.color.multiselected) : Color.TRANSPARENT);

        // for debugging



        // On Click Container is remaining
        holder.click.setOnClickListener(v -> {


            if(!globalVar.multiselected) {

                // take whole arrayList for shuffling or playing next
                globalVar.videoTrack=videos;

                // For Updated according to Track
                globalVar.updatedTrack=videos;


                if (!globalVar.playAsPopup) {

                    // as popup for Debugging
                    globalVar.videoservice.playVideo(video, false);
                    // start activity as an intend
                    Intent intent = new Intent(context, videoPlayerMain.class);
                    context.startActivity(intent);
                }
                // directly start
                else {
                    globalVar.videoservice.playVideo(video, true);
                }
            }
            else
            {
                video.selected=!video.selected;
                holder.click.setBackgroundColor(video.selected? ContextCompat.getColor(context,R.color.multiselected) : Color.TRANSPARENT);
                if(video.selected)
                {
                    i++;
                }
                else
                {
                    i--;
                }

                if(i==0)
                {
                   globalVar.multiselected=false;
                }

            }





        });

        holder.click.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                globalVar.videoTrack=videos;
               // globalVar.updatedTrack=videos;


                globalVar.multiselected=true;
                video.selected=!video.selected;
                holder.click.setBackgroundColor(video.selected? ContextCompat.getColor(context,R.color.multiselected) : Color.TRANSPARENT);

                if(video.selected)
                {
                    i++;
                }
                else
                {
                    i--;
                }

                if(i==0)
                {
                    globalVar.multiselected=false;
                }





                return true;
            }
        });


        // Options
        holder.three_dots.setOnClickListener(v -> {
            globalVar.videoTrack=videos;
            globalVar.updatedTrack=videos;

            options(video);
        });


    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    public class items extends RecyclerView.ViewHolder {
        View click;

        ImageView thumbNail,three_dots;
        TextView duration;

        TextView title;

        TextView folder;



        public items(@NonNull View itemView) {
            super(itemView);

            click=itemView;
            thumbNail=itemView.findViewById(R.id.imageView);
            duration=itemView.findViewById(R.id.txtVideoDuration);
            title=itemView.findViewById(R.id.txtVideoTitle);
            folder=itemView.findViewById(R.id.txtVideoPath);
            three_dots=itemView.findViewById(R.id.imageViewOption);

        }
    }

    private void options(videoItem x)
    {
        View view=context.getLayoutInflater().inflate(R.layout.video_option_dialog,null);

        LinearLayout popup,properties,share,bg_music;

        BottomSheetDialog dialog=new BottomSheetDialog(context);

        popup=view.findViewById(R.id.option_play_popup);
        popup.setOnClickListener(v -> {
            globalVar.currentPath="";
            globalVar.videoservice.playVideo(x,true);

            dialog.dismiss();
        });

        properties=view.findViewById(R.id.option_info);
        properties.setOnClickListener(v -> {
            infoDialog(x);

            dialog.dismiss();
        });

        share=view.findViewById(R.id.option_share);
        share.setOnClickListener(v -> {
            context.startActivity(Intent.createChooser(kxUtil.shareSingleVideo(x,context),"Share Video"));

            dialog.dismiss();
        });

        bg_music=view.findViewById(R.id.option_play_audio);
        bg_music.setOnClickListener(v -> {
            globalVar.currentPath="";
            globalVar.videoservice.playVideo(x,false);

            dialog.dismiss();
        });

        // Set View
        dialog.setContentView(view);

        dialog.create();

        dialog.show();

    }

    public  void update(ArrayList<videoItem> first)
    {
        //videos=first;



        int prev,current;
        prev=videos.size();
        current=first.size();

        // first Time Being Executed
        if(prev==0)
        {

            videos.addAll(first);

            // for debugging

            notifyItemRangeRemoved(0,prev);
            notifyItemRangeInserted(0,current);

            // for debugging

        }
        else
        {

            videos.clear();
            videos.addAll(first);

            notifyItemRangeRemoved(0,prev);
            notifyItemRangeInserted(0,current);

        }
    }


    // snippets

    public void infoDialog(videoItem v)
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        View view=context.getLayoutInflater().inflate(R.layout.content_video_info,null);
        TextView title,path,format,size,duration,resolution,dateAndTime;
        title=view.findViewById(R.id.txtVideoTitle);
        title.setText(v.getName());
        path=view.findViewById(R.id.txtLocation_value);
        path.setText(v.getPath());
        format=view.findViewById(R.id.txtFormat_value);
        format.setText(v.extension());
        size=view.findViewById(R.id.txtFileSize_value);
        size.setText(v.getFilesize());
        duration=view.findViewById(R.id.txtDuration_value);
        duration.setText(v.getDuration());
        resolution=view.findViewById(R.id.txResolution_value);
        resolution.setText(v.getResolution());
        dateAndTime=view.findViewById(R.id.txtDateAdded_value);
        dateAndTime.setText(v.getDate());

        builder.setView(view);

        builder.create().show();


    }




    // snippets

    // for debugging

    private void Message_1(String m)
    {
        int duration= Toast.LENGTH_SHORT;
        Toast t=Toast.makeText(context,m,duration);
        t.show();
    }

    // for debugging
}
