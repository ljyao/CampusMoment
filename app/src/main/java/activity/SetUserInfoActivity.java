package activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.uy.bbs.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

/**
 * Created by ljy on 15/12/29.
 */
@EActivity
public class SetUserInfoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo_set);
        Intent intent = new Intent(this, EditUserHeaderActivity_.class);
        startActivity(intent);
    }

    @AfterViews
    public void initView() {

    }
}
