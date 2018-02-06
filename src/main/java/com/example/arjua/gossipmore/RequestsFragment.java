package com.example.arjua.gossipmore;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestsFragment extends Fragment {

    private RecyclerView mFriendsList;

    private DatabaseReference mNotificationDatabase;
    private DatabaseReference mUsersDatabase;

    private FirebaseAuth mAuth;

    private String mCurrent_user_id;

    private View mMainView;



    public RequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mMainView = inflater.inflate(R.layout.fragment_requests, container, false);

        mFriendsList = (RecyclerView) mMainView.findViewById(R.id.req_list);
        mAuth = FirebaseAuth.getInstance();

        mCurrent_user_id = mAuth.getCurrentUser().getUid();

        mNotificationDatabase = FirebaseDatabase.getInstance().getReference().child("notifications").child(mCurrent_user_id);
        mNotificationDatabase.keepSynced(true);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersDatabase.keepSynced(true);


        mFriendsList.setHasFixedSize(true);
        mFriendsList.setLayoutManager(new LinearLayoutManager(getContext()));

        // Inflate the layout for this fragment
        return mMainView;


    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Request,ReqViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Request, ReqViewHolder>(
                Request.class,R.layout.users_single_layout,ReqViewHolder.class,mNotificationDatabase
        ) {
            @Override
            protected void populateViewHolder(final ReqViewHolder viewHolder, Request model, int position) {

                viewHolder.setDate(model.getDate());

                final String list_user=getRef(position).getKey();
                mUsersDatabase.child(list_user).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String username=dataSnapshot.child("name").getValue().toString();
                        String userth=dataSnapshot.child("thumb_image").getValue().toString();
                        if(dataSnapshot.hasChild("online"))
                        {
                            String userOnline=dataSnapshot.child("online").getValue().toString();
                            viewHolder.setUserOnline(userOnline);
                        }
                        viewHolder.setName(username);
                        viewHolder.setUserImage(userth, getContext());

                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                CharSequence options[] = new CharSequence[]{"Open Profile"};

                                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                                builder.setTitle("Select Options");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        //Click Event for each item.
                                        if(i == 0){

                                            Intent profileIntent = new Intent(getContext(), ProfileActivity.class);
                                            profileIntent.putExtra("user_id", list_user);
                                            startActivity(profileIntent);

                                        }



                                    }
                                });

                                builder.show();

                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };

        mFriendsList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class ReqViewHolder extends RecyclerView.ViewHolder
    {
        View mView;

        public ReqViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setUserOnline(String userOnline) {

            ImageView userOnlineView = (ImageView) mView.findViewById(R.id.user_single_online_icon);

            if(userOnline.equals("true")){

                userOnlineView.setVisibility(View.VISIBLE);

            } else {

                userOnlineView.setVisibility(View.INVISIBLE);

            }
        }
        public void setName(String username) {
            TextView userNameView = (TextView) mView.findViewById(R.id.user_single_name);
            userNameView.setText(username);
        }
        public void setUserImage(String userth,Context ctx) {

            CircleImageView userImageView = (CircleImageView) mView.findViewById(R.id.user_single_image);
            Picasso.with(ctx).load(userth).placeholder(R.drawable.default_avatar).into(userImageView);
        }

        public void setDate(String date) {
            TextView userStatusView = (TextView) mView.findViewById(R.id.user_single_status);
            userStatusView.setText("ACCEPT REQUEST");
        }
    }
}

