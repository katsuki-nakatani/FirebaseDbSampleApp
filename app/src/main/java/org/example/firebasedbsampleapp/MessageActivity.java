package org.example.firebasedbsampleapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.example.firebasedbsampleapp.adapter.MessageAdapter;
import org.example.firebasedbsampleapp.entity.Message;

import java.util.ArrayList;
import java.util.Calendar;

public class MessageActivity extends AppCompatActivity {

    public interface ELEMENT_TYPE {
        static final String NO_AUTH = "no_auth_messages";
        static final String AUTH = "auth_messages";
    }


    static final String PARAM_UID = "uid";
    static final String PARAM_ELEMENT = "element";
    FirebaseDatabase mFdInstance = FirebaseDatabase.getInstance();
    ToggleButton mTableSwitch;
    String mUid;
    String mElementName;
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    ChildEventListener mEventListener;

    MessageAdapter mMessageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        if (getIntent().hasExtra(PARAM_UID)) {
            mUid = getIntent().getStringExtra(PARAM_UID);
            mElementName = getIntent().getStringExtra(PARAM_ELEMENT);
        }


        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mTableSwitch = (ToggleButton) findViewById(R.id.auth_switch);
        findViewById(R.id.send_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView text = (EditText) findViewById(R.id.edit_text);
                if (!TextUtils.isEmpty(text.getText())) {
                    DatabaseReference ref = mFdInstance.getReference(mElementName).child(
                            String.valueOf(Calendar.getInstance().getTimeInMillis())
                    );
                    final Message message = new Message();
                    message.setContent(text.getText().toString());
                    message.setuId(mUid);
                    ref.setValue(message, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError == null) {
                                //send complete
                                //clear input field
                                ((EditText) findViewById(R.id.edit_text)).setText("");
                            } else {
                                Toast.makeText(MessageActivity.this, "send error", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

    }


    @Override
    protected void onPause() {
        super.onPause();
        removeFetchListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setTitle(mUid);
        setupFetchListener();
    }


    void removeFetchListener() {
        if (mEventListener != null) {
            mFdInstance.getReference(mElementName).removeEventListener(mEventListener);
        }
    }

    void addMessage(Message message) {
        if (mMessageAdapter == null) {
            setupRecyclerView();
        }
        mMessageAdapter.addItem(message);

    }

    void setupRecyclerView() {
        mMessageAdapter = new MessageAdapter(this, new ArrayList<Message>(), mUid);
        mRecyclerView.setAdapter(mMessageAdapter);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(PARAM_UID, mUid);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mUid = savedInstanceState.getString(PARAM_UID);
    }


    void setupFetchListener() {
        DatabaseReference myRef = mFdInstance.getReference(mElementName);
        mEventListener = myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()) {
                    Message message = dataSnapshot.getValue(Message.class);
                    addMessage(message);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    public static Intent createIntent(Context context, String uId, boolean isAuth) {
        Intent intent = new Intent(context, MessageActivity.class);
        intent.setAction(Intent.ACTION_VIEW);
        intent.putExtra(PARAM_UID, uId);
        intent.putExtra(PARAM_ELEMENT, isAuth ? ELEMENT_TYPE.AUTH : ELEMENT_TYPE.NO_AUTH);
        return intent;
    }


}
