package com.hust.ewsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hust.ewsystem.DTO.GetWarningsCountDTO;
import com.hust.ewsystem.DTO.QueryWarnDTO;
import com.hust.ewsystem.DTO.QueryWarnInfoDTO;
import com.hust.ewsystem.DTO.WarnHandleDTO;
import com.hust.ewsystem.DTO.WarnStatusDTO;
import com.hust.ewsystem.DTO.WarningsDTO;
import com.hust.ewsystem.VO.ModulePointVO;
import com.hust.ewsystem.VO.WarningsVO;
import com.hust.ewsystem.entity.Warnings;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface WarningMapper extends BaseMapper<Warnings> {

    ModulePointVO getModuleIdByWarningId(@Param("warningId")Integer warningId);

    String getRealDescByStandId(@Param("standPointId") Integer standPointId, @Param("turbineId") Integer turbineId);

    List<WarningsVO> getWarningsByModelId(@Param("records") List<Warnings> records);

    Integer getTurbineIdByWarningId(@Param("warningId") Integer warningId);

    List<Integer> getTurbineIdCountByWarningIdList(@Param("records") List<Integer> warningId);

    /**
     * 分页查询预警信息
     * @param param queryWarnDTO
     * @param page page
     * @return IPage<Warnings>
     */
    IPage<WarningsDTO> selectWarningsPage(@Param("param") QueryWarnDTO param, @Param("page") Page<Warnings> page);

    List<WarningsDTO> selectWarningsNoPage(@Param("param")QueryWarnInfoDTO queryWarnInfoDTO);

    List<WarningsDTO> selectWarningsDesc(@Param("param")QueryWarnInfoDTO queryWarnInfoDTO);

    IPage<WarningsDTO> selectWarningsPage(@Param("param")QueryWarnInfoDTO queryWarnInfoDTO, @Param("page") Page<Warnings> page);

    int getWarningsCount(@Param("param") GetWarningsCountDTO getWarningsCountDTO);

    WarnStatusDTO getCount(@Param("modelId") Integer modelId, @Param("startTime")LocalDateTime startTime,@Param("endTime")LocalDateTime endTime);

    Boolean warnHandle(@Param("param") WarnHandleDTO warnHandleDTO);
}
