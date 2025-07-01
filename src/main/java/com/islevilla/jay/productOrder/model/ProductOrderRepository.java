package com.islevilla.jay.productOrder.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ProductOrderRepository extends JpaRepository<ProductOrder, Integer> {

    @Transactional
    @Modifying
    @Query(value = "delete from product_order where product_order_id =?1", nativeQuery = true)
    void deleteByProductOrderId(Integer productOrderId);

    // 根據會員ID查詢訂單
    // List<ProductOrder> findByMemberId(Integer memberId);

    List<ProductOrder> findByMember_MemberId(Integer memberId);

    // 查詢所有訂單，按訂單時間降序排列（最新的在前），並加載優惠券關聯
    @Query("SELECT po FROM ProductOrder po LEFT JOIN FETCH po.coupon ORDER BY po.orderTime DESC")
    List<ProductOrder> findAllByOrderByOrderTimeDesc();

    // 根據會員ID查詢訂單，按訂單時間降序排列，並加載優惠券關聯
    @Query("SELECT po FROM ProductOrder po LEFT JOIN FETCH po.coupon WHERE po.member.memberId = :memberId ORDER BY po.orderTime DESC")
    List<ProductOrder> findByMember_MemberIdOrderByOrderTimeDesc(@Param("memberId") Integer memberId);

    // 根據訂單狀態查詢，按訂單時間降序排列，並加載優惠券關聯
    @Query("SELECT po FROM ProductOrder po LEFT JOIN FETCH po.coupon WHERE po.orderStatus = :orderStatus ORDER BY po.orderTime DESC")
    List<ProductOrder> findByOrderStatusOrderByOrderTimeDesc(@Param("orderStatus") Byte orderStatus);

    // 根據訂單狀態和會員ID查詢，按訂單時間降序排列，並加載優惠券關聯
    @Query("SELECT po FROM ProductOrder po LEFT JOIN FETCH po.coupon WHERE po.orderStatus = :orderStatus AND po.member.memberId = :memberId ORDER BY po.orderTime DESC")
    List<ProductOrder> findByOrderStatusAndMember_MemberIdOrderByOrderTimeDesc(@Param("orderStatus") Byte orderStatus, @Param("memberId") Integer memberId);

    // 根據付款方式查詢
    List<ProductOrder> findByPaymentMethod(Byte paymentMethod);

    // 根據訂單金額範圍查詢
    List<ProductOrder> findByProductOrderAmountBetween(Double minAmount, Double maxAmount);

    // 根據訂單狀態和會員ID查詢
    // List<ProductOrder> findByOrderStatusAndMemberId(Byte orderStatus, Integer memberId);

    List<ProductOrder> findByOrderStatusAndMember_MemberId(Byte orderStatus, Integer memberId);

    //● (自訂)條件查詢
    // @Query(value = "from ProductOrder where productOrderId=?1 and memberId=?2 and orderStatus=?3 order by productOrderId")
    // List<ProductOrder> findByOthers(Integer productOrderId, Integer memberId, Byte orderStatus);

    @Query("from ProductOrder where productOrderId = ?1 and member.memberId = ?2 and orderStatus = ?3 order by productOrderId")
    List<ProductOrder> findByOthers(Integer productOrderId, Integer memberId, Byte orderStatus);

    // List<ProductOrder> findByMember_MemberId(Integer memberId);
    
    // Dashboard 相關查詢方法
    long countByOrderTimeBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    List<ProductOrder> findByOrderTimeBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    long countByOrderStatus(Byte orderStatus);
}
