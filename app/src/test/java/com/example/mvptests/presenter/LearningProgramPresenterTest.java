package com.example.mvptests.presenter;


import com.example.mvptests.data.dataprovider.LearningProgramProvider;
import com.example.mvptests.data.models.Lecture;
import com.example.mvptests.view.IProgramInstalledView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(MockitoJUnitRunner.class)
public class LearningProgramPresenterTest {

    @Mock
    LearningProgramProvider learningProgramProvider;

    @Mock
    IProgramInstalledView programInstalledView;

    LearningProgramPresenter learningProgramPresenter;

    @Before
    public void setUp(){
        learningProgramPresenter = new LearningProgramPresenter(learningProgramProvider, programInstalledView);
    }

    @Test
    public void testDataAsync(){


        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {

                LearningProgramProvider.OnLoadingFinishListener onLoadingFinishListener =
                        (LearningProgramProvider.OnLoadingFinishListener) invocation.getArguments()[0];

                onLoadingFinishListener.onFinish(createTestData());
                return null;
            }
        }).when(learningProgramProvider).loadDataAsync(Mockito.any(LearningProgramProvider.OnLoadingFinishListener.class));

        learningProgramPresenter.loadAsync();

        InOrder inOrder = Mockito.inOrder(programInstalledView);

        inOrder.verify(programInstalledView).showProgress();
        inOrder.verify(programInstalledView).hideProgress();
        inOrder.verify(programInstalledView).showData(createTestData(), learningProgramProvider);

        inOrder.verifyNoMoreInteractions();

    }

    @Test
    public void testDetachView() {
        learningProgramPresenter.detachView();

        learningProgramPresenter.loadAsync();

        verifyNoMoreInteractions(programInstalledView);
    }

    private List<Lecture> createTestData() {
        List<Lecture> testData = new ArrayList<>();

        testData = Arrays.asList(new Lecture(1, "12.10.2019", "Фрагменты", "Дмитрий Соколов", Arrays.asList("FragmentManager", "NewInstance")),
                new Lecture(2, "15.01.2020", "Permissions", "Андрей Кудрявцев", Arrays.asList("Permissions", "Internal storage")));

        return testData;
    }
}
