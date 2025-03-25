package com.hust.ewsystem.VO;

import com.hust.ewsystem.DTO.StandPointDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class PicturesVO {

    private Integer pictureId;

    private String warningDescription;

    private Double threshold;

    private Integer picType;

    private List<StandPointDTO> points;
}
