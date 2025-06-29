package com.islevilla.lai.shuttle.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TempShuttleRVRequestRepository extends JpaRepository<TempShuttleRVRequest, Integer>{

}
