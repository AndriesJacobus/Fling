package com.fling.jaco.fling;

import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Jaco on 2017/11/11.
 */

public class Messages
{
    //Database vars
    private FirebaseDatabase database;
    private DatabaseReference messageRef;

    //Location vars
    private Location currLocation;

    //Message vars
    ArrayList<Message> messages = new ArrayList<>();

    public Messages (Location _currL)
    {
        database = FirebaseDatabase.getInstance();
        messageRef = database.getReference("message");

        currLocation = _currL;

        //getMessages
        getMessagesWithCurrentLocation();
    }

    public void updateLocation(Location location)
    {
        if (!currLocation.equals(location))
        {
            currLocation = location;
            messages = new ArrayList<>();
            getMessagesWithCurrentLocation();
        }
    }

    private void getMessagesWithCurrentLocation()
    {
        messageRef.addChildEventListener(new ChildEventListener()
        {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey)
            {
                final Message temp = new Message();
                final String currMessageLocation = dataSnapshot.child("location").getValue(String.class);
                final int range = dataSnapshot.child("range").getValue(Integer.class);

                temp.MId = dataSnapshot.getKey().toString();
                temp.ConId = dataSnapshot.child("ConId").getValue(String.class);
                temp.ViewId = dataSnapshot.child("ViewId").getValue(String.class);
                temp.dateCreated = dataSnapshot.child("dateCreated").getValue(String.class);
                temp.isInGroup = dataSnapshot.child("isInGroup").getValue(Boolean.class);
                temp.LId = currMessageLocation;
                temp.range = range;

                database.getReference("location/" + currMessageLocation).addValueEventListener(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        //dataSnapshot
                        //Log.i("Firebase", "dataSnapshot: " + dataSnapshot.child("lat").getValue(Double.class));

                        double lat = dataSnapshot.child("lat").getValue(Double.class);
                        double lon = dataSnapshot.child("long").getValue(Double.class);

                        if (locationIsInRangeOfCurrent(lat, lon, range))
                        {
                            //Add message to list
                            messages.add(temp);

                            //print all messages
                            Log.i("Firebase", "Messages: " + Arrays.toString(messages.toArray()));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError)
                    {
                        System.out.println("The read failed: " + databaseError.getCode());
                    }
                });
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {}

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private boolean locationIsInRangeOfCurrent(double _lat, double _lon, int range)
    {
        Location marker = new Location("Marker");
        marker.setLatitude(_lat);
        marker.setLongitude(_lon);

        Log.i("Firebase", "Distance: " + marker.distanceTo(currLocation) + ", range: " + range + ". Compare: " + (marker.distanceTo(currLocation) <= (float)range));

        return (marker.distanceTo(currLocation) <= (float)range);
    }

    private class Message
    {
        public String MId;
        public String ConId;
        public String ViewId;
        public String dateCreated;
        public boolean isInGroup;
        public String LId;
        public int range;

        public Message ()
        {

        }

        public Message (String _MId, String _ConId, String _ViewId, String _dateCreated, boolean _isInGroup, String _LId, int _range)
        {
            MId = _MId;
            ConId = _ConId;
            ViewId = _ViewId;
            dateCreated = _dateCreated;
            isInGroup = _isInGroup;
            LId = _LId;
            range = _range;
        }

        @Override
        public String toString ()
        {
            return MId;
        }

        public boolean compareTo (Message obj)
        {
             return (this.MId.equals(obj.MId));
        }
    }
}
