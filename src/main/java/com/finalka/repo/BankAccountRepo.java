package com.finalka.repo;

import com.finalka.entity.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;


public interface BankAccountRepo extends JpaRepository<BankAccount, Long> {

}
