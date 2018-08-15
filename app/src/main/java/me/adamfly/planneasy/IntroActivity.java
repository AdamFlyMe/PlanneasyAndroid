package me.adamfly.planneasy;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.github.paolorotolo.appintro.model.SliderPage;

public class IntroActivity extends AppIntro {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SliderPage sliderPage = new SliderPage();
        sliderPage.setTitle("Welcome to Planneasy!");
        sliderPage.setDescription("This intro will walk you through the basic functionality.");
        sliderPage.setImageDrawable(R.mipmap.ic_launcher_round);
        sliderPage.setBgColor(Color.parseColor("#3f51b5"));
        addSlide(AppIntroFragment.newInstance(sliderPage));

        SliderPage sliderPage2 = new SliderPage();
        sliderPage2.setTitle("Write notes");
        sliderPage2.setDescription("Create entries by clicking the plus icon and edit existing entries by holding down on them.");
        sliderPage2.setImageDrawable(R.drawable.ic_books);
        sliderPage2.setBgColor(Color.parseColor("#3f51b5"));
        addSlide(AppIntroFragment.newInstance(sliderPage2));

        SliderPage sliderPage3 = new SliderPage();
        sliderPage3.setTitle("Sync with Chrome Extension");
        sliderPage3.setDescription("Download the Planneasy Chrome Extension to access your notes on the go!");
        sliderPage3.setImageDrawable(R.drawable.ic_extension);
        sliderPage3.setBgColor(Color.parseColor("#3f51b5"));
        addSlide(AppIntroFragment.newInstance(sliderPage3));

        SliderPage sliderPage4 = new SliderPage();
        sliderPage4.setTitle("Need help?");
        sliderPage4.setDescription("Go to the Play Store listing for any futher questions/concerns!");
        sliderPage4.setImageDrawable(R.drawable.ic_help);
        sliderPage4.setBgColor(Color.parseColor("#3f51b5"));
        addSlide(AppIntroFragment.newInstance(sliderPage4));

        showSkipButton(true);
        setProgressButtonEnabled(true);

    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
    }
}