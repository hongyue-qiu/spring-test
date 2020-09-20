package com.thoughtworks.rslist.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TradeDto {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY) private int id;
    private int amount;
    private int rank;
    @ManyToOne
    private TradeDto trade;
}
