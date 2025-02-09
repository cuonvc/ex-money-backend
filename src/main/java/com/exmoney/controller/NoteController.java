package com.exmoney.controller;

import com.exmoney.entity.Note;
import com.exmoney.payload.common.BaseResponse;
import com.exmoney.service.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

import static com.exmoney.util.Constant.API_BASE_USER;

@RestController
@RequiredArgsConstructor
public class NoteController {

    final NoteService noteService;

    @PostMapping(API_BASE_USER + "/note")
    public ResponseEntity<BaseResponse<Note>> init(@RequestBody Note note, @RequestParam Locale locale) {
        return noteService.init(note, locale);
    }

    @PutMapping(API_BASE_USER + "/note/{id}")
    public ResponseEntity<BaseResponse<Note>> init(@PathVariable Long id,
                                                   @RequestBody Note note,
                                                   @RequestParam Locale locale) {
        return noteService.update(id, note, locale);
    }

    @GetMapping(API_BASE_USER + "/note")
    public ResponseEntity<BaseResponse<List<Note>>> getAll(@RequestParam Locale locale) {
        return noteService.list(locale);
    }

    @DeleteMapping(API_BASE_USER + "/note/{id}")
    public ResponseEntity<BaseResponse<Boolean>> delete(@PathVariable Long id, @RequestParam Locale locale) {
        return noteService.delete(id, locale);
    }
}
