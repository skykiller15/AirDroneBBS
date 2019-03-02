package com.kurofish.airdronebbs.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.kurofish.airdronebbs.activities.LoginActivity;
import com.kurofish.airdronebbs.R;
import com.kurofish.airdronebbs.activities.MyActsActivity;
import com.kurofish.airdronebbs.activities.MyChatPostsActivity;
import com.kurofish.airdronebbs.activities.MyOrdersActivity;
import com.kurofish.airdronebbs.activities.MyTechPostsActivity;
import com.kurofish.airdronebbs.activities.MyVideosActivity;

import java.util.Objects;
import java.util.Random;

public class MeFragment extends Fragment {
    private Button meLogoutButton;
    private TextView avatarTV;
    private TextView userNameTV;
    private TextView emailTV;
    private TextView myTechPostsTV;
    private TextView myChatPostsTV;
    private TextView myVideosTV;
    private TextView myOrdersTV;
    private TextView myActivitiesTV;
    private FirebaseAuth mAuth;
    private CardView myTechPostsCV;
    private CardView myChatPostsCV;
    private CardView myVideosCV;
    private CardView myOrdersCV;
    private CardView myActivitiesCV;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_me, container, false);

        mAuth = FirebaseAuth.getInstance();

        meLogoutButton = view.findViewById(R.id.meLogoutButton);
        avatarTV = view.findViewById(R.id.meAvatarTextView);
        userNameTV = view.findViewById(R.id.meUserNameTextView);
        emailTV = view.findViewById(R.id.meEmailTextView);

        myTechPostsTV = view.findViewById(R.id.meTechPostsTextView);
        myChatPostsTV = view.findViewById(R.id.meChatPostsTextView);
        myVideosTV = view.findViewById(R.id.meMyVideosTextView);
        myOrdersTV = view.findViewById(R.id.meMyOrdersTextView);
        myActivitiesTV = view.findViewById(R.id.meMyActivitiesTextView);

        myTechPostsCV = view.findViewById(R.id.meTechPostsCardView);
        myChatPostsCV = view.findViewById(R.id.meChatPostsCardView);
        myVideosCV = view.findViewById(R.id.meMyVideosCardView);
        myOrdersCV = view.findViewById(R.id.meMyOrdersCardView);
        myActivitiesCV = view.findViewById(R.id.meMyActivitiesCardView);

        String author = Objects.requireNonNull(mAuth.getCurrentUser()).getDisplayName();
        String avatar = Objects.requireNonNull(author).substring(0, 1).toUpperCase();
        avatarTV.setText(avatar);
        Random random = new Random();
        int r, g, b;
        r = random.nextInt(255);
        g = random.nextInt(255);
        b = random.nextInt(255);
        // if background is more likely to white
        if (r+g+b > 255*3/2) {
            avatarTV.setTextColor(Color.BLACK);
        } else {
            avatarTV.setTextColor(Color.WHITE);
        }
        avatarTV.setBackgroundColor(Color.rgb(r, g, b));
        userNameTV.setText(author);
        emailTV.setText(mAuth.getCurrentUser().getEmail());
        setIndentation(myTechPostsTV);
        setIndentation(myChatPostsTV);
        setIndentation(myVideosTV);
        setIndentation(myOrdersTV);
        setIndentation(myActivitiesTV);

        meLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                startActivity(new Intent(getActivity(), LoginActivity.class));
                Objects.requireNonNull(getActivity()).finish();
            }
        });

        myActivitiesCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MyActsActivity.class));
            }
        });

        myOrdersCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MyOrdersActivity.class));
            }
        });

        myTechPostsCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MyTechPostsActivity.class));
            }
        });

        myChatPostsCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MyChatPostsActivity.class));
            }
        });

        myVideosCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MyVideosActivity.class));
            }
        });
        return view;
    }

    private void setIndentation(TextView aimTV) {
        SpannableStringBuilder span = new SpannableStringBuilder("A" + aimTV.getText());
        span.setSpan(new ForegroundColorSpan(Color.TRANSPARENT), 0, 1,
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        aimTV.setText(span);
    }
}
