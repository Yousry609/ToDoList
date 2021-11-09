package com.turki.todolist.ui.adapter

import com.turki.todolist.model.entity.Note

interface OnListItemClick {
    fun onItemClick(note:Note)
    fun onItemDelete(note: Note)
}