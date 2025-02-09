package com.exmoney.service;

import com.exmoney.entity.Note;
import com.exmoney.payload.common.BaseResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Locale;

public interface NoteService {
    ResponseEntity<BaseResponse<Note>> init(Note note, Locale locale);
    ResponseEntity<BaseResponse<List<Note>>> list(Locale locale);
    ResponseEntity<BaseResponse<Note>> update(Long id, Note note, Locale locale);
    ResponseEntity<BaseResponse<Boolean>> delete(Long id, Locale locale);
}
