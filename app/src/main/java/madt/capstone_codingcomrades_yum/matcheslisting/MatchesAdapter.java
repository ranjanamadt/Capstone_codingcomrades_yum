package madt.capstone_codingcomrades_yum.matcheslisting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

import madt.capstone_codingcomrades_yum.R;
import madt.capstone_codingcomrades_yum.User;

public class MatchesAdapter extends BaseAdapter {

    // on below line we have created variables
    // for our array list and context.
    private ArrayList<User> matchesList;
    private Context context;

    // on below line we have created constructor for our variables.
    public MatchesAdapter(List<User> matchesList, Context context) {
        this.matchesList = (ArrayList<User>) matchesList;
        this.context = context;
    }

    @Override
    public int getCount() {
        // in get count method we are returning the size of our array list.
        return 10;
    }

    @Override
    public Object getItem(int position) {
        // in get item method we are returning the item from our array list.
        return matchesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // in get item id we are returning the position.
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // in get view method we are inflating our layout on below line.
        View v = convertView;
        if (v == null) {
            // on below line we are inflating our layout.
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.matches_stack_card, parent, false);
        }
        // on below line we are initializing our variables and setting data to our variables.
/*        ((TextView) v.findViewById(R.id.idTVCourseName)).setText(matchesList.get(position).getCourseName());
        ((TextView) v.findViewById(R.id.idTVCourseDescription)).setText(matchesList.get(position).getCourseDescription());
        ((TextView) v.findViewById(R.id.idTVCourseDuration)).setText(matchesList.get(position).getCourseDuration());
        ((TextView) v.findViewById(R.id.idTVCourseTracks)).setText(matchesList.get(position).getCourseTracks());
        ((ImageView) v.findViewById(R.id.idIVCourse)).setImageResource(matchesList.get(position).getImgId());*/
        return v;
    }
}
