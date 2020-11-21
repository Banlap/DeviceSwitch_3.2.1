package com.aliyun.iot.ilop.demo.page.login3rd;

import android.content.Context;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aliyun.iot.demo.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.jetbrains.annotations.NotNull;

/**
 * <p>A fragment that shows a list of items as a modal bottom sheet.</p>
 * <p>You can show this modal bottom sheet from your activity like this:</p>
 * <pre>
 *     AuthCodeFragment.newInstance(30).show(getSupportFragmentManager(), "dialog");
 * </pre>
 * <p>You activity (or fragment) needs to implement {@link AuthCodeFragment.Listener}.</p>
 */
public class AuthCodeFragment extends BottomSheetDialogFragment {

    private Listener mListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_authcode_list_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        final RecyclerView recyclerView = (RecyclerView) view;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new AuthCodeAdapter());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        final Fragment parent = getParentFragment();
        if (parent != null) {
            mListener = (Listener) parent;
        } else {
            mListener = (Listener) context;
        }
    }

    @Override
    public void onDetach() {
        mListener = null;
        super.onDetach();
    }

    public interface Listener {
        void onAuthCodeClicked(int position);
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        final TextView text;

        ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.fragment_authcode_list_dialog_item, parent, false));
            text = itemView.findViewById(R.id.text);
            text.setOnClickListener(v -> {
                if (mListener != null) {
                    mListener.onAuthCodeClicked(getAdapterPosition());
                    dismiss();
                }
            });
        }

    }

    public static final String[] STATIC_AUTH_CODES = new String[]{"ningchenauthcode001", "ningchenauthcode002", "ningchenauthcode003"};

    private class AuthCodeAdapter extends RecyclerView.Adapter<ViewHolder> {
        AuthCodeAdapter() {

        }

        @NotNull
        @Override
        public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.text.setText(STATIC_AUTH_CODES[position]);
        }

        @Override
        public int getItemCount() {
            return STATIC_AUTH_CODES.length;
        }

    }

}
