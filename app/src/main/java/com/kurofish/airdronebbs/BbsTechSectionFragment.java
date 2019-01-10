package com.kurofish.airdronebbs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class BbsTechSectionFragment extends Fragment {
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_bbs_section, container, false);

        BbsRVAdapter bbsRVAdapter = new BbsRVAdapter(getActivity(), String.valueOf(R.string.tech_collection_id));
        recyclerView = view.findViewById(R.id.bsRecyclerView);
        recyclerView.setAdapter(bbsRVAdapter);
        return view;
    }
}
