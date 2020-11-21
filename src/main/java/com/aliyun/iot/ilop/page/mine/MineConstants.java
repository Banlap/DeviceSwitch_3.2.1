/*
 *   Copyright (c) 2020 Sinyuk
 *   All rights reserved.

 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at

 *   http://www.apache.org/licenses/LICENSE-2.0

 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.aliyun.iot.ilop.page.mine;

/**
 * Created by david on 2018/4/10.
 *
 * @author david
 * @date 2018/04/10
 */
public class MineConstants {
    public static final int MINE_MESSAGE_NETWORK_ERROR = 0x00001;
    public static final int MINE_MESSAGE_RESPONSE_ERROR = 0x00002;

    /* 测试地址 */

    public static final String MINE_URL_APP_UPGRADE_TEST = "https://gaic.alicdn.com/tms/ilop-app-upgrade-config.json";

    /* 正式版地址 */

    public static final String MINE_URL_APP_UPGRADE_ONLINE = "https://gaic.alicdn.com/tms/ilop-app-upgrade-config-online.json";

    /* 我的主页 */

    public static final int MINE_MESSAGE_RESPONSE_MESSAGE_COUNT_SUCCESS = 0x10001;
    public static final int MINE_MESSAGE_RESPONSE_MESSAGE_COUNT_FAILED = 0x20001;

    public static final int MINE_MESSAGE_RESPONSE_CHECK_UPGRADE_SUCCESS = 0x10002;
    public static final int MINE_MESSAGE_RESPONSE_CHECK_UPGRADE_FAILED = 0x20002;

    public static final int MINE_MESSAGE_RESPONSE_PERSONINFO_SUCCESS = 0x10003;
    public static final int MINE_MESSAGE_RESPONSE_PERSONINFO_FAILED = 0x20003;
    //
    public static final int MINE_MESSAGE_RESPONSE_REGION_SUCCESS = 0x10004;

    public static final int MINE_MESSAGE_RESPONSE_FEEDBACK_COUNT_SUCCESS = 0x10005;
    public static final int MINE_MESSAGE_RESPONSE_FEEDBACK_COUNT_FAILED = 0x20005;

    public static final int MINE_MESSAGE_RESPONSE_AVATAR_SUCCESS = 0x10006;

    /* url跳转 */

    public static final String MINE_URL_MESSAGE = "https://com.aliyun.iot.ilop/page/message";
    public static final String MINE_URL_TRIPARTITE_PLATFROM = "https://com.aliyun.iot.ilop/page/me/tp";
    public static final String MINE_URL_TRIPARTITE_TMALL_GENIE = "https://com.aliyun.iot.ilop/page/me/tmallgenie";
    public static final String MINE_URL_TRIPARTITE_H5 = "https://com.aliyun.iot.ilop/page/me/h5";
    public static final String MINE_URL_SHARE = "https://com.aliyun.iot.ilop/page/share";
    public static final String MINE_URL_SETTINGS_HOME = "https://com.aliyun.iot.ilop/page/me/settings";
    public static final String MINE_URL_ABOUT = "https://com.aliyun.iot.ilop/page/me/about";
    public static final String MINE_URL_LANGUAGE = "https://com.aliyun.iot.ilop/page/me/settings/language";
    public static final String MINE_URL_ACCOUNT_MANAGER = "https://com.aliyun.iot.ilop/page/me/settings/account";
    public static final String MINE_URL_OTA_LIST = "https://com.aliyun.iot.ilop/page/ota/list";
    public static final String MINE_URL_OTA = "https://com.aliyun.iot.ilop/page/ota";
    public static final String MINE_URL_APP_HOME = "https://com.aliyun.iot.ilop/page/home";
    public static final String MINE_URL_COUNTRY_LIST = "https://com.aliyun.iot.ilop/page/countryList";
    public static final String MINE_URL_PROTOCOL = "https://com.aliyun.iot.ilop/page/me/about/protocol";
    public final static String MINE_URL_FEEDBACK = "link://router/feedback";//意见反馈

    /* intent key */

    public static final String MINE_INTENT_KEY_DOWNLOAD_STATUS = "MINE_INTENT_KEY_DOWNLOAD_STATUS";

    /* action */

    public static final String MINE_URL_ACTION_NAVIGATION = "com.aliyun.iot.aep.action.navigation";
    /* tp */
    public static final String TITLE = "title";
    public static final String CHANNEL = "channel";
    public static final String TM = "TmallGenie";
    public static final String AA = "Echo";
    public static final String GA = "GoogleHome";
    public static final String IFTTT = "IFTTT";

    public static final String APICLIENT_PATH_GETSUOOPRTEDEVICES = "/device/thirdsupport/get";
    public static final String APICLIENT_PATH_BIND_ACCOUNT = "/account/taobao/bind";
    public static final String APICLIENT_PATH_THIRDPARTY_GET = "/account/thirdparty/get";
    public static final String APICLIENT_PATH_UNBIND_ACCOUNT = "/account/thirdparty/unbind";

    public static final int MINE_MESSAGE_RESPONSE_GET_SUOOPRTED_DEVICES_SUCCESS = 0x12001;
    public static final int MINE_MESSAGE_RESPONSE_GET_SUOOPRTED_DEVICES_FAIL = 0x22001;

    public static final int MINE_MESSAGE_RESPONSE_BIND_TAOBAO_ACCOUNT_SUCCESS = 0x13001;
    public static final int MINE_MESSAGE_RESPONSE_BIND_TAOBAO_ACCOUNT_FAIL = 0x23001;

    public static final int MINE_MESSAGE_RESPONSE_GET_TAOBAO_ACCOUNT_SUCCESS = 0x14001;
    public static final int MINE_MESSAGE_RESPONSE_GET_TAOBAO_ACCOUNT_FAIL = 0x24001;

    public static final int MINE_MESSAGE_RESPONSE_UNBIND_TAOBAO_ACCOUNT_SUCCESS = 0x15001;
    public static final int MINE_MESSAGE_RESPONSE_UNBIND_TAOBAO_ACCOUNT_FAIL = 0x25001;


    public static final String APICLIENT_IOTAUTH = "iotAuth";
    public static final String APICLIENT_VERSION104 = "1.0.4";


    public static final String APICLIENT_VERSION101 = "1.0.1";
    public static final String APICLIENT_VERSION105 = "1.0.5";
    public static final String ACCOUNT_TYPE = "TAOBAO";
    public static final String AUTO_LANGUAGE = "AUTO_LANGUAGE";

    @Deprecated
    public static final int MINE_SMALL_COMPONENT_GET_DEVICE_LIST_SUCCESS = 0x30001;
    @Deprecated
    public static final int MINE_SMALL_COMPONENT_GET_DEVICE_LIST_FAIL = 0x30002;
    public static final int MINE_SMALL_COMPONENT_GET_DEVICE_PROPERTY_SUCCESS = 0x30003;
    public static final int MINE_SMALL_COMPONENT_GET_DEVICE_PROPERTY_FAIL = 0x30004;
    public static final int MINE_SMALL_COMPONENT_QUERY_COMPONENT_PRODUCT_SUCCESS = 0x30005;
    public static final int MINE_SMALL_COMPONENT_QUERY_COMPONENT_PRODUCT_FAIL = 0x30006;
    public static final int MINE_SMALL_COMPONENT_UPDATE_COMPONENT_PROPERTY_SUCCESS = 0x30007;
    public static final int MINE_SMALL_COMPONENT_UPDATE_COMPONENT_PROPERTY_FAIL = 0x30008;

    public static final int MINE_SMALL_COMPONENT_QUERY_COMPONENT_PRODUCT_SERVICE_SUCCESS = 0x30009;

}
