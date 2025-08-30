package com.tbear9.openfarm.Fragments;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tbear9.openfarm.R;
import com.tbear9.openfarm.activities.PostActivity;
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPostPageNavBinding.inflate(getLayoutInflater(), container, false);
        binding.back.setOnClickListener( e -> listener.back());
        binding.next.setOnClickListener( e -> listener.next());
        return binding.getRoot();
    }

    public interface Listener {
        default void next(){
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                PostActivity.next();
//            }
        };
        default void back(){
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                PostActivity.back();
//            }
        };
    }
}