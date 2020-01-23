package com.example.mvptests.view.adapters;

import androidx.annotation.NonNull;

import com.example.mvptests.data.models.Lecture;


/**
 * Обработчик нажатия на элемент списка лекций
 *
 * @author Evgeny Chumak
 **/
public interface OnLectureClickListener {

    /**
     * Обрабатывает нажатие на элемент списка с переданной лекцией
     *
     * @param lecture на элемент списка с какой лекцией нажали
     */
    void onItemClick(@NonNull Lecture lecture);
}
