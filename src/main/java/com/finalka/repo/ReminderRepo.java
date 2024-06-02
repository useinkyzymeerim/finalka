package com.finalka.repo;

import com.finalka.entity.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReminderRepo extends JpaRepository<Reminder, Long> {
    @Query("SELECT r FROM Reminder r WHERE r.deletedAt IS NULL AND r.id = :id")
    Optional<Reminder> findByIdAndDeletedAtIsNull(@Param("id") Long id);
    @Query("SELECT r FROM Reminder r WHERE r.deletedAt IS NULL")
    List<Reminder> findAllActive();
    List<Reminder> findByCreatedByAndDeletedAtIsNull(String createdBy);
}
