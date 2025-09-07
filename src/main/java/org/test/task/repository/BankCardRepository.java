package org.test.task.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.test.task.entity.BankCard;
import org.test.task.entity.CardStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BankCardRepository extends JpaRepository<BankCard, Long> {
    Page<BankCard> findByUserId(Long userId, Pageable pageable);

    List<BankCard> findByUserId(Long userId);

    Page<BankCard> findByUserIdAndStatus(Long userId, CardStatus status, Pageable pageable);

    @Query("SELECT c FROM BankCard c WHERE c.user.id = :userId AND " +
            "(LOWER(c.maskedCardNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(c.holderName) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<BankCard> findByUserIdAndSearch(@Param("userId") Long userId,
                                         @Param("search") String search,
                                         Pageable pageable);

    Optional<BankCard> findByIdAndUserId(Long id, Long userId);

    Long countByUserId(Long userId);

    List<BankCard> findByExpirationDateBeforeAndStatusNot(LocalDate date, CardStatus status);

    @Query("SELECT c FROM BankCard c WHERE c.cardNumberEncrypted = :encryptedNumber")
    Optional<BankCard> findByEncryptedCardNumber(@Param("encryptedNumber") String encryptedNumber);

    @Query("SELECT c FROM BankCard c WHERE c.user.id = :userId AND c.status = 'ACTIVE'")
    List<BankCard> findActiveCardsByUserId(@Param("userId") Long userId);

    @Query("SELECT SUM(c.balance) FROM BankCard c WHERE c.user.id = :userId AND c.status = 'ACTIVE'")
    Double getTotalBalanceByUserId(@Param("userId") Long userId);
}
