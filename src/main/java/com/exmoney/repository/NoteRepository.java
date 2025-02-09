package com.exmoney.repository;

import com.exmoney.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {

    @Query("SELECT n FROM Note n " +
            "WHERE n.id = :id " +
            "AND n.createdBy = :userId " +
            "AND n.status <> 'DELETED'")
    Note findByIdAndUser(Long id, Long userId);

    @Query("SELECT n FROM Note n " +
            "WHERE n.createdBy = :userId " +
            "AND n.status <> 'DELETED' " +
            "ORDER BY n.updatedAt DESC")
    List<Note> findAllByUser(Long userId);
}
