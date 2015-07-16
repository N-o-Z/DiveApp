package com.example.nozery.diveapp;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

//List imports


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BoardFragment.OnBoardInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DiveMapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BoardFragment extends Fragment {

    private ExpandableListView mExpandableList;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnBoardInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DiveMapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BoardFragment newInstance(String param1, String param2) {
        BoardFragment fragment = new BoardFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public BoardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Set title bar
        ((MainActivity) getActivity())
                .setActionBarTitle("Divers Board");

        View view = inflater.inflate(R.layout.fragment_board, container, false);
        mExpandableList = (ExpandableListView)view.findViewById(R.id.expandable_list);
        populateList();
        ArrayList<Parent> arrayParents = new ArrayList<Parent>();
        ArrayList<String> arrayChildren = new ArrayList<String>();

        //here we set the parents and the children
        for (int i = 0; i < 10; i++){
            //for each "i" create a new Parent object to set the title and the children
            Parent parent = new Parent();
            parent.setTitle("Parent " + i);

            arrayChildren = new ArrayList<String>();
            for (int j = 0; j < 10; j++) {
                arrayChildren.add("Child " + j);
            }
            parent.setArrayChildren(arrayChildren);

            //in this array we add the Parent object. We will use the arrayParents at the setAdapter
            arrayParents.add(parent);
        }

        //sets the adapter that provides data to the list.
        mExpandableList.setAdapter(new DiversBoardAdapter(this.getActivity(),arrayParents));

        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onBoardInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnBoardInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    interface OnBoardInteractionListener {
        // TODO: Update argument type and name
        void onBoardInteraction(Uri uri);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Auto-generated method stub
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.board_activity_actions, menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.action_settings:
                // do s.th.
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void populateList() {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("GameScore");
        query.whereEqualTo("playerName", "Dan Stemkoski");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> scoreList, ParseException e) {
                if (e == null) {
                    Log.d("score", "Retrieved " + scoreList.size() + " scores");
                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }
            }
        });
    }

    //Describes an entry in the list
    protected class Parent {
        private String mTitle;
        private ArrayList<String> mArrayChildren;

        public String getTitle() {
            return mTitle;
        }

        public void setTitle(String title) {
            mTitle = title;
        }

        public ArrayList<String> getArrayChildren() {
            return mArrayChildren;
        }

        public void setArrayChildren(ArrayList<String> arrayChildren) {
            mArrayChildren = arrayChildren;
        }
    }

    //Extended adapter for the list
    protected class DiversBoardAdapter extends BaseExpandableListAdapter {


        private LayoutInflater inflater;
        private ArrayList<Parent> mParent;

        public DiversBoardAdapter(Context context, ArrayList<Parent> parent){
            mParent = parent;
            inflater = LayoutInflater.from(context);
        }


        @Override
        //counts the number of group/parent items so the list knows how many times calls getGroupView() method
        public int getGroupCount() {
            return mParent.size();
        }

        @Override
        //counts the number of children items so the list knows how many times calls getChildView() method
        public int getChildrenCount(int i) {
            return mParent.get(i).getArrayChildren().size();
        }

        @Override
        //gets the title of each parent/group
        public Object getGroup(int i) {
            return mParent.get(i).getTitle();
        }

        @Override
        //gets the name of each item
        public Object getChild(int i, int i1) {
            return mParent.get(i).getArrayChildren().get(i1);
        }

        @Override
        public long getGroupId(int i) {
            return i;
        }

        @Override
        public long getChildId(int i, int i1) {
            return i1;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        //in this method you must set the text to see the parent/group on the list
        public View getGroupView(int groupPosition, boolean b, View view, ViewGroup viewGroup) {

            ViewHolder holder = new ViewHolder();
            holder.groupPosition = groupPosition;

            if (view == null) {
                view = inflater.inflate(R.layout.list_item_parent, viewGroup,false);
            }

            TextView textView = (TextView) view.findViewById(R.id.list_item_text_view);
            textView.setText(getGroup(groupPosition).toString());

            view.setTag(holder);

            //return the entire view
            return view;
        }

        @Override
        //in this method you must set the text to see the children on the list
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View view, ViewGroup viewGroup) {

            ViewHolder holder = new ViewHolder();
            holder.childPosition = childPosition;
            holder.groupPosition = groupPosition;

            if (view == null) {
                view = inflater.inflate(R.layout.list_item_child, viewGroup,false);
            }

            TextView textView = (TextView) view.findViewById(R.id.list_item_text_child);
            textView.setText(mParent.get(groupPosition).getArrayChildren().get(childPosition));

            view.setTag(holder);

            //return the entire view
            return view;
        }

        @Override
        public boolean isChildSelectable(int i, int i1) {
            return true;
        }

        @Override
        public void registerDataSetObserver(DataSetObserver observer) {
        /* used to make the notifyDataSetChanged() method work */
            super.registerDataSetObserver(observer);
        }

// Intentionally put on comment, if you need on click deactivate it
/*  @Override
    public void onClick(View view) {
        ViewHolder holder = (ViewHolder)view.getTag();
        if (view.getId() == holder.button.getId()){

           // DO YOUR ACTION
        }
    }*/


        protected class ViewHolder {
            protected int childPosition;
            protected int groupPosition;
            protected Button button;
        }
    }
}
