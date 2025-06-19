//package com.islevilla.patty.roompromotionprice.model;
//
//import java.util.List;
//import java.util.Map;
//import java.util.Optional;
//
//import org.hibernate.Session;
//import org.hibernate.SessionFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import hibernate.util.CompositeQuery.HibernateUtil_CompositeQuery_Emp3;
//
//
//@Service("empService")
//public class EmpService {
//
//	@Autowired
//	EmpRepository repository;
//	
//	@Autowired
//    private SessionFactory sessionFactory;
//
//	public void addEmp(EmpVO empVO) {
//		repository.save(empVO);
//	}
//
//	public void updateEmp(EmpVO empVO) {
//		repository.save(empVO);
//	}
//
//	public void deleteEmp(Integer empno) {
//		if (repository.existsById(empno))
//			repository.deleteByEmpno(empno);
////		    repository.deleteById(empno);
//	}
//
//	public EmpVO getOneEmp(Integer empno) {
//		Optional<EmpVO> optional = repository.findById(empno);
////		return optional.get();
//		return optional.orElse(null);  // public T orElse(T other) : 如果值存在就回傳其值，否則回傳other的值
//	}
//
//	public List<EmpVO> getAll() {
//		return repository.findAll();
//	}
//
//	public List<EmpVO> getAll(Map<String, String[]> map) {
//		return HibernateUtil_CompositeQuery_Emp3.getAllC(map,sessionFactory.openSession());
//	}
//
//}