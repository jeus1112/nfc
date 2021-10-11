package com.example.sns_project_nfc.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.sns_project_nfc.R;
import com.example.sns_project_nfc.activity.WritePostActivity;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class CheckListFragment extends Fragment {

    private EditText editText;
    private Button button;
    private ListView listView;;

    public CheckListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    ArrayAdapter<String> adapter;
    ArrayList<String> array = new ArrayList<>();

    // ArrayList -> Json으로 변환
    private static final String SETTINGS_PLAYER_JSON = "settings_item_json";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_checklist, container, false);
        Toolbar myToolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(myToolbar);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if(actionBar != null){
            actionBar.setTitle("체크리스트");
        }

        editText = (EditText) view.findViewById(R.id.editText);
        button = (Button) view.findViewById(R.id.button);
        button.setOnClickListener(onClickListener);
        listView = (ListView) view.findViewById(R.id.listView);

        array = getStringArrayPref(getActivity(), SETTINGS_PLAYER_JSON);

        adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_multiple_choice, array);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String str = array.get(position);

            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {  //체크리스트 길게 눌러서 삭제 -> 스와이프로 변경해야함
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                array.remove(position);
                adapter.notifyDataSetChanged();
                return false;
            }
        });

        return view;

    }

    public void Onstart(){ super.onStart(); }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.button:                                                         // 아마 새로고침..?
                    String str = editText.getText().toString();
                    if (str.length() != 0) {
                        array.add(str + "");
                        adapter.notifyDataSetChanged();
                        editText.setText("");
                    }
                    break;
            }
        }
    };

    public void setStringArrayPref(Context context, String key, ArrayList<String> values) {  //ArrayList를 JSON으로 변환해서 SharedPreferences에 String 저장

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        JSONArray a = new JSONArray();

        for (int i = 0; i < values.size(); i++) {
            a.put(values.get(i));
        }

        if (!values.isEmpty()) {
            editor.putString(key, a.toString());
        } else {
            editor.putString(key, null);
        }

        editor.apply();
    }

    public ArrayList getStringArrayPref(Context context, String key) {  //JSON형식을 SharedPreference에서 String 가져와서 ArrayList로 변환

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String json = prefs.getString(key, null);
        ArrayList urls = new ArrayList();

        if (json != null) {
            try {
                JSONArray a = new JSONArray(json);

                for (int i = 0; i < a.length(); i++) {
                    String url = a.optString(i);
                    urls.add(url);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return urls;
    }

    @Override
    public void onPause() {
        super.onPause();

        setStringArrayPref(getActivity(), SETTINGS_PLAYER_JSON, array);

    }
}