package com.dlj4.tech.queue.repository;


import com.dlj4.tech.queue.constants.OrderStatus;
import com.dlj4.tech.queue.constants.TransferRequestStatus;
import com.dlj4.tech.queue.entity.TransferRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransferRequestRepository  extends JpaRepository<TransferRequest,Long> {
    @Query("select p from TransferRequest p where p.requestUser.id = ?1 and p.requestStatus = ?2")
    Optional<List<TransferRequest>> findTransferRequestByUserIdAndRequestStatus(Long userId, TransferRequestStatus requestStatus);

    @Query("select p from TransferRequest p where p.requestWindow.id = ?1 and p.requestStatus = ?2")
    Optional<List<TransferRequest>> findTransferRequestByWindowIdAndRequestStatus(Long windowId, TransferRequestStatus requestStatus);

    @Query("select p from TransferRequest p where p.responseUser.id = ?1 and p.requestStatus = ?2")
    Optional<List<TransferRequest>> findTransferRequestByResponseUserAndRequestStatus(Long userId, TransferRequestStatus requestStatus);

    @Query("select p from TransferRequest p where p.responseWindow.id = ?1 and p.requestStatus = ?2")
    Optional<List<TransferRequest>> findTransferRequestByResponseWindowAndRequestStatus(Long windowId, TransferRequestStatus requestStatus);
}
