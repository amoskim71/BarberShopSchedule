package com.udl.bss.barbershopschedule.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.udl.bss.barbershopschedule.HomeActivity;
import com.udl.bss.barbershopschedule.R;
import com.udl.bss.barbershopschedule.database.BLL;
import com.udl.bss.barbershopschedule.domain.Promotion;

public class BarberPromotionDetailFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private Promotion promotionToChange;

    public BarberPromotionDetailFragment() {
        // Required empty public constructor
    }

    public static BarberPromotionDetailFragment newInstance(Promotion promotion) {
        BarberPromotionDetailFragment fragment = new BarberPromotionDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable("promotion", promotion);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_barber_promotion_detail, container, false);

        Bundle bundle = getArguments();

        String s_name = bundle.getString("name");
        String s_description = bundle.getString("description");
        String s_service = bundle.getString("service");

        EditText name_cv = (EditText) view.findViewById(R.id.name_cv);
        EditText description_cv = (EditText) view.findViewById(R.id.description_cv);
        EditText service_cv = (EditText) view.findViewById(R.id.service_cv);

        name_cv.setText(s_name);
        description_cv.setText(s_description);
        service_cv.setText(s_service);

        Button btn_delete = (Button) view.findViewById(R.id.btn_delete);
        btn_delete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                deleteInDB();
            }
        });
        return view;

        //return inflater.inflate(R.layout.fragment_barber_promotion_detail, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        promotionToChange = new Promotion(args.getInt("id"),args.getInt("id_barber"),args.getInt("service"),args.getString("name"),args.getString("description"),args.getInt("is_promotional"));

    }

    private void deleteInDB () {

        BLL instance = new BLL(getContext());

        instance.Delete_Promotion(promotionToChange);

        Toast.makeText(getContext(), "Your promotion was deleted succesfully", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(getContext(), HomeActivity.class);
        intent.putExtra("user", "Barber");
        this.startActivity(intent);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {

    }
}
