package org.test.task.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.test.task.entity.Transaction;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Page<Transaction> findByFromCardUserIdOrToCardUserId(Long fromUserId, Long toUserId, Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE " +
            "(t.fromCard.user.id = :userId OR t.toCard.user.id = :userId) " +
            "ORDER BY t.transactionDate DESC")
    Page<Transaction> findByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE " +
            "t.fromCard.id = :cardId OR t.toCard.id = :cardId " +
            "ORDER BY t.transactionDate DESC")
    List<Transaction> findByCardId(@Param("cardId") Long cardId);

    @Query("SELECT t FROM Transaction t WHERE " +
            "(t.fromCard.user.id = :userId OR t.toCard.user.id = :userId) AND " +
            "t.transactionDate BETWEEN :startDate AND :endDate " +
            "ORDER BY t.transactionDate DESC")
    Page<Transaction> findByUserIdAndDateRange(@Param("userId") Long userId,
                                               @Param("startDate") LocalDateTime startDate,
                                               @Param("endDate") LocalDateTime endDate,
                                               Pageable pageable);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE " +
            "t.fromCard.id = :cardId AND t.status = 'COMPLETED' AND " +
            "t.transactionDate BETWEEN :startDate AND :endDate")
    Double getTotalOutgoingByCardAndDateRange(@Param("cardId") Long cardId,
                                              @Param("startDate") LocalDateTime startDate,
                                              @Param("endDate") LocalDateTime endDate);
}
