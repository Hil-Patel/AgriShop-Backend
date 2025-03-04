package com.agrishop.repository;

import com.agrishop.model.Crop;
import com.agrishop.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CropRepository extends JpaRepository<Crop, Long> {
    List<Crop> findBySeller(User seller);
    List<Crop> findByStatus(Crop.CropStatus status);
    
    @Query("SELECT c FROM Crop c WHERE c.status = 'ACTIVE' AND c.endDate > ?1 ORDER BY c.createdAt DESC")
    List<Crop> findActiveCrops(LocalDateTime now);
    
    @Query("SELECT c FROM Crop c WHERE c.name LIKE %?1% OR c.description LIKE %?1%")
    List<Crop> searchCrops(String keyword);
}