package com.example.video_player_1.com.sdcode.videoplayer.Folders;

import java.io.File;
import java.util.ArrayList;
import com.example.video_player_1.com.sdcode.videoplayer.Videos.videoItem;
import com.example.video_player_1.com.sdcode.videoplayer.kxUtil.kxUtil;

public class folderItem
{
    String name,path;
    public ArrayList<videoItem> first=new ArrayList<>();
    // On Adding First Element use constructor
    folderItem(videoItem element)
    {
        this.name=element.ParentName();
        // Its an Path of video but We car comparing with parent so at the end both are same
        this.path=element.getPath();
        first.add(element);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getParent() // for comparison path
    {
        File f=new File(path);
        return f.getParent();
    }
    public void addElement(videoItem v)
    {
        first.add(v);
    }
    public String getTotalFolderSize()
    {
        String s;
        long sum=0;
        for(videoItem v:first)
        {
            sum+=v.getFile_size_float();
        }
        s=""+sum;
        s=kxUtil.size(sum);
        return s;
    }
    public ArrayList<videoItem> getVideos()
    {
        return first;
    }

}
