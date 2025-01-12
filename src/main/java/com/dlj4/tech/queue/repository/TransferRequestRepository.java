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
    @Query("select  TransferRequest p from  TransferRequest where p.requestUser.id=?1 and p.requestStatus=?2")
    public Optional<List<TransferRequest>> findTransferRequestByUserIdAndRequestStatus(int userId, TransferRequestStatus requestStatus);

    @Query  ("select  TransferRequest p from  TransferRequest where p.requestWindow.id=?1 and p.requestStatus=?2")
    public Optional<List<TransferRequest>> findTransferRequestByWindowIdAndRequestStatus(int windowId, TransferRequestStatus requestStatus);

    @Query("select  TransferRequest p from  TransferRequest where p.responseUser.id=?1 and p.requestStatus=?2")
    public Optional<List<TransferRequest>> findTransferRequestByResponseUserAndRequestStatus(int userId, TransferRequestStatus requestStatus);

    @Query  ("select  TransferRequest p from  TransferRequest where p.responseWindow.id=?1 and p.requestStatus=?2")
    public Optional<List<TransferRequest>> findTransferRequestByResponseWindowAndRequestStatus(int windowId, TransferRequestStatus requestStatus);

}
