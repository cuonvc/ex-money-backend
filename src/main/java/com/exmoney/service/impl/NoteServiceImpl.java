package com.exmoney.service.impl;

import com.exmoney.entity.Note;
import com.exmoney.payload.common.BaseResponse;
import com.exmoney.payload.common.ResponseFactory;
import com.exmoney.payload.mapper.NoteMapper;
import com.exmoney.repository.NoteRepository;
import com.exmoney.service.CommonService;
import com.exmoney.service.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

import static com.exmoney.payload.enumerate.ErrorCode.NOTE_NOT_FOUND;
import static com.exmoney.util.Constant.Status.ACTIVE;
import static com.exmoney.util.Constant.Status.DELETED;
import static com.exmoney.util.Utils.getNow;

@Service
@RequiredArgsConstructor
public class NoteServiceImpl implements NoteService {

    private final NoteRepository noteRepository;
    private final ResponseFactory responseFactory;
    private final CommonService commonService;
    private final NoteMapper noteMapper;

    @Value("${exmoney.application.action_log.note_init}")
    private String initNoteLog;
    @Value("${exmoney.application.action_log.note_update}")
    private String updateNoteLog;
    @Value("${exmoney.application.action_log.note_delete}")
    private String deleteNoteLog;

    @Override
    public ResponseEntity<BaseResponse<Note>> init(Note note, Locale locale) {
        LocalDateTime now = getNow();
        Long currentUserId = commonService.getCurrentUserId();
        note.setId(null);
        note.setCreatedAt(now);
        note.setUpdatedAt(now);
        note.setStatus(ACTIVE);
        note.setCreatedBy(currentUserId);
        note.setUpdatedBy(currentUserId);
        return responseFactory.success(initNoteLog, noteRepository.save(note));
    }

    @Override
    public ResponseEntity<BaseResponse<List<Note>>> list(Locale locale) {
        List<Note> list = noteRepository.findAllByUser(commonService.getCurrentUserId());
        return responseFactory.success(null, list);
    }

    @Override
    public ResponseEntity<BaseResponse<Note>> update(Long id, Note request, Locale locale) {
        Note note = noteRepository.findByIdAndUser(id, commonService.getCurrentUserId());
        if (note == null) {
            commonService.throwException(NOTE_NOT_FOUND, locale, null);
        }

        note = noteMapper.toEntity(request, note);
        note.setUpdatedAt(getNow());
        return responseFactory.success(updateNoteLog, noteRepository.save(note));
    }

    @Override
    public ResponseEntity<BaseResponse<Boolean>> delete(Long id, Locale locale) {
        Note note = noteRepository.findByIdAndUser(id, commonService.getCurrentUserId());
        if (note == null) {
            commonService.throwException(NOTE_NOT_FOUND, locale, null);
        }

        note.setStatus(DELETED);
        note.setUpdatedAt(getNow());
        return responseFactory.success(deleteNoteLog, true);
    }
}
