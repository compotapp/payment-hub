package com.pot.app.accountservice.grpc;

import com.pot.app.proto.account.AccountServiceGrpc;
import com.pot.app.proto.account.BalanceRequest;
import com.pot.app.proto.account.BalanceResponse;
import com.pot.app.proto.account.CancelRequest;
import com.pot.app.proto.account.CancelResponse;
import com.pot.app.proto.account.CommitRequest;
import com.pot.app.proto.account.ReserveRequest;
import com.pot.app.proto.account.ReserveResponse;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class AccountGrpcService extends AccountServiceGrpc.AccountServiceImplBase {

    @Override
    public void reserveFunds(ReserveRequest request, StreamObserver<ReserveResponse> responseObserver) {
        super.reserveFunds(request, responseObserver);
    }

    @Override
    public void commitFunds(CommitRequest request, StreamObserver<com.pot.app.proto.account.CommitResponse> responseObserver) {
        super.commitFunds(request, responseObserver);
    }

    @Override
    public void cancelReservation(CancelRequest request, StreamObserver<CancelResponse> responseObserver) {
        super.cancelReservation(request, responseObserver);
    }

    @Override
    public void getBalance(BalanceRequest request, StreamObserver<BalanceResponse> responseObserver) {
        super.getBalance(request, responseObserver);
    }
}