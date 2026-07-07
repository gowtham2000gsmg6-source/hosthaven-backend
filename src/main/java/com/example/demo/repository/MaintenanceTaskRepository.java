package com.example.demo.repository;

import com.example.demo.entity.MaintenanceTask;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface MaintenanceTaskRepository extends JpaRepository<MaintenanceTask, Long> {

    List<MaintenanceTask> findByStatus(MaintenanceTask.TaskStatus status);
}
