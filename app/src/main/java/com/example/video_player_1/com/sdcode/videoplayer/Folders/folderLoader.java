package com.example.video_player_1.com.sdcode.videoplayer.Folders;

import com.example.video_player_1.com.sdcode.videoplayer.Videos.videoItem;

import java.io.File;
import java.util.ArrayList;

public class folderLoader
{
    ArrayList<folderItem> folders;
    int signal=0;
    public folderLoader(ArrayList<videoItem> videoItems)
    {
        int i=0;
        ArrayList<folderItem> folderItems=new ArrayList<>();
        if(videoItems!=null && videoItems.size()!=0)
        {
            for(videoItem each_video:videoItems)
            {

                i++;
                // adding first element
                if(i==1)
                {
                    folderItems.add(new folderItem(each_video));
                }
                // Need to Check whether path exist or not because
                // We dont need to make another dir for that
                else
                 {
                    int index = check(folderItems, each_video.getParent());
                    // If Its Found
                    if (index >= 0) {
                        // Add On That Folder
                        folderItems.get(index).addElement(each_video);
                    }
                    // If Its Not found Then Create New Folder And Add
                    else
                    {
                        folderItems.add(new folderItem(each_video));
                    }
                }
            }
        }
        // IF any video found
        if(folderItems!=null && folderItems.size()!=0)
        {
            folders=folderItems;
            signal=1;
        }
    }

    // check whether path exist means parent dir is same or check Its exist
    private int check(ArrayList<folderItem> first,String path)
    {
        int index=0;
        for(folderItem f:first)
        {
            // If Found
            if(f.getParent().equals(path))
            {
                return index;
            }
            index++;
        }
        // not found
        return -1;
    }

    public ArrayList<folderItem> getFolders()
    {
        return folders;
    }


}
