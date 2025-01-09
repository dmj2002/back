package com.hust.ewsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hust.ewsystem.DTO.GetWarningsCountDTO;
import com.hust.ewsystem.DTO.ModelsDTO;
import com.hust.ewsystem.DTO.QueryWaitDoneInfoDTO;
import com.hust.ewsystem.DTO.TurbineWaitDoneInfo;
import com.hust.ewsystem.DTO.WarnStatusDTO;
import com.hust.ewsystem.DTO.WindFarmDTO;
import com.hust.ewsystem.common.constant.CommonConstant;
import com.hust.ewsystem.entity.Reports;
import com.hust.ewsystem.entity.WindFarm;
import com.hust.ewsystem.mapper.ReportsMapper;
import com.hust.ewsystem.mapper.WarningMapper;
import com.hust.ewsystem.mapper.WindFarmMapper;
import com.hust.ewsystem.service.WindFarmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @BelongsProject: back
 * @BelongsPackage: com.hust.ewsystem.service.impl
 * @Author: xdy
 * @CreateTime: 2025-01-03  10:23
 * @Description:
 * @Version: 1.0
 */
@Service
@Transactional
public class WindFarmServiceImpl extends ServiceImpl<WindFarmMapper, WindFarm> implements WindFarmService {

    @Autowired
    private WindFarmMapper windFarmMapper;

    @Autowired
    private ReportsMapper reportsMapper;

    @Autowired
    private WarningMapper warningMapper;

    @Override
    public List<WindFarmDTO> getWindFarmsByCompanyId(QueryWaitDoneInfoDTO queryWaitDoneInfoDTO) {
        List<WindFarmDTO> windFarmDTOList = windFarmMapper.getWindFarmsByCompanyId(queryWaitDoneInfoDTO);
        LambdaQueryWrapper<Reports> queryWrapper;
        for (WindFarmDTO windFarmDTO : windFarmDTOList) {
            List<TurbineWaitDoneInfo> turbineWaitDoneInfo = windFarmDTO.getTurbineWaitDoneInfo();
            for (TurbineWaitDoneInfo waitDoneInfo : turbineWaitDoneInfo) {
                int warningLevel1waitDoneSum = 0;
                int warningLevel2waitDoneSum = 0;
                int warningLevel1waitHangUpSum = 0;
                int warningLevel2waitHangUpSum = 0;
                int warningLevel1waitCloseWaitSum = 0;
                int warningLevel2waitCloseWaitSum = 0;
                for (ModelsDTO modelsDTO : waitDoneInfo.getModelList()) {
                    GetWarningsCountDTO getWarningsCountDTO = initGetWarningsCountDTO(queryWaitDoneInfoDTO, waitDoneInfo, modelsDTO);
                    // typeInfo传1查询一级预警  2查询二级预警
                    int warningsCount = warningMapper.getWarningsCount(getWarningsCountDTO);
                    if (CommonConstant.NUM_COMMON_1.equals(queryWaitDoneInfoDTO.getInfoType())){
                        modelsDTO.setWarningLevel1Sum(warningsCount);
                    } else if (CommonConstant.NUM_COMMON_2.equals(queryWaitDoneInfoDTO.getInfoType())) {
                        modelsDTO.setWarningLevel2Sum(warningsCount);
                    } else if (CommonConstant.NUM_COMMON_0.equals(queryWaitDoneInfoDTO.getInfoType())) {
                        getWarningsCountDTO.setWarningLevel(CommonConstant.NUM_COMMON_1);
                        warningsCount = warningMapper.getWarningsCount(getWarningsCountDTO);
                        modelsDTO.setWarningLevel1Sum(warningsCount);
                        getWarningsCountDTO.setWarningLevel(CommonConstant.NUM_COMMON_2);
                        warningsCount = warningMapper.getWarningsCount(getWarningsCountDTO);
                        modelsDTO.setWarningLevel2Sum(warningsCount);
                    }
                    WarnStatusDTO count = warningMapper.getCount(modelsDTO.getModelId(),queryWaitDoneInfoDTO.getStartDate(),queryWaitDoneInfoDTO.getEndDate());
                    if (Objects.nonNull(count)){
                        warningLevel1waitDoneSum += count.getWarningLevel1waitDone();
                        warningLevel2waitDoneSum += count.getWarningLevel2waitDone();
                        warningLevel1waitHangUpSum += count.getWarningLevel1waitHangUp();
                        warningLevel2waitHangUpSum += count.getWarningLevel2waitHangUp();
                        warningLevel1waitCloseWaitSum += count.getWarningLevel2waitCloseWait();
                        warningLevel2waitCloseWaitSum += count.getWarningLevel2waitCloseWait();
                    }
                }
                waitDoneInfo.setWarningLevel1waitDoneSum(warningLevel1waitDoneSum);
                waitDoneInfo.setWarningLevel2waitDoneSum(warningLevel2waitDoneSum);
                waitDoneInfo.setWarningLevel1waitHangUpSum(warningLevel1waitHangUpSum);
                waitDoneInfo.setWarningLevel2waitHangUpSum(warningLevel2waitHangUpSum);
                waitDoneInfo.setWarningLevel1waitCloseWaitSum(warningLevel1waitCloseWaitSum);
                waitDoneInfo.setWarningLevel2waitCloseWaitSum(warningLevel2waitCloseWaitSum);

                if (CommonConstant.NUM_COMMON_3.equals(queryWaitDoneInfoDTO.getInfoType()) || CommonConstant.NUM_COMMON_0.equals(queryWaitDoneInfoDTO.getInfoType())){
                    queryWrapper = new LambdaQueryWrapper<>();
                    queryWrapper.eq(Reports::getTurbineId,waitDoneInfo.getTurbineId()).eq(Reports::getStatus, CommonConstant.NUM_COMMON_0)
                            .ge(Reports::getInitialTime,queryWaitDoneInfoDTO.getStartDate())
                            .le(Reports::getInitialTime,queryWaitDoneInfoDTO.getEndDate());
                    Long count = reportsMapper.selectCount(queryWrapper);
                    Integer rePortSum = Optional.ofNullable(count).map(Long::intValue).orElse(0);
                    waitDoneInfo.setReportSum(rePortSum);
                }
            }
        }
        return windFarmDTOList;
    }

    public GetWarningsCountDTO initGetWarningsCountDTO(QueryWaitDoneInfoDTO queryWaitDoneInfoDTO,TurbineWaitDoneInfo waitDoneInfo,ModelsDTO modelsDTO){
        GetWarningsCountDTO getWarningsCountDTO = new GetWarningsCountDTO();
        getWarningsCountDTO.setTurbineId(waitDoneInfo.getTurbineId());
        getWarningsCountDTO.setModelId(modelsDTO.getModelId());
        getWarningsCountDTO.setWarningLevel(queryWaitDoneInfoDTO.getInfoType());
        getWarningsCountDTO.setStartTime(queryWaitDoneInfoDTO.getStartDate());
        getWarningsCountDTO.setEndTime(queryWaitDoneInfoDTO.getEndDate());
        return getWarningsCountDTO;
    }
}
