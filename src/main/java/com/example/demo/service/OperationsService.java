package com.example.demo.service;

import com.example.demo.entity.MaintenanceTask;
import com.example.demo.entity.PropertyListing;
import com.example.demo.repository.MaintenanceTaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;

@Service
public class OperationsService {

    private final MaintenanceTaskRepository maintenanceTaskRepository;

    public OperationsService(MaintenanceTaskRepository maintenanceTaskRepository) {
        this.maintenanceTaskRepository = maintenanceTaskRepository;
    }

    @Transactional
    public MaintenanceTask scheduleTurnover(PropertyListing listing, LocalDate checkOutDate) {
        MaintenanceTask task = new MaintenanceTask();
        task.setListing(listing);
        task.setTaskType(MaintenanceTask.TaskType.CLEANING);
        task.setStatus(MaintenanceTask.TaskStatus.TODO);
        task.setScheduledDate(checkOutDate.plusDays(1));
        return maintenanceTaskRepository.save(task);
    }
}
