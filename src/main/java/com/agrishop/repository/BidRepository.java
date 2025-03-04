package com.agrishop.repository;

import com.agrishop.model.Bid;
import com.agrishop.model.Crop;
import com.agrishop.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface BidRepository extends JpaRepository<Bid, Long> {
    List<Bid> findByCrop(Crop crop);
    List<Bid> findByBidder(User bidder);
    
    @Query("SELECT b FROM Bid b WHERE b.crop = ?1 ORDER BY b.amount DESC")
    List<Bid> findByCropOrderByAmountDesc(Crop crop);
    
    @Query("SELECT MAX(b.amount) FROM Bid b WHERE b.crop = ?1")
    Optional<BigDecimal> findHighestBidAmountByCrop(Crop crop);
}