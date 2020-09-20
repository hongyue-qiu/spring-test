package com.thoughtworks.rslist.repository;

import com.thoughtworks.rslist.dto.RsEventDto;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface RsEventRepository extends CrudRepository<RsEventDto, Integer> {
  List<RsEventDto> findAll();
  @Query(value = "SELECT * from rsEvent where rsEvent.trade.id = rsEvent.rank", nativeQuery = true)
  List<RsEventDto> findRsEventByTradeRecord(int rank);
//  List<RsEventDto> findRsEventByRank(int rank);
  RsEventDto findRsEventByRank(int rank);
//  List<RsEventDto> findRsEventByVoteNum();
  RsEventDto findRsEventById(int id);
  @Transactional
  void deleteAllByUserId(int userId);
}
