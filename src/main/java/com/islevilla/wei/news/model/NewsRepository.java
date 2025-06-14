package com.islevilla.wei.news.model;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsRepository extends JpaRepository<News, Integer> { // <實體, 主鍵型別>

}
