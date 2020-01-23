package com.example.mvptests.presenter;


import com.example.mvptests.data.dataprovider.LearningProgramProvider;
import com.example.mvptests.data.models.Lecture;
import com.example.mvptests.view.IProgramInstalledView;

import java.lang.ref.WeakReference;
import java.util.List;

public class LearningProgramPresenter {

    private final LearningProgramProvider learningProgramProvider;
    private final WeakReference<IProgramInstalledView> mainActivityWeakReference;

    public LearningProgramPresenter(LearningProgramProvider programProvider, IProgramInstalledView mainActivity){
        mainActivityWeakReference = new WeakReference<>(mainActivity);
        learningProgramProvider = programProvider;
    }

    public void loadAsync(){
        IProgramInstalledView iProgramInstalledView = mainActivityWeakReference.get();

        if(iProgramInstalledView != null){
            iProgramInstalledView.showProgress();
        }

        LearningProgramProvider.OnLoadingFinishListener onLoadingFinishListener = new LearningProgramProvider.OnLoadingFinishListener() {
            @Override
            public void onFinish(List<Lecture> lectures) {
                IProgramInstalledView programInstalledView = mainActivityWeakReference.get();
                if(programInstalledView != null) {
                    programInstalledView.hideProgress();
                    programInstalledView.showData(lectures, learningProgramProvider);
                }
            }
        };

        learningProgramProvider.loadDataAsync(onLoadingFinishListener);
    }

    public void detachView(){
        mainActivityWeakReference.clear();
    }
}
