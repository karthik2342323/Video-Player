package com.example.video_player_1.com.sdcode.videoplayer.Adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.video_player_1.R;

import net.steamcrafted.materialiconlib.MaterialIconView;
import  com.example.video_player_1.com.sdcode.videoplayer.Videos.videoItem;
import  com.example.video_player_1.com.sdcode.videoplayer.globalVar;

import java.util.ArrayList;

public class trackListAdaptor extends RecyclerView.Adapter<trackListAdaptor.kk>
{
    int size=0;
    public Context context;
    private ArrayList<videoItem> videos=new ArrayList<>();

    public trackListAdaptor(Context context)
    {
        this.context=context;
    }

    // UI Launching
    @NonNull
    @Override
    public kk onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // for debugging

        //Message_1(" Track UI working");

        // for debugging


        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_playing,null);

        return new kk(view);
    }

    @Override
    public void onBindViewHolder(@NonNull kk holder, int position) {



        videoItem video=videos.get(position);
        Glide.with(context).load(video.getPath()).into(holder.thumbNail);
        holder.duration.setText(video.getDuration());
        holder.name.setText(video.getName());
        holder.path.setText(video.getPath());

        holder.click.setOnClickListener(v -> {
            globalVar.videoservice.playVideo(video,false);
        });

        // DO It Later
        holder.remove.setOnClickListener(v -> {
            // Atleast One video should be Remain in Least
            if(videos.size()>=2)
            {
                size=videos.size();
                videos.remove(video);

                // This one is if we are parameterizing Itself while using  clear() make Clear Itself so Needed New List
                ArrayList<videoItem> list=new ArrayList<>();
                list.addAll(videos);

                update(list);
            }
            else
            {
               Message_1(" Aleast one video should remain in List ");
            }

        });




    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    public class kk extends RecyclerView.ViewHolder {
        View click;
        ImageView thumbNail;
        TextView duration,name,path;
        MaterialIconView remove;
        public kk(@NonNull View itemView) {
            super(itemView);
            this.click=itemView;
            thumbNail=itemView.findViewById(R.id.imageView);
            duration=itemView.findViewById(R.id.txtVideoDuration);
            name=itemView.findViewById(R.id.txtVideoTitle);
            path=itemView.findViewById(R.id.txtVideoPath);
            remove=itemView.findViewById(R.id.btn_remove_to_playingList);
        }
    }

    public void update(ArrayList<videoItem> videos)
    {
        globalVar.updatedTrack=videos;

        // first time being updated
        if(size==0)
        {
            this.videos.addAll(videos);
            notifyItemRangeInserted(0,size);
        }
        // After Removing Any Element in List
        else
        {
            this.videos.clear();
            this.videos.addAll(videos);

            //Message_1(" Initial Length : "+size+" Updated Length : "+videos.size());

            notifyItemRangeRemoved(0,size);
            notifyItemRangeInserted(0,videos.size());




        }



      //  Message_1(" Size : "+this.videos.size());
    }

    // for debugging

    private void Message_1(String m)
    {
        Context context=this.context;
        int duration= Toast.LENGTH_SHORT;
        Toast t=Toast.makeText(context,m,duration);
        t.show();
    }

    // for debugging
}
