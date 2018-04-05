package com.example.jeannette.laboratorio1jeannetterojas;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int IMAGE_CAPTURE = 101;
    private static final int SPEECH_REQUEST_CODE = 1234;
    Button photoB;
    Button save;
    EditText name;
    EditText profile;
    Dialog matchTextDialog;
    TextView speechTextView;
    ListView textListView;
    ArrayList<String> matchesText;

    List<persona> listaPersona = new ArrayList<persona>();
    PersonaAdapter adapter = null;


    //reconocimiento de voz
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        save = (Button) findViewById(R.id.save);
        photoB = (Button) findViewById(R.id.photo);
        speechTextView = (TextView) findViewById(R.id.profile);

        speechTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isConnected()) {
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra (RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    startActivityForResult(intent, SPEECH_REQUEST_CODE);
                } else {
                    Toast.makeText(getApplicationContext(), "Please Connect to Internet", Toast.LENGTH_SHORT).show();
                }
            }

        });
        if (!hasCamera())
            photoB.setEnabled(false);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button save = (Button) findViewById(R.id.save);
                save.setOnClickListener(onSave);
                ListView list = (ListView) findViewById(R.id.lista);
                adapter = new PersonaAdapter();
                list.setAdapter(adapter);

            }
        });

        photoB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent ( MediaStore . ACTION_IMAGE_CAPTURE ) ;
                startActivityForResult ( intent , IMAGE_CAPTURE ) ;
            }
        });

    }

    public boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo net = cm.getActiveNetworkInfo();
        if (net != null && net.isAvailable() && net.isConnected()) {
            return true;
        } else {
            return false;
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri videoUri = data.getData();
        if (requestCode == IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, " Video saved to :\n" +
                        videoUri, Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, " Video recording cancelled .",
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, " Failed to record video ",
                        Toast.LENGTH_LONG).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            matchTextDialog = new Dialog(MainActivity.this);
            matchTextDialog.setContentView(R.layout.dialog_matches_frag);
            matchTextDialog.setTitle(" Select Matching Text ");
            textListView = (ListView) matchTextDialog.findViewById(R.id.listR);
            matchesText = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, matchesText);
            textListView.setAdapter(adapter);
            textListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    speechTextView.setText(" You have said : " + matchesText.get(position));
                    matchTextDialog.hide();
                }
            });
            matchTextDialog.show();
        }
    }

    private boolean hasCamera() {
        return (getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA_ANY));
    }


    //lista

    private View.OnClickListener onSave = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            persona r = new persona();
            EditText name = (EditText) findViewById(R.id.name);
            EditText profile = (EditText) findViewById(R.id.profile);
            r.setName(name.getText().toString());
            r.setProfile(profile.getText().toString());
            RadioGroup types = (RadioGroup) findViewById(R.id.types);
            switch (types.getCheckedRadioButtonId()) {
                case R.id.female:
                    r.setTypes("female");
                    break;
                case R.id.male:
                    r.setTypes("male");
                    break;
            }
            adapter.add(r);
        }
    };

    class PersonaAdapter extends ArrayAdapter<persona> {
        PersonaAdapter() {
            super(MainActivity.this, R.layout.row, listaPersona);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            PersonaHolder holder = null;
            if (row == null) {
                LayoutInflater inflater = getLayoutInflater();
                row = inflater.inflate(R.layout.row, parent, false);
                holder = new PersonaHolder(row);
                row.setTag(holder);
            } else {
                holder = (PersonaHolder) row.getTag();
            }
            holder.populateFrom(listaPersona.get(position));
// Hay que modificar el model
            return (row);
        }
    }

    static class PersonaHolder {
        private TextView name = null;
        private TextView profile = null;
        private TextView sexo = null;
        private ImageView icon = null;

        PersonaHolder(View row) {
            name = (TextView) row.findViewById(R.id.name);
            profile = (TextView) row.findViewById(R.id.profile);
            sexo = (TextView) row.findViewById(R.id.sexo);
            icon = (ImageView) row.findViewById(R.id.icon);
        }

        void populateFrom(persona r) {
            name.setText(r.getName());
            profile.setText(r.getProfile());
            sexo.setText(r.getTypes());

        }
    }
}












