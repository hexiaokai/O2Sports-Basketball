package com.o2sports.hxiao.o2sports_basketball;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.microsoft.windowsazure.mobileservices.*;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PlayerProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PlayerProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlayerProfileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_Player_ID = "param1";

    // TODO: Rename and change types of parameters
    private String playerID;
    private Player currentPlayer;
    private PlayerSkill currentPlayerSkill;


    private MobileServiceTable<Player> mPlayerTable;
    private MobileServiceTable<PlayerSkill> mPlayerSkillTable;

    private OnFragmentInteractionListener mListener;

    private View currentView;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PlayerProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PlayerProfileFragment newInstance(String param1) {
        PlayerProfileFragment fragment = new PlayerProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_Player_ID, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public PlayerProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            playerID = getArguments().getString(ARG_Player_ID);
        }



        mPlayerTable = ((MainActivity)(this.getActivity())).mClient.getTable(Player.class);

        mPlayerTable.where().field("id").eq(playerID).execute(new TableQueryCallback<Player>() {
            public void onCompleted(List<Player> result,
                                    int count,
                                    Exception exception,
                                    ServiceFilterResponse response) {
                if (exception == null && !result.isEmpty()) {
                    currentPlayer = result.get(0);
                    ((TextView) currentView.findViewById(R.id.textView_name_value)).setText(currentPlayer.name);
                    ((TextView) currentView.findViewById(R.id.textView_gender_value)).setText(currentPlayer.gender ? "Male" : "Female");
                    ((TextView) currentView.findViewById(R.id.textView_height_value)).setText(((Double) currentPlayer.height).toString());
                    ((TextView) currentView.findViewById(R.id.textView_weight_value)).setText(((Double) currentPlayer.weight).toString());
                    ((TextView) currentView.findViewById(R.id.textView_position_value)).setText(((Integer) currentPlayer.position).toString());

                } else {
                    if (exception != null) {
                        messageDialog(exception.getMessage());
                    }
                    else
                    {
                        messageDialog("Player cannot be found");
                    }
                }
            }
        });

        mPlayerSkillTable = ((MainActivity)(this.getActivity())).mClient.getTable(PlayerSkill.class);

        mPlayerSkillTable.where().field("playerID").eq(playerID).execute(new TableQueryCallback<PlayerSkill>() {
            public void onCompleted(List<PlayerSkill> result,
                                    int count,
                                    Exception exception,
                                    ServiceFilterResponse response) {
                if (exception == null && !result.isEmpty()) {
                    currentPlayerSkill = result.get(0);
                    Double totalScore = currentPlayerSkill.totalScore();
                    ((TextView) currentView.findViewById(R.id.textView_score_overall_value)).setText(((Double) totalScore).toString());
                } else {
                    if (exception != null) {
                        messageDialog(exception.getMessage());
                    }
                    else
                    {
                        messageDialog("Player Score cannot be found");
                    }
                }
            }
        });


    }

    public void messageDialog(String dialogMessage)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
        builder.setMessage(dialogMessage);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                PlayerProfileFragment.this.getActivity().finish();
            }
        });
        builder.create().show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        currentView = inflater.inflate(R.layout.fragment_player_profile, container, false);
        return currentView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }


    }

    public void onResume()
    {
        super.onResume();
        if (currentPlayer != null)
        {
            ((TextView) currentView.findViewById(R.id.textView_name_value)).setText(currentPlayer.name);
            ((TextView) currentView.findViewById(R.id.textView_gender_value)).setText(currentPlayer.gender ? "Male" : "Female");
            ((TextView) currentView.findViewById(R.id.textView_height_value)).setText(((Double) currentPlayer.height).toString());
            ((TextView) currentView.findViewById(R.id.textView_weight_value)).setText(((Double) currentPlayer.weight).toString());
            ((TextView) currentView.findViewById(R.id.textView_position_value)).setText(((Integer) currentPlayer.position).toString());

            ((TextView) currentView.findViewById(R.id.textView_score_overall_value)).setText(((Double) currentPlayerSkill.totalScore()).toString());
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
