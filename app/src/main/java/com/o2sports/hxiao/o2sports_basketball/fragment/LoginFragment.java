package com.o2sports.hxiao.o2sports_basketball.fragment;

import android.app.Activity;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.microsoft.windowsazure.mobileservices.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableOperationCallback;
import com.microsoft.windowsazure.mobileservices.TableQueryCallback;
import com.o2sports.hxiao.o2sports_basketball.MainActivity;
import com.o2sports.hxiao.o2sports_basketball.R;
import com.o2sports.hxiao.o2sports_basketball.entity.Player;
import com.o2sports.hxiao.o2sports_basketball.entity.PlayerSkill;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LoginFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    // TODO: Rename and change types of parameters

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
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

    public void sign(View v)
    {
        final EditText mName = (EditText) ((MainActivity)(this.getActivity())).findViewById(R.id.editText_user_name);
        final EditText mPassword = (EditText) ((MainActivity)(this.getActivity())).findViewById(R.id.editText_password);

        if(mName.getText().toString() == "")
        {
            // error message, do nothing
            return;
        }
        else {

            MobileServiceTable<Player> mPlayer = ((MainActivity)(this.getActivity())).mClient.getTable(Player.class);


            mPlayer.where()
                    .field("name").eq(mName.getText().toString())
                    .execute(new TableQueryCallback<Player>() {

                        public void onCompleted(List<Player> result,
                                                int count,
                                                Exception exception,
                                                ServiceFilterResponse response) {


                            if (exception == null && !result.isEmpty()) {
                                foundPlayer(result.get(0).id);
                            } else {
                                if (exception != null) {
                                    //messageDialog(exception.getMessage() + response.getContent());
                                } else {

                                    addPlayer(mName.getText().toString());
                                }
                            }
                        }
                    });
        }
    }

    public void foundPlayer(String id)
    {
        String playerID = "LocalPlayerID";

        SharedPreferences settings = ((MainActivity)(this.getActivity())).getPreferences(0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(playerID, id);
        editor.commit();

        ((MainActivity)(this.getActivity())).localPlayerID = id;
        ((MainActivity)(this.getActivity())).needLogin = false;
        ((MainActivity)(this.getActivity())).reload();

        // return to playerprofile
    }

    public void addPlayer(String name)
    {
        final MobileServiceTable<Player> mPlayer = ((MainActivity)(this.getActivity())).mClient.getTable(Player.class);
        final MobileServiceTable<PlayerSkill> mPlayerSkill = ((MainActivity)(this.getActivity())).mClient.getTable(PlayerSkill.class);

        Player p = new Player();
        p.name = name;
        p.gender = true;
        p.height = 180;
        p.weight = 150;
        p.position = 3;

        ((MainActivity)(this.getActivity())).pDialog.show();

        mPlayer.insert(p, new TableOperationCallback<Player>() {
            public void onCompleted(Player entity,
                                    Exception exception,
                                    ServiceFilterResponse response) {
                if (exception == null) {

                    PlayerSkill ps = new PlayerSkill();
                    ps.playerID = entity.id;
                    ps.scoreCount = 0;
                    ps.setDefaultScore(5);



                    mPlayerSkill.insert(ps, new TableOperationCallback<PlayerSkill>() {
                        public void onCompleted(PlayerSkill entity,
                                                Exception exception,
                                                ServiceFilterResponse response) {
                            if (exception == null) {

                                foundPlayer(entity.playerID);
                            }
                            else
                            {
                                //messageDialog(exception.getMessage() + response.getContent());
                            }
                        }
                    });
                }
                else
                {
                    //messageDialog(exception.getMessage() + response.getContent());
                }
            }
        });
    }
}
