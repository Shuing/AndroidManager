package ning.xyw.androidmanager.fragment;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import ning.xyw.androidmanager.R;
import ning.xyw.androidmanager.util.Util;

/**
 * Created by ning on 15-4-29.
 */
public class ItemDialogFragment extends DialogFragment implements View.OnClickListener {
    private Button mBtn1;
    private Button mBtn2;
    private Button mBtn3;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_app, container);
        mBtn1 = (Button) view.findViewById(R.id.btn1);
        mBtn2 = (Button) view.findViewById(R.id.btn2);
        mBtn3 = (Button) view.findViewById(R.id.btn3);
        mBtn1.setOnClickListener(this);
        mBtn2.setOnClickListener(this);
        mBtn3.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn1:
                Util.t("btn1");
                break;
            case R.id.btn2:
                Util.t("btn2");
                break;
            case R.id.btn3:
                Util.t("btn3");
                break;
        }
    }
}
