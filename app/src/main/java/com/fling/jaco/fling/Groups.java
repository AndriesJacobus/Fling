package com.fling.jaco.fling;

import android.location.Location;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by Jaco on 2017/11/11.
 */

public class Groups
{
    //Database vars
    private FirebaseDatabase database;
    private DatabaseReference locationRef;

    //Location vars
    private Location currLocation;

    //Group vars
    ArrayList<GroupInformation> groups = new ArrayList<>();

    public Groups()
    {
        database = FirebaseDatabase.getInstance();
        locationRef = database.getReference("groups");
    }

    public Groups(Location location)
    {
        database = FirebaseDatabase.getInstance();
        locationRef = database.getReference("groups");
        currLocation = location;
    }

    public void updateLocation(Location location)
    {
        currLocation = location;
    }

    private class GroupInformation
    {
        public String GId;
        public String LId;
        public ArrayList<String> messages;
        public int range;

        public GroupInformation (String gid, String lid, int _range)
        {
            GId = gid;
            LId = lid;
            messages = new ArrayList<>();
            range = _range;
        }

        public void addMessage(String MId)
        {
            messages.add(MId);
        }
    }
}
