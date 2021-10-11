package com.example.sns_project_nfc.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sns_project_nfc.R;
import com.example.sns_project_nfc.UserInfo;
import com.example.sns_project_nfc.adapter.UserListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;

public class UserListFragment extends Fragment {
    private FirebaseFirestore firebaseFirestore;
    private UserListAdapter userListAdapter;
    private ArrayList<UserInfo> userList;
    private boolean updating;
    private boolean topScrolled;
    private Spinner building;
    private Spinner unit;


    public UserListFragment() {                                                                         // part22 : 유저리스트 (32')
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user_list, container, false);
        Toolbar myToolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(myToolbar);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if(actionBar != null){
            actionBar.setTitle("세대 관리");
        }

        building = (Spinner)view.findViewById(R.id.building_spinner);
        unit = (Spinner)view.findViewById(R.id.unit_spinner);

        view.findViewById(R.id.searchButton).setOnClickListener(onClickListener);
        view.findViewById(R.id.AdminButton).setOnClickListener(onClickListener);

        firebaseFirestore = FirebaseFirestore.getInstance();
        userList = new ArrayList<>();
        userListAdapter = new UserListAdapter(getActivity(), userList);

        final RecyclerView recyclerView = view.findViewById(R.id.recyclerView);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(userListAdapter);
        postsUpdate(false);
        return view;
    }
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.searchButton:
                    UserUpdate(true);
                    break;
                case R.id.AdminButton:
                    adminUpdate(true);
                    break;
            }
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    private void UserUpdate(final boolean clear) {
        updating = true;
        String buildingnum = building.getSelectedItem().toString();
        String unitnum = unit.getSelectedItem().toString();
        Date date = userList.size() == 0 || clear ? new Date() : userList.get(userList.size() - 1).getCreatedID();
        CollectionReference collectionReference = firebaseFirestore.collection("users");
        collectionReference.orderBy("createdID", Query.Direction.DESCENDING).whereLessThan("createdID", date).whereEqualTo("building",buildingnum).whereEqualTo("unit", unitnum).whereEqualTo("authState", "O").limit(10).get()       // + : 사용자 리스트 수정 (조건문)
        //collectionReference.limit(10).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if(clear){
                                userList.clear();
                            }
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                userList.add(new UserInfo(
                                        document.getData().get("name").toString(),
                                        document.getData().get("phoneNumber").toString(),
                                        document.getData().get("birthDay").toString(),
                                        document.getData().get("address").toString(),
                                        document.getData().get("building").toString(),
                                        document.getData().get("unit").toString(),
                                        new Date(document.getDate("createdID").getTime()),                                          // + : 사용자 리스트 수정 (날짜 받아오기)
                                        document.getData().get("photoUrl") == null ? null : document.getData().get("photoUrl").toString(),
                                        document.getData().get("authState").toString(),
                                        document.getData().get("userUID").toString()
                                ));

                            }
                            userListAdapter.notifyDataSetChanged();
                        } else {
                        }
                        updating = false;
                    }
                });
    }
    private void postsUpdate(final boolean clear) {
        updating = true;
        Date date = userList.size() == 0 || clear ? new Date() : userList.get(userList.size() - 1).getCreatedID();
        CollectionReference collectionReference = firebaseFirestore.collection("users");
        collectionReference.orderBy("createdID", Query.Direction.DESCENDING).whereLessThan("createdID", date).whereEqualTo("authState", "O").limit(10).get()       // + : 사용자 리스트 수정 (조건문)
                //collectionReference.limit(10).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if(clear){
                                userList.clear();
                            }
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                userList.add(new UserInfo(
                                        document.getData().get("name").toString(),
                                        document.getData().get("phoneNumber").toString(),
                                        document.getData().get("birthDay").toString(),
                                        document.getData().get("address").toString(),
                                        document.getData().get("building").toString(),
                                        document.getData().get("unit").toString(),
                                        new Date(document.getDate("createdID").getTime()),                                          // + : 사용자 리스트 수정 (날짜 받아오기)
                                        document.getData().get("photoUrl") == null ? null : document.getData().get("photoUrl").toString(),
                                        document.getData().get("authState").toString(),
                                        document.getData().get("userUID").toString()
                                ));

                            }
                            userListAdapter.notifyDataSetChanged();
                        } else {
                        }
                        updating = false;
                    }
                });
    }
    private void adminUpdate(final boolean clear) {
        updating = true;
        Date date = userList.size() == 0 || clear ? new Date() : userList.get(userList.size() - 1).getCreatedID();
        CollectionReference collectionReference = firebaseFirestore.collection("users");
        collectionReference.orderBy("createdID", Query.Direction.DESCENDING).whereLessThan("createdID", date).whereEqualTo("authState", "ADMINISTER").get()       // + : 사용자 리스트 수정 (조건문)
                //collectionReference.limit(10).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if(clear){
                                userList.clear();
                            }
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                userList.add(new UserInfo(
                                        document.getData().get("name").toString(),
                                        document.getData().get("phoneNumber").toString(),
                                        document.getData().get("birthDay").toString(),
                                        document.getData().get("address").toString(),
                                        document.getData().get("building").toString(),
                                        document.getData().get("unit").toString(),
                                        new Date(document.getDate("createdID").getTime()),                                          // + : 사용자 리스트 수정 (날짜 받아오기)
                                        document.getData().get("photoUrl") == null ? null : document.getData().get("photoUrl").toString(),
                                        document.getData().get("authState").toString(),
                                        document.getData().get("userUID").toString()
                                ));

                            }
                            userListAdapter.notifyDataSetChanged();
                        } else {
                        }
                        updating = false;
                    }
                });
    }
}
