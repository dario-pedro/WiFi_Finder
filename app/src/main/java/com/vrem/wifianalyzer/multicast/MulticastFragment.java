
package com.vrem.wifianalyzer.multicast;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.vrem.wifianalyzer.MainContext;
import com.vrem.wifianalyzer.R;

import java.util.ArrayList;

public class MulticastFragment extends Fragment  {

    private RecyclerView mRecyclerView;
    private Button mButtonSend;
    private EditText mEditTextMessage;


    private ChatMessageAdapter mAdapter;

    private MulticastSocketReceive mMessageReceive = null;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mMessageReceive = new MulticastSocketReceive(this);
        mMessageReceive.start();


        View view = inflater.inflate(R.layout.chat_content, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mButtonSend = (Button) view.findViewById(R.id.btn_send);
        mEditTextMessage = (EditText) view.findViewById(R.id.et_message);

        Activity a = MainContext.INSTANCE.getMainActivity();

        mRecyclerView.setLayoutManager(new LinearLayoutManager(a));

        mAdapter = new ChatMessageAdapter(a, new ArrayList<ChatMessage>());
        mRecyclerView.setAdapter(mAdapter);

        mButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = mEditTextMessage.getText().toString();
                if (TextUtils.isEmpty(message)) {
                    return;
                }
                sendMessage(message);
                mEditTextMessage.setText("");
            }
        });

        return view;
    }



    private void sendMessage(String message) {
        ChatMessage chatMessage = new ChatMessage(message, true, false);
        mAdapter.add(chatMessage);

        (new MulticastSocketSend(message)).execute((Void) null);
        //mimicOtherMessage(message);
    }


    public void receiveMessage(final String message)
    {

        MainContext.INSTANCE.getMainActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ChatMessage chatMessage = new ChatMessage(message, false, false);
                mAdapter.add(chatMessage);
            }
        });


    }



    private void mimicOtherMessage(String message) {
        ChatMessage chatMessage = new ChatMessage(message, false, false);
        mAdapter.add(chatMessage);

        mRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        mMessageReceive.interrupt();
        mMessageReceive=null;
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        //ui_update = null;
        //mMessageReceive.interrupt();
        mMessageReceive=null;
        super.onDestroy();
    }
}
