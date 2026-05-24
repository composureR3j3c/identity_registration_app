package ai.tech5.fingercapturedemo;

import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ai.tech5.finger.utils.LivenessScore;
import ai.tech5.sdk.abis.T5AirSnap.NistPosCode;

public class ResultScreen extends AppCompatActivity {


    private final ArrayList<FingerData> fingers = new ArrayList<>();

    private TextView txtLivenessScores;;

    private SettingsPrefManager settingsPrefManager;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);

        findViewById(R.id.btn_home).setOnClickListener(view -> finish());
        txtLivenessScores = findViewById(R.id.txt_liveness_scores);
settingsPrefManager =new SettingsPrefManager(this);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setTitle("Result Screen");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        LinearLayoutManager layoutManager = new LinearLayoutManager(ResultScreen.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        FingersListingAdapter adapter = new FingersListingAdapter(fingers);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);


//        QualityLivenessResult qualityLivenessResult=getIntent().getParcelableExtra("checkResult");
//if (qualityLivenessResult!=null && qualityLivenessResult.failureDetails != null){
//
//    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
//            RelativeLayout.LayoutParams.MATCH_PARENT,
//            RelativeLayout.LayoutParams.WRAP_CONTENT
//    );
//    params.addRule(RelativeLayout.ALIGN_PARENT_TOP);  // TOP OF SCREEN
//    params.topMargin = 20;  // 20dp from top
//    params.setMargins(20, 20, 20, 20);  // All sides
//    txtLivenessScores.setLayoutParams(params);
//
////    txtLivenessScores.setVisibility(VISIBLE);
////
////    txtLivenessScores.setText(qualityLivenessResult.failureDetails);
////    txtLivenessScores.setTextColor(Color.RED);
//
//    if (qualityLivenessResult != null) {
//        SpannableStringBuilder ssb = new SpannableStringBuilder();
//
//        // RED failures first
//        if (qualityLivenessResult.failureDetails != null) {
//            int start = ssb.length();
//            ssb.append(qualityLivenessResult.failureDetails);
//            ssb.setSpan(new ForegroundColorSpan(Color.RED), start, ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//            ssb.append("\n\n");
//        }
//
//        // GREEN successes ✓
//        if (qualityLivenessResult.successDetails != null) {
//            int start = ssb.length();
//            ssb.append(qualityLivenessResult.successDetails);
//            ssb.setSpan(new ForegroundColorSpan(Color.GREEN), start, ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        }
//
//        txtLivenessScores.setText(ssb);
//    } else {
//        txtLivenessScores.setText("No data");
//    }
//
//}

//        Log.d("ResultScreen", "qualityLivenessResult: " + qualityLivenessResult);
//        Log.d("ResultScreen", "isFailed: " + (qualityLivenessResult != null ? qualityLivenessResult.isFailed : "null"));
//        Log.d("ResultScreen", "failureDetails: " + (qualityLivenessResult != null ? qualityLivenessResult.failureDetails : "null"));
//
//        if (qualityLivenessResult != null && qualityLivenessResult.failureDetails != null && !qualityLivenessResult.failureDetails.isEmpty()) {
//            Log.d("ResultScreen", "Setting RED text: " + qualityLivenessResult.failureDetails);
//            txtLivenessScores.setText("qualityLivenessResult.failureDetails");
//            txtLivenessScores.setTextColor(Color.RED);
//        } else {
//            Log.d("ResultScreen", "Setting GREEN success text");
//            txtLivenessScores.setText("All checks passed ✓");
//            txtLivenessScores.setTextColor(Color.GREEN);
//        }

//        Log.d("ResultScreen", "TextView text after set: " + txtLivenessScores.getText());

        Result result = getIntent().getParcelableExtra("result");
        if (result != null) {
            if (result != null && result.fingers != null && !result.fingers.isEmpty()) {


                fingers.addAll(result.fingers);
                adapter.notifyDataSetChanged();


            }

//            StringBuilder livenessScores = new StringBuilder();
//
//            if (result != null && result.livenessScores != null && !result.livenessScores.isEmpty()) {
//
//                for (LivenessScore livenessScore : result.livenessScores) {
//                    livenessScores.append(getLivenessScoreDescription(livenessScore)).append("\n");
//                }
//
//            }
//
//            txtLivenessScores.setText(livenessScores.toString());

            SpannableStringBuilder ssb = new SpannableStringBuilder();

            if (result.livenessScores != null && !result.livenessScores.isEmpty()) {
                float livenessThreshold = settingsPrefManager.getLivenessThreshold();

                for (LivenessScore livenessScore : result.livenessScores) {
                    String line = getLivenessScoreDescription(livenessScore); // your existing mapping
                    int start = ssb.length();
                    ssb.append(line).append("\n");

                    // if score < threshold -> red; if >= threshold -> keep default color
                    if (livenessScore.score < livenessThreshold) {
                        ssb.setSpan(
                                new ForegroundColorSpan(Color.RED),
                                start,
                                ssb.length() - 1,
                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        );
                    }
                }
            }

            txtLivenessScores.setText(ssb);
        }
    }


//    private String getLivenessScoreDescription(LivenessScore livenessScore){
//
//        if(livenessScore==null){
//            return "";
//        }else if(livenessScore.pos== NistPosCode.POS_CODE_PL_L_4F){
//            return "Left Slap: "+livenessScore.score;
//
//        }else if(livenessScore.pos== NistPosCode.POS_CODE_PL_R_4F){
//            return "Right Slap: "+livenessScore.score;
//
//        }else if(livenessScore.pos== NistPosCode.POS_CODE_L_THUMB){
//            return "Left Thumb: "+livenessScore.score;
//
//        } else if(livenessScore.pos== NistPosCode.POS_CODE_R_THUMB){
//            return "Right Thumb: "+livenessScore.score;
//        }
//        else if (livenessScore.pos == NistPosCode.POS_CODE_L_INDEX_F) {
//            return  "Left Index: "+livenessScore.score;
//        }else if (livenessScore.pos == NistPosCode.POS_CODE_R_INDEX_F) {
//            return  "Right Index: "+livenessScore.score;
//        }  else if (livenessScore.pos == NistPosCode.POS_CODE_L_INDEX_MIDDLE) {
//            return  "Left Index Middle: "+livenessScore.score;
//        }else if (livenessScore.pos == NistPosCode.POS_CODE_R_INDEX_MIDDLE) {
//            return "Right Index Middle: " + livenessScore.score;
//        }
//        else{
//            return "Pos: "+livenessScore.pos+": "+livenessScore.score;
//        }
//    }

private String getLivenessScoreDescription(LivenessScore livenessScore) {
    if (livenessScore == null) {
        return "";
    }
    Log.d("ResultScreen.this","score:"+livenessScore.score+"threshold:"+settingsPrefManager.getLivenessThreshold());
    float threshold = settingsPrefManager.getLivenessThreshold();

    if (livenessScore.score > threshold) {
        // Passed with position label
        if (livenessScore.pos == NistPosCode.POS_CODE_PL_L_4F) {
            return "Left Slap Liveness passed:"+livenessScore.score;
        } else if (livenessScore.pos == NistPosCode.POS_CODE_PL_R_4F) {
            return "Right Slap Liveness passed:"+livenessScore.score;
        } else if (livenessScore.pos == NistPosCode.POS_CODE_L_THUMB) {
            return "Left Thumb Liveness passed:"+livenessScore.score;
        } else if (livenessScore.pos == NistPosCode.POS_CODE_R_THUMB) {
            return "Right Thumb Liveness passed:"+livenessScore.score;
        } else if (livenessScore.pos == NistPosCode.POS_CODE_L_INDEX_F) {
            return "Left Index Liveness passed:"+livenessScore.score;
        }else if (livenessScore.pos == NistPosCode.POS_CODE_R_INDEX_F) {
            return "Right Index Liveness passed:"+livenessScore.score;
        }  else if (livenessScore.pos == NistPosCode.POS_CODE_L_INDEX_MIDDLE) {
            return "Left Index Middle Liveness passed:"+livenessScore.score;
        }else if (livenessScore.pos == NistPosCode.POS_CODE_R_INDEX_MIDDLE) {
            return "Right Index Middle Liveness passed:"+livenessScore.score;
        } else {
            return "Liveness passed:"+livenessScore.score;
        }
    } else {
        // Not passed - show score with label
        if (livenessScore.pos == NistPosCode.POS_CODE_PL_L_4F) {
            return "Left Slap Liveness score: " + livenessScore.score;
        } else if (livenessScore.pos == NistPosCode.POS_CODE_PL_R_4F) {
            return "Right Slap Liveness score: " + livenessScore.score;
        } else if (livenessScore.pos == NistPosCode.POS_CODE_L_THUMB) {
            return "Left Thumb Liveness score: " + livenessScore.score;
        } else if (livenessScore.pos == NistPosCode.POS_CODE_R_THUMB) {
            return "Right Thumb Liveness score: " + livenessScore.score;
        }
        else if (livenessScore.pos == NistPosCode.POS_CODE_L_INDEX_F) {
            return "Left Index Liveness score: " + livenessScore.score;
        }else if (livenessScore.pos == NistPosCode.POS_CODE_R_INDEX_F) {
            return "Right Index Liveness score: " + livenessScore.score;
        }  else if (livenessScore.pos == NistPosCode.POS_CODE_L_INDEX_MIDDLE) {
            return "Left Index Middle Liveness score: " + livenessScore.score;
        }else if (livenessScore.pos == NistPosCode.POS_CODE_R_INDEX_MIDDLE) {
            return "Right Index Middle Liveness score: " + livenessScore.score;
        }else {
            return "Pos: " + livenessScore.pos + " Liveness score: " + livenessScore.score;
        }
    }
}


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
