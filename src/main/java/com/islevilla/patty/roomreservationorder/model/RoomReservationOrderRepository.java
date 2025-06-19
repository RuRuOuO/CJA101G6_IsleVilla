//// https://docs.spring.io/spring-data/jpa/docs/current/reference/html/
//
//package com.islevilla.patty.roomreservationorder.model;
//
//import java.util.List;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Modifying;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.transaction.annotation.Transactional;
//
//public interface RoomReservationOrderRepository extends JpaRepository<RoomReservationOrder, Integer> {
//
//	@Transactional
//	@Modifying
//	@Query(value = "delete from emp3 where empno =?1", nativeQuery = true)
//	void deleteByRoomReservationId(int roomreservationid);
//
//	//● (自訂)條件查詢
//	@Query(value = "from RoomReservationOrder where empno=?1 and ename like?2 and hiredate=?3 order by roomreservationorder")
//	List<RoomReservationOrder> findByOthers(int roomreservationorder , String ename , java.sql.Date hiredate);
//}