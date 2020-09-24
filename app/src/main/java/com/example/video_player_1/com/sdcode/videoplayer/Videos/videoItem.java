package com.example.video_player_1.com.sdcode.videoplayer.Videos;

import java.io.File;

public class videoItem
{
    private String name,folder,path,filesize,resolution,date,duration;
     private Long file_size_float;

    // we gonna take reverse so Its Selected or vise versa
   public boolean longClick=false,selected=false;

    videoItem(String name,String folder,String path,String filesize,String resolution,String date,String duration,Long file_size_float)
    {
        this.name=name;
        this.folder=folder;
        this.path=path;
        this.filesize=filesize;
        this.resolution=resolution;
        this.date=date;
        this.duration=duration;
        this.file_size_float=file_size_float;

    }

    public videoItem(String path)
    {
        this.path=path;
        this.name=(new File(path)).getName();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFilesize() {
        return filesize;
    }

    public void setFilesize(String filesize) {
        this.filesize = filesize;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getParent()
    {
        File f=new File(path);
        return f.getParent();
    }

    public Long getFile_size_float() {
        return file_size_float;
    }

    public String extension()
    {
        // This one gonna Split The . and last Occurrence is our extension
        String arr[]=path.split("\\.");
        // last one is our extension
        return  arr[arr.length-1];
    }

    public String ParentName()
    {
        String UnknownName=" Unknown";
        File f=new File(path);
        if(f.getParent()!=null)
        {
            f = new File(f.getParent());
            return f.getName();
        }
        else
        {
            return UnknownName;
        }

        //return parentName;

    }

}
