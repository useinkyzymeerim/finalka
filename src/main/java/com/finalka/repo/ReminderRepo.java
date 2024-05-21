package com.finalka.repo;

import com.finalka.entity.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReminderRepo extends JpaRepository<Reminder, Long> {
    List<Reminder> findByUserId(Long userId);

    void deleteByUserId(Long userId);
}
