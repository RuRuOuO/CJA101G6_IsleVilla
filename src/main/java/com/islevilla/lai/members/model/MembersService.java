package com.islevilla.lai.members.model;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MembersService {
	
	@Autowired
	MembersRepository repository;
	
	@Autowired
	private SessionFactory sessionFactory;
	
	public void addMember(Members members) {
		repository.save(members);
	}
	
	public void updateMember(Members members) {
		repository.save(members);
	}
	
	public void deleteMember(Integer memberId) {
		if (repository.existsById(memberId)) {
//			repository.deleteById(memberId);
		}
	}
	
	public Members getOneMember(Integer memberId) {
		Optional<Members> optional = repository.findById(memberId);
//		return optional.get();
		return optional.orElse(null);  // public T orElse(T other) : 如果值存在就回傳其值，否則回傳other的值
	}
	
	public List<Members> getAll() {
		return repository.findAll();
	}
	
}
