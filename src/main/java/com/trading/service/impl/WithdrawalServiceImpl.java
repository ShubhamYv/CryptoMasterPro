package com.trading.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.trading.domain.WithdrawalStatus;
import com.trading.modal.User;
import com.trading.modal.Withdrawal;
import com.trading.repository.WithdrawalRepository;
import com.trading.service.WithdrawalService;

@Service
public class WithdrawalServiceImpl implements WithdrawalService {

	@Autowired
	private WithdrawalRepository withdrawalRepository;

	@Override
	public Withdrawal requestWithdrawal(Long amount, User user) {
		Withdrawal withdrawal = new Withdrawal();
		withdrawal.setAmount(amount);
		withdrawal.setUser(user);
		withdrawal.setWithdrawalStatus(WithdrawalStatus.PENDING);
		return withdrawalRepository.save(withdrawal);
	}

	@Override
	public Withdrawal proceedWithWithdrawal(Long withdrawalId, boolean accept) throws Exception {
		Withdrawal withdrawal = withdrawalRepository.findById(withdrawalId)
				.orElseThrow(() -> new Exception("Withdrawal not found"));
		
		withdrawal.setDate(LocalDateTime.now());
		if (accept) {
			withdrawal.setWithdrawalStatus(WithdrawalStatus.SUCCESS);
		} else {
			withdrawal.setWithdrawalStatus(WithdrawalStatus.PENDING);
		}
		return withdrawalRepository.save(withdrawal);
	}

	@Override
	public List<Withdrawal> getUsersWithdrawalHistory(User user) {
		return withdrawalRepository.findByUserId(user.getId());
	}

	@Override
	public List<Withdrawal> getAllWithdrawalRequests() {
		return withdrawalRepository.findAll();
	}

}
