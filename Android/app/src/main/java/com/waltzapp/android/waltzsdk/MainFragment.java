package com.waltzapp.android.waltzsdk;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.waltzapp.androidsdk.WaltzCode;
import com.waltzapp.androidsdk.WaltzSDK;
import com.waltzapp.androidsdk.callback.WaltzCallback;
import com.waltzapp.androidsdk.guests.Invitation;
import com.waltzapp.androidsdk.guests.InvitationsCallback;
import com.waltzapp.androidsdk.guests.SendInvitationCallback;
import com.waltzapp.androidsdk.pojo.WaltzUserInfos;

import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by kamalazougagh on 2018-09-28.
 */
public class MainFragment extends Fragment {

    private Listener mListener;
    private TextView mInvitationsResult, mUserInfosResult;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (Listener) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mInvitationsResult = view.findViewById(R.id.invitations_result);
        mUserInfosResult = view.findViewById(R.id.user_infos_result);


        view.findViewById(R.id.camera_permission).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions( new String[]{Manifest.permission.CAMERA}, 1);
                }
                else {
                    Toast.makeText(getContext(), "Camera Permission Granted", Toast.LENGTH_SHORT).show();
                }
            }
        });

        view.findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) mListener.onLogin();
            }
        });

        view.findViewById(R.id.start_transaction).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) mListener.onStartTransaction();
            }
        });

        view.findViewById(R.id.user_infos).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WaltzUserInfos infos = WaltzSDK.getInstance().getUserInfos();
                String result = "Name  : " + infos.name + "\n" +
                        "Email : " + infos.email + "\n" +
                        "Uid   : " + infos.uid + "\n" +
                        "Token : " + WaltzSDK.getInstance().getJwt();
                mUserInfosResult.setText(result);
                mUserInfosResult.setVisibility(View.VISIBLE);
            }
        });

        view.findViewById(R.id.start_geofence).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WaltzSDK
                        .getInstance()
                        .setCallback(new WaltzCallback() {
                            @Override
                            public void onTransactionDone(WaltzCode waltzCode) {
                                Toast.makeText(getContext(), "Status code: " + waltzCode, Toast.LENGTH_SHORT).show();
                            }
                        })
                        .startGeofencing();
            }
        });

        view.findViewById(R.id.stop_geofence).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WaltzSDK
                        .getInstance()
                        .setCallback(new WaltzCallback() {
                            @Override
                            public void onTransactionDone(WaltzCode waltzCode) {
                                Toast.makeText(getContext(), "Status code: " + waltzCode, Toast.LENGTH_SHORT).show();
                            }
                        })
                        .stopGeofencing();
            }
        });

        view.findViewById(R.id.send_invitation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String firstName = "A-SDK fName";
                String lastName = "A-SDK lName";
                String email = "androidsdk@example.com";
                String phoneNumber = "5145457878";

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());
                Date startDate = calendar.getTime();

                calendar.add(Calendar.MINUTE, 30);
                Date endDate = calendar.getTime();

                WaltzSDK
                        .getInstance()
                        .sendInvitation(firstName, lastName, email, phoneNumber, startDate, endDate, new SendInvitationCallback() {

                            @Override
                            public void onInvitationSent(WaltzCode code) {
                                Log.d("LIB", "onInvitationSent() code : " + code);
                                Toast.makeText(getContext(), "Code: " + code, Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        view.findViewById(R.id.my_guests).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WaltzSDK.getInstance().fetchMyGuests(new InvitationsCallback() {
                    @Override
                    public void onInvitationsReceived(WaltzCode code, List<Invitation> invitationList) {
                        Toast.makeText(getContext(), "Code: " + code, Toast.LENGTH_SHORT).show();
                        if (code == WaltzCode.SUCCESS) {
                            Log.d("LIB", "fetchMyGuests - onInvitationsReceived() code : " + code + ", item count: " + invitationList.size());
                            Type listOfTestObject = new TypeToken<List<Invitation>>(){}.getType();
                            String s = new Gson().toJson(invitationList, listOfTestObject);
                            mInvitationsResult.setText(s);
                            mInvitationsResult.setVisibility(View.VISIBLE);
                        }
                        else Log.d("LIB", "fetchMyGuests - onInvitationsReceived() code : " + code );
                    }
                });
            }
        });

        view.findViewById(R.id.my_invitations).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WaltzSDK.getInstance().fetchMyInvitations(new InvitationsCallback() {
                    @Override
                    public void onInvitationsReceived(WaltzCode code, List<Invitation> invitationList) {
                        Toast.makeText(getContext(), "Code: " + code, Toast.LENGTH_SHORT).show();
                        if (code == WaltzCode.SUCCESS) {
                            Log.d("LIB", "fetchMyInvitations - onInvitationsReceived() code : " + code + ", item count: " + invitationList.size());
                            Type listOfTestObject = new TypeToken<List<Invitation>>(){}.getType();
                            String s = new Gson().toJson(invitationList, listOfTestObject);
                            mInvitationsResult.setText(s);
                            mInvitationsResult.setVisibility(View.VISIBLE);
                        }
                        else Log.d("LIB", "fetchMyInvitations - onInvitationsReceived() code : " + code);
                    }
                });
            }
        });

        view.findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WaltzSDK
                        .getInstance()
                        .logout();
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface Listener {
        void onLogin();
        void onStartTransaction();
    }
}
