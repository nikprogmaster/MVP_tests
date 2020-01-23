package com.example.mvptests.view;


import com.example.mvptests.data.dataprovider.LearningProgramProvider;
import com.example.mvptests.data.models.Lecture;

import java.util.List;

public interface IProgramInstalledView {

    void showProgress();

    void hideProgress();

    void showData(List<Lecture> lectures, LearningProgramProvider learningProgramProvider);
}
