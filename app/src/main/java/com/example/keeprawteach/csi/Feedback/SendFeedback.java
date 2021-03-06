package com.example.keeprawteach.csi.Feedback;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.keeprawteach.csi.Model.Charge;
import com.example.keeprawteach.csi.Model.SendFeed;
import com.example.keeprawteach.csi.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Random;

public class SendFeedback extends AppCompatActivity {

    private Button button;

    AlertDialog al;

    String namehere;

    FirebaseDatabase database;

    DatabaseReference reference;

    private ListView listView;

    ArrayList<SendFeed> arrayList;

    AdapterClass adapterClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_feedback);

        namehere = getIntent().getStringExtra("key");

        button = (Button) findViewById(R.id.newfeed);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSender();
            }
        });

        listView = (ListView) findViewById(R.id.list);

        arrayList = new ArrayList<>();

        loadData();
    }

    private void openSender() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater lay = LayoutInflater.from(this);

        final View viewdata = lay.inflate(R.layout.feed, null);

        final AutoCompleteTextView AA = (AutoCompleteTextView) viewdata.findViewById(R.id.aa);

        Button Cancel = (Button) viewdata.findViewById(R.id.terms);

        Button Login = (Button) viewdata.findViewById(R.id.login);

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String a = AA.getText().toString();
                if (a.isEmpty()) {
                    Toast.makeText(SendFeedback.this, "Enter Message", Toast.LENGTH_SHORT).show();
                } else {
                    seach(a);
                }

            }
        });

        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                al.dismiss();

            }
        });


        builder.setView(viewdata);

        al = builder.create();

        al.show();
    }

    private void seach(String a) {

        Random rand = new Random();

        int rand_int1 = rand.nextInt(10000) + 1000;

        String r = "" + rand_int1;

        String re = "";

        SendFeed upload = new SendFeed(a, namehere, r, re);

        database = FirebaseDatabase.getInstance();

        reference = database.getReference("Feedback");

        //adding an upload to firebase database
        String uploadId = reference.push().getKey();

        reference.child(uploadId).setValue(upload);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

//                Toast.makeText(SendFeedback.this, "Sent Successfully", Toast.LENGTH_SHORT).show();

                al.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void loadData() {
        database = FirebaseDatabase.getInstance();

        reference = database.getReference("Feedback");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                arrayList.clear();

                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {

                    SendFeed upload = new SendFeed();

                    String name = (String) messageSnapshot.child("sender").getValue();

                    String Message = (String) messageSnapshot.child("message").getValue();

                    String Id = (String) messageSnapshot.child("id").getValue();

                    String Response = (String) messageSnapshot.child("response").getValue();

                    if (name.equalsIgnoreCase(namehere)) {

                        upload.setId(""+Id);

                        upload.setMessage(Message);

                        upload.setResponse(Response);

                        arrayList.add(upload);
                    }

                }

                String[] uploads = new String[arrayList.size()];

                for (int i = 0; i < uploads.length; i++) {
                    uploads[i] = arrayList.get(i).getSender();
                }

                adapterClass = new AdapterClass(getApplicationContext(), arrayList);

                listView.setAdapter(adapterClass);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private class AdapterClass extends ArrayAdapter {

        ArrayList<SendFeed> status;


        public AdapterClass(Context context, ArrayList<SendFeed> status) {

            super(context, R.layout.charges, R.id.bb, status);

            this.status = status;

        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(R.layout.charges, parent, false);

            TextView textView = (TextView) view.findViewById(R.id.bb);

            SendFeed upload = status.get(position);

            textView.setText("Message Id:\n" + upload.getId() + "\nMessage:\n" + upload.getMessage() + "\nResponse:\n" + upload.getResponse());


            return view;
        }
    }
}
