package com.hust.ewsystem.DTO;

import com.hust.ewsystem.entity.CommonData;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class StandPointDTO {

    private Integer pointId;

    private String pointDescription;

    private List<CommonData> pointValue;

}
