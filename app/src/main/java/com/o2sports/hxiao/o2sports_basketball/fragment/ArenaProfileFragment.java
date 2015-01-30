package com.o2sports.hxiao.o2sports_basketball.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.microsoft.windowsazure.mobileservices.MobileServiceSystemProperty;
import com.microsoft.windowsazure.mobileservices.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.QueryOrder;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableOperationCallback;
import com.microsoft.windowsazure.mobileservices.TableQueryCallback;
import com.o2sports.hxiao.o2sports_basketball.MainActivity;
import com.o2sports.hxiao.o2sports_basketball.R;
import com.o2sports.hxiao.o2sports_basketball.entity.Arena;
import com.o2sports.hxiao.o2sports_basketball.entity.CheckIn;
import com.o2sports.hxiao.o2sports_basketball.entity.Registration;

import org.w3c.dom.Text;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ArenaProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ArenaProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ArenaProfileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_Arena_ID = "param1";

    // TODO: Rename and change types of parameters
    public String arenaID;

    private OnFragmentInteractionListener mListener;

    private MobileServiceTable<Arena> mArenaTable;
    private MobileServiceTable<CheckIn> mCheckInTable;
    private MobileServiceTable<Registration> mRegistrationTable;

    private boolean isCheckedIn;

    private Arena currentArena;
    private View currentView;

    public Button checkInButton;

    public int mYear;
    public int mMonth;
    public int mDay;
    public int mHour;
    public int mMinute;

    public Date registrationDate;

    public boolean registered = false;




    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment ArenaProfile.
     */
    // TODO: Rename and change types and number of parameters
    public static ArenaProfileFragment newInstance(String param1) {
        ArenaProfileFragment fragment = new ArenaProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_Arena_ID, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public ArenaProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            arenaID = getArguments().getString(ARG_Arena_ID);
        }


        mArenaTable = ((MainActivity) this.getActivity()).mClient.getTable(Arena.class);

        mArenaTable.where().field("id").eq(arenaID).execute(new TableQueryCallback<Arena>() {
            public void onCompleted(List<Arena> result,
                                    int count,
                                    Exception exception,
                                    ServiceFilterResponse response) {
                if (exception == null && !result.isEmpty()) {
                    currentArena = result.get(0);
                    ((TextView) currentView.findViewById(R.id.textView_name_value)).setText(currentArena.name);
                    ((TextView) currentView.findViewById(R.id.textView_address_value)).setText(currentArena.address);
                    ((TextView) currentView.findViewById(R.id.textView_basket_count_value)).setText(((Integer) currentArena.basketCount).toString());

                } else {
                    if (exception != null) {
                        messageDialog(exception.getMessage() + response.getContent());
                    }
                    else
                    {
                        messageDialog("Arena cannot be found");
                    }
                }
            }
        });

        mCheckInTable = ((MainActivity) this.getActivity()).mClient.getTable(CheckIn.class);

        EnumSet<MobileServiceSystemProperty> mobileServiceSystemPropertyEnumSet = EnumSet.of(MobileServiceSystemProperty.CreatedAt);
        mCheckInTable.setSystemProperties(mobileServiceSystemPropertyEnumSet);

        String playerID = ((MainActivity) this.getActivity()).localPlayerID;
        //TO Fix
        mCheckInTable.where()
                .field("PlayerId").eq(((MainActivity) (this.getActivity())).localPlayerID)
                .and().field("ArenaId").eq(this.arenaID)
                .select("Id", "ArenaId", "PlayerId", "Is_registered", "CreatedAt")
                .orderBy("CreatedAt", QueryOrder.Descending).top(1)
                .execute(new TableQueryCallback<CheckIn>() {

                    public void onCompleted(List<CheckIn> result,
                                            int count,
                                            Exception exception,
                                            ServiceFilterResponse response) {
                        Date now = new Date();
                        Date compare = new Date(now.getTime() - 1 * 60 * 60 * 1000);

                        if (exception == null && !result.isEmpty()) {
                            CheckIn latest = result.get(0);

                            if (latest.CreatedAt.after(compare)) {
                                isCheckedIn = true;
                                checkInButton.setText("Checked In");
                                checkInButton.setEnabled(false);
                            }
                        } else {
                            if (exception != null) {
                                messageDialog(exception.getMessage() + response.getContent());
                            } else {
                                isCheckedIn = false;
                                checkInButton.setText("Check In");
                                checkInButton.setEnabled(true);
                            }


                        }
                    }
                });


        mRegistrationTable = ((MainActivity) this.getActivity()).mClient.getTable(Registration.class);

        final Date today = new Date();
        Calendar cal = Calendar.getInstance();

        //TODO May have offset issue
        mRegistrationTable.where()
                .field("PlayerId").eq(((MainActivity) (this.getActivity())).localPlayerID)
                .and().field("ArenaId").eq(this.arenaID)
                .orderBy("StartTime", QueryOrder.Descending).top(1)
                .execute(new TableQueryCallback<Registration>() {

                    public void onCompleted(List<Registration> result,
                                            int count,
                                            Exception exception,
                                            ServiceFilterResponse response) {


                        if (exception == null && !result.isEmpty()) {
                            if (result.get(0).StartTime.after(today)) {
                                registered = true;
                                ((Button) currentView.findViewById(R.id.button_register)).setEnabled(false);
                                ((Button) currentView.findViewById(R.id.button_register)).setText("Registered");
                                registrationDate = result.get(0).StartTime;
                                ((TextView) currentView.findViewById(R.id.textView_registration_time)).setText(registrationDate.toString());
                            }
                            else
                            {
                                registered = false;
                                ((Button) currentView.findViewById(R.id.button_register)).setEnabled(true);
                                ((Button) currentView.findViewById(R.id.button_register)).setText("Register");
                            }
                        } else {
                            if (exception != null) {
                                messageDialog(exception.getMessage() + response.getContent());
                            } else {
                                registered = false;
                                ((Button) currentView.findViewById(R.id.button_register)).setEnabled(true);
                                ((Button) currentView.findViewById(R.id.button_register)).setText("Register");
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
                ArenaProfileFragment.this.getActivity().finish();
            }
        });
        builder.create().show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        currentView =  inflater.inflate(R.layout.fragment_arena_profile, container, false);
        checkInButton = (Button) currentView.findViewById(R.id.button_check_in);
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

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void checkIn(View v) {

        CheckIn mCheckin = new CheckIn();
        mCheckin.CreatedAt = new Date();

        mCheckin.playerId = ((MainActivity) this.getActivity()).localPlayerID;
        mCheckin.arenaId = this.arenaID;
        mCheckin.is_registered = false; // TODO

        mCheckInTable.insert(mCheckin, new TableOperationCallback<CheckIn>() {
            public void onCompleted(CheckIn entity,
                                    Exception exception,
                                    ServiceFilterResponse response) {
                if (exception == null) {
                    checkInButton.setText("Checked In");
                    checkInButton.setEnabled(false);
                }
                else
                {
                    messageDialog(exception.getMessage() + response.getContent());
                }
            }
        });
    }

    public void register(View v) {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        registrationDate = new Date();

        collectRegistrationDate();


    }

    public void collectRegistrationDate()
    {
        if(!registered) {

            DatePickerDialog dpd;
            dpd = new DatePickerDialog(this.getActivity(),
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
                            // Display Selected date in textbox
                            //txtDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                            registrationDate.setYear(year - 1900);
                            registrationDate.setMonth(monthOfYear);
                            registrationDate.setDate(dayOfMonth);

                            collectRegistrationTime();


                        }
                    }, mYear, mMonth, mDay);
            dpd.show();
        }
    }

    public void collectRegistrationTime()
    {
        if (!registered) {
            // for fixing duplicated registration record
            registered = true;
            TimePickerDialog tpd;

            tpd = new TimePickerDialog(this.getActivity(),
                    new TimePickerDialog.OnTimeSetListener() {

                        public boolean timeSet = false;

                        @Override
                        public void onTimeSet(TimePicker view, int hour,
                                              int minute) {

                            if (!timeSet) {
                                timeSet = true;
                                registrationDate.setHours(hour);
                                registrationDate.setMinutes(minute);

                                // insert to DB

                                registrationInsert();
                            }

                        }
                    }, mHour, mMinute, true);
            tpd.show();
        }
    }


    public void registrationInsert()
    {
        Registration mRegistration = new Registration();

        mRegistration.playerId = ((MainActivity) this.getActivity()).localPlayerID;
        mRegistration.arenaId = this.arenaID;
        mRegistration.StartTime = registrationDate;

        mRegistration.EndTime = new Date(registrationDate.getTime() + 1 * 60 * 60 * 1000); //TODO

        mRegistrationTable.insert(mRegistration, new TableOperationCallback<Registration>() {
            @Override
            public void onCompleted(Registration registration, Exception exception, ServiceFilterResponse response) {
                if (exception == null) {
                    ((Button) currentView.findViewById(R.id.button_register)).setEnabled(false);
                    ((Button) currentView.findViewById(R.id.button_register)).setText("Registered");
                    ((TextView) currentView.findViewById(R.id.textView_registration_time)).setText(registrationDate.toString());
                }
                else
                {
                    messageDialog(exception.getMessage() + response.getContent());
                }
            }
        });

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
