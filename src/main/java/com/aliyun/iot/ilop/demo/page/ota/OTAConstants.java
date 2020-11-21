package com.aliyun.iot.ilop.demo.page.ota;

/**
 * Created by david on 2018/4/13.
 *
 * @author david
 * @date 2018/04/13
 */
public class OTAConstants {
    public static final String OTA_KEY_DEVICESIMPLE_INFO = "OTA_KEY_DEVICESIMPLE_INFO";

    public static final int OTA_MESSAGE_NETWORK_ERROR = 0x00001;
    public static final int OTA_MESSAGE_RESQUEST_ERROR = 0x00002;

    /* APIClient */
    public static final String APICLIENT_IOTAUTH = "iotAuth";
    public static final String APICLIENT_VERSION = "1.0.2";
    public static final String APICLIENT_PATH_QUERYOTADEVICELIST = "/thing/ota/listByUser";
    //public static final String APICLIENT_PATH_QUERYOTADEVICEDETAIL = "/thing/ota/info/queryByUser";
    public static final String APICLIENT_PATH_DOUPGRADE = "/thing/ota/batchUpgradeByUser";
    public static final String APICLIENT_PATH_QUERYSTATUSINFO = "/thing/ota/info/progress/getByUser";
    public static final String APICLIENT_PATH_QUERYPRODUCTINFO = "/thing/detailInfo/queryProductInfo";
    //MAC
    public static final String APICLIENT_PATH_MAC = "/thing/extended/property/get";
    /* OTA */
    public static final int MINE_MESSAGE_RESPONSE_OTA_LIST_SUCCESS = 0x11001;
    public static final int MINE_MESSAGE_RESPONSE_OTA_LIST_FAILED = 0x21001;

    public static final int MINE_MESSAGE_RESPONSE_OTA_UPGRADE_SUCCESS = 0x11002;
    public static final int MINE_MESSAGE_RESPONSE_OTA_UPGRADE_FAILED = 0x21002;
    public static final int MINE_MESSAGE_RESPONSE_OTA_UPGRADE_STATUS = 0x31002;


    public static final int MINE_MESSAGE_RESPONSE_OTA_DEVICE_INFO_SUCCESS = 0x11003;
    public static final int MINE_MESSAGE_RESPONSE_OTA_DEVICE_INFO_FAILED = 0x21003;

    public static final int MINE_MESSAGE_RESPONSE_OTA_DEVICE_STATUS_SUCCESS = 0x11004;


    public static final int MINE_MESSAGE_RESPONSE_OTA_DEVICE_PRODUCT_INFO_SUCCESS = 0x11005;
    public static final int MINE_MESSAGE_RESPONSE_OTA_DEVICE_PRODUCT_INFO_FAILED = 0x21005;

    public static final int OTA_STATUS_PRE_LOAD = 0;
    public static final int OTA_STATUS_LOADING = 1;
    public static final int OTA_STATUS_ERROR = 2;
    public static final int OTA_STATUS_FAILURE = 3;
    public static final int OTA_STATUS_DONE = 4;
    public static final int OTA_STATUS_BUL_SUCCESS = 5;
    public static final int OTA_STATUS_BUL_FAILUER = 6;
    public static final int OTA_STATUS_BUL_LOADING = 7;

    public static final String MINE_URL_OTA = "https://com.aliyun.iot.ilop/page/ota";
}
