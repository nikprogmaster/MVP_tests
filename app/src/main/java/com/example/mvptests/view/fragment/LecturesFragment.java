package com.example.mvptests.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mvptests.R;
import com.example.mvptests.view.adapters.DisplayModeSpinnerAdapter;
import com.example.mvptests.view.adapters.LectorSpinnerAdapter;
import com.example.mvptests.view.adapters.LecturesAdapter;
import com.example.mvptests.view.adapters.OnLectureClickListener;
import com.example.mvptests.data.dataprovider.LearningProgramProvider;
import com.example.mvptests.data.models.DisplayMode;
import com.example.mvptests.data.models.Lecture;
import com.example.mvptests.presenter.LearningProgramPresenter;
import com.example.mvptests.view.IProgramInstalledView;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Фрагмент со списком лекций
 *
 * @author Evgeny Chumak
 **/
public class LecturesFragment extends Fragment implements IProgramInstalledView {

    private static final int POSITION_ALL = 0;

    private LecturesAdapter mLecturesAdapter;
    private View mLoadingView;
    private RecyclerView mRecyclerView;
    private Spinner mLectorsSpinner;
    private Spinner mDisplayModeSpinner;
    private OnLectureClickListener mOnLectureClickListener = new OnLectureClickListener() {
        @Override
        public void onItemClick(@NonNull Lecture lecture) {
            LecturesFragment.this.requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.root, DetailsFragment.newInstance(lecture))
                    .addToBackStack(DetailsFragment.class.getSimpleName())
                    .commit();
        }
    };

    private LearningProgramPresenter mMainPresenter;

    public static Fragment newInstance() {
        return new LecturesFragment();
    }

    {
        // нужно для того, чтобы инстанс LecturesProvider не убивался после смены конфигурации
        setRetainInstance(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        mMainPresenter.loadAsync();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMainPresenter.detachView();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lectures, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        providePresenter();

    }

    /*@Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        List<Lecture> lectures = mLearningProgramProvider.getLectures();
        if (lectures == null) {
            new LoadLecturesTask(this, savedInstanceState == null).execute();
        } else {
            initRecyclerView(savedInstanceState == null, lectures);
            initLectorsSpinner();
            initDisplayModeSpinner();
        }
    }*/

    private void initViews(View view){
        mLoadingView = view.findViewById(R.id.loading_view);
        mRecyclerView = view.findViewById(R.id.learning_program_recycler);
        mLectorsSpinner = view.findViewById(R.id.lectors_spinner);
        mDisplayModeSpinner = view.findViewById(R.id.display_mode_spinner);

    }

    private void providePresenter(){
        LearningProgramProvider learningProgramProvider = new LearningProgramProvider();
        mMainPresenter = new LearningProgramPresenter(learningProgramProvider, this);
    }

    private void initRecyclerView(boolean isFirstCreate, @NonNull List<Lecture> lectures, LearningProgramProvider mLearningProgramProvider) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mLecturesAdapter = new LecturesAdapter(getResources());
        mLecturesAdapter.setLectures(lectures);
        mLecturesAdapter.setClickListener(mOnLectureClickListener);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        mRecyclerView.setAdapter(mLecturesAdapter);
        if (isFirstCreate) {
            Lecture nextLecture = mLearningProgramProvider.getLectureNextTo(lectures, new Date());
            int positionOfNextLecture = mLecturesAdapter.getPositionOf(nextLecture);
            if (positionOfNextLecture != -1) {
                mRecyclerView.scrollToPosition(positionOfNextLecture);
            }
        }
    }

    private void initLectorsSpinner(final LearningProgramProvider mLearningProgramProvider) {
        final List<String> spinnerItems = mLearningProgramProvider.provideLectors();
        Collections.sort(spinnerItems);
        spinnerItems.add(POSITION_ALL, getResources().getString(R.string.all));
        LectorSpinnerAdapter adapter = new LectorSpinnerAdapter(spinnerItems);
        mLectorsSpinner.setAdapter(adapter);

        mLectorsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final List<Lecture> lectures = position == POSITION_ALL ?
                        mLearningProgramProvider.getLectures() :
                        mLearningProgramProvider.filterBy(spinnerItems.get(position));
                mLecturesAdapter.setLectures(lectures);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void initDisplayModeSpinner() {
        mDisplayModeSpinner.setAdapter(new DisplayModeSpinnerAdapter());
        mDisplayModeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                DisplayMode selectedDisplayMode = DisplayMode.values()[position];
                mLecturesAdapter.setDisplayMode(selectedDisplayMode);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }


    @Override
    public void showProgress() {
        mLoadingView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        mLoadingView.setVisibility(View.GONE);
    }

    @Override
    public void showData(List<Lecture> lectures, LearningProgramProvider learningProgramProvider) {
        initDisplayModeSpinner();
        initLectorsSpinner(learningProgramProvider);
        initRecyclerView(true, lectures, learningProgramProvider);
    }
}
