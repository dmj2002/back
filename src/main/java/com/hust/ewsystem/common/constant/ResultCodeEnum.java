package com.hust.ewsystem.common.constant;

/**
 * <p>PURPOSE:
 * <p>DESCRIPTION: 错误码
 * <p>CALLED BY:
 * <p>CREATE DATE: 2022/12/5 15:23 星期一
 * <p>UPDATE DATE:
 * <p>UPDATE USER:
 * <p>HISTORY:
 *
 * @author xdy
 * @version 1.0
 * @see
 * @since java 1.8
 */
public enum ResultCodeEnum {

    //系统级状态码
	SQL_ERROR(-1, "数据库执行失败"),
	PARAM_ERROR(-2, "参数校验失败"),

    ADAPTER_CALL_ERROR(1153, "适配接口调用方式异常"),
    GET_MAC_ERROR(1505, "获取MAC地址IP地址异常"),

    GET_REGISTER_ERROR(1506, "签到调用未获取到签到编号"),

    CALL_TFP_ERROR(1509,"调用两定接口异常"),

    CALL_EBC_ERROR(1510,"调用ebc接口异常"),

    CALL_COM_ERROR(1511,"调用COM组件失败"),

    CALL_DLL_INIT_ERROR(1512,"动态库初始化函数执行异常"),

    CALL_DLL_ERROR(1513,"调用动态库接口异常"),
    
    JACKSON_ERROR(1801, "jsckson解析失败");


    /**
     * 状态码
     */
    private int code;
    /**
     * 信息描述
     */
    private String desc;

    /**
     * 错误码枚举构造方法
     * @param code
     * @param desc
     */
    ResultCodeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 获取状态码
     * @return
     */
    public int getCode() {
        return code;
    }

    /**
     * 获取描述信息
     * @return
     */
    public String getDesc() {
        return desc;
    }
}
