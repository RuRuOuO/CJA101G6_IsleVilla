package com.islevilla.wei.news.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NewsRepository extends JpaRepository<News, Integer> { // <實體, 主鍵型別>
    Page<News> findAllByNewsStatus(int newsStatus, Pageable pageable);

    List<News> findTop3ByNewsStatusOrderByNewsTimeDesc(Integer newsStatus);
}
