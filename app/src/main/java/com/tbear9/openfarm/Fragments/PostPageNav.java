package com.tbear9.openfarm.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tbear9.openfarm.R;
import com.tbear9.openfarm.databinding.FragmentPostPageNavBinding;

import lombok.Setter;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class PostPageNav extends Fragment {

    @Setter private Listener listener = new Listener() {};
    FragmentPostPageNavBinding binding;

    public PostPageNav() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof Listener)
            listener = (Listener) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentPostPageNavBinding.inflate(getLayoutInflater());
        binding.back.setOnClickListener( e -> listener.back());
        binding.next.setOnClickListener( e -> listener.next());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return FragmentPostPageNavBinding.inflate(getLayoutInflater(), container, true).getRoot();
    }

    public interface Listener {
        default void next(){};
        default void back(){};
    }
}