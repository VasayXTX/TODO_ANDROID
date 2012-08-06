package com.example.todo;

// Declare the interface.
interface INoteService {
    // You can pass values in, out, or inout. 
    // Primitive datatypes (such as int, boolean, etc.) can only be passed in.
    String getNotes();
    void addNote(String title, String description);
    void deleteNotes(String notes);
}
