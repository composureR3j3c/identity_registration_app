package com.tech5.fingercapture;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.List;

public class FingersListingAdapter extends RecyclerView.Adapter<FingersListingAdapter.ViewHolder> {

    private SettingsPrefManager prefManager;

    private List<FingerData> fingers;

    public FingersListingAdapter(List<FingerData> fingers) {
        this.fingers = fingers;
    }


    @NonNull
    @Override
    public FingersListingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View rootView = LayoutInflater.from(context).inflate(R.layout.layout_finger_row, parent, false);
        prefManager = new SettingsPrefManager(context);

////        MyApp app = (MyApp) context.getApplicationContext();
//        Application app = (Application) context.getApplicationContext();
//
//        prefManager = new ViewModelProvider((ViewModelStoreOwner) app,
//                ViewModelProvider.AndroidViewModelFactory.getInstance(app))
//                .get(SettingsPrefManager.class);

        return new ViewHolder(rootView);
    }

//    @Override
//    public void onBindViewHolder(@NonNull FingersListingAdapter.ViewHolder holder, int position) {
//
//        FingerData finger = fingers.get(position);
//
//        if (finger.displayImagePath != null) {
//
//            holder.fingerImage.setImageURI(Uri.fromFile(new File(finger.displayImagePath)));
//
//
//        } else if (finger.primaryImagePath != null) {
//            holder.fingerImage.setImageURI(Uri.fromFile(new File(finger.primaryImagePath)));
//        } else {
//            holder.fingerImage.setImageBitmap(null);
//        }
//
//        holder.txtPos.setText(String.format("Pos: %d", finger.pos));
//        holder.txtQuality.setText(String.format("Quality: %d", finger.quality));
//        holder.txtNistQuality.setText(String.format("Nist Quality: %d", finger.nistQuality));
//       if (prefManager.isGetNfiq2QualityEnabled()){
//holder.txtNfiq2Quality.setVisibility(VISIBLE);
//           holder.txtNfiq2Quality.setText(String.format("NFIQ2 Quality: %d", finger.nist2Quality));
//
//       }else {
//           holder.txtNfiq2Quality.setVisibility(GONE);
//
//       }
////        holder.txtMinitiaesNumber.setText(String.format("Minitiaes Number: %d", finger.minutiaesNumber));
//
//    }

@Override
public void onBindViewHolder(@NonNull FingersListingAdapter.ViewHolder holder, int position) {
    FingerData finger = fingers.get(position);

    // image logic (same as you have)
    if (finger.displayImagePath != null) {
        holder.fingerImage.setImageURI(Uri.fromFile(new File(finger.displayImagePath)));
    } else if (finger.primaryImagePath != null) {
        holder.fingerImage.setImageURI(Uri.fromFile(new File(finger.primaryImagePath)));
    } else {
        holder.fingerImage.setImageBitmap(null);
    }

    holder.txtPos.setText(String.format("Pos: %d", finger.pos));

    // base text and color from XML (do NOT change if pass)
    holder.txtQuality.setText(String.format("Quality: %d", finger.quality));
    holder.txtNistQuality.setText(String.format("Nist Quality: %d", finger.nistQuality));
    if (prefManager.isGetNfiq2QualityEnabled()) {
        holder.txtNfiq2Quality.setVisibility(VISIBLE);
        holder.txtNfiq2Quality.setText(String.format("NFIQ2 Quality: %d", finger.nist2Quality));
    } else {
        holder.txtNfiq2Quality.setVisibility(GONE);
    }

    // compute threshold exactly like isQualityOrLivenessFailed(...)
    float qualityThreshold = prefManager.getQualityThreshold();
    if (finger.pos == 5 || finger.pos == 10) {
        qualityThreshold = prefManager.getLittleFingerThreshold();
    }
    if (finger.pos == 4 || finger.pos == 9) {
        qualityThreshold = prefManager.getRingFingerThreshold();
    }

    // if below threshold -> RED, else leave default color
    if (finger.quality < qualityThreshold) {
        holder.txtPos.setTextColor(Color.RED);
    holder.txtQuality.setTextColor(Color.RED);
        holder.txtNistQuality.setTextColor(Color.RED);
        holder.txtNfiq2Quality.setTextColor(Color.RED);
    }
//    if (finger.nistQuality < qualityThreshold) {
//        holder.txtNistQuality.setTextColor(Color.RED);
//    }
//    if (prefManager.isGetNfiq2QualityEnabled() && finger.nist2Quality < qualityThreshold) {
//        holder.txtNfiq2Quality.setTextColor(Color.RED);
//    }
}

    @Override
    public int getItemCount() {
        return fingers == null ? 0 : fingers.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView fingerImage;
        TextView txtPos;
        TextView txtQuality;
        TextView txtNistQuality;
        TextView txtNfiq2Quality;
//        TextView txtMinitiaesNumber;


        public ViewHolder(View root) {
            super(root);
            fingerImage = root.findViewById(R.id.finger_image);
            txtQuality = root.findViewById(R.id.txt_prop_score);
//            txtMinitiaesNumber = root.findViewById(R.id.txt_minitia_number);
            txtNistQuality = root.findViewById(R.id.txt_nist_quality);
            txtNfiq2Quality = root.findViewById(R.id.txt_nfiq2_quality);
            txtPos = root
                    .findViewById(R.id.txt_pos);

        }
    }
}
