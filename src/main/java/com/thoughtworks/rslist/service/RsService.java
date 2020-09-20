package com.thoughtworks.rslist.service;

import com.thoughtworks.rslist.domain.RsEvent;
import com.thoughtworks.rslist.domain.Trade;
import com.thoughtworks.rslist.domain.Vote;
import com.thoughtworks.rslist.dto.RsEventDto;
import com.thoughtworks.rslist.dto.TradeDto;
import com.thoughtworks.rslist.dto.UserDto;
import com.thoughtworks.rslist.dto.VoteDto;
import com.thoughtworks.rslist.repository.RsEventRepository;
import com.thoughtworks.rslist.repository.TradeRepository;
import com.thoughtworks.rslist.repository.UserRepository;
import com.thoughtworks.rslist.repository.VoteRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RsService {
  final RsEventRepository rsEventRepository;
  final UserRepository userRepository;
  final VoteRepository voteRepository;
  final TradeRepository tradeRepository;

  public RsService(RsEventRepository rsEventRepository, UserRepository userRepository, VoteRepository voteRepository, TradeRepository tradeRepository) {
    this.rsEventRepository = rsEventRepository;
    this.userRepository = userRepository;
    this.voteRepository = voteRepository;
    this.tradeRepository = tradeRepository;
  }

  public void vote(Vote vote, int rsEventId) {
    Optional<RsEventDto> rsEventDto = rsEventRepository.findById(rsEventId);
    Optional<UserDto> userDto = userRepository.findById(vote.getUserId());
    if (!rsEventDto.isPresent()
        || !userDto.isPresent()
        || vote.getVoteNum() > userDto.get().getVoteNum()) {
      throw new RuntimeException();
    }
    VoteDto voteDto =
        VoteDto.builder()
            .localDateTime(vote.getTime())
            .num(vote.getVoteNum())
            .rsEvent(rsEventDto.get())
            .user(userDto.get())
            .build();
    voteRepository.save(voteDto);
    UserDto user = userDto.get();
    user.setVoteNum(user.getVoteNum() - vote.getVoteNum());
    userRepository.save(user);
    RsEventDto rsEvent = rsEventDto.get();
    rsEvent.setVoteNum(rsEvent.getVoteNum() + vote.getVoteNum());
    rsEventRepository.save(rsEvent);
  }

  public ResponseEntity buy(Trade trade, int id) {
    List<TradeDto> tradeDtos = tradeRepository.findAll();
    Optional<RsEventDto> repositoryById = rsEventRepository.findById(id);
    if (!repositoryById.isPresent()) {
      throw new RuntimeException();
    }
    if (tradeDtos.size() > 0){
      if (trade.getAmount() > tradeDtos.get(tradeDtos.size()-1).getAmount()){
        TradeDto tradeDto = TradeDto.builder()
                .amount(trade.getAmount())
                .rank(trade.getRank())
                .build();
        tradeRepository.save(tradeDto);
        RsEventDto rsEvent = repositoryById.get();
        rsEvent.setRank(trade.getRank());
        rsEventRepository.save(rsEvent);
        return ResponseEntity.ok().build();
      }
      return ResponseEntity.badRequest().build();
    }else {
      TradeDto tradeDto = TradeDto.builder()
              .amount(trade.getAmount())
              .rank(trade.getRank())
              .build();
      tradeRepository.save(tradeDto);
      RsEventDto rsEvent = repositoryById.get();
      rsEvent.setRank(trade.getRank());
      rsEventRepository.save(rsEvent);
      return ResponseEntity.ok().build();
    }
  }
  public void sortRsEvent(){
    List<RsEventDto> rsEventsVote = rsEventRepository.findAll();
    Collections.sort(rsEventsVote,new Comparator<RsEventDto>() {
      public int compare(RsEventDto rsEventDto1, RsEventDto rsEventDto2) {
        if (rsEventDto1.getVoteNum() > rsEventDto2.getVoteNum()) {
          return -1;
        }
        if (rsEventDto1.getVoteNum() == rsEventDto2.getVoteNum()) {
          return 0;
        }
        return 1;
      }
    });
    Collections.sort(rsEventsVote,new Comparator<RsEventDto>() {
      public int compare(RsEventDto rsEventDto1, RsEventDto rsEventDto2) {
        if (rsEventDto1.getRank() > rsEventDto2.getRank()) {
          return -1;
        }
        if (rsEventDto1.getRank() == rsEventDto2.getRank()) {
          return 0;
        }
        return 1;
      }
    });
    rsEventRepository.saveAll(rsEventsVote);
  }
}
